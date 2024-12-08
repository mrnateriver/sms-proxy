# Message Proxy

Modules of the system:

- [`relayApp`](relayApp/) - Android KMP app that relays SMS messages between the user's device and the server.
- [`proxyApiTypeSpec`](proxyApiTypeSpec/) - TypeSpec of the API.
- [`shared`](shared/) - Shared module with platform-agnostic service contracts and implementation of processing
  pipeline.
- [`server`](server/) - Server that receives relayed messages, stores them and proxies to registered receivers.
- [`receiverApp`](receiverApp/) - Android KMP app that receives relayed messages via FCM.

## Architecture

See [architecture documentation](docs/README.md).

## Running

Steps:

1. Deploy the server.
2. Build `relayApp` and `receiverApp`, install on different devices.
3. Run the receiver, let it register on the server and issue a receiver key.
4. Run the relay app, configure the server URL and receiver key from the previous point.

TODO: elaborate on above steps

TODO: before running `docker compose up`, build the server to create SQL migrations

### Prerequisites

TODO: describe stack; JDK, Android SDK

TODO: generate client/server certificates before build: `generateProxyApiCertificate`

### Android Apps

See [relayApp/README.md](relayApp/README.md) and [receiverApp/README.md](receiverApp/README.md) for instructions of
building and running the apps.

### Server

TODO: Dockerfile

#### Infrastructure

TODO: Terraform/K8S/Helm?

#### Deployment

See [server/README.md](server/README.md) for instructions of deploying and configuring the server.

## License
