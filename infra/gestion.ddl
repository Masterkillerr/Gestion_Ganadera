-- WARNING: This schema is for context only and is not meant to be run.
-- Adapted from the real PostgreSQL database (Hibernate-generated) on 2026-05-30.
-- 28 tables total.

-- ============================================================
-- CATÁLOGOS / TABLAS DE REFERENCIA (9)
-- ============================================================

CREATE TABLE public.sexo (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE public.estado_animal (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE public.condicion_nacimiento (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE public.tipo_evento (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE public.tipo_movimiento (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE public.tipo_reproduccion (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE public.resultado_reproduccion (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE public.turno_produccion (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE public.rol (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

-- ============================================================
-- CATÁLOGOS SIMPLES (4)
-- ============================================================

CREATE TABLE public.raza (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE public.alimento (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE public.medicamento (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT
);

CREATE TABLE public.vacuna (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

-- ============================================================
-- ENTIDADES PRINCIPALES (4)
-- ============================================================

CREATE TABLE public.finca (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    ubicacion TEXT,
    extension NUMERIC(10,2)
);

CREATE TABLE public.dieta (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT
);

CREATE TABLE public.usuario (
    id SERIAL PRIMARY KEY,
    id_rol INTEGER REFERENCES public.rol(id),
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255),
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE public.animal (
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

CREATE TABLE public.lote (
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

CREATE TABLE public.dieta_alimento (
    id SERIAL PRIMARY KEY,
    id_dieta INTEGER NOT NULL REFERENCES public.dieta(id),
    id_alimento INTEGER NOT NULL REFERENCES public.alimento(id),
    cantidad NUMERIC(10,2),
    unidad VARCHAR(50)
);

-- ============================================================
-- TABLA CENTRAL DE EVENTOS (1)
-- ============================================================

CREATE TABLE public.evento (
    id SERIAL PRIMARY KEY,
    id_animal INTEGER NOT NULL REFERENCES public.animal(id),
    id_tipo_evento INTEGER NOT NULL REFERENCES public.tipo_evento(id),
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    descripcion TEXT
);

-- ============================================================
-- ACCIONES / SUB-EVENTOS (6)
-- Todas referencian evento como tabla central
-- ============================================================

CREATE TABLE public.alimentacion (
    id SERIAL PRIMARY KEY,
    id_animal INTEGER NOT NULL REFERENCES public.animal(id),
    id_dieta INTEGER REFERENCES public.dieta(id),
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observacion TEXT
);

CREATE TABLE public.movimiento (
    id SERIAL PRIMARY KEY,
    id_evento INTEGER NOT NULL REFERENCES public.evento(id),
    id_tipo_movimiento INTEGER REFERENCES public.tipo_movimiento(id),
    id_lote_origen INTEGER REFERENCES public.lote(id),
    id_lote_destino INTEGER REFERENCES public.lote(id),
    motivo TEXT
);

CREATE TABLE public.reproduccion (
    id SERIAL PRIMARY KEY,
    id_evento INTEGER NOT NULL REFERENCES public.evento(id),
    id_vaca INTEGER NOT NULL REFERENCES public.animal(id),
    id_toro INTEGER NOT NULL REFERENCES public.animal(id),
    id_tipo_reproduccion INTEGER REFERENCES public.tipo_reproduccion(id),
    id_resultado_reproduccion INTEGER REFERENCES public.resultado_reproduccion(id),
    fecha_parto_estimada DATE,
    observacion TEXT
);

CREATE TABLE public.parto (
    id SERIAL PRIMARY KEY,
    id_evento INTEGER NOT NULL REFERENCES public.evento(id),
    id_reproduccion INTEGER NOT NULL REFERENCES public.reproduccion(id),
    cantidad_crias INTEGER,
    observacion TEXT
);

CREATE TABLE public.registro_ternero (
    id SERIAL PRIMARY KEY,
    id_parto INTEGER NOT NULL REFERENCES public.parto(id),
    id_sexo INTEGER REFERENCES public.sexo(id),
    id_condicion_nacimiento INTEGER REFERENCES public.condicion_nacimiento(id),
    identificador_arete VARCHAR(100),
    peso_nacimiento_kg NUMERIC(10,2),
    observacion TEXT
);

CREATE TABLE public.tratamiento (
    id SERIAL PRIMARY KEY,
    id_evento INTEGER NOT NULL REFERENCES public.evento(id),
    id_medicamento INTEGER NOT NULL REFERENCES public.medicamento(id),
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE,
    dosis_ml VARCHAR(100),
    observacion TEXT
);

CREATE TABLE public.vacunacion (
    id SERIAL PRIMARY KEY,
    id_evento INTEGER NOT NULL REFERENCES public.evento(id),
    id_vacuna INTEGER NOT NULL REFERENCES public.vacuna(id),
    proxima_dosis DATE,
    observacion TEXT
);

-- ============================================================
-- PRODUCCIÓN (1)
-- ============================================================

CREATE TABLE public.produccion (
    id SERIAL PRIMARY KEY,
    id_animal INTEGER NOT NULL REFERENCES public.animal(id),
    id_turno_produccion INTEGER REFERENCES public.turno_produccion(id),
    fecha DATE NOT NULL,
    litros NUMERIC(10,2)
);
