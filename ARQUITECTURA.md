# Sistema de Gestión Ganadera - Arquitectura y Plan de Desarrollo

## 📋 Descripción del Sistema
Sistema web integral para la gestión de operaciones ganaderas, permitiendo administrar ganado, pastos, reproducción, sanidad, producción y reportes.

---

## 🏗️ Arquitectura del Sistema

### Stack Tecnológico

**Frontend:**
- React.js + Vite (framework moderno)
- TailwindCSS + shadcn/ui (UI responsive)
- React Router (navegación)
- Axios (consumo API)
- React Hook Form + Zod (validación)
- Recharts (dashboard y estadísticas)

**Backend:**
- Java 17
- Spring Boot 3.5.x
- Spring Security + JWT
- Spring Data JPA (Hibernate)
- Maven (Gestión de dependencias)

**Base de Datos:**
- PostgreSQL (base de datos relacional)

---

## 👥 Roles de Usuario

1. **Administrador**
   - Acceso completo al sistema
   - Gestión de usuarios
   - Reportes y estadísticas
   - Configuración del sistema

2. **Operador/Veterinario**
   - Gestión de ganado
   - Registro de sanidad y reproducción
   - Consultas y reportes básicos

---

## 🗄️ Modelo de Base de Datos

### Entidades Principales (8 entidades)

```
Usuario (id, nombre, email, password, rol, createdAt)
    ↓
Finca (id, nombre, ubicacion, area, encargadoId → Usuario)
    ↓
Lote (id, nombre, fincaId → Finca, capacidad, tipoPasto)
    ↓
Ganado (id, arete/identificador, raza, sexo, fechaNacimiento, 
        peso, estado, loteId → Lote, fotoUrl)
    ↓
├── Reproduccion (id, vacaId → Ganado, toroId → Ganado, 
│                 fechaMonta, fechaPartoEsperada, fechaPartoReal, 
│                 tipoParto, observaciones)
│       ↓
│   └── Ternero (id, madreId → Ganado, padreId → Ganado, 
│                fechaNacimiento, pesoNacimiento, sexo)
│
├── Sanidad (id, ganadoId → Ganado, tipoTratamiento, 
│            medicamento, dosis, fechaAplicacion, 
│            fechaRetiro, veterinarioId → Usuario, observaciones)
│
├── ProduccionLeche (id, ganadoId → Ganado, fecha, 
│                     cantidadLitros, turno, observaciones)
│
└── MovimientoInventario (id, ganadoId → Ganado, tipoMovimiento, 
                           fecha, origenId → Lote, destinoId → Lote, 
                           motivo, usuarioId → Usuario)
```

### Relaciones Clave:
- Usuario 1:N Finca (encargado)
- Finca 1:N Lote
- Lote 1:N Ganado
- Ganado 1:N Reproduccion (como madre o padre)
- Ganado 1:N Sanidad
- Ganado 1:N ProduccionLeche
- Ganado 1:N MovimientoInventario

---

## 🔹 Backend - Estructura de API REST

```
/api
├── /auth               # AuthController (Login/JWT)
├── /usuarios           # UsuarioController
├── /fincas             # FincaController
├── /lotes              # LoteController
├── /ganado             # GanadoController
├── /reproduccion       # Entidad/Repository
├── /sanidad            # Entidad/Repository
├── /produccion         # Entidad/Repository
└── /dashboard          # Lógica en servicios
```

---

## 🎨 Frontend - Estructura de Vistas

