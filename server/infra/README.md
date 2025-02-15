# Infrastructure as Code

* [Dockerfile.app](Dockerfile.app) - Dockerfile for building the server app image.
* [Dockerfile.migrations](Dockerfile.migrations) - Dockerfile for building the server DB migrations job image.
* [local](./local) - Local development environment with Docker Compose.
* [prod](./prod) - Production environment with Terraform and Kubernetes. "Production" here means any environment above
  local, so it can be staging, testing, etc up to and including actual production.
