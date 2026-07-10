# DeliverX Frontend

Unified Angular workspace with two portal applications for the DeliverX delivery management platform.

## Applications

| App | Port | Audience | Purpose |
|-----|------|----------|---------|
| **client-portal** | 4200 | Customers | Track packages, view delivery status |
| **admin-portal** | 4201 | Administrators | Fleet, packages, deliveries, system health |

Both apps communicate with the backend through **Spring Cloud Gateway** at `http://localhost:8090`.

## Prerequisites

- Node.js 18+
- Backend stack running (Eureka, Config Server, microservices, Gateway)

## Install

```powershell
cd frontend
npm install
```

## Run

Start each portal in a separate terminal:

```powershell
# Client portal
npm run start:client

# Admin portal
npm run start:admin
```

- Client: http://localhost:4200
- Admin: http://localhost:4201

## Demo credentials (Keycloak)

Authentication is backed by **Keycloak** (realm `deliverx`): the login form performs a
password grant against Keycloak, and an HTTP interceptor attaches the JWT to every
request going through the Gateway. Route guards check the real client roles from the
token (`resource_access.driver-client-service.roles`).

| Portal | Username | Password | Role |
|--------|----------|----------|------|
| Admin | `admin1` | `admin123` | admin (full write access) |
| Client | `client1` | `client123` | user (read-only / self-service) |

Keycloak must be running (`docker compose up -d keycloak`) for login to work.

## Project structure

```
frontend/
├── projects/
│   ├── client-portal/     # Customer-facing app
│   ├── admin-portal/      # Operations console
│   └── shared/            # Models, API services, UI components
```

## Shared library

The `shared` library contains:

- API services (`VehicleApiService`, `PackageApiService`, `DeliveryApiService`, `HealthApiService`)
- TypeScript models aligned with backend DTOs
- Reusable UI components (`StatusBadge`, `LoadingState`)
- Keycloak session services (password grant + refresh) and role-based route guards

## Features by portal

### Client portal
- Home page with platform overview
- Package tracking by ID (packages 1, 2, 3 have demo data)
- Delivery preparation details via delivery service
- Client login (mock)

### Admin portal
- Microservice health dashboard
- Vehicle fleet CRUD (full integration with vehicle-service)
- Package list and detail views
- Delivery orchestration (OpenFeign demo)
- Placeholder pages for assignments and drivers/clients

## Build

```powershell
npm run build
```

Build output is written to `dist/client-portal` and `dist/admin-portal`.

## Backend gateway routes

| Frontend calls | Gateway path |
|----------------|--------------|
| Vehicles | `/vehicles/**` |
| Packages | `/packages/**` |
| Deliveries | `/deliveries/**` |
| Health checks | `/assignment/health`, `/drivers/health`, etc. |

Ensure the gateway has CORS enabled for ports 4200 and 4201 before running the frontend against a local backend.
