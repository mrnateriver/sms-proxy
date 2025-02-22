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

variable "namespace" {
  type        = string
  description = "K8S namespace where the resources will be created"
  default     = "sms-proxy"
}

resource "helm_release" "fluxcd" {
  name             = "flux"
  repository       = "https://fluxcd-community.github.io/helm-charts"
  chart            = "flux2"
  namespace        = var.namespace
  version          = "2.14.1"
  create_namespace = true
}

resource "helm_release" "cert_manager" {
  name             = "cert-manager"
  repository       = "https://charts.jetstack.io"
  chart            = "cert-manager"
  namespace        = var.namespace
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
  namespace        = var.namespace
  version          = "69.2.0"
  create_namespace = true

  set {
    name  = "prometheusOperator.admissionWebhooks.certManager.enabled"
    value = "true"
  }

  # The following options are necessary to make prometheus-operator scrape all ServiceMonitors within the namespace, as opposed to only scanning the Helm-managed resources
  set {
    name  = "prometheus.prometheusSpec.podMonitorSelectorNilUsesHelmValues"
    value = "false"
  }
  set {
    name  = "prometheus.prometheusSpec.serviceMonitorSelectorNilUsesHelmValues"
    value = "false"
  }
}
