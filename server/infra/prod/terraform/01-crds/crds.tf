terraform {
  required_providers {
    helm = {
      source  = "hashicorp/helm"
      version = "2.17.0"
    }
  }
}

provider "helm" {
  kubernetes {
    config_context = var.context
  }
}

variable "context" {
  type        = string
  description = "Name of the K8S context in your ~/.kube/config file that will be used for provisioning"
}

resource "helm_release" "fluxcd" {
  name             = "flux"
  repository       = "https://fluxcd-community.github.io/helm-charts"
  chart            = "flux2"
  namespace        = "flux-system"
  version          = "2.14.1"
  create_namespace = true
}

resource "helm_release" "cert_manager" {
  name             = "cert-manager"
  repository       = "https://charts.jetstack.io"
  chart            = "cert-manager"
  namespace        = "cert-manager"
  version          = "v1.17.0"
  create_namespace = true

  set {
    name  = "crds.enabled"
    value = "true"
  }
}

resource "helm_release" "kube_prometheus_stack" {
  depends_on       = [helm_release.cert_manager]
  name             = "kube-prometheus-stack"
  repository       = "https://prometheus-community.github.io/helm-charts"
  chart            = "kube-prometheus-stack"
  namespace        = "kube-prometheus-stack"
  version          = "69.2.0"
  create_namespace = true

  set {
    name  = "prometheusOperator.admissionWebhooks.certManager.enabled"
    value = "true"
  }
}

resource "helm_release" "vault_config_operator" {
  depends_on       = [helm_release.cert_manager, helm_release.kube_prometheus_stack]
  name             = "vault-config-operator"
  repository       = "https://redhat-cop.github.io/vault-config-operator"
  chart            = "vault-config-operator"
  namespace        = "vault-config-operator"
  version          = "v0.8.29"
  create_namespace = true

  set {
    name  = "enableCertManager"
    value = "true"
  }
}
