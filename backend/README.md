# FleetTracker — Backend

Spring Boot REST API backend for the FleetTracker fleet management system. Provides full CRUD endpoints for vehicles, drivers, routes, stops, trips, alerts, and real-time location tracking, with JWT-based authentication and a built-in Swagger UI.

---

## Tech Stack

| Technology | Version |
|---|---|
| Java | 21 |
| Spring Boot | 3.5.11 |
| Build Tool | Maven |
| Database | PostgreSQL + PostGIS |
| ORM | Hibernate + Hibernate Spatial |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| API Docs | SpringDoc OpenAPI (Swagger UI) |

---

## Dependencies

| Dependency | Purpose |
|---|---|
| spring-boot-starter-web | REST API (controllers, HTTP) |
| spring-boot-starter-data-jpa | ORM & database persistence via Hibernate |
| spring-boot-starter-security | Spring Security filter chain & CORS |
| spring-boot-starter-websocket | Real-time WebSocket communication |
| hibernate-spatial | PostGIS geometry column support |
| jjwt 0.12.6 | JWT token generation and validation |
| postgresql | PostgreSQL JDBC driver (runtime) |
| lombok | Reduces boilerplate (getters, setters, constructors) |
| spring-dotenv | Loads `.env` file as Spring properties |
| springdoc-openapi-starter-webmvc-ui 2.8.16 | Swagger UI & OpenAPI 3 docs |
| spring-boot-starter-test | Unit & integration testing (JUnit 5) |

---

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/example/backend/
│   │   │   ├── BackendApplication.java
│   │   │   ├── config/
│   │   │   │   ├── OpenApiConfig.java          # Swagger / OpenAPI metadata
│   │   │   │   ├── SecurityConfig.java         # Spring Security, JWT filter, CORS
│   │   │   │   └── CorsConfig.java             # CORS for localhost:4200
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java         # /api/auth — login, register, current user
│   │   │   │   ├── VehicleController.java
│   │   │   │   ├── DriverController.java
│   │   │   │   ├── RouteController.java
│   │   │   │   ├── StopController.java
│   │   │   │   ├── TripController.java
│   │   │   │   ├── AlertController.java        # /api/alerts — LATE trip alerts
│   │   │   │   └── LocationUpdateController.java
│   │   │   ├── dto/
│   │   │   │   ├── AuthDTO.java
│   │   │   │   ├── VehicleDTO.java
│   │   │   │   ├── DriverDTO.java
│   │   │   │   ├── RouteDTO.java
│   │   │   │   ├── StopDTO.java
│   │   │   │   ├── TripDTO.java
│   │   │   │   ├── AlertDTO.java
│   │   │   │   └── LocationUpdateDTO.java
│   │   │   ├── exception/
│   │   │   │   └── GlobalExceptionHandler.java # 404 / 400 / 500 responses
│   │   │   ├── model/
│   │   │   │   ├── enums/
│   │   │   │   │   ├── VehicleType.java        # BUS, TRUCK, VAN, CAR, MOTORCYCLE
│   │   │   │   │   ├── VehicleStatus.java      # ACTIVE, INACTIVE, MAINTENANCE, IN_TRANSIT
│   │   │   │   │   ├── TripStatus.java         # SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
│   │   │   │   │   └── AlertType.java          # LATE
│   │   │   │   ├── Vehicle.java
│   │   │   │   ├── Driver.java
│   │   │   │   ├── Route.java
│   │   │   │   ├── Stop.java
│   │   │   │   ├── Trip.java
│   │   │   │   ├── Alert.java
│   │   │   │   └── LocationUpdate.java
│   │   │   ├── repository/
│   │   │   │   ├── VehicleRepository.java
│   │   │   │   ├── DriverRepository.java
│   │   │   │   ├── RouteRepository.java
│   │   │   │   ├── StopRepository.java
│   │   │   │   ├── TripRepository.java
│   │   │   │   ├── AlertRepository.java
│   │   │   │   └── LocationUpdateRepository.java
│   │   │   ├── security/
│   │   │   │   ├── JwtUtil.java               # JWT generation & parsing (driverId, isManager claims)
│   │   │   │   └── JwtAuthFilter.java         # Extracts & validates JWT from Authorization header
│   │   │   └── service/
│   │   │       ├── AuthService.java           # Registration, login, token generation
│   │   │       ├── VehicleService.java
│   │   │       ├── DriverService.java
│   │   │       ├── RouteService.java
│   │   │       ├── StopService.java
│   │   │       ├── TripService.java
│   │   │       ├── AlertService.java          # Alert creation and resolution
│   │   │       ├── LocationUpdateService.java
│   │   │       └── TripScheduler.java         # @Scheduled — auto-complete & LATE alerts
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/example/backend/
│           └── BackendApplicationTests.java
├── .env                                        # Local DB credentials (not committed)
├── pom.xml
├── mvnw / mvnw.cmd
└── README.md
```

---

## Configuration

Credentials and secrets are loaded from a `.env` file in the `backend/` directory:

```env
DB_URL=jdbc:postgresql://localhost:5432/db_fleet
DB_USERNAME=postgres
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret_key
```

Core settings in `src/main/resources/application.properties`:

```properties
spring.jpa.hibernate.ddl-auto=update   # auto-creates/updates tables on startup
spring.jpa.show-sql=true
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/api-docs
```

---

## Prerequisites

- Java 21+
- Maven 3.x (or use the included `mvnw` wrapper)
- PostgreSQL running locally with a `db_fleet` database and PostGIS extension enabled

---

## Running the Application

```bash
# Using Maven wrapper (recommended)
./mvnw spring-boot:run

