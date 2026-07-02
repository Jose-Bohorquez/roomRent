# SCHEMA VALIDATION — RoomRent MongoDB

Esquema completo de todas las colecciones MongoDB con validaciones, tipos y relaciones.

**Generado desde**: código fuente Java (`src/main/java/com/roomrent/app/domain/`)  
**Fecha**: 2026-07-02 | **Versión**: RC1 | **Motor**: MongoDB 7

---

## Convenciones

| Símbolo | Significado |
|---------|-------------|
| ✅ `@NotNull` | Campo obligatorio — rechaza `null` en validación Bean |
| `@Min(n)` / `@Max(n)` | Rango numérico inclusive |
| `@Size(min,max)` | Longitud de String |
| `@Email` | Formato email |
| `@Pattern` | Regex |
| `@Indexed` | Índice MongoDB |
| `@DBRef` | Referencia a otro documento (ID almacenado) |
| `AUDIT` | Campos heredados de `AbstractAuditingEntity` |

**Todos los documentos heredan 4 campos de auditoría** (`AbstractAuditingEntity`):

| Campo MongoDB | Tipo | Descripción |
|---------------|------|-------------|
| `created_by` | String | Login del usuario que creó el documento |
| `created_date` | Date (Instant) | Fecha de creación (auto) |
| `last_modified_by` | String | Login del último modificador |
| `last_modified_date` | Date (Instant) | Fecha de última modificación (auto) |

---

## 1. `jhi_user` — Usuarios del sistema

Colección manejada por JHipster. Almacena credenciales y estado de autenticación.

| Campo MongoDB | Tipo Java | Constraints | Descripción |
|---------------|-----------|-------------|-------------|
| `_id` | String | ✅ `@Id` | ObjectId generado |
| `login` | String | ✅ `@NotNull` `@Pattern(LOGIN_REGEX)` `@Size(1,50)` `@Indexed` | Nombre de usuario único |
| `password_hash` | String | ✅ `@NotNull` `@Size(60,60)` `@JsonIgnore` | BCrypt hash (60 chars fijos) |
| `first_name` | String | `@Size(max=50)` | Primer nombre |
| `last_name` | String | `@Size(max=50)` | Apellido |
| `email` | String | `@Email` `@Size(5,254)` `@Indexed` | Correo único |
| `activated` | Boolean | — | `false` hasta activar cuenta |
| `lang_key` | String | `@Size(2,10)` | Idioma (`es`, `en`) |
| `image_url` | String | `@Size(max=256)` | URL foto de perfil |
| `activation_key` | String | `@Size(max=20)` `@JsonIgnore` | Token de activación de email |
| `reset_key` | String | `@Size(max=20)` `@JsonIgnore` | Token para reset de contraseña |
| `reset_date` | Date | — | Fecha de expiración del reset_key |
| `authorities` | Set\<String\> | — | Roles: `ROLE_ADMIN`, `ROLE_USER`, `ROLE_ARRENDADOR`, `ROLE_ARRENDATARIO` |
| + AUDIT | — | — | created_by, created_date, last_modified_by, last_modified_date |

**Índices**: `login` (unique), `email` (unique)  
**Ejemplo de authorities**: `["ROLE_USER", "ROLE_ARRENDADOR"]`

---

## 2. `perfil_usuario` — Perfil extendido del usuario

Datos personales del arrendador o arrendatario, vinculado a `jhi_user`.

