# RELEASE CANDIDATE RC1 — RoomRent

**Versión**: 0.0.1-RC1  
**Fecha de validación**: 2026-07-02  
**Entorno**: https://room-rent.xyz  
**Branch**: `main` | **Commit HEAD**: `a1436f1`  
**Validado por**: Jose J. Bohorquez D.  
**Roles durante validación**: Tech Lead, QA Lead, DevOps, Product Owner, Software Architect, Auditor, Release Manager

---

## RESUMEN EJECUTIVO

RoomRent RC1 es una plataforma de arrendamiento inmobiliario desarrollada como proyecto académico del programa ADSO (ficha 3311941, trimestre 4) del SENA. El sistema fue construido con JHipster 9.1.0 sobre un stack monolítico Spring Boot 4.0.6 + Angular 21 (panel admin) + React 18 (portal público) + MongoDB 7, desplegado en EC2 AWS mediante Docker Compose con nginx como reverse proxy y TLS via Let's Encrypt.

**Estado RC1: APROBADO CON OBSERVACIONES**

- ✅ 11 de 12 módulos validados satisfactoriamente
- ✅ 6 bugs críticos corregidos durante la validación
- ✅ 17 documentos técnicos generados
- ⚠️ 3 bugs activos no bloqueantes (BUG-007, BUG-008, BUG-009)
- ⚠️ 1 módulo con validación parcial (Módulo 1: email de activación no funcional)
- ⚠️ 2 riesgos de seguridad conocidos (aceptables en ambiente académico)

---

## 1. INFORMACIÓN DEL PROYECTO

| Campo | Valor |
|-------|-------|
| Proyecto | RoomRent |
| Versión | 0.0.1-RC1 |
| Tipo | Plataforma web de arrendamiento inmobiliario |
| Contexto | Proyecto académico SENA ADSO 3311941 |
| Repositorio | github.com/Jose-Bohorquez/roomRent |
| Dominio | room-rent.xyz |
| IP producción | 34.202.0.109 (EC2 us-east-1) |
| Framework | JHipster 9.1.0 |
| Backend | Spring Boot 4.0.6 (Java 21) |
| Frontend admin | Angular 21 |
| Frontend portal | React 18 + Vite |
| Base de datos | MongoDB 7 |
| Contenedores | Docker Compose (2 containers) |
| SSL/TLS | Let's Encrypt, TLS 1.2/1.3, HTTP/2 |

---

## 2. ARQUITECTURA

```
Browser (HTTPS:443)
      │
      ▼
nginx 1.28.3 ── TLS termination, HTTP/2, gzip, HSTS, security headers
      │ proxy_pass http://127.0.0.1:8080
      ▼
Spring Boot 4.0.6 ── Docker container roomrent-app-1
  ├── /portal/**     → React 18 (SPA arrendatario)
  ├── /api/**        → REST API (JWT HS512)
  ├── /uploads/**    → Archivos multimedia
  ├── /management/** → Spring Actuator
  └── /**            → Angular 21 (SPA admin)
      │
      ▼
MongoDB 7 ── Docker container roomrent-mongo-1
             Volumen roomrent-mongo-data (persistente)

Fotos: Docker volumen roomrent-uploads-data → /app/uploads/
```

**Patrón Dual-SPA**: Angular admin en `/` para administradores, React portal en `/portal/` para arrendadores y arrendatarios públicos.

**Seguridad**: Spring Security + JWT stateless. Rutas públicas explícitas; todo lo demás requiere autenticación. `@PreAuthorize` a nivel de método en recursos críticos.

---

## 3. MÓDULOS VALIDADOS

### Módulo 1 — Registro y Autenticación

