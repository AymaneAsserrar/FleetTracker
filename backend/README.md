# FleetTracker — Backend

Spring Boot REST API backend for the FleetTracker Automated Fare Collection system.

---

## Tech Stack

| Technology | Version |
|-----------|---------|
| Java | 21 |
| Spring Boot | 3.5.11 |
| Build Tool | Maven |
| Database | PostgreSQL |

---

## Dependencies

| Dependency | Purpose |
|-----------|---------|
| spring-boot-starter-web | REST API (controllers, HTTP) |
| spring-boot-starter-data-jpa | ORM & database persistence via Hibernate |
| spring-boot-starter-websocket | Real-time WebSocket communication |
| postgresql | PostgreSQL JDBC driver (runtime) |
| lombok | Reduces boilerplate (getters, setters, constructors) |
| spring-boot-starter-test | Unit & integration testing (JUnit 5) |

---

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/example/backend/
│   │   │   └── BackendApplication.java     # App entry point
│   │   └── resources/
│   │       └── application.properties      # App configuration
│   └── test/
│       └── java/com/example/backend/
│           └── BackendApplicationTests.java
├── pom.xml                                 # Maven build config
├── mvnw / mvnw.cmd                         # Maven wrapper scripts
└── README.md
```

---

## Configuration

Edit `src/main/resources/application.properties` to set up your database connection:

```properties
spring.application.name=backend

# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/fleettracker
spring.datasource.username=your_username
spring.datasource.password=your_password

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## Prerequisites

- Java 21+
- Maven 3.x (or use the included `mvnw` wrapper)
- PostgreSQL running locally

---

## Running the Application

```bash
# Using Maven wrapper (recommended)
./mvnw spring-boot:run

# Or on Windows
mvnw.cmd spring-boot:run
```

The server starts on **http://localhost:8080** by default.

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
