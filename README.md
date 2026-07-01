# RoomRent — Plataforma de Arrendamiento

Sistema de gestión de arrendamiento de inmuebles desarrollado con **JHipster 9.1.0**.

> Repositorio: [github.com/Jose-Bohorquez/roomRent](https://github.com/Jose-Bohorquez/roomRent)

---

## Descripción del sistema

RoomRent conecta arrendadores con arrendatarios y candidatos roomie. Ofrece:

- Registro y verificación de perfiles de usuario
- Publicación de inmuebles (apartamentos, casas, habitaciones, locales, oficinas)
- Flujo completo de solicitud → visita → contrato digital
- Sistema de publicaciones roomie para co-habitación
- Calificaciones y reputación entre actores
- Panel de administración Angular + Portal público React

---

## Arquitectura

| Capa | Tecnología |
|---|---|
| Backend | Spring Boot 4.0.6 + Java 21 |
| Base de datos | MongoDB |
| Autenticación | JWT (stateless) |
| Admin frontend | Angular 21 (standalone components, signals) |
| Portal público | React 18 (SPA independiente en `/portal/`) |
| Build | Maven (backend) + esbuild/Angular CLI (frontend) |

### Enrutamiento dual SPA

El proyecto tiene dos SPAs coexistiendo:

- `/portal/*` → React (`src/main/resources/static/portal/index.html`)
- Todo lo demás → Angular

El filtro `SpaWebFilter.java` gestiona el enrutamiento. **Después de cada build Angular**, el portal React debe restaurarse:

```bash
cp -r src/main/resources/static/portal target/classes/static/portal
```

---

## Prerrequisitos

| Herramienta | Versión requerida |
|---|---|
| Java (JDK) | 21 |
| Maven | 3.9+ (incluido como wrapper `./mvnw`) |
| Node.js | 20+ |
| npm | 10+ |
| MongoDB | 7.x (o vía Docker) |

> **Importante:** Usar explícitamente `JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64` si el sistema tiene múltiples JDKs.

---

## Configuración inicial

```bash
# Clonar el repositorio
git clone git@github.com:Jose-Bohorquez/roomRent.git
cd roomRent

# Instalar dependencias Node
npm install
```

---

## Desarrollo local

### 1. Iniciar MongoDB (Docker)

```bash
docker compose -f src/main/docker/mongodb.yml up -d
```

### 2. Iniciar el backend (Spring Boot)

```bash
JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64 ./mvnw spring-boot:run
```

El servidor queda en `http://localhost:8080`

### 3. Iniciar el frontend Angular (modo watch)

En una terminal separada:

```bash
npm start
```

El proxy de desarrollo redirige `/api/*` al puerto 8080.

---

## Comandos de compilación

### Frontend Angular (producción)

```bash
npm run webapp:build
# Restaurar portal React después del build:
cp -r src/main/resources/static/portal target/classes/static/portal
```

### Backend (compilar sin ejecutar)

```bash
JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64 ./mvnw compile -Denforcer.skip=true
```

### JAR completo de producción

```bash
JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64 ./mvnw -Pprod clean verify
java -jar target/*.jar
```

---

## Estructura del proyecto

```
roomRent/
├── src/
│   ├── main/
│   │   ├── java/com/roomrent/app/
│   │   │   ├── domain/           # Entidades MongoDB (documentos)
│   │   │   ├── repository/       # Spring Data MongoDB repositories
│   │   │   ├── service/          # Interfaces de servicio
│   │   │   │   └── impl/         # Implementaciones de servicio
│   │   │   ├── web/rest/         # Controladores REST (API)
│   │   │   └── config/           # Configuración Spring
│   │   ├── resources/
│   │   │   ├── static/portal/    # React portal (producción)
│   │   │   └── config/           # application.yml, etc.
│   │   └── webapp/               # Angular SPA
│   │       └── app/
│   │           ├── entities/     # Módulos CRUD por entidad
│   │           ├── layouts/      # Navbar, Sidebar, Footer
│   │           ├── dashboard/    # Panel principal
│   │           └── shared/       # Componentes reutilizables
├── jhipster-jdl.jdl              # Modelo de dominio completo
├── .yo-rc.json                   # Configuración JHipster
├── .jhipster/                    # JSON de configuración por entidad
└── pom.xml
```

---

## Modelo de dominio

Ver [`jhipster-jdl.jdl`](jhipster-jdl.jdl) para el modelo completo con anotaciones.

### Entidades principales

| Entidad | Descripción | Paginación |
|---|---|---|
| `PerfilUsuario` | Datos personales y laborales del usuario | Sí |
| `DocumentoUsuario` | Documentos de verificación de identidad | Sí |
| `Inmueble` | Unidad arrendable (aparto, casa, habitación…) | Sí |
| `PublicacionInmueble` | Anuncio activo de un inmueble en el portal | Sí |
| `MultimediaInmueble` | Fotos y archivos del inmueble | No |
| `SolicitudArriendo` | Solicitud enviada por un arrendatario | Sí |
| `VisitaProgramada` | Cita para conocer el inmueble | No |
| `ContratoArriendo` | Contrato digital firmado | Sí |
| `PublicacionRoomie` | Habitación disponible para compartir | Sí |
| `SolicitudRoomie` | Postulación a una publicación roomie | Sí |
| `Calificacion` | Valoración entre actores al cerrar contrato | Sí |

### Flujos principales

```
Inmueble → PublicacionInmueble → SolicitudArriendo → VisitaProgramada
                                                   ↓
                                            ContratoArriendo → Calificacion
```

```
PublicacionRoomie → SolicitudRoomie (flujo co-habitación independiente)
```

---

## Estado del modelo vs. requerimientos

### Soportado completamente

- Arrendador con múltiples inmuebles
- Tipos: Apartamento, Casa, Habitación, Apartaestudio, Local, Oficina
- Múltiples publicaciones a lo largo del tiempo por inmueble
- Múltiples contratos históricos por inmueble
- Publicaciones y solicitudes para roomies
- Contratos con URL a documento digital (firma electrónica)
- Sistema de calificaciones entre arrendadores, arrendatarios y roomies
- Visitas programadas vinculadas a solicitudes
- Multimedia (fotos/videos) por inmueble
- Documentos de verificación por perfil

### Limitaciones actuales y propuesta de evolución

| Escenario | Estado | Propuesta |
|---|---|---|
| Edificio con múltiples unidades | No implementado | Entidad `Edificio` → OneToMany → `Inmueble` |
| Múltiples ocupantes por contrato | Parcial (roomies tienen flujo propio) | Entidad `OcupanteContrato` → ManyToOne → `ContratoArriendo` |
| Seguimiento de pagos mensuales | No implementado | Entidad `PagoArriendo` → ManyToOne → `ContratoArriendo` |
| Notificaciones in-app | No implementado | Entidad `Notificacion` o servicio de eventos |

> Estas extensiones son **aditivas** y compatibles con el modelo actual. No requieren modificar entidades existentes.

---

## Variables de entorno

El archivo `src/main/resources/config/application.yml` y `application-dev.yml` controlan la configuración. Las variables sensibles se sobreescriben en producción:

| Variable | Descripción | Valor por defecto (dev) |
|---|---|---|
| `SPRING_DATA_MONGODB_URI` | URI de conexión MongoDB | `mongodb://localhost:27017/room` |
| `JHIPSTER_SECURITY_AUTHENTICATION_JWT_SECRET` | Clave JWT (base64) | En `.yo-rc.json` |
| `SERVER_PORT` | Puerto del servidor | `8080` |

---

## API REST

Todos los endpoints siguen la convención JHipster:

```
GET    /api/{entidades}           → listado paginado
GET    /api/{entidades}/{id}      → detalle
POST   /api/{entidades}           → crear
PUT    /api/{entidades}/{id}      → actualizar completo
PATCH  /api/{entidades}/{id}      → actualizar parcial
DELETE /api/{entidades}/{id}      → eliminar
```

Documentación Swagger en: `http://localhost:8080/swagger-ui/index.html` (perfil `api-docs`)

---

## Regeneración con JHipster

Para regenerar entidades usando el JDL:

```bash
# Instalar JHipster globalmente (si no está)
npm install -g generator-jhipster@9.1.0

# Importar el JDL (regenera solo lo que cambió)
jhipster import-jdl jhipster-jdl.jdl

# ⚠️ Respaldar ANTES de regenerar:
# - src/main/webapp/app/ (componentes personalizados)
# - src/main/webapp/content/scss/ (estilos)
# - src/main/webapp/app/layouts/ (sidebar, navbar)
# - src/main/webapp/app/shared/components/ (RrTableToolbar, RrEmptyState)
```

---

## Pruebas

### Backend

```bash
JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64 ./mvnw verify
```

### Frontend

```bash
npm test
```

---

## Docker

```bash
# MongoDB standalone
docker compose -f src/main/docker/mongodb.yml up -d

# Aplicación completa (requiere build previo)
npm run java:docker
docker compose -f src/main/docker/app.yml up -d
```

---

## Referencias

- [JHipster 9.1.0](https://www.jhipster.tech/documentation-archive/v9.1.0)
- [Spring Boot 4.x](https://docs.spring.io/spring-boot/index.html)
- [Angular 21](https://angular.dev)
- [Spring Data MongoDB](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/)
