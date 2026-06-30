# CONTEXTO — Estado Actual del Proyecto RoomRent

**Última actualización:** 2026-06-30  
**Versión:** 1.0  
**Mantenedor:** Jose Bohorquez

---

## 📍 LÍNEA BASE ACTUAL

### Tecnología Stack
- **Backend:** Spring Boot 4.0.6 + JHipster 9.1.0
- **BD:** MongoDB (local: `mongodb://localhost:27017/room`)
- **Frontend:** React + Vite
- **Autenticación:** JWT + Spring Security
- **Email:** Spring Mail (SMTP configurado)

### Compilación y Ejecución
- **Build:** `./mvnw clean package -DskipTests` (11-15 segundos)
- **Run:** `./run-dev.sh` (carga variables de .env, inicia en `http://localhost:8080`)
- **Tests:** Compilados pero skipped por default

---

## ✅ QUÉ FUNCIONA (Validado, Sin Bloqueos)

### 1. LOGIN — 🟢 FUNCIONA BIEN
```
Endpoint: POST /api/authenticate
Usuarios: admin/admin (predefinido)
Response: HTTP 200 + JWT válido
Validación: Completa, sin bloqueadores
```

### 2. EXPLORACIÓN DE PUBLICACIONES — 🟢 FUNCIONA BIEN
```
Endpoint: GET /api/publicacion-inmuebles
Data: 10+ publicaciones de seed
Campos: título, descripción, precio, estado, inmueble (relación)
Multimedia: URLs funcionales
Validación: Completa, datos consistentes
```

### 3. CREAR SOLICITUD + AUDITORÍA — 🟢 FUNCIONA BIEN
```
Endpoint: POST /api/solicitud-arriendos
Payload requerido:
  - publicacion: {id: "..."}
  - estado: "CREADA"
  - aceptaTerminos: true
  - fechaCreacion: ISO timestamp
Response: HTTP 201
Auditoría: createdBy y createdDate se populan automáticamente
Razón: SolicitudArriendo extends AbstractAuditingEntity<String>
Validación: Completa, auditoría funciona
```

---

## 🟡 QUÉ ESTÁ INCOMPLETO (Bloqueado por Credenciales)

### 4. SMTP / EMAIL — 🟡 INCOMPLETO

**Configuración:**
- Host: `smtp.gmail.com:587`
- Auth: Activado
- STARTTLS: Activado
- Variables: `${MAIL_HOST}`, `${MAIL_PORT}`, `${MAIL_USERNAME}`, `${MAIL_PASSWORD}`

**Estado:**
- ✅ SMTP intenta conectar
- ✅ STARTTLS se negocia
- ❌ Autenticación FALLA: `jakarta.mail.AuthenticationFailedException`

**Bloqueador:** `MAIL_PASSWORD` en `.env` no es Google App Password válido (es placeholder)

**Cómo desbloquear:**
1. Ir a: https://myaccount.google.com/apppasswords
2. Generar App Password (16 caracteres)
3. Actualizar `.env`: `MAIL_PASSWORD=xxxx-xxxx-xxxx-xxxx`
4. Reiniciar: `./run-dev.sh`

### 5. ACTIVACIÓN POR CORREO — 🟡 INCOMPLETO

**Endpoint:** GET /api/activate?key=...
**Status:** Público (no requiere JWT)
**Bloqueador:** Email no llega (SMTP falla)

**Para desbloquear:** Necesita P0 completo (email funcional)

### 6. PASSWORD RESET — 🟡 INCOMPLETO

**Endpoints:**
- POST /api/account/reset-password/init (público)
- POST /api/account/reset-password/finish (público)

**Status:** Endpoints existen, son públicos
**Bloqueador:** Email no llega

**Para desbloquear:** Necesita P0 completo (email funcional)

---

## ❓ QUÉ NO ESTÁ VALIDADO

### 7. PROPIETARIO / FILTRADO POR ROL

**Sin validar:**
- ¿Arrendador solo ve sus inmuebles?
- ¿Arrendatario solo ve sus solicitudes?
- ¿Se ejecutan filtros por usuario?

**Importancia:** CRÍTICA (security issue potencial)

### 8. CITA / VISITA

**Sin validar:**
- ¿Existe flujo de cita/visita?
- ¿Endpoints funcionan?

**Importancia:** IMPORTANTE (flujo core del negocio)

---

## 📊 MATRIZ DE ESTADO

| Flujo | Status | Validación | Bloqueador | P# |
|---|---|---|---|---|
| Login | ✅ | COMPLETA | Ninguno | - |
| Exploración | ✅ | COMPLETA | Ninguno | - |
| Solicitud + Auditoría | ✅ | COMPLETA | Ninguno | - |
| SMTP/Email | 🟡 | INCOMPLETA | Credencial | P0 |
| Activación | 🟡 | INCOMPLETA | Email | P0 |
| Registro+Activación | 🟡 | INCOMPLETA | Email | P0 |
| Password Reset | 🟡 | INCOMPLETA | Email | P0 |
| Propietario/Rol | ❓ | NO HECHA | - | P1 |
| Cita/Visita | ❓ | NO HECHA | - | P2 |

---

## 🔐 CREDENCIALES Y CONFIGURACIÓN

### Archivo `.env` (Local, NO en Git)

Ubicación: `/home/ing-hernan-torres/Escritorio/roomRent/.env`

