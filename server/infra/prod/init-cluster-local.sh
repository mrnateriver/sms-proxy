#!/bin/sh

context=$1
namespace=${2:-sms-proxy}

if [ -z "$context" ]; then
  echo "Usage: $0 <context> [namespace]"
  exit 1
fi

export KUBE_CONFIG_PATH="~/.kube/config"

cd ./terraform
rm */terraform.tfstate */terraform.tfstate.backup > /dev/null 2>&1

provision() {
    terraform init && terraform apply -auto-approve -var="context=$context" -var="namespace=$namespace"
}

cd 01-crds && provision && cd ..
cd 02-consul && provision && cd ..
cd 03-vault && provision && cd ..
cd 04-app && provision && cd ..

cd ..
