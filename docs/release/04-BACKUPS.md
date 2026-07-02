# BACKUPS — RoomRent

Estrategia de respaldo y recuperación de datos.

Fecha: 2026-07-02 | Versión: RC1

---

## Datos críticos

| Dato | Ubicación | Volumen Docker |
|------|-----------|----------------|
| Base de datos MongoDB | roomrent-mongo-1:/data/db | `roomrent-mongo-data` |
| Archivos subidos (fotos) | roomrent-app-1:/app/uploads | `roomrent-uploads-data` |
| Configuración | /opt/roomrent/.env | Host filesystem |
| Nginx config | /etc/nginx/sites-enabled/ | Host filesystem |
| Certificados SSL | /etc/letsencrypt/ | Host filesystem |

---

## Backup manual

### MongoDB

```bash
# En EC2, como ubuntu
cd /opt/roomrent

# Backup completo de la base de datos
MONGO_USER=$(grep '^MONGO_INITDB_ROOT_USERNAME=' .env | cut -d= -f2-)
MONGO_PASS=$(grep '^MONGO_INITDB_ROOT_PASSWORD=' .env | cut -d= -f2-)
DATE=$(date +%Y%m%d_%H%M%S)

docker exec roomrent-mongo-1 mongodump \
  --username "$MONGO_USER" \
  --password "$MONGO_PASS" \
  --authenticationDatabase admin \
  --db room \
  --out /tmp/backup_$DATE

# Comprimir
docker exec roomrent-mongo-1 tar -czf /tmp/roomrent-mongo-$DATE.tar.gz /tmp/backup_$DATE
docker cp roomrent-mongo-1:/tmp/roomrent-mongo-$DATE.tar.gz /opt/backups/

echo "Backup MongoDB: /opt/backups/roomrent-mongo-$DATE.tar.gz"
```

### Uploads (fotos de inmuebles)

```bash
DATE=$(date +%Y%m%d_%H%M%S)
mkdir -p /opt/backups

# Copiar desde el volumen Docker
docker run --rm \
  -v roomrent-uploads-data:/uploads:ro \
  -v /opt/backups:/backup \
  alpine tar -czf /backup/roomrent-uploads-$DATE.tar.gz -C /uploads .

echo "Backup uploads: /opt/backups/roomrent-uploads-$DATE.tar.gz"
```

### Configuración

```bash
DATE=$(date +%Y%m%d_%H%M%S)
mkdir -p /opt/backups

# .env (NUNCA subir a git)
cp /opt/roomrent/.env /opt/backups/.env-$DATE

# nginx
cp /etc/nginx/sites-enabled/room-rent.conf /opt/backups/nginx-$DATE.conf

echo "Config backup: /opt/backups/"
```

---

## Restore

### MongoDB

```bash
# Descomprimir el backup
tar -xzf /opt/backups/roomrent-mongo-YYYYMMDD_HHMMSS.tar.gz -C /tmp/

# Copiar al contenedor
docker cp /tmp/tmp/backup_YYYYMMDD_HHMMSS roomrent-mongo-1:/tmp/restore/

# Restaurar
MONGO_USER=$(grep '^MONGO_INITDB_ROOT_USERNAME=' .env | cut -d= -f2-)
MONGO_PASS=$(grep '^MONGO_INITDB_ROOT_PASSWORD=' .env | cut -d= -f2-)

docker exec roomrent-mongo-1 mongorestore \
  --username "$MONGO_USER" \
  --password "$MONGO_PASS" \
  --authenticationDatabase admin \
  --db room \
  --drop \
  /tmp/restore/room
```

### Uploads

```bash
# Restaurar fotos desde backup
docker run --rm \
  -v roomrent-uploads-data:/uploads \
  -v /opt/backups:/backup:ro \
  alpine sh -c "tar -xzf /backup/roomrent-uploads-YYYYMMDD_HHMMSS.tar.gz -C /uploads"
```

---

## Script de backup automático (recomendado)

Crear `/opt/roomrent/backup.sh`:

```bash
#!/bin/bash
set -e

BACKUP_DIR=/opt/backups
DATE=$(date +%Y%m%d_%H%M%S)
RETENTION_DAYS=7

mkdir -p "$BACKUP_DIR"

# Cargar variables
MONGO_USER=$(grep '^MONGO_INITDB_ROOT_USERNAME=' /opt/roomrent/.env | cut -d= -f2-)
MONGO_PASS=$(grep '^MONGO_INITDB_ROOT_PASSWORD=' /opt/roomrent/.env | cut -d= -f2-)

# 1. MongoDB
docker exec roomrent-mongo-1 mongodump \
  --username "$MONGO_USER" --password "$MONGO_PASS" \
  --authenticationDatabase admin --db room \
  --archive=/tmp/mongo-$DATE.gz --gzip 2>/dev/null

docker cp roomrent-mongo-1:/tmp/mongo-$DATE.gz $BACKUP_DIR/
docker exec roomrent-mongo-1 rm /tmp/mongo-$DATE.gz

# 2. Uploads
docker run --rm \
  -v roomrent-uploads-data:/uploads:ro \
  -v $BACKUP_DIR:/backup \
  alpine tar -czf /backup/uploads-$DATE.tar.gz -C /uploads .

# 3. .env
cp /opt/roomrent/.env $BACKUP_DIR/.env-$DATE

# 4. Limpiar backups viejos
find $BACKUP_DIR -name "mongo-*.gz" -mtime +$RETENTION_DAYS -delete
find $BACKUP_DIR -name "uploads-*.tar.gz" -mtime +$RETENTION_DAYS -delete
find $BACKUP_DIR -name ".env-*" -mtime +$RETENTION_DAYS -delete

echo "[$(date)] Backup completado en $BACKUP_DIR"
```

Registrar en cron (ejecutar daily a las 2:00 AM):

```bash
chmod +x /opt/roomrent/backup.sh
crontab -e
# Agregar:
# 0 2 * * * /opt/roomrent/backup.sh >> /var/log/roomrent-backup.log 2>&1
```

---

## Estado actual en RC1

| Item | Estado |
|------|--------|
| Backup automático | ❌ No configurado (pendiente) |
| Backup manual documentado | ✅ |
| Backup de .env | ✅ Manual |
| Certificados SSL | Let's Encrypt autorenews |
| Código fuente | ✅ GitHub (github.com/Jose-Bohorquez/roomRent) |
