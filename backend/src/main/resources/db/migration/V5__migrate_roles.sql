-- ============================================================
-- V5: Migrate roles — retroactive upgrade from old role names
-- (ADMIN, USER, USUARIO) to new roles
-- (ADMINISTRADOR, OPERARIO, VETERINARIO, ZOOTECNISTA).
--
-- Since roles weren't functionally used before, ALL existing
-- users become OPERARIO. A fresh admin account is created so
-- the owner can log in and assign roles manually.
--
-- Idempotent — safe to run on fresh databases, existing RDS
-- instances, and databases where this has already been run.
-- ============================================================

-- ============================================================
-- 1. Insert new roles (idempotent)
-- ============================================================
INSERT INTO public.rol (id, nombre) VALUES
    (10, 'ADMINISTRADOR'),
    (20, 'OPERARIO'),
    (30, 'ZOOTECNISTA')
ON CONFLICT (nombre) DO NOTHING;

-- Note: VETERINARIO already exists with id=3 from V3,
-- so we don't insert it again.

-- ============================================================
-- 2. Set ALL existing users to OPERARIO
--    Roles weren't functional before, so this is the safest
--    default. The admin can reassign roles from the UI.
-- ============================================================
UPDATE public.usuario u
SET id_rol = (SELECT id FROM public.rol WHERE nombre = 'OPERARIO')
WHERE u.id_rol IS NOT NULL
  AND u.id_rol != (SELECT id FROM public.rol WHERE nombre = 'OPERARIO');

-- ============================================================
-- 3. Create a fresh admin account
--    This is the only user that can log in and assign roles
--    to the others from the Administration page.
-- ============================================================
INSERT INTO public.usuario (id_rol, nombre, email, password)
SELECT
    r.id,
    'Administrador',
    'admin@sistema.com',
    '$2a$10$FYiAjPrsvVb3i2XMcGq3NuThC/diY8C0Av9SWkqxrZkC2LrrYlPZK' -- password: Admin123!
FROM public.rol r
WHERE r.nombre = 'ADMINISTRADOR'
  AND NOT EXISTS (
      SELECT 1 FROM public.usuario u2
      WHERE u2.id_rol = (SELECT id FROM public.rol WHERE nombre = 'ADMINISTRADOR')
  );

-- ============================================================
-- 4. Remove old roles that are no longer used
-- ============================================================
DELETE FROM public.rol WHERE nombre IN ('ADMIN', 'USER', 'USUARIO');

-- ============================================================
-- 5. Reset sequence to max id
-- ============================================================
SELECT setval('public.rol_id_seq', COALESCE((SELECT MAX(id) FROM public.rol), 0) + 1, false);
