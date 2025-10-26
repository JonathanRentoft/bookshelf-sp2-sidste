#!/bin/bash
#
# Emergency Deployment Script for Server
# Run this on your server at /opt/books-api
#

set -e

echo "🚀 Books API - Emergency Deployment"
echo "===================================="
echo ""

# Navigate to project directory
cd /opt/books-api

echo "📝 Step 1: Resolving git conflicts..."
# Save any local changes (in case you edited .env or docker-compose.yml manually)
git stash

echo "📥 Step 2: Pulling latest code from GitHub..."
git pull origin main

echo "🔧 Step 3: Ensuring .env file is configured..."
# Check if .env exists
if [ ! -f .env ]; then
    echo "❌ ERROR: .env file not found!"
    echo "Creating .env file with default values..."
    echo "⚠️  YOU MUST EDIT THIS FILE WITH YOUR DOCKER USERNAME!"
    cat > .env << 'EOF'
# Docker Configuration
DOCKER_USERNAME=jonathan0912000

# Database Configuration
DB_NAME=books_db
DB_USERNAME=postgres
DB_PASSWORD=postgres
DB_PORT=5432

# Application Configuration
API_PORT=7070

# JWT/Security Configuration
SECRET_KEY=841D8A6C80CBA4FCAD32D5367C18C53B
ISSUER=books.api
TOKEN_EXPIRATION_TIME=3600000

# Deployment Flag
DEPLOYED=true
EOF
    echo "✅ .env file created. Please review it!"
    echo "Edit with: nano .env"
    echo ""
else
    echo "✅ .env file exists"
    echo "Current DOCKER_USERNAME: $(grep DOCKER_USERNAME .env || echo 'NOT SET')"
    echo ""
fi

echo "🛑 Step 4: Stopping all containers..."
docker-compose down -v

echo "🐳 Step 5: Pulling latest image from Docker Hub..."
docker-compose pull books-api

echo "🚀 Step 6: Starting services..."
docker-compose up -d

echo ""
echo "⏳ Waiting 5 seconds for services to start..."
sleep 5

echo ""
echo "📊 Step 7: Checking service status..."
docker-compose ps

echo ""
echo "📋 Step 8: Showing application logs..."
echo "Press Ctrl+C to exit log view"
echo "================================"
docker-compose logs -f books-api
