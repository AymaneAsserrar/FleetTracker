# FleetTracker — Frontend

Angular frontend for the FleetTracker fleet management system. Displays vehicles, routes, trips, and real-time GPS location tracking on an interactive map.

---

## Tech Stack

| Technology | Version |
|---|---|
| Angular | 21 |
| TypeScript | 5.x |
| Leaflet | 1.9.4 (interactive maps) |
| Tailwind CSS | 4.x |
| RxJS | 7.8 |
| STOMP / SockJS | 7.3 / 1.6 (WebSocket) |

---

## Prerequisites

- Node.js 18+
- Angular CLI: `npm install -g @angular/cli`
- Backend running on `http://localhost:8080`

---

## Setup

```bash
cd frontend
npm install
```

---

## Running the Dev Server

```bash
npm start
# or
ng serve
```

Open `http://localhost:4200` in your browser. The app reloads automatically on file changes.

---

## Building for Production

```bash
ng build
```

Output is placed in `dist/`. The production build is optimized for performance.

---

## Running Tests

```bash
ng test
```

---

## Project Structure

```
frontend/
├── src/
│   ├── app/
│   │   ├── components/       # Reusable UI components
│   │   ├── services/         # HTTP & WebSocket services
│   │   ├── models/           # TypeScript interfaces / DTOs
│   │   ├── pages/            # Route-level page components
│   │   └── app.routes.ts     # Angular routing
│   ├── assets/
│   └── styles.css            # Global Tailwind styles
├── package.json
└── angular.json
```

---

## Backend API

The frontend communicates with the Spring Boot backend at `http://localhost:8080`.

| Feature | Endpoint |
|---|---|
| Vehicles | `/api/vehicles` |
| Routes | `/api/routes` |
| Stops | `/api/stops` |
| Trips | `/api/trips` |
| Location Updates | `/api/location-updates` |
| WebSocket | `ws://localhost:8080/ws` |

See the [backend README](../backend/README.md) for full API documentation.
