# 06 — Base de Datos

**Versión:** RC1
**Fecha:** 2026-07-02
**Motor:** MongoDB 7
**Proyecto:** RoomRent — room-rent.xyz

---

## 1. Configuración General

| Parámetro | Valor |
|---|---|
| Motor | MongoDB 7.x |
| Autenticación | Habilitada (`--auth`) |
| Base de datos | `room` |
| Usuario de aplicación | `roomrent` |
| authSource | `admin` |
| Volumen Docker | `roomrent-mongo-data` (nombrado) |
| URI de conexión | `mongodb://roomrent:***@mongo:27017/room?authSource=admin` |

El volumen nombrado `roomrent-mongo-data` persiste los datos entre reinicios y recreaciones del contenedor. **Nunca eliminar este volumen** en producción sin hacer backup previo.

---

## 2. Colecciones

### 2.1 `jhi_user`

Gestiona los usuarios de la plataforma. Creada por JHipster.

| Campo | Tipo | NotNull | Descripción |
|---|---|---|---|
| `_id` | ObjectId | ✓ | Identificador interno MongoDB |
| `login` | String | ✓ | Nombre de usuario único |
| `email` | String | ✓ | Correo electrónico único |
| `password_hash` | String | ✓ | Hash BCrypt de la contraseña |
| `activated` | Boolean | ✓ | Si la cuenta está activada |
| `authorities` | String[] | ✓ | Lista de roles (`ROLE_*`) |
| `activation_key` | String | — | Token temporal de activación de cuenta |
| `reset_key` | String | — | Token temporal de reset de contraseña |
| `lang_key` | String | — | Idioma preferido (`es`, `en`) |
| `first_name` | String | — | Nombre |
| `last_name` | String | — | Apellido |
| `image_url` | String | — | URL de foto de perfil |
| `created_date` | Date | — | Fecha de creación |
| `last_modified_date` | Date | — | Última modificación |

---

### 2.2 `jhi_authority`

Catálogo de roles. Creada por JHipster.

| Campo | Tipo | NotNull | Descripción |
|---|---|---|---|
| `_id` / `name` | String | ✓ | Nombre del rol (clave primaria) |

Valores presentes en producción:

| Rol | Descripción |
|---|---|
| `ROLE_ADMIN` | Administrador total de la plataforma |
| `ROLE_USER` | Usuario base (arrendatario / roomie) |
| `ROLE_ARRENDADOR` | Propietario / publicador de inmuebles |

---

### 2.3 `inmueble`

Representa la propiedad física.

| Campo | Tipo | NotNull | Descripción |
|---|---|---|---|
| `_id` | ObjectId | ✓ | ID MongoDB |
| `nombre` | String | ✓ | Nombre descriptivo del inmueble |
| `direccion` | String | ✓ | Dirección completa |
| `ciudad` | String | ✓ | Ciudad |
| `barrio` | String | ✓ | Barrio |
| `tipo_inmueble` | String (enum) | ✓ | `APARTAMENTO`, `CASA`, `HABITACION`, `FINCA` |
| `numero_habitaciones` | Integer | ✓ | Cantidad de habitaciones |
| `numero_banos` | Integer | ✓ | Cantidad de baños |
| `estrato` | Integer | — | Estrato socioeconómico (1-6) |
| `descripcion` | String | — | Descripción libre |
| `area_m2` | Double | — | Área en metros cuadrados |
| `propietario` | DBRef → jhi_user | — | Referencia al usuario propietario |

---

### 2.4 `publicacion_inmueble`

Anuncio de arriendo asociado a un inmueble.

| Campo | Tipo | NotNull | Descripción |
|---|---|---|---|
| `_id` | ObjectId | ✓ | ID MongoDB |
| `titulo` | String | ✓ | Título del anuncio |
| `canon_arriendo` | Double | ✓ | Precio mensual en pesos |
| `estado` | String (enum) | ✓ | `DISPONIBLE`, `ARRENDADO`, `INACTIVO` |
| `permite_roomies` | Boolean | ✓ | Acepta co-residentes |
| `acepta_mascotas` | Boolean | ✓ | Acepta mascotas |
| `permite_fumadores` | Boolean | ✓ | Permite fumadores |
| `permite_ninos` | Boolean | ✓ | Permite niños |
| `permite_visitas` | Boolean | ✓ | Permite visitas |
| `permite_parejas` | Boolean | ✓ | Permite parejas |
| `inmueble` | DBRef → inmueble | ✓ | Referencia al inmueble |
| `descripcion` | String | — | Descripción adicional del anuncio |
| `fecha_disponible` | Date | — | Fecha desde la cual está disponible |
| `fecha_publicacion` | Date | — | Fecha de creación del anuncio |

---

### 2.5 `multimedia_inmueble`

