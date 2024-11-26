# Clean Architecture

`MessageProcessingService` corresponds to the "Use Cases" of
the [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) and contains
reusable business logic.

Contracts are intended to be implemented in the "Interface Adapters" layer, although `MessageProcessingService`
interface exists only for automatically mocking the use case service in tests.

Models correspond to the "Entities" layer.

However, practically speaking, adherence to particular categories of the Clean Architecture depends on the concrete
implementation, since this package is platform-agnostic and can be used in different contexts. For example,
`MessageRelayService` can actually be a Use Case if it depends on user-provided settings, like in the `relayApp` module.