| Campo MongoDB | Tipo Java | Constraints | Descripción |
|---------------|-----------|-------------|-------------|
| `_id` | String | ✅ `@Id` | ObjectId |
| `tipo_documento` | Enum | ✅ `@NotNull` | `CC` `CE` `TI` `PASSPORT` `NIT` `OTRO` |
| `numero_documento` | String | ✅ `@NotNull` | Número de documento de identidad |
| `primer_nombre` | String | ✅ `@NotNull` | — |
| `segundo_nombre` | String | — | Opcional |
| `primer_apellido` | String | ✅ `@NotNull` | — |
| `segundo_apellido` | String | — | Opcional |
| `fecha_nacimiento` | Date (LocalDate) | ✅ `@NotNull` | Fecha de nacimiento |
| `genero` | Enum | — | `MASCULINO` `FEMENINO` `OTRO` `PREFIERO_NO_DECIR` |
| `telefono` | String | ✅ `@NotNull` | Teléfono de contacto |
| `direccion_actual` | String | — | Dirección de residencia |
| `ciudad` | String | ✅ `@NotNull` | Ciudad de residencia |
| `barrio` | String | — | Barrio |
| `profesion` | String | — | Profesión |
| `ocupacion` | String | — | Ocupación actual |
| `empresa_trabajo` | String | — | Empresa empleadora |
| `universidad` | String | — | Universidad (si aplica) |
| `biografia` | String | — | Descripción personal |
| `intereses` | String | — | Intereses y hobbies |
| `tiene_mascotas` | Boolean | — | Si tiene mascotas |
| `fumador` | Boolean | — | Si fuma |
| `verificado` | Boolean | ✅ `@NotNull` | Si el perfil fue verificado por admin |
| `habilitado_roomie` | Boolean | ✅ `@NotNull` | Si puede participar en búsqueda de roomies |
| `estado` | Enum | ✅ `@NotNull` | `PENDIENTE_VERIFICACION` `ACTIVO` `INACTIVO` `SUSPENDIDO` `BLOQUEADO` `BANEADO` |
| `usuario` | @DBRef → jhi_user | — | Usuario de autenticación vinculado |
| `documentoses` | @DBRef → Set\<documento_usuario\> | — | Documentos subidos |
| `inmuebleses` | @DBRef → Set\<inmueble\> | — | Inmuebles del arrendador |
| + AUDIT | — | — | Campos de auditoría |

---

## 3. `inmueble` — Inmuebles registrados

| Campo MongoDB | Tipo Java | Constraints | Descripción |
|---------------|-----------|-------------|-------------|
| `_id` | String | ✅ `@Id` | ObjectId |
| `nombre` | String | ✅ `@NotNull` | Nombre/título del inmueble |
| `direccion` | String | ✅ `@NotNull` | Dirección completa |
| `ciudad` | String | ✅ `@NotNull` | Ciudad |
| `localidad` | String | — | Localidad (Bogotá) |
| `barrio` | String | ✅ `@NotNull` | Barrio |
| `latitud` | Double | — | Coordenada GPS lat |
| `longitud` | Double | — | Coordenada GPS lng |
| `tipo_inmueble` | Enum | ✅ `@NotNull` | `APARTAMENTO` `CASA` `HABITACION` `APARTAESTUDIO` `LOCAL` `OFICINA` `OTRO` |
| `area_metros_cuadrados` | Double | — | Área en m² |
| `numero_habitaciones` | Integer | ✅ `@NotNull` | Número de habitaciones |
| `numero_banos` | Integer | ✅ `@NotNull` | Número de baños |
| `numero_parqueaderos` | Integer | — | Parqueaderos disponibles |
| `estrato` | Integer | `@Min(1)` `@Max(6)` | Estrato socioeconómico 1-6 |
| `publicaciones` | @DBRef → Set\<publicacion_inmueble\> | — | Publicaciones del inmueble |
| `multimedia` | @DBRef → Set\<multimedia_inmueble\> | — | Fotos y videos |
| `contratos` | @DBRef → Set\<contrato_arriendo\> | — | Contratos asociados |
| `propietario` | @DBRef → perfil_usuario | — | Perfil del arrendador dueño |
| + AUDIT | — | — | `created_by` = login del arrendador |

**Nota**: `estrato` acepta `null` (opcional). Si se envía, debe estar entre 1 y 6 inclusive.

---

## 4. `publicacion_inmueble` — Publicaciones de arrendamiento

