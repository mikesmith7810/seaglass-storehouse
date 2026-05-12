# Session Context

This file provides continuity between Claude Code sessions for the Stager Hub project.

---

## What this project is

A home staging inventory app. Tracks furniture and props across storage locations with pricing, dimensions, and physical label IDs. Used on iPhone. Built as a personal project.

The frontend is being replaced: the React PWA is being removed and replaced with a native Swift iOS app. The core new capability is RFID scanning via a TSL 1128 Bluetooth reader to update item locations without manual input.

---

## Tech stack decisions (and why)

| Decision | Choice | Reason |
|---|---|---|
| Backend | Quarkus 3.35.1 | Chosen over Spring Boot; Gradle 9.4.1 requires 3.35.1+ (3.15.1 incompatible) |
| Mobile app | Swift + SwiftUI (iOS) | Replacing React PWA; native required for TSL 1128 SDK |
| Database | PostgreSQL 16 | Self-managed on GCP e2-micro (always-free tier) |
| Migrations | Flyway | Versioned schema, runs at startup in dev |
| Testing | Mockito + AssertJ | Pure unit tests only, no @QuarkusTest |
| Build | Gradle 9.4.1 | Pre-existing; drove Quarkus version constraint |
| Hosting plan | GCP Cloud Run (backend) + Cloud Storage (frontend) + e2-micro VM (DB) | Zero-cost using free tiers |

---

## Key architectural decisions

- **Item IDs**: Auto-increment integers starting at 1 — printed on physical labels attached to furniture
- **Locations & Categories**: Dynamic, DB-managed. Delete is blocked (HTTP 409) if any items are assigned. Managed via the Admin page in the frontend
- **Location photos**: `photo_url VARCHAR(500)` stored in DB (V2 migration). Entered as a URL in Admin. Full upload to Cloud Storage deferred to Phase 3
- **Dimensions**: Stored in cm (heightCm, widthCm, depthCm), all optional
- **Prices**: GBP, stored as DECIMAL(10,2)
- **Item photo URLs**: Column exists in schema and entity (`photo_url`) but upload not yet implemented — deferred to Phase 3
- **CORS**: Permissively configured (`*`) for now; will be restricted to Cloud Run domain in Phase 5
- **JAX-RS path routing**: Item endpoints use `@Path("/{id:\\d+}")` regex to avoid conflict with the `/search` literal path
- **RFID EPC**: `rfid_epc` column will be added to the `item` table (nullable); stores the EPC programmed onto the physical RFID tag attached to that item
- **RFID scanner abstraction**: A `RFIDScanner` Swift protocol with `MockRFIDScanner` (in-app simulate button, no Bluetooth) and `TSL1128Scanner` (real SDK) implementations, toggled by a build flag
- **Scan-to-move flow**: User selects destination location → scans tags → each EPC resolves to an item via `GET /api/items/epc/{epc}` → location patched via `PATCH /api/items/{id}/location`
- **Apple Developer license**: Not yet purchased. Free Xcode provisioning used for on-device testing (7-day certificates). Mock scanner runs in iOS Simulator so hardware is not required for Phases 1–3

---

## Project structure

```
stager-hub/
├── build.gradle                          # Quarkus 3.35.1, all deps
├── docker-compose.yml                    # PostgreSQL 16 for local dev
├── postman/stager-hub.json               # Postman collection
├── src/main/
│   ├── resources/
│   │   ├── application.properties        # Quarkus config, CORS, DB (dev profile)
│   │   ├── db/migration/V1__initial_schema.sql
│   │   └── db/migration/V2__add_location_photo.sql
│   └── java/com/mike/stagerhub/
│       ├── entity/         Location, Category, Item (JPA + Panache)
│       ├── repository/     LocationRepository, CategoryRepository, ItemRepository
│       ├── model/          Records: *Request, *Response, ErrorResponse
│       ├── service/        LocationService, CategoryService, ItemService
│       ├── resource/       LocationResource, CategoryResource, ItemResource (JAX-RS)
│       └── exception/      ConflictException + ConflictExceptionMapper
├── src/test/               Unit tests (Mockito + AssertJ)
└── frontend/               TO BE DELETED — replaced by native Swift iOS app
```

