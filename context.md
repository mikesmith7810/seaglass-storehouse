# Session Context

This file provides continuity between Claude Code sessions for the Stager Hub project.

---

## What this project is

A home staging inventory PWA. Tracks furniture and props across storage locations with pricing, dimensions, and physical label IDs. Used on iPhone. Built as a personal project.

---

## Tech stack decisions (and why)

| Decision | Choice | Reason |
|---|---|---|
| Backend | Quarkus 3.35.1 | Chosen over Spring Boot; Gradle 9.4.1 requires 3.35.1+ (3.15.1 incompatible) |
| Frontend | React 19 + Vite 6 + vite-plugin-pwa | PWA for iPhone home screen install |
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
- **CORS**: Permissively configured (`*`) for now; will be restricted to Cloud Storage domain in Phase 3
- **JAX-RS path routing**: Item endpoints use `@Path("/{id:\\d+}")` regex to avoid conflict with the `/search` literal path
- **Frontend dev**: Vite proxies `/api` to `localhost:8080`, so no CORS issues in dev. `allowedHosts: true` set for ngrok access from iPhone

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
└── frontend/
    ├── vite.config.js      # Dev proxy + PWA manifest; allowedHosts: true
    ├── src/
    │   ├── api/client.js   # Fetch wrapper (uses VITE_API_URL env var)
    │   ├── components/     NavBar (SVG wordmark), ItemCard
    │   ├── pages/          Dashboard, Browse, Search, ItemDetail, ItemForm, Admin
    │   └── styles/global.css  # Brand CSS variables, full component styles
    └── public/logo.png
```

---

## Frontend UI notes

- **Font**: Source Sans 3 (Google Fonts) — used for both heading and body
- **Navbar**: CSS/SVG wordmark ("Stager Hub" + house icon), no image logo
- **Dashboard**: stat cards in a fixed 3-column row (always horizontal); label above number; "Items" card links to /browse
- **Browse**: location filter is a single dropdown pill (not a tab row); shows "All" or the active location name
- **Admin — Locations**: separate form with name + optional photo URL inputs; thumbnails shown in list

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
| Phase 1 — Local foundation (backend, DB, tests, Postman) | Complete |
| Phase 2 — React PWA frontend | Complete |
| Phase 3 — GCP deployment | Not started |
| Phase 4 — PWA polish | Not started |

---

## Phase 3 scope (next up)

1. Dockerfile for Quarkus backend
2. Deploy backend to Cloud Run (free tier)
3. Provision e2-micro VM on GCP, install PostgreSQL, create DB + user
4. Deploy frontend static build (`npm run build`) to Cloud Storage bucket, configure as public static site
5. Secret Manager for production DB credentials
6. Implement `POST /api/items/{id}/photo` and `POST /api/locations/{id}/photo` — upload to Cloud Storage, store URL in `photo_url`
7. Restrict CORS to the Cloud Storage frontend domain

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