| Flujo | Estado | Evidencia |
|-------|--------|-----------|
| POST /api/register (crear cuenta) | ✅ HTTP 201 | `{"created":true}` |
| MongoDB: usuario con `activated:false` + `activation_key` | ✅ Verificado | mongosh query |
| Email de activación | ❌ No funcional | SMTP no configurado (BUG-008) |
| Activación manual via admin | ✅ Funcional | Admin panel |
| POST /api/authenticate | ✅ HTTP 200 + JWT | Token recibido |
| Logout (clear token client-side) | ✅ Funcional | UI verifica |
| Password reset: POST .../init | ✅ HTTP 200 | `reset_key` en MongoDB |
| Password reset: POST .../finish | ✅ HTTP 200 | Nueva password activa |

**Resultado Módulo 1**: PARCIAL (email no funcional, workaround via admin disponible)

---

### Módulo 2 — Arrendador: CRUD de Inmueble

| Operación | HTTP esperado | HTTP real | Estado |
|-----------|---------------|-----------|--------|
| POST /api/inmuebles | 201 | 201 | ✅ |
| GET /api/inmuebles/{id} | 200 | 200 | ✅ |
| PATCH /api/inmuebles/{id} | 200 | 200 | ✅ |
| DELETE /api/inmuebles/{id} | 204 | 204 | ✅ |
| POST /api/publicacion-inmuebles | 201 | 201 | ✅ |
| PATCH publicacion estado=PUBLICADA | 200 | 200 | ✅ |
| PATCH publicacion estado=ARCHIVADA | 200 | 200 | ✅ |
| DELETE /api/publicacion-inmuebles | 204 | 204 | ✅ |
| MisInmueblesPage filtra por propietario | ✅ Fix BUG-005 | acafa33 | ✅ |
| MisInmueblesPage muestra thumbnails | ❌ BUG-007 activo | Sin fotos | ⚠️ |

**Resultado Módulo 2**: APROBADO CON OBSERVACIÓN (thumbnails no cargan)

---

### Módulo 3 — Home: Grid de propiedades

| Check | Estado |
|-------|--------|
| GET /api/publicacion-inmuebles devuelve publicadas | ✅ |
| React portal muestra grid de propiedades | ✅ |
| Datos reales de MongoDB (no hardcoded) | ✅ |
| Paginación disponible (JHipster standard) | ✅ |

**Resultado Módulo 3**: APROBADO

---

### Módulo 4 — PropertyDetail: Detalle de inmueble

| Check | Estado |
|-------|--------|
| GET /api/inmuebles/{id} accesible sin token | ✅ |
| GET /api/multimedia-inmuebles accesible sin token | ✅ Fix BUG-004 |
| GET /uploads/{uuid}.png devuelve imagen | ✅ HTTP 200 |
| Especificaciones muestran datos reales | ✅ |
| URLs de multimedia no rotas | ✅ |

**Resultado Módulo 4**: APROBADO

---

### Módulo 5 — Arrendatario: Búsqueda

| Check | Estado |
|-------|--------|
| Listado de publicaciones visible sin login | ✅ |
| GET /api/inmuebles (público) | ✅ HTTP 200 |
| Filtros UI (ciudad, tipo, precio) | ⚠️ No implementado (pendiente P3) |
| PropertyDetail accesible anónimo | ✅ |

**Resultado Módulo 5**: APROBADO CON OBSERVACIÓN (filtros pendientes)

---

### Módulo 6 — Administrador: Gestión de usuarios

| Check | Estado |
|-------|--------|
| GET /api/admin/users (ROLE_ADMIN) | ✅ HTTP 200 |
| GET /api/admin/users (ROLE_ARRENDADOR) | ✅ HTTP 403 |
| GET /api/admin/users (sin token) | ✅ HTTP 401 |
| POST /api/admin/users (crear) | ✅ HTTP 201 |
| PUT /api/admin/users (editar roles) | ✅ HTTP 200 |
| Panel Angular admin funcional | ✅ |

**Resultado Módulo 6**: APROBADO

---

### Módulo 7 — Multimedia: Upload y persistencia

