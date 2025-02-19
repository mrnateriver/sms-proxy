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

module "cert_manager_issuers" {
  source     = "../modules/k8s-apply-all"
  filename   = "01-cert-manager-issuers.yml"
  namespace  = var.namespace
}

module "vault" {
  source     = "../modules/k8s-apply-all"
  filename   = "02-vault.yml"
  namespace  = var.namespace
  depends_on = [module.cert_manager_issuers]
}

module "vault_init" {
  source     = "../modules/vault-init"
  namespace  = var.namespace
  depends_on = [module.vault]
}
