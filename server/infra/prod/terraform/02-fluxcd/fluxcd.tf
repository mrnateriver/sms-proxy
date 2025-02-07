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
  source  = "../01-crds"
  context = var.context
}

module "cert_manager_issuer" {
  source     = "../modules/k8s-apply-all"
  filename   = "00-flux-cd.yml"
  namespace  = var.namespace
  depends_on = [module.setup]
}