```
/src
├── /pages
│   ├── Login.jsx               # Inicio de sesión
│   ├── Dashboard.jsx           # Panel principal con estadísticas
│   │
│   ├── /usuarios
│   │   ├── UsuariosList.jsx    # Listado usuarios
│   │   └── UsuarioForm.jsx     # Crear/Editar usuario
│   │
│   ├── /fincas
│   │   ├── FincasList.jsx      # Listado fincas
│   │   └── FincaForm.jsx       # Crear/Editar finca
│   │
│   ├── /lotes
│   │   ├── LotesList.jsx       # Listado lotes
│   │   └── LoteForm.jsx        # Crear/Editar lote
│   │
│   ├── /ganado
│   │   ├── GanadoList.jsx      # Listado ganado (con filtros)
│   │   ├── GanadoForm.jsx      # Crear/Editar ganado
│   │   └── GanadoDetail.jsx    # Ficha técnica + historial
│   │
│   ├── /reproduccion
│   │   ├── ReproduccionList.jsx # Registros reproducción
│   │   ├── ReproduccionForm.jsx # Registrar monta
│   │   └── PartosPendientes.jsx # Partos próximos
│   │
│   ├── /sanidad
│   │   ├── SanidadList.jsx     # Historial sanitario
│   │   ├── SanidadForm.jsx     # Registrar tratamiento
│   │   └── CalendarioVacunacion.jsx
│   │
│   ├── /produccion
│   │   ├── ProduccionList.jsx  # Registros producción
│   │   └── ProduccionForm.jsx  # Registrar producción diaria
│   │
│   ├── /movimientos
│   │   ├── MovimientosList.jsx # Historial movimientos
│   │   └── MovimientoForm.jsx  # Registrar traslado
│   │
│   └── /reportes
│       ├── Reportes.jsx        # Reportes avanzados
│       └── Exportar.jsx        # Exportar datos
│
├── /components
│   ├── Layout.jsx              # Layout principal
│   ├── Sidebar.jsx             # Menú lateral
│   ├── Navbar.jsx              # Barra superior
│   ├── DataTable.jsx           # Tabla reutilizable
│   ├── Modal.jsx               # Modal reutilizable
│   ├── FormField.jsx           # Campos formulario
│   ├── SearchBar.jsx           # Búsqueda
│   └── StatsCard.jsx           # Tarjetas estadísticas
│
├── /context
│   └── AuthContext.jsx         # Contexto autenticación
│
├── /services
│   ├── api.js                  # Configuración Axios
│   ├── authService.js
│   ├── ganadoService.js
│   └── ... (por entidad)
│
└── /utils
    ├── validators.js           # Validaciones
    ├── formatters.js           # Formateo datos
    └── constants.js
```

---

## 📦 Módulos del Sistema

### Módulo 1: Autenticación y Usuarios
- Login/Registro
- JWT tokens
- Roles y permisos
- Gestión de usuarios (Admin)

### Módulo 2: Gestión de Fincas y Lotes
- CRUD Fincas
- CRUD Lotes
- Capacidad y carga de pastoreo

### Módulo 3: Gestión de Ganado (CRUD + Lógica de negocio)
- Registro individual (arete/ID)
- Ficha técnica completa
- Historial por animal
- **Lógica de negocio:** Cálculo de edad, estado reproductivo, alertas sanitarias
- Subida de fotos

### Módulo 4: Reproducción
- Registro de montas
- Cálculo fecha parto esperada
- Registro de partos y terneros
- **Lógica de negocio:** Alertas de partos próximos, historial reproductivo

### Módulo 5: Sanidad
- Registro tratamientos
- Calendario de vacunación
- Control de medicamentos
- **Lógica de negocio:** Alertas de retiro de medicamentos, recordatorios

### Módulo 6: Producción
- Registro diario de leche
- Estadísticas por animal/lote
- **Lógica de negocio:** Promedios, tendencias, comparativas

### Módulo 7: Movimientos
- Traslados entre lotes
- Registro de bajas/ventas
- Trazabilidad completa

### Módulo 8: Dashboard y Reportes ⭐
- Estadísticas generales
- Producción mensual (gráficos)
- Estado de hatos
- Alertas del sistema
- Exportar datos (PDF/Excel)

---

## 🔐 Sistema de Autenticación

```javascript
// Flujo de autenticación
1. Usuario login → POST /api/auth/login
2. Backend valida credenciales → genera JWT
3. Frontend guarda token en localStorage
4. Cada request incluye token en header Authorization
5. Middleware verifica token y rol
6. Protected routes según rol
```

**Middleware de autenticación:**
- `authenticateToken` - Verifica JWT válido
- `authorizeRole(roles)` - Verifica permisos

---

