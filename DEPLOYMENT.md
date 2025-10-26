# Deployment Guide - Docker Hub Workflow

This guide explains how to deploy the Books API using Docker Hub for production deployments.

## Prerequisites

1. Docker and Docker Compose installed on your server
2. GitHub account with repository access
3. Docker Hub account
4. GitHub repository secrets configured

## Setup GitHub Secrets

Add these secrets to your GitHub repository (Settings → Secrets and variables → Actions):

- `DOCKER_USERNAME`: Your Docker Hub username
- `DOCKER_PASSWORD`: Your Docker Hub password or access token

## Deployment Steps

### 1. Configure Environment Variables

On your deployment server, update the `.env` file:

```bash
# Docker Configuration
DOCKER_USERNAME=your-dockerhub-username

# Database Configuration
DB_NAME=books_db
DB_USERNAME=postgres
DB_PASSWORD=postgres
DB_PORT=5432

# Application Configuration
API_PORT=7070

# JWT/Security Configuration
SECRET_KEY=your-secret-key-here
ISSUER=books.api
TOKEN_EXPIRATION_TIME=3600000

# Deployment Flag
DEPLOYED=true
```

### 2. Push to Main Branch

When you push to the `main` or `master` branch:

```bash
git add .
git commit -m "Update application"
git push origin main
```

GitHub Actions will automatically:
- Build the Maven project
- Create a Docker image
- Push the image to Docker Hub with the `latest` tag

### 3. Deploy on Server

Once the GitHub Actions workflow completes:

```bash
cd /opt/books-api  # or your deployment directory

# Pull the latest code (for docker-compose.yml updates)
git pull origin main

# Pull the latest Docker image from Docker Hub
docker-compose pull books-api

# Restart the services
docker-compose down
docker-compose up -d

# View logs
docker-compose logs -f books-api
```

### 4. Verify Deployment

Check that the application is running:

```bash
# Check container status
docker-compose ps

# Check application health
curl http://localhost:7070/api/v1/routes

# View logs
docker-compose logs -f books-api
```

## Troubleshooting

### Database Connection Issues

If you see "database does not exist" errors:

1. **Verify environment variables are set correctly:**
   ```bash
   docker-compose config
   ```

2. **Check database logs:**
   ```bash
   docker-compose logs postgres
   ```

3. **Ensure DB_NAME matches in both services:**
   - PostgreSQL service: `POSTGRES_DB` should match `DB_NAME`
   - Books API service: `DB_NAME` environment variable

4. **Restart with clean database:**
   ```bash
   docker-compose down -v  # WARNING: This deletes all data
   docker-compose up -d
   ```

### Image Pull Issues

If `docker-compose pull` fails:

1. **Verify DOCKER_USERNAME in .env file**
2. **Check that the image exists on Docker Hub:**
   ```bash
   docker search your-dockerhub-username/books-api
   ```
3. **Login to Docker Hub if the image is private:**
   ```bash
   docker login
   docker-compose pull books-api
   ```

### GitHub Actions Workflow Issues

If the workflow fails:

1. **Check GitHub Actions logs:** Go to Actions tab in your repository
2. **Verify secrets are set correctly:** Settings → Secrets and variables → Actions
3. **Ensure Docker Hub credentials are valid**

## Quick Reference Commands

```bash
# View all logs
docker-compose logs -f

# View only books-api logs
docker-compose logs -f books-api

# View only postgres logs
docker-compose logs -f postgres

# Restart a specific service
docker-compose restart books-api

# Check service status
docker-compose ps

# Stop all services
docker-compose down

# Stop and remove volumes (data will be lost)
docker-compose down -v

# Pull latest images
docker-compose pull

# Rebuild and restart
docker-compose up -d --force-recreate
```

## Workflow Overview

```
┌─────────────────┐
│  Developer      │
│  Push to main   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ GitHub Actions  │
│ - Build Maven   │
│ - Build Docker  │
│ - Push to Hub   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Docker Hub     │
│  Image: latest  │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Server         │
│ docker-compose  │
│ pull & restart  │
└─────────────────┘
```

## Security Best Practices

1. **Never commit `.env` files** - They contain sensitive information
2. **Use strong SECRET_KEY** - Generate a secure random key
3. **Use Docker Hub access tokens** instead of passwords for GitHub secrets
4. **Keep dependencies updated** - Regularly update Docker images and Maven dependencies
5. **Use HTTPS in production** - Set up a reverse proxy with SSL/TLS

## Environment-Specific Configurations

### Development
- Build locally: `docker-compose build`
- Use default credentials

### Production
- Pull from Docker Hub
- Use strong passwords
- Configure proper network security
- Set up backups for the database volume

## Database Backups

To backup the PostgreSQL database:

```bash
# Create backup
docker-compose exec postgres pg_dump -U postgres books_db > backup.sql

# Restore backup
docker-compose exec -T postgres psql -U postgres books_db < backup.sql
```

## Monitoring

Monitor application health:

```bash
# Check health endpoints
curl http://localhost:7070/api/v1/routes

# Monitor resource usage
docker stats

# Check disk usage
docker system df
```
