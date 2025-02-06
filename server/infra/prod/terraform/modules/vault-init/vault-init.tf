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
  description = "K8S namespace of the Vault"
  default     = "sms-proxy"
}

variable "shamir_key_shares" {
  type        = number
  description = "Number of key shares to generate when initialising Vault"
  default     = 1
}

variable "vault_replicas" {
  type        = number
  description = "Number of Vault pod replicas"
  default     = 3
}

data "kubernetes_pod" "vault_pod_leader" {
  metadata {
    name      = "sms-proxy-vault-0"
    namespace = var.namespace
  }
}

data "kubernetes_pod" "vault_pods_standby" {
  count = var.vault_replicas - 1
  metadata {
    name      = "sms-proxy-vault-${count.index + 1}"
    namespace = var.namespace
  }
}

data "local_file" "cluster_keys_json" {
  depends_on = [null_resource.init_vault]
  filename   = "${path.module}/cluster-keys.json"
}

locals {
  cluster_keys              = jsondecode(data.local_file.cluster_keys_json.content)
  vault_pod_leader_metadata = data.kubernetes_pod.vault_pod_leader.metadata[0]
  vault_pod_leader_name     = local.vault_pod_leader_metadata.name
  vault_pods_standby_metadata = {
    for pod in data.kubernetes_pod.vault_pods_standby : pod.metadata[0].name => pod.metadata[0]
  }
}

resource "null_resource" "init_vault" {
  triggers = {
    namespace = var.namespace
    pod_name  = lookup(local.vault_pod_leader_metadata.labels, "statefulset.kubernetes.io/pod-name", "")
    status    = lookup(local.vault_pod_leader_metadata.labels, "vault-initialized", "false")
  }

  provisioner "local-exec" {
    interpreter = ["/bin/bash", "-c"]
    command     = <<EOF
        kubectl wait pods/${local.vault_pod_leader_name} -n ${var.namespace} --for=condition=Ready --timeout=300s

        initialized=$(kubectl get pod "${local.vault_pod_leader_name}" -n ${var.namespace} -o jsonpath="{.metadata.labels.vault-initialized}" 2>/dev/null)
        if [[ "$initialized" == "false" ]]; then
            kubectl exec ${local.vault_pod_leader_name} -n ${var.namespace} -- vault operator init -key-shares=${var.shamir_key_shares} -key-threshold=${var.shamir_key_shares} -format=json > ${path.module}/cluster-keys.json
        fi

        sealed=$(kubectl get pod "${local.vault_pod_leader_name}" -n ${var.namespace} -o jsonpath="{.metadata.labels.vault-sealed}" 2>/dev/null)
        if [[ "$sealed" == "true" ]]; then
            UNSEAL_KEY=$(jq -r ".unseal_keys_b64[]" cluster-keys.json)
            kubectl exec -it "pods/${local.vault_pod_leader_name}" -n ${var.namespace} -- vault operator unseal $UNSEAL_KEY
            sleep 5 # Let standby nodes to catch up
        fi
    EOF
  }
}

resource "null_resource" "unseal_vault" {
  depends_on = [null_resource.init_vault]

  for_each = local.vault_pods_standby_metadata

  triggers = {
    namespace = var.namespace
    pod_name  = lookup(each.value.labels, "statefulset.kubernetes.io/pod-name", "")
    status    = lookup(each.value.labels, "vault-sealed", "false")
  }

  provisioner "local-exec" {
    interpreter = ["/bin/bash", "-c"]
    command     = <<EOF
        kubectl wait "pods/${each.key}" -n ${var.namespace} --for=condition=Ready --timeout=300s

        sealed=$(kubectl get pod "${each.key}" -n ${var.namespace} -o jsonpath="{.metadata.labels.vault-sealed}" 2>/dev/null)
        if [[ "$sealed" == "true" ]]; then
            kubectl exec -it "pods/${each.key}" -n ${var.namespace} -- vault operator unseal ${local.cluster_keys.unseal_keys_b64[0]}
        fi
    EOF
  }
}

output "root_token" {
  value = local.cluster_keys.root_token
}

output "unseal_keys_b64" {
  value = local.cluster_keys.unseal_keys_b64
}
