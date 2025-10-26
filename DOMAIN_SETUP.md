# 🌐 Domain Setup for books.johannesfoog.dk

This guide will configure your server to serve the Books API at `https://books.johannesfoog.dk`.

## Prerequisites

✅ DNS A record pointing `books.johannesfoog.dk` to `161.35.82.205` (already done in GoDaddy)  
✅ Server running at 161.35.82.205  
✅ Books API running on port 7070  

## Architecture

```
Internet → books.johannesfoog.dk:443 (HTTPS)
    ↓
Nginx (reverse proxy with SSL)
    ↓
books-api container :7070
```

## Step 1: Install Nginx

```bash
# Update package list
sudo apt update

# Install Nginx
sudo apt install -y nginx

# Start and enable Nginx
sudo systemctl start nginx
sudo systemctl enable nginx

# Check status
sudo systemctl status nginx
```

## Step 2: Configure Firewall

```bash
# Allow HTTP and HTTPS traffic
sudo ufw allow 'Nginx Full'

# Or if UFW not configured, use iptables/allow ports:
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# Check firewall status
sudo ufw status
```

## Step 3: Create Nginx Configuration for Books API

```bash
# Create Nginx config file
sudo nano /etc/nginx/sites-available/books.johannesfoog.dk
```

**Paste this configuration** (HTTP only, we'll add HTTPS in Step 5):

```nginx
server {
    listen 80;
    listen [::]:80;
    server_name books.johannesfoog.dk;

    # Increase client body size for larger requests
    client_max_body_size 10M;

    # Logging
    access_log /var/log/nginx/books-api-access.log;
    error_log /var/log/nginx/books-api-error.log;

    location / {
        # Proxy to Books API container
        proxy_pass http://localhost:7070;
        
        # Proxy headers
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        
        # WebSocket support (if needed later)
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    # Health check endpoint
    location /health {
        access_log off;
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }
}
```

Save and exit (Ctrl+X, then Y, then Enter).

## Step 4: Enable the Site

```bash
# Create symbolic link to enable site
sudo ln -s /etc/nginx/sites-available/books.johannesfoog.dk /etc/nginx/sites-enabled/

# Test Nginx configuration
sudo nginx -t

# Reload Nginx
sudo systemctl reload nginx
```

## Step 5: Install SSL Certificate (HTTPS)

```bash
# Install Certbot for Let's Encrypt
sudo apt install -y certbot python3-certbot-nginx

# Get SSL certificate (replace with your email)
sudo certbot --nginx -d books.johannesfoog.dk --email your-email@example.com --agree-tos --non-interactive

# Certbot will automatically:
# 1. Verify domain ownership
# 2. Get SSL certificate
# 3. Update Nginx config for HTTPS
# 4. Set up auto-renewal
```

**Important:** Replace `your-email@example.com` with your actual email address.

## Step 6: Verify Configuration

After Certbot finishes, your Nginx config will be updated to:

```nginx
server {
    listen 80;
    listen [::]:80;
    server_name books.johannesfoog.dk;
    
    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    listen [::]:443 ssl http2;
    server_name books.johannesfoog.dk;

    # SSL certificates (added by Certbot)
    ssl_certificate /etc/letsencrypt/live/books.johannesfoog.dk/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/books.johannesfoog.dk/privkey.pem;
    include /etc/letsencrypt/options-ssl-nginx.conf;
    ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem;

    # ... rest of your config ...
    
    location / {
        proxy_pass http://localhost:7070;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## Step 7: Test Your Setup

```bash
# Test from server
curl http://books.johannesfoog.dk/api/v1/books
curl https://books.johannesfoog.dk/api/v1/books

# Test from your local machine
curl https://books.johannesfoog.dk/api/v1/books
```

## Step 8: Set Up Auto-Renewal for SSL

```bash
# Test auto-renewal (dry run)
sudo certbot renew --dry-run

# If successful, Certbot is configured to auto-renew
# Certificates renew automatically via systemd timer
```

## Troubleshooting

### DNS Not Resolving

```bash
# Check if DNS is propagated
nslookup books.johannesfoog.dk
dig books.johannesfoog.dk

# Should show: 161.35.82.205
```

If not, **wait 5-60 minutes** for DNS propagation.

### Nginx Not Starting

```bash
# Check Nginx error logs
sudo tail -f /var/log/nginx/error.log

# Check configuration
sudo nginx -t
```

### Can't Get SSL Certificate

```bash
# Make sure:
# 1. DNS is propagated (wait 10-60 minutes after GoDaddy DNS update)
# 2. Port 80 is open and accessible
# 3. Nginx is running

# Test HTTP access first
curl -I http://books.johannesfoog.dk

# If this works, try Certbot again
sudo certbot --nginx -d books.johannesfoog.dk --email your@email.com --agree-tos
```

### API Returns 502 Bad Gateway

```bash
# Check if Books API is running
docker-compose ps
curl http://localhost:7070/api/v1/books

# If localhost works but domain doesn't, check Nginx logs
sudo tail -f /var/log/nginx/books-api-error.log
```

### Check Nginx Logs

```bash
# Access logs (successful requests)
sudo tail -f /var/log/nginx/books-api-access.log

# Error logs (failed requests)
sudo tail -f /var/log/nginx/books-api-error.log
```

## Complete Setup Script

Run this all-in-one script on your server:

```bash
#!/bin/bash
# Save as: setup-domain.sh

set -e

echo "🌐 Setting up books.johannesfoog.dk"
echo "===================================="

# Install Nginx
echo "📦 Installing Nginx..."
sudo apt update
sudo apt install -y nginx

# Start Nginx
echo "🚀 Starting Nginx..."
sudo systemctl start nginx
sudo systemctl enable nginx

# Configure firewall
echo "🔥 Configuring firewall..."
sudo ufw allow 'Nginx Full' || true

# Create Nginx config
echo "⚙️  Creating Nginx configuration..."
sudo tee /etc/nginx/sites-available/books.johannesfoog.dk > /dev/null <<'EOF'
server {
    listen 80;
    listen [::]:80;
    server_name books.johannesfoog.dk;

    client_max_body_size 10M;

    access_log /var/log/nginx/books-api-access.log;
    error_log /var/log/nginx/books-api-error.log;

    location / {
        proxy_pass http://localhost:7070;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    location /health {
        access_log off;
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }
}
EOF

# Enable site
echo "✅ Enabling site..."
sudo ln -sf /etc/nginx/sites-available/books.johannesfoog.dk /etc/nginx/sites-enabled/

# Test and reload Nginx
echo "🔍 Testing Nginx configuration..."
sudo nginx -t

echo "🔄 Reloading Nginx..."
sudo systemctl reload nginx

echo ""
echo "✅ HTTP setup complete!"
echo ""
echo "📋 Next steps:"
echo "1. Wait 5-10 minutes for DNS to propagate"
echo "2. Test: curl http://books.johannesfoog.dk/api/v1/books"
echo "3. Install SSL: sudo certbot --nginx -d books.johannesfoog.dk --email YOUR_EMAIL"
echo ""
echo "🔐 To add HTTPS, run:"
echo "sudo apt install -y certbot python3-certbot-nginx"
echo "sudo certbot --nginx -d books.johannesfoog.dk --email your@email.com --agree-tos"
```

## Quick Commands Reference

```bash
# Restart Nginx
sudo systemctl restart nginx

# Reload Nginx (no downtime)
sudo systemctl reload nginx

# Test Nginx config
sudo nginx -t

# View Nginx logs
sudo tail -f /var/log/nginx/books-api-access.log
sudo tail -f /var/log/nginx/books-api-error.log

# Renew SSL certificate manually
sudo certbot renew

# Check SSL certificate expiry
sudo certbot certificates
```

## Security Recommendations

1. **Keep SSL certificates updated** - Auto-renewal is configured
2. **Monitor Nginx logs** - Check for suspicious activity
3. **Rate limiting** - Add if you get too much traffic:
   ```nginx
   limit_req_zone $binary_remote_addr zone=api_limit:10m rate=10r/s;
   
   location / {
       limit_req zone=api_limit burst=20 nodelay;
       # ... rest of config
   }
   ```

4. **CORS headers** - If needed for web apps, add to Nginx config:
   ```nginx
   add_header Access-Control-Allow-Origin * always;
   add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS" always;
   add_header Access-Control-Allow-Headers "Authorization, Content-Type" always;
   ```

## Success Checklist

- [ ] Nginx installed and running
- [ ] Firewall allows ports 80 and 443
- [ ] Nginx config created and enabled
- [ ] DNS resolves `books.johannesfoog.dk` to `161.35.82.205`
- [ ] HTTP works: `curl http://books.johannesfoog.dk/api/v1/books`
- [ ] SSL certificate installed
- [ ] HTTPS works: `curl https://books.johannesfoog.dk/api/v1/books`
- [ ] HTTP redirects to HTTPS
- [ ] Auto-renewal tested

## 🎉 When Complete

Your API will be accessible at:
- **https://books.johannesfoog.dk/api/v1/books**
- **https://books.johannesfoog.dk/api/v1/auth/login**
- All other endpoints!
