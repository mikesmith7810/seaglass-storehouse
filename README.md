# Stager Hub

Inventory management system for tracking furniture and props across storage locations, with pricing and dimensions. Designed to be used as a PWA on an iPhone.

---

## Architecture

| Layer | Technology |
|---|---|
| Backend | Quarkus 3.35.1 (JAX-RS, Hibernate ORM Panache, Flyway) |
| Frontend | React 19 + Vite 6 + vite-plugin-pwa |
| Database | PostgreSQL 16 |
| Build | Gradle 9.4.1 |

The backend exposes a REST API under `/api`. The frontend is a separate Vite app that proxies `/api` requests to the Quarkus server during development.

---

## Running locally

**Prerequisites:** Java 21, Docker, Node 20+

### 1. Start the database

```bash
docker-compose up -d
```

### 2. Start the backend

```bash
./gradlew quarkusDev
```

Runs on `http://localhost:8080`. Flyway migrations run automatically on startup.

### 3. Start the frontend

```bash
cd frontend && npm install && npm run dev
```

Runs on `http://localhost:5173`. The Vite dev server proxies `/api` to `:8080`.

---

## REST API

### Locations

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/locations` | List all locations |
| `POST` | `/api/locations` | Create a location |
| `DELETE` | `/api/locations/{id}` | Delete a location (blocked if items are assigned) |

### Categories

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/categories` | List all categories |
| `POST` | `/api/categories` | Create a category |
| `DELETE` | `/api/categories/{id}` | Delete a category (blocked if items are assigned) |

### Items

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/items` | List all items |
| `GET` | `/api/items/search` | Search/filter items (`q`, `categoryId`, `locationId`, `minPrice`, `maxPrice`) |
| `GET` | `/api/items/{id}` | Get a single item |
| `POST` | `/api/items` | Create an item |
| `PUT` | `/api/items/{id}` | Update an item |

A Postman collection is available at `postman/stager-hub.json`.

---

## Frontend pages

| Page | Route | Description |
|---|---|---|
| Dashboard | `/` | Per-location summary stats and item counts |
| Browse | `/browse` | Items filtered by location tab |
| Search | `/search` | Full filter form (keyword, category, location, price range) |
| Item detail | `/items/:id` | Full item view with dimensions and label ID |
| Add item | `/items/new` | Create a new inventory item |
| Edit item | `/items/:id/edit` | Edit an existing item |
| Admin | `/admin` | Manage locations and categories |

---

## Data model

Each **Item** has:
- Auto-increment integer ID (printed on a physical label attached to the furniture)
- Description, category, location
- Price (GBP)
- Optional dimensions (height, width, depth in cm)
- Photo URL (stored externally — see Phase 3)

**Locations** and **Categories** are dynamic — managed via the Admin page. Deleting either is blocked if any items are currently assigned to them.

---

## Project status

### Phase 1 — Local foundation (complete)
- Quarkus backend with full REST API
- PostgreSQL schema via Flyway migration
- JPA entities, Panache repositories, CDI services
- 29 unit tests (Mockito + AssertJ)
- Postman collection

### Phase 2 — Frontend PWA (complete)
- React + Vite PWA with offline support via service worker
- Brand-matched design (Seaglass sage, teal, and sand palette; Playfair Display + Lato)
- Mobile-first layout with hamburger navigation
- All CRUD flows: add, edit, view, browse, search, admin

### Phase 3 — GCP deployment (next)
- Dockerfile for Quarkus backend
- Deploy backend to **Cloud Run** (free tier)
- Provision **e2-micro VM** on GCP with self-managed PostgreSQL (always-free tier)
- Deploy frontend static build to **Cloud Storage** behind a CDN
- Secret Manager for production credentials
- Photo upload endpoint (`POST /api/items/{id}/photo`) backed by Cloud Storage bucket

### Phase 4 — PWA polish (backlog)
- Proper iOS home screen icons (192×192, 512×512)
- Offline service worker caching refinement
- Delete item support
- Sold/archived item status
