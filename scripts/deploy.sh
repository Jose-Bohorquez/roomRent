#!/bin/bash
# ═══════════════════════════════════════════════════════════════
# RoomRent — Script de despliegue
#
# Uso: ./scripts/deploy.sh [--no-cache]
#
# Prerrequisitos en el servidor:
#   - Docker y Docker Compose instalados
#   - Archivo .env en /opt/roomrent/ (ver .env.example)
#   - Nginx configurado y corriendo
# ═══════════════════════════════════════════════════════════════
set -euo pipefail

COMPOSE_DIR="$(cd "$(dirname "$0")/.." && pwd)"
IMAGE_NAME="roomrent-app"

echo "[deploy] Directorio del proyecto: $COMPOSE_DIR"
cd "$COMPOSE_DIR"

# ── 1. Verificar .env ──────────────────────────────────────────
if [ ! -f .env ]; then
  echo "[deploy] ERROR: Falta el archivo .env. Cópialo desde .env.example y configúralo."
  exit 1
fi

# ── 2. Pull de la última versión del código ────────────────────
echo "[deploy] Actualizando código desde Git..."
git pull origin main

# ── 3. Construir la imagen Docker ─────────────────────────────
BUILD_ARGS=""
if [ "${1:-}" = "--no-cache" ]; then
  BUILD_ARGS="--no-cache"
  echo "[deploy] Construyendo imagen sin caché..."
else
  echo "[deploy] Construyendo imagen (con caché)..."
fi

docker build $BUILD_ARGS -t "${IMAGE_NAME}:latest" .

# ── 4. Levantar los servicios ─────────────────────────────────
echo "[deploy] Iniciando servicios con Docker Compose..."
docker compose up -d --remove-orphans

# ── 5. Esperar health checks ──────────────────────────────────
echo "[deploy] Esperando que la aplicación esté lista..."
RETRIES=30
until docker compose exec -T app wget -qO- http://localhost:8080/management/health 2>/dev/null | grep -q '"status":"UP"'; do
  RETRIES=$((RETRIES - 1))
  if [ "$RETRIES" -eq 0 ]; then
    echo "[deploy] ERROR: La aplicación no arrancó en el tiempo esperado."
    echo "[deploy] Logs recientes:"
    docker compose logs --tail=50 app
    exit 1
  fi
  echo "[deploy] Esperando... ($RETRIES intentos restantes)"
  sleep 5
done

echo "[deploy] ✓ Aplicación disponible en https://room-rent.xyz"
echo "[deploy] Estado de los contenedores:"
docker compose ps