| Operación | Resultado | Estado |
|-----------|-----------|--------|
| POST /api/uploads/multimedia (PNG) | HTTP 201, URL generada | ✅ |
| POST /api/multimedia-inmuebles | HTTP 201, vinculado | ✅ |
| GET /uploads/{uuid}.png | HTTP 200, image/png | ✅ |
| Persistencia después de compose down/up | Fotos siguen en volumen | ✅ |
| DELETE /api/multimedia-inmuebles/{id} | HTTP 204 | ✅ |
| Acceso público sin token | HTTP 200 (post BUG-004 fix) | ✅ |

**Resultado Módulo 7**: APROBADO

---

### Módulo 8 — PWA

| Item | Resultado | Estado |
|------|-----------|--------|
| /portal/sw.js | HTTP 200, Cache-Control: no-store | ✅ |
| /portal/manifest.webmanifest | HTTP 200, application/manifest+json | ✅ |
| /portal/icon-192.png | HTTP 200, 9729 bytes | ✅ |
| /portal/icon-512.png | HTTP 200, 29531 bytes | ✅ |
| Lighthouse PWA score | Sin captura formal | ⚠️ Pendiente |

**Resultado Módulo 8**: APROBADO CON OBSERVACIÓN (Lighthouse formal pendiente)

---

### Módulo 9 — Seguridad

| Test | Esperado | Resultado | Estado |
|------|----------|-----------|--------|
| GET /api/admin/users sin token | 401 | 401 | ✅ |
| POST /api/inmuebles sin token | 401 | 401 | ✅ |
| DELETE /api/inmuebles/{id} sin token | 401 | 401 | ✅ |
| GET /api/solicitud-arriendos sin token | 401 | 401 | ✅ |
| GET /api/visita-programadas sin token | 401 | 401 | ✅ |
| GET /api/contrato-arriendos sin token | 401 | 401 | ✅ |
| GET /api/admin/users con ROLE_ARRENDADOR | 403 | 403 | ✅ |
| IDOR: modificar inmueble de otro usuario | 403 | 403 | ✅ |
| JWT expirado | 401 | 401 | ✅ |
| JWT manipulado | 401 | 401 | ✅ |
| GET /api/inmuebles (público) | 200 | 200 | ✅ |
| HTTPS obligatorio (HTTP → redirect 301) | 301 | 301 | ✅ |
| HSTS header | max-age=63072000 | Presente | ✅ |
| X-Frame-Options | SAMEORIGIN | Presente | ✅ |
| X-Content-Type-Options | nosniff | Presente | ✅ |

**Resultado Módulo 9**: APROBADO

---

### Módulo 10 — Performance

| Métrica | Valor | Estado |
|---------|-------|--------|
| nginx gzip | Habilitado (level 6) | ✅ |
| HTTP/2 | Habilitado | ✅ |
| Cache JS/CSS | 1 año + immutable | ✅ |
| Cache imágenes | 30 días | ✅ |
| Cache manifest | 24 horas | ✅ |
| Spring Boot startup | ~35 segundos | ✅ |
| Response time /management/health | < 50ms | ✅ |
| RAM total disponible | 908 MB | ✅ |
| Swap configurado | 2048 MB | ✅ |
| Docker volumes nombrados | Sin pérdida de datos | ✅ |

**Resultado Módulo 10**: APROBADO

---

### Módulo 11 — Producción

| Check | Resultado | Estado |
|-------|-----------|--------|
| GitHub HEAD == EC2 HEAD | `a1436f1` en ambos | ✅ |
| Docker containers running | app + mongo (healthy) | ✅ |
| nginx systemd enabled | Autostart en boot | ✅ |
| docker systemd enabled | Autostart en boot | ✅ |
| SSL certificado válido | Let's Encrypt, no expirado | ✅ |
| HTTP → HTTPS redirect | 301 | ✅ |
| TLS 1.2 + 1.3 | Habilitados | ✅ |
| 0 ERRORs en Spring logs | 0 errores | ✅ |
| MongoDB 13 colecciones | Verificado | ✅ |
| MongoDB 12 índices custom | Verificado | ✅ |
| Spring Boot health UP | `{"status":"UP"}` | ✅ |
| Credenciales fuera del código | Solo en .env | ✅ |

**Resultado Módulo 11**: APROBADO

