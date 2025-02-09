// TODO: fine-grained ACL for Vault provisioning
path "/*" {
  capabilities = ["create", "read", "update", "delete", "list", "sudo"]
}
