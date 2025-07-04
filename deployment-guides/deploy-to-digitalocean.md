# N1neTails Deployment Guide

This document walks through setting up the **N1neTails** application with Docker Compose and configuring Nginx as a reverse proxy on a Digitalocean Ubuntu server.

- Requirements
  - Digitalocean Postgres Database

---

## 1. Docker Compose Setup

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
      N1NETAILS_PASSKEY_RELYING_PARTY_ID: <digital-ocean-ip-address>
      N1NETAILS_PASSKEY_ORIGINS: http://<digital-ocean-ip-address>:9900,http://<digital-ocean-ip-address>:9901
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
      API_BASE_URL: http://<digital-ocean-ip-address>:9901
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

## 2. Nginx Setup

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
    server_name <digital-ocean-ip-address>;

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
```

---

## Summary

* Backend API exposed on port `9901`
* Frontend UI exposed on port `9900`
* Nginx reverse proxy routes:
    * `/ninetails/`, `/v3/` and `/swagger-ui/` → API backend (`localhost:9901`)
    * All other paths → frontend UI (`localhost:9900`)
* Nginx config is at `/etc/nginx/sites-available/n1netails.conf` and symlinked into `sites-enabled`
* Docker Compose manages containers for API, UI, and Liquibase
