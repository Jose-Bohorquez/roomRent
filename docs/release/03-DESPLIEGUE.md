# 03 — Guía de Despliegue

**Versión:** RC1
**Fecha:** 2026-07-02
**Proyecto:** RoomRent — room-rent.xyz
**Rama de producción:** `main` (GitHub == Producción, sin excepciones)

---

## 1. Requisitos Previos

El servidor de producción es una instancia EC2 Ubuntu. Antes de cualquier operación verificar que estos paquetes estén instalados:

| Herramienta | Versión mínima | Verificación |
|---|---|---|
| git | 2.x | `git --version` |
| Docker Engine | 24.x | `docker --version` |
| Docker Compose (plugin) | 2.x | `docker compose version` |
| Nginx | 1.24.x | `nginx -v` |
| Certbot | 2.x | `certbot --version` |

```bash
# Instalación rápida en Ubuntu 22.04/24.04
sudo apt-get update
sudo apt-get install -y git nginx certbot python3-certbot-nginx

# Docker Engine (método oficial)
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker $USER
newgrp docker
```

---

## 2. Variables de Entorno

Todas las variables se definen en `/opt/roomrent/.env`. Este archivo **no debe existir en el repositorio** ni en ningún backup sin cifrar.

```bash
sudo mkdir -p /opt/roomrent
sudo nano /opt/roomrent/.env
```

Contenido completo requerido:

```dotenv
# ── Spring Boot ──────────────────────────────────────────────
SPRING_PROFILES_ACTIVE=prod

# ── MongoDB ──────────────────────────────────────────────────
SPRING_MONGODB_URI=mongodb://roomrent:CAMBIAR_PASSWORD@mongo:27017/room?authSource=admin
MONGO_INITDB_ROOT_USERNAME=root
MONGO_INITDB_ROOT_PASSWORD=CAMBIAR_ROOT_PASSWORD
MONGO_INITDB_DATABASE=room

# ── Seguridad / JWT ──────────────────────────────────────────
JWT_BASE64_SECRET=CAMBIAR_SECRET_BASE64_512BITS

# ── Correo ───────────────────────────────────────────────────
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=noreply@room-rent.xyz
SPRING_MAIL_PASSWORD=CAMBIAR_APP_PASSWORD
SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true

# ── JHipster ─────────────────────────────────────────────────
JHIPSTER_MAIL_BASE_URL=https://room-rent.xyz

# ── Frontend (Vite build-time) ───────────────────────────────
VITE_API_BASE=https://room-rent.xyz
```

> **Seguridad:** `JWT_BASE64_SECRET` debe generarse con `openssl rand -base64 64`.
> El password de MongoDB debe ser diferente para el usuario `root` y el usuario `roomrent`.

---

## 3. Proceso de Build

La imagen Docker utiliza un build multi-stage con tres etapas:

| Stage | Base | Duración aprox. |
|---|---|---|
| 1 — Maven build | `maven:3.9-eclipse-temurin-21` | ~10 min |
| 2 — Node/Vite build | `node:20-alpine` | ~3 min |
| 3 — Runtime JRE | `eclipse-temurin:21-jre-alpine` | ~2 min |

**Tamaño final de imagen:** ~560 MB

```bash
cd /opt/roomrent
git clone https://github.com/Jose-Bohorquez/roomRent.git app
cd app

# Build completo (15-20 minutos en primera ejecución)
docker build -t roomrent-app:latest .
```

Para builds posteriores Docker reutiliza caché de capas; el tiempo se reduce a ~5 min si sólo cambió el código Java o ~2 min si sólo cambió el frontend.

---

## 4. Configuración de Nginx

```bash
sudo nano /etc/nginx/sites-available/roomrent
```

```nginx
server {
    listen 80;
    server_name room-rent.xyz www.room-rent.xyz;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl;
    server_name room-rent.xyz www.room-rent.xyz;

    ssl_certificate     /etc/letsencrypt/live/room-rent.xyz/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/room-rent.xyz/privkey.pem;
    include             /etc/letsencrypt/options-ssl-nginx.conf;
    ssl_dhparam         /etc/letsencrypt/ssl-dhparams.pem;

    client_max_body_size 50M;

    location / {
        proxy_pass         http://localhost:8080;
        proxy_set_header   Host              $host;
        proxy_set_header   X-Real-IP         $remote_addr;
        proxy_set_header   X-Forwarded-For   $proxy_add_x_forwarded_for;
        proxy_set_header   X-Forwarded-Proto $scheme;
        proxy_read_timeout 120s;
    }
}
```

```bash
sudo ln -sf /etc/nginx/sites-available/roomrent /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

---

## 5. Certificado SSL (Let's Encrypt)

```bash
# Requiere que Nginx esté activo y el DNS ya apunte a esta instancia
sudo certbot --nginx -d room-rent.xyz -d www.room-rent.xyz \
  --non-interactive --agree-tos -m admin@room-rent.xyz

