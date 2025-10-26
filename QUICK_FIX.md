# ⚡ Quick Fix - Get Your App Running NOW

## What Was Wrong?

1. ❌ **JAR had no Main-Class manifest** → App couldn't start
2. ❌ **Database name mismatch** → "books" vs "books_db"
3. ❌ **Server out of memory** when building → 512MB is not enough

## What Was Fixed?

✅ **pom.xml** - Now uses maven-shade-plugin (industry standard for executable JARs)  
✅ **Populate.java** - Reads DB_NAME from environment  
✅ **BookController.java** - Reads DB_NAME from environment  
✅ **docker-compose.yml** - Configured for Docker Hub deployment  

## 🚀 Deploy in 3 Steps

### On Your LOCAL Machine:

```bash
# Push to main branch (triggers GitHub Actions build)
git push origin main

# Wait 2-5 minutes for GitHub Actions to complete
# Check: https://github.com/YOUR_USERNAME/YOUR_REPO/actions
```

### On Your SERVER (161.35.82.205):

```bash
# 1. Create/update .env file
cd /opt/books-api
nano .env
```

Paste this (replace jonathan0912000 with YOUR Docker Hub username):

```bash
DOCKER_USERNAME=jonathan0912000
DB_NAME=books_db
DB_USERNAME=postgres
DB_PASSWORD=postgres
DB_PORT=5432
API_PORT=7070
SECRET_KEY=841D8A6C80CBA4FCAD32D5367C18C53B
ISSUER=books.api
TOKEN_EXPIRATION_TIME=3600000
DEPLOYED=true
```

Save and exit (Ctrl+X, then Y, then Enter)

```bash
# 2. Pull latest configuration
git pull origin main

# 3. Clean start with new image
docker-compose down -v
docker-compose pull books-api
docker-compose up -d

# 4. Watch it start
docker-compose logs -f books-api
```

## ✅ Success Looks Like

You should see logs like this:

```
books-api | INFO  com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Start completed.
books-api | INFO  Javalin - Listening on http://localhost:7070/
books-api | INFO  Javalin - Javalin started in 2345ms
```

Then test:

```bash
curl http://localhost:7070/api/v1/books
```

## ❌ If It Still Fails

### "no main manifest attribute"
→ Old image! Wait for GitHub Actions, then:
```bash
docker-compose pull books-api
docker-compose up -d --force-recreate
```

### "database 'books' does not exist"  
→ Environment variables not loaded:
```bash
# Verify .env has DOCKER_USERNAME and DB_NAME
cat .env

# Check what docker-compose sees
docker-compose config | grep -E 'DB_NAME|POSTGRES_DB'

# Should show BOTH as books_db
```

### "Build failed with exit code 137"
→ Don't build on server! Use GitHub Actions to build, server only pulls:
```bash
# NEVER do this on your 512MB server:
docker build .  # ❌ NO!

# ALWAYS do this instead:
docker-compose pull books-api  # ✅ YES!
```

## 🎯 The Correct Workflow

| ❌ WRONG (Old Way) | ✅ RIGHT (New Way) |
|---|---|
| Build on server | Build on GitHub Actions |
| 512MB RAM fails | GitHub has plenty of RAM |
| Takes 5-10 minutes | Takes 2-3 minutes |
| `docker build .` | `git push origin main` |
| Manual rebuild | Automatic on push |

## Need Help?

See detailed guides:
- **SERVER_SETUP.md** - Complete troubleshooting
- **DEPLOYMENT.md** - Full deployment workflow
- **README.md** - Project overview

## One-Liner Deploy (after .env is configured):

```bash
cd /opt/books-api && git pull origin main && docker-compose down && docker-compose pull books-api && docker-compose up -d && docker-compose logs -f books-api
```
