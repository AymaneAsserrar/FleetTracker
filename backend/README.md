# FleetTracker — Backend

Spring Boot REST API backend for the FleetTracker fleet management system. Provides full CRUD endpoints for vehicles, routes, stops, trips, and real-time location tracking, with a built-in Swagger UI for interactive API testing.

---

## Tech Stack

| Technology | Version |
|---|---|
| Java | 21 |
| Spring Boot | 3.5.11 |
| Build Tool | Maven |
| Database | PostgreSQL |
| ORM | Hibernate + Hibernate Spatial |
| API Docs | SpringDoc OpenAPI (Swagger UI) |

---

## Dependencies

| Dependency | Purpose |
|---|---|
| spring-boot-starter-web | REST API (controllers, HTTP) |
| spring-boot-starter-data-jpa | ORM & database persistence via Hibernate |
| spring-boot-starter-websocket | Real-time WebSocket communication |
| hibernate-spatial | PostGIS geometry column support |
| postgresql | PostgreSQL JDBC driver (runtime) |
| lombok | Reduces boilerplate (getters, setters, constructors) |
| spring-dotenv | Loads `.env` file as Spring properties |
| springdoc-openapi-starter-webmvc-ui | Swagger UI & OpenAPI 3 docs |
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
│   │   │   │   └── OpenApiConfig.java          # Swagger / OpenAPI metadata
│   │   │   ├── controller/
│   │   │   │   ├── VehicleController.java
│   │   │   │   ├── RouteController.java
│   │   │   │   ├── StopController.java
│   │   │   │   ├── TripController.java
│   │   │   │   └── LocationUpdateController.java
│   │   │   ├── dto/
│   │   │   │   ├── VehicleDTO.java
│   │   │   │   ├── RouteDTO.java
│   │   │   │   ├── StopDTO.java
│   │   │   │   ├── TripDTO.java
│   │   │   │   └── LocationUpdateDTO.java
│   │   │   ├── exception/
│   │   │   │   └── GlobalExceptionHandler.java # 404 / 400 / 500 responses
│   │   │   ├── model/
│   │   │   │   ├── enums/
│   │   │   │   │   ├── VehicleType.java        # BUS, TRUCK, VAN, CAR, MOTORCYCLE
│   │   │   │   │   ├── VehicleStatus.java      # ACTIVE, INACTIVE, MAINTENANCE, IN_TRANSIT
│   │   │   │   │   └── TripStatus.java         # SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
│   │   │   │   ├── Vehicle.java
│   │   │   │   ├── Route.java
│   │   │   │   ├── Stop.java
│   │   │   │   ├── Trip.java
│   │   │   │   └── LocationUpdate.java
│   │   │   ├── repository/
│   │   │   │   ├── VehicleRepository.java
│   │   │   │   ├── RouteRepository.java
│   │   │   │   ├── StopRepository.java
│   │   │   │   ├── TripRepository.java
│   │   │   │   └── LocationUpdateRepository.java
│   │   │   └── service/
│   │   │       ├── VehicleService.java
│   │   │       ├── RouteService.java
│   │   │       ├── StopService.java
│   │   │       ├── TripService.java
│   │   │       └── LocationUpdateService.java
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

Database credentials are loaded from a `.env` file in the `backend/` directory:

```env
DB_URL=jdbc:postgresql://localhost:5432/fleet_db
DB_USERNAME=postgres
DB_PASSWORD=your_password
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
- PostgreSQL running locally with a `fleet_db` database

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

### Vehicles — `/api/vehicles`

| Method | Path | Description |
|---|---|---|
| GET | `/api/vehicles` | List all vehicles (optional `?status=` filter) |
| GET | `/api/vehicles/{id}` | Get vehicle by ID |
| POST | `/api/vehicles` | Create a new vehicle |
| PUT | `/api/vehicles/{id}` | Update vehicle details |
| PUT | `/api/vehicles/{id}/location` | Update vehicle's current GPS position |
| DELETE | `/api/vehicles/{id}` | Delete a vehicle |

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
| POST | `/api/trips` | Create a new trip |
| PUT | `/api/trips/{id}` | Update trip details |
| PATCH | `/api/trips/{id}/status` | Update trip status only |
| DELETE | `/api/trips/{id}` | Delete a trip |

### Location Updates — `/api/location-updates`

| Method | Path | Description |
|---|---|---|
| GET | `/api/location-updates/vehicle/{vehicleId}` | Full location history (newest first) |
| GET | `/api/location-updates/vehicle/{vehicleId}/latest` | Latest position only |
| POST | `/api/location-updates` | Record a new location update |

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