```bash
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=bd567358546@gmail.com
MAIL_PASSWORD=??????????  ← REQUIERE APP PASSWORD REAL
MAIL_FROM=noreply-room@gmail.com
LOGGING_LEVEL_SPRING_MAIL=DEBUG
LOGGING_LEVEL_COM_ROOMRENT_SERVICE=DEBUG
```

### Usuarios de Prueba Predefinidos

| Login | Password | Rol | Status |
|---|---|---|---|
| admin | admin | ADMIN | Activo |
| user | user | USER | (si existe) |

### Cómo Obtener App Password Real

```
1. Ir a: https://myaccount.google.com/apppasswords
2. Iniciar sesión con cuenta Gmail (bd567358546@gmail.com)
3. Seleccionar: Mail > Windows Computer (o tu OS)
4. Google genera 16 caracteres automáticos
5. Copiar: xxxx-xxxx-xxxx-xxxx
6. Guardar en .env como: MAIL_PASSWORD=xxxx-xxxx-xxxx-xxxx
7. Reiniciar app: ./run-dev.sh
8. Validar: Ver que email llega en bandeja real
```

---

## 🚀 PRIORIDADES PARA PRÓXIMAS MEJORAS

### P0 — CRÍTICO (Bloqueador Principal)

**Objetivo:** Validar flujo completo de usuario nuevo: Registro → Email → Activación → Login

**Activador:** App Password real de Google

**Checklist:**
- [ ] Obtener App Password real
- [ ] Actualizar `.env` con credencial
- [ ] Reiniciar app
- [ ] Crear usuario nuevo
- [ ] Capturar email real en bandeja
- [ ] Abrir link de activación
- [ ] Login con usuario nuevo
- [ ] Documentar evidencia real

**Resultado esperado:** Email funciona, usuarios nuevos pueden activarse

### P1 — IMPORTANTE (Después de P0)

1. **Password Reset Real**
   - [ ] Validar flujo completo con email
   - [ ] Login con nueva contraseña

2. **Validar Propietario/Filtrado por Rol**
   - [ ] Arrendador solo ve sus inmuebles
   - [ ] Arrendatario solo ve sus solicitudes
   - [ ] Verificar que no hay data leaks

3. **Revalidar Solicitudes + Auditoría**
   - [ ] Verificar persistencia de auditoría
   - [ ] Validar que otro rol puede ver solicitud

### P2 — MENOR (Sprint Siguiente)

1. **Cita / Visita**
   - [ ] ¿Existe flujo?
   - [ ] Si sí: validar E2E
   - [ ] Si no: descartar o implementar

---

## 📁 ARCHIVOS CLAVE

### Configuración
- `application-dev.yml` — SMTP con variables de entorno
- `.env.example` — Template (committeado)
- `.env` — Credenciales locales (en .gitignore, NO committeado)
- `run-dev.sh` — Startup script (carga .env automáticamente)

### Documentación
- `INFORME_FINAL_ESTADO_PRODUCTO.md` — Estado actual completo
- `SMTP_CONFIGURATION.md` — Guía de setup SMTP
- `CONTEXTO.md` — ESTE ARCHIVO (línea base para futuras mejoras)

### Código
- `SolicitudArriendo.java` — Auditoría implementada
- `MailService.java` — Flujos de email (sendCreationEmail, sendPasswordResetMail, etc.)
- `AccountResource.java` — Endpoints de registro, activación, password reset
- `SecurityConfiguration.java` — Endpoints públicos vs autenticados

---

## 🔄 CÓMO CONTINUAR EN FUTURAS SESIONES

### Para próximo desarrollo:

1. **Leer este archivo primero** — Entiende qué funciona y qué no
2. **Revisar INFORME_FINAL_ESTADO_PRODUCTO.md** — Detalles de instalación y validación
3. **Ejecutar:**
   ```bash
   cd ~/Escritorio/roomRent
   ./run-dev.sh
   ```
4. **Validar estado actual:**
   ```bash
   # Login
   curl -X POST http://localhost:8080/api/authenticate \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"admin"}'
   
   # Exploración
   curl http://localhost:8080/api/publicacion-inmuebles
   ```

5. **Proceder con P0 si tienes App Password**
   - Actualiza `.env`
   - Reinicia app
   - Ejecuta flujo de registro → email → activación → login

---

## ⚠️ PUNTOS CRÍTICOS A RECORDAR

### Seguridad
- ✅ `.env` está en `.gitignore` (no commitear credenciales)
- ✅ SMTP usa STARTTLS (conexión segura)
- ✅ Auditoría está en place (createdBy, createdDate)
- ⚠️ Propietario/filtrado NO está validado (security risk potencial)

### Funcionamiento
- ✅ Login funciona sin dependencias
- ✅ Exploración funciona sin dependencias
- ✅ Solicitud funciona sin dependencias
- 🟡 Email es BLOQUEADOR para activación y password reset
- ❓ Propietario/rol NO está validado

### Próximos Pasos
- P0 DEBE completarse antes de P1
- Email es la roca bloqueadora para MVP
- Propietario/filtrado es security-critical para P1

---

## 📞 CONTACTO Y REFERENCIAS

- **GitHub:** https://github.com/Jose-Bohorquez/roomRent
- **Branch:** main
- **App:** http://localhost:8080 (cuando está corriendo)
- **MongoDB:** `mongodb://localhost:27017/room`

---

**Este documento debe actualizarse después de cada fase (P0, P1, P2) para mantener la línea base clara.**
