#!/bin/bash
# ═══════════════════════════════════════════════════════════════
# RoomRent — Script de backup de MongoDB
#
# Uso: ./scripts/backup.sh
# Cron sugerido (diario a las 3am):
#   0 3 * * * /opt/roomrent/scripts/backup.sh >> /var/log/roomrent-backup.log 2>&1
#
# Los backups se guardan en /var/backups/roomrent/ y se retienen
# los últimos 7 días.
# ═══════════════════════════════════════════════════════════════
set -euo pipefail

BACKUP_DIR="/var/backups/roomrent"
RETENTION_DAYS=7
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="${BACKUP_DIR}/mongo_${TIMESTAMP}.gz"

# Cargar variables de entorno desde el .env del proyecto
COMPOSE_DIR="$(cd "$(dirname "$0")/.." && pwd)"
if [ -f "${COMPOSE_DIR}/.env" ]; then
  # Solo carga las variables MONGO_* para no contaminar el entorno
  export $(grep -E '^MONGO_' "${COMPOSE_DIR}/.env" | xargs)
fi

echo "[backup] Iniciando backup: $BACKUP_FILE"
mkdir -p "$BACKUP_DIR"

# Dump comprimido directo al archivo
docker compose -f "${COMPOSE_DIR}/docker-compose.yml" exec -T mongo \
  mongodump \
    --username  "$MONGO_INITDB_ROOT_USERNAME" \
    --password  "$MONGO_INITDB_ROOT_PASSWORD" \
    --authenticationDatabase admin \
    --db room \
    --archive \
    --gzip \
  > "$BACKUP_FILE"

SIZE=$(du -sh "$BACKUP_FILE" | cut -f1)
echo "[backup] ✓ Backup completado: $BACKUP_FILE ($SIZE)"

# Eliminar backups más antiguos que $RETENTION_DAYS días
echo "[backup] Eliminando backups con más de ${RETENTION_DAYS} días..."
find "$BACKUP_DIR" -name "mongo_*.gz" -mtime "+${RETENTION_DAYS}" -delete
echo "[backup] Backups actuales:"
ls -lh "$BACKUP_DIR"/mongo_*.gz 2>/dev/null || echo "  (ninguno)"
