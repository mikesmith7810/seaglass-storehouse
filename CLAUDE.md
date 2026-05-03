# Project Conventions

## Java

- All variables must be `final` where possible
- Variable names must reflect their type using camelCase — e.g. a `StorageHouse` instance is named `storageHouse`, a `List<Item>` is named `itemList`
- No Javadoc comments — code and naming should be self-describing

## Quarkus Architecture

Strict layered architecture with no logic leaking between layers:

- **Resource** (`@Path` JAX-RS class) — HTTP boundary only; no business logic; delegates entirely to a service
- **Service** (`@ApplicationScoped` CDI bean) — all business logic lives here; dependencies (repositories, third-party clients, etc.) are injected via `@Inject`

## Model / DTO Objects

- All DTOs, request/response objects, and value types go in the `model` package
- Use Java `record` types wherever possible

## Testing

- Unit tests must exist for each component (controller, service, etc.)
- Use **AssertJ** for assertions
- Unit tests only — no integration tests at this stage
