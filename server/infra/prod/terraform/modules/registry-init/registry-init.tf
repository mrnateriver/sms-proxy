
terraform {
  required_providers {
  }
}

variable "namespace" {
  type        = string
  description = "K8S namespace"
  default     = "sms-proxy"
}

resource "null_resource" "registry_check_hosts" {
  triggers = {
    registry_namespace = var.namespace
  }

  provisioner "local-exec" {
    interpreter = ["/bin/sh", "-c"]
    command     = <<EOF
        if ! grep -E -q "127\.0\.0\.1\s+oci-registry$" /etc/hosts || ! grep -E -q "127\.0\.0\.1\s+s3$" /etc/hosts; then
            echo "Add the following entries to /etc/hosts:"
            echo "127.0.0.1 oci-registry"
            echo "127.0.0.1 s3"
            exit 1
        fi
    EOF
  }
}

resource "null_resource" "registry_kubernetes_docker_auth" {
  depends_on = [null_resource.registry_check_hosts]

  triggers = {
    registry_namespace = var.namespace
  }

  provisioner "local-exec" {
    interpreter = ["/bin/sh", "-c"]
    command     = <<EOF
        until kubectl get secret -n ${var.namespace} oci-registry-password >/dev/null 2>&1; do
            echo "Waiting for secret 'oci-registry-password' to be created..."
            sleep 2
        done
        echo "Secret 'oci-registry-password' has been created!"

        if ! kubectl get secret -n ${var.namespace} oci-registry-docker-secret; then
            kubectl get secret -n ${var.namespace} oci-registry-password -o jsonpath='{.data.password}' | base64 --decode | xargs -I {} \
                kubectl -n ${var.namespace} create secret docker-registry oci-registry-docker-secret \
                    --docker-server=oci-registry:5000 \
                    --docker-username=sms-proxy \
                    --docker-password={}
        fi
    EOF
  }
}


resource "null_resource" "registry_kubernetes_images_push" {
  depends_on = [null_resource.registry_kubernetes_docker_auth]

  triggers = {
    registry_namespace = var.namespace
  }

  provisioner "local-exec" {
    interpreter = ["/bin/sh", "-c"]
    command     = <<EOF
        REGISTRY_PWD=$(kubectl get secret -n ${var.namespace} oci-registry-password -o jsonpath='{.data.password}' | base64 -d)
        docker login -u=sms-proxy -p=$REGISTRY_PWD oci-registry:5000

        APP_IMAGE=$(docker run --rm \
            -v "${abspath("${path.module}/../../../k8s")}:/app" \
            -w="/app" \
            mikefarah/yq:4.45.1 \
            'select(.kind == "Deployment") | .spec.template.spec.containers[0].image' 07-app.yml
        )
        MIGRATIONS_IMAGE=$(docker run --rm \
            -v "${abspath("${path.module}/../../../k8s")}:/app" \
            -w="/app" \
            mikefarah/yq:4.45.1 \
            'select(.kind == "Job") | .spec.template.spec.containers[0].image' 06-app-migrations.yml
        )

        if [[ $(docker images -q sms-proxy:latest) ]]; then
            docker tag sms-proxy:latest $APP_IMAGE
            docker push $APP_IMAGE
        fi
        if [[ $(docker images -q sms-proxy-migrations:latest) ]]; then
            docker tag sms-proxy-migrations:latest $MIGRATIONS_IMAGE
            docker push $MIGRATIONS_IMAGE
        fi
    EOF
  }
}
