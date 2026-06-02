# 🐳 Local Deployment with Docker

## Quick Start (5 minutes)

### Prerequisites
- Docker
- Docker Compose
- Git

### Step 1: Clone & Navigate
```bash
cd /home/alvaro/Gestion_Ganadera
```

### Step 2: Create Environment File
```bash
cat > .env << EOF
DB_PASSWORD=secure_password_here
JWT_SECRET=your-jwt-secret-key-min-32-chars
CORS_ORIGINS=http://localhost:3000,http://localhost:5173
EOF
```

### Step 3: Start Services
```bash
docker-compose up -d
```

This starts:
- ✅ PostgreSQL (port 5432)
- ✅ Backend API (port 8080)
- ✅ Frontend dev server (port 5173)

### Step 4: Verify Deployment
```bash
# Check backend health
curl http://localhost:8080/api/health

# Check PostgreSQL
docker-compose exec postgres psql -U postgres -d gestion_ganadera -c "SELECT COUNT(*) FROM usuario;"

# View logs
docker-compose logs -f backend
```

---

## Production Deployment

### Option 1: Docker Hub (Recommended)

```bash
# Build and push backend image
docker build -t yourusername/gestion-ganadera-backend:latest ./backend
docker push yourusername/gestion-ganadera-backend:latest

# Run on production server
docker run -d \
  --name gestion-ganadera-backend \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db:5432/gestion_ganadera \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=$DB_PASSWORD \
  -e APP_JWT_SECRET=$JWT_SECRET \
  -p 8080:8080 \
  yourusername/gestion-ganadera-backend:latest
```

### Option 2: Heroku

```bash
# Login to Heroku
heroku login

# Create app
heroku create gestion-ganadera-backend

# Set environment variables
heroku config:set -a gestion-ganadera-backend \
  SPRING_DATASOURCE_URL=postgres://... \
  APP_JWT_SECRET=$JWT_SECRET

# Add Heroku Procfile
echo "web: java -jar target/backend-1.0.0.jar" > Procfile

# Deploy
git push heroku master
```

### Option 3: DigitalOcean App Platform

```bash
# Create app.yaml
cat > app.yaml << 'EOF'
name: gestion-ganadera
services:
  - name: api
    github:
      repo: your-github/Gestion_Ganadera
      branch: master
    build_command: cd backend && mvn clean package -DskipTests
    run_command: java -jar backend/target/backend-1.0.0.jar
    envs:
      - key: SPRING_DATASOURCE_URL
        value: ${db.connection_string}
      - key: APP_JWT_SECRET
        value: ${JWT_SECRET}

databases:
  - name: db
    engine: PG
    version: "16"
EOF

# Deploy
doctl apps create --spec app.yaml
```

### Option 4: AWS EC2 (Manual)

```bash
# SSH to instance
ssh -i key.pem ubuntu@ec2-instance

# Install Docker & Docker Compose
sudo apt update && sudo apt install -y docker.io docker-compose

# Clone repository
git clone https://github.com/yourusername/Gestion_Ganadera.git
cd Gestion_Ganadera

# Create environment file
echo "DB_PASSWORD=..." > .env
echo "JWT_SECRET=..." >> .env

# Start services
docker-compose up -d

# View logs
docker-compose logs -f
```

---

## Troubleshooting

### Backend won't start
```bash
# Check logs
docker-compose logs backend

# Rebuild image
docker-compose build --no-cache backend

# Restart
docker-compose restart backend
```

### Database connection error
```bash
# Verify PostgreSQL is running
docker-compose ps

# Check PostgreSQL logs
docker-compose logs postgres

# Connect to database
docker-compose exec postgres psql -U postgres -d gestion_ganadera
```

### Port already in use
```bash
# Change ports in docker-compose.yml
# Or kill process using port:
lsof -i :8080
kill -9 <PID>
```

---

## Monitoring

### View Logs
```bash
# All services
docker-compose logs -f

# Just backend
docker-compose logs -f backend

# Just database
docker-compose logs -f postgres
```

### Container Stats
```bash
docker stats gestion_ganadera_backend
```

### Database Backup
```bash
docker-compose exec postgres pg_dump -U postgres gestion_ganadera > backup.sql
```

### Database Restore
```bash
docker-compose exec -T postgres psql -U postgres gestion_ganadera < backup.sql
```

---

## Cleanup

```bash
# Stop all services
docker-compose down

# Remove volumes (database data)
docker-compose down -v

# Remove images
docker-compose down --rmi all
```

---

## Environment Variables Reference

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_PASSWORD` | `postgres` | PostgreSQL password |
| `JWT_SECRET` | 32-char key | JWT signing secret (min 32 chars) |
| `CORS_ORIGINS` | `http://localhost:3000` | Allowed CORS origins |
| `SPRING_DATASOURCE_URL` | Auto-set | Database URL (auto from docker-compose) |
| `LOGGING_LEVEL_ROOT` | `INFO` | Root log level |
| `LOGGING_LEVEL_COM_GESTIONGANADERA_BACKEND` | `DEBUG` | App log level |

---

## Next Steps

1. **Testing**: Run integration tests against running containers
   ```bash
   docker-compose up -d
   mvn test -Dspring.datasource.url=jdbc:postgresql://localhost:5432/gestion_ganadera
   ```

2. **SSL/TLS**: Add reverse proxy (nginx) in front of backend
   ```yaml
   nginx:
     image: nginx:alpine
     ports:
       - "443:443"
     volumes:
       - ./nginx.conf:/etc/nginx/nginx.conf
       - ./certs:/etc/nginx/certs
   ```

3. **Monitoring**: Add Prometheus & Grafana
   ```yaml
   prometheus:
     image: prom/prometheus
     volumes:
       - ./prometheus.yml:/etc/prometheus/prometheus.yml
   
   grafana:
     image: grafana/grafana
     ports:
       - "3000:3000"
   ```

---

**Status:** ✅ Ready to deploy  
**Version:** 2026-06-02  
**For Issues:** Check Docker logs or contact support
