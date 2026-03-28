-- Habitats biome constraint (update allowed list to match your seed data)
ALTER TABLE habitats
DROP CONSTRAINT IF EXISTS chk_habitats_biome;

ALTER TABLE habitats
ADD CONSTRAINT chk_habitats_biome
CHECK (biome IN (
    'FOREST','DESERT','OCEAN','ARCTIC','MOUNTAIN','SWAMP','VOLCANIC','PLAINS','JUNGLE','TUNDRA'
));

-- Creatures danger_level constraint (include EXTREME)
ALTER TABLE creatures
DROP CONSTRAINT IF EXISTS chk_creatures_danger_level;

ALTER TABLE creatures
ADD CONSTRAINT chk_creatures_danger_level
CHECK (danger_level IN ('LOW','MEDIUM','HIGH','EXTREME'));

-- Creatures condition constraint (include AGGRESSIVE + RECOVERING)
ALTER TABLE creatures
DROP CONSTRAINT IF EXISTS chk_creatures_condition;

ALTER TABLE creatures
ADD CONSTRAINT chk_creatures_condition
CHECK (condition IN ('STABLE','QUARANTINED','CRITICAL','RECOVERING','AGGRESSIVE'));
