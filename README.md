# FleetTracker

A full-stack **fleet management system** for tracking vehicles, trips, and routes in real time. Built with a Spring Boot REST API and an Angular single-page application.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.5 (Java 21) |
| Frontend | Angular 21 (Standalone Components + Signals) |
| Database | PostgreSQL + PostGIS |
| Map | Leaflet.js |
| Styling | Tailwind CSS 4 |
| Real-time | Spring WebSocket (STOMP) |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Build (BE) | Maven |
| Build (FE) | Angular CLI 21 |

---

## Features

- **Dashboard** — stats cards (total / active / in-transit / maintenance), interactive fleet map with live vehicle markers, active trips panel with click-to-locate, recent trips table
- **Fleet Map** — Leaflet map with color-coded markers per vehicle status; clicking an active trip flies the map to that vehicle and opens its popup
- **Vehicle Management** — full CRUD, status filtering (All / Active / In Transit / Maintenance / Inactive), optional GPS coordinates
- **Trip Management** — create/edit trips with vehicle & route selection, one-click status transitions (Start → In Progress → Complete / Cancel)
- **Route Management** — create/edit routes with active/inactive toggle and description
- **Geospatial Storage** — vehicle positions stored as PostGIS geometry points, exposed as lat/lng via REST
- **API Documentation** — Swagger UI at `http://localhost:8080/swagger-ui.html`

---

## Project Structure

```
FleetTracker/
├── database/
│   ├── schema.sql          # Table definitions (vehicles, routes, stops, trips, location_updates)
│   └── sample-data.sql     # Seed data for local development
├── backend/
│   ├── .env.example        # Environment variable template
│   └── src/main/java/com/example/backend/
│       ├── config/          # CORS, OpenAPI
│       ├── controller/      # VehicleController, TripController, RouteController ...
│       ├── service/         # Business logic
│       ├── repository/      # Spring Data JPA
│       ├── model/           # JPA entities + enums
│       └── dto/             # Request / Response DTOs
├── frontend/
│   └── src/app/
│       ├── dashboard/       # Dashboard page
│       ├── vehicles/        # Vehicles page
│       ├── trips/           # Trips page
│       ├── routes/          # Routes page
│       ├── layout/          # Sidebar + shell layout
│       ├── models/          # TypeScript interfaces & enums
│       └── services/        # HTTP services (vehicle, trip, route, location)
└── README.md
```

---

## Getting Started

### Prerequisites

- Java 21+
- Node.js 18+
- PostgreSQL with PostGIS extension

### 1. Configure the database

Copy the example env file and fill in your credentials:

```bash
cp backend/.env.example backend/.env
```

```env
DB_URL=jdbc:postgresql://localhost:5432/db_fleet
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
```

Then create the database and run the schema:

```bash
psql -U postgres -c "CREATE DATABASE db_fleet;"
psql -U postgres -d db_fleet -f database/schema.sql
```

Optionally seed sample data (5 vehicles, 3 routes, 5 trips, location history):

```bash
psql -U postgres -d db_fleet -f database/sample-data.sql
```

### 2. Start the backend

```bash
cd backend
./mvnw spring-boot:run
```

Backend runs at `http://localhost:8080`.
Swagger UI: `http://localhost:8080/swagger-ui.html`

> On Windows use `mvnw.cmd spring-boot:run`

### 3. Start the frontend

```bash
cd frontend
npm install
npm start
```

App runs at `http://localhost:4200`.

---

## Backend Overview

| Package | Purpose |
|---------|---------|
| spring-boot-starter-web | REST controllers |
| spring-boot-starter-data-jpa | ORM & persistence |
| spring-boot-starter-websocket | Real-time STOMP messaging |
| hibernate-spatial | PostGIS geometry column support |
| postgresql | JDBC driver |
| lombok | Boilerplate reduction |
| springdoc-openapi | Swagger UI & OpenAPI 3 spec |

## Frontend Overview

| Package | Purpose |
|---------|---------|
| @angular/core 21 | Standalone components, Signals API |
| @angular/router | Lazy-loaded client-side routing |
| @angular/forms | Template-driven forms |
| leaflet | Interactive maps |
| @stomp/stompjs + sockjs-client | WebSocket real-time layer |
| rxjs | Reactive HTTP with forkJoin |
| tailwindcss 4 | Utility-first styling |

### Development commands

```bash
npm start        # Dev server → http://localhost:4200
npm run build    # Production build (output: dist/)
npm test         # Run tests with Vitest
```

---

## License

See [LICENSE](LICENSE).
