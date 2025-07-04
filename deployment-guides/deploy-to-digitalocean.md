# 🌐 N1neTails DigitalOcean Deployment Guide

This document walks through setting up the **N1neTails** application with Docker Compose and configuring Nginx as a reverse proxy on a Digitalocean Ubuntu server.

- Table of Contents
  - Postgres Database
  - Domain or subdomain
  - Docker setup
  - Nginx setup
  - Setup HTTPS with certbot

---

## 1. Create Postgres Database
For this implementation I decided to use DigitalOcean to host my Database Cluster. 
You can read more about setting up Database Clusters on DigitalOcean here: 
https://docs.digitalocean.com/products/databases/postgresql/how-to/create/

You can use DigitalOcean to help you with creating new users and databases within your cluster.
Steps 1 & 2 can be done on DigitalOcean.

### Step 1: Create `n1netails` user (can do done on DigitalOcean):
```sql
CREATE USER n1netails WITH PASSWORD 'your_password';
```

### Step 2: Create `n1netails` database (can do done on DigitalOcean):
```sql
CREATE DATABASE "n1netails"
  WITH
  OWNER = n1netails
  ENCODING = 'UTF8'
  LOCALE_PROVIDER = 'libc'
  CONNECTION LIMIT = -1
  IS_TEMPLATE = False;
```

### Step 3: Grant n1netails user access to n1netails database:
```sql
GRANT CREATE ON DATABASE n1netails TO n1netails;
```

### Step 4: Generate `ntail` schema:
```sql
CREATE SCHEMA IF NOT EXISTS ntail AUTHORIZATION n1netails;
```

### Step 5: Set timezone to UTC:
```sql
ALTER DATABASE n1netails SET timezone TO 'Etc/UTC';
```

## 2. Create a Subdomain (e.g., `app.n1netails.com`)
You can set up n1netails using your own domain or subdomain.
You can configure DNS settings where you registered your domain (e.g., Squarespace, GoDaddy, Namecheap).

**Add an A Record**:

* **Host**: `app`
* **Type**: `A`
* **Value**: Your server’s public IP address
* **TTL**: Default (3600)

Save the DNS settings.

📌 *It may take a few minutes (or up to 24 hours) for DNS to propagate.*

## 3. Docker Compose Setup

Set up docker compose on ubuntu by following these docs:
- [Docker Engine](https://docs.docker.com/engine/install/ubuntu/#install-using-the-repository)
- [Docker Compose](https://docs.docker.com/compose/install/linux/)


Add the following directories within the digital ocean server `projects/n1netails`

Create a `docker-compose.yml` file inside of `projects/n1netails` with the following content:

```yaml
services:
  api:
    image: shahidfo/n1netails-api:latest
    container_name: n1netails-api
    ports:
      - "9901:9901"
    depends_on:
      liquibase:
        condition: service_completed_successfully
    environment:
      SPRING_PROFILE_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:postgresql://<digital-ocean-postgres-db-url>:<digital-ocean-postgres-db-port>/n1netails
      SPRING_DATASOURCE_USERNAME: n1netails
      SPRING_DATASOURCE_PASSWORD: <n1netails_password>
      N1NETAILS_PASSKEY_RELYING_PARTY_ID: <domain-url> # e.g. app.n1netails.com
      N1NETAILS_PASSKEY_ORIGINS: https://<domain-url> # e.g. https://app.n1netails.com
      OPENAI_ENABLED: true
      OPENAI_API_KEY: <your_openai_api_key>
      OPENAI_API_URL: https://api.openai.com

  ui:
    image: shahidfo/n1netails-ui:latest
    container_name: n1netails-ui
    ports:
      - "9900:9900"
    depends_on:
      - api
    environment:
      SPRING_PROFILE_ACTIVE: docker
      API_BASE_URL: https://<domain-url>
      OPENAI_ENABLED: true

  liquibase:
    image: shahidfo/n1netails-liquibase:latest
    container_name: n1netails-liquibase
    environment:
      SPRING_PROFILE_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:postgresql://<digital-ocean-postgres-db-url>:<digital-ocean-postgres-db-port>/n1netails
      SPRING_DATASOURCE_USERNAME: n1netails
      SPRING_DATASOURCE_PASSWORD: <n1netails_password>
```

**Prepare Docker Compose** with above config and deploy:

```bash
# Stop and remove old containers
docker compose down -v

# Start containers in detached mode
docker compose up -d

# tail docker logs (optional)
docker compose logs -f
```

---

## 4. Nginx Setup

### Step 1: **Install Nginx** on your server if not installed:

```bash
# Update the package list to ensure you get the latest available versions
sudo apt update

# Install the Nginx web server
sudo apt install nginx
```

### Step 2: Create the Nginx config file

Create the file `/etc/nginx/sites-available/n1netails.conf` with the following content:

```nginx
server {
    listen 80;
    server_name <domain-url>;
    
    add_header X-Frame-Options "SAMEORIGIN";
    add_header X-Content-Type-Options "nosniff";
    add_header X-XSS-Protection "1; mode=block";

    # Route API calls to backend
    location /ninetails/ {
        proxy_pass http://localhost:9901/ninetails/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # Route OpenAPI config (Swagger needs this)
    location /v3/ {
        proxy_pass http://localhost:9901/v3/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # Route Swagger UI to backend
    location /swagger-ui/ {
        proxy_pass http://localhost:9901/swagger-ui/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # Route all other requests to frontend
    location / {
        proxy_pass http://localhost:9900/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}

```

### Step 3: Enable the site and disable default

```bash
# Enable the new site
sudo ln -s /etc/nginx/sites-available/n1netails.conf /etc/nginx/sites-enabled/n1netails.conf

# Disable the default site
sudo rm /etc/nginx/sites-enabled/default
```

### Step 4: Test and reload Nginx

```bash
# Test the Nginx configuration for syntax errors
sudo nginx -t

# Reload Nginx to apply the new configuration without downtime
sudo systemctl reload nginx

# Check nginx logs
sudo tail -f /var/log/nginx/error.log
```

---

## 5. Install Certbot & Enable HTTPS

```bash
# Update packages
sudo apt update

# Install Certbot and Nginx plugin
sudo apt install certbot python3-certbot-nginx
```

Then run:

```bash
# Automatically obtain and configure SSL
sudo certbot --nginx
```

Follow the prompts:

* Choose your subdomain (e.g., `app.n1netails.com`)
* Redirect HTTP to HTTPS when prompted ✅

✅ You now have HTTPS enabled.

---

### Step 1: Verify HTTPS

Visit: 

Visit your domain to varify https

Example
```
https://app.n1netails.com
```

Check for the padlock icon in the address bar.

---

### Step 2: Enable SSL Certificate Auto-Renewal

Let’s Encrypt certificates expire every 90 days. Certbot sets up a systemd timer automatically.

Verify with:

```bash
systemctl list-timers | grep certbot
```

Test the renewal process:

```bash
sudo certbot renew --dry-run
```
