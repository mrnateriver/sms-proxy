terraform {
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "2.35.1"
    }
  }
}

provider "kubernetes" {
  config_context = var.context
}

variable "context" {
  type        = string
  description = "Name of the K8S context in your ~/.kube/config file that will be used for provisioning"
}

variable "dirname" {
  type        = string
  description = "Directory name of the K8S resources to be applied"
}

variable "namespace" {
  type        = string
  description = "K8S namespace where the resources will be created"
  default     = "sms-proxy"
}

locals {
  k8s_dir = "${path.module}/../../../k8s/${var.dirname}"
  manifests = {
    for file in fileset(local.k8s_dir, "*.yml")
    : file => yamldecode(file("${local.k8s_dir}/${file}"))
  }
}

resource "kubernetes_manifest" "k8s_manifests" {
  for_each = local.manifests
  manifest = merge(
    each.value,
    contains(keys(each.value.metadata), "namespace") ?
    { metadata = merge(each.value.metadata, { namespace = var.namespace }) } :
    {}
  )

  lifecycle {
    ignore_changes = [manifest.metadata]
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

output "k8s_manifests" {
  value = kubernetes_manifest.k8s_manifests
}
