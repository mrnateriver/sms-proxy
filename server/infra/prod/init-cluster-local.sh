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

cd 01-crds && terraform init && terraform apply -auto-approve -var="context=$context" -var="namespace=$namespace" && cd ..
cd 02-vault && terraform init && terraform apply -auto-approve -var="context=$context" -var="namespace=$namespace" && cd ..

cd 03-app && terraform init && terraform apply -auto-approve  -var="context=$context" -var="namespace=$namespace" && cd ..
cd ..
