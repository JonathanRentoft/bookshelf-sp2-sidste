#!/bin/bash

# Books API - Docker Hub Deployment Script
# This script deploys the application using images from Docker Hub

set -e

echo "================================================"
echo "Books API - Docker Hub Deployment"
echo "================================================"
echo ""

# Check if .env file exists
if [ ! -f .env ]; then
    echo "❌ Error: .env file not found!"
    echo "Please create a .env file with your configuration."
    echo "You can copy .env.example and update the values:"
    echo "  cp .env.example .env"
    exit 1
fi

# Source the .env file to check DOCKER_USERNAME
set -a
source .env
set +a

# Check if DOCKER_USERNAME is set
if [ -z "$DOCKER_USERNAME" ] || [ "$DOCKER_USERNAME" = "your-dockerhub-username" ]; then
    echo "❌ Error: DOCKER_USERNAME not set in .env file!"
    echo "Please update your .env file with your Docker Hub username."
    exit 1
fi

echo "📦 Configuration:"
echo "  Docker Hub User: $DOCKER_USERNAME"
echo "  Database: $DB_NAME"
echo "  API Port: ${API_PORT:-7070}"
echo ""

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check for required commands
if ! command_exists docker; then
    echo "❌ Error: docker is not installed!"
    exit 1
fi

if ! command_exists docker-compose; then
    echo "❌ Error: docker-compose is not installed!"
    exit 1
fi

# Pull the latest configuration from git (if in a git repo)
if [ -d .git ]; then
    echo "🔄 Pulling latest configuration from git..."
    git pull origin main 2>/dev/null || git pull origin master 2>/dev/null || echo "⚠️  Could not pull from git (skipping)"
    echo ""
fi

# Stop existing containers
echo "🛑 Stopping existing containers..."
docker-compose down
echo ""

# Pull the latest images from Docker Hub
echo "📥 Pulling latest image from Docker Hub..."
docker-compose pull books-api
echo ""

# Start the services
echo "🚀 Starting services..."
docker-compose up -d
echo ""

# Wait for services to be healthy
echo "⏳ Waiting for services to start..."
sleep 5

# Check service status
echo ""
echo "📊 Service Status:"
docker-compose ps
echo ""

# Show logs
echo "📋 Recent logs:"
docker-compose logs --tail=50 books-api
echo ""

# Health check
echo "🏥 Health check..."
sleep 2

if curl -f http://localhost:${API_PORT:-7070}/api/v1/routes >/dev/null 2>&1; then
    echo "✅ Application is healthy and running!"
    echo ""
    echo "🎉 Deployment successful!"
    echo ""
    echo "Access your API at: http://localhost:${API_PORT:-7070}/api/v1"
    echo ""
    echo "Useful commands:"
    echo "  View logs:         docker-compose logs -f books-api"
    echo "  View all logs:     docker-compose logs -f"
    echo "  Stop services:     docker-compose down"
    echo "  Restart service:   docker-compose restart books-api"
else
    echo "⚠️  Application may not be fully started yet."
    echo "Check logs with: docker-compose logs -f books-api"
fi

echo ""
echo "================================================"
