#!/bin/sh
set -euo pipefail
trap 'echo "Ctrl+C pressed, terminating script..."; kill 0' SIGINT

CURRENT_CONTEXT=$(kubectl config current-context)

# Select appropriate K8S context
if [ -n "$1" ]; then
    echo "Switching K8S context to '$1'"
    if ! kubectl config use-context "$1"; then
        echo "Failed to switch K8S context to '$1'"
        exit 1
    fi
else
    echo "Specify K8S context as the first argument"
    kubectl config get-contexts
    exit 1
fi

# Init FluxCD
kubectl apply -f https://github.com/fluxcd/flux2/releases/latest/download/install.yaml

# App components in the order of dependencies
kubectl apply -k cert-manager

echo "Waiting for cert-manager to become ready..."
kubectl wait helmrelease/cert-manager -n sms-proxy-cert-manager --for=condition=Ready --timeout=300s

kubectl apply -k cert-manager-issuer
kubectl apply -k vault-tls
kubectl apply -k vault

echo "Waiting for vault to become ready..."
kubectl wait helmrelease/vault -n sms-proxy-vault --for=condition=Ready --timeout=300s
kubectl wait helmrelease/secrets-store-csi-driver -n sms-proxy-vault --for=condition=Ready --timeout=300s

# TODO: other namespaces

# Initialize Vault cluster

# Init leader
echo "Waiting for vault leader to start..."
kubectl wait pods/sms-proxy-vault-0 -n sms-proxy-vault --for=condition=Ready --timeout=300s
kubectl exec -n sms-proxy-vault sms-proxy-vault-0 -- vault operator init -key-shares=1 -key-threshold=1 -format=json > cluster-keys.json

# Unseal all pods
VAULT_UNSEAL_KEY=$(jq -r ".unseal_keys_b64[]" cluster-keys.json)
kubectl exec -n sms-proxy-vault sms-proxy-vault-0 -- vault operator unseal "$VAULT_UNSEAL_KEY"

echo "Sleeping for 5 seconds to allow followers to catch up..."
sleep 5

kubectl wait pods/sms-proxy-vault-1 -n sms-proxy-vault --for=condition=Ready --timeout=300s
kubectl exec -n sms-proxy-vault sms-proxy-vault-1 -- vault operator unseal "$VAULT_UNSEAL_KEY"

kubectl wait pods/sms-proxy-vault-2 -n sms-proxy-vault --for=condition=Ready --timeout=300s
kubectl exec -n sms-proxy-vault sms-proxy-vault-2 -- vault operator unseal "$VAULT_UNSEAL_KEY"

echo "Done!"

# Revert to the original context
kubectl config use-context "$CURRENT_CONTEXT"
