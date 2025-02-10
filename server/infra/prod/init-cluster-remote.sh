#!/bin/sh

context=$1
namespace=${2:-sms-proxy}

if [ -z "$context" ]; then
  echo "Usage: $0 <context> [namespace]"
  exit 1
fi

cd ./terraform
cd 01-crds && terraform apply -auto-approve -var="context=$context" -var="namespace=$namespace" && cd ..
cd 02-vault && terraform apply -auto-approve -var="context=$context" -var="namespace=$namespace" && cd ..

cd 03-fluxcd && terraform apply -auto-approve  -var="context=$context" -var="namespace=$namespace" && cd ..
cd ..
