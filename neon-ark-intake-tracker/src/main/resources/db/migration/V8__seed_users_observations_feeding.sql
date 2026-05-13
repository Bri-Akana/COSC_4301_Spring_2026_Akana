-- Seed users
INSERT INTO users (full_name, email, phone, role, created_at) VALUES
    ('Dr. Yara Okafor',    'yara.okafor@neonark.com',    '512-555-0101', 'ADMIN',      NOW()),
    ('Marcus Hale',        'marcus.hale@neonark.com',    '512-555-0102', 'STAFF',      NOW()),
    ('Priya Nair',         'priya.nair@neonark.com',     '512-555-0103', 'VET',        NOW()),
    ('Jordan Reyes',       'jordan.reyes@neonark.com',   '512-555-0104', 'RESEARCHER', NOW()),
    ('Ingrid Solberg',     'ingrid.solberg@neonark.com', '512-555-0105', 'STAFF',      NOW());

-- Seed observations (link to creatures by name lookup)
INSERT INTO observations (creature_id, author_id, notes, observed_at) VALUES
    ((SELECT id FROM creatures WHERE name = 'Nyx'       LIMIT 1),
     (SELECT id FROM users WHERE email = 'priya.nair@neonark.com'),
     'Reacted aggressively to overhead lighting. Recommend blackout curtains in containment bay.', NOW() - INTERVAL '5 days'),

    ((SELECT id FROM creatures WHERE name = 'Brimscale' LIMIT 1),
     (SELECT id FROM users WHERE email = 'marcus.hale@neonark.com'),
     'Heat output increased by 15% this week. Containment wall integrity check scheduled.', NOW() - INTERVAL '3 days'),

    ((SELECT id FROM creatures WHERE name = 'Aquarion'  LIMIT 1),
     (SELECT id FROM users WHERE email = 'jordan.reyes@neonark.com'),
     'Salinity levels in tank adjusted. Subject appears calmer post-adjustment.', NOW() - INTERVAL '2 days'),

    ((SELECT id FROM creatures WHERE name = 'Zephyra'   LIMIT 1),
     (SELECT id FROM users WHERE email = 'priya.nair@neonark.com'),
     'Electrical discharge reduced after anti-static lining installed. Recovery progressing well.', NOW() - INTERVAL '1 day'),

    ((SELECT id FROM creatures WHERE name = 'Nyx'       LIMIT 1),
     (SELECT id FROM users WHERE email = 'ingrid.solberg@neonark.com'),
     'Feeding accepted for first time in 48 hours. Positive development.', NOW() - INTERVAL '12 hours'),

    ((SELECT id FROM creatures WHERE name = 'Terradon'  LIMIT 1),
     (SELECT id FROM users WHERE email = 'yara.okafor@neonark.com'),
     'Seismic readings near enclosure remain elevated. Escalating to engineering team.', NOW() - INTERVAL '6 hours');

-- Seed feeding schedules
INSERT INTO feeding_schedules (creature_id, feed_time, food_type, notes) VALUES
    ((SELECT id FROM creatures WHERE name = 'Nyx'       LIMIT 1), '08:00', 'Raw protein blend',     'Serve in darkness'),
    ((SELECT id FROM creatures WHERE name = 'Nyx'       LIMIT 1), '20:00', 'Raw protein blend',     'Evening feeding only'),
    ((SELECT id FROM creatures WHERE name = 'Brimscale' LIMIT 1), '12:00', 'Volcanic mineral paste', 'Handle with heat-resistant gloves'),
    ((SELECT id FROM creatures WHERE name = 'Aquarion'  LIMIT 1), '08:00', 'Deep sea krill',         'Add to tank directly'),
    ((SELECT id FROM creatures WHERE name = 'Aquarion'  LIMIT 1), '18:00', 'Deep sea krill',         'Evening dose'),
    ((SELECT id FROM creatures WHERE name = 'Sylpha'    LIMIT 1), '07:00', 'Wind-dried insects',     NULL),
    ((SELECT id FROM creatures WHERE name = 'Cryon'     LIMIT 1), '12:00', 'Frozen moss blocks',     'Keep below freezing until served'),
    ((SELECT id FROM creatures WHERE name = 'Lumina'    LIMIT 1), '08:00', 'Nectar solution',        NULL),
    ((SELECT id FROM creatures WHERE name = 'Lumina'    LIMIT 1), '16:00', 'Nectar solution',        'Afternoon top-up'),
    ((SELECT id FROM creatures WHERE name = 'Zephyra'   LIMIT 1), '09:00', 'Storm-charged pellets',  'Use insulated tongs');
