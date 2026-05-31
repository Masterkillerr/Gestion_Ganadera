-- ============================================================
-- V6: Ensure admin account exists (email-based, not role-based)
--
-- V5 used a role-based NOT EXISTS check that could skip the
-- insert if any user had ADMINISTRADOR role — even if the
-- admin@sistema.com account was missing. This migration
-- checks specifically by email, which is always correct.
--
-- Also includes an UPDATE to ensure the password hash is
-- correct in case the user already exists with a bad hash.
--
-- Idempotent — safe to run on any environment.
-- ============================================================

-- Step 1: Insert the admin account if it doesn't exist
INSERT INTO public.usuario (id_rol, nombre, email, password)
SELECT
    r.id,
    'Administrador',
    'admin@sistema.com',
    '$2a$10$FYiAjPrsvVb3i2XMcGq3NuThC/diY8C0Av9SWkqxrZkC2LrrYlPZK' -- password: Admin123!
FROM public.rol r
WHERE r.nombre = 'ADMINISTRADOR'
  AND NOT EXISTS (
      SELECT 1 FROM public.usuario u
      WHERE u.email = 'admin@sistema.com'
  );

-- Step 2: Ensure password is correct even if user already exists
UPDATE public.usuario
SET password = '$2a$10$FYiAjPrsvVb3i2XMcGq3NuThC/diY8C0Av9SWkqxrZkC2LrrYlPZK' -- password: Admin123!
WHERE email = 'admin@sistema.com';
