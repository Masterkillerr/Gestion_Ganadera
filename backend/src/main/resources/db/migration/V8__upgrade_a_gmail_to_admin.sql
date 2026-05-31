-- ============================================================
-- V8: Upgrade a@gmail.com to ADMINISTRADOR role
-- ============================================================

UPDATE public.usuario
SET id_rol = (SELECT id FROM public.rol WHERE nombre = 'ADMINISTRADOR'),
    nombre  = 'Administrador'
WHERE email = 'a@gmail.com';
