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
  # TODO: port-forward from k8s cluster - use `null_resource` to run `kubectl port-forward` command, save $! to a file, then to output, delete temp file, and then use the PID in this module to kill the port-forward process
  # TODO: use root token for auth
}

# TODO: setup postgres creds
# TODO: setup kv storage
# TODO: generate secrets?

# TODO: output (as references?) names of generates K8S secrets or whatever

# TODO: revoke root token when done
