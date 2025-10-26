#!/bin/bash

# Books API - Deployment Script
# This script automates the deployment process to your Digital Ocean droplet

set -e  # Exit on any error

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Configuration
REMOTE_USER=${REMOTE_USER:-root}
REMOTE_HOST=${REMOTE_HOST:-""}
REMOTE_PATH=${REMOTE_PATH:-"/opt/books-api"}
BRANCH=${BRANCH:-main}

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Books API Deployment Script${NC}"
echo -e "${GREEN}========================================${NC}"

# Check if REMOTE_HOST is set
if [ -z "$REMOTE_HOST" ]; then
    echo -e "${RED}Error: REMOTE_HOST is not set${NC}"
    echo "Usage: REMOTE_HOST=your-droplet-ip ./deploy.sh"
    echo "Or: export REMOTE_HOST=your-droplet-ip && ./deploy.sh"
    exit 1
fi

echo -e "${YELLOW}Deploying to: ${REMOTE_USER}@${REMOTE_HOST}${NC}"
echo -e "${YELLOW}Remote path: ${REMOTE_PATH}${NC}"
echo -e "${YELLOW}Branch: ${BRANCH}${NC}"
echo ""

# Function to run commands on remote server
remote_exec() {
    ssh ${REMOTE_USER}@${REMOTE_HOST} "$@"
}

# Step 1: Check connection
echo -e "${GREEN}[1/6] Testing SSH connection...${NC}"
if ! remote_exec "echo 'Connection successful'"; then
    echo -e "${RED}Failed to connect to remote server${NC}"
    exit 1
fi

# Step 2: Pull latest code
echo -e "${GREEN}[2/6] Pulling latest code from Git...${NC}"
remote_exec "cd ${REMOTE_PATH} && git pull origin ${BRANCH}"

# Step 3: Stop running containers
echo -e "${GREEN}[3/6] Stopping running containers...${NC}"
remote_exec "cd ${REMOTE_PATH} && docker-compose down" || true

# Step 4: Build new images
echo -e "${GREEN}[4/6] Building new Docker images...${NC}"
remote_exec "cd ${REMOTE_PATH} && docker-compose build --no-cache"

# Step 5: Start containers
echo -e "${GREEN}[5/6] Starting containers...${NC}"
remote_exec "cd ${REMOTE_PATH} && docker-compose up -d"

# Step 6: Check status
echo -e "${GREEN}[6/6] Checking deployment status...${NC}"
sleep 5
remote_exec "cd ${REMOTE_PATH} && docker-compose ps"

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Deployment completed successfully!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "${YELLOW}Useful commands:${NC}"
echo "  View logs:    ssh ${REMOTE_USER}@${REMOTE_HOST} 'cd ${REMOTE_PATH} && docker-compose logs -f'"
echo "  Check status: ssh ${REMOTE_USER}@${REMOTE_HOST} 'cd ${REMOTE_PATH} && docker-compose ps'"
echo "  Restart:      ssh ${REMOTE_USER}@${REMOTE_HOST} 'cd ${REMOTE_PATH} && docker-compose restart'"
echo ""
