# ⚡ Quick Start - Gestion Ganadera

**Deploy in 2 minutes with Docker** 🐳

## Prerequisites

- Docker: https://docs.docker.com/get-docker/
- Docker Compose: https://docs.docker.com/compose/install/

## 1. One-Command Deployment

```bash
cd /home/alvaro/Gestion_Ganadera
chmod +x deploy.sh
./deploy.sh
```

That's it! The script will:
- ✅ Check Docker installation
- ✅ Create `.env` file with safe defaults
- ✅ Build all images
- ✅ Start all services
- ✅ Verify everything is healthy

## 2. Manual Deployment (3 steps)

```bash
# Step 1: Create environment file
cat > .env << EOF
DB_PASSWORD=your_password_here
JWT_SECRET=your-secret-key-min-32-chars
CORS_ORIGINS=http://localhost:3000,http://localhost:5173
EOF

# Step 2: Start services
docker-compose up -d

# Step 3: Verify
curl http://localhost:8080/api/health
```

## Access the Application

| Service | URL | Port |
|---------|-----|------|
| **API** | http://localhost:8080/api | 8080 |
| **Frontend** | http://localhost:5173 | 5173 |
| **Database** | localhost:5432 | 5432 |

## First Login

```
Email:    admin@test.com
Password: (created during registration)
```

Or create a test user via the API:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Test User",
    "email": "test@example.com",
    "password": "TestPassword123!@#"
  }'
```

## Common Commands

```bash
# View logs
docker-compose logs -f

# View backend logs only
docker-compose logs -f backend

# Stop services
docker-compose down

# Remove everything (including data)
docker-compose down -v

# Restart backend
docker-compose restart backend

# Connect to database
docker-compose exec postgres psql -U postgres -d gestion_ganadera

# Backup database
docker-compose exec postgres pg_dump -U postgres gestion_ganadera > backup.sql

# View running containers
docker-compose ps
```

## Troubleshooting

### Backend won't start
```bash
docker-compose logs backend
docker-compose restart backend
```

### Database connection error
```bash
docker-compose logs postgres
# Check DB is running:
docker-compose exec postgres pg_isready
```

### Port already in use
Edit `docker-compose.yml` and change port numbers, then restart.

## Full Documentation

- **Detailed Deployment**: `DEPLOY_LOCAL.md`
- **Build Information**: `BUILD_VERIFICATION.md`
- **API Documentation**: `README.md`
- **Security Setup**: `backend/HTTPS_SETUP.md`
- **Logging Guide**: `backend/LOGGING_GUIDE.md`

## Environment Variables

| Variable | Default | Purpose |
|----------|---------|---------|
| `DB_PASSWORD` | `postgres` | PostgreSQL password |
| `JWT_SECRET` | `change-me` | JWT signing key (32+ chars) |
| `CORS_ORIGINS` | `localhost:*` | Allowed CORS origins |
| `VITE_API_URL` | `http://localhost:8080/api` | Frontend API URL |

## Production Deployment

Ready to deploy to production? See `DEPLOY_LOCAL.md` for:
- ☁️ AWS EC2
- 🚀 Heroku
- 🌊 DigitalOcean
- 🐳 Docker Hub
- 📦 Any Docker-compatible host

## Next Steps

1. **Register a user** and explore the application
2. **Check the API** at http://localhost:8080/api/swagger-ui.html
3. **View logs** to understand how it works
4. **Deploy to production** when ready

## Support

If you encounter issues:
1. Check `docker-compose logs -f`
2. Read `DEPLOY_LOCAL.md` troubleshooting section
3. Verify environment variables in `.env`
4. Check Docker is running: `docker ps`

---

**Status:** ✅ Ready to use  
**Version:** 2026-06-02  
**Stack:** Java 21 + Spring Boot 4.0.6 + React 19 + PostgreSQL 16
