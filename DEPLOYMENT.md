# Despliegue en producción — room-rent.xyz

## Infraestructura

- **Servidor:** AWS EC2 (Debian 13 / Trixie), 2 vCPU, 2 GB RAM + 2 GB swap
- **Dominio:** room-rent.xyz (Hostinger DNS, registro A apuntando a la IP del servidor)
- **Contenedores:** Docker 29 + Docker Compose v5 (`docker-compose.yml` en la raíz)
- **Proxy:** Nginx en el host (no en contenedor), con TLS vía Let's Encrypt / Certbot
- **Base de datos:** MongoDB 7 (contenedor `mongo`, volumen `roomrent-mongo-data`)

## Servicios

| Servicio | Descripción |
|---|---|
| `app` | Imagen `roomrent-app:latest`, build multi-stage (React + Angular + Spring Boot), expuesta solo en `127.0.0.1:8080` |
| `mongo` | MongoDB 7 con usuario de aplicación creado vía `scripts/mongo-init.sh` |

## Variables de entorno (`.env`)

No versionado. Contiene: credenciales Mongo (root + app user), secreto JWT (`openssl rand -base64 64`), credenciales SMTP Gmail (App Password), y `JHIPSTER_MAIL_BASE_URL=https://room-rent.xyz`. Ver `.env.example` para la plantilla completa.

## Perfiles Spring activos

```
SPRING_PROFILES_ACTIVE=prod,api-docs
```

- `prod`: perfil estándar de producción JHipster.
- `api-docs`: habilita Swagger/OpenAPI (`/v3/api-docs`, `/swagger-ui/**`), protegido con rol `ADMIN` en `SecurityConfiguration`. Sin este perfil, JHipster deshabilita springdoc por defecto (404 en Swagger UI del panel admin).

## CORS

Habilitado en `application-prod.yml` bajo `jhipster.cors`, restringido a los orígenes canónicos:

```yaml
jhipster:
  cors:
    allowed-origins: "https://room-rent.xyz,https://www.room-rent.xyz"
```

Sin esta configuración, el registro/login fallan silenciosamente en el navegador cuando la página se carga desde un origen distinto al de la API (por ejemplo `http://` vs `https://`).

## Nginx

Config de producción en `nginx/room-rent.conf` (instalada en `/etc/nginx/sites-available/`). Requiere el módulo `libnginx-mod-http-headers-more-filter` (usa la directiva `more_set_headers`).

- Redirige HTTP → HTTPS y `www` → apex.
- Sirve `/portal/**` (portal público React) con cache larga para assets con hash.
- Proxea todo lo demás a `127.0.0.1:8080` (Spring Boot).

## Certificados TLS

Emitidos con `certbot certonly --webroot` para `room-rent.xyz` y `www.room-rent.xyz`. Renovación automática vía `certbot.timer` (systemd).

## Despliegue / actualización

```bash
cd ~/roomRent
./scripts/deploy.sh          # pull + build + up -d, con healthcheck
./scripts/deploy.sh --no-cache   # build sin caché de Docker
```

## Datos de prueba

`DevDataSeeder` (`src/main/java/com/roomrent/app/config/DevDataSeeder.java`) está anotado `@Profile("dev")` y **no se ejecuta en producción**. La base de producción solo contiene lo generado por las migraciones Mongock (roles, índices, setup inicial).
