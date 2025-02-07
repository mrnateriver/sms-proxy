terraform {
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "2.35.1"
    }
  }
}

variable "filename" {
  type        = string
  description = "File name of the K8S resource to be applied. Can be a collection of resources in a single file"
}

variable "namespace" {
  type        = string
  description = "K8S namespace where the resources will be created"
  default     = "sms-proxy"
}

locals {
  k8s_base = "${path.module}/../../../k8s"
  k8s_file = "${local.k8s_base}/${var.filename}"
  resources = [
    for resource in [
      for yaml in split("\n---\n", "\n${replace(file(local.k8s_file), "/(?m)^---[[:blank:]]*(#.*)?$/", "---")}\n") :
      yamldecode(yaml)
      if trimspace(replace(yaml, "/(?m)(^[[:blank:]]*(#.*)?$)+/", "")) != ""
    ] : resource
  ]
  manifests = {
    for resource in local.resources : "${resource["kind"]}--${resource["metadata"]["name"]}" => resource
  }
}

resource "kubernetes_manifest" "kubernetes_manifests" {
  for_each = local.manifests
  manifest = merge(
    each.value,
    contains(keys(each.value.metadata), "namespace") ?
    { metadata = merge(each.value.metadata, { namespace = var.namespace }) } :
    {}
  )

  lifecycle {
    ignore_changes = [manifest.metadata.labels]
  }

  wait {
    condition {
      type   = "Ready"
      status = "True"
    }
  }

  timeouts {
    create = "5m"
    update = "5m"
    delete = "5m"
  }
}

output "kubernetes_manifests" {
  value = kubernetes_manifest.kubernetes_manifests
}
