# Book REST API

## 🚀 Quick Start - Docker Hub Deployment

For production deployment using Docker Hub, see **[DEPLOYMENT.md](DEPLOYMENT.md)**

**Quick deploy:**
```bash
# 1. Configure .env file with your Docker Hub username
# 2. Run deployment script
./deploy-dockerhub.sh
```

## 🌐 Domain Setup (books.johannesfoog.dk)

To make your API accessible via your domain with HTTPS:

```bash
# On your server (161.35.82.205), run:
sudo bash setup-domain.sh your-email@example.com
```

This will:
- Install and configure Nginx as a reverse proxy
- Set up SSL certificate with Let's Encrypt
- Configure automatic certificate renewal
- Enable HTTPS for your domain

See **[DOMAIN_SETUP.md](DOMAIN_SETUP.md)** for detailed instructions and troubleshooting.

---

## Beskrivelse
REST API til håndtering af 500 bøger med CRUD operationer.

## Teknologi
- Java 17
- Javalin 6.1.3
- Hibernate 6.2.4
- PostgreSQL
- Maven

## Database Setup
Opret PostgreSQL database: `books`
- Username: `postgres`
- Password: `postgres`
- Port: `5432`

## Kør Applikation
```bash
mvn clean install
java -jar target/app.jar
```
Server starter på port 7170.

## API Endpoints
Base URL: `http://localhost:7170/api/v1`

### Bøger
- `GET /books` - Hent alle bøger
- `GET /books/{id}` - Hent bog efter ID
- `POST /books` - Opret ny bog
- `PUT /books/{id}` - Opdater bog
- `DELETE /books/{id}` - Slet bog

## Book Model
```json
{
  "id": 1,
  "title": "Bog Titel",
  "author": "Forfatter Navn",
  "publisher": "Forlag",
  "yearPublished": 2023,
  "genre": "FICTION"
}
```

## Genres
`FICTION`, `NONFICTION`, `SCIENCE`, `HISTORY`, `BIOGRAPHY`, `FANTASY`, `MYSTERY`, `ROMANCE`, `THRILLER`, `HORROR`

## Test Data
Databasen populeres automatisk med 500 bøger ved opstart.
