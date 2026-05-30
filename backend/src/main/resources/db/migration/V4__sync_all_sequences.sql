-- ============================================================
-- V4: Sync all sequences to MAX(id) + 1
--
-- Fixes duplicate key errors caused by sequences being out of
-- sync with actual data (e.g., rows inserted with explicit IDs
-- without updating the sequence).
--
-- Safe to run multiple times — idempotent.
-- ============================================================

SELECT setval('public.sexo_id_seq',              COALESCE((SELECT MAX(id) FROM public.sexo),              0) + 1, false);
SELECT setval('public.estado_animal_id_seq',     COALESCE((SELECT MAX(id) FROM public.estado_animal),      0) + 1, false);
SELECT setval('public.condicion_nacimiento_id_seq', COALESCE((SELECT MAX(id) FROM public.condicion_nacimiento), 0) + 1, false);
SELECT setval('public.tipo_evento_id_seq',       COALESCE((SELECT MAX(id) FROM public.tipo_evento),        0) + 1, false);
SELECT setval('public.tipo_movimiento_id_seq',   COALESCE((SELECT MAX(id) FROM public.tipo_movimiento),    0) + 1, false);
SELECT setval('public.tipo_reproduccion_id_seq', COALESCE((SELECT MAX(id) FROM public.tipo_reproduccion),  0) + 1, false);
SELECT setval('public.resultado_reproduccion_id_seq', COALESCE((SELECT MAX(id) FROM public.resultado_reproduccion), 0) + 1, false);
SELECT setval('public.turno_produccion_id_seq',  COALESCE((SELECT MAX(id) FROM public.turno_produccion),   0) + 1, false);
SELECT setval('public.rol_id_seq',               COALESCE((SELECT MAX(id) FROM public.rol),                0) + 1, false);
SELECT setval('public.raza_id_seq',              COALESCE((SELECT MAX(id) FROM public.raza),               0) + 1, false);
SELECT setval('public.alimento_id_seq',          COALESCE((SELECT MAX(id) FROM public.alimento),           0) + 1, false);
SELECT setval('public.medicamento_id_seq',       COALESCE((SELECT MAX(id) FROM public.medicamento),        0) + 1, false);
SELECT setval('public.vacuna_id_seq',            COALESCE((SELECT MAX(id) FROM public.vacuna),             0) + 1, false);
SELECT setval('public.finca_id_seq',             COALESCE((SELECT MAX(id) FROM public.finca),              0) + 1, false);
SELECT setval('public.dieta_id_seq',             COALESCE((SELECT MAX(id) FROM public.dieta),              0) + 1, false);
SELECT setval('public.usuario_id_seq',           COALESCE((SELECT MAX(id) FROM public.usuario),            0) + 1, false);
SELECT setval('public.animal_id_seq',            COALESCE((SELECT MAX(id) FROM public.animal),             0) + 1, false);
SELECT setval('public.lote_id_seq',              COALESCE((SELECT MAX(id) FROM public.lote),               0) + 1, false);
SELECT setval('public.dieta_alimento_id_seq',    COALESCE((SELECT MAX(id) FROM public.dieta_alimento),     0) + 1, false);
SELECT setval('public.evento_id_seq',            COALESCE((SELECT MAX(id) FROM public.evento),             0) + 1, false);
SELECT setval('public.alimentacion_id_seq',      COALESCE((SELECT MAX(id) FROM public.alimentacion),       0) + 1, false);
SELECT setval('public.movimiento_id_seq',        COALESCE((SELECT MAX(id) FROM public.movimiento),         0) + 1, false);
SELECT setval('public.reproduccion_id_seq',      COALESCE((SELECT MAX(id) FROM public.reproduccion),       0) + 1, false);
SELECT setval('public.parto_id_seq',             COALESCE((SELECT MAX(id) FROM public.parto),              0) + 1, false);
SELECT setval('public.registro_ternero_id_seq',  COALESCE((SELECT MAX(id) FROM public.registro_ternero),   0) + 1, false);
SELECT setval('public.tratamiento_id_seq',       COALESCE((SELECT MAX(id) FROM public.tratamiento),        0) + 1, false);
SELECT setval('public.vacunacion_id_seq',        COALESCE((SELECT MAX(id) FROM public.vacunacion),         0) + 1, false);
SELECT setval('public.produccion_id_seq',        COALESCE((SELECT MAX(id) FROM public.produccion),         0) + 1, false);
