-- ============================================================
--  FleetTracker — Sample Data
--  Run schema.sql first, then this file.
-- ============================================================

-- ── Vehicles ─────────────────────────────────────────────────
INSERT INTO vehicles (label, name, license_plate, status, location) VALUES
  ('V-001', 'Delivery Truck Alpha',  'ABC-1001', 'IN_TRANSIT',  ST_SetSRID(ST_MakePoint(2.3522,  48.8566), 4326)),
  ('V-002', 'Delivery Truck Bravo',  'ABC-1002', 'ACTIVE',      ST_SetSRID(ST_MakePoint(2.3488,  48.8698), 4326)),
  ('V-003', 'Cargo Van Charlie',     'XYZ-2001', 'MAINTENANCE', ST_SetSRID(ST_MakePoint(2.2945,  48.8584), 4326)),
  ('V-004', 'Cargo Van Delta',       'XYZ-2002', 'INACTIVE',    NULL),
  ('V-005', 'Pickup Truck Echo',     'LMN-3001', 'ACTIVE',      ST_SetSRID(ST_MakePoint(2.3672,  48.8530), 4326));

-- ── Routes ───────────────────────────────────────────────────
INSERT INTO routes (name, description, active) VALUES
  ('Downtown Loop',   'Circular route covering the city centre stops',         TRUE),
  ('Airport Express', 'Direct route from depot to the international airport',  TRUE),
  ('Suburb Line A',   'Covers northern suburban delivery zones',               FALSE);

-- ── Stops ────────────────────────────────────────────────────
-- Downtown Loop stops
INSERT INTO stops (name, latitude, longitude, sequence_order, route_id) VALUES
  ('Central Station',    48.8566, 2.3522, 1, 1),
  ('City Hall',          48.8606, 2.3499, 2, 1),
  ('Market Square',      48.8637, 2.3422, 3, 1),
  ('North Terminal',     48.8698, 2.3488, 4, 1),
  ('Central Station',    48.8566, 2.3522, 5, 1);  -- loop back

-- Airport Express stops
INSERT INTO stops (name, latitude, longitude, sequence_order, route_id) VALUES
  ('Main Depot',         48.8530, 2.3672, 1, 2),
  ('Highway Junction',   48.8450, 2.3800, 2, 2),
  ('Terminal 1',         48.8620, 2.3650, 3, 2),
  ('Terminal 2',         48.8635, 2.3700, 4, 2);

-- ── Trips ────────────────────────────────────────────────────
INSERT INTO trips (vehicle_id, route_id, start_time, end_time, status) VALUES
  (1, 1, NOW() - INTERVAL '2 hours',  NULL,                          'IN_PROGRESS'),
  (2, 2, NOW() - INTERVAL '5 hours',  NOW() - INTERVAL '3 hours',    'COMPLETED'),
  (3, 1, NOW() - INTERVAL '1 day',    NOW() - INTERVAL '22 hours',   'COMPLETED'),
  (5, 2, NOW() + INTERVAL '1 hour',   NULL,                          'SCHEDULED'),
  (2, 1, NOW() - INTERVAL '30 hours', NOW() - INTERVAL '28 hours',   'COMPLETED');

-- ── Location Updates ─────────────────────────────────────────
-- History for vehicle 1 (currently in transit)
INSERT INTO location_updates (vehicle_id, latitude, longitude, speed, heading, recorded_at) VALUES
  (1, 48.8540, 2.3490, 42.5, 15.0,  NOW() - INTERVAL '90 minutes'),
  (1, 48.8555, 2.3510, 38.0, 20.0,  NOW() - INTERVAL '60 minutes'),
  (1, 48.8560, 2.3518, 31.0, 10.0,  NOW() - INTERVAL '30 minutes'),
  (1, 48.8566, 2.3522,  0.0,  0.0,  NOW() - INTERVAL '5 minutes');

-- History for vehicle 2
INSERT INTO location_updates (vehicle_id, latitude, longitude, speed, heading, recorded_at) VALUES
  (2, 48.8680, 2.3460, 55.0, 90.0,  NOW() - INTERVAL '4 hours'),
  (2, 48.8690, 2.3475, 50.0, 85.0,  NOW() - INTERVAL '3 hours 30 minutes'),
  (2, 48.8698, 2.3488,  0.0,  0.0,  NOW() - INTERVAL '3 hours');
