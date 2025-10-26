# 🚀 Books API - Quick Start Guide

Get your Books REST API up and running in minutes!

## 📦 Prerequisites

- Docker & Docker Compose installed
- Java 17 (for local development without Docker)
- Git

## ⚡ Quick Start (Docker)

### 1. Clone and Setup
```bash
git clone <your-repo-url>
cd bookshelf-sp2-sidste

# Create environment file
cp .env.example .env
```

### 2. Start Everything
```bash
# Using docker-compose
docker-compose up -d

# OR using make (if available)
make up
```

### 3. Verify It's Running
```bash
# Check services
docker-compose ps

# View logs
docker-compose logs -f books-api

# Test API
curl http://localhost:7070/api/v1/routes
```

**That's it! 🎉** Your API is now running at `http://localhost:7070/api/v1`

---

## 🛠️ Common Commands

### Using Docker Compose
```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs -f

# Restart
docker-compose restart

# Rebuild after code changes
docker-compose up -d --build
```

### Using Make (Easier!)
```bash
make up          # Start all services
make down        # Stop all services
make logs        # View logs
make restart     # Restart services
make rebuild     # Rebuild and restart
make status      # Check service status
make help        # See all available commands
```

---

## 🔌 API Endpoints

Once running, access your API at: `http://localhost:7070/api/v1`

### Available Routes
- **GET** `/api/v1/routes` - View all available routes
- **GET** `/api/v1/books` - Get all books
- **GET** `/api/v1/books/{id}` - Get book by ID
- **POST** `/api/v1/books` - Create new book
- **PUT** `/api/v1/books/{id}` - Update book
- **DELETE** `/api/v1/books/{id}` - Delete book

### Test with curl
```bash
# Get all books
curl http://localhost:7070/api/v1/books

# Get routes overview
curl http://localhost:7070/api/v1/routes

# Create a book (example)
curl -X POST http://localhost:7070/api/v1/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Book","author":"Test Author"}'
```

---

## 🐛 Troubleshooting

### Port Already in Use
```bash
# Change port in .env file
API_PORT=8080

# Or stop the conflicting service
docker-compose down
```

### Can't Connect to Database
```bash
# Check if PostgreSQL is running
docker-compose ps

# View database logs
docker-compose logs postgres

# Restart database
docker-compose restart postgres
```

### Application Errors
```bash
# View application logs
docker-compose logs -f books-api

# Restart application
docker-compose restart books-api

# Complete rebuild
docker-compose down
docker-compose up -d --build
```

---

## 🌐 Deploy to Production

See **[DEPLOYMENT.md](./DEPLOYMENT.md)** for complete deployment instructions including:
- Digital Ocean setup
- Domain configuration with GoDaddy
- SSL/HTTPS setup
- Production best practices

### Quick Deploy
```bash
# Set your server IP
export REMOTE_HOST=your-droplet-ip

# Run deployment script
./deploy.sh
```

---

## 📚 Documentation

- **[DEPLOYMENT.md](./DEPLOYMENT.md)** - Complete deployment guide
- **[.env.example](./.env.example)** - Environment variables reference
- **[docker-compose.yml](./docker-compose.yml)** - Service configuration

---

## 🆘 Need Help?

1. Check the logs: `docker-compose logs -f`
2. Verify services are running: `docker-compose ps`
3. Review [DEPLOYMENT.md](./DEPLOYMENT.md) for detailed troubleshooting

---

**Happy coding! 🎉**