## 🚀 Funcionalidades con Lógica de Negocio

### 1. Cálculo de Edad y Categoría
```
- Edad en días/meses/años desde fecha nacimiento
- Categoría automática: Ternero (0-12m), Novillo (12-24m), Adulto (>24m)
```

### 2. Calendario Reproductivo
```
- Fecha parto esperada = fecha monta + 283 días (bovinos)
- Alerta 15 días antes del parto esperado
- Historial reproductivo por vaca
```

### 3. Alertas Sanitarias
```
- Próximas vacunaciones
- Retiro de medicamentos (fecha de espera antes de consumo/producción)
- Control de tratamientos activos
```

### 4. Estadísticas de Producción
```
- Producción diaria/mensual por animal y lote
- Promedios y tendencias
- Comparativas entre períodos
- Gráficos de producción
```

### 5. Control de Capacidad
```
- Capacidad máxima por lote
- Alerta si se excede capacidad
- Cálculo de carga animal por hectárea
```

---

## 📊 Dashboard - Estadísticas

**Cards superiores:**
- Total ganado (por estado: activo, en tratamiento, seco)
- Producción leche hoy (litros)
- Partos pendientes (próximos 15 días)
- Tratamientos activos

**Gráficos:**
- Producción mensual (línea)
- Distribución por categoría (pie)
- Nacimientos vs bajas por mes (barras)
- Producción por lote (barras)

**Tablas:**
- Últimos movimientos
- Alertas pendientes
- Próximos eventos

---

## 📁 Estructura del Proyecto

```
Gestion_Ganadera/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/gestionganadera/backend/
│   │   │   │   ├── config/       # SecurityConfig
│   │   │   │   ├── controller/   # REST Controllers
│   │   │   │   ├── dto/          # Data Transfer Objects
│   │   │   │   ├── model/        # Entities (JPA)
│   │   │   │   ├── repository/   # Spring Data Repositories
│   │   │   │   ├── service/      # Business Logic
│   │   │   │   └── util/         # JWT, FileUpload
│   │   │   └── resources/
│   │   │       └── application.properties
│   ├── pom.xml                   # Maven dependencies
│   └── .classpath                # IDE config
│
├── frontend/
│   ├── src/
│   │   ├── pages/                # Vistas por módulo
│   │   ├── components/           # Componentes reutilizables
│   │   ├── context/              # Contextos React
│   │   ├── services/             # Servicios API
│   │   ├── utils/                # Helpers
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── public/
│   ├── .env
│   └── package.json
│
├── database/
│   └── init.sql                  # Script BD
│
├── docs/
│   └── documento_proyecto.md     # Documentación completa
│
├── README.md
└── .gitignore
```

---

## 📝 Plan de Desarrollo por Fases

### Fase 1: Setup y Estructura (Semana 1-2)
- [ ] Inicializar proyecto (frontend + backend)
- [ ] Configurar PostgreSQL y Prisma
- [ ] Definir modelo de datos (schema.prisma)
- [ ] Crear estructura de carpetas
- [ ] Configurar autenticación JWT
- [ ] Crear layouts base del frontend

### Fase 2: CRUD Básico (Semana 3-4)
- [ ] API: Usuarios, Fincas, Lotes
- [ ] Frontend: Gestión de usuarios (Admin)
- [ ] Frontend: Gestión de fincas y lotes
- [ ] Formularios con validación
- [ ] Tablas con filtros y paginación

### Fase 3: Módulo Ganado (Semana 5-6)
- [ ] API: CRUD Ganado completo
- [ ] Frontend: Listado, creación, edición
- [ ] Subida de fotos
- [ ] Ficha técnica detallada
- [ ] Lógica: cálculo edad, categorías

### Fase 4: Reproducción y Sanidad (Semana 7-8)
- [ ] API: Reproducción y Sanidad
- [ ] Frontend: Registro y consulta
- [ ] Lógica: calendario reproductivo
- [ ] Lógica: alertas sanitarias
- [ ] Vista de partos pendientes

