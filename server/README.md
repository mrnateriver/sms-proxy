# SMS Proxy Server

## Overview

This module is the server component of the SMS Proxy application. It handles the relaying of SMS messages between the
user's device and the server.

## Setup

### Generate Certificates

#### Ingress TLS

Even for local development, the server can run with ingress TLS. Before running the server, generate the certificate:

```sh
./gradlew :server:generateProxyApiCertificate
```

See [Configuration](#configuration) for configuring the server to use the generated certificate.

#### Client mTLS

All clients communicate with the server over mTLS, so their certificates must also be generated and their public keys
added to the server's assets before running it. To generate `relayApp`'s client certificate pair:

```sh
./gradlew :relayApp:generateProxyApiCertificate
```

See [relayApp](../relayApp/README.md) and [receiverApp](../receiverApp/README.md) documentation for more information.

### Build SQL migrations

> This is a temporary step until a custom Containerfile is created for Flyway migrations that would build them as part
> of container image build.

Before running Flyway as part of the development [docker-compose.yml](infra/local/docker-compose.yml), it's necessary to
generate
SQL migrations using SQLDelight's Gradle task:

```sh
./gradlew :server:generateMainDatabaseMigrations
```

### Configuration

The server configuration is managed through environment variables. Key configurations include:

| Variable                  | Default   | Required                                          | Description                                                                |
|---------------------------|-----------|---------------------------------------------------|----------------------------------------------------------------------------|
| `KTOR_LOG_LEVEL`          | debug     |                                                   | Log level. Applied to Ktor internal logging and Logback's overall level.   |
| `OTLP_TRACING_GRPC_URL`   |           |                                                   | Base URL for sending traces to via OTLP. Must include protocol.            |
| `OTLP_SERVICE_NAME`       | sms-proxy |                                                   | Service name used in OT traces.                                            |
| `METRICS_HTTP_PORT`       | 4000      |                                                   | Port used for serving Prometheus metrics endpoint.                         |
| `SENTRY_DSN`              |           |                                                   | Sentry DSN.                                                                |
| `SERVER_HOST`             | 127.0.0.1 |                                                   | Host for the server to listen on.                                          |
| `SERVER_PORT`             | 4430      |                                                   | Port for the server to listen on.                                          |
| `API_KEY` *               |           | Yes                                               | The API key used to authenticate clients.                                  |
| `HASHING_SECRET`          |           | Yes                                               | Secret key for hashing values using SHA512 HMAC.                           |
| `DB_JDBC_URI`             |           | Yes                                               | JDBC URI for connecting to the database.                                   |
| `DB_USER`                 |           | Yes                                               | DB user.                                                                   |
| `DB_PASSWORD`             |           | Yes                                               | DB password.                                                               |
| `CERT_KEY_STORE_PASSWORD` |           | If `CERT_JKS_PATH` and `CERT_CLIENTS_PATH` is set | Password for the keystore used in TLS.                                     |
| `CERT_KEY_PASSWORD`       |           | If `CERT_JKS_PATH` and `CERT_CLIENTS_PATH` is set | Password for the server TLS certificate within the keystore.               |
| `CERT_KEY_ALIAS`          | serverKey |                                                   | Server TLS certificate's alias in the keystore.                            |
| `CERT_JKS_PATH`           |           |                                                   | Path to the keystore that is used for TLS.                                 |
| `CERT_CLIENTS_PATH`       |           |                                                   | Path to the directory with clients' public TLS certificates in PEM format. |

API Key can also be set using `io.mrnateriver.smsproxy.server.apiKey` system property. It is used by default to
synchronize the key between Android apps and the server.

## Running Locally

The server and all of the required infrastructure services can be run locally using Docker Compose:

```sh
cd server/infra/local
docker-compose up
```

In order to launch the server on the host JVM (for example for debugging purposes), you can shut down Traefik and server
containers, while the Postgres and Grafana Alloy containers, the only two required services, will be available from the
host machine. With this approach, however, Alloy will not be able to collect server's logs.

## Deployment

### Production Deployment

Cluster deployment are managed using FluxCD from the `server/infra/prod` directory.

It will use HelmRelease resources for dependencies and HashiCorp Vault as CSI for secrets.

TODO: K8S diagram
