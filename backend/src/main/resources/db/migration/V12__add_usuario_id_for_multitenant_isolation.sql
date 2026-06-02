-- Multi-tenant isolation: Add usuario_id to key entities
-- This ensures each user can only access their own fincas and related data

-- Add usuario_id to finca (primary ownership table)
ALTER TABLE finca ADD COLUMN id_usuario INTEGER;
ALTER TABLE finca ADD CONSTRAINT fk_finca_usuario
  FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE;
CREATE INDEX idx_finca_usuario ON finca(id_usuario);

-- Backfill existing fincas with a default admin user (ID 1 if it exists)
-- In production, manually assign fincas to correct users
UPDATE finca SET id_usuario = 1 WHERE id_usuario IS NULL;

-- Make the column NOT NULL after backfill
ALTER TABLE finca ALTER COLUMN id_usuario SET NOT NULL;

-- Add usuario_id to key transaction tables for faster authorization checks
-- This denormalizes slightly but enables fast authorization without joins

-- Add to evento (events: treatments, breeding, health)
ALTER TABLE evento ADD COLUMN id_usuario INTEGER;
ALTER TABLE evento ADD CONSTRAINT fk_evento_usuario
  FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE;
CREATE INDEX idx_evento_usuario ON evento(id_usuario);
UPDATE evento SET id_usuario = 1 WHERE id_usuario IS NULL;
ALTER TABLE evento ALTER COLUMN id_usuario SET NOT NULL;

-- Add to tratamiento (veterinary treatments)
ALTER TABLE tratamiento ADD COLUMN id_usuario INTEGER;
ALTER TABLE tratamiento ADD CONSTRAINT fk_tratamiento_usuario
  FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE;
CREATE INDEX idx_tratamiento_usuario ON tratamiento(id_usuario);
UPDATE tratamiento SET id_usuario = 1 WHERE id_usuario IS NULL;
ALTER TABLE tratamiento ALTER COLUMN id_usuario SET NOT NULL;

-- Add to alimentacion (feeding records)
ALTER TABLE alimentacion ADD COLUMN id_usuario INTEGER;
ALTER TABLE alimentacion ADD CONSTRAINT fk_alimentacion_usuario
  FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE;
CREATE INDEX idx_alimentacion_usuario ON alimentacion(id_usuario);
UPDATE alimentacion SET id_usuario = 1 WHERE id_usuario IS NULL;
ALTER TABLE alimentacion ALTER COLUMN id_usuario SET NOT NULL;

-- Add to produccion (milk/production records)
ALTER TABLE produccion ADD COLUMN id_usuario INTEGER;
ALTER TABLE produccion ADD CONSTRAINT fk_produccion_usuario
  FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE;
CREATE INDEX idx_produccion_usuario ON produccion(id_usuario);
UPDATE produccion SET id_usuario = 1 WHERE id_usuario IS NULL;
ALTER TABLE produccion ALTER COLUMN id_usuario SET NOT NULL;

-- Add to movimiento (animal movements)
ALTER TABLE movimiento ADD COLUMN id_usuario INTEGER;
ALTER TABLE movimiento ADD CONSTRAINT fk_movimiento_usuario
  FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE;
CREATE INDEX idx_movimiento_usuario ON movimiento(id_usuario);
UPDATE movimiento SET id_usuario = 1 WHERE id_usuario IS NULL;
ALTER TABLE movimiento ALTER COLUMN id_usuario SET NOT NULL;

-- Note: lote, animal, reproduccion are accessed through finca hierarchy
-- They don't need direct usuario_id, but filters should use finca.id_usuario
