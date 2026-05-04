# Session Context

This file provides continuity between Claude Code sessions for the Seaglass Storehouse project.

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
- **Dimensions**: Stored in cm (heightCm, widthCm, depthCm), all optional
- **Prices**: GBP, stored as DECIMAL(10,2)
- **Photo URLs**: Column exists in schema and entity (`photo_url`) but upload not yet implemented — deferred to Phase 3
- **CORS**: Permissively configured (`*`) for now; will be restricted to Cloud Storage domain in Phase 3
- **JAX-RS path routing**: Item endpoints use `@Path("/{id:\\d+}")` regex to avoid conflict with the `/search` literal path
- **Frontend dev**: Vite proxies `/api` to `localhost:8080`, so no CORS issues in dev

---

## Project structure

```
seaglass-storehouse/
├── build.gradle                          # Quarkus 3.35.1, all deps
├── docker-compose.yml                    # PostgreSQL 16 for local dev
├── postman/seaglass-storehouse.json      # 11-request Postman collection
├── logo-black-write-trans.png            # Brand logo (copied to frontend/public/logo.png)
├── src/main/
│   ├── resources/
│   │   ├── application.properties        # Quarkus config, CORS, DB (dev profile)
│   │   └── db/migration/V1__initial_schema.sql
│   └── java/com/mike/seaglassstorehouse/
│       ├── entity/         Location, Category, Item (JPA + Panache)
│       ├── repository/     LocationRepository, CategoryRepository, ItemRepository
│       ├── model/          Records: *Request, *Response, ErrorResponse
│       ├── service/        LocationService, CategoryService, ItemService
│       ├── resource/       LocationResource, CategoryResource, ItemResource (JAX-RS)
│       └── exception/      ConflictException + ConflictExceptionMapper
├── src/test/               29 unit tests (Mockito + AssertJ)
└── frontend/
    ├── vite.config.js      # Dev proxy + PWA manifest
    ├── src/
    │   ├── api/client.js   # Fetch wrapper (uses VITE_API_URL env var)
    │   ├── components/     NavBar, ItemCard
    │   ├── pages/          Dashboard, Browse, Search, ItemDetail, ItemForm, Admin
    │   └── styles/global.css  # Brand CSS variables, full component styles
    └── public/logo.png
```

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
3. Provision e2-micro VM on GCP, install PostgreSQL, create `seaglass` DB + user
4. Deploy frontend static build (`npm run build`) to Cloud Storage bucket, configure as public static site
5. Secret Manager for production DB credentials
6. Implement `POST /api/items/{id}/photo` — upload to Cloud Storage, store URL in `photo_url`
7. Restrict CORS to the Cloud Storage frontend domain

---

## Known gotchas

- Quarkus 3.15.1 is incompatible with Gradle 9.4.1 — must use 3.35.1+
- `assertj-core` is not in the Quarkus BOM; must specify version explicitly
- Use `quarkus-junit5-mockito` (not `mockito-core`) — provides `MockitoExtension`
- `enforcedPlatform` BOM import must be on `testImplementation` scope as well as `implementation`
- In unit tests, entity IDs (private, no setter) must be set via reflection: `field.setAccessible(true)`
