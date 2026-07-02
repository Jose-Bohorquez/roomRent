# 08 — Usuarios y Roles

**Proyecto:** RoomRent  
**Versión:** RC1  
**Fecha:** 2026-07-02  

---

## 1. Roles del Sistema

RoomRent define cuatro roles gestionados por Spring Security. Los roles se acumulan (un arrendador también tiene ROLE_USER).

| Rol                | Descripción                                      | Permisos principales                                                                                   |
|--------------------|--------------------------------------------------|--------------------------------------------------------------------------------------------------------|
| `ROLE_ADMIN`       | Administrador del sistema                        | Acceso total: CRUD usuarios, inmuebles, publicaciones, multimedia y toda la API `/api/admin/**`        |
| `ROLE_ARRENDADOR`  | Propietario o gestor de inmuebles                | Crear, editar y eliminar sus propios inmuebles; subir y vincular multimedia; crear publicaciones       |
| `ROLE_ARRENDATARIO`| Persona que busca inmueble en arriendo           | Consultar listado de publicaciones e inmueble detalle; solicitar arriendo                              |
| `ROLE_USER`        | Rol base asignado a todo usuario registrado      | Acceso a endpoints autenticados básicos (`/api/account`, `/api/authenticate`)                         |

> **Nota:** Todo usuario creado por registro propio recibe `ROLE_USER` de forma automática. Los roles adicionales (`ROLE_ARRENDADOR`, `ROLE_ARRENDATARIO`) se asignan manualmente desde el panel de administración.

---

## 2. Flujo de Registro y Activación de Cuenta

```
Cliente                        API (JHipster)               SMTP (Gmail)
  |                                |                              |
  |--- POST /api/register -------->|                              |
  |    { login, email, password,   |                              |
  |      langKey }                 |                              |
  |                                |--- Genera activationKey ---->|
  |                                |    (UUID aleatorio, 20 chars)|
  |                                |--- Envía email con link ---->|
  |                                |    /portal/#/activate?key=xx |
  |<--- 201 Created ---------------|                              |
  |                                |                              |
  |    [Usuario abre email]        |                              |
  |                                |                              |
  |--- GET /api/activate?key=xxx ->|                              |
  |                                |--- activated=true ---------->|
  |<--- 200 OK --------------------|    activationKey=null        |
```

**Campos requeridos en `POST /api/register`:**

```json
{
  "login": "maria.lopez",
  "email": "maria@ejemplo.co",
  "password": "contraseña123",
  "langKey": "es"
}
```

**Validaciones:**
- `login`: 1–50 caracteres, solo letras/números/guión/punto/arroba
- `email`: formato email válido, máx. 254 caracteres
- `password`: mínimo 4 caracteres, máximo 100 caracteres
- El `login` y el `email` deben ser únicos en la base de datos

**Respuesta exitosa:** `HTTP 201 Created` (sin cuerpo)  
**Email ya registrado:** `HTTP 400 Bad Request` con `{"type": "LOGIN_ALREADY_USED"}`

---

## 3. Flujo de Login (Autenticación JWT)

```
Cliente                        API (JHipster)
  |                                |
  |--- POST /api/authenticate ---->|
  |    { username, password,       |
  |      rememberMe }              |
  |                                |--- Valida credenciales ------>
  |                                |--- Genera JWT HS512 --------->
  |<--- 200 OK --------------------|
  |    { id_token: "eyJ..." }      |
  |                                |
  |    localStorage.setItem(       |
  |      'authenticationToken',    |
  |      id_token)                 |
```

**Cuerpo de la petición:**

```json
{
  "username": "maria.lopez",
  "password": "contraseña123",
  "rememberMe": false
}
```

**Respuesta exitosa:**

```json
{
  "id_token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtYXJpYS5sb3BleiIsImF1dGgiOiJST0xFX0FSUkVOREFET1IgUk9MRV9VU0VSIiwiZXhwIjoxNzUxNjAwMDAwfQ.xxxx"
}
```

**Errores:**
- `HTTP 401 Unauthorized` — credenciales incorrectas o cuenta no activada

El token se almacena en `localStorage` bajo la clave `authenticationToken` y se envía en cada petición como:

