-- ============================================================
--  FleetTracker — Database Schema
--  Run this once to set up the database from scratch.
--  Hibernate (ddl-auto=update) will also auto-create/update
--  tables on startup, so this is mainly for reference or
--  manual provisioning.
-- ============================================================

-- 1. Create the database (run as superuser outside this script)
--    CREATE DATABASE db_fleet;
--    \c db_fleet

-- 2. Enable PostGIS extension
CREATE EXTENSION IF NOT EXISTS postgis;

-- ── vehicles ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS vehicles (
    id            BIGSERIAL PRIMARY KEY,
    label         VARCHAR(255)        NOT NULL,
    name          VARCHAR(255)        NOT NULL,
    license_plate VARCHAR(255)        NOT NULL UNIQUE,
    location      GEOMETRY(Point, 4326),
    status        VARCHAR(50)         NOT NULL DEFAULT 'INACTIVE',
    last_updated  TIMESTAMP
);

-- ── routes ───────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS routes (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP
);

-- ── stops ────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS stops (
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(255)   NOT NULL,
    latitude       DOUBLE PRECISION NOT NULL,
    longitude      DOUBLE PRECISION NOT NULL,
    sequence_order INTEGER,
    route_id       BIGINT         NOT NULL REFERENCES routes(id) ON DELETE CASCADE,
    created_at     TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- ── trips ────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS trips (
    id         BIGSERIAL PRIMARY KEY,
    vehicle_id BIGINT      NOT NULL REFERENCES vehicles(id),
    route_id   BIGINT      NOT NULL REFERENCES routes(id),
    start_time TIMESTAMP,
    end_time   TIMESTAMP,
    status     VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    created_at TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ── location_updates ─────────────────────────────────────────
CREATE TABLE IF NOT EXISTS location_updates (
    id          BIGSERIAL PRIMARY KEY,
    vehicle_id  BIGINT           NOT NULL REFERENCES vehicles(id),
    latitude    DOUBLE PRECISION NOT NULL,
    longitude   DOUBLE PRECISION NOT NULL,
    speed       DOUBLE PRECISION,
    heading     DOUBLE PRECISION,
    recorded_at TIMESTAMP        NOT NULL DEFAULT NOW()
);
