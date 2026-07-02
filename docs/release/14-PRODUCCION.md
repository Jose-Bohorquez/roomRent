# PRODUCCIÓN — RoomRent

Estado y configuración del entorno de producción.

Fecha: 2026-07-02 | Versión: RC1

---

## Entorno de producción

| Item | Valor |
|------|-------|
| Dominio | room-rent.xyz |
| IP EC2 | 34.202.0.109 |
| Región AWS | us-east-1 |
| Tipo instancia | t2.micro (AWS Academy / Student benefit) |
| OS | Ubuntu 24.04 LTS |
| RAM | 908 MB |
| Swap | 2048 MB |
| Disco | 38 GB (33% usado) |
| SSH user | ubuntu |
| SSH key | ~/.ssh/id_ed25519 |

---

## Stack de producción

```
Browser (HTTPS)
      │
      ▼
nginx/1.28.3 (Ubuntu) — puerto 443, TLS 1.2/1.3, HTTP/2
  ├── /portal/**           → Spring Boot (React portal estático)
  ├── /api/**              → Spring Boot (REST API)
  ├── /uploads/**          → Spring Boot (fotos)
  ├── /management/**       → Spring Boot (actuator)
  └── /**                  → Spring Boot (Angular admin)
      │
      ▼ proxy_pass http://127.0.0.1:8080
Spring Boot 4.0.6 (roomrent-app-1)
      │
      ▼ mongodb://roomrent:***@mongo:27017/room
MongoDB 7 (roomrent-mongo-1)
```

---

## Servicios systemd

```bash
# Verificar estado
sudo systemctl status nginx
sudo systemctl status docker

# Ambos habilitados para auto-start en boot:
# nginx: enabled
# docker: enabled
```

---

## Docker en producción

```bash
# Ver contenedores
docker compose -f /opt/roomrent/docker-compose.yml ps

# Ver logs en tiempo real
docker compose -f /opt/roomrent/docker-compose.yml logs -f app
docker compose -f /opt/roomrent/docker-compose.yml logs -f mongo

# Reiniciar aplicación (sin downtime de MongoDB)
docker compose -f /opt/roomrent/docker-compose.yml restart app

# Actualización completa (con downtime ~60s)
cd /opt/roomrent
git pull origin main
docker build -t roomrent-app:latest .
docker compose up -d

# Health check manual
curl http://127.0.0.1:8080/management/health
```

---

## SSL / TLS

| Item | Valor |
|------|-------|
| Proveedor | Let's Encrypt |
| Certificado | CN=room-rent.xyz |
| Emisor | YE1 (R10/Let's Encrypt) |
| Renovación | Automática via certbot |
| Protocolos | TLS 1.2, TLS 1.3 |
| HTTP/2 | Habilitado |

```bash
# Verificar certificado
openssl s_client -connect room-rent.xyz:443 -servername room-rent.xyz </dev/null 2>/dev/null | \
  grep -E 'subject=|issuer=|expire'

# Renovar manualmente si es necesario
sudo certbot renew --nginx
```

---

## Variables de entorno requeridas

Archivo: `/opt/roomrent/.env` (NUNCA en git, NUNCA en código)

```ini
SPRING_PROFILES_ACTIVE=prod
SPRING_MONGODB_URI=mongodb://roomrent:***@mongo:27017/room?authSource=admin
JWT_BASE64_SECRET=<base64 64 bytes>
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=bd567358546@gmail.com
SPRING_MAIL_PASSWORD=<gmail-app-password>
JHIPSTER_MAIL_BASE_URL=https://room-rent.xyz
VITE_API_BASE=
MONGO_INITDB_ROOT_USERNAME=root
MONGO_INITDB_ROOT_PASSWORD=***
MONGO_INITDB_DATABASE=room
MONGO_USERNAME=roomrent
MONGO_PASSWORD=***
```

---

## Monitoreo

| Endpoint | Acceso | Propósito |
|----------|--------|-----------|
| `GET /management/health` | Público | Liveness/Readiness probes |
| `GET /management/info` | Público | Versión, build info |
| `GET /management/prometheus` | Público | Métricas Prometheus |
| `GET /management/**` | ROLE_ADMIN | Métricas detalladas, logs, threads |

```bash
# Health check externo
curl https://room-rent.xyz/management/health

# Métricas de aplicación (necesita token admin)
curl https://room-rent.xyz/management/metrics \
  -H "Authorization: Bearer <admin-token>"
```

---

## Comportamiento del EC2 (AWS Student plan)

La instancia EC2 se apaga periódicamente por el plan de estudiantes de AWS Academy.

**Cuando el EC2 se apaga:**
1. nginx se detiene (ExitCode=0)
2. Docker containers se detienen (ExitCode=0)
3. Sistema se apaga (approximately 7-8 minutos de downtime)

**Al reiniciar:**
1. Sistema inicia (~40s)
2. nginx inicia automáticamente (systemd)
3. Docker daemon inicia automáticamente (systemd)
4. Docker Compose no auto-inicia los contenedores (no hay restart policy configurada)

**Protocolo de recuperación post-apagado:**
```bash
ssh ubuntu@<IP_EC2>
cd /opt/roomrent
docker compose up -d
# Esperar 60-90 segundos
curl http://127.0.0.1:8080/management/health
```

> **Nota**: La IP pública del EC2 cambia en cada reinicio del plan de estudiantes. Verificar la nueva IP en el panel de AWS Academy.

---

## Checklist de verificación post-deploy

```
[ ] docker compose ps — ambos contenedores healthy
[ ] curl http://127.0.0.1:8080/management/health — {"status":"UP"}
[ ] curl https://room-rent.xyz/management/health — HTTP 200
[ ] curl -o /dev/null -w "%{http_code}" https://room-rent.xyz/portal/ — HTTP 200
[ ] curl -o /dev/null -w "%{http_code}" https://room-rent.xyz/api/inmuebles — HTTP 200
[ ] POST /api/authenticate con admin/admin — token recibido
[ ] GET /uploads/{archivo-conocido}.png — HTTP 200 (fotos persisten)
[ ] GET https://room-rent.xyz/ — redirige a /portal/ (no a http://)
```
