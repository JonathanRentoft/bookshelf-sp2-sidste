# 📚 Books REST API

A RESTful API for managing books, built with Javalin, JPA/Hibernate, and PostgreSQL.

## 🚀 Quick Start

**Using Docker (Recommended):**
```bash
# Start everything with one command
docker-compose up -d

# Or use make
make up
```

**API will be available at:** `http://localhost:7070/api/v1`

See **[QUICK_START.md](./QUICK_START.md)** for detailed quick start instructions.

## 📖 Documentation

- **[QUICK_START.md](./QUICK_START.md)** - Get started in minutes
- **[DEPLOYMENT.md](./DEPLOYMENT.md)** - Complete deployment guide for Digital Ocean & GoDaddy
- **[.env.example](./.env.example)** - Environment variables reference

## 🛠️ Local Development

### Option 1: With Docker (Easy)
```bash
# Create environment file
cp .env.example .env

# Start services
docker-compose up -d

# View logs
docker-compose logs -f books-api
```

### Option 2: Without Docker
1. Create a database in your local Postgres instance called `books_db`
2. Update database credentials in `pom.xml` or use environment variables
3. Run the main method in the `config.Populate` class to populate the database
4. Run the main method in the `Main` class to start the server on port 7170
5. See the routes in your browser at `http://localhost:7170/api/v1/routes`

## 📡 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/routes` | View all available routes |
| GET | `/api/v1/books` | Get all books |
| GET | `/api/v1/books/{id}` | Get book by ID |
| POST | `/api/v1/books` | Create new book |
| PUT | `/api/v1/books/{id}` | Update book |
| DELETE | `/api/v1/books/{id}` | Delete book |

Test the API using the `src/main/resources/http/dev.http` file in IntelliJ or any HTTP client.

## 🐳 Docker Commands

```bash
# Start services
make up              # or: docker-compose up -d

# Stop services
make down            # or: docker-compose down

# View logs
make logs            # or: docker-compose logs -f

# Rebuild after code changes
make rebuild         # or: docker-compose up -d --build

# See all commands
make help
```

## 🌐 Deployment

Deploy to Digital Ocean with custom domain (GoDaddy):

```bash
# Quick deploy
export REMOTE_HOST=your-droplet-ip
./deploy.sh
```

See **[DEPLOYMENT.md](./DEPLOYMENT.md)** for complete deployment instructions including:
- Digital Ocean Droplet setup
- PostgreSQL database configuration
- Domain setup with GoDaddy
- SSL/HTTPS with Let's Encrypt
- CI/CD with GitHub Actions

## 📦 Project Structure

```
.
├── src/main/java/dat/
│   ├── config/          # Application configuration
│   ├── controllers/     # API controllers
│   ├── daos/           # Data access objects
│   ├── dtos/           # Data transfer objects
│   ├── entities/       # JPA entities
│   ├── routes/         # Route definitions
│   └── security/       # Security & authentication
├── Dockerfile          # Docker image configuration
├── docker-compose.yml  # Multi-container setup
├── deploy.sh           # Deployment script
└── Makefile           # Convenient commands
```

## 🔧 Technology Stack

- **Framework:** Javalin 6.1.3
- **ORM:** Hibernate 6.2.4
- **Database:** PostgreSQL 15
- **Build Tool:** Maven
- **Java Version:** 17
- **Containerization:** Docker & Docker Compose

## 🔒 Security

- JWT-based authentication
- Secure password hashing with BCrypt
- Environment-based configuration
- Database connection pooling with HikariCP

## 🧪 Testing

```bash
# Run tests
mvn test

# Run with Docker
make test
```

## 📝 License

[Your License Here]

---

Made with ❤️ by [Your Name]

