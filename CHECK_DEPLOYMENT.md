# 🔍 Check Deployment Status

Your database connection is working! Now let's verify the full application startup.

## On Your Server, Run These Commands:

### 1. Check if containers are running:
```bash
docker-compose ps
```

**Expected output:**
```
NAME                IMAGE                                    STATUS
books-api           jonathan0912000/books-api:latest        Up X seconds
books-db            postgres:16-alpine                       Up X seconds
```

### 2. See the FULL logs (last 100 lines):
```bash
docker-compose logs --tail=100 books-api
```

**Look for these SUCCESS indicators:**
```
✅ INFO  com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Start completed.
✅ INFO  Javalin - Listening on http://localhost:7070/
✅ INFO  Javalin - Javalin started in XXXXms
```

### 3. If container keeps restarting:
```bash
# Check restart count
docker-compose ps

# If books-api shows "Restarting" or restart count > 0:
docker-compose logs books-api | grep -E "ERROR|Exception|FATAL"
```

### 4. Test the API:
```bash
# From inside the server
curl http://localhost:7070/api/v1/books

# From your local machine (if firewall allows)
curl http://161.35.82.205:7070/api/v1/books
```

## What to Send Me:

Please send me the output of:
```bash
# 1. Container status
docker-compose ps

# 2. Last 100 lines of logs
docker-compose logs --tail=100 books-api

# 3. Try to access the API
curl http://localhost:7070/api/v1/books
```

## Quick Troubleshooting:

### If you see "Javalin started" → SUCCESS! ✅
The app is running. Test with curl.

### If logs stop after "HikariPool-1 - Start completed" → App might be hanging
```bash
# Check if process is running
docker-compose exec books-api ps aux

# Check resource usage
docker stats books-api --no-stream
```

### If you see errors after database connection → Show me the errors
Copy the error messages and send them to me.

### If container keeps restarting → Configuration issue
```bash
# Show me the full logs
docker-compose logs books-api
```
