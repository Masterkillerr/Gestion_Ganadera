-- ============================================================
-- V3: Seed data for all catalog/reference tables
-- Idempotent — safe to run on both fresh databases and
-- existing RDS instances that may already have some rows.
-- ============================================================

-- ============================================================
-- CATÁLOGOS / TABLAS DE REFERENCIA (9)
-- ============================================================

INSERT INTO public.sexo (id, nombre) VALUES
    (1, 'Macho'),
    (2, 'Hembra')
ON CONFLICT DO NOTHING;
SELECT setval('public.sexo_id_seq', COALESCE((SELECT MAX(id) FROM public.sexo), 0) + 1, false);

INSERT INTO public.estado_animal (id, nombre) VALUES
    (1, 'Activo'),
    (2, 'Vendido'),
    (3, 'Muerto'),
    (4, 'Enfermo'),
    (5, 'En Tratamiento')
ON CONFLICT DO NOTHING;
SELECT setval('public.estado_animal_id_seq', COALESCE((SELECT MAX(id) FROM public.estado_animal), 0) + 1, false);

INSERT INTO public.condicion_nacimiento (id, nombre) VALUES
    (1, 'Normal'),
    (2, 'Distocia'),
    (3, 'Gemelar'),
    (4, 'Prematuro')
ON CONFLICT DO NOTHING;
SELECT setval('public.condicion_nacimiento_id_seq', COALESCE((SELECT MAX(id) FROM public.condicion_nacimiento), 0) + 1, false);

INSERT INTO public.tipo_evento (id, nombre) VALUES
    (1, 'Vacunación'),
    (2, 'Tratamiento'),
    (3, 'Parto'),
    (4, 'Movimiento'),
    (5, 'Reproducción'),
    (6, 'Alimentación'),
    (7, 'Pesaje'),
    (8, 'Desparasitación')
ON CONFLICT DO NOTHING;
SELECT setval('public.tipo_evento_id_seq', COALESCE((SELECT MAX(id) FROM public.tipo_evento), 0) + 1, false);

INSERT INTO public.tipo_movimiento (id, nombre) VALUES
    (1, 'Entrada'),
    (2, 'Salida'),
    (3, 'Traslado Interno')
ON CONFLICT DO NOTHING;
SELECT setval('public.tipo_movimiento_id_seq', COALESCE((SELECT MAX(id) FROM public.tipo_movimiento), 0) + 1, false);

INSERT INTO public.tipo_reproduccion (id, nombre) VALUES
    (1, 'Monta Natural'),
    (2, 'Inseminación Artificial'),
    (3, 'Transferencia Embrionaria')
ON CONFLICT DO NOTHING;
SELECT setval('public.tipo_reproduccion_id_seq', COALESCE((SELECT MAX(id) FROM public.tipo_reproduccion), 0) + 1, false);

INSERT INTO public.resultado_reproduccion (id, nombre) VALUES
    (1, 'Exitosa'),
    (2, 'No Exitosa'),
    (3, 'Aborto'),
    (4, 'Gemelar')
ON CONFLICT DO NOTHING;
SELECT setval('public.resultado_reproduccion_id_seq', COALESCE((SELECT MAX(id) FROM public.resultado_reproduccion), 0) + 1, false);

INSERT INTO public.turno_produccion (id, nombre) VALUES
    (1, 'Mañana'),
    (2, 'Tarde'),
    (3, 'Noche')
ON CONFLICT DO NOTHING;
SELECT setval('public.turno_produccion_id_seq', COALESCE((SELECT MAX(id) FROM public.turno_produccion), 0) + 1, false);

INSERT INTO public.rol (id, nombre) VALUES
    (1, 'ADMIN'),
    (2, 'USUARIO'),
    (3, 'VETERINARIO')
ON CONFLICT DO NOTHING;
SELECT setval('public.rol_id_seq', COALESCE((SELECT MAX(id) FROM public.rol), 0) + 1, false);

-- ============================================================
-- CATÁLOGOS SIMPLES (4)
-- ============================================================

INSERT INTO public.raza (id, nombre) VALUES
    (1,  'Angus'),
    (2,  'Brahman'),
    (3,  'Brangus'),
    (4,  'Charolais'),
    (5,  'Hereford'),
    (6,  'Holstein'),
    (7,  'Jersey'),
    (8,  'Limousin'),
    (9,  'Nelore'),
    (10, 'Simmental')
ON CONFLICT DO NOTHING;
SELECT setval('public.raza_id_seq', COALESCE((SELECT MAX(id) FROM public.raza), 0) + 1, false);

INSERT INTO public.alimento (id, nombre) VALUES
    (1, 'Concentrado'),
    (2, 'Heno'),
    (3, 'Silo'),
    (4, 'Pasto'),
    (5, 'Sal Mineral'),
    (6, 'Melaza')
ON CONFLICT DO NOTHING;
SELECT setval('public.alimento_id_seq', COALESCE((SELECT MAX(id) FROM public.alimento), 0) + 1, false);

INSERT INTO public.medicamento (id, nombre, descripcion) VALUES
    (1, 'Ivermectina',     'Antiparasitario interno y externo'),
    (2, 'Oxitetraciclina', 'Antibiótico de amplio espectro'),
    (3, 'Vitaminas ADE',   'Complejo vitamínico A, D, E'),
    (4, 'Desparasitante Oral', 'Desparasitante vía oral'),
    (5, 'Antiinflamatorio',    'Antiinflamatorio no esteroideo')
ON CONFLICT DO NOTHING;
SELECT setval('public.medicamento_id_seq', COALESCE((SELECT MAX(id) FROM public.medicamento), 0) + 1, false);

INSERT INTO public.vacuna (id, nombre) VALUES
    (1, 'Fiebre Aftosa'),
    (2, 'Brucelosis'),
    (3, 'Carbón Sintomático'),
    (4, 'Leptospirosis'),
    (5, 'Rabia')
ON CONFLICT DO NOTHING;
SELECT setval('public.vacuna_id_seq', COALESCE((SELECT MAX(id) FROM public.vacuna), 0) + 1, false);