---

### Módulo 12 — Documentación

| Documento | Estado |
|-----------|--------|
| 01-ARQUITECTURA.md | ✅ Creado (508 líneas) |
| 02-INFRAESTRUCTURA.md | ✅ Creado (791 líneas) |
| 03-DESPLIEGUE.md | ✅ Creado |
| 04-BACKUPS.md | ✅ Creado |
| 05-PWA.md | ✅ Creado |
| 06-DATABASE.md | ✅ Creado |
| 07-API.md | ✅ Creado |
| 08-USUARIOS-Y-ROLES.md | ✅ Creado |
| 09-FLUJOS-FUNCIONALES.md | ✅ Creado |
| 10-PRUEBAS.md | ✅ Creado |
| 11-CHANGELOG.md | ✅ Creado (36 commits) |
| 12-PENDIENTES.md | ✅ Creado |
| 13-CONOCIDOS.md | ✅ Creado |
| 14-PRODUCCION.md | ✅ Creado |
| 15-OPERACION.md | ✅ Creado |
| 16-RECUPERACION.md | ✅ Creado |
| 17-ROADMAP.md | ✅ Creado |
| RELEASE_CANDIDATE_RC1.md | ✅ Este documento |

**Resultado Módulo 12**: APROBADO — 18 documentos completos

---

## 4. BUGS CORREGIDOS DURANTE RC1

| ID | Severidad | Descripción | Commit |
|----|-----------|-------------|--------|
| BUG-001 | Bloqueante | `Paths.get()` rechazado por modernizer-maven-plugin | `7cb0f25` |
| BUG-002 | Bloqueante | Duplicate `spring:` key en application.yml (SnakeYAML 2.x) | `c98a1b1` |
| BUG-003 | Alta | `/app/uploads` no escribible por usuario `roomrent` (UID 100) | `71022a8` |
| BUG-004 | Alta | `GET /api/multimedia-inmuebles/**` → HTTP 401 para usuarios anónimos | `acafa33` |
| BUG-005 | Media | `MisInmueblesPage` mostraba inmuebles de TODOS los usuarios | `acafa33` |
| BUG-006 | Media | Redirect `/` generaba `Location: http://` en lugar de `https://` | `a1436f1` |

---

## 5. BUGS ACTIVOS (NO BLOQUEANTES)

| ID | Severidad | Descripción | Fix requerido |
|----|-----------|-------------|---------------|
| BUG-007 | Media | `MisInmueblesPage` no muestra thumbnails (multimedia no embebido) | Fetch adicional de multimedia |
| BUG-008 | Alta | Email de activación no llega (SMTP no configurado) | `SPRING_MAIL_PASSWORD` en .env |
| BUG-009 | Baja | PATCH requiere `id` en body además del path (comportamiento JHipster) | Documentar |

---

## 6. RIESGOS DE SEGURIDAD

| ID | Severidad | Descripción | Impacto |
|----|-----------|-------------|---------|
| RIESGO-001 | CRÍTICA* | JWT secret de `secret-samples` en git público | Suplantación de tokens en prod real |
| RIESGO-002 | Media | URI MongoDB puede aparecer en logs DEBUG | Exposición de credenciales |

*Aceptable en ambiente académico. CRÍTICO si se migrara a producción real con usuarios reales.

---

## 7. COMMITS PRINCIPALES RC1

| Hash | Mensaje | Archivos |
|------|---------|---------|
| `a1436f1` | fix: forward-headers-strategy para HTTPS redirect correcto | application-prod.yml |
| `acafa33` | fix: permitAll multimedia-inmuebles + filtro MisInmueblesPage por propietario | SecurityConfiguration.java, MisInmueblesPage.jsx |
| `71022a8` | fix: mkdir /app/uploads y chown roomrent en Dockerfile | Dockerfile |
| `7cb0f25` | fix: Path.of() en lugar de Paths.get() (modernizer-maven-plugin) | FileUploadResource.java |
| `c98a1b1` | fix: mergear bloques spring: duplicados en application.yml | application.yml |

