# SMS Relay

`relayApp` is an Android KMP app that relays SMS messages between the user's device and the server.

## Architecture

### Shared module

The app uses functionality from the [`shared` module](../shared). The `shared` module is configured with environment variables or the root [`gradle.properties` file](../gradle.properties), but it is also preconfigured with expected default values. See [`shared/README.md`](../shared/README.md) for more information.

Implementations of shared contracts are injected using Hilt; see [`UsecasesModule.kt`](src/androidMain/kotlin/io/mrnateriver/smsproxy/relay/services/usecases/UsecasesModule.kt) and [`DataModule.kt`](src/androidMain/kotlin/io/mrnateriver/smsproxy/relay/services/data/DataModule.kt) for the modules where that happens.

When implementing shared contracts, the app follows the "Clean Architecture" approach by Robert C. Martin. See [`services/README.md`](src/androidMain/kotlin/io/mrnateriver/smsproxy/relay/services/README.md) for more information.

### API client and codegen

The app uses the server's public certificate and its own private key in PEM formats for mTLS communication with the server. The [Gradle task to generate these certificates](/buildSrc/src/main/kotlin/GenerateCertificatesTask.kt) can be run manually.

The OkHttp client is configured with mTLS support in [`ProxyApiClientFactory.kt`](/shared/src/androidMain/kotlin/io/mrnateriver/smsproxy/shared/services/ProxyApiClientFactory.kt), and provided via Hilt injection in [`ProxyApiModule.kt`](src/androidMain/kotlin/io/mrnateriver/smsproxy/relay/services/data/ProxyApiModule.kt). The required certificates are loaded from assets in the same module.

The app uses code generation for the Proxy API client. The API definitions are specified using TypeSpec in [`proxyApiTypeSpec`](../proxyApiTypeSpec), which generates OpenAPI YAML files. These files are then used by OpenAPI Generator to produce the Kotlin API client code used in the app. The generated code is linked to the app through the [`build.gradle.kts`](../proxyApiTypeSpec/build.gradle.kts) configuration.

### Data storage

The app uses Room for storing relaying statistics. See the [`DataModule.kt`](src/androidMain/kotlin/io/mrnateriver/smsproxy/relay/services/data/DataModule.kt) for the module that sets up Room and the [`MessageDao.kt`](src/androidMain/kotlin/io/mrnateriver/smsproxy/relay/services/data/MessageDao.kt) for the Room DAO definitions.

### More information

See also [architecture documentation](/docs/README.md).

## Testing

The app only uses instrumented Android tests for simplicity in the pipeline.

## File Structure

- [`services`](src/androidMain/kotlin/io/mrnateriver/smsproxy/relay/services) - Clean Architecture services
- [`composables`](src/androidMain/kotlin/io/mrnateriver/smsproxy/relay/composables) - Reusable composables for behavior
- [`layout`](src/androidMain/kotlin/io/mrnateriver/smsproxy/relay/layout) - Parts of the global UI layout
- [`pages`](src/androidMain/kotlin/io/mrnateriver/smsproxy/relay/pages) - Composables specific to different pages
- [`App.kt`](src/androidMain/kotlin/io/mrnateriver/smsproxy/relay/App.kt) - Main entry point in Compose
- [`AppPages.kt`](src/androidMain/kotlin/io/mrnateriver/smsproxy/relay/AppPages.kt) - Static definitions of app pages
- [`AppViewModel.kt`](src/androidMain/kotlin/io/mrnateriver/smsproxy/relay/AppViewModel.kt) - Global app state in a ViewModel
- [`MainApplication.kt`](src/androidMain/kotlin/io/mrnateriver/smsproxy/relay/MainApplication.kt) - App entry point, sets wake lock and schedules background processing worker
- [`MainActivity.kt`](src/androidMain/kotlin/io/mrnateriver/smsproxy/relay/MainActivity.kt) - App activity, necessary for applying edge-to-edge styles
