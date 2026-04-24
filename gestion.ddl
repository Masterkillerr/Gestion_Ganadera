-- =========================
-- EXTENSIONES
-- =========================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =========================
-- USUARIOS Y ROLES
-- =========================
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password TEXT NOT NULL,
    rol_id INT REFERENCES roles(id),
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- TAXONOMÍA
-- =========================
CREATE TABLE especies (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE razas (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    especie_id INT REFERENCES especies(id) ON DELETE CASCADE
);

CREATE TABLE categorias (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    especie_id INT REFERENCES especies(id) ON DELETE CASCADE
);

-- =========================
-- FINCA Y UBICACIÓN
-- =========================
CREATE TABLE fincas (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    ubicacion TEXT
);

-- =========================
-- LOTES (POBLACIONES)
-- =========================
CREATE TABLE lotes (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100),
    especie_id INT REFERENCES especies(id),
    cantidad INT NOT NULL,
    fecha_ingreso DATE,
    estado VARCHAR(50)
);

-- =========================
-- ANIMALES (INDIVIDUAL)
-- =========================
CREATE TABLE animales (
    id SERIAL PRIMARY KEY,
    identificador VARCHAR(100) UNIQUE NOT NULL,
    especie_id INT REFERENCES especies(id),
    raza_id INT REFERENCES razas(id),
    categoria_id INT REFERENCES categorias(id),
    lote_id INT REFERENCES lotes(id),
    fecha_nacimiento DATE,
    peso DECIMAL(10,2),
    estado VARCHAR(50), -- activo, vendido, muerto
    finca_id INT REFERENCES fincas(id),
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- TRAZABILIDAD (EVENTOS)
-- =========================
CREATE TABLE eventos (
    id SERIAL PRIMARY KEY,
    animal_id INT REFERENCES animales(id) ON DELETE CASCADE,
    tipo VARCHAR(50), -- nacimiento, traslado, venta, muerte
    descripcion TEXT,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- SANIDAD
-- =========================
CREATE TABLE vacunas (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE vacunaciones (
    id SERIAL PRIMARY KEY,
    animal_id INT REFERENCES animales(id) ON DELETE CASCADE,
    vacuna_id INT REFERENCES vacunas(id),
    fecha DATE NOT NULL,
    proxima_dosis DATE,
    UNIQUE(animal_id, vacuna_id, fecha)
);

CREATE TABLE medicamentos (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100),
    descripcion TEXT
);

CREATE TABLE tratamientos (
    id SERIAL PRIMARY KEY,
    animal_id INT REFERENCES animales(id) ON DELETE CASCADE,
    medicamento_id INT REFERENCES medicamentos(id),
    dosis VARCHAR(50),
    fecha_inicio DATE,
    fecha_fin DATE
);

-- =========================
-- REPRODUCCIÓN
-- =========================
CREATE TABLE reproducciones (
    id SERIAL PRIMARY KEY,
    animal_id INT REFERENCES animales(id) ON DELETE CASCADE,
    tipo VARCHAR(50), -- celo, inseminacion, preñez
    fecha DATE NOT NULL,
    resultado VARCHAR(100)
);

CREATE TABLE partos (
    id SERIAL PRIMARY KEY,
    madre_id INT REFERENCES animales(id),
    fecha DATE NOT NULL,
    cantidad_crias INT
);

-- =========================
-- ALIMENTACIÓN
-- =========================
CREATE TABLE alimentos (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE alimentaciones (
    id SERIAL PRIMARY KEY,
    animal_id INT REFERENCES animales(id),
    alimento_id INT REFERENCES alimentos(id),
    cantidad DECIMAL(10,2),
    fecha DATE NOT NULL
);

CREATE TABLE inventario_alimentos (
    id SERIAL PRIMARY KEY,
    alimento_id INT REFERENCES alimentos(id),
    stock DECIMAL(10,2),
    unidad VARCHAR(20)
);

-- =========================
-- PRODUCCIÓN
-- =========================
CREATE TABLE producciones (
    id SERIAL PRIMARY KEY,
    animal_id INT REFERENCES animales(id),
    tipo VARCHAR(50), -- leche, peso, huevos
    valor DECIMAL(10,2),
    fecha DATE NOT NULL
);

-- =========================
-- ALERTAS
-- =========================
CREATE TABLE alertas (
    id SERIAL PRIMARY KEY,
    tipo VARCHAR(50),
    mensaje TEXT,
    animal_id INT REFERENCES animales(id),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    leida BOOLEAN DEFAULT FALSE
);
