-- ============================================================
-- V1: Initial Schema — 28 tables
-- Matches the actual RDS PostgreSQL database as of 2026-05-30.
-- Uses CREATE TABLE IF NOT EXISTS for idempotency — safe to run
-- on both fresh databases AND the existing RDS instance.
-- ============================================================

-- ============================================================
-- CATÁLOGOS / TABLAS DE REFERENCIA (9)
-- ============================================================

CREATE TABLE IF NOT EXISTS public.sexo (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS public.estado_animal (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS public.condicion_nacimiento (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS public.tipo_evento (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS public.tipo_movimiento (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS public.tipo_reproduccion (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS public.resultado_reproduccion (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS public.turno_produccion (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS public.rol (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

-- ============================================================
-- CATÁLOGOS SIMPLES (4)
-- ============================================================

CREATE TABLE IF NOT EXISTS public.raza (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS public.alimento (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS public.medicamento (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT
);

CREATE TABLE IF NOT EXISTS public.vacuna (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

-- ============================================================
-- ENTIDADES PRINCIPALES (4)
-- ============================================================

CREATE TABLE IF NOT EXISTS public.finca (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    ubicacion TEXT,
    extension NUMERIC(10,2)
);

CREATE TABLE IF NOT EXISTS public.dieta (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT
);

CREATE TABLE IF NOT EXISTS public.usuario (
    id SERIAL PRIMARY KEY,
    id_rol INTEGER REFERENCES public.rol(id),
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255),
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS public.animal (
    id SERIAL PRIMARY KEY,
    id_sexo INTEGER REFERENCES public.sexo(id),
    id_estado_animal INTEGER REFERENCES public.estado_animal(id),
    id_raza INTEGER REFERENCES public.raza(id),
    id_madre INTEGER REFERENCES public.animal(id),
    id_padre INTEGER REFERENCES public.animal(id),
    identificador_arete VARCHAR(100) NOT NULL UNIQUE,
    nombre VARCHAR(100),
    fecha_nacimiento DATE NOT NULL,
    peso_actual_kg NUMERIC(10,2),
    foto_url TEXT,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- DEPENDIENTES DE FINCA (1)
-- ============================================================

CREATE TABLE IF NOT EXISTS public.lote (
    id SERIAL PRIMARY KEY,
    id_finca INTEGER NOT NULL REFERENCES public.finca(id),
    nombre VARCHAR(100) NOT NULL,
    hectareas NUMERIC(10,2),
    capacidad_maxima INTEGER,
    tipo_pasto VARCHAR(100),
    estado VARCHAR(50)
);

-- ============================================================
-- DEPENDIENTES DE DIETA (1)
-- ============================================================

CREATE TABLE IF NOT EXISTS public.dieta_alimento (
    id SERIAL PRIMARY KEY,
    id_dieta INTEGER NOT NULL REFERENCES public.dieta(id),
    id_alimento INTEGER NOT NULL REFERENCES public.alimento(id),
    cantidad NUMERIC(10,2),
    unidad VARCHAR(50)
);

-- ============================================================
-- TABLA CENTRAL DE EVENTOS (1)
-- ============================================================

CREATE TABLE IF NOT EXISTS public.evento (
    id SERIAL PRIMARY KEY,
    id_animal INTEGER NOT NULL REFERENCES public.animal(id),
    id_tipo_evento INTEGER NOT NULL REFERENCES public.tipo_evento(id),
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    descripcion TEXT
);

-- ============================================================
-- ACCIONES / SUB-EVENTOS (6)
-- ============================================================

CREATE TABLE IF NOT EXISTS public.alimentacion (
    id SERIAL PRIMARY KEY,
    id_animal INTEGER NOT NULL REFERENCES public.animal(id),
    id_dieta INTEGER REFERENCES public.dieta(id),
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observacion TEXT
);

CREATE TABLE IF NOT EXISTS public.movimiento (
    id SERIAL PRIMARY KEY,
    id_evento INTEGER NOT NULL REFERENCES public.evento(id),
    id_tipo_movimiento INTEGER REFERENCES public.tipo_movimiento(id),
    id_lote_origen INTEGER REFERENCES public.lote(id),
    id_lote_destino INTEGER REFERENCES public.lote(id),
    motivo TEXT
);

CREATE TABLE IF NOT EXISTS public.reproduccion (
    id SERIAL PRIMARY KEY,
    id_evento INTEGER NOT NULL REFERENCES public.evento(id),
    id_vaca INTEGER NOT NULL REFERENCES public.animal(id),
    id_toro INTEGER NOT NULL REFERENCES public.animal(id),
    id_tipo_reproduccion INTEGER REFERENCES public.tipo_reproduccion(id),
    id_resultado_reproduccion INTEGER REFERENCES public.resultado_reproduccion(id),
    fecha_parto_estimada DATE,
    observacion TEXT
);

CREATE TABLE IF NOT EXISTS public.parto (
    id SERIAL PRIMARY KEY,
    id_evento INTEGER NOT NULL REFERENCES public.evento(id),
    id_reproduccion INTEGER NOT NULL REFERENCES public.reproduccion(id),
    cantidad_crias INTEGER,
    observacion TEXT
);

CREATE TABLE IF NOT EXISTS public.registro_ternero (
    id SERIAL PRIMARY KEY,
    id_parto INTEGER NOT NULL REFERENCES public.parto(id),
    id_sexo INTEGER REFERENCES public.sexo(id),
    id_condicion_nacimiento INTEGER REFERENCES public.condicion_nacimiento(id),
    identificador_arete VARCHAR(100),
    peso_nacimiento_kg NUMERIC(10,2),
    observacion TEXT
);

CREATE TABLE IF NOT EXISTS public.tratamiento (
    id SERIAL PRIMARY KEY,
    id_evento INTEGER NOT NULL REFERENCES public.evento(id),
    id_medicamento INTEGER NOT NULL REFERENCES public.medicamento(id),
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE,
    dosis_ml VARCHAR(100),
    observacion TEXT
);

CREATE TABLE IF NOT EXISTS public.vacunacion (
    id SERIAL PRIMARY KEY,
    id_evento INTEGER NOT NULL REFERENCES public.evento(id),
    id_vacuna INTEGER NOT NULL REFERENCES public.vacuna(id),
    proxima_dosis DATE,
    observacion TEXT
);

-- ============================================================
-- PRODUCCIÓN (1)
-- ============================================================

CREATE TABLE IF NOT EXISTS public.produccion (
    id SERIAL PRIMARY KEY,
    id_animal INTEGER NOT NULL REFERENCES public.animal(id),
    id_turno_produccion INTEGER REFERENCES public.turno_produccion(id),
    fecha DATE NOT NULL,
    litros NUMERIC(10,2)
);