---

## 8. ENTORNO DE PRODUCCIÓN RC1

| Item | Valor |
|------|-------|
| EC2 | t2.micro, us-east-1, Ubuntu 24.04 |
| RAM | 908 MB + 2048 MB swap |
| Disco | 38 GB (33% usado) |
| Contenedor app | `roomrent-app-1` (eclipse-temurin:21-jre-alpine) |
| Contenedor BD | `roomrent-mongo-1` (mongo:7) |
| Usuario runtime | `roomrent` (UID 100, GID 101) — no root |
| Volúmenes | `roomrent-mongo-data`, `roomrent-uploads-data` |
| Nginx | 1.28.3, HTTP/2, gzip level 6 |
| SSL | Let's Encrypt, CN=room-rent.xyz, TLS 1.2/1.3 |
| CI/CD | Manual: git pull → docker build → compose up -d |
| Autostart | nginx y docker via systemd; compose manual post-EC2-reboot |

---

## 9. VARIABLES DE ENTORNO (ESTRUCTURA)

Archivo `/opt/roomrent/.env` — NUNCA en git, NUNCA en código:

```ini
SPRING_PROFILES_ACTIVE=prod
SPRING_MONGODB_URI=mongodb://roomrent:***@mongo:27017/room?authSource=admin
JWT_BASE64_SECRET=<base64 64 bytes>
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=bd567358546@gmail.com
SPRING_MAIL_PASSWORD=<pendiente configurar>
JHIPSTER_MAIL_BASE_URL=https://room-rent.xyz
VITE_API_BASE=
MONGO_INITDB_ROOT_USERNAME=root
MONGO_INITDB_ROOT_PASSWORD=***
MONGO_INITDB_DATABASE=room
MONGO_USERNAME=roomrent
MONGO_PASSWORD=***
```

---

## 10. PRUEBAS REALIZADAS — RESUMEN

| Fase | Descripción | Resultado |
|------|-------------|-----------|
| RC1 | Crear inmueble + publicación via API | ✅ |
| RC2 | Subir 5 fotos y vincularlas a inmueble | ✅ |
| RC3 | Persistencia: docker compose down/up | ✅ |
| RC4 | Módulos 1-12 completos | ✅ (11/12) |
| Seguridad | 15 tests de autorización y autenticación | ✅ 15/15 |
| Performance | nginx gzip, HTTP/2, cache, RAM | ✅ |
| IDOR | Intentar modificar recursos de otro usuario | ✅ bloqueado |

Total: **~70 verificaciones** manuales realizadas via curl + Docker + MongoDB.

---

## 11. RUTAS PÚBLICAS CONFIRMADAS

```
GET  /portal/**                              → Portal React (sin auth)
GET  /uploads/**                             → Fotos de inmuebles (sin auth)
POST /api/authenticate                       → Login
POST /api/register                           → Registro
GET  /api/activate                           → Activación de cuenta
POST /api/account/reset-password/init        → Solicitar reset
POST /api/account/reset-password/finish      → Completar reset
GET  /api/publicacion-inmuebles/**           → Ver publicaciones
GET  /api/inmuebles/**                       → Ver inmuebles
GET  /api/multimedia-inmuebles/**            → Ver multimedia (BUG-004 fix)
GET  /management/health                      → Health check
GET  /management/info                        → Info
GET  /management/prometheus                  → Métricas
```

---

## 12. FLUJO E2E VALIDADO

```
1. Admin crea usuario arrendador_rc02 → HTTP 201
2. Password reset flow → nueva contraseña funcional
3. arrendador_rc02 hace login → JWT recibido
4. Crea inmueble (habitación) → HTTP 201
5. Sube 5 fotos → HTTP 201 × 5, URLs /uploads/UUID.png
6. Crea publicacion PUBLICADA → HTTP 201
7. Usuario anónimo ve la publicacion en /api/publicacion-inmuebles → HTTP 200
8. Usuario anónimo ve las fotos via /api/multimedia-inmuebles → HTTP 200 (post fix)
9. Usuario anónimo descarga foto /uploads/UUID.png → HTTP 200
10. docker compose down → mongo y app se detienen
11. docker compose up -d → mongo y app levantan
12. Los datos del paso 4-9 persisten (volúmenes Docker)
```

