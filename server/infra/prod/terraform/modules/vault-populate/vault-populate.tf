terraform {
  required_providers {
    vault = {
      source = "hashicorp/vault"
      version = "4.6.0"
    }
  }
}

provider "vault" {
  # Configuration options
}

# TODO: init
# TODO: unseal
# TODO: setup postgres creds
# TODO: setup kv storage
# TODO: generate secrets?

# TODO: output keys after vault init!

# TODO: output (as references?) names of generates K8S secrets or whatever
