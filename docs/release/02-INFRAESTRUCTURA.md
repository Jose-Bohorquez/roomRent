# RoomRent — Infraestructura y Despliegue

> **Versión:** RC1
> **Fecha:** 2026-07-02
> **Estado:** Release Candidate

---

## Tabla de contenido

1. [Visión general](#1-visión-general)
2. [AWS EC2](#2-aws-ec2)
3. [Docker](#3-docker)
4. [nginx](#4-nginx)
5. [TLS — Let's Encrypt](#5-tls--lets-encrypt)
6. [Red y firewall](#6-red-y-firewall)
7. [Variables de entorno](#7-variables-de-entorno)
8. [Autostart y systemd](#8-autostart-y-systemd)
9. [Monitoreo](#9-monitoreo)
10. [Recursos del sistema](#10-recursos-del-sistema)
11. [Procedimientos operativos](#11-procedimientos-operativos)

---

## 1. Visión general

```
┌──────────────────────────────────────────────────────────────┐
│                    AWS EC2 — us-east-1                       │
│                    t2.micro — Ubuntu 24.04                   │
│                                                              │
│  ┌─────────────────────────────────────────────────────┐    │
│  │  nginx 1.26.x (systemd service)                      │    │
│  │  TLS :443 → proxy_pass :8080                         │    │
│  │  HTTP :80 → redirect a HTTPS                         │    │
│  └─────────────────────────────────────────────────────┘    │
│                                                              │
│  ┌─────────────────────────────────────────────────────┐    │
│  │  Docker Engine 27.x (systemd service)                │    │
│  │                                                      │    │
│  │  ┌─────────────────────┐  ┌──────────────────────┐  │    │
│  │  │  roomrent-app-1      │  │  roomrent-mongo-1    │  │    │
│  │  │  Spring Boot / JRE21 │  │  MongoDB 7           │  │    │
│  │  │  127.0.0.1:8080      │  │  red interna         │  │    │
│  │  └─────────────────────┘  └──────────────────────┘  │    │
│  │                                                      │    │
│  │  Volúmenes nombrados:                                │    │
│  │    roomrent-mongo-data    (datos MongoDB)            │    │
│  │    roomrent-uploads-data  (archivos de usuarios)     │    │
│  └─────────────────────────────────────────────────────┘    │
│                                                              │
│  /opt/roomrent/.env   ← variables de entorno (no en git)    │
│  /etc/nginx/          ← configuración nginx                  │
│  /etc/letsencrypt/    ← certificados TLS                     │
└──────────────────────────────────────────────────────────────┘
```

---

## 2. AWS EC2

### Especificaciones de la instancia

| Parámetro | Valor |
|---|---|
| Tipo de instancia | t2.micro |
| vCPUs | 1 |
| RAM | 1 GB (908 MB disponibles) |
| Almacenamiento | 38 GB (EBS gp2) |
| Sistema operativo | Ubuntu 24.04 LTS |
| Región | us-east-1 (Norte de Virginia) |
| Plan | AWS Academy / Student Benefit |
| IP | Elástica (asociada al dominio room-rent.xyz) |

### Comportamiento de apagado periodico (AWS Student Plan)

Las instancias del plan educativo de AWS tienen restricciones que difieren de las cuentas de producción convencionales:

- **Apagado automático programado**: AWS puede apagar la instancia al agotar el saldo de créditos del periodo. El saldo se renueva periódicamente (generalmente mensual).
- **Pérdida de IP pública**: Si la instancia se detiene y la IP elástica no está asociada, la IP pública cambia al reiniciar. La IP elástica asignada al dominio `room-rent.xyz` mitiga este riesgo siempre que permanezca asociada.
- **Persistencia de datos**: Los volúmenes EBS y los volúmenes Docker nombrados (`roomrent-mongo-data`, `roomrent-uploads-data`) sobreviven al apagado de la instancia. Los datos no se pierden al reiniciar.
- **Arranque automático**: Al reiniciar la instancia, Docker y nginx arrancan automáticamente via systemd (ver sección 8). La aplicación vuelve a estar disponible sin intervención manual.

### Recomendaciones operativas para el plan student

```bash
# Verificar el estado de la instancia antes de cada sesión de trabajo
aws ec2 describe-instance-status --instance-ids <id>

# Monitorear el saldo de créditos AWS en la consola
# https://console.aws.amazon.com/billing/home#/credits

# Asegurarse de que la IP elástica esté siempre asociada a la instancia
aws ec2 describe-addresses
```

---

## 3. Docker

### Versiones

| Componente | Versión |
|---|---|
| Docker Engine | 27.x |
| Docker Compose | v2.x (plugin integrado) |
| Imagen base de compilación | node:24-alpine (stage 1 — Vite build) |
| Imagen base de compilación | maven:3-eclipse-temurin-21 (stage 2 — Maven build) |
| Imagen de runtime | eclipse-temurin:21-jre (stage 3 — runtime) |

### Build multi-stage

El Dockerfile utiliza tres etapas para producir una imagen final mínima:

```dockerfile
# Stage 1: Build del portal React con Vite
FROM node:24-alpine AS frontend-builder
WORKDIR /build
COPY src/portal/package*.json ./
RUN npm ci
COPY src/portal/ ./
RUN npm run build
# Resultado: /build/dist/ (archivos estáticos del portal React)

# Stage 2: Compilación de la aplicación Spring Boot con Maven
FROM maven:3-eclipse-temurin-21 AS backend-builder
WORKDIR /build
COPY pom.xml ./
RUN mvn dependency:go-offline -q
COPY src/main/ ./src/main/
COPY --from=frontend-builder /build/dist/ ./src/main/resources/static/portal/
RUN mvn package -DskipTests -q
# Resultado: /build/target/roomrent-app.jar

# Stage 3: Runtime mínimo
FROM eclipse-temurin:21-jre AS runtime
RUN groupadd -g 101 roomrent && useradd -u 100 -g 101 -s /bin/false roomrent
WORKDIR /app
COPY --from=backend-builder /build/target/roomrent-app.jar ./app.jar
RUN mkdir -p /app/uploads && chown -R roomrent:roomrent /app
USER roomrent
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose

Archivo ubicado en `/opt/roomrent/docker-compose.yml`:

```yaml
services:
  app:
    container_name: roomrent-app-1
    image: roomrent/app:latest
    restart: unless-stopped
    ports:
      - "127.0.0.1:8080:8080"   # Solo accesible desde localhost (nginx)
    env_file:
      - .env
    volumes:
      - roomrent-uploads-data:/app/uploads
    depends_on:
      - mongo
    networks:
      - roomrent-internal

  mongo:
    container_name: roomrent-mongo-1
    image: mongo:7
    restart: unless-stopped
    volumes:
      - roomrent-mongo-data:/data/db
    networks:
      - roomrent-internal
    # Sin exposición de puertos al host — acceso solo interno

volumes:
  roomrent-mongo-data:
    name: roomrent-mongo-data
  roomrent-uploads-data:
    name: roomrent-uploads-data

networks:
  roomrent-internal:
    driver: bridge
```

### Contenedores

| Nombre | Imagen | Puerto expuesto | Descripción |
|---|---|---|---|
| `roomrent-app-1` | `roomrent/app:latest` | `127.0.0.1:8080` | Spring Boot — acceso solo desde nginx |
| `roomrent-mongo-1` | `mongo:7` | Ninguno (red interna) | MongoDB — sin acceso externo |

### Volúmenes nombrados

| Nombre del volumen | Punto de montaje | Contenido |
|---|---|---|
| `roomrent-mongo-data` | `/data/db` en mongo-1 | Archivos de datos de MongoDB (WiredTiger) |
| `roomrent-uploads-data` | `/app/uploads` en app-1 | Archivos subidos por usuarios (imágenes, PDFs) |

Los volúmenes nombrados persisten independientemente del ciclo de vida de los contenedores. Un `docker compose down` no elimina los volúmenes. Para eliminarlos se requiere `docker compose down -v` (operación destructiva).

### Usuario no-root en el contenedor

```
Usuario:  roomrent
UID:      100
GID:      101
Shell:    /bin/false (no puede iniciar sesión)
Home:     /app
```

El proceso de Spring Boot corre con este usuario de bajo privilegio. Si el proceso es comprometido, el atacante no tendrá acceso root al contenedor.

### Archivos subidos por usuarios

```
Ruta interna del contenedor: /app/uploads/
Ruta del volumen Docker:     roomrent-uploads-data
URL pública:                 https://room-rent.xyz/uploads/<archivo>

Nginx hace proxy_pass a http://127.0.0.1:8080/uploads/
Spring Boot sirve los archivos desde el sistema de archivos local (/app/uploads/)
```

---

## 4. nginx

### Versión y configuración general

| Parámetro | Valor |
|---|---|
| Versión | 1.26.x (Ubuntu 24.04 APT) |
| Modo de instalación | Sistema (APT) — no Docker |
| Servicio systemd | `nginx.service` (enabled, arranca con el sistema) |
| Configuración principal | `/etc/nginx/nginx.conf` |
| Sitio activo | `/etc/nginx/sites-enabled/room-rent.xyz` |

### Configuración del sitio (estructura)

```nginx
# /etc/nginx/sites-available/room-rent.xyz

# Bloque HTTP — redirige todo a HTTPS
server {
    listen 80;
    listen [::]:80;
    server_name room-rent.xyz www.room-rent.xyz;

    # Let's Encrypt ACME challenge (para renovación de certificados)
    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }

    # Todo lo demás → redirect permanente a HTTPS
    location / {
        return 301 https://$host$request_uri;
    }
}

# Bloque HTTPS principal
server {
    listen 443 ssl;
    listen [::]:443 ssl;
    http2 on;
    server_name room-rent.xyz;

    # --- Certificados TLS ---
    ssl_certificate     /etc/letsencrypt/live/room-rent.xyz/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/room-rent.xyz/privkey.pem;
    ssl_trusted_certificate /etc/letsencrypt/live/room-rent.xyz/chain.pem;

    # --- Protocolos y cifrado ---
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:...;
    ssl_prefer_server_ciphers off;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 1d;
    ssl_session_tickets off;

    # --- OCSP Stapling ---
    ssl_stapling on;
    ssl_stapling_verify on;

    # --- Headers de seguridad ---
    add_header Strict-Transport-Security "max-age=63072000" always;
    add_header X-Frame-Options DENY;
    add_header X-Content-Type-Options nosniff;
    add_header Referrer-Policy no-referrer-when-downgrade;
    add_header Permissions-Policy "camera=(), microphone=(), geolocation=()";

    # --- Compresión ---
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css application/json application/javascript
               text/xml application/xml image/svg+xml;

    # --- Rutas ---

    # API REST y gestión — proxy al backend Spring Boot
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_read_timeout 60s;
    }

    location /management/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /uploads/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        client_max_body_size 50M;
    }

    # Portal React — SPA routing
    location /portal/ {
        proxy_pass http://127.0.0.1:8080/portal/;
        proxy_set_header Host $host;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Panel Angular — SPA routing (fallback a index.html)
    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### Características habilitadas

| Característica | Estado | Descripción |
|---|---|---|
| TLS 1.2 + 1.3 | Activo | Protocolos seguros, TLS 1.0/1.1 deshabilitados |
| HTTP/2 | Activo | Multiplexado de conexiones, mejor rendimiento |
| HSTS | Activo | `max-age=63072000` (2 años) — fuerza HTTPS |
| gzip | Activo | Comprime JSON, CSS, JS antes de enviar |
| OCSP Stapling | Activo | Mejora el tiempo de handshake TLS |
| X-Frame-Options DENY | Activo | Previene clickjacking |
| X-Content-Type-Options | Activo | Previene MIME-type sniffing |
| Tamaño máximo de upload | 50 MB | `client_max_body_size` en location /uploads/ |

---

## 5. TLS — Let's Encrypt

### Certificados

| Parámetro | Valor |
|---|---|
| Autoridad certificadora | Let's Encrypt (ACME v2) |
| Cliente ACME | Certbot |
| Dominio | `room-rent.xyz` |
| Tipo de certificado | RSA 2048 bits (predeterminado de Certbot) |
| Validez | 90 días por emisión |
| Archivo de certificado | `/etc/letsencrypt/live/room-rent.xyz/fullchain.pem` |
| Archivo de clave privada | `/etc/letsencrypt/live/room-rent.xyz/privkey.pem` |
| Cadena de confianza | `/etc/letsencrypt/live/room-rent.xyz/chain.pem` |

### Renovación automática

```bash
# Certbot instala un timer de systemd que ejecuta la renovación dos veces al día
systemctl status certbot.timer

# La renovación solo ocurre si el certificado vence en menos de 30 días
# Si la renovación falla, Certbot envía email de alerta al correo registrado

# Verificar que la renovación funciona (dry-run)
certbot renew --dry-run

# Estado de los certificados
certbot certificates
```

El timer de systemd de Certbot (`certbot.timer`) se activa dos veces al día. La renovación real solo se ejecuta cuando el certificado tiene menos de 30 días de vigencia restante. Después de renovar, Certbot ejecuta `nginx -s reload` para que nginx cargue el nuevo certificado sin downtime.

### Proceso de renovación

```
certbot.timer (2x/día)
  ↓
certbot renew
  ↓ (si faltan < 30 días)
Obtiene nuevo certificado via ACME HTTP-01 challenge
  ↓
Escribe en /etc/letsencrypt/live/room-rent.xyz/
  ↓
Hook post-renewal: nginx -s reload
  ↓
nginx carga el nuevo certificado (sin downtime)
```

---

## 6. Red y firewall

### Puertos expuestos al exterior

| Puerto | Protocolo | Servicio | Estado |
|---|---|---|---|
| 22 | TCP | SSH | Abierto (acceso de administración) |
| 80 | TCP | HTTP | Abierto (redirige a HTTPS) |
| 443 | TCP | HTTPS | Abierto (tráfico de aplicación) |

### Puertos internos (no expuestos)

| Puerto | Servicio | Escucha en |
|---|---|---|
| 8080 | Spring Boot | `127.0.0.1:8080` (solo localhost) |
| 27017 | MongoDB | Red interna Docker (`roomrent-internal`) |

### Firewall (UFW / AWS Security Groups)

```
# UFW en el host Ubuntu
ufw status verbose

# Reglas activas:
22/tcp   ALLOW IN  Anywhere   ← SSH
80/tcp   ALLOW IN  Anywhere   ← HTTP → redirect HTTPS
443/tcp  ALLOW IN  Anywhere   ← HTTPS
8080     DENY  IN  Anywhere   ← Spring Boot NO expuesto (nginx hace el proxy)

# AWS Security Group (nivel de red, antes del host)
# Debe coincidir con las reglas UFW:
#   Inbound: TCP 22, 80, 443 desde 0.0.0.0/0
#   Outbound: All traffic (para actualizaciones, Let's Encrypt, etc.)
```

### Seguridad de red Docker

MongoDB (`roomrent-mongo-1`) no tiene ningún puerto mapeado al host. Solo es accesible desde dentro de la red Docker `roomrent-internal`. Spring Boot se conecta a MongoDB usando el nombre del servicio como hostname:

```
mongodb://roomrent-mongo-1:27017/roomrent
```

---

## 7. Variables de entorno

### Ubicación

```
/opt/roomrent/.env
```

Este archivo **nunca debe estar en el repositorio git**. Está en `.gitignore`. Contiene secretos de producción.

### Variables requeridas

```bash
# /opt/roomrent/.env — EJEMPLO (valores reales nunca en git)

# Spring Boot — perfil activo
SPRING_PROFILES_ACTIVE=prod

# Base de datos MongoDB
SPRING_DATA_MONGODB_URI=mongodb://roomrent-mongo-1:27017/roomrent

# JWT — clave secreta (mínimo 512 bits para HS512)
# Generar con: openssl rand -base64 128
JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET=<secreto-base64-512bits>

# URL pública de la aplicación
JHIPSTER_SECURITY_CONTENT_SECURITY_POLICY=...
APP_BASE_URL=https://room-rent.xyz

# Directorio de archivos subidos
APP_UPLOADS_DIR=/app/uploads

# Email (para notificaciones, si aplica)
SPRING_MAIL_HOST=smtp.example.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=<usuario>
SPRING_MAIL_PASSWORD=<contraseña>
JHIPSTER_MAIL_FROM=no-reply@room-rent.xyz
```

### Seguridad del archivo .env

```bash
# Permisos correctos — solo el usuario que corre Docker puede leerlo
chmod 600 /opt/roomrent/.env
chown ubuntu:ubuntu /opt/roomrent/.env

# Verificar que NO está en git
git -C /opt/roomrent ls-files .env
# Debe devolver vacío — si devuelve ".env", es una vulnerabilidad crítica
```

### Secretos críticos

| Variable | Descripción | Cómo generar |
|---|---|---|
| `JWT_BASE64_SECRET` | Clave HMAC-SHA-512 para firmar los JWT. Si se compromete, todos los tokens existentes son vulnerables. | `openssl rand -base64 128` |
| `MONGODB_URI` | Cadena de conexión con credenciales a MongoDB. | Configurar en el compose + .env |
| `MAIL_PASSWORD` | Contraseña del servidor SMTP para envío de emails. | Usar contraseña de aplicación del proveedor |

---

## 8. Autostart y systemd

### Servicios habilitados

Los servicios arrancan automáticamente cuando la instancia EC2 se reinicia, sin intervención manual.

```bash
# Verificar estado de los servicios críticos
systemctl status docker
systemctl status nginx
systemctl status certbot.timer

# Verificar que están habilitados (arrancan con el sistema)
systemctl is-enabled docker    # → enabled
systemctl is-enabled nginx     # → enabled
systemctl is-enabled certbot.timer  # → enabled
```

### Cadena de arranque

```
1. Kernel Linux arranca
2. systemd activa servicios en orden de dependencias
3. docker.service arranca → Docker Engine disponible
4. nginx.service arranca → nginx comienza a servir (puede recibir conexiones)
5. Docker Compose (si hay un servicio systemd para ello) arranca los contenedores:
     roomrent-mongo-1 → arranca primero (depends_on en compose)
     roomrent-app-1   → arranca después (espera a mongo)
6. Spring Boot ejecuta Mongock migrations al arrancar
7. Aplicación disponible en 127.0.0.1:8080
8. nginx ya está listo y comienza a proxyar las requests al backend
```

### Servicio systemd para Docker Compose (opcional pero recomendado)

```ini
# /etc/systemd/system/roomrent.service
[Unit]
Description=RoomRent Application (Docker Compose)
Requires=docker.service
After=docker.service network-online.target

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=/opt/roomrent
ExecStart=/usr/bin/docker compose up -d
ExecStop=/usr/bin/docker compose down
TimeoutStartSec=300

[Install]
WantedBy=multi-user.target
```

```bash
# Activar el servicio
systemctl enable roomrent.service
systemctl start roomrent.service
```

### Comandos de gestión habituales

```bash
# Ver estado de los contenedores
cd /opt/roomrent && docker compose ps

# Ver logs de la aplicación (últimas 100 líneas)
docker logs roomrent-app-1 --tail=100

# Ver logs en tiempo real
docker logs roomrent-app-1 -f

# Reiniciar la aplicación sin afectar MongoDB
docker compose restart app

# Desplegar una nueva versión
docker compose pull        # si la imagen viene de un registry
docker compose up -d       # recrea el contenedor con la nueva imagen

# Ver uso de recursos
docker stats roomrent-app-1 roomrent-mongo-1
```

---

## 9. Monitoreo

### Endpoints de salud de la aplicación

| Endpoint | Acceso | Descripción |
|---|---|---|
| `https://room-rent.xyz/management/health` | Público | Estado general: UP / DOWN. Incluye el estado de MongoDB. |
| `https://room-rent.xyz/management/health/liveness` | Público | Liveness probe — ¿el proceso está vivo? |
| `https://room-rent.xyz/management/health/readiness` | Público | Readiness probe — ¿la app puede recibir tráfico? |
| `https://room-rent.xyz/management/info` | Público | Versión del build, nombre de la app, perfil activo. |
| `https://room-rent.xyz/management/prometheus` | ROLE_ADMIN | Métricas en formato Prometheus (JVM, HTTP, DB). |
| `https://room-rent.xyz/management/loggers` | ROLE_ADMIN | Consultar y cambiar niveles de log en runtime. |

### Respuesta típica de /management/health

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MongoDB",
        "validationQuery": "{ isMaster: 1 }"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 40836997120,
        "free": 28000000000,
        "threshold": 10485760
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### Verificación manual del sistema

```bash
# 1. Comprobar que la app responde
curl -s https://room-rent.xyz/management/health | jq .status

# 2. Comprobar que MongoDB está conectado
curl -s https://room-rent.xyz/management/health | jq '.components.db.status'

# 3. Ver uso de disco del volumen de uploads
docker exec roomrent-app-1 df -h /app/uploads

# 4. Ver uso de disco del volumen de MongoDB
docker exec roomrent-mongo-1 du -sh /data/db

# 5. Comprobar el espacio en el host
df -h /

# 6. Comprobar la validez del certificado TLS
openssl s_client -connect room-rent.xyz:443 -servername room-rent.xyz \
  </dev/null 2>/dev/null | openssl x509 -noout -dates

# 7. Verificar que la renovación de certificados funcionará
certbot renew --dry-run
```

### Alertas manuales recomendadas

Dado que no hay un sistema de monitoreo automatizado configurado, se recomienda verificar periódicamente:

| Frecuencia | Verificación |
|---|---|
| Diaria | `docker compose ps` — todos los contenedores en estado `Up` |
| Semanal | Espacio en disco (`df -h /`) — alerta si supera el 80% |
| Mensual | Logs de errores de la aplicación (`docker logs roomrent-app-1 2>&1 \| grep ERROR`) |
| Antes de cada vencimiento | Validez del certificado TLS (vence cada 90 días, Certbot renueva automáticamente) |

---

## 10. Recursos del sistema

### Capacidad de la instancia t2.micro

| Recurso | Total | Disponible aprox. | Notas |
|---|---|---|---|
| RAM | 1 GB | 908 MB | Después de OS y procesos del sistema |
| Disco | 40 GB | 38 GB usables | EBS gp2, volumen único |
| vCPU | 1 | 1 | t2.micro — CPU créditos burstable |
| Red | Hasta 1 Gbps | Variable (créditos burst) | t2.micro tiene red limitada |

### Distribución estimada de RAM

```
Sistema Ubuntu + systemd + sshd:    ~150 MB
nginx:                              ~10 MB
Docker Engine daemon:               ~50 MB
roomrent-mongo-1 (MongoDB 7):       ~300 MB (WiredTiger cache = 256 MB mínimo)
roomrent-app-1 (JRE 21 + Spring):   ~350 MB (JVM heap + metaspace)
                                    ─────────
Total estimado:                     ~860 MB
Margen disponible:                  ~48 MB
```

La instancia t2.micro es ajustada para este workload. Spring Boot está configurado con heap reducido para caber en el espacio disponible:

```bash
# Flags JVM recomendados en el Dockerfile o en docker-compose.yml
JAVA_OPTS="-Xmx256m -Xms128m -XX:+UseContainerSupport -XX:MaxRAMPercentage=40.0"
```

### Limites de almacenamiento a vigilar

```
/var/lib/docker/volumes/roomrent-mongo-data/   ← crece con los datos de usuarios
/var/lib/docker/volumes/roomrent-uploads-data/ ← crece con fotos e imágenes subidas
/etc/letsencrypt/                              ← estático, no crece significativamente
/var/log/nginx/                                ← rotar logs periódicamente
```

Con 38 GB disponibles, el crecimiento esperado en la fase inicial (datos de prueba + primeros usuarios reales) es manejable. Se debe revisar el espacio en disco mensualmente.

---

## 11. Procedimientos operativos

### Despliegue de una nueva versión

```bash
# 1. Acceder al servidor via SSH
ssh ubuntu@room-rent.xyz

# 2. Ir al directorio de la aplicación
cd /opt/roomrent

# 3. Copiar la nueva imagen al servidor (o hacer pull desde registry)
# Opción A: desde un registry Docker
docker pull roomrent/app:nueva-version
docker tag roomrent/app:nueva-version roomrent/app:latest

# Opción B: cargar imagen desde archivo (build local)
# En la máquina local: docker save roomrent/app:latest | gzip > roomrent-app.tar.gz
# En el servidor:
docker load < roomrent-app.tar.gz

# 4. Recrear el contenedor con la nueva imagen
docker compose up -d --no-deps app

# 5. Verificar que arrancó correctamente
docker logs roomrent-app-1 --tail=50
curl -s http://127.0.0.1:8080/management/health | jq .status
```

### Backup de datos

```bash
# Backup de MongoDB
docker exec roomrent-mongo-1 mongodump \
  --db roomrent \
  --out /tmp/backup-$(date +%Y%m%d)
docker cp roomrent-mongo-1:/tmp/backup-$(date +%Y%m%d) ./backups/

# Backup de uploads (archivos de usuarios)
tar -czf ./backups/uploads-$(date +%Y%m%d).tar.gz \
  -C /var/lib/docker/volumes/roomrent-uploads-data/_data .
```

### Rollback de emergencia

```bash
# Volver a la imagen anterior
docker tag roomrent/app:anterior roomrent/app:latest
docker compose up -d --no-deps app
docker logs roomrent-app-1 --tail=20
```

### Reinicio completo del stack

```bash
cd /opt/roomrent

# Parar todo
docker compose down

# Arrancar (MongoDB primero, luego app por depends_on)
docker compose up -d

# Verificar
docker compose ps
docker logs roomrent-app-1 --tail=30
curl -s http://127.0.0.1:8080/management/health
```
