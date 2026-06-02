#!/bin/bash
set -e

# Color output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}🚀 Gestion Ganadera - Docker Deployment${NC}"
echo ""

# Check Docker
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker is not installed${NC}"
    echo "Install Docker: https://docs.docker.com/get-docker/"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}❌ Docker Compose is not installed${NC}"
    echo "Install Docker Compose: https://docs.docker.com/compose/install/"
    exit 1
fi

echo -e "${GREEN}✓ Docker and Docker Compose are installed${NC}"
echo ""

# Check if .env file exists
if [ ! -f .env ]; then
    echo -e "${YELLOW}⚠️  .env file not found. Creating one...${NC}"
    cat > .env << EOF
# Database
DB_PASSWORD=secure_password_change_me

# JWT Secret (minimum 32 characters)
JWT_SECRET=your-super-secret-jwt-key-minimum-32-characters-long

# CORS Origins
CORS_ORIGINS=http://localhost:3000,http://localhost:5173

# Optional: reCAPTCHA (leave empty if not configured)
VITE_RECAPTCHA_SITE_KEY=

# API URL for frontend
VITE_API_URL=http://localhost:8080/api
EOF
    echo -e "${YELLOW}Created .env file. Please review and update sensitive values:${NC}"
    cat .env
    echo ""
    read -p "Press Enter to continue..."
fi

# Stop existing containers
echo ""
echo -e "${YELLOW}Stopping existing containers...${NC}"
docker-compose down --remove-orphans 2>/dev/null || true

# Build images
echo ""
echo -e "${YELLOW}Building Docker images...${NC}"
docker-compose build --no-cache

# Start services
echo ""
echo -e "${YELLOW}Starting services...${NC}"
docker-compose up -d

# Wait for services to be ready
echo ""
echo -e "${YELLOW}Waiting for services to be healthy...${NC}"
sleep 10

# Check PostgreSQL
echo -e "${YELLOW}Checking PostgreSQL...${NC}"
if docker-compose exec -T postgres pg_isready -U postgres > /dev/null 2>&1; then
    echo -e "${GREEN}✓ PostgreSQL is ready${NC}"
else
    echo -e "${RED}✗ PostgreSQL failed to start${NC}"
    docker-compose logs postgres
    exit 1
fi

# Check Backend
echo -e "${YELLOW}Checking Backend API...${NC}"
if docker-compose exec -T backend curl -s http://localhost:8080/api/health > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Backend API is ready${NC}"
else
    echo -e "${YELLOW}⏳ Backend still starting, waiting...${NC}"
    sleep 10
    if docker-compose exec -T backend curl -s http://localhost:8080/api/health > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Backend API is ready${NC}"
    else
        echo -e "${RED}✗ Backend failed to start${NC}"
        docker-compose logs backend
        exit 1
    fi
fi

# Summary
echo ""
echo -e "${GREEN}════════════════════════════════════════${NC}"
echo -e "${GREEN}✅ DEPLOYMENT SUCCESSFUL!${NC}"
echo -e "${GREEN}════════════════════════════════════════${NC}"
echo ""
echo "📍 Services running at:"
echo "   🔌 Backend API:  http://localhost:8080/api"
echo "   🌐 Frontend:     http://localhost:5173"
echo "   🗄️  PostgreSQL:   localhost:5432"
echo ""
echo "📊 Management:"
echo "   View logs:       docker-compose logs -f"
echo "   View specific:   docker-compose logs -f backend"
echo "   Stop services:   docker-compose down"
echo "   Stop & remove:   docker-compose down -v"
echo ""
echo "🔗 First login:"
echo "   Email:    admin@example.com"
echo "   Password: Check database or registration flow"
echo ""
echo "📚 Documentation:"
echo "   Deployment:  cat DEPLOY_LOCAL.md"
echo "   Build info:  cat BUILD_VERIFICATION.md"
echo ""
