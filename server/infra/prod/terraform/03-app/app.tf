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

# This module has to be here because it depends on CRDs which are installed in 02-vault
module "vault_init_crds" {
  source    = "../modules/k8s-apply-all"
  filename  = "03-vault-init.yml"
  namespace = var.namespace
}

module "minio" {
  source     = "../modules/minio"
  namespace  = var.namespace
  depends_on = [module.vault_init_crds]
}

module "registry" {
  source     = "../modules/k8s-apply-all"
  filename   = "04-registry.yml"
  namespace  = var.namespace
  depends_on = [module.minio]
}

module "registry_init" {
  source     = "../modules/registry-init"
  namespace  = var.namespace
  depends_on = [module.registry]
}

module "postgresql" {
  source     = "../modules/k8s-apply-all"
  filename   = "05-postgresql.yml"
  namespace  = var.namespace
  depends_on = [module.vault_init_crds] # Let the registry initialize while we provision Postgres
}

module "app_migrations" {
  source     = "../modules/k8s-apply-all"
  filename   = "06-app-migrations.yml"
  namespace  = var.namespace
  depends_on = [module.postgresql, module.registry_init]
}

module "app" {
  source     = "../modules/k8s-apply-all"
  filename   = "07-app.yml"
  namespace  = var.namespace
  depends_on = [module.app_migrations]
}

module "traefik" {
  source     = "../modules/k8s-apply-all"
  filename   = "08-traefik.yml"
  namespace  = var.namespace
  depends_on = [module.app]
}

module "wave" {
  source     = "../modules/k8s-apply-all"
  filename   = "09-wave.yml"
  namespace  = var.namespace
  depends_on = [module.traefik]
}

# TODO: observability
