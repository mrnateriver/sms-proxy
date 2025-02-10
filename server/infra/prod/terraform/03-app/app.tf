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

# TODO: app-centric Vault policies using K8S CRDs
module "postgresql" {
  source     = "../modules/k8s-apply-all"
  filename   = "03-postgresql.yml"
  namespace  = var.namespace
}

# TODO: app, observability, db etc
