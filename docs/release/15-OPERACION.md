# OPERACIÓN DIARIA — RoomRent

Runbook operativo para administrar el sistema en producción.

Fecha: 2026-07-02 | Versión: RC1

---

## Verificación diaria (2 minutos)

```bash
ssh ubuntu@34.202.0.109

# 1. Contenedores vivos
docker compose -f /opt/roomrent/docker-compose.yml ps

# 2. Health check
curl -s http://127.0.0.1:8080/management/health | python3 -m json.tool

# 3. Espacio en disco
df -h /

# 4. RAM disponible
free -m
```

**Estado OK si:**
- `roomrent-app-1` y `roomrent-mongo-1` muestran `(healthy)`
- health devuelve `{"status":"UP"}`
- Disco < 90% usado
- RAM libre > 100 MB (swap disponible como buffer)

---

## Iniciar contenedores post-apagado EC2

```bash
ssh ubuntu@<NUEVA_IP_EC2>
cd /opt/roomrent
docker compose up -d

# Esperar 60-90 segundos y verificar
sleep 90
curl -s http://127.0.0.1:8080/management/health
```

> La IP del EC2 cambia en cada reinicio del plan de estudiantes.
> Verificar la IP actual en el panel de AWS Academy.

---

## Ver logs

```bash
# Logs de Spring Boot (últimas 100 líneas)
docker compose -f /opt/roomrent/docker-compose.yml logs --tail=100 app

# Logs en tiempo real
docker compose -f /opt/roomrent/docker-compose.yml logs -f app

# Solo ERRORs
docker compose -f /opt/roomrent/docker-compose.yml logs app | grep ERROR

# Logs de MongoDB
docker compose -f /opt/roomrent/docker-compose.yml logs --tail=50 mongo
```

---

## Reiniciar aplicación (sin tocar MongoDB)

```bash
cd /opt/roomrent
docker compose restart app

# Verificar que levantó correctamente
sleep 60
curl -s http://127.0.0.1:8080/management/health
```

---

## Actualizar la aplicación

```bash
cd /opt/roomrent

# 1. Obtener últimos cambios
git pull origin main

# 2. Construir nueva imagen (15-20 min)
docker build -t roomrent-app:latest . 2>&1 | tee /tmp/build.log

# 3. Reemplazar el contenedor de app (MongoDB no se toca)
docker compose up -d app

# 4. Verificar
sleep 90
curl -s http://127.0.0.1:8080/management/health
```

---

## Gestión de usuarios (admin)

```bash
# Obtener token admin
TOKEN=$(curl -s -X POST https://room-rent.xyz/api/authenticate \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin"}' | python3 -c "import sys,json; print(json.load(sys.stdin)['id_token'])")

# Listar usuarios
curl -s https://room-rent.xyz/api/admin/users \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool | head -100

# Crear usuario
curl -X POST https://room-rent.xyz/api/admin/users \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"login":"nuevo_usuario","email":"mail@example.com","activated":true,"langKey":"es","authorities":["ROLE_USER","ROLE_ARRENDADOR"]}'

# Activar usuario manualmente
curl -X PUT https://room-rent.xyz/api/admin/users \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"login":"usuario","activated":true,...}'
```

---

## Gestión de inmuebles (admin)

```bash
# Ver todos los inmuebles publicados
curl -s "https://room-rent.xyz/api/publicacion-inmuebles?size=50" \
  -H "Authorization: Bearer $TOKEN"

# Archivar una publicación
curl -X PATCH "https://room-rent.xyz/api/publicacion-inmuebles/{ID}" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"id":"{ID}","estado":"ARCHIVADA"}'
```

---

## MongoDB — operaciones frecuentes

```bash
# Entrar al shell de MongoDB
MONGO_USER=$(grep '^MONGO_INITDB_ROOT_USERNAME=' /opt/roomrent/.env | cut -d= -f2-)
MONGO_PASS=$(grep '^MONGO_INITDB_ROOT_PASSWORD=' /opt/roomrent/.env | cut -d= -f2-)

docker exec -it roomrent-mongo-1 mongosh \
  --username "$MONGO_USER" \
  --password "$MONGO_PASS" \
  --authenticationDatabase admin \
  --db room

# Dentro de mongosh:
db.jhi_user.find({}, {login:1, activated:1, email:1}).sort({created_date:-1}).limit(10)
db.inmueble.countDocuments()
db.publicacion_inmueble.countDocuments({estado: "PUBLICADA"})
db.multimedia_inmueble.countDocuments()
```

---

## Nginx — operaciones frecuentes

```bash
# Verificar configuración
sudo nginx -t

# Recargar sin downtime
sudo nginx -s reload

# Ver logs de acceso (últimos 50)
sudo tail -50 /var/log/nginx/access.log

# Ver errores
sudo tail -50 /var/log/nginx/error.log

# Ver estado
sudo systemctl status nginx
```

---

## SSL/TLS — renovación de certificado

```bash
# Verificar fecha de expiración
sudo certbot certificates

# Renovar (automático via cron, pero si hay que forzar)
sudo certbot renew --nginx

# Verificar desde afuera
echo | openssl s_client -connect room-rent.xyz:443 2>/dev/null | grep 'not after'
```

---

## Alertas y umbrales

| Métrica | Umbral WARN | Umbral CRIT | Acción |
|---------|-------------|-------------|--------|
| RAM libre | < 150 MB | < 100 MB | Reiniciar app |
| Disco usado | > 80% | > 90% | Limpiar Docker imágenes antiguas |
| Health status | WARN | DOWN | Ver logs, reiniciar app |
| Response time | > 2s | > 5s | Ver logs, revisar MongoDB |
| Logs ERROR | > 5/hora | > 20/hora | Revisar stack trace |

```bash
# Limpiar imágenes Docker antiguas (libera espacio)
docker image prune -f

# Ver tamaño de imágenes
docker images --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}"
```

---

## Cambiar contraseña admin

```bash
# Obtener reset_key via MongoDB
MONGO_USER=$(grep '^MONGO_INITDB_ROOT_USERNAME=' /opt/roomrent/.env | cut -d= -f2-)
MONGO_PASS=$(grep '^MONGO_INITDB_ROOT_PASSWORD=' /opt/roomrent/.env | cut -d= -f2-)

docker exec roomrent-mongo-1 mongosh \
  --username "$MONGO_USER" --password "$MONGO_PASS" \
  --authenticationDatabase admin --db room --quiet \
  --eval 'db.jhi_user.findOne({login:"admin"},{reset_key:1})'

# Primero solicitar reset
curl -X POST https://room-rent.xyz/api/account/reset-password/init \
  -H 'Content-Type: text/plain' \
  -d 'admin@roomrent.com'

# Leer el reset_key de MongoDB y usar:
curl -X POST https://room-rent.xyz/api/account/reset-password/finish \
  -H 'Content-Type: application/json' \
  -d '{"key":"<reset_key>","newPassword":"<NuevoPassword1!"}'
```

---

## Comandos de emergencia

```bash
# Parar todo
docker compose -f /opt/roomrent/docker-compose.yml down

# Iniciar todo
docker compose -f /opt/roomrent/docker-compose.yml up -d

# Ver recursos de contenedores
docker stats --no-stream

# Ver procesos dentro del app
docker exec roomrent-app-1 ps aux

# Limpiar logs de Docker (si crecen mucho)
docker system prune --volumes  # CUIDADO: borra volúmenes sin uso
```
