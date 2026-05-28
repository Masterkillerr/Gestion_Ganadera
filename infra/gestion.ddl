-- WARNING: This schema is for context only and is not meant to be run.
-- Table order and constraints may not be valid for execution.

CREATE TABLE public.alertas (
  id integer NOT NULL DEFAULT nextval('alertas_id_seq'::regclass),
  animal_id integer,
  tipo character varying,
  mensaje text,
  fecha timestamp without time zone,
  leida boolean DEFAULT false,
  CONSTRAINT alertas_pkey PRIMARY KEY (id),
  CONSTRAINT alertas_animal_id_fkey FOREIGN KEY (animal_id) REFERENCES public.animales(id)
);
CREATE TABLE public.alimentaciones (
  id integer NOT NULL DEFAULT nextval('alimentaciones_id_seq'::regclass),
  animal_id integer,
  alimento_id integer,
  fecha date NOT NULL,
  cantidad numeric,
  observaciones text,
  CONSTRAINT alimentaciones_pkey PRIMARY KEY (id),
  CONSTRAINT alimentaciones_animal_id_fkey FOREIGN KEY (animal_id) REFERENCES public.animales(id),
  CONSTRAINT alimentaciones_alimento_id_fkey FOREIGN KEY (alimento_id) REFERENCES public.alimentos(id)
);
CREATE TABLE public.alimentos (
  id integer NOT NULL DEFAULT nextval('alimentos_id_seq'::regclass),
  nombre character varying NOT NULL,
  CONSTRAINT alimentos_pkey PRIMARY KEY (id)
);
CREATE TABLE public.animales (
  id integer NOT NULL DEFAULT nextval('animales_id_seq'::regclass),
  identificador_arete character varying NOT NULL UNIQUE,
  nombre character varying,
  sexo character varying CHECK (sexo::text = ANY (ARRAY['Macho'::character varying, 'Hembra'::character varying]::text[])),
  fecha_nacimiento date NOT NULL,
  peso_actual numeric,
  estado character varying CHECK (estado::text = ANY (ARRAY['Activo'::character varying, 'En tratamiento'::character varying, 'En cuarentena'::character varying, 'Vendido'::character varying, 'Fallecido'::character varying, 'Sacrificado'::character varying]::text[])),
  foto_url text,
  creado_en timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  raza_id integer,
  categoria_id integer,
  finca_id integer,
  lote_id integer,
  madre_id integer,
  padre_id integer,
  CONSTRAINT animales_pkey PRIMARY KEY (id),
  CONSTRAINT animales_raza_id_fkey FOREIGN KEY (raza_id) REFERENCES public.razas(id),
  CONSTRAINT animales_categoria_id_fkey FOREIGN KEY (categoria_id) REFERENCES public.categorias(id),
  CONSTRAINT animales_finca_id_fkey FOREIGN KEY (finca_id) REFERENCES public.fincas(id),
  CONSTRAINT animales_lote_id_fkey FOREIGN KEY (lote_id) REFERENCES public.lotes(id),
  CONSTRAINT animales_madre_id_fkey FOREIGN KEY (madre_id) REFERENCES public.animales(id),
  CONSTRAINT animales_padre_id_fkey FOREIGN KEY (padre_id) REFERENCES public.animales(id)
);
CREATE TABLE public.categorias (
  id integer NOT NULL DEFAULT nextval('categorias_id_seq'::regclass),
  nombre character varying NOT NULL UNIQUE,
  descripcion text,
  CONSTRAINT categorias_pkey PRIMARY KEY (id)
);
CREATE TABLE public.eventos (
  id integer NOT NULL DEFAULT nextval('eventos_id_seq'::regclass),
  animal_id integer,
  tipo character varying,
  descripcion text,
  fecha timestamp without time zone,
  CONSTRAINT eventos_pkey PRIMARY KEY (id),
  CONSTRAINT eventos_animal_id_fkey FOREIGN KEY (animal_id) REFERENCES public.animales(id)
);
CREATE TABLE public.fincas (
  id integer NOT NULL DEFAULT nextval('fincas_id_seq'::regclass),
  nombre character varying NOT NULL,
  ubicacion text,
  CONSTRAINT fincas_pkey PRIMARY KEY (id)
);
CREATE TABLE public.lotes (
  id integer NOT NULL DEFAULT nextval('lotes_id_seq'::regclass),
  finca_id integer NOT NULL,
  nombre character varying NOT NULL,
  hectareas numeric,
  capacidad_maxima integer,
  tipo_pasto character varying,
  estado character varying,
  CONSTRAINT lotes_pkey PRIMARY KEY (id),
  CONSTRAINT lotes_finca_id_fkey FOREIGN KEY (finca_id) REFERENCES public.fincas(id)
);
CREATE TABLE public.medicamentos (
  id integer NOT NULL DEFAULT nextval('medicamentos_id_seq'::regclass),
  nombre character varying NOT NULL,
  descripcion text,
  CONSTRAINT medicamentos_pkey PRIMARY KEY (id)
);
CREATE TABLE public.movimientos (
  id integer NOT NULL DEFAULT nextval('movimientos_id_seq'::regclass),
  animal_id integer,
  lote_origen_id integer,
  lote_destino_id integer,
  fecha date NOT NULL,
  tipo_movimiento character varying,
  motivo text,
  CONSTRAINT movimientos_pkey PRIMARY KEY (id),
  CONSTRAINT movimientos_animal_id_fkey FOREIGN KEY (animal_id) REFERENCES public.animales(id),
  CONSTRAINT movimientos_lote_origen_id_fkey FOREIGN KEY (lote_origen_id) REFERENCES public.lotes(id),
  CONSTRAINT movimientos_lote_destino_id_fkey FOREIGN KEY (lote_destino_id) REFERENCES public.lotes(id)
);
CREATE TABLE public.partos (
  id integer NOT NULL DEFAULT nextval('partos_id_seq'::regclass),
  reproduccion_id integer,
  fecha_parto date NOT NULL,
  cantidad_crias integer,
  observaciones text,
  CONSTRAINT partos_pkey PRIMARY KEY (id),
  CONSTRAINT partos_reproduccion_id_fkey FOREIGN KEY (reproduccion_id) REFERENCES public.reproducciones(id)
);
CREATE TABLE public.producciones (
  id integer NOT NULL DEFAULT nextval('producciones_id_seq'::regclass),
  animal_id integer,
  fecha date NOT NULL,
  litros numeric,
  turno character varying,
  CONSTRAINT producciones_pkey PRIMARY KEY (id),
  CONSTRAINT producciones_animal_id_fkey FOREIGN KEY (animal_id) REFERENCES public.animales(id)
);
CREATE TABLE public.razas (
  id integer NOT NULL DEFAULT nextval('razas_id_seq'::regclass),
  nombre character varying NOT NULL UNIQUE,
  CONSTRAINT razas_pkey PRIMARY KEY (id)
);
CREATE TABLE public.registro_terneros (
  id integer NOT NULL DEFAULT nextval('registro_terneros_id_seq'::regclass),
  parto_id integer,
  animal_id integer,
  peso_nacimiento numeric,
  sexo_nacimiento character varying,
  condicion_nacimiento character varying,
  observaciones text,
  CONSTRAINT registro_terneros_pkey PRIMARY KEY (id),
  CONSTRAINT registro_terneros_parto_id_fkey FOREIGN KEY (parto_id) REFERENCES public.partos(id),
  CONSTRAINT registro_terneros_animal_id_fkey FOREIGN KEY (animal_id) REFERENCES public.animales(id)
);
CREATE TABLE public.reproducciones (
  id integer NOT NULL DEFAULT nextval('reproducciones_id_seq'::regclass),
  vaca_id integer NOT NULL,
  toro_id integer NOT NULL,
  fecha_monta date NOT NULL,
  tipo character varying,
  resultado character varying,
  fecha_parto_estimada date,
  observaciones text,
  CONSTRAINT reproducciones_pkey PRIMARY KEY (id),
  CONSTRAINT reproducciones_vaca_id_fkey FOREIGN KEY (vaca_id) REFERENCES public.animales(id),
  CONSTRAINT reproducciones_toro_id_fkey FOREIGN KEY (toro_id) REFERENCES public.animales(id)
);
CREATE TABLE public.roles (
  id integer NOT NULL DEFAULT nextval('roles_id_seq'::regclass),
  nombre character varying NOT NULL UNIQUE,
  CONSTRAINT roles_pkey PRIMARY KEY (id)
);
CREATE TABLE public.tratamientos (
  id integer NOT NULL DEFAULT nextval('tratamientos_id_seq'::regclass),
  animal_id integer,
  medicamento_id integer,
  fecha_inicio date NOT NULL,
  fecha_fin date,
  dosis character varying,
  dias_retiro integer,
  observaciones text,
  CONSTRAINT tratamientos_pkey PRIMARY KEY (id),
  CONSTRAINT tratamientos_animal_id_fkey FOREIGN KEY (animal_id) REFERENCES public.animales(id),
  CONSTRAINT tratamientos_medicamento_id_fkey FOREIGN KEY (medicamento_id) REFERENCES public.medicamentos(id)
);
CREATE TABLE public.usuarios (
  id uuid NOT NULL,
  nombre character varying NOT NULL,
  email character varying NOT NULL UNIQUE,
  creado_en timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  rol_id integer,
  CONSTRAINT usuarios_pkey PRIMARY KEY (id),
  CONSTRAINT usuarios_rol_id_fkey FOREIGN KEY (rol_id) REFERENCES public.roles(id)
);
CREATE TABLE public.vacunaciones (
  id integer NOT NULL DEFAULT nextval('vacunaciones_id_seq'::regclass),
  animal_id integer,
  vacuna_id integer,
  fecha date NOT NULL,
  proxima_dosis date,
  observaciones text,
  CONSTRAINT vacunaciones_pkey PRIMARY KEY (id),
  CONSTRAINT vacunaciones_animal_id_fkey FOREIGN KEY (animal_id) REFERENCES public.animales(id),
  CONSTRAINT vacunaciones_vacuna_id_fkey FOREIGN KEY (vacuna_id) REFERENCES public.vacunas(id)
);
CREATE TABLE public.vacunas (
  id integer NOT NULL DEFAULT nextval('vacunas_id_seq'::regclass),
  nombre character varying NOT NULL,
  CONSTRAINT vacunas_pkey PRIMARY KEY (id)
);