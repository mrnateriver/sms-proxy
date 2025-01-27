# Message Proxy

[![Server](https://github.com/mrnateriver/sms-proxy/actions/workflows/main-server.yml/badge.svg)](https://github.com/mrnateriver/sms-proxy/actions/workflows/main-server.yml)
[![Relay](https://github.com/mrnateriver/sms-proxy/actions/workflows/main-relayApp.yml/badge.svg)](https://github.com/mrnateriver/sms-proxy/actions/workflows/main-relayApp.yml)
[![Receiver](https://github.com/mrnateriver/sms-proxy/actions/workflows/main-receiverApp.yml/badge.svg)](https://github.com/mrnateriver/sms-proxy/actions/workflows/main-receiverApp.yml)

Modules:

- [`relayApp`](relayApp/) - Android [KMP](https://kotlinlang.org/docs/multiplatform.html) app that relays SMS messages
  between the user's device and the server.
- [`proxyApiTypeSpec`](proxyApiTypeSpec/) - [TypeSpec](https://typespec.io/) of the API.
- [`shared`](shared/) - Shared module with platform-agnostic service contracts and implementation of processing
  pipeline.
- [`server`](server/) - Server that receives relayed messages, stores them and proxies to registered receivers.
- [`receiverApp`](receiverApp/) - Android [KMP](https://kotlinlang.org/docs/multiplatform.html) app that receives
  relayed messages via FCM.

## Architecture

See [architecture documentation](docs/README.md).

### Stack

#### Android

[Android SDK](https://developer.android.com/develop); [Kotlin Multiplatform](https://www.jetbrains.com/kotlin-multiplatform/);
[Jetpack Compose](https://developer.android.com/compose); [Hilt](https://dagger.dev/hilt/); [Room](https://developer.android.com/training/data-storage/room/);
[OkHttp](https://square.github.io/okhttp/); [Retrofit](https://square.github.io/retrofit/); [JUnit](https://junit.org/junit5/); [Mockito](https://site.mockito.org/)

#### Server

[Kotlin Multiplatform](https://www.jetbrains.com/kotlin-multiplatform/); [Ktor](https://ktor.io/); [SQLDelight](https://github.com/sqldelight/sqldelight); [Logback](https://logback.qos.ch/index.html); [HikariCP](https://github.com/brettwooldridge/HikariCP); [Dagger](https://dagger.dev); [OpenTelemetry SDK](https://opentelemetry.io/docs/languages/java/intro/);
[JUnit](https://junit.org/junit5/); [Mockito](https://site.mockito.org/)

#### Infrastructure

[PostgreSQL](https://www.postgresql.org/); [Flyway](https://www.red-gate.com/products/flyway/community/); [Traefik](https://doc.traefik.io/traefik/);
[Grafana](https://grafana.com/); [Prometheus](https://prometheus.io/); [Loki](https://grafana.com/oss/loki/); [Tempo](https://grafana.com/oss/tempo/);
[Alloy](https://grafana.com/oss/alloy-opentelemetry-collector/); [cAdvisor](https://github.com/google/cadvisor);
[node_exporter](https://github.com/prometheus/node_exporter); [Sentry](https://sentry.io/); [HashiCorp Vault](https://www.hashicorp.com/products/vault);
[Kubernetes](https://kubernetes.io/); [FluxCD](https://fluxcd.io/); [Helm](https://helm.sh/); [Snyk](https://snyk.io/)

## Running

### Running and deploying

General steps to run the system:

1. Deploy the server.
2. Build `relayApp` and `receiverApp`, install on different devices.
3. Run the receiver, let it register on the server and issue a receiver key.
4. Run the relay app, configure the server URL and receiver key from the previous point.

See [relayApp/README.md](relayApp/README.md) and [receiverApp/README.md](receiverApp/README.md) for instructions for
building and running the apps.

See [server/README.md](server/README.md) for instructions for configuring and deploying the server.

## Development

The easiest way to start is to use [Android Studio](https://developer.android.com/studio).

[Detekt](https://detekt.dev/) is used for both linting and formatting (formatting is delegated
to [KtLint](https://pinterest.github.io/ktlint/latest/)) the whole project,
so [corresponding IDE plugin](https://plugins.jetbrains.com/plugin/10761-detekt) might be useful.

[Docker](https://www.docker.com/) (or any other compatible alternative) can be used to run the required infrastructure
for local development. See [docker-compose.yml](server/docker-compose.yml).

## License

[GPLv3](LICENSE)
