# RoomRent — Arquitectura Técnica

> **Versión:** RC1
> **Fecha:** 2026-07-02
> **Estado:** Release Candidate

---

## Tabla de contenido

1. [Diagrama de arquitectura](#1-diagrama-de-arquitectura)
2. [Stack tecnológico](#2-stack-tecnológico)
3. [Patrón Dual-SPA](#3-patrón-dual-spa)
4. [Flujo de requests](#4-flujo-de-requests)
5. [Estructura de módulos Java](#5-estructura-de-módulos-java)
6. [Modelo de dominio](#6-modelo-de-dominio)
7. [REST Resources](#7-rest-resources)
8. [Seguridad](#8-seguridad)
9. [Migraciones Mongock](#9-migraciones-mongock)

---

## 1. Diagrama de arquitectura

```
                        INTERNET
                           │
                    ┌──────┴──────┐
                    │   BROWSER   │
                    └──────┬──────┘
                           │ HTTPS :443
                    ┌──────┴──────────────────────────────┐
                    │          NGINX (TLS termination)     │
                    │   room-rent.xyz — Let's Encrypt      │
                    │   HTTP/2, gzip, security headers     │
                    │                                      │
                    │  /           → Angular 21 (admin)   │
                    │  /portal/    → React 18 (público)   │
                    │  /api/**     → proxy_pass :8080      │
                    │  /uploads/** → proxy_pass :8080      │
                    │  /management/** → proxy_pass :8080   │
                    └──────┬──────────────────────────────┘
                           │ HTTP :8080 (127.0.0.1 solo)
              ┌────────────┴──────────────────────┐
              │    roomrent-app-1 (Docker)         │
              │    Spring Boot 4.0.6 / JRE 21      │
              │    Usuario no-root: roomrent        │
              │    UID 100 / GID 101               │
              │                                    │
              │  ┌──────────────┐ ┌─────────────┐  │
              │  │  Angular 21  │ │  React 18   │  │
              │  │  (admin SPA) │ │ (portal SPA)│  │
              │  │  /webapp/    │ │  /portal/   │  │
              │  └──────────────┘ └─────────────┘  │
              │                                    │
              │  ┌──────────────────────────────┐  │
              │  │      Spring Security          │  │
              │  │   JWT HS512 — stateless       │  │
              │  └──────────────────────────────┘  │
              │                                    │
              │  ┌──────────────────────────────┐  │
              │  │     Spring Data MongoDB       │  │
              │  │      Mongock migrations       │  │
              │  └───────────┬──────────────────┘  │
              └──────────────┼────────────────────-┘
                             │ MongoDB protocol
              ┌──────────────┴──────────────────────┐
              │    roomrent-mongo-1 (Docker)         │
              │    MongoDB 7 — red interna           │
              │    Volumen: roomrent-mongo-data       │
              └─────────────────────────────────────┘

              Volumen separado: roomrent-uploads-data
              Montado en /app/uploads/ dentro del contenedor
              Expuesto en URL pública /uploads/**
```

---

## 2. Stack tecnológico

### Backend

| Componente | Versión | Rol |
|---|---|---|
| JHipster | 9.1.0 | Generador de proyecto monolito |
| Spring Boot | 4.0.6 | Framework principal |
| Spring Security | 6.x (incluido con Boot 4) | Autenticación y autorización |
| Spring Data MongoDB | 4.x (incluido con Boot 4) | Capa de acceso a datos |
| Mongock | 5.x | Migraciones de base de datos |
| Java | 21 (LTS) | Runtime de la JVM |
| Maven | 3.x | Gestor de construcción |
| JWT (JJWT) | 0.12.x | Tokens de autenticación |

### Frontend

| Componente | Versión | Rol |
|---|---|---|
| Angular | 21 | SPA de administración (ruta `/`) |
| React | 18 | Portal público (ruta `/portal/`) |
| Node.js | 24 | Runtime de compilación (Docker build stage) |
| Vite | 6.x | Bundler del portal React |
| TypeScript | 5.x | Tipado estático en Angular |

### Base de datos

| Componente | Versión | Rol |
|---|---|---|
| MongoDB | 7 | Base de datos principal |
| Motor de almacenamiento | WiredTiger | Motor por defecto de MongoDB 7 |

### Infraestructura

| Componente | Versión / Detalle | Rol |
|---|---|---|
| Docker | 27.x | Contenerización |
| Docker Compose | v2.x | Orquestación local |
| nginx | 1.26.x | Proxy inverso y TLS |
| Let's Encrypt / Certbot | Última estable | Certificados TLS |
| AWS EC2 | t2.micro — us-east-1 | Servidor de producción |
| Ubuntu | 24.04 LTS | Sistema operativo del host |

---

## 3. Patrón Dual-SPA

RoomRent sirve dos aplicaciones Single Page App desde el mismo servidor Spring Boot / nginx. Son builds estáticos compilados e incluidos en el JAR de Spring Boot.

```
room-rent.xyz/              → Angular 21 (panel de administración)
room-rent.xyz/portal/       → React 18 (portal público para usuarios finales)
```

### Angular — Panel de administración (`/`)

- Generado y mantenido por JHipster
- Acceso restringido: requiere autenticación con rol `ROLE_ADMIN`
- Contiene el CRUD completo de todas las entidades del dominio
- Ubicación en el JAR: `src/main/webapp/`
- Ruta nginx: sirve `index.html` del build Angular para cualquier ruta no-API

### React — Portal público (`/portal/`)

- Desarrollado de forma independiente del generador JHipster
- Acceso mixto: páginas públicas sin autenticación + secciones protegidas para arrendadores y arrendatarios
- Interactúa con la misma API REST (`/api/**`) que el panel admin
- Build compilado con Vite y copiado en `src/main/resources/static/portal/`
- Ruta nginx: todas las rutas bajo `/portal/` apuntan a `portal/index.html`

### Configuración nginx de rutas

```nginx
# Panel de administración Angular
location / {
    try_files $uri $uri/ /index.html;
}

# Portal React
location /portal/ {
    try_files $uri $uri/ /portal/index.html;
}

# API REST — proxy al backend
location /api/ {
    proxy_pass http://127.0.0.1:8080;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-Proto $scheme;
}

# Archivos subidos por usuarios
location /uploads/ {
    proxy_pass http://127.0.0.1:8080;
}

# Endpoints de gestión y salud
location /management/ {
    proxy_pass http://127.0.0.1:8080;
}
```

---

## 4. Flujo de requests

### Request de API autenticada (caso típico)

```
Browser
  │
  │  1. GET https://room-rent.xyz/api/publicaciones-inmuebles
  │     Authorization: Bearer <JWT>
  ▼
nginx (:443, TLS)
  │
  │  2. TLS termination — desencripta la request
  │  3. Valida headers de seguridad (HSTS, etc.)
  │  4. proxy_pass http://127.0.0.1:8080/api/publicaciones-inmuebles
  ▼
Spring Boot (:8080)
  │
  │  5. JwtFilter — extrae y valida el JWT del header Authorization
  │  6. Carga el SecurityContext con el usuario y sus roles
  │  7. Spring Security — verifica que la ruta está permitida para el rol
  │  8. DispatcherServlet enruta al REST Resource correspondiente
  ▼
PublicacionInmuebleResource
  │
  │  9. Llama a PublicacionInmuebleService
  ▼
PublicacionInmuebleServiceImpl
  │
  │  10. Aplica lógica de negocio y filtros según el rol del usuario
  │  11. Llama al Repository correspondiente
  ▼
PublicacionInmuebleRepository (Spring Data MongoDB)
  │
  │  12. Ejecuta la query contra MongoDB
  ▼
MongoDB (:27017, red interna Docker)
  │
  │  13. Retorna los documentos de la colección publicacion_inmueble
  ▼
(respuesta sube por el mismo camino)
  │
  │  14. Repository → Service → Resource → JSON response
  │  15. Spring Boot retorna HTTP 200 + JSON
  │  16. nginx reenvía la respuesta al browser
  │  17. Browser procesa el JSON
```

### Request de página estática (Angular o React)

```
Browser
  │
  │  GET https://room-rent.xyz/portal/publicaciones
  ▼
nginx
  │
  │  try_files — no existe el archivo físico
  │  retorna /portal/index.html (React SPA bootstrap)
  ▼
Browser
  │
  │  React Router maneja la ruta /portal/publicaciones
  │  Renderiza el componente correspondiente
  │  Hace fetch() a /api/publicaciones-inmuebles para los datos
```

---

## 5. Estructura de módulos Java

La estructura de paquetes sigue el estándar JHipster para proyectos monolito:

```
com.roomrent.app/
├── config/
│   ├── SecurityConfiguration.java       ← Spring Security, rutas públicas vs protegidas
│   ├── DatabaseConfiguration.java       ← MongoDB, índices, configuración
│   ├── LoggingConfiguration.java
│   └── WebConfigurer.java               ← CORS, archivos estáticos
│
├── security/
│   ├── jwt/
│   │   ├── JwtFilter.java               ← Intercepta cada request HTTP
│   │   ├── TokenProvider.java           ← Crea y valida JWT (HS512)
│   │   └── JwtRelayFilter.java
│   ├── AuthoritiesConstants.java        ← Constantes de roles
│   └── SecurityUtils.java
│
├── domain/                              ← Entidades de dominio (documentos MongoDB)
│   ├── User.java
│   ├── Authority.java
│   ├── PerfilUsuario.java
│   ├── DocumentoUsuario.java
│   ├── Inmueble.java
│   ├── PublicacionInmueble.java
│   ├── MultimediaInmueble.java
│   ├── SolicitudArriendo.java
│   ├── VisitaProgramada.java
│   ├── ContratoArriendo.java
│   ├── PublicacionRoomie.java
│   ├── SolicitudRoomie.java
│   ├── Calificacion.java
│   └── enumeration/                     ← 9 enums de dominio
│
├── repository/                          ← Interfaces Spring Data MongoDB
│   ├── UserRepository.java
│   ├── PerfilUsuarioRepository.java
│   ├── InmuebleRepository.java
│   └── ... (un repository por entidad)
│
├── service/                             ← Interfaces de servicio
│   ├── UserService.java
│   ├── PerfilUsuarioService.java
│   └── ... (un servicio por entidad)
│   └── impl/                           ← Implementaciones de servicios
│       ├── PerfilUsuarioServiceImpl.java
│       └── ... (un ServiceImpl por entidad)
│
├── web/
│   └── rest/                           ← Controllers REST (REST Resources)
│       ├── AccountResource.java         ← /api/account
│       ├── UserResource.java            ← /api/admin/users
│       ├── AuthenticateResource.java    ← /api/authenticate
│       ├── PublicResource.java          ← /api/public/**
│       ├── PerfilUsuarioResource.java
│       └── ... (un Resource por entidad)
│
└── infrastructure/
    └── config/
        └── mongock/                    ← Clases de migraciones Mongock
            ├── AddDefaultAuthoritiesMigration.java
            ├── DevDataSeeder.java
            └── AddMongoIndexesMigration.java
```

---

## 6. Modelo de dominio

### Colecciones MongoDB (13 entidades)

| # | Entidad Java | Colección MongoDB | Descripción |
|---|---|---|---|
| 1 | `User` | `jhi_user` | Cuenta de acceso (gestionada por JHipster) |
| 2 | `Authority` | `jhi_authority` | Rol del sistema (ROLE_ADMIN, etc.) |
| 3 | `PerfilUsuario` | `perfil_usuario` | Perfil extendido de negocio del usuario |
| 4 | `DocumentoUsuario` | `documento_usuario` | Documentos de verificación de identidad |
| 5 | `Inmueble` | `inmueble` | Unidad arrendable (apartamento, habitación) |
| 6 | `PublicacionInmueble` | `publicacion_inmueble` | Anuncio de un inmueble en el portal |
| 7 | `MultimediaInmueble` | `multimedia_inmueble` | Fotos y archivos de un inmueble |
| 8 | `SolicitudArriendo` | `solicitud_arriendo` | Solicitud de arrendamiento de un candidato |
| 9 | `VisitaProgramada` | `visita_programada` | Cita presencial al inmueble |
| 10 | `ContratoArriendo` | `contrato_arriendo` | Contrato formal arrendador-arrendatario |
| 11 | `PublicacionRoomie` | `publicacion_roomie` | Anuncio de búsqueda de roomie |
| 12 | `SolicitudRoomie` | `solicitud_roomie` | Postulación de un candidato a roomie |
| 13 | `Calificacion` | `calificacion` | Evaluación entre partes al finalizar contrato |

### Jerarquía de dominio

```
User (jhi_user)
  └── PerfilUsuario (1:1)
        ├── DocumentoUsuario (1:N) — documentos de verificación
        └── Inmueble (1:N) — inmuebles del propietario
              ├── PublicacionInmueble (1:N)
              │     ├── MultimediaInmueble (1:N) — fotos
              │     └── SolicitudArriendo (1:N)
              │           └── VisitaProgramada (1:N)
              └── ContratoArriendo (1:N)
                    └── Calificacion (1:N)

PublicacionRoomie (N:1 → PerfilUsuario arrendatario, N:1 → Inmueble)
  └── SolicitudRoomie (1:N)
```

### Enums de dominio (9 activos)

| Enum | Valores | Entidad que lo usa |
|---|---|---|
| `EstadoPublicacion` | BORRADOR, PUBLICADA, VISITA_AGENDADA, POSTULANTE_SELECCIONADO, RESERVADA, CONTRATO_EN_FIRMA, ARRENDADA, FINALIZADA, ARCHIVADA | PublicacionInmueble |
| `EstadoSolicitud` | PENDIENTE, EN_REVISION, PREAPROBADA, APROBADA, RECHAZADA, CANCELADA | SolicitudArriendo, SolicitudRoomie |
| `EstadoVisita` | PROGRAMADA, CONFIRMADA, REALIZADA, CANCELADA | VisitaProgramada |
| `EstadoContrato` | BORRADOR, EN_REVISION, VIGENTE, FINALIZADO, CANCELADO | ContratoArriendo |
| `TipoInmueble` | APARTAMENTO, CASA, HABITACION, ESTUDIO, LOCAL | Inmueble |
| `TipoDocumento` | CC, CE, TI, PASSPORT, NIT, OTRO | DocumentoUsuario, PerfilUsuario |
| `TipoMedio` | IMAGEN, VIDEO, DOCUMENTO | MultimediaInmueble |
| `TipoCalificacion` | ARRENDADOR_A_ARRENDATARIO, ARRENDATARIO_A_ARRENDADOR, ARRENDATARIO_A_ROOMIE, ROOMIE_A_ARRENDATARIO | Calificacion |
| `EstadoPublicacionRoomie` | ACTIVA, CUBIERTA, CANCELADA | PublicacionRoomie |

---

## 7. REST Resources

Los 16 recursos REST siguen la convención JHipster: `ResourceName` → `/api/nombre-en-kebab-case`.

| # | REST Resource | Ruta base | Acceso |
|---|---|---|---|
| 1 | `AuthenticateResource` | `/api/authenticate` | Público |
| 2 | `AccountResource` | `/api/account` | Autenticado |
| 3 | `UserResource` | `/api/admin/users` | ROLE_ADMIN |
| 4 | `PublicResource` | `/api/public/**` | Público |
| 5 | `PerfilUsuarioResource` | `/api/perfil-usuarios` | Autenticado |
| 6 | `DocumentoUsuarioResource` | `/api/documento-usuarios` | Autenticado |
| 7 | `InmuebleResource` | `/api/inmuebles` | Autenticado |
| 8 | `PublicacionInmuebleResource` | `/api/publicaciones-inmuebles` | Mixto |
| 9 | `MultimediaInmuebleResource` | `/api/multimedia-inmuebles` | Autenticado |
| 10 | `SolicitudArriendoResource` | `/api/solicitudes-arriendos` | Autenticado |
| 11 | `VisitaProgramadaResource` | `/api/visitas-programadas` | Autenticado |
| 12 | `ContratoArriendoResource` | `/api/contratos-arriendos` | Autenticado |
| 13 | `PublicacionRoomieResource` | `/api/publicaciones-roomies` | Mixto |
| 14 | `SolicitudRoomieResource` | `/api/solicitudes-roomies` | Autenticado |
| 15 | `CalificacionResource` | `/api/calificaciones` | Autenticado |
| 16 | `UploadsResource` | `/uploads/**` | Público (archivos estáticos) |

### Endpoints de gestión (Spring Boot Actuator)

| Ruta | Descripción |
|---|---|
| `/management/health` | Estado de salud de la aplicación |
| `/management/info` | Información de versión y build |
| `/management/prometheus` | Métricas en formato Prometheus |
| `/management/loggers` | Gestión de niveles de log en runtime |

---

## 8. Seguridad

### Spring Security — Configuración de rutas

```
Rutas PÚBLICAS (sin JWT requerido):
  /api/authenticate                → Login, devuelve JWT
  /api/register                    → Registro de nuevos usuarios
  /api/activate                    → Activación de cuenta
  /api/public/**                   → Publicaciones activas (portal)
  /uploads/**                      → Archivos multimedia
  /management/health               → Health check
  /management/info                 → Info de versión
  /portal/**                       → React SPA (archivos estáticos)
  /**                              → Angular SPA (archivos estáticos)

Rutas PROTEGIDAS (JWT obligatorio):
  /api/account                     → Cualquier usuario autenticado
  /api/perfil-usuarios/**          → Cualquier usuario autenticado
  /api/inmuebles/**                → ROLE_ARRENDADOR o ROLE_ADMIN
  /api/publicaciones-inmuebles/**  → Mixto según método HTTP
  /api/solicitudes-arriendos/**    → Autenticado
  /api/contratos-arriendos/**      → Autenticado
  /api/admin/**                    → ROLE_ADMIN exclusivo
  /management/**                   → ROLE_ADMIN (excepto /health, /info)
```

### Roles del sistema

| Rol | Descripción | Heredado de |
|---|---|---|
| `ROLE_ADMIN` | Administrador del sistema. Acceso completo a todas las entidades y el panel JHipster. | — |
| `ROLE_USER` | Rol base de cualquier usuario autenticado. Requerido para acceder a endpoints autenticados. | — |
| `ROLE_ARRENDADOR` | Propietario o gestor de inmuebles. Puede crear y gestionar publicaciones. | ROLE_USER |
| `ROLE_ARRENDATARIO` | Persona que busca arrendar. Puede enviar solicitudes y contratos. | ROLE_USER |

Un usuario puede tener múltiples roles simultáneamente. Por ejemplo, un arrendatario que también tiene un inmueble tendrá `ROLE_ARRENDATARIO` y `ROLE_ARRENDADOR` al mismo tiempo.

### JWT — Estructura del token

```
Algoritmo:  HS512 (HMAC-SHA-512)
Validez:    24 horas desde la emisión
Tipo:       Stateless — no hay sesión en el servidor

Claims del payload:
  sub   → login del usuario (String)
  auth  → roles separados por coma (String)
         ej: "ROLE_USER,ROLE_ARRENDADOR"
  exp   → timestamp de expiración (Unix epoch)

Flujo:
  1. POST /api/authenticate  { username, password, rememberMe }
  2. Respuesta: { id_token: "eyJ..." }
  3. Cada request subsiguiente: Authorization: Bearer eyJ...
  4. JwtFilter valida la firma HS512 y el exp en cada request
  5. Si exp vencido o firma inválida → 401 Unauthorized
```

### Matriz de permisos por rol (resumen)

| Entidad | ROLE_USER | ROLE_ARRENDATARIO | ROLE_ARRENDADOR | ROLE_ADMIN |
|---|---|---|---|---|
| User | R (propio) | R (propio) | R (propio) | CRUD* |
| PerfilUsuario | R* (público) | CRUD (propio) | CRUD (propio) | CRUD* |
| Inmueble | — | — | CRUD (propios) | CRUD* |
| PublicacionInmueble | R* (PUBLICADA) | R* (PUBLICADA) | CRUD (propias) | CRUD* |
| SolicitudArriendo | — | CRD (propias) | R,U-estado (recibidas) | CRUD* |
| VisitaProgramada | — | CR, U-estado | CR, U-estado | CRUD* |
| ContratoArriendo | — | R (donde es parte) | CRUD (propios) | CRUD* |
| PublicacionRoomie | R* (PUBLICADA) | CRUD (propias) | — | CRUD* |
| Calificacion | R* (visible=true) | CR (condicional) | CR (condicional) | CRUD* |

---

## 9. Migraciones Mongock

Mongock gestiona las migraciones de datos de forma similar a Flyway/Liquibase pero para MongoDB. Las migraciones se ejecutan en el arranque de la aplicación y son idempotentes.

### Migraciones ejecutadas en producción

| # | Clase Java | ID Mongock | Orden | Descripción |
|---|---|---|---|---|
| 001 | `AddDefaultAuthoritiesMigration` | `001` | 1 | Crea los documentos de roles en `jhi_authority`: ROLE_ADMIN, ROLE_USER, ROLE_ARRENDADOR, ROLE_ARRENDATARIO. También crea el usuario admin inicial. |
| 002 | `DevDataSeeder` | `002` | 2 | Inserta datos de desarrollo: usuarios de prueba con distintos roles, inmuebles de ejemplo, publicaciones en estado PUBLICADA, para facilitar el desarrollo y las demos. |
| 003 | `AddMongoIndexesMigration` | `003` | 3 | Crea índices MongoDB en las colecciones principales para optimizar las queries más frecuentes: índice en `publicacion_inmueble.estado`, índice en `solicitud_arriendo.publicacion`, índice en `perfil_usuario.userId`. |

### Estrategia de migraciones

```
Arranque de Spring Boot
  ↓
Mongock verifica la colección mongockChangeLog en MongoDB
  ↓
  ├── Si el changeId ya está registrado → SKIP (idempotente)
  └── Si el changeId no existe → EJECUTAR y registrar
```

Las migraciones son de **solo avance** (no tienen rollback automático). Para revertir un cambio se debe escribir una nueva migración con el ID siguiente.
