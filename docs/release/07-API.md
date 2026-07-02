# 07 — Referencia de API

**Versión:** RC1
**Fecha:** 2026-07-02
**Base URL:** `https://room-rent.xyz`
**Proyecto:** RoomRent — room-rent.xyz

---

## 1. Autenticación

La API utiliza **JWT Bearer Token**. Para obtener un token, usar el endpoint `POST /api/authenticate`. El token debe enviarse en el header `Authorization: Bearer <token>` en todas las peticiones protegidas.

**Duración del token:** 86400 segundos (24 horas) para sesión normal; 2592000 segundos (30 días) con `rememberMe: true`.

---

## 2. Tabla Completa de Endpoints

### 2.1 Endpoints Públicos (`permitAll` — sin autenticación)

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/authenticate` | Obtener token JWT (login) |
| `POST` | `/api/register` | Registrar nueva cuenta de usuario |
| `GET` | `/api/activate` | Activar cuenta vía token de email |
| `POST` | `/api/account/reset-password/init` | Solicitar reset de contraseña |
| `POST` | `/api/account/reset-password/finish` | Completar reset de contraseña |
| `GET` | `/api/inmuebles/**` | Listar / obtener inmuebles (lectura pública) |
| `GET` | `/api/publicacion-inmuebles/**` | Listar / obtener publicaciones de arriendo |
| `GET` | `/api/multimedia-inmuebles/**` | Listar / obtener multimedia de inmuebles |
| `GET` | `/uploads/**` | Servir archivos subidos (imágenes/videos) |
| `GET` | `/management/health` | Health check del servidor |
| `GET` | `/management/health/**` | Health check detallado |

---

### 2.2 Endpoints Autenticados (cualquier rol — `ROLE_USER`, `ROLE_ARRENDADOR`, `ROLE_ADMIN`)

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/account` | Obtener datos de la cuenta propia |
| `POST` | `/api/account` | Actualizar datos de la cuenta propia |
| `POST` | `/api/account/change-password` | Cambiar contraseña |
| `GET` | `/api/perfil-usuarios` | Listar perfiles de usuario |
| `GET` | `/api/perfil-usuarios/{id}` | Obtener perfil por ID |
| `POST` | `/api/perfil-usuarios` | Crear perfil de usuario |
| `PUT` | `/api/perfil-usuarios/{id}` | Actualizar perfil completo |
| `PATCH` | `/api/perfil-usuarios/{id}` | Actualizar perfil parcial |
| `GET` | `/api/solicitud-arriendos` | Listar solicitudes de arriendo propias |
| `GET` | `/api/solicitud-arriendos/{id}` | Obtener solicitud por ID |
| `POST` | `/api/solicitud-arriendos` | Crear solicitud de arriendo |
| `PATCH` | `/api/solicitud-arriendos/{id}` | Actualizar estado de solicitud |
| `GET` | `/api/visita-programadas` | Listar visitas programadas |
| `GET` | `/api/visita-programadas/{id}` | Obtener visita por ID |
| `POST` | `/api/visita-programadas` | Programar visita |
| `PATCH` | `/api/visita-programadas/{id}` | Actualizar visita |
| `GET` | `/api/contrato-arriendos` | Listar contratos |
| `GET` | `/api/contrato-arriendos/{id}` | Obtener contrato por ID |
| `GET` | `/api/publicacion-roomies` | Listar publicaciones roomie |
| `GET` | `/api/publicacion-roomies/{id}` | Obtener publicación roomie por ID |
| `POST` | `/api/publicacion-roomies` | Crear publicación roomie |
| `PUT` | `/api/publicacion-roomies/{id}` | Actualizar publicación roomie |
| `DELETE` | `/api/publicacion-roomies/{id}` | Eliminar publicación roomie propia |
| `GET` | `/api/solicitud-roomies` | Listar solicitudes roomie |
| `POST` | `/api/solicitud-roomies` | Crear solicitud roomie |
| `GET` | `/api/calificacions` | Listar calificaciones |
| `POST` | `/api/calificacions` | Crear calificación |
| `GET` | `/api/documento-usuarios` | Ver documentos de identidad propios |
| `POST` | `/api/documento-usuarios` | Subir documento de identidad |

---

### 2.3 Endpoints ARRENDADOR y ADMIN (`ROLE_ARRENDADOR` o `ROLE_ADMIN`)

| Método | Ruta | Rol mínimo | Descripción |
|---|---|---|---|
| `POST` | `/api/inmuebles` | ARRENDADOR | Crear inmueble |
| `PUT` | `/api/inmuebles/{id}` | ARRENDADOR | Actualizar inmueble completo |
| `PATCH` | `/api/inmuebles/{id}` | ARRENDADOR | Actualizar inmueble parcial |
| `DELETE` | `/api/inmuebles/{id}` | ARRENDADOR | Eliminar inmueble |
| `POST` | `/api/publicacion-inmuebles` | ARRENDADOR | Crear publicación de arriendo |
| `PUT` | `/api/publicacion-inmuebles/{id}` | ARRENDADOR | Actualizar publicación |
| `PATCH` | `/api/publicacion-inmuebles/{id}` | ARRENDADOR | Actualizar publicación parcial |
| `DELETE` | `/api/publicacion-inmuebles/{id}` | ARRENDADOR | Eliminar publicación |
| `POST` | `/api/multimedia-inmuebles` | ARRENDADOR | Registrar multimedia (metadata) |
| `PUT` | `/api/multimedia-inmuebles/{id}` | ARRENDADOR | Actualizar multimedia |
| `DELETE` | `/api/multimedia-inmuebles/{id}` | ARRENDADOR | Eliminar registro multimedia |
| `POST` | `/api/uploads/multimedia` | ARRENDADOR | Subir archivo físico al servidor |
| `POST` | `/api/contrato-arriendos` | ARRENDADOR | Crear contrato de arriendo |
| `PUT` | `/api/contrato-arriendos/{id}` | ARRENDADOR | Actualizar contrato |

---

### 2.4 Endpoints ADMIN exclusivos (`ROLE_ADMIN`)

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/admin/users` | Listar todos los usuarios |
| `POST` | `/api/admin/users` | Crear usuario |
| `PUT` | `/api/admin/users` | Actualizar usuario |
| `DELETE` | `/api/admin/users/{login}` | Eliminar usuario |
| `GET` | `/api/admin/users/{login}` | Obtener usuario por login |
| `GET` | `/api/authorities` | Listar todos los roles disponibles |
| `GET` | `/v3/api-docs` | Especificación OpenAPI 3.0 (JSON) |
| `GET` | `/v3/api-docs.yaml` | Especificación OpenAPI 3.0 (YAML) |
| `GET` | `/swagger-ui/**` | Interfaz Swagger UI |

---

### 2.5 Endpoints `/management/**`

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `GET` | `/management/health` | Público | Estado general (`UP`/`DOWN`) |
| `GET` | `/management/health/**` | Público | Estado por componente |
| `GET` | `/management/info` | ADMIN | Información de la app |
| `GET` | `/management/configprops` | ADMIN | Propiedades de configuración |
| `GET` | `/management/env` | ADMIN | Variables de entorno |
| `GET` | `/management/loggers` | ADMIN | Configuración de loggers |
| `POST` | `/management/loggers/{name}` | ADMIN | Cambiar nivel de un logger en vivo |
| `GET` | `/management/metrics` | ADMIN | Métricas de la aplicación (Micrometer) |
| `GET` | `/management/threaddump` | ADMIN | Thread dump de la JVM |
| `GET` | `/management/heapdump` | ADMIN | Heap dump de la JVM |

---

## 3. Parámetros de Paginación

Los endpoints de listado soportan paginación JHipster estándar:

| Parámetro | Tipo | Defecto | Descripción |
|---|---|---|---|
| `page` | Integer | `0` | Número de página (base 0) |
| `size` | Integer | `20` | Registros por página |
| `sort` | String | — | Campo y dirección: `campo,asc` o `campo,desc` |

La respuesta incluye el header `X-Total-Count` con el total de registros.

---

## 4. Ejemplos cURL

### 4.1 Login — Obtener JWT

```bash
curl -X POST https://room-rent.xyz/api/authenticate \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin",
    "rememberMe": false
  }'
```

**Respuesta exitosa (200 OK):**

```json
{
  "id_token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOIiwiZXhwIjoxNzUxNTI..."
}
```

---

### 4.2 Obtener Cuenta Propia

```bash
TOKEN="eyJhbGciOiJIUzUxMiJ9..."

curl -X GET https://room-rent.xyz/api/account \
  -H "Authorization: Bearer $TOKEN"
```

**Respuesta exitosa (200 OK):**

```json
{
  "id": "6847a2b3c1d4e5f6a7b8c9d0",
  "login": "admin",
  "firstName": "Administrator",
  "lastName": "System",
  "email": "admin@room-rent.xyz",
  "imageUrl": null,
  "activated": true,
  "langKey": "es",
  "authorities": ["ROLE_ADMIN", "ROLE_USER"]
}
```

---

### 4.3 Listar Publicaciones (público, con paginación)

```bash
curl -X GET "https://room-rent.xyz/api/publicacion-inmuebles?page=0&size=10&sort=id,desc"
```

**Respuesta exitosa (200 OK):**

```json
[
  {
    "id": "6847a2b3c1d4e5f6a7b8c9d1",
    "titulo": "Habitación amplia en Chapinero",
    "canonArriendo": 750000,
    "estado": "DISPONIBLE",
    "permiteRoomies": true,
    "aceptaMascotas": false,
    "permiteFumadores": false,
    "permiteNinos": true,
    "permiteVisitas": true,
    "permiteParejas": false,
    "inmueble": {
      "id": "6847a2b3c1d4e5f6a7b8c9d2",
      "nombre": "Casa Chapinero Centro",
      "ciudad": "Bogotá",
      "barrio": "Chapinero"
    }
  }
]
```

Header de respuesta: `X-Total-Count: 47`

---

### 4.4 Crear Inmueble (requiere ROLE_ARRENDADOR)

```bash
TOKEN="eyJhbGciOiJIUzUxMiJ9..."

curl -X POST https://room-rent.xyz/api/inmuebles \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Apartamento Zona Rosa",
    "direccion": "Cra 13 # 85-32 Apto 401",
    "ciudad": "Bogotá",
    "barrio": "Zona Rosa",
    "tipoInmueble": "APARTAMENTO",
    "numeroHabitaciones": 2,
    "numeroBanos": 1,
    "estrato": 4,
    "areaM2": 65.5,
    "descripcion": "Apartamento moderno con vista a la ciudad"
  }'
```

**Respuesta exitosa (201 Created):**

```json
{
  "id": "6847a2b3c1d4e5f6a7b8c9d3",
  "nombre": "Apartamento Zona Rosa",
  "direccion": "Cra 13 # 85-32 Apto 401",
  "ciudad": "Bogotá",
  "barrio": "Zona Rosa",
  "tipoInmueble": "APARTAMENTO",
  "numeroHabitaciones": 2,
  "numeroBanos": 1,
  "estrato": 4,
  "areaM2": 65.5
}
```

---

### 4.5 Subir Imagen de Inmueble (requiere ROLE_ARRENDADOR)

```bash
TOKEN="eyJhbGciOiJIUzUxMiJ9..."
INMUEBLE_ID="6847a2b3c1d4e5f6a7b8c9d3"

# Paso 1: subir el archivo físico
curl -X POST "https://room-rent.xyz/api/uploads/multimedia?inmuebleId=${INMUEBLE_ID}" \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/ruta/local/foto-sala.jpg"
```

**Respuesta (200 OK):**

```json
{
  "id": "6847a2b3c1d4e5f6a7b8c9d4",
  "urlMedia": "/uploads/multimedia/6847a2b3c1d4e5f6a7b8c9d3/foto-sala.jpg",
  "tipoMedia": "IMAGEN",
  "principal": false,
  "inmueble": { "id": "6847a2b3c1d4e5f6a7b8c9d3" }
}
```

---

### 4.6 Health Check

```bash
curl -sf https://room-rent.xyz/management/health | jq .
```

**Respuesta (200 OK — sistema sano):**

```json
{
  "status": "UP",
  "components": {
    "diskSpace": { "status": "UP" },
    "mongo": { "status": "UP", "details": { "version": "7.0.x" } },
    "ping": { "status": "UP" }
  }
}
```

---

### 4.7 Registro de Nueva Cuenta

```bash
curl -X POST https://room-rent.xyz/api/register \
  -H "Content-Type: application/json" \
  -d '{
    "login": "nuevousuario",
    "email": "usuario@ejemplo.com",
    "password": "MiPassword123!",
    "langKey": "es"
  }'
```

**Respuesta exitosa:** `201 Created` (sin cuerpo). Se envía email de activación.

---

### 4.8 Crear Solicitud de Arriendo

```bash
TOKEN="eyJhbGciOiJIUzUxMiJ9..."

curl -X POST https://room-rent.xyz/api/solicitud-arriendos \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "mensaje": "Estoy interesado en arrendar este inmueble. Trabajo desde casa y soy muy organizado.",
    "publicacion": { "id": "6847a2b3c1d4e5f6a7b8c9d1" }
  }'
```

**Respuesta exitosa (201 Created):**

```json
{
  "id": "6847a2b3c1d4e5f6a7b8c9d5",
  "estado": "PENDIENTE",
  "fechaSolicitud": "2026-07-02T15:30:00Z",
  "mensaje": "Estoy interesado en arrendar este inmueble...",
  "publicacion": { "id": "6847a2b3c1d4e5f6a7b8c9d1" }
}
```

---

## 5. Códigos de Error Estándar

| Código HTTP | Significado | Causa frecuente |
|---|---|---|
| `200 OK` | Éxito | GET, PUT, PATCH exitosos |
| `201 Created` | Recurso creado | POST exitoso |
| `204 No Content` | Sin contenido | DELETE exitoso |
| `400 Bad Request` | Datos inválidos | Campos requeridos faltantes o formato incorrecto |
| `401 Unauthorized` | No autenticado | Token ausente, expirado o inválido |
| `403 Forbidden` | Sin permiso | Rol insuficiente para la operación |
| `404 Not Found` | No encontrado | ID inexistente |
| `422 Unprocessable Entity` | Error de validación | Violación de restricciones de negocio |
| `500 Internal Server Error` | Error del servidor | Ver logs: `docker compose logs app` |

**Formato de error (RFC 7807):**

```json
{
  "type": "https://www.jhipster.tech/problem/constraint-violation",
  "title": "Method argument not valid",
  "status": 400,
  "path": "/api/inmuebles",
  "message": "error.validation",
  "fieldErrors": [
    {
      "objectName": "inmueble",
      "field": "ciudad",
      "message": "must not be blank"
    }
  ]
}
```