The iOS app will live in a separate Xcode project directory (e.g. `ios/StagerHub.xcodeproj`). It communicates with the Quarkus backend over HTTP using `URLSession` + `async/await`.

---

## Conventions (from CLAUDE.md)

- All variables `final` where possible
- Type-reflective naming (`locationRepository` for `LocationRepository`)
- `Resource` (JAX-RS `@Path`) → `Service` (`@ApplicationScoped` CDI) pattern
- Records in `model` package where possible
- AssertJ for all unit tests
- No Javadoc
- Constructor injection in services and resources (for testability)

---

## Gradle dependencies (notable)

```groovy
implementation enforcedPlatform("io.quarkus.platform:quarkus-bom:3.35.1")
testImplementation enforcedPlatform("io.quarkus.platform:quarkus-bom:3.35.1")  // required — BOM must be on both scopes
implementation 'io.quarkus:quarkus-rest'
implementation 'io.quarkus:quarkus-rest-jackson'
implementation 'io.quarkus:quarkus-hibernate-orm-panache'
implementation 'io.quarkus:quarkus-jdbc-postgresql'
implementation 'io.quarkus:quarkus-flyway'
testImplementation 'io.quarkus:quarkus-junit5'
testImplementation 'org.assertj:assertj-core:3.26.3'   // not in BOM — explicit version required
testImplementation 'io.quarkus:quarkus-junit5-mockito' // includes mockito-junit-jupiter
```

---

## Phase status

| Phase | Status |
|---|---|
| Phase 0 — Local foundation (backend, DB, tests, Postman) | Complete |
| Phase 1 — Swift app foundation (Xcode project, API layer, free provisioning) | Not started |
| Phase 2 — Core screens (item list, detail, add/edit, search, admin) | Not started |
| Phase 3 — RFID scanner abstraction + mock + scan-to-move flow | Not started |
| Phase 4 — Real TSL 1128 integration (requires hardware) | Not started |
| Phase 5 — GCP deployment | Not started |

---

## Backend additions required before Phase 3

These backend changes are needed before the RFID scan flow can work:

1. **`V3__add_rfid_epc.sql`** migration — add `rfid_epc VARCHAR(100) UNIQUE` (nullable) to `item`
2. **`GET /api/items/epc/{epc}`** — look up a single item by its RFID EPC
3. **`PATCH /api/items/{id}/location`** — update only the `location_id` of an item (avoids requiring a full PUT payload at scan time)

---

## Phase 5 scope (GCP deployment)

| Component | Solution |
|---|---|
| Backend | Quarkus container on Cloud Run (free tier) |
| Database | PostgreSQL on always-free e2-micro Compute Engine VM |
| Photos | Cloud Storage bucket |
| Secrets | Secret Manager |

Steps:
1. Dockerfile for Quarkus backend
2. Deploy backend to Cloud Run
3. Provision e2-micro VM, install PostgreSQL, create DB + user
4. Implement `POST /api/items/{id}/photo` and `POST /api/locations/{id}/photo` — upload to Cloud Storage, store URL in `photo_url`
5. Secret Manager for production DB credentials
6. Restrict CORS to the Cloud Run domain

---

## Known gotchas

- Quarkus 3.15.1 is incompatible with Gradle 9.4.1 — must use 3.35.1+
- `assertj-core` is not in the Quarkus BOM; must specify version explicitly
- Use `quarkus-junit5-mockito` (not `mockito-core`) — provides `MockitoExtension`
- `enforcedPlatform` BOM import must be on `testImplementation` scope as well as `implementation`
- In unit tests, entity IDs (private, no setter) must be set via reflection: `field.setAccessible(true)`
- Entity fields with camelCase names that map to snake_case columns **must** have explicit `@Column(name = "snake_case")` — Hibernate will not find them otherwise
- `TIMESTAMP` columns mapped to `Instant` must use `TIMESTAMPTZ` in SQL — otherwise Hibernate schema validation fails
- `String` fields without `length` in `@Column` default to `varchar(255)` — must match migration exactly (e.g. `length = 100` for name fields)
- `BigDecimal` fields without `precision`/`scale` default to `numeric(38,2)` — must specify `precision = 10, scale = 2` to match `DECIMAL(10,2)` in migration
