-- ============================================================
-- V7: Guarantee admin account exists using PostgreSQL upsert
--
-- V5 used a role-based NOT EXISTS check that could skip the
-- insert if any user had ADMINISTRADOR role.
-- V6 fixed the check to be email-based, but added an UPDATE
-- that might have had edge cases on some environments.
--
-- This migration uses PostgreSQL's ON CONFLICT ... DO UPDATE
-- (upsert) which GUARANTEES the account exists with the
-- correct password hash, regardless of the DB state.
--
-- The email column has a UNIQUE constraint (from V1), so
-- ON CONFLICT (email) uniquely identifies the row.
--
-- Idempotent — safe to run on any environment, any number
-- of times.
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
