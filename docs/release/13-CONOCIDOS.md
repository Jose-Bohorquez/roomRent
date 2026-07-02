# BUGS CONOCIDOS — RoomRent RC1

Bugs identificados y su estado actual.

Fecha: 2026-07-02 | Versión: RC1

---

## Corregidos en RC1

### BUG-001 — modernizer-maven-plugin rechaza Paths.get()
- **Commit fix**: `7cb0f25`
- **Descripción**: `FileUploadResource.java` usaba `Paths.get()` (deprecated API). `modernizer-maven-plugin` v3.4.0 rechaza esta API y falla el build con `VIOLATION: java.nio.file.Paths#get(String)`.
- **Fix**: Reemplazar `Paths.get(uploadPath)` por `Path.of(uploadPath)` y eliminar `import java.nio.file.Paths`.
- **Archivos**: `FileUploadResource.java`

### BUG-002 — SnakeYAML crash por duplicate key en application.yml
- **Commit fix**: `c98a1b1`
- **Descripción**: `application.yml` tenía tres bloques `spring:` en el mismo documento YAML. SnakeYAML 2.x (Spring Boot 4) es estricto y lanza `found duplicate key spring in 'reader', line 156`. Versiones anteriores lo ignoraban silenciosamente.
- **Fix**: Mergear los settings de `servlet.multipart` dentro del bloque `spring:` existente y eliminar el bloque duplicado.
- **Archivos**: `src/main/resources/config/application.yml`

### BUG-003 — Docker: /app/uploads no escribible por usuario roomrent
- **Commit fix**: `71022a8`
- **Descripción**: El contenedor runtime usa usuario `roomrent` (UID 100), pero el volumen nombrado `roomrent-uploads-data` montaba con permisos `root:root` (755). La subida de archivos fallaba con HTTP 500 / `Permission denied`.
- **Fix**: `mkdir -p /app/uploads && chown roomrent:roomrent /app/uploads` en el Dockerfile runtime stage antes del `USER roomrent`.
- **Fix inmediato EC2**: `sudo chown -R 100:101 /var/lib/docker/volumes/roomrent-uploads-data/_data`
- **Archivos**: `Dockerfile`

### BUG-004 — GET /api/multimedia-inmuebles → HTTP 401 para usuarios anónimos
- **Commit fix**: `acafa33`
- **Descripción**: El endpoint de multimedia de inmuebles requería autenticación, pero las fotos de una propiedad deben ser públicas. Usuarios anónimos viendo el detalle de un inmueble no podían cargar las fotos.
- **Fix**: Agregar `.requestMatchers(HttpMethod.GET, "/api/multimedia-inmuebles/**").permitAll()` en `SecurityConfiguration.java`.
- **Archivos**: `SecurityConfiguration.java`

### BUG-005 — MisInmueblesPage mostraba todos los inmuebles del sistema
- **Commit fix**: `acafa33`
- **Descripción**: La página "Mis Inmuebles" del arrendador cargaba `GET /api/inmuebles?size=50` sin filtro, devolviendo inmuebles de todos los propietarios.
- **Fix**: Filtrar en frontend por `createdBy === user.login` usando `useAuth()`.
- **Archivos**: `frontRoomRent/src/pages/arrendador/MisInmueblesPage.jsx`

### BUG-006 — / redirect genera Location con esquema HTTP (no HTTPS)
- **Commit fix**: `a1436f1`
- **Descripción**: Spring Boot no procesaba el header `X-Forwarded-Proto: https` que nginx envía. Al redirigir `/` → `/portal/`, generaba `Location: http://room-rent.xyz/portal/` (HTTP) en lugar de HTTPS. Requería un redirect adicional de nginx para corregir.
- **Fix**: `server.forward-headers-strategy: native` en `application-prod.yml`.
- **Archivos**: `src/main/resources/config/application-prod.yml`
- **Estado**: Corregido en código, pendiente de despliegue (rebuild en curso al momento del RC1).

---

## Activos — No corregidos

### BUG-007 — MisInmueblesPage no muestra thumbnail de fotos
- **Severidad**: Media (cosmética)
- **Descripción**: La página `MisInmueblesPage.jsx` intenta mostrar la foto principal del inmueble leyendo `inm.multimedias` del objeto inmueble. El endpoint `GET /api/inmuebles` no incluye las multimedias embebidas en la respuesta, por lo que `getImage(inm)` siempre retorna `null`. Las tarjetas se muestran sin imagen.
- **Workaround**: Ninguno en UI. Los inmuebles se muestran pero sin preview de foto.
- **Fix requerido**: Agregar fetch de multimedia por inmueble o incluir multimedias en la respuesta de `GET /api/inmuebles`.

### BUG-008 — Email de activación no funcional en producción
- **Severidad**: Alta (funcionalidad crítica)
- **Descripción**: `POST /api/register` registra el usuario correctamente en MongoDB con `activated: false` y un `activation_key`. Pero el email de activación no llega porque `SPRING_MAIL_PASSWORD` puede no estar correctamente configurado en `.env`.
- **Síntoma**: Registro retorna HTTP 201 pero el usuario no recibe email. Workaround: activar manualmente desde el admin panel.
- **Verificación**: Spring logs muestran "Password reset requested for non existing mail" (indica SMTP falla en enviar).
- **Fix requerido**: Verificar `SPRING_MAIL_PASSWORD` en `/opt/roomrent/.env` y asegurarse que la cuenta Gmail tiene la app password correcta y está configurada para SMTP.

### BUG-009 — PATCH /api/* requiere `id` en body (no documentado)
- **Severidad**: Baja (conocido, JHipster behavior)
- **Descripción**: Los endpoints `PATCH /api/inmuebles/{id}` y similares requieren que el campo `id` esté presente en el cuerpo del request, además del path. Si se omite, retorna HTTP 400 con "Invalid id: idnull".
- **Workaround**: Siempre incluir `{"id": "{id}", ...campos a actualizar}` en requests PATCH.
- **Fix requerido**: Documentar en 07-API.md. No es un bug real, es el comportamiento esperado de JHipster.

---

## Riesgos de seguridad conocidos

### RIESGO-001 — JWT secret de muestra en producción
- **Severidad**: CRÍTICA (en producción real)
- **Descripción**: El perfil `secret-samples` carga `application-secret-samples.yml` que contiene un `base64-secret` hardcodeado. Este secret está en el repositorio git público.
- **Estado**: En un sistema de pruebas/estudiantes es aceptable. En producción real con datos reales, DEBE rotarse.
- **Fix**: Mover `jhipster.security.authentication.jwt.base64-secret` a variable de entorno `JWT_BASE64_SECRET` y cargarlo desde `.env`.

### RIESGO-002 — Mongo credentials en SPRING_MONGODB_URI visible en logs
- **Severidad**: Media
- **Descripción**: La URI de MongoDB con credenciales puede aparecer en logs de startup de Spring Boot en modo DEBUG.
- **Estado**: En prod el log level es INFO, por lo que no debería aparecer.
- **Fix**: Monitorear que el nivel de log en prod nunca baje a DEBUG o TRACE.
