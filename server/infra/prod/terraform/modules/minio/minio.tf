terraform {
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "2.35.1"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.0"
    }
  }
}

variable "namespace" {
  type        = string
  description = "K8S namespace where the resources will be created"
  default     = "sms-proxy"
}

resource "kubernetes_manifest" "minio_cert_operator" {
  manifest = yamldecode(<<EOF
apiVersion: cert-manager.io/v1
kind: Certificate
metadata:
  name: sts-certmanager-cert
  namespace: ${var.namespace}
spec:
  dnsNames:
    - sts
    - sts.${var.namespace}.svc
    - sts.${var.namespace}.svc.cluster.local
  secretName: sts-tls # must be `sts-tls` according to the MinIO operator docs
  issuerRef:
    name: issuer-leaf
    kind: Issuer
   EOF
  )
}

resource "kubernetes_manifest" "minio_cert_tenant" {
  manifest = yamldecode(<<EOF
apiVersion: cert-manager.io/v1
kind: Certificate
metadata:
  name: minio-tenant-certificate
  namespace: ${var.namespace}
spec:
  dnsNames:
    - "minio"
    - "minio.${var.namespace}"
    - "minio.${var.namespace}.svc"
    - 'minio.${var.namespace}.svc.cluster.local'
    - '*.minio.${var.namespace}.svc.cluster.local'
    - "s3"
    - "s3.${var.namespace}"
    - "s3.${var.namespace}.svc"
    - 's3.${var.namespace}.svc.cluster.local'
    - '*.s3.${var.namespace}.svc.cluster.local'
  secretName: operator-ca-tls-minio-tenant # This name deliberately starts with `operator-ca-tls-` to combine tenant TLS secret and its CA for operator to trust
  issuerRef:
    name: issuer-leaf
    kind: Issuer
   EOF
  )
}

resource "helm_release" "minio" {
  depends_on       = [kubernetes_manifest.minio_cert_operator, kubernetes_manifest.minio_cert_tenant]
  name             = "minio"
  repository       = "https://operator.min.io/"
  chart            = "operator"
  namespace        = var.namespace
  version          = "7.0.0"
  create_namespace = true

  values = [
    <<EOF
    operator:
        replicaCount: 1
        env:
            - name: "OPERATOR_STS_AUTO_TLS_ENABLED"
              value: "off"
            - name: "OPERATOR_STS_ENABLED"
              value: "on"
    EOF
  ]
}

resource "random_password" "minio_root_password" {
  length           = 16
  special          = true
  override_special = "_-%@$#*^!/"
}

locals {
  minio_root_user     = "minio"
  minio_root_password = random_password.minio_root_password.result
}

resource "kubernetes_secret" "minio_tenant_config" {
  metadata {
    name      = "secrets-minio"
    namespace = var.namespace
  }

  data = {
    "config.env" = <<EOF
        export MINIO_ROOT_USER="${local.minio_root_user}"
        export MINIO_ROOT_PASSWORD="${local.minio_root_password}"
    EOF
  }
}

# This secret should be synced with credentials of external S3 storage which are provided for the OCI repository
resource "kubernetes_secret" "oci_repository_s3_credentials" {
  metadata {
    name      = "credentials-s3-oci-registry"
    namespace = var.namespace
  }

  data = {
    "accessKey" = local.minio_root_user
    "secretKey" = local.minio_root_password
  }
}

resource "kubernetes_service" "minio_tenant_lb" {
  depends_on = [helm_release.minio]

  metadata {
    name      = "s3"
    namespace = var.namespace
  }

  spec {
    type = "LoadBalancer"

    selector = {
      "v1.min.io/tenant" = "minio"
    }

    port {
      port        = 5443
      target_port = 9000
      protocol    = "TCP"
    }
  }

  wait_for_load_balancer = false
}

resource "helm_release" "minio_tenant" {
  depends_on       = [helm_release.minio]
  name             = "minio-tenant"
  repository       = "https://operator.min.io/"
  chart            = "tenant"
  namespace        = var.namespace
  version          = "7.0.0"
  create_namespace = true

  values = [
    <<EOF
    tenant:
        name: minio
        configuration:
            name: secrets-minio
        configSecret:
            name: secrets-minio
            existingSecret: true
            accessKey: ""
            secretKey: ""
        metrics:
            enabled: true
        prometheusOperator: false # MinIO operator looks for Prometheus in the default namespace
        buckets:
            - name: oci-registry
        users: 
            - name: oci-registry
        certificate:
            requestAutoCert: false
            externalCertSecret:
                - name: operator-ca-tls-minio-tenant
                  type: cert-manager.io/v1
        pools:
            - name: minio-pool
              servers: 1
              volumesPerServer: 1
              size: 5Gi
    # Ingresses are disabled because the whole storage is internal
    ingress:
        api:
            enabled: false
        console:
            enabled: false
    EOF
  ]
}