---

## 13. DOCUMENTACIÓN COMPLETA

17 documentos técnicos + este documento en `docs/release/`:

| # | Archivo | Contenido |
|---|---------|-----------|
| 01 | ARQUITECTURA.md | Diagrama, stack, rutas, JWT, roles, migraciones |
| 02 | INFRAESTRUCTURA.md | EC2, Docker, nginx, TLS, red, .env |
| 03 | DESPLIEGUE.md | Build, fresh deploy, actualización, rollback |
| 04 | BACKUPS.md | mongodump, uploads tar, crontab, restore |
| 05 | PWA.md | Service Worker, manifest, install prompt |
| 06 | DATABASE.md | 13 colecciones, índices, Mongock, mongosh |
| 07 | API.md | 70+ endpoints, ejemplos cURL, códigos de error |
| 08 | USUARIOS-Y-ROLES.md | 4 roles, permisos, matriz de acceso |
| 09 | FLUJOS-FUNCIONALES.md | Flujos completos: registro, CRUD, publicación |
| 10 | PRUEBAS.md | Evidencias: curl, HTTP codes, JSON responses |
| 11 | CHANGELOG.md | 36 commits documentados (v0.0.1 → RC1) |
| 12 | PENDIENTES.md | P1-P5 alto, M1-M6 medio, B1-B5 bajo |
| 13 | CONOCIDOS.md | 6 bugs corregidos, 3 activos, 2 riesgos |
| 14 | PRODUCCION.md | EC2, Docker, SSL, .env, monitoreo, EC2 reboot |
| 15 | OPERACION.md | Runbook diario, comandos frecuentes, alertas |
| 16 | RECUPERACION.md | 8 escenarios de desastre con procedimientos |
| 17 | ROADMAP.md | v0.1.0 → v1.0.0, deuda técnica, arquitectura futura |

---

## 14. PENDIENTES CRÍTICOS POST-RC1

| # | Pendiente | Prioridad | Esfuerzo |
|---|-----------|-----------|---------|
| 1 | Configurar `SPRING_MAIL_PASSWORD` en .env | ALTA | 30 min |
| 2 | Validar email de activación funcional | ALTA | 1 hora |
| 3 | Rotar JWT secret (sacar de secret-samples) | ALTA | 2 horas |
| 4 | Agregar `restart: unless-stopped` en docker-compose.yml | ALTA | 15 min |
| 5 | Configurar backup automático (crontab backup.sh) | MEDIA | 30 min |
| 6 | Fix BUG-007: thumbnails en MisInmueblesPage | MEDIA | 4 horas |
| 7 | Implementar flujo arrendatario (solicitudes, visitas) | ALTA | 7-10 días |
| 8 | Lighthouse PWA score ≥ 90 | BAJA | 1-2 días |

---

## 15. APROBACIÓN RC1

**Estado**: ✅ RELEASE CANDIDATE RC1 APROBADO

La plataforma RoomRent cumple con los requisitos funcionales básicos del ciclo ADSO:
- Autenticación y roles funcionan correctamente
- CRUD completo de arrendador (inmueble + publicación + multimedia)
- Datos persisten entre reinicios de contenedores
- Seguridad: endpoints protegidos, IDOR bloqueado, HTTPS obligatorio
- Documentación técnica completa (17 + 1 documentos)
- Producción accesible en https://room-rent.xyz

Las observaciones identificadas (BUG-007, BUG-008, Lighthouse) no bloquean la entrega académica y están documentadas en [12-PENDIENTES.md](12-PENDIENTES.md) y [17-ROADMAP.md](17-ROADMAP.md).

---

*Generado durante validación RC1 — 2026-07-02*  
*RoomRent — Proyecto ADSO 3311941 | SENA*
