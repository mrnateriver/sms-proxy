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

variable "namespace" {
  type        = string
  description = "K8S namespace where the resources will be created"
  default     = "sms-proxy"
}

module "setup" {
  source    = "../local"
  context   = var.context
  namespace = var.namespace
}

resource "kubernetes_manifest" "flux_sources" {
  depends_on = [module.setup]

  for_each = {
    for value in [
      for yaml in split(
        "\n---\n",
        "\n${replace(file("${path.module}/../../k8s/flux-cd.yml"), "/(?m)^---[[:blank:]]*(#.*)?$/", "---")}\n"
      ) :
      yamldecode(yaml)
      if trimspace(replace(yaml, "/(?m)(^[[:blank:]]*(#.*)?$)+/", "")) != ""
    ] : "${value["kind"]}--${value["metadata"]["name"]}" => value
  }

  manifest = each.value

  lifecycle {
    ignore_changes = [metadata]
  }
}
