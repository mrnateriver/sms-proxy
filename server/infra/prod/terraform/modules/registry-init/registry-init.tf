
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
    force_redeploy = uuid()
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
    force_redeploy = uuid()
  }

  provisioner "local-exec" {
    interpreter = ["/bin/sh", "-c"]
    command     = <<EOF
        until kubectl get secret -n ${var.namespace} password-oci-registry >/dev/null 2>&1; do
            echo "Waiting for secret 'password-oci-registry' to be created..."
            sleep 2
        done
        echo "Secret 'password-oci-registry' has been created!"

        if ! kubectl get secret -n ${var.namespace} credentials-docker-oci-registry; then
            kubectl get secret -n ${var.namespace} password-oci-registry -o jsonpath='{.data.password}' | base64 --decode | xargs -I {} \
                kubectl -n ${var.namespace} create secret docker-registry credentials-docker-oci-registry \
                    --docker-server=oci-registry:5555 \
                    --docker-username=sms-proxy \
                    --docker-password={}
        fi
    EOF
  }
}


resource "null_resource" "registry_kubernetes_images_push" {
  depends_on = [null_resource.registry_kubernetes_docker_auth]

  triggers = {
    force_redeploy = uuid()
  }

  provisioner "local-exec" {
    interpreter = ["/bin/sh", "-c"]
    working_dir = abspath("${path.module}/../../../../../../")
    command     = <<EOF
        kubectl get pods -n ${var.namespace} -l app=oci-registry -o jsonpath='{.items[*].metadata.name}' |
            xargs -n1 -I {} kubectl wait -n ${var.namespace} pods/{} --for=condition=Ready --timeout=300s

        REGISTRY_PWD=$(kubectl get secret -n ${var.namespace} password-oci-registry -o jsonpath='{.data.password}' | base64 -d)
        docker login -u=sms-proxy -p=$REGISTRY_PWD oci-registry:5555

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

        if [ -z "$(docker images -q sms-proxy:latest)" ]; then
            docker build -f server/infra/Dockerfile.app -t sms-proxy:latest .
        fi
        if [ -z "$(docker images -q sms-proxy-migrations:latest)" ]; then
            docker build -f server/infra/Dockerfile.migrations -t sms-proxy-migrations:latest .
        fi

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