| Campo MongoDB | Tipo Java | Constraints | Descripción |
|---------------|-----------|-------------|-------------|
| `_id` | String | ✅ `@Id` | ObjectId |
| `titulo` | String | ✅ `@NotNull` | Título de la publicación |
| `descripcion` | String | — | Descripción libre |
| `canon_arriendo` | Long | ✅ `@NotNull` | Canon mensual en pesos COP |
| `deposito` | Long | — | Depósito (pesos COP) |
| `requisitos` | String | — | Requisitos del arrendador |
| `seguro_requerido` | Boolean | — | Si requiere seguro |
| `datacredito_requerido` | Boolean | — | Si requiere datacredito |
| `fecha_disponible` | Date (LocalDate) | — | Fecha desde la que está disponible |
| `estado` | Enum | ✅ `@NotNull` | Ver `EstadoPublicacion` abajo |
| `permite_roomies` | Boolean | ✅ `@NotNull` | Permite compañeros de habitación |
| `acepta_mascotas` | Boolean | ✅ `@NotNull` | Acepta mascotas |
| `permite_fumadores` | Boolean | ✅ `@NotNull` | Permite fumadores |
| `permite_ninos` | Boolean | ✅ `@NotNull` | Permite niños |
| `permite_visitas` | Boolean | ✅ `@NotNull` | Permite visitas |
| `permite_parejas` | Boolean | ✅ `@NotNull` | Permite parejas |
| `solicitudes` | @DBRef → Set\<solicitud_arriendo\> | — | Solicitudes recibidas |
| `inmueble` | @DBRef → inmueble | — | Inmueble al que pertenece |
| + AUDIT | — | — | Campos de auditoría |

**EstadoPublicacion**:
```
BORRADOR → PUBLICADA → VISITA_AGENDADA → POSTULANTE_SELECCIONADO
→ RESERVADA → CONTRATO_EN_FIRMA → ARRENDADA → FINALIZADA → ARCHIVADA
```

---

## 5. `multimedia_inmueble` — Fotos y videos de inmuebles

| Campo MongoDB | Tipo Java | Constraints | Descripción |
|---------------|-----------|-------------|-------------|
| `_id` | String | ✅ `@Id` | ObjectId |
| `url_media` | String | ✅ `@NotNull` | URL del archivo (ej: `/uploads/uuid.png`) |
| `tipo_media` | String | ✅ `@NotNull` | MIME type (ej: `image/png`, `video/mp4`) |
| `principal` | Boolean | ✅ `@NotNull` | Si es la foto principal del inmueble |
| `titulo` | String | — | Descripción opcional de la foto |
| `inmueble` | @DBRef → inmueble | — | Inmueble al que pertenece |
| + AUDIT | — | — | Campos de auditoría |

**Archivos físicos**: almacenados en volumen Docker `roomrent-uploads-data` → `/app/uploads/`  
**URL pública**: `https://room-rent.xyz/uploads/{uuid}.{ext}` (sin autenticación)

---

## 6. `solicitud_arriendo` — Solicitudes de arrendamiento

| Campo MongoDB | Tipo Java | Constraints | Descripción |
|---------------|-----------|-------------|-------------|
| `_id` | String | ✅ `@Id` | ObjectId |
| `mensaje` | String | — | Mensaje libre del solicitante |
| `acepta_terminos` | Boolean | ✅ `@NotNull` | El arrendatario aceptó términos |
| `estado` | Enum | ✅ `@NotNull` | `CREADA` `EN_REVISION` `APROBADA` `RECHAZADA` `CANCELADA` |
| `fecha_creacion` | Date (Instant) | ✅ `@NotNull` | Timestamp de creación |
| `visitas` | @DBRef → Set\<visita_programada\> | — | Visitas agendadas |
| `arrendatario` | @DBRef → perfil_usuario | — | Quien solicita |
| `publicacion` | @DBRef → publicacion_inmueble | — | Publicación solicitada |
| + AUDIT | — | — | Campos de auditoría |

---

## 7. `visita_programada` — Visitas a inmuebles

| Campo MongoDB | Tipo Java | Constraints | Descripción |
|---------------|-----------|-------------|-------------|
| `_id` | String | ✅ `@Id` | ObjectId |
| `fecha_solicitada` | Date (Instant) | ✅ `@NotNull` | Fecha/hora propuesta por el arrendatario |
| `fecha_confirmada` | Date (Instant) | — | Fecha/hora confirmada por el arrendador |
| `notas` | String | — | Observaciones adicionales |
| `estado` | Enum | ✅ `@NotNull` | `SOLICITADA` `CONFIRMADA` `CANCELADA` `FINALIZADA` |
| `visitante` | @DBRef → perfil_usuario | — | Quien va a visitar |
| `solicitud` | @DBRef → solicitud_arriendo | — | Solicitud a la que pertenece |
| + AUDIT | — | — | Campos de auditoría |