### Fase 5: Producción y Movimientos (Semana 9-10)
- [ ] API: Producción y Movimientos
- [ ] Frontend: Registro producción diaria
- [ ] Frontend: Gestión de traslados
- [ ] Lógica: estadísticas producción
- [ ] Trazabilidad de movimientos

### Fase 6: Dashboard y Reportes (Semana 11-12)
- [ ] API: Endpoints de estadísticas
- [ ] Frontend: Dashboard con gráficos
- [ ] Reportes avanzados
- [ ] Exportar datos (PDF/Excel)
- [ ] Sistema de alertas

### Fase 7: Testing y Documentación (Semana 13-14)
- [ ] Testing completo de funcionalidades
- [ ] Corrección de bugs
- [ ] Documentación del proyecto
- [ ] Video de presentación
- [ ] Preparar sustentación

---

## 🎯 Cumplimiento de Requisitos

### ✅ Backend
- [x] API REST completa
- [x] Operaciones CRUD
- [x] Conexión a PostgreSQL
- [x] Manejo de endpoints
- [x] Sistema de autenticación JWT

### ✅ Frontend
- [x] Interfaz web responsive (TailwindCSS)
- [x] Formularios con validación (Zod)
- [x] Consumo de API (Axios)
- [x] Navegación entre vistas (React Router)

### ✅ Base de Datos
- [x] Modelo relacional (Prisma)
- [x] 8 tablas con relaciones definidas

### ✅ Requisitos Funcionales
- [x] 8 entidades principales (>5 requeridas)
- [x] 2 roles de usuario (Admin, Operador)
- [x] CRUD completo
- [x] Múltiples módulos con lógica de negocio

### ⭐ Extras (Calificación superior)
- [x] Dashboard con estadísticas y gráficos
- [x] Reportes y consultas avanzadas
- [x] Manejo de roles y permisos
- [x] Subida de imágenes
- [x] Sistema de notificaciones/alertas
- [x] Framework moderno (React + Vite)

---

## 🔧 Scripts Útiles

```bash
# Backend
cd backend
npm install                    # Instalar dependencias
npm run dev                    # Desarrollo con nodemon
npx prisma migrate dev         # Migraciones BD
npx prisma studio              # Ver BD en interfaz
npx prisma db seed             # Datos iniciales

# Frontend
cd frontend
npm install                    # Instalar dependencias
npm run dev                    # Servidor desarrollo
npm run build                  # Build producción

# Base de Datos
npx prisma generate            # Generar cliente
npx prisma migrate reset       # Resetear BD
```

---

## 📚 Documentación a Entregar

1. **Documento del Proyecto** (docs/documento_proyecto.md)
   - Nombre del sistema
   - Descripción detallada
   - Objetivos generales y específicos
   - Funcionalidades
   - Roles de usuario
   - Requerimientos funcionales y técnicos
   - Diagrama de base de datos
   - Manual de usuario

2. **Código Fuente**
   - Frontend completo
   - Backend completo
   - Script de base de datos

3. **Repositorio GitHub**
   - README con instrucciones
   - Historial de commits
   - Ramas organizadas

4. **Video/Presentación**
   - Demo del sistema funcionando
   - Explicación de funcionalidades
   - Presentación de todos los integrantes

---

## 💡 Recomendaciones de Desarrollo

1. **División de trabajo (3 personas):**
   - **Persona 1:** Backend completo + Base de datos
   - **Persona 2:** Frontend (módulos principales)
   - **Persona 3:** Frontend (dashboard, reportes) + Testing

2. **Working agreement:**
   - Commits frecuentes y descriptivos
   - Ramas por feature (git flow)
   - Reuniones semanales de seguimiento
   - Code reviews entre compañeros

3. **Buenas prácticas:**
   - Variables de entorno para configuración
   - Manejo de errores en toda la app
   - Validación frontend y backend
   - Documentar endpoints API
   - Testing continuo

---

## 🚀 Próximos Pasos

1. **Inicializar estructura del proyecto**
2. **Definir e implementar modelo de datos**
3. **Configurar autenticación**
4. **Comenzar desarrollo por módulos**

¿Procedemos a iniciar el proyecto con esta arquitectura?
