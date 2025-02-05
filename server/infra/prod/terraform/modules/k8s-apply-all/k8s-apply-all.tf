variable "dirname" {
  type        = string
  description = "Directory name of the K8S resources to be applied"
}

variable "namespace" {
  type        = string
  description = "K8S namespace where the resources will be created"
  default     = "sms-proxy"
}

resource "kubernetes_manifest" "k8s_manifests" {
  for_each = fileset("${path.module}/../../../k8s/${var.dirname}", "*.yml")
  manifest = merge(
    yamldecode(file("${path.module}/../../../k8s/${var.dirname}/${each.value}")),
    {
      metadata = {
        namespace = var.namespace
      }
    }
  )

  lifecycle {
    ignore_changes = [metadata]
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
