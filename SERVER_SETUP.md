# Server Setup Guide

## Issue Resolution

Your deployment had two critical issues that have been fixed:

### 1. ❌ JAR Manifest Issue (Fixed)
**Problem**: `no main manifest attribute, in /app/app.jar`

**Solution**: Replaced maven-assembly-plugin with maven-shade-plugin in pom.xml. This creates a proper executable fat JAR with all dependencies and correct manifest.

### 2. ❌ Database Name Mismatch (Fixed)
**Problem**: App trying to connect to database "books" but PostgreSQL created "books_db"

**Solution**: Updated code to read DB_NAME from environment variable:
- `Populate.java` now reads `DB_NAME` env var
- `BookController.java` now reads `DB_NAME` env var
- Docker Compose properly passes `DB_NAME=books_db`

## Server Deployment Steps

### Step 1: Configure Server Environment

On your server at `/opt/books-api`, create or update the `.env` file:

```bash
cd /opt/books-api
nano .env
```

**CRITICAL**: Add this content (replace with your actual Docker Hub username):

```bash
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
```

### Step 2: Push Code to GitHub (Trigger Docker Hub Build)

From your **local machine** (not the server):

```bash
# Make sure you're on the correct branch
git status

# Push to trigger GitHub Actions
git push origin main
```

### Step 3: Wait for GitHub Actions

1. Go to your GitHub repository
2. Click on "Actions" tab
3. Wait for the build to complete (usually 2-5 minutes)
4. Verify the workflow shows a green checkmark ✅

### Step 4: Deploy on Server

Once GitHub Actions completes, run these commands on your server:

```bash
cd /opt/books-api

# Pull latest docker-compose.yml from git
git pull origin main

# Remove old containers and volumes
docker-compose down -v

# Pull the NEW image from Docker Hub (with fixes)
docker-compose pull books-api

# Start services
docker-compose up -d

# Watch logs
docker-compose logs -f books-api
```

### Step 5: Verify Deployment

You should see logs like this (indicating success):

```
books-api | INFO  org.hibernate.Version - HHH000412: Hibernate ORM core version
books-api | INFO  com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Starting...
books-api | INFO  com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Start completed.
books-api | INFO  org.hibernate.dialect.Dialect - HHH000400: Using dialect: org.hibernate.dialect.PostgreSQLDialect
books-api | INFO  o.h.e.t.j.p.i.JtaPlatformInitiator - HHH000490: Using JtaPlatform implementation
books-api | INFO  Javalin - Listening on http://localhost:7070/
books-api | INFO  Javalin - You are running Javalin
books-api | INFO  Javalin - Javalin started in XXXms
```

Test the API:

```bash
curl http://localhost:7070/api/v1/routes
curl http://localhost:7070/api/v1/books
```

## Troubleshooting

### If you still see "database does not exist":

```bash
# Check environment variables are loaded
docker-compose config | grep -A 5 "books-api:"

# Should show:
#   DB_NAME: books_db
#   POSTGRES_DB: books_db

# If not, verify .env file is correct and restart
docker-compose down -v
docker-compose up -d
```

### If you see "no main manifest attribute":

This means you're still using the old Docker image. The new image from GitHub Actions should fix this.

```bash
# Force pull new image
docker-compose pull books-api

# Check image digest
docker images jonathan0912000/books-api:latest

# The digest should match the latest build on Docker Hub
```

### If GitHub Actions fails:

1. Verify GitHub Secrets are set:
   - Go to Repository → Settings → Secrets and variables → Actions
   - Verify `DOCKER_USERNAME` and `DOCKER_PASSWORD` exist
   - If not, add them with your Docker Hub credentials

2. Check the Actions log for errors

### Low Memory Server Issues

Your server has only 512MB RAM. If Maven build fails with exit code 137 (out of memory):

**Solution**: Don't build on the server! Use GitHub Actions (which has more memory) to build and push to Docker Hub, then pull the image on your server.

This is exactly what the Docker Hub workflow is designed for! ✅

## Quick Reference

```bash
# View logs
docker-compose logs -f books-api

# Restart service
docker-compose restart books-api

# Check status
docker-compose ps

# Stop all
docker-compose down

# Nuclear option (removes all data)
docker-compose down -v
docker system prune -af
docker-compose pull
docker-compose up -d
```

## Image Name Configuration

Make sure your Docker Hub image name matches in both places:

1. **GitHub Actions** (`.github/workflows/docker-build.yml`):
   ```yaml
   images: ${{ secrets.DOCKER_USERNAME }}/books-api
   ```

2. **Docker Compose** (`docker-compose.yml`):
   ```yaml
   image: ${DOCKER_USERNAME}/books-api:latest
   ```

3. **Server .env** (`/opt/books-api/.env`):
   ```bash
   DOCKER_USERNAME=jonathan0912000
   ```

## Complete Workflow

```
┌──────────────────────────────────────┐
│ 1. Developer: Code changes locally   │
│    git add .                          │
│    git commit -m "message"            │
│    git push origin main               │
└─────────────┬────────────────────────┘
              │
              ▼
┌──────────────────────────────────────┐
│ 2. GitHub Actions: Build & Push      │
│    - mvn clean package                │
│    - docker build                     │
│    - docker push to Docker Hub        │
└─────────────┬────────────────────────┘
              │
              ▼
┌──────────────────────────────────────┐
│ 3. Docker Hub: Image stored          │
│    jonathan0912000/books-api:latest   │
└─────────────┬────────────────────────┘
              │
              ▼
┌──────────────────────────────────────┐
│ 4. Server: Pull & Deploy             │
│    cd /opt/books-api                  │
│    git pull origin main               │
│    docker-compose pull books-api      │
│    docker-compose down                │
│    docker-compose up -d               │
└──────────────────────────────────────┘
```

## Success Indicators

✅ **GitHub Actions**: Green checkmark on Actions tab  
✅ **Docker Hub**: New image timestamp matches your push  
✅ **Server**: `docker-compose logs -f books-api` shows "Javalin started"  
✅ **API**: `curl http://localhost:7070/api/v1/books` returns data  

## Common Mistakes

❌ Forgetting to update DOCKER_USERNAME in server's .env  
❌ Not waiting for GitHub Actions to complete before pulling  
❌ Using old cached image - always do `docker-compose pull`  
❌ Trying to build on low-memory server - use GitHub Actions instead!  
