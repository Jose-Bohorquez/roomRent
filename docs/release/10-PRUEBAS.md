# PRUEBAS — RoomRent RC1

Evidencia de pruebas realizadas durante la validación Release Candidate 1.

Fecha: 2026-07-02 | Entorno: https://room-rent.xyz

---

## FASE RC1 — Creación de inmueble

**Usuario**: arrendador_rc01 (ROLE_ARRENDADOR + ROLE_USER)
**Fecha**: 2026-07-02

### POST /api/authenticate
```bash
curl -X POST https://room-rent.xyz/api/authenticate \
  -H 'Content-Type: application/json' \
  -d '{"username":"arrendador_rc01","password":"***","rememberMe":false}'
# Respuesta: HTTP 200, {"id_token":"eyJhbGci..."}
```

### POST /api/inmuebles
```http
POST /api/inmuebles HTTP/1.1
Authorization: Bearer eyJhbGci...
Content-Type: application/json

{
  "nombre": "Habitación RC Test",
  "tipoInmueble": "HABITACION",
  "ciudad": "Bogotá",
  "barrio": "Galerías",
  "direccion": "Calle 72 #10-34 Apto 501",
  "estrato": 3,
  "areaMetrosCuadrados": 58.0,
  "numeroHabitaciones": 1,
  "numeroBanos": 1
}
```
**Respuesta**: HTTP 201  
**ID creado**: `6a46d9774f2cf34de0c9b485`  
**createdBy**: `arrendador_rc01`

### POST /api/publicacion-inmuebles
```http
POST /api/publicacion-inmuebles HTTP/1.1
Authorization: Bearer eyJhbGci...
Content-Type: application/json

{
  "titulo": "Habitación disponible RC1",
  "estado": "PUBLICADA",
  "canonArriendo": 1850000,
  "permiteRoomies": false,
  "aceptaMascotas": false,
  "permiteFumadores": false,
  "permiteNinos": true,
  "permiteVisitas": true,
  "permiteParejas": true,
  "inmueble": {"id": "6a46d9774f2cf34de0c9b485"}
}
```
**Respuesta**: HTTP 201  
**ID creado**: `6a46d9934f2cf34de0c9b486`  
**Estado**: PUBLICADA

---

## FASE RC2 — Subida de 5 fotos

```bash
# 5 veces:
curl -X POST https://room-rent.xyz/api/uploads/multimedia \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@foto_N.png;type=image/png"
# Respuesta cada vez: HTTP 201 {"filename":"UUID.png","url":"/uploads/UUID.png"}
```

**Archivos subidos** (5 PNGs en /app/uploads/):
- `0610b537-2b0f-4585-a505-2d9249053c91.png`
- `b513f15b-59a8-46f2-bf92-95b62845fee7.png`
- `5e9ffd39-f6cc-4aa6-ae7d-dde61dea2456.png`
- `04183205-e46a-40b2-9546-c91be3b9a114.png`
- `0ba51cff-beb5-42fb-8918-fc324f871543.png`

```bash
# Vincular 5 veces:
curl -X POST https://room-rent.xyz/api/multimedia-inmuebles \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"urlMedia":"/uploads/UUID.png","tipoMedia":"image/png","principal":true,"inmueble":{"id":"6a46d9774f2cf34de0c9b485"}}'
# Respuesta: HTTP 201 × 5
```

**Verificación de acceso público**:
```bash
curl -I https://room-rent.xyz/uploads/0610b537-2b0f-4585-a505-2d9249053c91.png
# HTTP 200, Content-Type: image/png, 238 bytes
```

---

## FASE RC3 — Persistencia docker compose down/up

**Ejecutado**: 2026-07-02 17:44:38

```bash
cd /opt/roomrent && docker compose down && docker compose up -d
```

**Verificación post-restart**:

