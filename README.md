# 🐄 Gestión Ganadera

**Production-ready livestock management system** with comprehensive security, multi-tenant isolation, and professional UX/accessibility.

> *Modern web application for agricultural operations management*

---

## ✨ Key Features

- **Livestock Management**: Track animals, genealogy, breeding history
- **Farm Operations**: Manage fincas (farms), lotes (lots), movements
- **Health Tracking**: Veterinary treatments, vaccinations, health alerts
- **Production Records**: Milk production metrics, daily statistics
- **Interactive Dashboard**: Real-time KPIs, analytics, system alerts
- **Multi-User System**: Secure user isolation with role-based access
- **Mobile Responsive**: Works on desktop, tablet, and mobile devices
- **WCAG 2.1 AA**: Fully accessible web application
- **Production Security**: JWT auth, rate limiting, HTTPS, exception handling

---

## 🏗️ Architecture

### **Monorepo Structure**

```
Gestion_Ganadera/
├── backend/                          # Java Spring Boot API
│   ├── src/main/java/...            # REST controllers, services, models
│   ├── src/main/resources/
│   │   ├── db/migration/            # Flyway database migrations
│   │   ├── application.yml          # Application configuration
│   │   └── ...
│   ├── pom.xml                      # Maven dependencies
│   ├── Dockerfile                   # Container image
│   ├── HTTPS_SETUP.md               # Production security guide
│   └── .env.example                 # Environment template
│
├── frontend/                         # React 19 + Vite application
│   ├── src/
│   │   ├── components/              # Reusable UI components
│   │   ├── pages/                   # Route pages
│   │   ├── services/                # API client
│   │   ├── hooks/                   # Custom React hooks
│   │   └── ...
│   ├── package.json
│   ├── vite.config.js
│   ├── tailwind.config.js
│   ├── README.md
│   ├── ACCESSIBILITY_GUIDE.md
│   └── DESIGN.md
│
├── docs/                            # Project documentation
│   ├── ARQUITECTURA.md              # System architecture
│   ├── DESIGN.md                    # Design decisions
│   ├── CODE_REVIEW.md               # Audit history
│   └── SWAGGER_IMPLEMENTATION.md
│
├── infra/
│   └── gestion.ddl                 # Database schema (reference)
│
├── .github/workflows/
│   └── deploy.yml                  # CI/CD pipeline (GitHub Actions)
│
├── FIX_ALL_SUMMARY.md             # Security & UX remediation report
└── README.md                       # This file
```

---

## 🚀 Technology Stack

### **Backend**
| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 21 | Language |
| **Spring Boot** | 4.0.6 | Web framework |
| **Spring Security** | Latest | Authentication & authorization |
| **Spring Data JPA** | Latest | ORM & database access |
| **PostgreSQL** | 14+ | Relational database |
| **JWT** | 0.13.0 | Token-based auth |
| **Bucket4j** | 7.6.0 | Rate limiting |
| **Flyway** | Latest | Database migrations |
| **Swagger/OpenAPI** | 3.0 | API documentation |

### **Frontend**
| Technology | Purpose |
|------------|---------|
| **React 19** | UI framework |
| **Vite** | Build tool & dev server |
| **TailwindCSS** | Utility-first styling |
| **React Router** | Client-side routing |
| **Axios** | HTTP client |
| **React Hook Form** | Form handling |
| **Zod** | Schema validation |
| **Recharts** | Data visualization |

### **Infrastructure**
- **AWS Elastic Beanstalk**: Backend hosting
- **AWS S3 + CloudFront**: Frontend & static content
- **AWS RDS**: PostgreSQL database
- **GitHub Actions**: CI/CD pipeline
- **AWS Certificate Manager**: SSL/TLS certificates

---

## 🔐 Security Features

### **Authentication & Authorization**
✅ JWT-based stateless authentication  
✅ Token blacklist for logout  
✅ Strong password requirements (12 chars + complexity)  
✅ Multi-layer authorization (controller + service + DB)  
✅ User data isolation (multi-tenant)  

