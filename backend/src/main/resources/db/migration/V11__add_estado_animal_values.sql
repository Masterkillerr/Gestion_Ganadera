-- ============================================================
-- V11: Add missing estado_animal values for frontend dropdown
-- ============================================================
-- The frontend GanadoList dropdown offers: Sano, Gestante,
-- Lactancia, Seca, Vendido/Baja -- but some of these didn't exist
-- in the estado_animal catalog, causing the filter to return
-- zero results (no match).  This migration adds the missing
-- entries and migrates existing animals so backend filters
-- work with the frontend values.
-- ============================================================

-- 1. Add missing estados (idempotent)
INSERT INTO public.estado_animal (nombre) VALUES
    ('Sano'),
    ('Gestante'),
    ('Lactancia'),
    ('Seca'),
    ('Vendido/Baja')
ON CONFLICT (nombre) DO NOTHING;

-- 2. Migrate existing animals: 'Activo' → 'Sano'
UPDATE public.animal a
SET id_estado_animal = (SELECT id FROM public.estado_animal WHERE nombre = 'Sano')
WHERE a.id_estado_animal = (SELECT id FROM public.estado_animal WHERE nombre = 'Activo');

-- 3. Migrate existing animals: 'Vendido' → 'Vendido/Baja'
UPDATE public.animal a
SET id_estado_animal = (SELECT id FROM public.estado_animal WHERE nombre = 'Vendido/Baja')
WHERE a.id_estado_animal = (SELECT id FROM public.estado_animal WHERE nombre = 'Vendido');