Archivos multimedia (fotos, videos) asociados a un inmueble.

| Campo | Tipo | NotNull | Descripción |
|---|---|---|---|
| `_id` | ObjectId | ✓ | ID MongoDB |
| `url_media` | String | ✓ | Ruta del archivo servido por `/uploads/` |
| `tipo_media` | String (enum) | ✓ | `IMAGEN`, `VIDEO` |
| `principal` | Boolean | ✓ | Si es la imagen principal (portada) |
| `inmueble` | DBRef → inmueble | ✓ | Referencia al inmueble |
| `descripcion` | String | — | Descripción del archivo |
| `orden` | Integer | — | Orden de visualización |

---

### 2.6 `solicitud_arriendo`

Solicitud formal de un arrendatario para arrendar un inmueble.

| Campo | Tipo | Descripción |
|---|---|---|
| `_id` | ObjectId | ID MongoDB |
| `estado` | String (enum) | `PENDIENTE`, `APROBADA`, `RECHAZADA`, `CANCELADA` |
| `fecha_solicitud` | Date | Fecha de creación |
| `mensaje` | String | Mensaje del solicitante |
| `publicacion` | DBRef → publicacion_inmueble | Publicación objetivo |
| `arrendatario` | DBRef → jhi_user | Usuario solicitante |

---

### 2.7 `visita_programada`

Agenda de visitas para ver un inmueble en persona.

| Campo | Tipo | Descripción |
|---|---|---|
| `_id` | ObjectId | ID MongoDB |
| `fecha_hora` | Date | Fecha y hora de la visita |
| `estado` | String (enum) | `PROGRAMADA`, `REALIZADA`, `CANCELADA` |
| `notas` | String | Notas adicionales |
| `solicitud` | DBRef → solicitud_arriendo | Solicitud asociada |

---

### 2.8 `contrato_arriendo`

Contrato activo entre arrendador y arrendatario.

| Campo | Tipo | Descripción |
|---|---|---|
| `_id` | ObjectId | ID MongoDB |
| `fecha_inicio` | Date | Inicio del contrato |
| `fecha_fin` | Date | Fin del contrato |
| `valor_mensual` | Double | Canon mensual pactado |
| `estado` | String (enum) | `ACTIVO`, `FINALIZADO`, `CANCELADO` |
| `solicitud` | DBRef → solicitud_arriendo | Solicitud origen |

---

### 2.9 `publicacion_roomie`

Publicación de un usuario que busca co-residentes (roomies).

| Campo | Tipo | Descripción |
|---|---|---|
| `_id` | ObjectId | ID MongoDB |
| `descripcion` | String | Descripción personal |
| `presupuesto_max` | Double | Presupuesto máximo mensual |
| `ciudad` | String | Ciudad de búsqueda |
| `estado` | String (enum) | `ACTIVA`, `INACTIVA` |
| `usuario` | DBRef → jhi_user | Usuario que publica |

---

### 2.10 `solicitud_roomie`

Solicitud de contacto entre usuarios de la sección roomies.

| Campo | Tipo | Descripción |
|---|---|---|
| `_id` | ObjectId | ID MongoDB |
| `estado` | String (enum) | `PENDIENTE`, `ACEPTADA`, `RECHAZADA` |
| `mensaje` | String | Mensaje de contacto |
| `publicacion_roomie` | DBRef → publicacion_roomie | Publicación objetivo |
| `solicitante` | DBRef → jhi_user | Usuario que solicita |

---

### 2.11 `calificacion`

Calificaciones y reseñas post-contrato.

| Campo | Tipo | Descripción |
|---|---|---|
| `_id` | ObjectId | ID MongoDB |
| `puntaje` | Integer | Puntuación de 1 a 5 |
| `comentario` | String | Comentario de texto |
| `tipo` | String (enum) | `ARRENDADOR_A_ARRENDATARIO`, `ARRENDATARIO_A_ARRENDADOR` |
| `contrato` | DBRef → contrato_arriendo | Contrato calificado |
| `autor` | DBRef → jhi_user | Quien califica |

---

### 2.12 `perfil_usuario`

Información extendida del perfil (complementa `jhi_user`).

| Campo | Tipo | Descripción |
|---|---|---|
| `_id` | ObjectId | ID MongoDB |
| `telefono` | String | Teléfono de contacto |
| `bio` | String | Descripción personal |
| `ocupacion` | String | Ocupación |
| `tiene_mascotas` | Boolean | Si tiene mascotas |
| `fuma` | Boolean | Si fuma |
| `usuario` | DBRef → jhi_user | Referencia 1:1 al usuario |

---

### 2.13 `documento_usuario`

Documentos de identidad subidos para verificación.