```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

---

## 4. Estructura del Token JWT

El payload decodificado contiene:

```json
{
  "sub": "maria.lopez",
  "auth": "ROLE_ARRENDADOR ROLE_USER",
  "exp": 1751600000
}
```

> **Importante:** JHipster almacena los roles en el claim `"auth"` (no `"roles"` ni `"authorities"`), separados por espacio. El portal React los parsea con `split(' ')` al leer el token.

---

## 5. Flujo de Restablecimiento de Contraseña

```
Paso 1 — Solicitar reset:

  Cliente                        API                          SMTP
    |                             |                              |
    |--- POST /api/account/ ----->|                              |
    |    reset-password/init      |                              |
    |    Body: "maria@ejemplo.co" |                              |
    |    (text/plain)             |--- Genera resetKey ----------|
    |                             |    (UUID, expira en 24h)     |
    |                             |--- Envía email con link ---->|
    |<--- 200 OK -----------------|    /portal/#/reset/finish    |
    |                             |    ?key=xxx                  |

Paso 2 — Confirmar nueva contraseña:

  Cliente                        API
    |                             |
    |--- POST /api/account/ ----->|
    |    reset-password/finish    |
    |    { key: "xxx",            |
    |      newPassword: "nueva" } |
    |                             |--- resetKey válido y vigente?
    |                             |--- BCrypt nueva contraseña --
    |                             |--- resetKey=null -----------
    |<--- 200 OK -----------------|
```

**Notas:**
- El `resetKey` expira exactamente 24 horas después de la solicitud
- Si el `resetKey` no existe o ya expiró: `HTTP 500` (JHipster default)
- La nueva contraseña debe cumplir las mismas validaciones que el registro (mín. 4 chars)

---

## 6. Asignación de Roles (Panel Admin)

Los roles se gestionan desde el panel Angular (accesible solo para `ROLE_ADMIN`):

**Interfaz web:** `http://localhost:8080/#/admin/user-management`

**Endpoint API:**

```http
PUT /api/admin/users
Authorization: Bearer {token_admin}
Content-Type: application/json

{
  "id": "64a1b2c3d4e5f6a7b8c9d0e1",
  "login": "maria.lopez",
  "email": "maria@ejemplo.co",
  "authorities": ["ROLE_ARRENDADOR", "ROLE_USER"],
  "activated": true,
  "langKey": "es"
}
```

**Listar usuarios:**

```http
GET /api/admin/users?page=0&size=20&sort=id,asc
Authorization: Bearer {token_admin}
```

**Ver usuario específico:**

```http
GET /api/admin/users/{login}
Authorization: Bearer {token_admin}
```

---

## 7. Política de Contraseñas

| Parámetro              | Valor                          |
|------------------------|--------------------------------|
| Longitud mínima        | 4 caracteres                   |
| Longitud máxima        | 100 caracteres                 |
| Caracteres requeridos  | Ninguno (JHipster default)     |
| Algoritmo de hash      | BCrypt                         |
| Cost factor (rounds)   | 10                             |
| Almacenamiento         | MongoDB campo `password_hash`  |

> **Recomendación para producción:** Aumentar la longitud mínima a 8 caracteres modificando las anotaciones `@Size` en `ManagedUserVM.java` y las validaciones del portal.

---

## 8. Gestión de Sesión y Expiración

| Parámetro                  | Valor              | Duración         |
|----------------------------|--------------------|------------------|
| Validez token normal       | 86 400 segundos    | 24 horas         |
| Validez token remember-me  | 2 592 000 segundos | 30 días          |
| Algoritmo firma            | HS512              | —                |
| Almacenamiento cliente     | `localStorage`     | —                |
| Renovación automática      | No implementada    | —                |

Cuando el token expira, el portal React recibe `HTTP 401` en cualquier llamada autenticada. El `AuthContext` captura este evento, elimina el token de `localStorage` y redirige al usuario a `/portal/#/login`.

---

## 9. Configuración en `application.yml`

```yaml
jhipster:
  security:
    authentication:
      jwt:
        base64-secret: <SECRET_BASE64_HS512>
        token-validity-in-seconds: 86400
        token-validity-in-seconds-for-remember-me: 2592000
```

El secreto debe ser una cadena Base64 de al menos 512 bits (64 bytes). En producción se establece mediante la variable de entorno `JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET`.
