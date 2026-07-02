# RECUPERACIÓN DE DESASTRES — RoomRent

Procedimientos de recuperación ante fallos críticos.

Fecha: 2026-07-02 | Versión: RC1

---

## Escenarios y tiempos de recuperación

| Escenario | RTO estimado | RPO estimado | Procedimiento |
|-----------|-------------|-------------|---------------|
| EC2 se apagó (plan student) | 5-10 min | 0 (datos en volúmenes) | Iniciar contenedores |
| App container caído | 2-5 min | 0 | docker compose up -d app |
| MongoDB container caído | 3-7 min | 0 | docker compose up -d mongo |
| Toda la EC2 destruida | 2-4 horas | Hasta último backup | Recrear desde cero |
| Corrupción de datos MongoDB | 15-30 min | Hasta último backup | Restore mongorestore |
| Fotos perdidas | 15-30 min | Hasta último backup | Restore volumen uploads |
| SSL expirado | 5 min | N/A | certbot renew |

**RTO** = Recovery Time Objective (tiempo hasta volver a operar)  
**RPO** = Recovery Point Objective (datos que podrían perderse)

---

## ESCENARIO 1 — EC2 apagado (más frecuente)

### Síntoma
```
ssh: connect to host 34.202.0.109 port 22: Connection refused
curl: (7) Failed to connect to room-rent.xyz port 443
```

### Diagnóstico
1. Abrir panel AWS Academy
2. Verificar estado de la instancia EC2
3. Si está "Stopped" → iniciar

### Recuperación
```bash
# La IP puede haber cambiado — verificar en el panel de AWS Academy
ssh ubuntu@<NUEVA_IP>

cd /opt/roomrent
docker compose up -d

# Verificar después de 90 segundos
sleep 90
curl -s http://127.0.0.1:8080/management/health
curl -s https://room-rent.xyz/management/health
```

### Verificación post-recuperación
```bash
# Todos estos deben pasar
curl -o /dev/null -w "%{http_code}" https://room-rent.xyz/portal/        # 200
curl -o /dev/null -w "%{http_code}" https://room-rent.xyz/api/inmuebles  # 200
curl -o /dev/null -w "%{http_code}" https://room-rent.xyz/management/health  # 200
```

---

## ESCENARIO 2 — App container caído (MongoDB OK)

### Síntoma
```
curl http://127.0.0.1:8080/management/health → Connection refused
docker compose ps → roomrent-app-1: Exited (1)
```

### Diagnóstico
```bash
docker compose -f /opt/roomrent/docker-compose.yml logs --tail=50 app | grep -E "ERROR|FATAL|Exception"
```

### Recuperación
```bash
cd /opt/roomrent
docker compose start app

# Si no levanta, ver logs
docker compose logs --tail=100 app

# Si hay error de configuración, verificar .env
cat /opt/roomrent/.env | grep -v PASSWORD | grep -v SECRET | grep -v URI
```

### Causas frecuentes y solución

| Error en log | Causa | Solución |
|-------------|-------|---------|
| `Address already in use` | Puerto 8080 ocupado | `docker compose down && up -d` |
| `Authentication failed` | MongoDB password incorrecta en .env | Verificar SPRING_MONGODB_URI |
| `Cannot connect to mongo` | MongoDB container caído | `docker compose start mongo` primero |
| `OutOfMemoryError` | RAM insuficiente | Revisar `-Xmx` en docker-compose.yml |

---

## ESCENARIO 3 — MongoDB container caído

### Síntoma
```
docker compose ps → roomrent-mongo-1: Exited (X)
Spring Boot log → com.mongodb.MongoSocketException
```

### Recuperación
```bash
cd /opt/roomrent

# Iniciar solo MongoDB
docker compose start mongo

# Esperar 30 segundos y reiniciar app
sleep 30
docker compose restart app

# Verificar
sleep 60
curl http://127.0.0.1:8080/management/health
```

### Si los datos de MongoDB están dañados (raro)
```bash
# 1. Parar todo
docker compose down

# 2. Eliminar volumen dañado
docker volume rm roomrent-mongo-data

# 3. Restaurar desde backup (ver ESCENARIO 5)
# ... (después de restaurar el backup)

# 4. Reiniciar
docker compose up -d
```

---

## ESCENARIO 4 — Código corrupto / Rollback de versión

### Síntoma
```
Spring Boot arranca pero falla en runtime
Build falla con errores inesperados
Nueva versión introduce regresiones críticas
```

### Rollback a commit anterior
```bash
cd /opt/roomrent

# Ver historial de commits
git log --oneline -10

# Volver al commit anterior
git checkout <HASH_ANTERIOR>
git checkout -b rollback-<HASH_ANTERIOR>

# Reconstruir imagen con versión anterior
docker build -t roomrent-app:rollback .
docker tag roomrent-app:rollback roomrent-app:latest

# Reiniciar
docker compose up -d app

# Verificar
sleep 90
curl http://127.0.0.1:8080/management/health
```

---

## ESCENARIO 5 — Restaurar datos MongoDB desde backup

### Pre-requisito
Tener un archivo de backup `mongo-YYYYMMDD_HHMMSS.gz` (generado por backup.sh)