# On Windows
mvnw.cmd spring-boot:run
```

The server starts on **http://localhost:8080** by default.

---

## Swagger UI

Once the application is running, open the interactive API docs in your browser:

```
http://localhost:8080/swagger-ui.html
```

The raw OpenAPI JSON spec is available at:

```
http://localhost:8080/api-docs
```

---

## API Endpoints

### Authentication — `/api/auth`

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register a new driver or manager |
| POST | `/api/auth/login` | Public | Login and receive a JWT token |
| GET | `/api/auth/me` | JWT | Get current authenticated user info |

### Vehicles — `/api/vehicles`

| Method | Path | Description |
|---|---|---|
| GET | `/api/vehicles` | List all vehicles (optional `?status=` filter) |
| GET | `/api/vehicles/{id}` | Get vehicle by ID |
| POST | `/api/vehicles` | Create a new vehicle |
| PUT | `/api/vehicles/{id}` | Update vehicle details |
| PUT | `/api/vehicles/{id}/location` | Update vehicle's current GPS position |
| DELETE | `/api/vehicles/{id}` | Delete a vehicle |

### Drivers — `/api/drivers`

| Method | Path | Description |
|---|---|---|
| GET | `/api/drivers` | List all drivers |
| GET | `/api/drivers/{id}` | Get driver by ID |
| POST | `/api/drivers` | Create a new driver |
| PUT | `/api/drivers/{id}` | Update driver details |
| DELETE | `/api/drivers/{id}` | Delete a driver |

### Routes — `/api/routes`

| Method | Path | Description |
|---|---|---|
| GET | `/api/routes` | List all routes (optional `?activeOnly=true`) |
| GET | `/api/routes/{id}` | Get route by ID |
| GET | `/api/routes/{id}/stops` | Get stops for a route (ordered by sequence) |
| POST | `/api/routes` | Create a new route |
| PUT | `/api/routes/{id}` | Update route details |
| DELETE | `/api/routes/{id}` | Delete a route |

### Stops — `/api/stops`

| Method | Path | Description |
|---|---|---|
| GET | `/api/stops` | List all stops |
| GET | `/api/stops/{id}` | Get stop by ID |
| POST | `/api/stops` | Create a new stop |
| PUT | `/api/stops/{id}` | Update stop details |
| DELETE | `/api/stops/{id}` | Delete a stop |

### Trips — `/api/trips`

| Method | Path | Description |
|---|---|---|
| GET | `/api/trips` | List all trips (optional `?status=` filter) |
| GET | `/api/trips/{id}` | Get trip by ID |
| GET | `/api/trips/vehicle/{vehicleId}` | Get all trips for a vehicle |
| GET | `/api/trips/driver/{driverId}` | Get all trips assigned to a driver |
| POST | `/api/trips` | Create a new trip |
| PUT | `/api/trips/{id}` | Update trip details |
| PATCH | `/api/trips/{id}/status?status=` | Update trip status only |
| DELETE | `/api/trips/{id}` | Delete a trip |

### Alerts — `/api/alerts`

| Method | Path | Description |
|---|---|---|
| GET | `/api/alerts` | List all alerts (optional `?resolved=` filter) |
| GET | `/api/alerts/{id}` | Get alert by ID |
| PATCH | `/api/alerts/{id}/resolve` | Mark an alert as resolved |
| DELETE | `/api/alerts/{id}` | Delete an alert |

### Location Updates — `/api/location-updates`

| Method | Path | Description |
|---|---|---|
| GET | `/api/location-updates/vehicle/{vehicleId}` | Full location history (newest first) |
| GET | `/api/location-updates/vehicle/{vehicleId}/latest` | Latest position only |
| POST | `/api/location-updates` | Record a new location update |

---

## Scheduler

`TripScheduler` runs every **60 seconds** and checks all `IN_PROGRESS` trips whose `end_time` has passed:

- If the assigned vehicle is within **20 meters** of the trip's destination (Haversine formula): trip status → `COMPLETED`, vehicle status → `ACTIVE`
- Otherwise: a `LATE` alert is created for that trip (if one doesn't exist already)

---

## Security

- All endpoints require a valid JWT in the `Authorization: Bearer <token>` header, except `/api/auth/login` and `/api/auth/register`
- JWT claims include `driverId` and `isManager` — manager-only operations are enforced in the service layer
- Passwords are hashed with BCrypt before storage

---

## Building

```bash
# Package as JAR
./mvnw clean package

# Run the JAR
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

---

## Running Tests

```bash
./mvnw test
```

---

## Package

- **Group ID**: `com.example`
- **Artifact ID**: `backend`
- **Version**: `0.0.1-SNAPSHOT`
