-- ============================================================
-- V10: Consolidated Role & Admin Account Setup
--
-- THIS MIGRATION CONSOLIDATES the intent of V5–V7 into one
-- authoritative migration (role creation, user migration, admin
-- account upsert). V5–V7 were iterative fixes for the same
-- issue (flawed role-based admin check → email-based insert
-- → PostgreSQL upsert). This file is the clean, single version.
--
-- V8 and V9 were ad-hoc user role upgrades and have been
-- deleted — Flyway ignore-missing-migrations handles this.
--
-- Idempotent — safe to run on any environment, any number
-- of times.
-- ============================================================

-- ============================================================
-- 1. Insert new roles (idempotent)
--    VETERINARIO exists with id=3 from V3, so we skip it.
-- ============================================================
INSERT INTO public.rol (id, nombre) VALUES
    (10, 'ADMINISTRADOR'),
    (20, 'OPERARIO'),
    (30, 'ZOOTECNISTA')
ON CONFLICT (nombre) DO NOTHING;

-- ============================================================
-- 2. Set ALL existing users to OPERARIO
--    Roles weren't functional before, so OPERARIO is the
--    safest default. The admin can reassign from the UI.
-- ============================================================
UPDATE public.usuario u
SET id_rol = (SELECT id FROM public.rol WHERE nombre = 'OPERARIO')
WHERE u.id_rol IS NOT NULL
  AND u.id_rol != (SELECT id FROM public.rol WHERE nombre = 'OPERARIO');

-- ============================================================
-- 3. Create / ensure admin account with correct password
--    Uses PostgreSQL upsert (ON CONFLICT ... DO UPDATE) to
--    guarantee the account exists with the right hash,
--    regardless of the database state.
--
--    Email has a UNIQUE constraint (from V1), so
--    ON CONFLICT (email) uniquely identifies the row.
-- ============================================================
INSERT INTO public.usuario (id_rol, nombre, email, password)
SELECT
    r.id,
    'Administrador',
    'admin@sistema.com',
    '$2a$10$FYiAjPrsvVb3i2XMcGq3NuThC/diY8C0Av9SWkqxrZkC2LrrYlPZK' -- password: Admin123!
FROM public.rol r
WHERE r.nombre = 'ADMINISTRADOR'
ON CONFLICT (email) DO UPDATE SET
    password = '$2a$10$FYiAjPrsvVb3i2XMcGq3NuThC/diY8C0Av9SWkqxrZkC2LrrYlPZK',
    id_rol = (SELECT id FROM public.rol WHERE nombre = 'ADMINISTRADOR'),
    nombre = 'Administrador';

-- ============================================================
-- 4. Remove old roles that are no longer used
-- ============================================================
DELETE FROM public.rol WHERE nombre IN ('ADMIN', 'USER', 'USUARIO');

-- ============================================================
-- 5. Reset sequence to max id
-- ============================================================
SELECT setval('public.rol_id_seq', COALESCE((SELECT MAX(id) FROM public.rol), 0) + 1, false);
