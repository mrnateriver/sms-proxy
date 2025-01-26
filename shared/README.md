# Shared Module

The `shared` module contains reusable components and functions for Android SDK and Jetpack Compose, as well as a service
for processing incoming messages on both client apps and the server. This module is designed to be platform-agnostic,
providing a consistent API for various parts of the system.

## Architecture

The architecture of the `shared` module is built around a set of contracts that define the interactions between
different components. These contracts are implemented by the downstream users, allowing for flexibility and
customization.

### Key Components

- [Android](src/androidMain): A set of Android SDK and Jetpack Compose components that can be used across different
  parts of the application.
- [Message Processing Service](src/commonMain/kotlin/io/mrnateriver/smsproxy/shared/services/MessageProcessingService.kt):
  A service responsible for processing incoming messages. It operates based on several contracts provided by the
  downstream users and used by both backend and frontend applications.

Read more about common processing service in
corresponding [README](src/commonMain/kotlin/io/mrnateriver/smsproxy/shared/README.md).
