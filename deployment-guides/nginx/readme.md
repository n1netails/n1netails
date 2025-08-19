# Nginx + Certbot Guide

This guide provides useful commands and configurations for running an application behind **Nginx** with **HTTPS certificates** from Certbot (Let's Encrypt).

---

## 📌 Nginx Basics

### Install Nginx
```bash
sudo apt update
sudo apt install nginx -y
````

### Start/Stop/Reload Nginx

```bash
# Start nginx
sudo systemctl start nginx

# Stop nginx
sudo systemctl stop nginx

# Restart nginx completely
sudo systemctl restart nginx

# Reload config without dropping connections
sudo systemctl reload nginx

# Check nginx status
systemctl status nginx

# Test nginx config syntax (always run before reload/restart!)
sudo nginx -t
```

### Nginx Config Files

* Default location: `/etc/nginx/nginx.conf`
* Site configs: `/etc/nginx/sites-available/`
* Enabled sites: `/etc/nginx/sites-enabled/`

Enable a site:

```bash
sudo ln -s /etc/nginx/sites-available/myapp /etc/nginx/sites-enabled/
```

Disable a site:

```bash
sudo rm /etc/nginx/sites-enabled/myapp
```

---

## 📌 Example Reverse Proxy Config

Example config for a Spring Boot backend (`localhost:9000`) and Angular frontend (`/var/www/myapp`):

```nginx
server {
    server_name app.example.com;

    # Redirect HTTP → HTTPS
    listen 80;
    listen [::]:80;
    return 301 https://$host$request_uri;
}

server {
    server_name app.example.com;

    listen 443 ssl;
    listen [::]:443 ssl;

    # SSL certs provided by Certbot
    ssl_certificate /etc/letsencrypt/live/app.example.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/app.example.com/privkey.pem;

    # Angular frontend
    root /var/www/myapp;
    index index.html;
    location / {
        try_files $uri /index.html;
    }

    # Spring Boot backend
    location /api/ {
        proxy_pass http://localhost:9000/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto https;
    }
}
```

---

## 📌 Certbot (Let's Encrypt) HTTPS

### Install Certbot

```bash
sudo apt install certbot python3-certbot-nginx -y
```

### Get an SSL Certificate

```bash
sudo certbot --nginx -d app.example.com
```

### Test Renewal

```bash
sudo certbot renew --dry-run
```

Certificates are stored in:

```
/etc/letsencrypt/live/<your-domain>/
```

---

## 📌 Useful Tips

* Always test config before reloading:

  ```bash
  sudo nginx -t
  ```
* Reload instead of restart when possible:

  ```bash
  sudo systemctl reload nginx
  ```
* To check logs:

  ```bash
  tail -f /var/log/nginx/error.log
  tail -f /var/log/nginx/access.log
  ```
* Certificates renew automatically via `systemd` timer.

---

## ✅ Quick Reference

| Action                   | Command                              |
| ------------------------ | ------------------------------------ |
| Test config              | `sudo nginx -t`                      |
| Reload config            | `sudo systemctl reload nginx`        |
| Restart Nginx            | `sudo systemctl restart nginx`       |
| Check status             | `systemctl status nginx`             |
| View access logs         | `tail -f /var/log/nginx/access.log`  |
| View error logs          | `tail -f /var/log/nginx/error.log`   |
| Obtain SSL cert          | `sudo certbot --nginx -d yourdomain` |
| Test certificate renewal | `sudo certbot renew --dry-run`       |

---

