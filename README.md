# Stager Hub

Inventory management system for tracking furniture and props across storage locations, with pricing and dimensions.

---

## Architecture

| Layer | Technology |
|---|---|
| Backend | Quarkus 3.35.1 (JAX-RS, Hibernate ORM Panache, Flyway) |
| iOS app | SwiftUI, iOS 17+, XcodeGen |
| Database | PostgreSQL 16 |
| Build | Gradle 9.4.1 |

The backend exposes a REST API under `/api`. The iOS app communicates with it directly over the local network.

---

## Running locally

**Prerequisites:** Java 21, Docker, Xcode 15+, [XcodeGen](https://github.com/yonaskolb/XcodeGen)

### 1. Start the database

```bash
docker-compose up -d
```

### 2. Start the backend

```bash
./gradlew quarkusDev
```

Runs on `http://localhost:8080`. Flyway migrations run on startup.

### 3. Generate the Xcode project

```bash
cd ios && xcodegen generate
```

This produces `ios/StagerHub.xcodeproj`. Only needs re-running when `project.yml` changes.

### 4. Configure the API base URL

Open `ios/StagerHub/Config.swift`:

- **Simulator** — `localhost:8080` works as-is, no change needed.
- **Physical device** — replace with your Mac's local IP address:

  ```swift
  static let apiBaseURL = "http://192.168.1.x:8080"
  ```

  Find your Mac's IP in System Settings → Wi-Fi → Details, or run `ipconfig getifaddr en0`.

### 5. Build and run

Open `ios/StagerHub.xcodeproj` in Xcode, select a simulator or connected device, and press **Run**.

---

## REST API

### Items

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/items` | List all items |
| `GET` | `/api/items/{id}` | Get a single item |
| `GET` | `/api/items/search` | Filter items (`q`, `categoryId`, `locationId`, `minPrice`, `maxPrice`) |
| `POST` | `/api/items` | Create an item |
| `PUT` | `/api/items/{id}` | Update an item |
| `DELETE` | `/api/items/{id}` | Delete an item |

### Locations

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/locations` | List all locations |
| `POST` | `/api/locations` | Create a location |
| `PUT` | `/api/locations/{id}` | Update a location |
| `DELETE` | `/api/locations/{id}` | Delete a location (blocked if items are assigned) |

### Categories

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/categories` | List all categories |
| `POST` | `/api/categories` | Create a category |
| `DELETE` | `/api/categories/{id}` | Delete a category (blocked if items are assigned) |

A Postman collection is available at `postman/stager-hub.json`.

---

## Data model

Each **Item** has:
- Auto-increment integer ID
- Description, category, location
- Price (GBP)
- Optional dimensions (height, width, depth in cm)
- Photo URL (stored externally — see Phase 3)

**Locations** and **Categories** are dynamic — created, edited, and deleted via the app. Deleting either is blocked if any items are currently assigned.

---

## Project status

### Phase 1 — Backend foundation (complete)
- Quarkus REST API with full CRUD for items, locations, and categories
- PostgreSQL schema via Flyway migration
- JPA entities, Panache repositories, CDI services
- Unit tests (Mockito + AssertJ)
- Postman collection

### Phase 2 — iOS app (complete)
- Native SwiftUI app, iOS 17+
- Brand-matched design (Seaglass sage, teal, and sand palette; Source Sans 3)
- Browse items by location, search/filter, view item detail
- Full CRUD: add, edit, and delete items and locations
- Swipe-to-delete and swipe-to-edit on list rows

### Phase 3 — GCP deployment (next)
- Dockerfile for Quarkus backend
- Deploy backend to **Cloud Run** (free tier)
- Provision **e2-micro VM** on GCP with self-managed PostgreSQL (always-free tier)
- Secret Manager for production credentials
- Photo upload endpoint (`POST /api/items/{id}/photo`) backed by Cloud Storage bucket
