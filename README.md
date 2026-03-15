# FleetTracker

Transportation/Fintech: Automated Fare Collection (NFC/QR Simulator)

A full-stack application for managing fleet operations and automated fare collection using NFC/QR simulation technology.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.5.11 (Java 21) |
| Frontend | Angular 21.2.0 (Standalone Components) |
| Database | PostgreSQL |
| Styling | Tailwind CSS 4.1.12 |
| Build (BE) | Maven |
| Build (FE) | Angular CLI 21.2.2 |

---

## Project Structure

```
FleetTracker/
├── backend/                  # Spring Boot REST API
│   ├── src/main/java/com/example/backend/
│   │   └── BackendApplication.java
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
├── frontend/                 # Angular SPA
│   ├── src/app/
│   │   ├── app.ts
│   │   ├── app.html
│   │   ├── app.routes.ts
│   │   └── app.config.ts
│   ├── angular.json
│   └── package.json
└── README.md
```

---

## Backend

Built with **Spring Boot 3.5.11** on **Java 21**, using Maven as the build tool.

### Dependencies

| Package | Purpose |
|---------|---------|
| spring-boot-starter-web | REST API development |
| spring-boot-starter-data-jpa | ORM & database persistence |
| spring-boot-starter-websocket | Real-time WebSocket communication |
| postgresql | PostgreSQL JDBC driver |
| lombok | Boilerplate reduction (getters/setters/constructors) |
| spring-boot-starter-test | Unit testing (JUnit 5) |

### Configuration

- Application name: `backend`
- Config file: `src/main/resources/application.properties`
- Entry point: `BackendApplication.java` with `@SpringBootApplication`

### Running the Backend

```bash
./mvnw spring-boot:run
```

---

## Frontend

Built with **Angular 21.2.0** using standalone components (no NgModule), styled with **Tailwind CSS**.

### Dependencies

| Package | Version | Purpose |
|---------|---------|---------|
| @angular/core | ^21.2.0 | Core Angular framework |
| @angular/router | ^21.2.0 | Client-side routing |
| @angular/forms | ^21.2.0 | Reactive and template forms |
| @angular/common | ^21.2.0 | Common directives and pipes |
| rxjs | ~7.8.0 | Reactive programming |
| tailwindcss | ^4.1.12 | Utility-first CSS framework |

### Architecture

- **Components**: Standalone (no NgModule required)
- **State Management**: Angular Signals API
- **Routing**: Angular Router (`app.routes.ts`)
- **Styling**: Tailwind CSS via PostCSS, with component-level inline CSS
- **Code Formatting**: Prettier (100-char width, single quotes, Angular HTML parser)

### TypeScript Configuration

- Target: ES2022
- Strict mode: enabled
- Strict Angular template checking: enabled

### Development Commands

```bash
npm start        # Dev server at http://localhost:4200
npm run build    # Production build (output: dist/)
npm run watch    # Watch mode build
npm test         # Run tests with Vitest
```

### Testing

- Framework: **Vitest 4.0.8** with jsdom
- Test file: `src/app/app.spec.ts`

### Build Budgets (Production)

| Type | Warning | Error |
|------|---------|-------|
| Initial bundle | 500 kB | 1 MB |
| Component styles | 4 kB | 8 kB |

---

## Getting Started

### Prerequisites

- Java 21+
- Node.js 18+
- PostgreSQL running locally

### 1. Start the Backend

```bash
cd backend
./mvnw spring-boot:run
```

### 2. Start the Frontend

```bash
cd frontend
npm install
npm start
```

The app will be available at [http://localhost:4200](http://localhost:4200).

---

## License

See [LICENSE](LICENSE).