# Verificar renovación automática
sudo certbot renew --dry-run
```

El cron de renovación automática lo instala Certbot en `/etc/cron.d/certbot`.

---

## 6. Flujo Completo desde Cero (Fresh Deploy)

```bash
# 1. Clonar repositorio
sudo mkdir -p /opt/roomrent
cd /opt/roomrent
git clone https://github.com/Jose-Bohorquez/roomRent.git app
cd app

# 2. Crear y poblar archivo de entorno
sudo cp /opt/roomrent/.env .env   # si ya lo tienes preparado
# o editar manualmente: sudo nano .env

# 3. Build de imagen
docker build -t roomrent-app:latest .

# 4. Configurar Nginx (ver sección 4) y SSL (ver sección 5)
#    Nginx debe estar activo antes de certbot

# 5. Levantar servicios
docker compose --env-file .env up -d

# 6. Verificar estado
docker compose ps
docker compose logs -f app --tail=50

# 7. Smoke tests (ver sección 9)
```

---

## 7. Actualización (Nuevo Release)

Regla fundamental: **sólo se despliega lo que está en `main`**.

```bash
cd /opt/roomrent/app

# 1. Traer cambios
git pull origin main

# 2. Etiquetar imagen anterior antes de sobreescribir
docker tag roomrent-app:latest roomrent-app:previous

# 3. Build nueva imagen
docker build -t roomrent-app:latest .

# 4. Reemplazar contenedor de la app (MongoDB no se reinicia)
docker compose up -d app

# 5. Health check
curl -sf https://room-rent.xyz/management/health | jq .status

# 6. Si el health check falla → iniciar rollback (ver sección 8)
```

---

## 8. Rollback

Si el nuevo deploy falla, revertir a la imagen anterior toma menos de 1 minuto:

```bash
# Restaurar imagen etiquetada como previous
docker tag roomrent-app:previous roomrent-app:latest

# Reiniciar el contenedor de la app
docker compose up -d app

# Confirmar que volvió al estado anterior
curl -sf https://room-rent.xyz/management/health | jq .status
```

> Si no existe la etiqueta `:previous` (primer deploy fallido), reconstruir desde el commit anterior:
> ```bash
> git log --oneline -5     # identificar commit estable
> git checkout <commit>
> docker build -t roomrent-app:latest .
> docker compose up -d app
> git checkout main        # volver a main
> ```

---

## 9. Smoke Tests Post-Deploy

Ejecutar los siguientes checks después de cada deploy antes de considerarlo exitoso:

```bash
# 1. Health check del backend (debe retornar {"status":"UP"})
curl -sf https://room-rent.xyz/management/health | jq .status

# 2. Verificar HTTPS activo (debe responder 200)
curl -o /dev/null -w "%{http_code}" https://room-rent.xyz/

# 3. Verificar redirect HTTP→HTTPS (debe responder 301)
curl -o /dev/null -w "%{http_code}" http://room-rent.xyz/

# 4. Login check (debe retornar token JWT)
curl -sf -X POST https://room-rent.xyz/api/authenticate \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin","rememberMe":false}' \
  | jq -r '.id_token' | cut -c1-20

# 5. Listar publicaciones públicas (debe retornar array JSON)
curl -sf "https://room-rent.xyz/api/publicacion-inmuebles?page=0&size=5" \
  | jq 'length'

# 6. Verificar contenedores corriendo
docker compose ps
```

Resultado esperado: todos los checks retornan el valor indicado sin errores.

---

## 10. Protocolo si la Instancia EC2 se Apaga y Reinicia

Cuando una instancia EC2 se detiene y vuelve a iniciar, la **IP pública cambia** si no es una Elastic IP. El procedimiento es:

```bash
# 1. Obtener la nueva IP pública
curl -s http://169.254.169.254/latest/meta-data/public-ipv4

# 2. Si el DNS (Route 53 u otro) no apunta aún a la nueva IP,
#    actualizar el registro A de room-rent.xyz

# 3. Verificar que los contenedores reiniciaron automáticamente
docker compose ps
# Si no levantaron solos:
cd /opt/roomrent/app
docker compose --env-file .env up -d

# 4. Nginx debería iniciar automáticamente (habilitado en systemd)
sudo systemctl status nginx
# Si no está activo:
sudo systemctl start nginx

# 5. Verificar SSL (certbot puede necesitar reload si nginx no arrancó)
sudo nginx -t && sudo systemctl reload nginx

# 6. Ejecutar smoke tests completos (sección 9)
```

> **Recomendación:** Asignar una Elastic IP a la instancia EC2 para que la IP no cambie entre reinicios y el DNS permanezca estable.

Para que Docker Compose levante automáticamente tras un reinicio, asegurarse de que el servicio tenga `restart: unless-stopped` en el `docker-compose.yml` y que Docker esté habilitado en systemd:

```bash
sudo systemctl enable docker
```
