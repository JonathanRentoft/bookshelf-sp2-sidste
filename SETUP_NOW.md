# 🚀 Set Up Your Domain NOW

## What You Need

✅ Domain DNS configured: `books.johannesfoog.dk` → `161.35.82.205` (DONE)  
✅ Server running at: `161.35.82.205`  
✅ Books API container running on port 7070

## 📋 Complete Setup in 3 Minutes

### On Your Server (161.35.82.205), Run These Commands:

```bash
# 1. Go to the project directory
cd /opt/books-api

# 2. Pull latest code (includes setup script)
git pull origin main

# 3. Run the automated setup script
sudo bash setup-domain.sh YOUR_EMAIL@example.com
```

**Replace `YOUR_EMAIL@example.com` with your actual email** (needed for Let's Encrypt SSL certificate).

Example:
```bash
sudo bash setup-domain.sh johannes@johannesfoog.dk
```

## What the Script Does

1. ✅ Installs Nginx (web server/reverse proxy)
2. ✅ Configures Nginx to forward `books.johannesfoog.dk` → `localhost:7070`
3. ✅ Opens firewall ports (80, 443)
4. ✅ Gets free SSL certificate from Let's Encrypt
5. ✅ Sets up HTTPS with automatic renewal
6. ✅ Tests everything

## Expected Output

```
🌐 Setting up books.johannesfoog.dk
====================================

📦 Installing Nginx...
🚀 Starting Nginx...
⚙️  Creating Nginx configuration...
✅ Enabling site...
🔍 Testing Nginx configuration...
   ✅ Nginx configuration is valid
🔄 Reloading Nginx...
✅ HTTP setup complete!

🔍 Checking if Books API is running on port 7070...
   ✅ Books API is responding

📡 Checking DNS resolution...
   ✅ DNS resolves to: 161.35.82.205

🔐 Installing SSL certificate (HTTPS)...
🔑 Getting Let's Encrypt certificate...
   ✅ SSL certificate installed successfully!

============================================
🎉 Setup Complete!
============================================

Your API is now available at:
   🌐 http://books.johannesfoog.dk/api/v1/books
   🔒 https://books.johannesfoog.dk/api/v1/books
```

## After Setup - Test It!

```bash
# Test from anywhere (your local machine or server)
curl https://books.johannesfoog.dk/api/v1/books

# Should return JSON with books list
```

## ⚠️ If SSL Certificate Fails

This usually means DNS hasn't propagated yet. Wait 10-30 minutes, then run:

```bash
sudo certbot --nginx -d books.johannesfoog.dk --email YOUR_EMAIL --agree-tos
```

## 🔍 Troubleshooting

### Check if API is running
```bash
docker-compose ps
curl http://localhost:7070/api/v1/books
```

### Check DNS resolution
```bash
nslookup books.johannesfoog.dk
# Should show: 161.35.82.205
```

### Check Nginx logs
```bash
sudo tail -f /var/log/nginx/books-api-error.log
```

### Restart Nginx
```bash
sudo systemctl restart nginx
```

## 📁 Files Created

- `/etc/nginx/sites-available/books.johannesfoog.dk` - Nginx config
- `/etc/nginx/sites-enabled/books.johannesfoog.dk` - Enabled site link
- `/etc/letsencrypt/live/books.johannesfoog.dk/` - SSL certificates
- `/var/log/nginx/books-api-access.log` - Access logs
- `/var/log/nginx/books-api-error.log` - Error logs

## 🎯 Quick Reference

| Command | Purpose |
|---------|---------|
| `sudo systemctl restart nginx` | Restart Nginx |
| `sudo nginx -t` | Test Nginx config |
| `sudo certbot renew` | Renew SSL certificate |
| `sudo tail -f /var/log/nginx/books-api-access.log` | View access logs |
| `curl https://books.johannesfoog.dk/health` | Health check |

## 🌐 Your API Endpoints (After Setup)

Base URL: `https://books.johannesfoog.dk/api/v1`

- `GET /books` - Get all books
- `GET /books/{id}` - Get book by ID
- `POST /books` - Create new book
- `PUT /books/{id}` - Update book
- `DELETE /books/{id}` - Delete book
- `POST /auth/login` - Login
- `POST /auth/register` - Register

## Need Help?

See [DOMAIN_SETUP.md](DOMAIN_SETUP.md) for detailed manual setup and troubleshooting.
