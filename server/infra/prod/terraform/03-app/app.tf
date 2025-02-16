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

module "vault_init_crds" {
  source    = "../modules/k8s-apply-all"
  filename  = "03-vault-init.yml"
  namespace = var.namespace
}

module "postgresql" {
  source     = "../modules/k8s-apply-all"
  filename   = "04-postgresql.yml"
  namespace  = var.namespace
  depends_on = [module.vault_init_crds]
}

module "app" {
  source     = "../modules/k8s-apply-all"
  filename   = "05-app.yml"
  namespace  = var.namespace
  depends_on = [module.postgresql]
}

module "app_migrations" {
  source     = "../modules/k8s-apply-all"
  filename   = "06-app-migrations.yml"
  namespace  = var.namespace
  depends_on = [module.app]
}

# TODO: observability, db etc
