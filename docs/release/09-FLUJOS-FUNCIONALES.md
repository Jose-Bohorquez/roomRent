# 09 — Flujos Funcionales

**Proyecto:** RoomRent  
**Versión:** RC1  
**Fecha:** 2026-07-02  

---

## Convenciones de este documento

- Todos los endpoints son relativos a la base URL del servidor (ej. `http://localhost:8080`)
- El header `Authorization: Bearer {token}` es obligatorio en endpoints protegidos
- Los cuerpos JSON usan `Content-Type: application/json` salvo que se indique lo contrario
- Los IDs de MongoDB son strings de 24 caracteres hexadecimales

---

## Flujo 1 — Arrendador: Publicar un Inmueble

Este flujo cubre el recorrido completo desde el login hasta tener un inmueble publicado y visible para arrendatarios.

### Paso 1.1 — Autenticación

```http
POST /api/authenticate
Content-Type: application/json

{
  "username": "carlos.arrendador",
  "password": "mi_password",
  "rememberMe": false
}
```

**Respuesta exitosa (`200 OK`):**

```json
{
  "id_token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjYXJsb3MuYXJyZW5kYWRvciIsImF1dGgiOiJST0xFX0FSUkVOREFET1IgUk9MRV9VU0VSIn0.xxx"
}
```

Almacenar el token: `localStorage.setItem('authenticationToken', id_token)`

---

### Paso 1.2 — Crear el Inmueble

```http
POST /api/inmuebles
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "Apartaestudio Centro",
  "tipoInmueble": "APARTAMENTO",
  "ciudad": "Bogotá",
  "barrio": "La Candelaria",
  "direccion": "Cra 7 # 12-34 Apto 301",
  "numeroHabitaciones": 1,
  "numeroBanos": 1
}
```

**Respuesta exitosa (`201 Created`):**

```json
{
  "id": "64a1b2c3d4e5f6a7b8c9d0e1",
  "nombre": "Apartaestudio Centro",
  "tipoInmueble": "APARTAMENTO",
  "ciudad": "Bogotá",
  "barrio": "La Candelaria",
  "direccion": "Cra 7 # 12-34 Apto 301",
  "numeroHabitaciones": 1,
  "numeroBanos": 1
}
```

Guardar el `id` del inmueble para los siguientes pasos.

---

### Paso 1.3 — Subir Fotografías

Cada fotografía se sube de forma independiente como `multipart/form-data`.

```http
POST /api/uploads/multimedia
Authorization: Bearer {token}
Content-Type: multipart/form-data

file=@/ruta/local/foto1.jpg
```

**Respuesta exitosa (`200 OK`):**

```json
{
  "url": "https://storage.roomrent.co/multimedia/2026/07/foto1_abc123.jpg",
  "mimeType": "image/jpeg",
  "tamanioBytes": 245760
}
```

Repetir para cada fotografía. Guardar la `url` de cada respuesta.

---

### Paso 1.4 — Vincular Fotografías al Inmueble

Por cada foto subida, crear el registro de asociación:

```http
POST /api/multimedia-inmuebles
Authorization: Bearer {token}
Content-Type: application/json

{
  "urlMedia": "https://storage.roomrent.co/multimedia/2026/07/foto1_abc123.jpg",
  "tipoMedia": "IMAGEN",
  "principal": true,
  "inmueble": {
    "id": "64a1b2c3d4e5f6a7b8c9d0e1"
  }
}
```

> **Nota:** Solo una fotografía debe tener `"principal": true`. Las siguientes fotos van con `"principal": false`.

**Respuesta exitosa (`201 Created`):**

```json
{
  "id": "64a1b2c3d4e5f6a7b8c9d0e2",
  "urlMedia": "https://storage.roomrent.co/multimedia/2026/07/foto1_abc123.jpg",
  "tipoMedia": "IMAGEN",
  "principal": true,
  "inmueble": { "id": "64a1b2c3d4e5f6a7b8c9d0e1" }
}
```

---

### Paso 1.5 — Crear la Publicación

```http
POST /api/publicacion-inmuebles
Authorization: Bearer {token}
Content-Type: application/json

{
  "titulo": "Apartaestudio amoblado en La Candelaria",
  "canonArriendo": 950000,
  "estado": "ACTIVO",
  "permiteAnimales": false,
  "permiteFumar": false,
  "permiteNinos": true,
  "inmueble": {
    "id": "64a1b2c3d4e5f6a7b8c9d0e1"
  }
}
```