### Procedimiento
```bash
# 1. Copiar backup al servidor si está en local
scp /local/ruta/mongo-20260702_020000.gz ubuntu@<IP>:/tmp/

# 2. Copiar al contenedor
docker cp /tmp/mongo-20260702_020000.gz roomrent-mongo-1:/tmp/

# 3. Restaurar (--drop borra la colección antes de restaurar)
MONGO_USER=$(grep '^MONGO_INITDB_ROOT_USERNAME=' /opt/roomrent/.env | cut -d= -f2-)
MONGO_PASS=$(grep '^MONGO_INITDB_ROOT_PASSWORD=' /opt/roomrent/.env | cut -d= -f2-)

docker exec roomrent-mongo-1 mongorestore \
  --username "$MONGO_USER" \
  --password "$MONGO_PASS" \
  --authenticationDatabase admin \
  --db room \
  --drop \
  --archive=/tmp/mongo-20260702_020000.gz \
  --gzip

# 4. Limpiar
docker exec roomrent-mongo-1 rm /tmp/mongo-20260702_020000.gz

# 5. Reiniciar app
docker compose restart app
sleep 60
curl http://127.0.0.1:8080/management/health
```

---

## ESCENARIO 6 — Restaurar fotos desde backup

### Procedimiento
```bash
# 1. Parar app (opcional, para evitar escrituras durante restore)
docker compose stop app

# 2. Copiar backup al servidor si está en local
scp /local/ruta/uploads-20260702_020000.tar.gz ubuntu@<IP>:/tmp/

# 3. Restaurar en el volumen
docker run --rm \
  -v roomrent-uploads-data:/uploads \
  -v /tmp:/backup:ro \
  alpine sh -c "rm -rf /uploads/* && tar -xzf /backup/uploads-20260702_020000.tar.gz -C /uploads"

# 4. Reiniciar
docker compose start app
sleep 60
curl http://127.0.0.1:8080/management/health
```

---

## ESCENARIO 7 — SSL expirado

### Síntoma
```
curl: (60) SSL certificate problem: certificate has expired
browser: NET::ERR_CERT_DATE_INVALID
```

### Recuperación
```bash
# Verificar expiración
sudo certbot certificates

# Renovar
sudo certbot renew --nginx

# Si falla por puerto 80 bloqueado
sudo certbot renew --standalone --pre-hook "sudo nginx -s stop" --post-hook "sudo nginx"

# Verificar renovación
echo | openssl s_client -connect room-rent.xyz:443 2>/dev/null | grep 'not after'
```

---

## ESCENARIO 8 — Reconstruir desde CERO (catástrofe total)

### Si la EC2 fue terminada/destruida y no hay backup

Tiempo estimado: 3-4 horas

```bash
# PASO 1: Nueva instancia EC2 Ubuntu 24.04

# PASO 2: Instalar Docker
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker ubuntu
newgrp docker
sudo systemctl enable docker

# PASO 3: Instalar nginx y certbot
sudo apt update
sudo apt install -y nginx certbot python3-certbot-nginx

# PASO 4: Clonar repositorio
sudo mkdir -p /opt/roomrent
sudo chown ubuntu:ubuntu /opt/roomrent
git clone https://github.com/Jose-Bohorquez/roomRent.git /opt/roomrent

# PASO 5: Crear .env
cat > /opt/roomrent/.env << 'EOF'
SPRING_PROFILES_ACTIVE=prod
SPRING_MONGODB_URI=mongodb://roomrent:<MONGO_PASS>@mongo:27017/room?authSource=admin
JWT_BASE64_SECRET=<SECRET>
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=bd567358546@gmail.com
SPRING_MAIL_PASSWORD=<GMAIL_APP_PASSWORD>
JHIPSTER_MAIL_BASE_URL=https://room-rent.xyz
VITE_API_BASE=
MONGO_INITDB_ROOT_USERNAME=root
MONGO_INITDB_ROOT_PASSWORD=<ROOT_PASS>
MONGO_INITDB_DATABASE=room
MONGO_USERNAME=roomrent
MONGO_PASSWORD=<MONGO_PASS>
EOF

# PASO 6: Configurar nginx + SSL
sudo cp /opt/roomrent/nginx/room-rent.conf /etc/nginx/sites-enabled/
sudo certbot --nginx -d room-rent.xyz -d www.room-rent.xyz --non-interactive --agree-tos -m admin@roomrent.com
sudo nginx -s reload

# PASO 7: Build y arranque
cd /opt/roomrent
docker build -t roomrent-app:latest .  # 15-20 min
docker compose up -d

# PASO 8: Restaurar datos (si hay backup)
# Ver ESCENARIO 5 y ESCENARIO 6

# PASO 9: Verificar
sleep 90
curl https://room-rent.xyz/management/health
```

---

## Contactos de escalada

| Rol | Contacto | Cuándo escalar |
|-----|---------|----------------|
| Desarrollador principal | Jose Bohorquez (jose.bohorquez@servitel.co) | Bugs, cambios de código |
| AWS Support | Panel de AWS Academy | Problemas de instancia EC2 |
| Let's Encrypt | letsencrypt.org | Problemas de certificado |

---

## Checklist post-recuperación

```
[ ] docker compose ps — ambos containers healthy
[ ] GET /management/health — {"status":"UP"}
[ ] GET /portal/ — HTTP 200
[ ] GET /api/inmuebles — HTTP 200
[ ] GET /api/publicacion-inmuebles — HTTP 200 con publicaciones
[ ] POST /api/authenticate admin — token recibido
[ ] GET /uploads/{archivo-conocido}.png — HTTP 200 (datos persisten)
[ ] https:// funciona (no HTTP)
[ ] Certificado SSL válido
```
