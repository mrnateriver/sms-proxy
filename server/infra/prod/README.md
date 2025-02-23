# Production Infrastructure

## DISCLAIMER

This implementation contains a few simplifications which must not be used in a real production environment:
1. TODO: Vault is not deployed in a separate cluster
1. TODO: Vault pod anti affinity is disabled, even though they should be deployed on separate nodes
1. TODO: if Vault is deployed to the same single node (which should not be the case for production), HA should be disabled
1. TODO: 1 Shamir key share for Vault
1. TODO: Vault unseal keys are passed as CLI arguments, even though they should be provided via stdin/ui
1. TODO: OCI registry should not be part of the cluster for the sake of cluster penetration security
1. TODO: K8S deployment relies on Secret resources, which are stored in etcd in plaintext by default. In a real prod env, at the very least encryption at rest should be enabled for etcd (using KMS), or even better, but much harder - refrain from using Secrets at all and rely on Vault (possibly external) and volume mounts for all sensitive data
1. TODO: with the above point said, CSI driver (has been partially implemented - look up in git history) or Vault Agent should be used to mount secrets, but vault-secret-operator used in this project for simplicity. And actually, with the current setup sensitive values are still used as Secrets anyway (PKI secrets, Postgres initial password (even though it's immediately rotated) etc), so we might as well use Secrets for everything.

## Structure

* [k8s](./k8s) - Kubernetes manifests for all resources.
* [terraform](./terraform) - Terraform configuration for the infrastructure and initial configuration of some of the
  resources (for example, HashiCorp Vault).
    - [terraform/00-infra](./terraform/00-infra) - K8S cluster provisioning in a cloud provider.
    - [terraform/01-crds](./terraform/01-crds) - Basix CRDs which are required for the rest of the resources.
    - [terraform/02-vault](./terraform/02-vault) - HashiCorp Vault configuration, including CRDs for Vault K8S operator.
    - [terraform/03-app](./terraform/03-app) - K8S resources of the application, including direct dependencies like PostgreSQL.
    - [terraform/03-fluxcd](./terraform/03-fluxcd) - K8S resources which set up FluxCD in that cluster, and then add this Git repository as the source for provisioning.
    - [terraform/modules](./terraform/modules) - reusable Terraform modules.
      * [terraform/minio](./terraform/modules/minio) - Minio deployment, which is used as an S3-compatible storage for the application. Only used in local deployment to not depend on external services.
      * [terraform/modules/k8s-apply-all](./terraform/modules/k8s-apply-all) - applies specified K8S manifest to the cluster, splitting it into multiple resources if needed.
      * [terraform/modules/vault-init](./terraform/modules/vault-init) - initializes HashiCorp Vault, unseals it, and then mounts required secret engines and authentication backend.

## Deployment

Run `./init-cluster-local.sh <name of the context>` to deploy the application and all of its dependencies to a cluster with configured `kubectl` context.

TODO: when deploying to local cluster, S3 storage hostname and OCI repo hostname must be added to /etc/hosts because OCI registry redirects client to the underlying S3 storage, which it references by internal cluster hostname
TODO: when deploying K8S resources to local cluster, on Mac OS & Windows, Docker has to be in host networking mode, because OCI registry redirects client to the underlying S3 storage, which would not otherwise be accessible from Docker's VM.

Run `./init-cluster-remote.sh <name of the context>` to deploy the application and all of its dependencies to a remote cluster. That cluster must also be configured as a context in local `kubectl`. The difference with local cluster is that this script will only provision FluxCD `GitRepository` and `Kustomization` sources as opposed to directly provisioning K8S resources.