---

## 8. `contrato_arriendo` — Contratos de arrendamiento

| Campo MongoDB | Tipo Java | Constraints | Descripción |
|---------------|-----------|-------------|-------------|
| `_id` | String | ✅ `@Id` | ObjectId |
| `numero_contrato` | String | ✅ `@NotNull` | Número único del contrato |
| `url_contrato_digital` | String | — | URL del PDF del contrato firmado |
| `fecha_inicio` | Date (LocalDate) | ✅ `@NotNull` | Inicio del arrendamiento |
| `fecha_fin` | Date (LocalDate) | ✅ `@NotNull` | Fin del arrendamiento |
| `valor_mensual` | Long | ✅ `@NotNull` | Canon mensual acordado (COP) |
| `valor_deposito` | Long | — | Depósito acordado (COP) |
| `estado` | Enum | ✅ `@NotNull` | `BORRADOR` `PENDIENTE_FIRMA` `VIGENTE` `FINALIZADO` `CANCELADO` |
| `fecha_firma` | Date (Instant) | — | Timestamp de firma |
| `arrendador` | @DBRef → perfil_usuario | — | Propietario/arrendador |
| `arrendatario` | @DBRef → perfil_usuario | — | Inquilino/arrendatario |
| `inmueble` | @DBRef → inmueble | — | Inmueble arrendado |
| + AUDIT | — | — | Campos de auditoría |

---

## 9. `calificacion` — Calificaciones entre usuarios

| Campo MongoDB | Tipo Java | Constraints | Descripción |
|---------------|-----------|-------------|-------------|
| `_id` | String | ✅ `@Id` | ObjectId |
| `tipo_calificacion` | Enum | ✅ `@NotNull` | `ARRENDADOR_A_ARRENDATARIO` `ARRENDATARIO_A_ARRENDADOR` `ARRENDATARIO_A_ROOMIE` `ROOMIE_A_ARRENDATARIO` |
| `puntaje` | Integer | ✅ `@NotNull` `@Min(1)` `@Max(5)` | Puntaje del 1 al 5 |
| `comentario` | String | — | Comentario de texto libre |
| `fecha_creacion` | Date (Instant) | ✅ `@NotNull` | Fecha de la calificación |
| `visible` | Boolean | ✅ `@NotNull` | Si es visible públicamente |
| `autor` | @DBRef → perfil_usuario | — | Quien califica |
| `calificado` | @DBRef → perfil_usuario | — | Quien recibe la calificación |
| `contrato` | @DBRef → contrato_arriendo | — | Contrato origen de la calificación |
| + AUDIT | — | — | Campos de auditoría |

---

## 10. `documento_usuario` — Documentos adjuntos del usuario

| Campo MongoDB | Tipo Java | Constraints | Descripción |
|---------------|-----------|-------------|-------------|
| `_id` | String | ✅ `@Id` | ObjectId |
| `tipo_documento` | Enum | ✅ `@NotNull` | `CC` `CE` `TI` `PASSPORT` `NIT` `OTRO` |
| `nombre_documento` | String | ✅ `@NotNull` | Nombre descriptivo del documento |
| `url_archivo` | String | ✅ `@NotNull` | URL de descarga del archivo |
| `tipo_mime` | String | — | MIME type del archivo |
| `tamano_archivo` | Long | — | Tamaño en bytes |
| `fecha_carga` | Date (Instant) | ✅ `@NotNull` | Timestamp de carga |
| `aprobado` | Boolean | — | Si fue aprobado por un admin |
| `observaciones` | String | — | Notas del admin sobre el documento |
| `perfilUsuario` | @DBRef → perfil_usuario | — | Perfil al que pertenece |
| + AUDIT | — | — | Campos de auditoría |

---

## 11. `publicacion_roomie` — Publicaciones de búsqueda de roomie

