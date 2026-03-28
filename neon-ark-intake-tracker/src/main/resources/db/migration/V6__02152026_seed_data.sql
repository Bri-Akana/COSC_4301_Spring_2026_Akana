-- Seed habitats (same as yours)
INSERT INTO habitats (biome, location, min_temp_c, max_temp_c, created_at)
VALUES
    ('FOREST', 'Sector A — Moss Caverns', 8, 18, NOW()),
    ('DESERT', 'Sector B — Ember Dunes', 32, 52, NOW()),
    ('ARCTIC', 'Sector C — Frost Hollow', -40, -10, NOW()),
    ('OCEAN', 'Sector D — Abyssal Containment', 2, 6, NOW()),
    ('MOUNTAIN', 'Sector E — Highwind Peaks', -5, 10, NOW()),
    ('SWAMP', 'Sector F — Mire Basin', 18, 30, NOW()),
    ('VOLCANIC', 'Sector G — Magma Core', 45, 85, NOW()),
    ('PLAINS', 'Sector H — Open Range', 15, 28, NOW()),
    ('JUNGLE', 'Sector I — Canopy Vault', 24, 36, NOW()),
    ('TUNDRA', 'Sector J — Permafrost Zone', -30, -5, NOW());

-- Seed creatures using habitat lookup by location (no hardcoded ids)
INSERT INTO creatures (name, species, danger_level, condition, notes, habitat_id, created_at)
VALUES
    ('Nyx', 'Void Fox', 'HIGH', 'QUARANTINED', 'Avoid bright light',
     (SELECT id FROM habitats WHERE location = 'Sector A — Moss Caverns' ORDER BY id LIMIT 1), NOW()),

    ('Aetheris', 'Sky Serpent', 'MEDIUM', 'STABLE', 'Prefers high altitude habitats',
     (SELECT id FROM habitats WHERE location = 'Sector B — Ember Dunes' ORDER BY id LIMIT 1), NOW()),

    ('Brimscale', 'Magma Drake', 'EXTREME', 'AGGRESSIVE', 'Emits intense heat when threatened',
     (SELECT id FROM habitats WHERE location = 'Sector G — Magma Core' ORDER BY id LIMIT 1), NOW()),

    ('Sylpha', 'Wind Sprite', 'LOW', 'STABLE', 'Very fast and difficult to observe',
     (SELECT id FROM habitats WHERE location = 'Sector E — Highwind Peaks' ORDER BY id LIMIT 1), NOW()),

    ('Umbros', 'Shadow Stalker', 'HIGH', 'QUARANTINED', 'Active primarily at night',
     (SELECT id FROM habitats WHERE location = 'Sector A — Moss Caverns' ORDER BY id LIMIT 1), NOW()),

    ('Cryon', 'Frost Golem', 'MEDIUM', 'STABLE', 'Requires subzero temperatures',
     (SELECT id FROM habitats WHERE location = 'Sector C — Frost Hollow' ORDER BY id LIMIT 1), NOW()),

    ('Verdantis', 'Forest Guardian', 'LOW', 'RECOVERING', 'Responds well to plant-based nutrients',
     (SELECT id FROM habitats WHERE location = 'Sector I — Canopy Vault' ORDER BY id LIMIT 1), NOW()),

    ('Pyrelis', 'Flame Wyrm', 'HIGH', 'AGGRESSIVE', 'Fire resistant containment required',
     (SELECT id FROM habitats WHERE location = 'Sector G — Magma Core' ORDER BY id LIMIT 1), NOW()),

    ('Aquarion', 'Deep Leviathan', 'EXTREME', 'STABLE', 'Must remain fully submerged',
     (SELECT id FROM habitats WHERE location = 'Sector D — Abyssal Containment' ORDER BY id LIMIT 1), NOW()),

    ('Zephyra', 'Storm Phoenix', 'HIGH', 'RECOVERING', 'Electrical discharge detected',
     (SELECT id FROM habitats WHERE location = 'Sector E — Highwind Peaks' ORDER BY id LIMIT 1), NOW()),

    ('Nocturne', 'Moon Panther', 'MEDIUM', 'STABLE', 'Enhanced vision in darkness',
     (SELECT id FROM habitats WHERE location = 'Sector A — Moss Caverns' ORDER BY id LIMIT 1), NOW()),

    ('Glacielle', 'Ice Seraph', 'LOW', 'STABLE', 'Fragile wing structure',
     (SELECT id FROM habitats WHERE location = 'Sector C — Frost Hollow' ORDER BY id LIMIT 1), NOW()),

    ('Terradon', 'Earth Titan', 'EXTREME', 'QUARANTINED', 'Seismic activity observed nearby',
     (SELECT id FROM habitats WHERE location = 'Sector F — Mire Basin' ORDER BY id LIMIT 1), NOW()),

    ('Lumina', 'Radiant Moth', 'LOW', 'STABLE', 'Emits constant soft light',
     (SELECT id FROM habitats WHERE location = 'Sector H — Open Range' ORDER BY id LIMIT 1), NOW()),

    ('Cinderclaw', 'Ash Beast', 'HIGH', 'AGGRESSIVE', 'Leaves scorched ground',
     (SELECT id FROM habitats WHERE location = 'Sector G — Magma Core' ORDER BY id LIMIT 1), NOW()),

    ('Nebulon', 'Cosmic Jelly', 'MEDIUM', 'STABLE', 'Unknown biological composition',
     (SELECT id FROM habitats WHERE location = 'Sector D — Abyssal Containment' ORDER BY id LIMIT 1), NOW()),

    ('Thalassa', 'Ocean Siren', 'MEDIUM', 'RECOVERING', 'Vocalizations influence nearby creatures',
     (SELECT id FROM habitats WHERE location = 'Sector D — Abyssal Containment' ORDER BY id LIMIT 1), NOW()),

    ('Ironhide', 'Steel Colossus', 'EXTREME', 'STABLE', 'Extremely dense exterior',
     (SELECT id FROM habitats WHERE location = 'Sector E — Highwind Peaks' ORDER BY id LIMIT 1), NOW()),

    ('Phantomis', 'Spectral Wolf', 'HIGH', 'QUARANTINED', 'Partially intangible',
     (SELECT id FROM habitats WHERE location = 'Sector A — Moss Caverns' ORDER BY id LIMIT 1), NOW()),

    ('Aurorix', 'Aurora Dragon', 'EXTREME', 'STABLE', 'Body emits shifting colors',
     (SELECT id FROM habitats WHERE location = 'Sector J — Permafrost Zone' ORDER BY id LIMIT 1), NOW()),

    ('Sporeling', 'Fungal Beast', 'LOW', 'STABLE', 'Releases spores when threatened',
     (SELECT id FROM habitats WHERE location = 'Sector F — Mire Basin' ORDER BY id LIMIT 1), NOW()),

    ('Voltaris', 'Thunder Roc', 'HIGH', 'AGGRESSIVE', 'Produces lightning strikes',
     (SELECT id FROM habitats WHERE location = 'Sector E — Highwind Peaks' ORDER BY id LIMIT 1), NOW()),

    ('Obsidian', 'Crystal Golem', 'MEDIUM', 'STABLE', 'Reflective armor plating',
     (SELECT id FROM habitats WHERE location = 'Sector G — Magma Core' ORDER BY id LIMIT 1), NOW()),

    ('Mistveil', 'Fog Serpent', 'MEDIUM', 'RECOVERING', 'Blends into mist easily',
     (SELECT id FROM habitats WHERE location = 'Sector F — Mire Basin' ORDER BY id LIMIT 1), NOW()),

    ('Solaris', 'Sun Lion', 'EXTREME', 'STABLE', 'Radiates intense solar energy',
     (SELECT id FROM habitats WHERE location = 'Sector B — Ember Dunes' ORDER BY id LIMIT 1), NOW());
