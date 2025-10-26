# Books REST API - Deployment Guide

This guide covers deploying the Books REST API to Digital Ocean with a custom domain through GoDaddy.

## 📋 Table of Contents
- [Prerequisites](#prerequisites)
- [Local Development with Docker](#local-development-with-docker)
- [Digital Ocean Deployment](#digital-ocean-deployment)
- [Domain Configuration (GoDaddy)](#domain-configuration-godaddy)
- [Environment Variables](#environment-variables)
- [Troubleshooting](#troubleshooting)

---

## 🔧 Prerequisites

1. **Docker & Docker Compose** installed locally
2. **Digital Ocean Account** with:
   - A Droplet (Ubuntu 22.04 recommended)
   - PostgreSQL Managed Database (recommended) OR self-hosted PostgreSQL
3. **GoDaddy Domain** with access to DNS settings
4. **Git** installed

---

## 🏠 Local Development with Docker

### 1. Clone the Repository
```bash
git clone <your-repo-url>
cd bookshelf-sp2-sidste
```

### 2. Create Environment File
```bash
cp .env.example .env
# Edit .env with your local settings
```

### 3. Start with Docker Compose
```bash
# Build and start all services
docker-compose up --build

# Or run in detached mode
docker-compose up -d

# View logs
docker-compose logs -f books-api
```

### 4. Test the API
The API will be available at:
- **Base URL**: `http://localhost:7070/api/v1`
- **Routes Overview**: `http://localhost:7070/api/v1/routes`

### 5. Stop Services
```bash
docker-compose down

# To also remove volumes (database data)
docker-compose down -v
```

---

## 🚀 Digital Ocean Deployment

### Option A: Using Docker on a Droplet

#### 1. Create a Droplet
- Choose **Ubuntu 22.04 LTS**
- Minimum: 2 GB RAM, 1 vCPU
- Enable monitoring
- Add your SSH key

#### 2. Connect to Your Droplet
```bash
ssh root@your-droplet-ip
```

#### 3. Install Docker and Docker Compose
```bash
# Update system
apt update && apt upgrade -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# Install Docker Compose
apt install docker-compose -y

# Verify installation
docker --version
docker-compose --version
```

#### 4. Setup Application Directory
```bash
# Create app directory
mkdir -p /opt/books-api
cd /opt/books-api

# Clone your repository
git clone <your-repo-url> .
```

#### 5. Configure Environment Variables
```bash
# Create .env file
nano .env
```

Add your production values (see [Environment Variables](#environment-variables) section below):
```env
DB_NAME=books_db
DB_USERNAME=your_db_user
DB_PASSWORD=your_secure_password
CONNECTION_STR=jdbc:postgresql://your-db-host:25060/

SECRET_KEY=your_random_secret_key_here
ISSUER=books.api
TOKEN_EXPIRATION_TIME=3600000

API_PORT=7070
DEPLOYED=true
```

#### 6. Setup PostgreSQL Database

**Option 1: Digital Ocean Managed Database (Recommended)**
1. Go to Digital Ocean Dashboard → Databases
2. Create PostgreSQL Database (Version 15 recommended)
3. Note down the connection details
4. Create a database named `books_db`
5. Update `.env` with connection string

**Option 2: Self-hosted with Docker**
```bash
# The docker-compose.yml includes PostgreSQL
# Just start both services
docker-compose up -d
```

#### 7. Build and Deploy
```bash
# Build and start the application
docker-compose up -d --build

# Check if services are running
docker-compose ps

# View logs
docker-compose logs -f books-api
```

#### 8. Setup Firewall
```bash
# Allow SSH, HTTP, HTTPS, and your API port
ufw allow 22/tcp
ufw allow 80/tcp
ufw allow 443/tcp
ufw allow 7070/tcp
ufw enable
```

#### 9. Setup Nginx Reverse Proxy (Optional but Recommended)
```bash
# Install Nginx
apt install nginx -y

# Create Nginx configuration
nano /etc/nginx/sites-available/books-api
```

Add this configuration:
```nginx
server {
    listen 80;
    server_name your-domain.com www.your-domain.com;

    location / {
        proxy_pass http://localhost:7070;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Enable the site:
```bash
ln -s /etc/nginx/sites-available/books-api /etc/nginx/sites-enabled/
nginx -t
systemctl restart nginx
```

#### 10. Setup SSL with Let's Encrypt (Recommended)
```bash
# Install Certbot
apt install certbot python3-certbot-nginx -y

# Obtain SSL certificate
certbot --nginx -d your-domain.com -d www.your-domain.com

# Certificate will auto-renew
```

### Option B: Using Digital Ocean App Platform

1. Go to **App Platform** in Digital Ocean Dashboard
2. Create New App → Choose GitHub repository
3. Configure:
   - **Resource Type**: Docker Hub or Dockerfile
   - **Build Command**: Automatic (uses Dockerfile)
   - **Port**: 7070
4. Add Environment Variables (from .env.example)
5. Add PostgreSQL Database as a component
6. Deploy

---

## 🌐 Domain Configuration (GoDaddy)

### 1. Get Your Droplet IP
```bash
# From Digital Ocean Dashboard or
curl ifconfig.me
```

### 2. Configure DNS in GoDaddy

1. Log in to **GoDaddy**
2. Go to **My Products** → **DNS** for your domain
3. Add/Edit DNS Records:

| Type  | Name | Value               | TTL       |
|-------|------|---------------------|-----------|
| A     | @    | your-droplet-ip     | 600       |
| A     | www  | your-droplet-ip     | 600       |
| CNAME | api  | your-domain.com     | 600       |

### 3. Wait for DNS Propagation
- Can take 10 minutes to 48 hours
- Check with: `nslookup your-domain.com`

---

## 🔐 Environment Variables

### Required Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_NAME` | Database name | `books_db` |
| `DB_USERNAME` | Database user | `postgres` |
| `DB_PASSWORD` | Database password | `secure_password` |
| `CONNECTION_STR` | JDBC connection string | `jdbc:postgresql://localhost:5432/` |
| `SECRET_KEY` | JWT secret key | Generate with: `openssl rand -hex 32` |
| `ISSUER` | JWT issuer | `books.api` |
| `TOKEN_EXPIRATION_TIME` | Token expiry in ms | `3600000` (1 hour) |
| `DEPLOYED` | Deployment flag | `true` |
| `API_PORT` | API port | `7070` |

### Generate Secure Secret Key
```bash
# Linux/Mac
openssl rand -hex 32

# Or use online generator
# https://randomkeygen.com/
```

---

## 📝 Useful Commands

### Docker Commands
```bash
# View running containers
docker ps

# View logs
docker-compose logs -f

# Restart service
docker-compose restart books-api

# Rebuild and restart
docker-compose up -d --build

# Stop all services
docker-compose down

# View resource usage
docker stats
```

### Application Updates
```bash
# Pull latest code
git pull origin main

# Rebuild and deploy
docker-compose down
docker-compose up -d --build
```

### Database Backup
```bash
# Backup database
docker exec books-postgres pg_dump -U postgres books_db > backup.sql

# Restore database
docker exec -i books-postgres psql -U postgres books_db < backup.sql
```

---

## 🔍 Troubleshooting

### Application Won't Start
```bash
# Check logs
docker-compose logs books-api

# Check if database is ready
docker-compose logs postgres

# Verify environment variables
docker-compose config
```

### Can't Connect to Database
1. Verify `CONNECTION_STR` format
2. Check database credentials
3. Ensure database service is running
4. Check firewall rules
5. Verify database allows remote connections

### API Returns 502/504
1. Check if application is running: `docker ps`
2. Verify port 7070 is exposed
3. Check Nginx configuration
4. Review application logs

### SSL Certificate Issues
```bash
# Renew certificate manually
certbot renew

# Test renewal process
certbot renew --dry-run
```

### Port Already in Use
```bash
# Find process using port 7070
lsof -i :7070

# Kill the process
kill -9 <PID>
```

---

## 📊 API Endpoints

Once deployed, your API will be available at:

- **Production**: `https://your-domain.com/api/v1`
- **Routes Overview**: `https://your-domain.com/api/v1/routes`

### Example Endpoints
- `GET /api/v1/books` - Get all books
- `GET /api/v1/books/{id}` - Get book by ID
- `POST /api/v1/books` - Create new book
- `PUT /api/v1/books/{id}` - Update book
- `DELETE /api/v1/books/{id}` - Delete book

---

## 🔒 Security Checklist

- [ ] Change default `SECRET_KEY` to a strong random value
- [ ] Use strong database passwords
- [ ] Enable firewall (UFW)
- [ ] Setup SSL/HTTPS with Let's Encrypt
- [ ] Restrict database access to application only
- [ ] Regular security updates: `apt update && apt upgrade`
- [ ] Setup automated backups
- [ ] Monitor logs regularly
- [ ] Use environment variables for secrets (never commit .env)

---

## 📞 Support & Resources

- **Javalin Documentation**: https://javalin.io/
- **Docker Documentation**: https://docs.docker.com/
- **Digital Ocean Tutorials**: https://www.digitalocean.com/community/tutorials
- **PostgreSQL Documentation**: https://www.postgresql.org/docs/

---

## 📄 License

[Your License Here]

---

**Happy Deploying! 🚀**