| Campo MongoDB | Tipo Java | Constraints | Descripción |
|---------------|-----------|-------------|-------------|
| `_id` | String | ✅ `@Id` | ObjectId |
| `titulo` | String | ✅ `@NotNull` | Título de la publicación |
| `nombre_habitacion` | String | ✅ `@NotNull` | Nombre/descripción de la habitación |
| `valor_mensual` | Long | ✅ `@NotNull` | Valor mensual (COP) |
| `servicios_incluidos` | String | — | Qué servicios están incluidos |
| `espacios_compartidos` | String | — | Qué espacios se comparten |
| `genero_preferido` | Enum | — | `MASCULINO` `FEMENINO` `OTRO` `PREFIERO_NO_DECIR` |
| `fecha_disponible` | Date (LocalDate) | — | Fecha desde la que está disponible |
| `estado` | Enum | ✅ `@NotNull` | `EstadoPublicacion` (mismos valores que publicacion_inmueble) |
| `solicitudes` | @DBRef → Set\<solicitud_roomie\> | — | Postulaciones recibidas |
| `arrendatario` | @DBRef → perfil_usuario | — | Quien publica la habitación |
| `inmueble` | @DBRef → inmueble | — | Inmueble donde está la habitación |
| + AUDIT | — | — | Campos de auditoría |

---

## 12. `solicitud_roomie` — Postulaciones a roomie

| Campo MongoDB | Tipo Java | Constraints | Descripción |
|---------------|-----------|-------------|-------------|
| `_id` | String | ✅ `@Id` | ObjectId |
| `mensaje` | String | — | Mensaje del postulante |
| `referencias` | String | — | Referencias del postulante |
| `estado` | Enum | ✅ `@NotNull` | `CREADA` `EN_REVISION` `APROBADA` `RECHAZADA` `CANCELADA` |
| `fecha_creacion` | Date (Instant) | ✅ `@NotNull` | Timestamp de postulación |
| `postulante` | @DBRef → perfil_usuario | — | Quien se postula |
| `publicacionRoomie` | @DBRef → publicacion_roomie | — | Publicación a la que se postula |
| + AUDIT | — | — | Campos de auditoría |

---

## 13. `jhi_authority` — Roles del sistema

Colección simple, creada por Mongock migration 001.

| Campo MongoDB | Tipo | Descripción |
|---------------|------|-------------|
| `_id` | String | Nombre del rol (PK) |

**Valores**: `ROLE_ADMIN`, `ROLE_USER`, `ROLE_ARRENDADOR`, `ROLE_ARRENDATARIO`

---

## Resumen de colecciones

| # | Colección | Campos `@NotNull` | Relaciones `@DBRef` |
|---|-----------|-------------------|---------------------|
| 1 | `jhi_user` | login, password, activated | authorities (Set) |
| 2 | `perfil_usuario` | tipoDoc, numDoc, primerNombre, primerApellido, fechaNacimiento, telefono, ciudad, verificado, habilitadoRoomie, estado | usuario, documentoses, inmuebleses |
| 3 | `inmueble` | nombre, direccion, ciudad, barrio, tipoInmueble, numeroHabitaciones, numeroBanos | publicaciones, multimedia, contratos, propietario |
| 4 | `publicacion_inmueble` | titulo, canonArriendo, estado, permiteRoomies, aceptaMascotas, permiteFumadores, permiteNinos, permiteVisitas, permiteParejas | solicitudes, inmueble |
| 5 | `multimedia_inmueble` | urlMedia, tipoMedia, principal | inmueble |
| 6 | `solicitud_arriendo` | aceptaTerminos, estado, fechaCreacion | visitas, arrendatario, publicacion |
| 7 | `visita_programada` | fechaSolicitada, estado | visitante, solicitud |
| 8 | `contrato_arriendo` | numeroContrato, fechaInicio, fechaFin, valorMensual, estado | arrendador, arrendatario, inmueble |
| 9 | `calificacion` | tipoCalificacion, puntaje(1-5), fechaCreacion, visible | autor, calificado, contrato |
| 10 | `documento_usuario` | tipoDocumento, nombreDocumento, urlArchivo, fechaCarga | perfilUsuario |
| 11 | `publicacion_roomie` | titulo, nombreHabitacion, valorMensual, estado | solicitudes, arrendatario, inmueble |
| 12 | `solicitud_roomie` | estado, fechaCreacion | postulante, publicacionRoomie |
| 13 | `jhi_authority` | _id (nombre del rol) | — |