| Dato | Antes | Después | Estado |
|------|-------|---------|--------|
| Spring Boot | UP | UP | ✅ |
| inmueble `6a46d977...` | Existe | Existe | ✅ |
| publicacion `6a46d993...` | PUBLICADA | PUBLICADA | ✅ |
| multimedia_inmueble | 5 docs | 5 docs | ✅ |
| /uploads/*.png | 5 archivos | 5 archivos | ✅ |
| GET /uploads/...png | HTTP 200 | HTTP 200 | ✅ |

---

## MÓDULO 2 — Arrendador CRUD (arrendador_rc02)

**Usuario**: arrendador_rc02 (creado via admin API + reset password)

| Operación | Endpoint | HTTP esperado | HTTP real | Estado |
|-----------|----------|---------------|-----------|--------|
| Login | POST /api/authenticate | 200 | 200 | ✅ |
| Crear inmueble | POST /api/inmuebles | 201 | 201 | ✅ |
| Editar inmueble | PATCH /api/inmuebles/{id} | 200 | 200 | ✅ |
| Crear publicacion | POST /api/publicacion-inmuebles | 201 | 201 | ✅ |
| Despublicar | PATCH /api/publicacion-inmuebles/{id} | 200 | 200 | ✅ |
| Re-publicar | PATCH /api/publicacion-inmuebles/{id} | 200 | 200 | ✅ |
| Eliminar publicacion | DELETE /api/publicacion-inmuebles/{id} | 204 | 204 | ✅ |
| Eliminar inmueble | DELETE /api/inmuebles/{id} | 204 | 204 | ✅ |

**Nota sobre PATCH**: Requiere campo `id` en el body además del path parameter (comportamiento JHipster estándar).

---

## MÓDULO 7 — Multimedia completo

| Operación | Resultado | Estado |
|-----------|-----------|--------|
| POST /api/uploads/multimedia (PNG 70B) | HTTP 201, URL devuelta | ✅ |
| POST /api/multimedia-inmuebles | HTTP 201, ID asignado | ✅ |
| GET /uploads/{uuid}.png | HTTP 200, image/png | ✅ |
| DELETE /api/multimedia-inmuebles/{id} | HTTP 204 | ✅ |
| GET /api/multimedia-inmuebles sin token (post-fix) | HTTP 200 | ✅ (após rebuild) |

---

## MÓDULO 9 — Seguridad

| Test | Esperado | Resultado | Estado |
|------|----------|-----------|--------|
| GET /api/admin/users sin token | 401 | 401 | ✅ |
| POST /api/inmuebles sin token | 401 | 401 | ✅ |
| DELETE /api/inmuebles/{id} sin token | 401 | 401 | ✅ |
| GET /api/solicitud-arriendos sin token | 401 | 401 | ✅ |
| GET /api/visita-programadas sin token | 401 | 401 | ✅ |
| GET /api/contrato-arriendos sin token | 401 | 401 | ✅ |
| GET /api/admin/users con token ARRENDADOR | 403 | 403 | ✅ |
| GET /api/inmuebles (público) | 200 | 200 | ✅ |
| GET /api/publicacion-inmuebles (público) | 200 | 200 | ✅ |
| GET /management/health (público) | 200 | 200 | ✅ |

---

## MÓDULO 8 — PWA

| Item | Resultado |
|------|-----------|
| /portal/sw.js | HTTP 200, 1073 bytes, Cache-Control: no-store |
| /portal/manifest.webmanifest | HTTP 200, Content-Type: application/manifest+json |
| /portal/icon-192.png | HTTP 200, 9729 bytes |
| /portal/icon-512.png | HTTP 200, 29531 bytes |

---

## MÓDULO 10 — Performance / Nginx

| Header | Valor | Estado |
|--------|-------|--------|
| HTTP/2 | Habilitado | ✅ |
| Content-Encoding: gzip | Habilitado | ✅ |
| Strict-Transport-Security | max-age=63072000; includeSubDomains | ✅ |
| X-Frame-Options | SAMEORIGIN | ✅ |
| X-Content-Type-Options | nosniff | ✅ |
| Content-Security-Policy | Configurada | ✅ |
| HTTPS response time | 38ms (local) | ✅ |

---

## MÓDULO 11 — Producción

| Check | Resultado | Estado |
|-------|-----------|--------|
| HTTP → HTTPS redirect | 301 | ✅ |
| SSL CN | room-rent.xyz | ✅ |
| SSL issuer | Let's Encrypt | ✅ |
| TLS version | 1.2 + 1.3 | ✅ |
| Docker nginx autostart | enabled | ✅ |
| Docker docker autostart | enabled | ✅ |
| MongoDB collections | 13 colecciones | ✅ |
| MongoDB indexes | 12 índices custom | ✅ |
| Spring Boot NO ERRORS en logs | 0 ERROR | ✅ |

---

## Bugs encontrados y corregidos en RC1

| Bug | Severidad | Commit fix |
|-----|-----------|------------|
| GET /api/multimedia-inmuebles → 401 | Alta | acafa33 |
| MisInmueblesPage carga todos los inmuebles | Media | acafa33 |
| / redirect genera URL HTTP | Media | a1436f1 |
| Paths.get() deprecated rechazado en build | Bloqueante | 7cb0f25 |
| Duplicate spring: key en application.yml | Bloqueante | c98a1b1 |
| /app/uploads no escribible por roomrent | Alta | 71022a8 |
