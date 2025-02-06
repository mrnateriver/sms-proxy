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

resource "kubernetes_namespace" "project_namespace" {
  metadata {
    name = var.namespace
  }
  lifecycle {
    ignore_changes = [metadata]
  }
}

module "cert_manager_issuer" {
  source     = "../modules/k8s-apply-all"
  dirname    = "cert-manager-issuer"
  namespace  = var.namespace
  depends_on = [kubernetes_namespace.project_namespace]
}
module "vault_tls" {
  source     = "../modules/k8s-apply-all"
  dirname    = "vault-tls"
  namespace  = var.namespace
  depends_on = [kubernetes_namespace.project_namespace, module.cert_manager_issuer]
}
module "vault" {
  source     = "../modules/k8s-apply-all"
  dirname    = "vault"
  namespace  = var.namespace
  depends_on = [kubernetes_namespace.project_namespace, module.vault_tls]
}
# module "vault-init" {
#     source     = "../modules/vault-init"
#     test       = "test"
#     depends_on = [module.vault]
# }

# TODO: app, observability, db etc
