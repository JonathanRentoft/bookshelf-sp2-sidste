# Book REST API

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
