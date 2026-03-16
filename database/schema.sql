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
    id            BIGSERIAL    PRIMARY KEY,
    label         VARCHAR(255) NOT NULL,
    name          VARCHAR(255) NOT NULL,
    license_plate VARCHAR(255) NOT NULL UNIQUE,
    location      GEOMETRY(Point, 4326),
    status        VARCHAR(50)  NOT NULL DEFAULT 'INACTIVE',
    last_updated  TIMESTAMP
);

-- ── drivers ──────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS drivers (
    id             BIGSERIAL    PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    age            BIGINT       NOT NULL,
    is_manager     BOOLEAN      NOT NULL DEFAULT FALSE,
    licence_number VARCHAR(255) NOT NULL,
    contact_phone  VARCHAR(50)  NOT NULL,
    username       VARCHAR(100) NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL
);

-- Migration: if the table already exists, add the new columns:
-- ALTER TABLE drivers ADD COLUMN IF NOT EXISTS username      VARCHAR(100) NOT NULL DEFAULT '';
-- ALTER TABLE drivers ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255) NOT NULL DEFAULT '';
-- ALTER TABLE drivers ADD CONSTRAINT drivers_username_unique UNIQUE (username);

-- ── routes ───────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS routes (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP
);

-- ── stops ────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS stops (
    id             BIGSERIAL        PRIMARY KEY,
    name           VARCHAR(255)     NOT NULL,
    latitude       DOUBLE PRECISION NOT NULL,
    longitude      DOUBLE PRECISION NOT NULL,
    sequence_order INTEGER,
    route_id       BIGINT           NOT NULL REFERENCES routes(id) ON DELETE CASCADE,
    created_at     TIMESTAMP        NOT NULL DEFAULT NOW()
);

-- ── trips ────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS trips (
    id              BIGSERIAL        PRIMARY KEY,
    vehicle_id      BIGINT           NOT NULL REFERENCES vehicles(id),
    route_id        BIGINT           NOT NULL REFERENCES routes(id),
    driver_id       BIGINT           REFERENCES drivers(id),
    start_latitude  DOUBLE PRECISION,
    start_longitude DOUBLE PRECISION,
    end_latitude    DOUBLE PRECISION,
    end_longitude   DOUBLE PRECISION,
    start_time      TIMESTAMP,
    end_time        TIMESTAMP,
    status          VARCHAR(50)      NOT NULL DEFAULT 'SCHEDULED',
    created_at      TIMESTAMP        NOT NULL DEFAULT NOW()
);

-- ── alerts ───────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS alerts (
    id         BIGSERIAL    PRIMARY KEY,
    trip_id    BIGINT       NOT NULL REFERENCES trips(id) ON DELETE CASCADE,
    type       VARCHAR(50)  NOT NULL DEFAULT 'LATE',
    message    TEXT         NOT NULL,
    resolved   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ── location_updates ─────────────────────────────────────────
CREATE TABLE IF NOT EXISTS location_updates (
    id          BIGSERIAL        PRIMARY KEY,
    vehicle_id  BIGINT           NOT NULL REFERENCES vehicles(id),
    latitude    DOUBLE PRECISION NOT NULL,
    longitude   DOUBLE PRECISION NOT NULL,
    speed       DOUBLE PRECISION,
    heading     DOUBLE PRECISION,
    recorded_at TIMESTAMP        NOT NULL DEFAULT NOW()
);
