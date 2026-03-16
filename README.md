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
| Auth | Spring Security + JWT (jjwt 0.12.6) |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Build (BE) | Maven |
| Build (FE) | Angular CLI 21 |

---

## Features

### Fleet Management
- **Dashboard** — stats cards (total / active / in-transit / maintenance), interactive fleet map with live vehicle markers, active trips panel with click-to-visualize (start marker, end marker, route polyline)
- **Vehicle Management** — full CRUD, status filtering (All / Active / In Transit / Maintenance / Inactive), optional GPS coordinates stored as PostGIS geometry points
- **Driver Management** — full CRUD, manager role distinction, contact phone, licence number
- **Route Management** — create/edit routes with active/inactive toggle, description, and ordered stops
- **Trip Management** — create/edit trips with vehicle, route, and driver assignment; one-click status transitions (SCHEDULED → IN_PROGRESS → COMPLETED / CANCELLED); filter by status

### Real-time Tracking
- Vehicle GPS location stored as PostGIS geometry (lat/lng via REST)
- Full location history and latest-position endpoints per vehicle

### Automation & Alerts
- **TripScheduler** — runs every 60 seconds; if a trip's end time has passed:
  - Vehicle within **20 m** of destination → trip is **auto-completed**
  - Vehicle still far → a **LATE alert** is created
- **Alerts page** — managers view all LATE alerts, toggle resolved/unresolved, and resolve them manually
- Drivers receive a dedicated **alerts notification** page when their trip is overdue

### Authentication & Authorization
- JWT-based authentication (no sessions); tokens carry `driverId` and `isManager` claims
- Secure password hashing with BCrypt
- **Login / Register** pages for both drivers and managers
- Role-based route guards: manager-only pages (Dashboard, Vehicles, Trips, Routes, Drivers, Alerts) vs driver pages (My Trips)
- HTTP interceptor automatically attaches the JWT to every request

### Driver Portal
- **My Trips** page — driver-specific view showing active trip, upcoming trips, completed trips, and computed stats

### API Documentation
- Swagger UI at `http://localhost:8080/swagger-ui.html`
- Raw OpenAPI spec at `http://localhost:8080/api-docs`

---

## Project Structure

```
FleetTracker/
├── database/
│   ├── schema.sql              # Table definitions (vehicles, drivers, routes, stops, trips, alerts, location_updates)
│   └── sample-data.sql         # Seed data for local development
├── backend/
│   ├── .env.example            # Environment variable template
│   └── src/main/java/com/example/backend/
│       ├── config/             # CORS, OpenAPI, Spring Security
│       ├── controller/         # VehicleController, TripController, RouteController, DriverController,
│       │                       #   AuthController, AlertController, LocationUpdateController, StopController
│       ├── service/            # Business logic (includes TripScheduler for auto-complete & alerts)
│       ├── repository/         # Spring Data JPA repositories
│       ├── model/              # JPA entities + enums (VehicleStatus, VehicleType, TripStatus, AlertType)
│       ├── dto/                # Request / Response DTOs
│       ├── security/           # JwtUtil, JwtAuthFilter
│       └── exception/          # GlobalExceptionHandler
├── frontend/
│   └── src/app/
│       ├── dashboard/          # Dashboard page (map + stats + active trips)
│       ├── vehicles/           # Vehicles CRUD page
│       ├── trips/              # Trips management page
│       ├── routes/             # Routes management page
│       ├── drivers/            # Drivers management page
│       ├── alerts/             # Alerts page (manager view)
│       ├── my-trips/           # My Trips page (driver view)
│       ├── login/              # Login page
│       ├── register/           # Register page
│       ├── layout/             # Sidebar + shell layout
│       ├── guards/             # authGuard, managerGuard
│       ├── interceptors/       # JWT auth interceptor
│       ├── models/             # TypeScript interfaces & enums
│       └── services/           # HTTP services (vehicle, trip, route, driver, alert, auth, location)
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
JWT_SECRET=your_jwt_secret_key
```

Then create the database and run the schema:

```bash
psql -U postgres -c "CREATE DATABASE db_fleet;"
psql -U postgres -d db_fleet -f database/schema.sql
```

Optionally seed sample data:

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
| spring-boot-starter-security | Spring Security filter chain |
| spring-boot-starter-websocket | WebSocket support (STOMP) |
| hibernate-spatial | PostGIS geometry column support |
| jjwt 0.12.6 | JWT generation and validation |
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
