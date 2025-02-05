# Production Infrastructure

* [k8s](./k8s) - Kubernetes manifests for all of the resources.
* [terraform](./terraform) - Terraform configuration for the infrastructure and initial configuration of some of the
  resources (for example, HashiCorp Vault).
    - [terraform/local](./terraform/local) - Terraform configuration for any K8S cluster that is configured in the local `kubectl` context.
    - [terraform/remote](./terraform/remote) - Terraform configuration for a remote K8S cluster that sets up FluxCD in that cluster, and then adds this Git repository as the source for provisioning.
    - [terraform/infra](./terraform/infra) - Terraform configuration for provisioning a K8S cluster in a cloud provider.
    - [terraform/modules](./terraform/modules) - reusable Terraform modules.

## Deployment

TODO: separate TF files for local and remote cluster; remote one applies flux-cd.yml, local applies manifests directly
TODO: apply infra/ TF first (optional), then remote/ TF
TODO: if K8S cluster is already provisioned and there's no need for GitOps, apply local/ TF only