| Campo | Tipo | Descripción |
|---|---|---|
| `_id` | ObjectId | ID MongoDB |
| `tipo_documento` | String (enum) | `CC`, `CE`, `PASAPORTE` |
| `numero_documento` | String | Número del documento |
| `url_frente` | String | URL imagen frente |
| `url_reverso` | String | URL imagen reverso |
| `verificado` | Boolean | Si fue verificado por admin |
| `usuario` | DBRef → jhi_user | Dueño del documento |

---

## 3. Índices

Los índices son creados automáticamente por **Mongock** en la migration `003`. Se listan a continuación:

### Colección `inmueble`

| Índice | Campos | Tipo |
|---|---|---|
| `idx_inmueble_ciudad` | `ciudad` | Simple ascendente |
| `idx_inmueble_tipo` | `tipo_inmueble` | Simple ascendente |
| `idx_inmueble_estrato` | `estrato` | Simple ascendente |
| `idx_inmueble_propietario` | `propietario.$id` | Simple ascendente |

### Colección `publicacion_inmueble`

| Índice | Campos | Tipo |
|---|---|---|
| `idx_pub_estado` | `estado` | Simple ascendente |
| `idx_pub_canon` | `canon_arriendo` | Simple ascendente |
| `idx_pub_roomies` | `permite_roomies` | Simple ascendente |

### Colección `multimedia_inmueble`

| Índice | Campos | Tipo |
|---|---|---|
| `idx_multimedia_principal` | `principal` | Simple ascendente |

---

## 4. Mongock (Migraciones)

| Parámetro | Valor |
|---|---|
| Colección de control | `mongockChangeLog` |
| Modo de transacción | `NO-TRANSACTION` (MongoDB replica set no requerido) |
| Ubicación de changelogs | `src/main/java/com/roomrent/config/dbmigrations/` |

Las migraciones se ejecutan automáticamente al iniciar la aplicación. El orden es:

| ID | Descripción |
|---|---|
| `001` | Datos iniciales (roles y usuario admin) |
| `002` | Datos de ejemplo (inmuebles de demostración) |
| `003` | Creación de índices de rendimiento |

Si Mongock detecta que una migración ya fue aplicada (por el hash almacenado en `mongockChangeLog`), la omite.

---

## 5. Backup con `mongodump`

```bash
# Backup completo de la base de datos room
docker exec roomrent-mongo mongodump \
  --uri="mongodb://roomrent:CAMBIAR_PASSWORD@localhost:27017/room?authSource=admin" \
  --out=/dump/$(date +%Y-%m-%d)

# Copiar backup fuera del contenedor
docker cp roomrent-mongo:/dump/$(date +%Y-%m-%d) /opt/roomrent/backups/

# Comprimir
tar -czf /opt/roomrent/backups/roomrent-$(date +%Y-%m-%d).tar.gz \
  /opt/roomrent/backups/$(date +%Y-%m-%d)
```

> Recomendación: automatizar con cron y copiar a S3 u otro almacenamiento externo.

---

## 6. Restore con `mongorestore`

```bash
# Descomprimir backup
tar -xzf /opt/roomrent/backups/roomrent-YYYY-MM-DD.tar.gz -C /tmp/restore/

# Copiar al contenedor
docker cp /tmp/restore/YYYY-MM-DD roomrent-mongo:/restore/

# Restaurar (drop primero para evitar duplicados)
docker exec roomrent-mongo mongorestore \
  --uri="mongodb://roomrent:CAMBIAR_PASSWORD@localhost:27017/room?authSource=admin" \
  --drop \
  /restore/YYYY-MM-DD/room
```

> **Advertencia:** `--drop` elimina los datos actuales antes de restaurar. Usar con precaución.

---

## 7. Acceso Administrativo (mongosh)

```bash
# Entrar al contenedor MongoDB
docker exec -it roomrent-mongo mongosh \
  -u root \
  -p MONGO_INITDB_ROOT_PASSWORD \
  --authenticationDatabase admin

# Dentro de mongosh, cambiar a la base room
use room

# Ver colecciones
show collections

# Ver documentos de ejemplo
db.inmueble.find().limit(5).pretty()

# Ver logs de migraciones Mongock
db.mongockChangeLog.find().pretty()
```

---

## 8. Consideraciones de Producción

- El volumen `roomrent-mongo-data` se monta en el host vía Docker. **Nunca hacer `docker compose down -v`** en producción, ya que `-v` elimina los volúmenes nombrados.
- Realizar backup antes de cualquier migración nueva.
- Mongock en modo `NO-TRANSACTION` significa que si una migración falla a mitad, los cambios parciales quedan en la BD. Diseñar changelogs idempotentes.
- MongoDB 7 soporta operaciones atómicas a nivel de documento. No se usa transacciones multi-documento en esta versión.