**Respuesta exitosa (`201 Created`):**

```json
{
  "id": "64a1b2c3d4e5f6a7b8c9d0e3",
  "titulo": "Apartaestudio amoblado en La Candelaria",
  "canonArriendo": 950000,
  "estado": "ACTIVO",
  "permiteAnimales": false,
  "permiteFumar": false,
  "permiteNinos": true,
  "inmueble": { "id": "64a1b2c3d4e5f6a7b8c9d0e1" }
}
```

La publicación queda visible inmediatamente en el listado público.

---

## Flujo 2 — Arrendatario: Buscar y Ver Inmuebles

Este flujo no requiere autenticación para el listado y detalle básico.

### Paso 2.1 — Ver Listado de Publicaciones

```http
GET /api/publicacion-inmuebles?page=0&size=20&sort=id,desc
```

**Respuesta exitosa (`200 OK`):**

```json
[
  {
    "id": "64a1b2c3d4e5f6a7b8c9d0e3",
    "titulo": "Apartaestudio amoblado en La Candelaria",
    "canonArriendo": 950000,
    "estado": "ACTIVO",
    "permiteAnimales": false,
    "permiteFumar": false,
    "permiteNinos": true,
    "inmueble": {
      "id": "64a1b2c3d4e5f6a7b8c9d0e1",
      "nombre": "Apartaestudio Centro",
      "ciudad": "Bogotá",
      "barrio": "La Candelaria"
    }
  }
]
```

El header de respuesta incluye `X-Total-Count` con el total de registros para paginación.

---

### Paso 2.2 — Ver Detalle del Inmueble

```http
GET /api/publicacion-inmuebles/{id}
```

**Respuesta exitosa (`200 OK`):** objeto completo de la publicación con el inmueble anidado.

---

### Paso 2.3 — Obtener Fotografías del Inmueble

```http
GET /api/multimedia-inmuebles?inmueble.id=64a1b2c3d4e5f6a7b8c9d0e1
```

Este endpoint es **público** (no requiere token). Retorna todas las fotos vinculadas al inmueble.

**Respuesta exitosa (`200 OK`):**

```json
[
  {
    "id": "64a1b2c3d4e5f6a7b8c9d0e2",
    "urlMedia": "https://storage.roomrent.co/multimedia/2026/07/foto1_abc123.jpg",
    "tipoMedia": "IMAGEN",
    "principal": true,
    "inmueble": { "id": "64a1b2c3d4e5f6a7b8c9d0e1" }
  }
]
```

El portal React ordena las fotos poniendo `principal: true` al inicio del carrusel.

---

### Paso 2.4 — Solicitar Arriendo (requiere autenticación)

El arrendatario debe estar autenticado (ver Flujo 1, Paso 1.1). La solicitud se realiza desde la vista de detalle del inmueble en el portal.

```http
POST /api/authenticate
Content-Type: application/json

{
  "username": "pedro.arrendatario",
  "password": "mi_password",
  "rememberMe": false
}
```

Una vez autenticado, el arrendatario puede interactuar con los endpoints protegidos de solicitudes.

---

## Flujo 3 — Administrador: Gestión de Usuarios y Roles

El panel de administración Angular solo es accesible con `ROLE_ADMIN`.

### Paso 3.1 — Login como Administrador

Las credenciales por defecto en entorno de desarrollo son:

| Campo    | Valor   |
|----------|---------|
| Usuario  | `admin` |
| Password | `admin` |

```http
POST /api/authenticate
Content-Type: application/json

{
  "username": "admin",
  "password": "admin",
  "rememberMe": false
}
```

> **Importante:** Cambiar las credenciales de `admin` inmediatamente en entornos de producción.

---

### Paso 3.2 — Acceder al Panel Angular

Navegar a: `http://localhost:8080`

El panel Angular (JHipster) está disponible en la raíz `/`. El menú de administración aparece solo para usuarios con `ROLE_ADMIN`.

Rutas disponibles:

