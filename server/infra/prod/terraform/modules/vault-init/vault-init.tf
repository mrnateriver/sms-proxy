terraform {
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "2.35.1"
    }
  }
}

variable "namespace" {
  type        = string
  description = "K8S namespace of the Vault"
  default     = "sms-proxy"
}

variable "vault_release_name" {
  type        = string
  description = "Name of the Vault Helm release"
  default     = "sms-proxy-vault"
}

variable "shamir_key_shares" {
  type        = number
  description = "Number of key shares to generate when initialising Vault"
  default     = 1
}

variable "vault_replicas" {
  type        = number
  description = "Number of Vault pod replicas"
  default     = 2
}

variable "vault_init_role" {
  type        = string
  description = "Name of the Vault auth role for provisioning Kubernetes resources using `vault-operator`"
  default     = "vault-operator"
}

variable "vault_init_service_account" {
  type        = string
  description = "Name of the Kubernetes service account that is granted privileges for provisioning initial Vault resources"
  default     = "vault-operator-sa"
}

data "kubernetes_pod" "vault_pod_leader" {
  metadata {
    name      = "${var.vault_release_name}-0"
    namespace = var.namespace
  }
}

data "kubernetes_pod" "vault_pods_standby" {
  count = var.vault_replicas - 1
  metadata {
    name      = "${var.vault_release_name}-${count.index + 1}"
    namespace = var.namespace
  }
}

locals {
  cluster_keys                     = jsondecode(data.local_file.cluster_keys_json.content)
  vault_pod_leader_metadata        = data.kubernetes_pod.vault_pod_leader.metadata[0]
  vault_pod_leader_metadata_labels = local.vault_pod_leader_metadata.labels == null ? {} : local.vault_pod_leader_metadata.labels
  vault_pod_leader_name            = local.vault_pod_leader_metadata.name
  vault_pods_standby_metadata = {
    for pod in data.kubernetes_pod.vault_pods_standby : pod.metadata[0].name => pod.metadata[0]
  }
}

resource "null_resource" "init_vault" {
  triggers = {
    namespace    = var.namespace
    release_name = var.vault_release_name
  }

  provisioner "local-exec" {
    interpreter = ["/bin/sh", "-c"]
    command     = <<EOF
        kubectl wait helmreleases.helm.toolkit.fluxcd.io/vault -n ${var.namespace} --for=condition=Ready --timeout=300s
        kubectl wait pods/${local.vault_pod_leader_name} -n ${var.namespace} --for=condition=Ready --timeout=300s

        initialized=$(kubectl get pod "${local.vault_pod_leader_name}" -n ${var.namespace} -o jsonpath="{.metadata.labels.vault-initialized}" 2>/dev/null)
        if [[ "$initialized" == "false" ]]; then
            kubectl exec ${local.vault_pod_leader_name} -n ${var.namespace} -- vault operator init -non-interactive -key-shares=${var.shamir_key_shares} -key-threshold=${var.shamir_key_shares} -format=json > cluster-keys.json
        fi
    EOF
  }
}

data "local_file" "cluster_keys_json" {
  depends_on = [null_resource.init_vault]
  filename   = "cluster-keys.json"
}

resource "null_resource" "unseal_vault_leader" {
  depends_on = [null_resource.init_vault]

  triggers = {
    force_redeploy = uuid()
  }

  provisioner "local-exec" {
    interpreter = ["/bin/sh", "-c"]
    command     = <<EOF
        sealed=$(kubectl get pod "${local.vault_pod_leader_name}" -n ${var.namespace} -o jsonpath="{.metadata.labels.vault-sealed}" 2>/dev/null)
        if [[ "$sealed" == "true" ]]; then
            kubectl exec -it "pods/${local.vault_pod_leader_name}" -n ${var.namespace} -- vault operator unseal -non-interactive $(jq -r ".unseal_keys_b64[]" cluster-keys.json)
            sleep 5 # Let standby nodes to catch up
        fi
    EOF
  }
}

