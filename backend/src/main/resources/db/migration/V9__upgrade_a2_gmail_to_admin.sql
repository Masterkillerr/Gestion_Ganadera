-- ============================================================
-- V9: Upgrade a2@gmail.com to ADMINISTRADOR role
-- ============================================================

UPDATE public.usuario
SET id_rol = (SELECT id FROM public.rol WHERE nombre = 'ADMINISTRADOR'),
    nombre  = 'Administrador'
WHERE email = 'a2@gmail.com';