---

## Enumeraciones

| Enum | Valores |
|------|---------|
| `TipoInmueble` | `APARTAMENTO` `CASA` `HABITACION` `APARTAESTUDIO` `LOCAL` `OFICINA` `OTRO` |
| `EstadoPublicacion` | `BORRADOR` `PUBLICADA` `VISITA_AGENDADA` `POSTULANTE_SELECCIONADO` `RESERVADA` `CONTRATO_EN_FIRMA` `ARRENDADA` `FINALIZADA` `ARCHIVADA` |
| `EstadoSolicitud` | `CREADA` `EN_REVISION` `APROBADA` `RECHAZADA` `CANCELADA` |
| `EstadoVisita` | `SOLICITADA` `CONFIRMADA` `CANCELADA` `FINALIZADA` |
| `EstadoContrato` | `BORRADOR` `PENDIENTE_FIRMA` `VIGENTE` `FINALIZADO` `CANCELADO` |
| `EstadoUsuario` | `PENDIENTE_VERIFICACION` `ACTIVO` `INACTIVO` `SUSPENDIDO` `BLOQUEADO` `BANEADO` |
| `TipoDocumento` | `CC` `CE` `TI` `PASSPORT` `NIT` `OTRO` |
| `TipoCalificacion` | `ARRENDADOR_A_ARRENDATARIO` `ARRENDATARIO_A_ARRENDADOR` `ARRENDATARIO_A_ROOMIE` `ROOMIE_A_ARRENDATARIO` |
| `Genero` | `MASCULINO` `FEMENINO` `OTRO` `PREFIERO_NO_DECIR` |

---

## Diagrama de relaciones

```
jhi_authority ←── jhi_user ──→ perfil_usuario
                                    │
                    ┌───────────────┼───────────────┐
                    ▼               ▼               ▼
               inmueble      documento_usuario  (arrendador/arrendatario)
                    │                                │
          ┌─────────┼──────────┐                     │
          ▼         ▼          ▼                     │
  publicacion_  multimedia_  contrato_arriendo ◄─────┘
  inmueble      inmueble          │
      │                           ▼
      ▼                      calificacion
  solicitud_arriendo
      │
      ▼
  visita_programada

  publicacion_roomie ──→ solicitud_roomie
      │
      └──→ inmueble
      └──→ perfil_usuario
```

---

## Ejemplo de request válido por colección

### POST /api/inmuebles
```json
{
  "nombre": "Apartamento Chapinero",
  "direccion": "Calle 53 #13-45 Apto 301",
  "ciudad": "Bogotá",
  "barrio": "Chapinero Alto",
  "tipoInmueble": "APARTAMENTO",
  "numeroHabitaciones": 2,
  "numeroBanos": 1,
  "estrato": 4,
  "areaMetrosCuadrados": 65.0
}
```

### POST /api/publicacion-inmuebles
```json
{
  "titulo": "Apto disponible inmediato",
  "canonArriendo": 1800000,
  "estado": "PUBLICADA",
  "permiteRoomies": false,
  "aceptaMascotas": false,
  "permiteFumadores": false,
  "permiteNinos": true,
  "permiteVisitas": true,
  "permiteParejas": true,
  "inmueble": { "id": "<inmueble_id>" }
}
```

### POST /api/multimedia-inmuebles
```json
{
  "urlMedia": "/uploads/uuid.png",
  "tipoMedia": "image/png",
  "principal": true,
  "inmueble": { "id": "<inmueble_id>" }
}
```

### POST /api/solicitud-arriendos
```json
{
  "mensaje": "Estoy interesado en el apartamento",
  "aceptaTerminos": true,
  "estado": "CREADA",
  "fechaCreacion": "2026-07-02T20:00:00Z",
  "publicacion": { "id": "<publicacion_id>" }
}
```

### PATCH /api/inmuebles/{id}
```json
{
  "id": "<mismo_id_del_path>",
  "estrato": 3
}
```
> **IMPORTANTE**: el campo `id` en el body debe coincidir con el `{id}` del path (comportamiento JHipster).