### **Network Security**
✅ HTTPS enforcement (HTTP → HTTPS redirect)  
✅ HSTS (HTTP Strict-Transport-Security)  
✅ CSP (Content-Security-Policy) headers  
✅ XSS protection headers  
✅ Rate limiting on auth endpoints (5 req/min)  
✅ CORS restricted to allowed origins  

### **Application Security**
✅ SQL injection prevention (JPQL queries)  
✅ Professional error responses (no stack traces)  
✅ Input validation on all endpoints  
✅ Exception handling with structured logging  
✅ Password reset with token expiry  
✅ reCAPTCHA on registration  

### **Database Security**
✅ Parameterized queries  
✅ Foreign key constraints  
✅ User isolation via indexes  
✅ Database backups (AWS RDS)  

---

## ♿ Accessibility

**WCAG 2.1 Level AA Compliant** - The application meets professional accessibility standards:

- **Touch Targets**: 44×44px minimum (mobile-friendly)
- **Color Contrast**: 4.5:1 ratio for text
- **Keyboard Navigation**: Full support, Tab-focused
- **Screen Readers**: Compatible with NVDA, JAWS, VoiceOver
- **Focus Indicators**: Bright, visible (2px outline + glow)
- **Skip Links**: Jump to main content
- **Form Labels**: Associated with inputs
- **Aria Labels**: Semantic HTML with proper labels
- **Dark Mode**: Glassmorphic design with accessible colors

See [`frontend/ACCESSIBILITY_GUIDE.md`](frontend/ACCESSIBILITY_GUIDE.md) for implementation details.

---

## 📊 Database Schema

**28 tables** covering:
- User management & authentication
- Farm (finca) & lot (lote) hierarchy
- Livestock individual records
- Breeding & reproductive data
- Health & veterinary treatment records
- Production metrics (milk, etc.)
- Movement tracking & traceability
- Reference catalogs (breeds, statuses, etc.)

See [`infra/gestion.ddl`](infra/gestion.ddl) for full schema and [`docs/ARQUITECTURA.md`](docs/ARQUITECTURA.md) for entity relationships.

---

## 🚀 Quick Start

### **Prerequisites**
- Java 21+
- Node.js 20+
- PostgreSQL 14+
- Maven
- AWS CLI (for deployment)

### **Local Development**

#### **1. Backend**
```bash
cd backend

# Create .env file
cp .env.example .env
# Edit .env with your database credentials

# Install dependencies & run
mvn clean install
mvn spring-boot:run
```

Backend runs on `http://localhost:8080/api`

#### **2. Frontend**
```bash
cd frontend

# Install dependencies
npm install

# Create .env
echo 'VITE_API_URL=http://localhost:8080/api' > .env

# Start dev server
npm run dev
```

Frontend runs on `http://localhost:5173`

#### **3. Database**
```bash
# Create database
psql -U postgres -c "CREATE DATABASE gestion_ganadera;"

# Run migrations (automatic with Spring Boot)
# Flyway migrations apply on startup
```

---

## 📖 Documentation

| Document | Purpose |
|----------|---------|
| [`docs/ARQUITECTURA.md`](docs/ARQUITECTURA.md) | Complete system architecture, entity relationships, deployment plan |
| [`docs/DESIGN.md`](docs/DESIGN.md) | Technical design decisions and patterns |
| [`docs/CODE_REVIEW.md`](docs/CODE_REVIEW.md) | Code quality & security audit history |
| [`frontend/README.md`](frontend/README.md) | Frontend-specific setup and development |
| [`frontend/ACCESSIBILITY_GUIDE.md`](frontend/ACCESSIBILITY_GUIDE.md) | WCAG 2.1 compliance guidelines |
| [`backend/HTTPS_SETUP.md`](backend/HTTPS_SETUP.md) | Production security & HTTPS configuration |
| [`FIX_ALL_SUMMARY.md`](FIX_ALL_SUMMARY.md) | Security & UX remediation completed |

