#!/bin/bash
#
# Domain Setup Script for books.johannesfoog.dk
# Run this on your server (161.35.82.205)
#

set -e

DOMAIN="books.johannesfoog.dk"
API_PORT="7070"
EMAIL="${1:-your-email@example.com}"

echo "🌐 Setting up $DOMAIN"
echo "===================================="
echo ""

# Check if running as root
if [ "$EUID" -ne 0 ]; then 
    echo "⚠️  Please run as root (use: sudo bash setup-domain.sh your@email.com)"
    exit 1
fi

# Check if email provided
if [ "$EMAIL" = "your-email@example.com" ]; then
    echo "⚠️  Usage: sudo bash setup-domain.sh YOUR_EMAIL_ADDRESS"
    echo "Example: sudo bash setup-domain.sh john@example.com"
    exit 1
fi

echo "📋 Configuration:"
echo "   Domain: $DOMAIN"
echo "   API Port: $API_PORT"
echo "   Email: $EMAIL"
echo ""

# Install Nginx
echo "📦 Installing Nginx..."
apt update -qq
apt install -y nginx

# Start Nginx
echo "🚀 Starting Nginx..."
systemctl start nginx
systemctl enable nginx

# Configure firewall
echo "🔥 Configuring firewall..."
ufw allow 'Nginx Full' 2>/dev/null || echo "   (UFW not active, skipping)"

# Create Nginx config
echo "⚙️  Creating Nginx configuration..."
cat > /etc/nginx/sites-available/$DOMAIN <<'EOF'
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
ln -sf /etc/nginx/sites-available/$DOMAIN /etc/nginx/sites-enabled/

# Remove default site if exists
if [ -L /etc/nginx/sites-enabled/default ]; then
    echo "🗑️  Removing default Nginx site..."
    rm /etc/nginx/sites-enabled/default
fi

# Test Nginx configuration
echo "🔍 Testing Nginx configuration..."
if nginx -t; then
    echo "   ✅ Nginx configuration is valid"
else
    echo "   ❌ Nginx configuration has errors!"
    exit 1
fi

# Reload Nginx
echo "🔄 Reloading Nginx..."
systemctl reload nginx

echo ""
echo "✅ HTTP setup complete!"
echo ""

# Check if API is running
echo "🔍 Checking if Books API is running on port $API_PORT..."
if curl -s http://localhost:$API_PORT/api/v1/books > /dev/null 2>&1; then
    echo "   ✅ Books API is responding"
else
    echo "   ⚠️  Books API is not responding on port $API_PORT"
    echo "   Make sure Docker containers are running: docker-compose ps"
fi

echo ""
echo "📡 Checking DNS resolution..."
DNS_IP=$(dig +short $DOMAIN | head -n1)
if [ -n "$DNS_IP" ]; then
    echo "   ✅ DNS resolves to: $DNS_IP"
    if [ "$DNS_IP" != "161.35.82.205" ]; then
        echo "   ⚠️  Warning: Expected 161.35.82.205, got $DNS_IP"
        echo "   Wait a few minutes for DNS propagation"
    fi
else
    echo "   ⚠️  DNS not yet propagated. Wait 5-60 minutes."
    echo "   Check with: nslookup $DOMAIN"
fi

echo ""
echo "🔐 Installing SSL certificate (HTTPS)..."
apt install -y certbot python3-certbot-nginx

echo ""
echo "🔑 Getting Let's Encrypt certificate..."
if certbot --nginx -d $DOMAIN --email $EMAIL --agree-tos --non-interactive --redirect; then
    echo "   ✅ SSL certificate installed successfully!"
else
    echo "   ⚠️  SSL certificate installation failed."
    echo "   This usually happens if DNS is not propagated yet."
    echo "   Wait 10-30 minutes and run manually:"
    echo "   sudo certbot --nginx -d $DOMAIN --email $EMAIL --agree-tos"
    exit 0
fi

echo ""
echo "🧪 Testing auto-renewal..."
certbot renew --dry-run

echo ""
echo "============================================"
echo "🎉 Setup Complete!"
echo "============================================"
echo ""
echo "Your API is now available at:"
echo "   🌐 http://$DOMAIN/api/v1/books"
echo "   🔒 https://$DOMAIN/api/v1/books"
echo ""
echo "Test it:"
echo "   curl https://$DOMAIN/api/v1/books"
echo ""
echo "View logs:"
echo "   sudo tail -f /var/log/nginx/books-api-access.log"
echo "   sudo tail -f /var/log/nginx/books-api-error.log"
echo ""
echo "SSL certificate will auto-renew every 60 days."
echo ""
