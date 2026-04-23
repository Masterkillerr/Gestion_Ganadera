# Sistema de Gestión Ganadera - Backend API 🐄

Backend API para el sistema de gestión ganadera. Este repositorio contiene exclusivamente el backend desarrollado con Spring Boot.

**Frontend:** [Gestion_Ganadera_Front](https://github.com/stevencardenas-dev/Gestion_Ganadera_Front)

## 🚀 Tecnologías

**Backend:**
- Java 17
- Spring Boot 3.5.13
- Spring Data JPA (Hibernate)
- Spring Security + JWT
- PostgreSQL
- Maven
- Lombok

**Frontend:** (en repo separado)
- React 18 + Vite
- TailwindCSS
- React Router DOM
- Axios
- React Hook Form + Zod
- Recharts

**Base de Datos:**
- PostgreSQL

## 📋 Requisitos Previos

- Java 17+
- Maven 3.6+
- PostgreSQL 14+
- (Opcional) Node.js 18+ para el frontend en su [repo separado](https://github.com/stevencardenas-dev/Gestion_Ganadera_Front)

## 🛠️ Instalación y Ejecución

### 1. Clonar el repositorio

```bash
git clone git@github.com:Masterkillerr/Gestion_Ganadera.git
cd Gestion_Ganadera
```

### 2. Configurar Base de Datos

Crear una base de datos PostgreSQL:

```sql
CREATE DATABASE gestion_ganadera;
```

### 3. Configurar Backend

Editar `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gestion_ganadera
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password
jwt.secret=tu_jwt_secret_key
```

### 4. Ejecutar Backend

```bash
cd backend
mvn spring-boot:run
```

El servidor se ejecutará en `http://localhost:8080`

### 5. Frontend (Repo Separado)

El frontend se encuentra en: https://github.com/stevencardenas-dev/Gestion_Ganadera_Front

```bash
git clone git@github.com:stevencardenas-dev/Gestion_Ganadera_Front.git
cd Gestion_Ganadera_Front
npm install
npm run dev
```

La aplicación frontend se ejecutará en `http://localhost:5173`

## 📁 Estructura del Proyecto

```
Gestion_Ganadera/
├── backend/
│   ├── src/main/java/com/gestionganadera/backend/
│   │   ├── config/          # SecurityConfig
│   │   ├── controller/      # Auth, Finca, Ganado, Lote, Usuario
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── model/           # Entidades JPA
│   │   ├── repository/      # Repositorios Spring Data
│   │   ├── service/         # Lógica de negocio
│   │   └── util/            # JWT, FileUpload
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
├── database/
│   └── init.sql
├── .github/workflows/
│   └── deploy.yml
├── ARQUITECTURA.md
├── DESIGN.md
└── README.md
```

## 🔐 Autenticación

El sistema utiliza JWT (JSON Web Tokens) para autenticación:
- Endpoint: `POST /api/auth/login`
- El token debe enviarse en el header: `Authorization: Bearer <token>`

## 👥 Roles de Usuario

- **Administrador**: Acceso completo al sistema
- **Operador/Veterinario**: Gestión de ganado, sanidad y reproducción

## 📚 Documentación

- [ARQUITECTURA.md](./ARQUITECTURA.md) - Detalles de arquitectura
- [DESIGN.md](./DESIGN.md) - Decisiones de diseño
- [Tarea.md](./Tarea.md) - Descripción de la tarea

## 🚀 Deploy

El proyecto utiliza GitHub Actions para CI/CD. Ver `.github/workflows/deploy.yml`

## 📝 Licencia

MIT
