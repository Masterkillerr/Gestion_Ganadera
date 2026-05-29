# 🐄 Gestión Ganadera

Sistema web integral para la gestión de operaciones ganaderas. Permite administrar ganado, fincas, lotes, reproducción, sanidad, producción lechera y generar reportes con dashboards interactivos.

---

## 🏗️ Estructura del Proyecto (Monorepo)

```
Gestion_Ganadera/
├── backend/                     ← Spring Boot API (Java 21, Maven)
│   ├── src/main/java/.../backend/
│   │   ├── config/              # Seguridad, JWT, CORS
│   │   ├── controller/          # REST Controllers
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── model/               # Entidades JPA
│   │   ├── repository/          # Spring Data JPA
│   │   ├── service/             # Lógica de negocio
│   │   └── util/                # Utilidades (JWT, FileUpload)
│   ├── Dockerfile               # Container para deploy
│   ├── pom.xml                  # Dependencias Maven
│   └── .env.example             # Variables de entorno
│
├── frontend/                    ← 🌐 Submódulo → Masterkillerr/Gestion_Ganadera_Front
│   └── (React + Vite + TailwindCSS)
│       git clone requiere: git submodule init && git submodule update
│
├── docs/                        ← 📚 Documentación
│   ├── ARQUITECTURA.md          # Arquitectura del sistema
│   ├── DESIGN.md                # Decisiones de diseño
│   ├── CODE_REVIEW.md           # Historial de code reviews
│   └── Tarea.md                 # Enunciado del proyecto
│
├── infra/                       ← ⚙️ Infraestructura
│   └── gestion.ddl              # Esquema de base de datos (PostgreSQL)
│
├── .github/workflows/ci.yml     ← CI/CD (GitHub Actions)
├── LICENSE
└── README.md
```

---

## 🛠️ Stack Tecnológico

### Backend
| Tecnología | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.x |
| Spring Security + JWT | — |
| Spring Data JPA (Hibernate) | — |
| PostgreSQL | — |
| Maven | — |
| Lombok | — |

### Frontend
| Tecnología | Versión |
|---|---|
| React | 19.x |
| Vite | — |
| TailwindCSS | — |
| Axios | — |
| React Router | — |
| Recharts | — |

### Infraestructura
- **CI/CD:** GitHub Actions (test + build)
- **Backend:** AWS Elastic Beanstalk (Java 21, Corretto)
- **Frontend:** AWS S3 (static hosting) + CloudFront CDN
- **Base de datos:** AWS RDS PostgreSQL

---

## 🚀 Inicio Rápido

### Opción 1: Ejecución Directa (Recomendada para desarrollo)

#### Prerrequisitos
- Java 21+
- Maven
- PostgreSQL (local o remota)
- Node.js 20+
- Git

#### Backend

```bash
cd backend
mvn clean install -DskipTests
mvn spring-boot:run
```

La API estará disponible en `http://localhost:8080/api`.

#### Frontend

```bash
cd frontend
npm install
npm run dev
```

La app estará disponible en `http://localhost:5173`.

### Opción 2: Ejecución con Docker

```bash
cd backend
# Copiar .env a .env si no existe
docker build -t gestion-ganadera-backend .
docker run -p 8080:8080 --env-file .env gestion-ganadera-backend
```

---

## 🔐 Autenticación

El sistema usa **JWT (JSON Web Tokens)** almacenados en localStorage. Los endpoints protegidos requieren el header:

```
Authorization: Bearer <token>
```

### Roles
- **ADMIN** — Acceso completo al sistema, gestión de usuarios
- **OPERADOR** — Gestión de ganado, sanidad, reproducción, consultas

---

## 📦 Módulos del Sistema

| Módulo | Descripción |
|---|---|
| **Autenticación** | Login/registro con JWT + reCAPTCHA |
| **Usuarios** | CRUD de usuarios con roles |
| **Fincas y Lotes** | Organización del terreno |
| **Ganado** | Registro individual con arete, raza, genealogía |
| **Reproducción** | Montas, partos, cálculo de fecha estimada |
| **Sanidad** | Tratamientos, vacunaciones, alertas |
| **Producción** | Registro diario de leche, estadísticas |
| **Movimientos** | Trazabilidad de traslados entre lotes |
| **Dashboard** | Gráficos, KPIs, alertas del sistema |

---

## 🌐 Deploy

### Backend (AWS Elastic Beanstalk)
El backend se despliega en **Elastic Beanstalk** (Amazon Linux 2023, Corretto 21). El entorno se llama `Gestionganaderabackend-env`.

1. Build del backend: `mvn clean package -DskipTests`
2. Deploy: Subir el `.jar` generado en `target/` a Elastic Beanstalk (EB CLI o consola AWS).

El servicio está disponible en:
- **URL**: `https://Gestionganaderabackend-env.eba-kmujbtjg.us-east-2.elasticbeanstalk.com/api`
- **Health**: Monitorear desde EB Dashboard.

### Frontend (AWS S3 + CloudFront)
El frontend (React build) se sirve desde un bucket S3 privado con CloudFront como CDN.

1. Build: `npm run build` en la carpeta `frontend/`
2. Sincronizar: `aws s3 sync dist/ s3://gestion-ganadera-frontend --delete --region us-east-2`
3. Invalidar cache CloudFront (opcional): `aws cloudfront create-invalidation --distribution-id E36X49KRLGV2TK --paths "/*"`

La app está disponible en: `https://d3gw8tv95pui9q.cloudfront.net`

### CI/CD
GitHub Actions (`.github/workflows/ci.yml`) ejecuta tests automáticamente en cada push a `main`/`master`. El deploy a AWS es manual actualmente.

---

## 📚 Documentación

- **[ARQUITECTURA.md](docs/ARQUITECTURA.md)** — Arquitectura completa, modelo de datos, plan de desarrollo
- **[DESIGN.md](docs/DESIGN.md)** — Decisiones técnicas y patrones de diseño
- **[CODE_REVIEW.md](docs/CODE_REVIEW.md)** — Historial de auditorías de código
- **[gestion.ddl](infra/gestion.ddl)** — Esquema DDL de la base de datos

---

## 🧪 Tests

```bash
cd backend
mvn test                    # Todos los tests
mvn test -Dtest=AnimalControllerTest  # Test específico
```

---

## 📄 Licencia

Este proyecto es parte de un trabajo académico. Ver `LICENSE` para más detalles.