resource "null_resource" "unseal_vault" {
  depends_on = [null_resource.unseal_vault_leader]

  triggers = {
    force_redeploy = uuid()
  }

  for_each = local.vault_pods_standby_metadata

  provisioner "local-exec" {
    interpreter = ["/bin/sh", "-c"]
    command     = <<EOF
        kubectl wait "pods/${each.key}" -n ${var.namespace} --for=condition=Ready --timeout=300s

        sealed=$(kubectl get pod "${each.key}" -n ${var.namespace} -o jsonpath="{.metadata.labels.vault-sealed}" 2>/dev/null)
        if [[ "$sealed" == "true" ]]; then
            kubectl exec -it "pods/${each.key}" -n ${var.namespace} -- vault operator unseal -non-interactive $(jq -r ".unseal_keys_b64[]" cluster-keys.json)
        fi
    EOF
  }
}

resource "null_resource" "init_vault_engines" {
  depends_on = [null_resource.unseal_vault]

  triggers = {
    namespace    = var.namespace
    release_name = var.vault_release_name
  }

  provisioner "local-exec" {
    interpreter = ["/bin/sh", "-c"]
    command     = <<EOF
        kubectl exec "${local.vault_pod_leader_name}" -n ${var.namespace} -- vault login -no-print -non-interactive $(jq -r ".root_token" cluster-keys.json)
        kubectl exec "${local.vault_pod_leader_name}" -n ${var.namespace} -- vault write sys/mounts/kv2 type=kv options=version=2
        kubectl exec "${local.vault_pod_leader_name}" -n ${var.namespace} -- vault write sys/mounts/kv type=kv
        kubectl exec "${local.vault_pod_leader_name}" -n ${var.namespace} -- vault secrets enable -path=transit transit
        kubectl exec "${local.vault_pod_leader_name}" -n ${var.namespace} -- sh -c "rm ~/.vault-token"
    EOF
  }
}

resource "null_resource" "init_vault_auth" {
  depends_on = [null_resource.init_vault_engines]

  triggers = {
    namespace    = var.namespace
    release_name = var.vault_release_name
  }

  provisioner "local-exec" {
    interpreter = ["/bin/sh", "-c"]
    command     = <<EOF
        kubectl cp ${path.module}/vault-init-policy.hcl "${local.vault_pod_leader_name}":/tmp/vault-init-policy.hcl -n ${var.namespace}
        kubectl exec "${local.vault_pod_leader_name}" -n ${var.namespace} -- vault login -no-print -non-interactive $(jq -r ".root_token" cluster-keys.json)
        kubectl exec "${local.vault_pod_leader_name}" -n ${var.namespace} -- vault policy write vault-init-policy /tmp/vault-init-policy.hcl
        kubectl exec "${local.vault_pod_leader_name}" -n ${var.namespace} -- vault auth enable kubernetes
        kubectl exec "${local.vault_pod_leader_name}" -n ${var.namespace} -- sh -c "vault write auth/kubernetes/config kubernetes_host=\"https://\$KUBERNETES_PORT_443_TCP_ADDR:443\""
        kubectl exec "${local.vault_pod_leader_name}" -n ${var.namespace} -- vault write auth/kubernetes/role/${var.vault_init_role} \
                                                                                    bound_service_account_names=${var.vault_init_service_account} \
                                                                                    bound_service_account_namespaces=${var.namespace} \
                                                                                    policies=vault-init-policy \
                                                                                    ttl=1h
        kubectl exec "${local.vault_pod_leader_name}" -n ${var.namespace} -- sh -c "rm ~/.vault-token"
    EOF
  }
}

resource "null_resource" "revoke_root_token" {
  depends_on = [null_resource.init_vault_auth]

  triggers = {
    namespace    = var.namespace
    release_name = var.vault_release_name
  }

  provisioner "local-exec" {
    interpreter = ["/bin/sh", "-c"]
    command     = <<EOF
        kubectl exec "${local.vault_pod_leader_name}" -n ${var.namespace} -- vault login -no-print -non-interactive $(jq -r ".root_token" cluster-keys.json)
        kubectl exec "${local.vault_pod_leader_name}" -n ${var.namespace} -- vault token revoke $(jq -r ".root_token" cluster-keys.json)
        kubectl exec "${local.vault_pod_leader_name}" -n ${var.namespace} -- sh -c "rm ~/.vault-token"
    EOF
  }
}