---

## 🧪 Testing

### **Backend**
```bash
cd backend
mvn test                           # Run all tests
mvn test -Dtest=AnimalControllerTest  # Specific test
```

### **Frontend**
```bash
cd frontend
npm run test                       # Run unit tests
npm run test:watch               # Watch mode
npm run test:coverage            # Coverage report
```

---

## 🌐 API Documentation

Interactive API docs available at:
- **Local**: `http://localhost:8080/api/swagger-ui.html`
- **Production**: `https://api.yourdomain.com/api/swagger-ui.html`

All endpoints documented with OpenAPI/Swagger.

---

## 🚢 Deployment

### **Production Checklist**

#### **Backend (AWS Elastic Beanstalk)**
```bash
cd backend
mvn clean package -DskipTests
# Upload target/backend-1.0.0.jar to Elastic Beanstalk
```

#### **Frontend (AWS S3 + CloudFront)**
```bash
cd frontend
npm run build
aws s3 sync dist/ s3://your-bucket --delete
aws cloudfront create-invalidation --distribution-id DIST_ID --paths "/*"
```

See:
- [`backend/HTTPS_SETUP.md`](backend/HTTPS_SETUP.md) - Complete production security guide
- [`.github/workflows/deploy.yml`](.github/workflows/deploy.yml) - Automated CI/CD pipeline

---

## 🔑 Environment Variables

### **Backend (.env)**
```env
# Database
DB_URL=jdbc:postgresql://localhost:5432/gestion_ganadera
DB_USER=postgres
DB_PASSWORD=your-password

# JWT
JWT_SECRET=your-very-long-random-secret-key-at-least-32-chars

# reCAPTCHA
RECAPTCHA_SECRET=your-recaptcha-secret

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:5173,https://yourdomain.com
```

### **Frontend (.env)**
```env
VITE_API_URL=http://localhost:8080/api
VITE_JWT_EXPIRES_IN=86400
```

---

## 📊 Metrics & Quality

| Aspect | Status |
|--------|--------|
| **Security Audit** | ✅ Complete - All critical issues fixed |
| **Accessibility** | ✅ WCAG 2.1 Level AA compliant |
| **Test Coverage** | ✅ 48 integration tests, unit tests |
| **API Documentation** | ✅ OpenAPI/Swagger coverage |
| **Database Migrations** | ✅ Flyway version control |
| **CI/CD Pipeline** | ✅ GitHub Actions automated testing & deployment |
| **HTTPS Ready** | ✅ Security headers configured |
| **Rate Limiting** | ✅ Auth endpoints protected |

---

## 🤝 Contributing

1. Create a feature branch (`git checkout -b feature/amazing-feature`)
2. Commit changes (`git commit -m 'Add amazing feature'`)
3. Push to branch (`git push origin feature/amazing-feature`)
4. Open a Pull Request

---

## 📝 License

This project is part of an academic livestock management system.

---

## 🆘 Support

For issues, feature requests, or questions:
1. Check existing documentation
2. Review [`docs/`](docs/) for architecture & design
3. Check [`FIX_ALL_SUMMARY.md`](FIX_ALL_SUMMARY.md) for known improvements

---

## 🎯 Roadmap

### **Completed ✅**
- Multi-tenant isolation
- JWT authentication & logout
- Rate limiting
- HTTPS enforcement
- WCAG 2.1 AA accessibility
- Custom exception handling
- Password reset infrastructure

### **In Progress ⏳**
- Password reset email notifications
- Advanced analytics & reporting
- Mobile app (React Native)

### **Planned 🗺️**
- Two-factor authentication (2FA)
- Advanced search & filtering
- Data export (CSV/PDF)
- User activity audit logs

---

**Last Updated:** 2026-06-01  
**Status:** 🟢 Production Ready