| Ruta                            | Función                              |
|---------------------------------|--------------------------------------|
| `/#/admin/user-management`      | Listar, crear, editar y eliminar usuarios |
| `/#/admin/user-management/new`  | Crear nuevo usuario                  |
| `/#/admin/user-management/{login}/edit` | Editar usuario y asignar roles |

---

### Paso 3.3 — Listar Usuarios vía API

```http
GET /api/admin/users?page=0&size=20&sort=id,asc
Authorization: Bearer {token_admin}
```

**Respuesta (`200 OK`):** array de objetos usuario con sus authorities.

---

### Paso 3.4 — Asignar Roles a un Usuario

```http
PUT /api/admin/users
Authorization: Bearer {token_admin}
Content-Type: application/json

{
  "id": "64a1b2c3d4e5f6a7b8c9d0e4",
  "login": "maria.lopez",
  "email": "maria@ejemplo.co",
  "firstName": "María",
  "lastName": "López",
  "activated": true,
  "langKey": "es",
  "authorities": ["ROLE_ARRENDADOR", "ROLE_USER"]
}
```

**Respuesta exitosa (`200 OK`):** objeto usuario actualizado.

---

### Paso 3.5 — Activar o Desactivar una Cuenta

Usar el mismo `PUT /api/admin/users` con `"activated": false` para suspender una cuenta. El usuario no podrá autenticarse mientras `activated` sea `false`.

---

## Flujo 4 — Registro de Nuevo Usuario y Activación por Email

### Paso 4.1 — Registro

```http
POST /api/register
Content-Type: application/json

{
  "login": "nuevo.usuario",
  "email": "nuevo@ejemplo.co",
  "password": "password123",
  "langKey": "es"
}
```

**Respuesta exitosa:** `HTTP 201 Created`

---

### Paso 4.2 — Email de Activación

El sistema envía automáticamente un email con el asunto **"[RoomRent] Activación de cuenta"** al correo registrado.

El email contiene un enlace de la forma:

```
http://localhost:8080/portal/#/activate?key=a1b2c3d4e5f6g7h8i9j0
```

La clave de activación (`activationKey`) es un string aleatorio de 20 caracteres.

---

### Paso 4.3 — Activación de la Cuenta

Al hacer clic en el enlace, el portal React extrae el parámetro `key` de la URL y llama:

```http
GET /api/activate?key=a1b2c3d4e5f6g7h8i9j0
```

**Respuesta exitosa (`200 OK`):** La cuenta queda `activated: true`. El usuario puede iniciar sesión.

**Clave inválida o ya usada:** `HTTP 500 Internal Server Error`

---

### Paso 4.4 — Primer Login

Una vez activada la cuenta, el usuario sigue el Flujo 1 (Paso 1.1) para autenticarse y obtener su token JWT.

---

## Resumen de Endpoints por Rol

| Endpoint                                        | ADMIN | ARRENDADOR | ARRENDATARIO | Público |
|-------------------------------------------------|:-----:|:----------:|:------------:|:-------:|
| `POST /api/authenticate`                        | ✓     | ✓          | ✓            | ✓       |
| `POST /api/register`                            | ✓     | ✓          | ✓            | ✓       |
| `GET /api/activate`                             | ✓     | ✓          | ✓            | ✓       |
| `GET /api/publicacion-inmuebles`                | ✓     | ✓          | ✓            | ✓       |
| `GET /api/multimedia-inmuebles`                 | ✓     | ✓          | ✓            | ✓       |
| `POST /api/inmuebles`                           | ✓     | ✓          | —            | —       |
| `PUT /api/inmuebles/{id}`                       | ✓     | ✓ (propio) | —            | —       |
| `DELETE /api/inmuebles/{id}`                    | ✓     | ✓ (propio) | —            | —       |
| `POST /api/uploads/multimedia`                  | ✓     | ✓          | —            | —       |
| `POST /api/multimedia-inmuebles`                | ✓     | ✓          | —            | —       |
| `POST /api/publicacion-inmuebles`               | ✓     | ✓          | —            | —       |
| `GET /api/admin/users`                          | ✓     | —          | —            | —       |
| `PUT /api/admin/users`                          | ✓     | —          | —            | —       |
| `POST /api/account/reset-password/init`         | ✓     | ✓          | ✓            | ✓       |
| `POST /api/account/reset-password/finish`       | ✓     | ✓          | ✓            | ✓       |
