# 18 — Plan de Migración Técnica

> **Tipo:** Documento de arquitectura y planificación  
> **Estado:** Pendiente de aprobación antes de implementar  
> **Fecha:** 2026-07-02  
> **Reglas de negocio base:** `17-reglas-negocio-definitivas.md`  
>
> **RESTRICCIÓN ABSOLUTA:** Este documento no modifica código.  
> Es la hoja de ruta que debe aprobarse antes de tocar cualquier archivo del proyecto.

---

## Estado actual del proyecto

### Resumen técnico

| Componente | Estado |
|---|---|
| **Backend** | Spring Boot 4.0.6 — compila y funciona |
| **Frontend Angular** | Angular 21 — compila y funciona |
| **Portal React** | React 18 en `/portal/` — funciona |
| **Base de datos** | MongoDB — 11 colecciones activas con datos |
| **Entidades** | 11 entidades, 9 enums, full CRUD en admin |
| **Autenticación** | JWT con Spring Security — funciona |

### Estructura actual de archivos críticos

```
src/main/java/com/roomrent/app/
  domain/                         ← 11 clases de dominio + 9 enums
  web/rest/                       ← 11 REST Resources
  service/                        ← 11 Services + 11 ServiceImpl
  repository/                     ← Implícito (MongoDB Spring Data)

src/main/webapp/app/
  entities/                       ← 11 módulos Angular (list/detail/edit/delete)
  enumerations/                   ← TypeScript enums para el frontend

src/main/resources/static/portal/ ← React portal compilado
jhipster-jdl.jdl                  ← Definición actual del modelo
.yo-rc.json                       ← Configuración JHipster (NO TOCAR)
```

---

## Análisis de las nuevas entidades aprobadas

### NE-01 — Notificacion

**¿Por qué existe?**  
Todos los flujos documentados (solicitud, visita, contrato, calificación) describen eventos donde "el sistema notifica". Actualmente estas notificaciones no existen. Sin esta entidad, los usuarios no saben que algo cambió sin recargar la página manualmente.

**¿Qué problema resuelve?**  
Elimina la dependencia de que el usuario refresque la pantalla. Habilita alertas in-app inmediatas ante cualquier cambio de estado relevante.

**¿Qué entidades actuales modifica?**  
Ninguna directamente. Es una entidad nueva que referencia `PerfilUsuario`.

**Relaciones:**
- `ManyToOne → PerfilUsuario` (destinatario)

**Reglas de negocio que implementa:**
- 16 tipos de notificación definidos en `17-reglas-negocio-definitivas.md`
- Las notificaciones no se eliminan, solo se marcan como leídas
- Se generan automáticamente en cada transición de estado relevante

**Pantallas afectadas:**
- Angular: Nuevo módulo `notificacion/` con lista de notificaciones del usuario
- Campana de notificaciones en el navbar (número de no leídas)
- React portal: Indicador de notificaciones al autenticarse

**APIs afectadas:**
- Nueva: `GET /api/notificaciones` (propias del usuario autenticado)
- Nueva: `PUT /api/notificaciones/{id}/leer`
- Nueva: `PUT /api/notificaciones/leer-todas`

**CRUD a modificar:**
- Los 11 `ServiceImpl` existentes deben inyectar `NotificacionService` para generar notificaciones en cada cambio de estado.

**Riesgos:**
- **MEDIO:** Inyectar el `NotificacionService` en 11 servicios existentes requiere tocar archivos que actualmente funcionan.
- **BAJO:** La entidad en sí es nueva y no afecta datos existentes.

**¿Puede generarse con JHipster?** SÍ — entidad completamente nueva.

---

### NE-02 — AcuerdoConvivencia

**¿Por qué existe?**  
Las calificaciones roomie (`ARRENDATARIO_A_ROOMIE`, `ROOMIE_A_ARRENDATARIO`) necesitan un ancla formal. El `ContratoArriendo` es incorrecto para este caso porque el roomie no tiene contrato con el propietario. El `AcuerdoConvivencia` es el equivalente del contrato para la relación arrendatario↔roomie.

**¿Qué problema resuelve?**  
- Da un ciclo de vida formal al roomie post-aprobación (ACTIVO → FINALIZADO)
- Permite anclar calificaciones roomie correctamente
- Registra fechas de ingreso y salida del roomie

**¿Qué entidades actuales modifica?**  
- `Calificacion`: Se agrega campo `acuerdoConvivencia` nullable y `tipoAncla` enum
- `SolicitudRoomie`: Al pasar a APROBADA, se genera automáticamente un `AcuerdoConvivencia`

**Relaciones:**
- `ManyToOne → PerfilUsuario` (anfitrión)
- `ManyToOne → PerfilUsuario` (roomie)
- `ManyToOne → PublicacionRoomie`
- `ManyToOne → Inmueble`
- ← `OneToMany` desde `Calificacion`

**Reglas de negocio que implementa:**
- Ciclo de vida: BORRADOR → ACTIVO → FINALIZADO / CANCELADO
- Al pasar a FINALIZADO: habilita calificaciones roomie (15 días)
- `fechaLimiteCalificacion` calculada automáticamente

**Pantallas afectadas:**
- Angular: Nuevo módulo `acuerdo-convivencia/` para el admin
- Futuro: Vista "Mis roomies activos" para el arrendatario

**APIs afectadas:**
- Nueva: `GET /api/acuerdos-convivencia`
- Nueva: `POST /api/acuerdos-convivencia`
- Nueva: `PUT /api/acuerdos-convivencia/{id}` (cambiar estado)

**CRUD a modificar:**
- `SolicitudRoomieServiceImpl`: Al aprobar solicitud, crear `AcuerdoConvivencia` automáticamente
- `CalificacionResource`/`CalificacionServiceImpl`: Validar que el ancla corresponde al tipo

**Riesgos:**
- **ALTO:** Modifica `Calificacion` (entidad existente con datos). El campo `contrato` pasa de requerido a nullable.
- **MEDIO:** Requiere lógica en `SolicitudRoomieServiceImpl` para crear el acuerdo automáticamente.

**¿Puede generarse con JHipster?** SÍ para la entidad base. Los cambios en `Calificacion` son manuales.

---

### NE-03 — OcupacionUnidad

**¿Por qué existe?**  
El sistema actualmente no registra quién vive en cada unidad en un momento dado, ni cuándo ingresó o salió. Para auditoría, historial de ocupantes y consultas como "¿quién vivió aquí entre 2024 y 2025?", se necesita este registro.

**¿Qué problema resuelve?**  
- Historial completo de ocupantes por unidad
- Separación entre "quién firmó el contrato" y "quién habitó físicamente"
- Base para el reporte de historial de ocupación

**¿Qué entidades actuales modifica?**  
Ninguna directamente. Es referenciada por `ContratoArriendo` y `AcuerdoConvivencia`.

**Relaciones:**
- `ManyToOne → Inmueble`
- `ManyToOne → PerfilUsuario` (ocupante)
- `ManyToOne → ContratoArriendo` (nullable — para arrendatarios)
- `ManyToOne → AcuerdoConvivencia` (nullable — para roomies)

**Reglas de negocio que implementa:**
- Se crea cuando `ContratoArriendo` → VIGENTE o `AcuerdoConvivencia` → ACTIVO
- `fechaSalida` se registra cuando el contrato/acuerdo → FINALIZADO o CANCELADO
- Estado: ACTIVA | FINALIZADA | CANCELADA

**Pantallas afectadas:**
- Angular: Nueva vista "Historial de ocupantes" por inmueble (admin)

**APIs afectadas:**
- Nueva: `GET /api/ocupaciones-unidad?inmuebleId=` (historial por unidad)
- Nueva: `GET /api/ocupaciones-unidad?ocupanteId=` (por persona)

**Riesgos:**
- **BAJO:** Entidad nueva, no modifica existentes.
- **MEDIO:** La lógica de creación automática debe integrarse en `ContratoArriendoServiceImpl` y en el futuro `AcuerdoConvivenciaServiceImpl`.

**¿Puede generarse con JHipster?** SÍ — entidad completamente nueva.

---

### NE-04 — HistorialPrecio

**¿Por qué existe?**  
Cuando el arrendador edita `canonArriendo` en una `PublicacionInmueble` existente, el precio anterior se pierde permanentemente. No hay forma de saber si un arrendatario vio la publicación a $1.2M y luego el precio subió a $1.5M.

**¿Qué problema resuelve?**  
- Trazabilidad de cambios de precio
- Protección al arrendatario en disputas
- Análisis histórico de precios por zona

**¿Qué entidades actuales modifica?**  
- `PublicacionInmuebleServiceImpl`: Al hacer UPDATE de `canonArriendo`, primero crear registro en `HistorialPrecio`.

**Relaciones:**
- `ManyToOne → PublicacionInmueble`
- `ManyToOne → User` (quién lo cambió)

**Reglas de negocio que implementa:**
- Se registra automáticamente antes de cada cambio de `canonArriendo`
- Es de solo lectura para todos los actores (solo el sistema puede crear registros)
- El admin puede consultarlos

**Pantallas afectadas:**
- Angular: Vista "Historial de precios" en el detalle de publicación (admin)

**APIs afectadas:**
- Nueva: `GET /api/historial-precios?publicacionId=` (de solo lectura)

**Riesgos:**
- **BAJO:** Entidad nueva. El único riesgo es olvidar interceptar el UPDATE del campo `canonArriendo`.
- **BAJO-MEDIO:** Requiere modificar `PublicacionInmuebleServiceImpl`.

**¿Puede generarse con JHipster?** SÍ — entidad completamente nueva.

---

### NE-05 — Propiedad (Fase 2 — No implementar aún)

**¿Por qué existe?**  
Para agrupar múltiples `Inmueble` (unidades) bajo un mismo contenedor físico (edificio, conjunto, casa de habitaciones).

**Estado:** Diferido a Fase 2. No se implementa en esta iteración.

**Impacto cuando se implemente:**
- Nueva relación: `Inmueble.propiedad → Propiedad` (ManyToOne, nullable en Fase 1 para compatibilidad)
- Nuevo módulo Angular
- Nueva API REST
- **SIN impacto en datos existentes** si el campo es nullable

---

## Análisis de entidades existentes

### EA-01 — PerfilUsuario

| Pregunta | Respuesta |
|---|---|
| ¿Debe permanecer igual? | Mayormente. Agregar campos de auditoría. |
| ¿Debe modificarse? | Sí — campos opcionales nuevos |
| ¿Debe dividirse? | No |
| ¿Debe fusionarse? | No |
| ¿Debe agregar relaciones? | Sí — `OneToMany → Notificacion` |
| ¿Debe agregar nuevos estados? | No |

**Campos nuevos:**
- `urlFotoPerfil: String` (nullable) — foto de perfil

**Campos a modificar:**
- `verificado: Boolean` — Actualmente `required`. Se mantiene, pero la regla de cuándo cambia a `true` se implementa en el servicio.

**Impacto en archivos:**
- `PerfilUsuario.java`: Agregar `urlFotoPerfil`
- `perfil-usuario-form.component.html`: Agregar campo en formulario Angular
- `jhipster-jdl.jdl`: Agregar campo

**Nivel de riesgo:** BAJO — campo nullable, no afecta datos existentes.

---

### EA-02 — DocumentoUsuario

| Pregunta | Respuesta |
|---|---|
| ¿Debe permanecer igual? | No |
| ¿Debe modificarse? | Sí |
| ¿Debe dividirse? | No |
| ¿Debe fusionarse? | No |
| ¿Debe agregar nuevos estados? | Sí — reemplazar Boolean `aprobado` por enum |

**Campos nuevos:**
- `estadoDocumento: EstadoDocumento` (nuevo enum: PENDIENTE | APROBADO | RECHAZADO)
- `fechaAprobacion: Instant` (nullable)
- `aprobadoPor: String` (login del admin, nullable)

**Campo a deprecar:**
- `aprobado: Boolean` → Reemplazar por `estadoDocumento`. **RIESGO:** Hay datos existentes con `aprobado = true/false/null`. Requiere migración de datos.

**Impacto en archivos:**
- `DocumentoUsuario.java`
- `DocumentoUsuarioServiceImpl.java`
- `documento-usuario-form.component.html`
- `documento-usuario-detail.component.html`

**Nivel de riesgo:** MEDIO-ALTO — el campo `aprobado` está en MongoDB y hay datos existentes.

**Estrategia recomendada:** Mantener `aprobado` en el modelo pero agregar `estadoDocumento` como campo adicional. La lógica de negocio usa `estadoDocumento`. El campo `aprobado` queda como legado.

---

### EA-03 — Inmueble

| Pregunta | Respuesta |
|---|---|
| ¿Debe permanecer igual? | No — agregar campos |
| ¿Debe modificarse? | Sí |
| ¿Debe dividirse? | No (Propiedad es una adición futura, no una división) |
| ¿Debe agregar relaciones? | Sí — preparar para Propiedad (nullable) |
| ¿Debe agregar nuevos estados? | No tiene estado en el modelo actual |

**Campos nuevos:**
- `activo: Boolean` (default: `true`) — Para desactivar sin eliminar
- `amenidades: String` (nullable) — Lista separada por comas o JSON
- `codigoCatastral: String` (nullable) — Referencia oficial
- `propiedadId: String` (nullable) — ID de futura entidad Propiedad (ManyToOne en Fase 2)

**Impacto en archivos:**
- `Inmueble.java`
- `InmuebleServiceImpl.java`: Lógica de validación al eliminar
- `inmueble-form.component.html`
- `jhipster-jdl.jdl`

**Nivel de riesgo:** BAJO — campos nullable, no afecta datos existentes.

---

### EA-04 — PublicacionInmueble

| Pregunta | Respuesta |
|---|---|
| ¿Debe permanecer igual? | No — es una de las entidades más afectadas |
| ¿Debe modificarse? | Sí |
| ¿Debe agregar nuevos estados? | Sí — 7 nuevos valores en EstadoPublicacion |

**Cambios en el enum EstadoPublicacion:**

| Estado actual | Estado nuevo |
|---|---|
| BORRADOR | BORRADOR (sin cambio) |
| PUBLICADO | PUBLICADA (renombrar) |
| PAUSADO | PAUSADA (renombrar) |
| ARRENDADO | ARRENDADA (renombrar) |
| FINALIZADO | FINALIZADA (renombrar) |
| — | VISITA_AGENDADA (nuevo) |
| — | POSTULANTE_SELECCIONADO (nuevo) |
| — | RESERVADA (nuevo) |
| — | CONTRATO_EN_FIRMA (nuevo) |
| — | ARCHIVADA (nuevo) |

> **ALERTA CRÍTICA:** Si se renombran los valores existentes (PUBLICADO → PUBLICADA), los documentos MongoDB que tienen el valor antiguo quedarán inválidos. **Los datos existentes deben migrarse o los nuevos valores deben ser completamente distintos.** Recomendación: NO renombrar los valores existentes. Solo agregar los nuevos. Mantener compatibilidad con datos actuales.

**Campos nuevos:**
- `republicarAutomaticamente: Boolean` (default: `false`)
- `fechaCambioEstado: Instant` (nullable)
- `estadoAnterior: String` (nullable)
- `motivoCambioEstado: String` (nullable)

**Impacto en archivos:**
- `EstadoPublicacion.java` (enum) — solo agregar valores, no renombrar
- `PublicacionInmueble.java` — agregar campos
- `PublicacionInmuebleServiceImpl.java` — lógica de máquina de estados
- `publicacion-inmueble-form.component.html` — nuevos campos
- `publicacion-inmueble-list.component.html` — mostrar nuevos estados
- `jhipster-jdl.jdl`
- **React portal**: Mostrar badge "Reservada", ocultar botón solicitar

**Nivel de riesgo:** ALTO — El enum de estados afecta datos existentes en MongoDB. La máquina de estados requiere lógica compleja en el servicio.

---

### EA-05 — MultimediaInmueble

| Pregunta | Respuesta |
|---|---|
| ¿Debe permanecer igual? | Mayormente |
| ¿Debe modificarse? | Sí — agregar campos de ordenamiento y auditoría |

**Campos nuevos:**
- `orden: Integer` (nullable, default: 0) — posición en la galería
- `fechaCarga: Instant` (nullable)

**Nivel de riesgo:** BAJO — campos nullable.

---

### EA-06 — SolicitudArriendo

| Pregunta | Respuesta |
|---|---|
| ¿Debe modificarse? | Sí |
| ¿Debe agregar nuevos estados? | Sí — EN_ESPERA |

**Cambios en EstadoSolicitud:**
- Agregar: `EN_ESPERA` (cuando la publicación pasa a RESERVADA)

**Campos nuevos:**
- `razonRechazo: String` (nullable)
- `fechaRespuesta: Instant` (nullable)
- `fechaCambioEstado: Instant` (nullable)
- `estadoAnterior: String` (nullable)

**Reglas de negocio:**
- Al crear contrato VIGENTE: todas las solicitudes CREADA/EN_REVISION/EN_ESPERA → RECHAZADA (automático)
- Al pasar publicación a RESERVADA: solicitudes CREADA/EN_REVISION → EN_ESPERA (automático)

**Impacto en archivos:**
- `EstadoSolicitud.java` (enum)
- `SolicitudArriendo.java`
- `SolicitudArriendoServiceImpl.java` — lógica de auto-rechazo
- `solicitud-arriendo-list.component.html`
- `solicitud-arriendo-form.component.html`

**Nivel de riesgo:** MEDIO — el nuevo estado EN_ESPERA es aditivo. La lógica de auto-rechazo es nueva pero no modifica datos existentes.

---

### EA-07 — VisitaProgramada

| Pregunta | Respuesta |
|---|---|
| ¿Debe modificarse? | Sí — agregar campos de auditoría |

**Campos nuevos:**
- `razonCancelacion: String` (nullable)
- `fechaCambioEstado: Instant` (nullable)

**Nivel de riesgo:** BAJO — campos nullable.

---

### EA-08 — ContratoArriendo

| Pregunta | Respuesta |
|---|---|
| ¿Debe modificarse? | Sí — es una de las entidades más críticas |
| ¿Debe agregar relaciones? | Sí — OcupacionUnidad |

**Campos nuevos:**
- `fechaLimiteCalificacion: LocalDate` (nullable, calculado al finalizar)
- `razonCancelacion: String` (nullable)
- `penalizacion: Long` (nullable)
- `fechaCambioEstado: Instant` (nullable)
- `estadoAnterior: String` (nullable)
- `motivoCambioEstado: String` (nullable)

**Reglas de negocio a implementar en el servicio:**
- Al pasar a VIGENTE: verificar que no hay otro VIGENTE para el mismo inmueble
- Al pasar a VIGENTE: auto-rechazar otras solicitudes
- Al pasar a VIGENTE: crear `OcupacionUnidad`
- Al pasar a FINALIZADO/CANCELADO: calcular `fechaLimiteCalificacion = hoy + 15 días`
- Al pasar a FINALIZADO/CANCELADO: actualizar estado de `PublicacionInmueble`
- Al pasar a FINALIZADO/CANCELADO: cerrar `OcupacionUnidad`
- Al pasar a FINALIZADO/CANCELADO: generar notificaciones

**Impacto en archivos:**
- `ContratoArriendo.java`
- `ContratoArriendoServiceImpl.java` — múltiples reglas complejas
- `contrato-arriendo-form.component.html`
- `contrato-arriendo-detail.component.html`
- `jhipster-jdl.jdl`

**Nivel de riesgo:** ALTO — entidad central del negocio. Múltiples efectos en cadena al cambiar de estado.

---

### EA-09 — PublicacionRoomie

| Pregunta | Respuesta |
|---|---|
| ¿Debe modificarse? | Sí |

**Campos nuevos:**
- `areaHabitacion: Double` (nullable)
- `banoPrivado: Boolean` (nullable)
- `fechaCambioEstado: Instant` (nullable)

**Nivel de riesgo:** BAJO.

---

### EA-10 — SolicitudRoomie

| Pregunta | Respuesta |
|---|---|
| ¿Debe modificarse? | Sí |
| ¿Qué relaciones agrega? | Al aprobar, genera automáticamente un `AcuerdoConvivencia` |

**Campos nuevos:**
- `razonRechazo: String` (nullable)
- `fechaRespuesta: Instant` (nullable)

**Reglas de negocio:**
- Al pasar a APROBADA: crear `AcuerdoConvivencia` automáticamente

**Nivel de riesgo:** MEDIO — la generación automática del acuerdo requiere lógica en el servicio.

---

### EA-11 — Calificacion

| Pregunta | Respuesta |
|---|---|
| ¿Debe modificarse? | Sí — es la modificación más delicada a una entidad existente |
| ¿Qué relaciones modifica? | `contrato` pasa de requerido a nullable |

**Cambios:**
- Campo `contrato` (actualmente `required`): Se hace nullable
- Nuevo campo: `tipoAncla: TipoAncla` (enum: CONTRATO_ARRIENDO | ACUERDO_CONVIVENCIA)
- Nueva relación: `ManyToOne → AcuerdoConvivencia` (nullable)

**Regla de validación:**
- Si `tipoAncla = CONTRATO_ARRIENDO`: `contrato` debe ser no-null
- Si `tipoAncla = ACUERDO_CONVIVENCIA`: `acuerdoConvivencia` debe ser no-null

> **ALERTA:** Los datos existentes en MongoDB con `calificacion.contrato` tienen un valor. Al hacer nullable el campo, los documentos existentes siguen siendo válidos. El riesgo está en la validación Java: si el campo se marca `@NotNull`, los documentos existentes fallarán la carga. Si se quita `@NotNull`, pasa a ser opcional globalmente.

**Estrategia:** Agregar `tipoAncla` con default `CONTRATO_ARRIENDO` para los registros existentes, y hacer `contrato` nullable solo en el modelo Java. La validación se hace en el `ServiceImpl`.

**Nivel de riesgo:** ALTO — hay datos existentes con estructura diferente. Requiere migración cuidadosa.

---

## Análisis de impacto sobre infraestructura JHipster

### JDL (`jhipster-jdl.jdl`)

**¿Qué cambia?**
- Nuevas entidades: Notificacion, AcuerdoConvivencia, OcupacionUnidad, HistorialPrecio
- Nuevos enums: TipoAncla, EstadoAcuerdo, TipoOcupante, EstadoOcupacion, TipoNotificacion, EstadoDocumento
- Nuevos valores en enums existentes: EstadoPublicacion (+7 valores), EstadoSolicitud (+1 valor)
- Nuevos campos en entidades existentes
- `application { entities [...] }`: agregar las 4 nuevas entidades

**Riesgo de actualizar el JDL:** BAJO — el JDL es solo documentación/input. Actualizarlo no modifica código automáticamente.

---

### `.yo-rc.json`

**¿Qué cambia?**
- Agregar las 4 nuevas entidades a la lista `"entities": [...]`

**Riesgo:** BAJO si solo se actualiza la lista de entidades. NO modificar otros campos (authenticationType, databaseType, etc.). El archivo es leído por JHipster al regenerar.

---

### MongoDB

**¿Qué cambia?**
- MongoDB es esquema libre (schemaless). Agregar campos a entidades existentes NO requiere migración de esquema.
- Los documentos existentes simplemente no tendrán los nuevos campos (retornan `null`).
- Los nuevos valores de enum que se agregan son strings en MongoDB — no afectan documentos existentes.

**Riesgo de datos:** BAJO para campos nuevos nullable.  
**Riesgo de datos:** MEDIO para el campo `Calificacion.contrato` que pasa de string a nullable.  
**Riesgo de datos:** ALTO si se renombran valores de enum (ej. PUBLICADO → PUBLICADA).

**Índices a crear (manual — no los crea JHipster):**
- `{inmueble: 1, estado: 1}` parcial con condición `{estado: "VIGENTE"}` en colección `contrato_arriendo`
- `{inmueble: 1, principal: 1}` parcial con condición `{principal: true}` en `multimedia_inmueble`
- `{usuario: 1, leida: 1}` en `notificacion` para consultas eficientes de no-leídas

---

### Spring Boot

**¿Qué cambia?**
- Nuevas clases de dominio para 4 entidades
- Nuevos enums
- Modificaciones a 11 clases existentes (campos nuevos)
- Nuevos Repositories para 4 entidades
- Nuevos Services para 4 entidades
- Nuevos REST Resources para 4 entidades
- Modificaciones a 4 ServiceImpl críticos (ContratoArriendo, SolicitudArriendo, PublicacionInmueble, SolicitudRoomie)
- Nuevo scheduler (cron job) para expiración automática de contratos

**Riesgo:** MEDIO para entidades nuevas. ALTO para servicios con lógica de máquina de estados.

---

### Angular (frontend admin)

**¿Qué cambia?**
- 4 nuevos módulos de entidad generados por JHipster
- Actualización de enums TypeScript (aditivo, bajo riesgo)
- Actualización de formularios en 8 entidades existentes (nuevos campos)
- Actualización de listas para mostrar nuevos estados y campos

**Riesgo:** BAJO para nuevos módulos. MEDIO para modificaciones en formularios existentes.

---

### React (portal público)

**¿Qué cambia?**
- Mostrar estado "Reservada" en tarjetas de publicación
- Ocultar botón "Solicitar" cuando la publicación está en RESERVADA, CONTRATO_EN_FIRMA, ARRENDADA
- Mostrar badge de estado con colores por estado
- Futuro: sección de notificaciones

**Riesgo:** BAJO — son cambios de presentación, no de lógica.

---

### Spring Security

**¿Qué cambia?**
- Las nuevas APIs (`/api/notificaciones`, `/api/acuerdos-convivencia`, etc.) deben quedar protegidas con `@PreAuthorize("isAuthenticated()")`
- No se modifican los roles actuales (`ROLE_ADMIN`, `ROLE_USER`)
- Las reglas de acceso por nivel (Nivel 0-3 del doc 17) se implementan en los servicios, no en Spring Security

**Riesgo:** BAJO — adición de `@PreAuthorize` en nuevos endpoints.

---

## Plan de implementación por fases

**Principio rector:** Cada fase deja el proyecto en estado compilable y funcional. Si algo falla en una fase, se hace rollback antes de continuar.

---

### FASE 0 — Preparación y protección

**Objetivo:** Asegurar el proyecto antes de cualquier cambio.

**Actividades:**
1. Crear rama de trabajo: `git checkout -b feature/nuevas-entidades-v2`
2. Documentar commit actual como punto de restauración
3. Hacer backup de los archivos de plantilla Angular personalizados
4. Verificar que el proyecto compila limpio en el estado actual

**Archivos afectados:** Ninguno del proyecto.

**Nivel de riesgo:** NINGUNO  
**Dependencias:** Ninguna  
**Tiempo estimado:** 30 minutos  
**Rollback:** `git checkout main`

---

### FASE 1 — Actualización de enums existentes

**Objetivo:** Agregar nuevos valores a enums que ya existen. Es la operación más segura posible.

**Actividades:**
1. Agregar valores a `EstadoPublicacion.java`:  
   `VISITA_AGENDADA, POSTULANTE_SELECCIONADO, RESERVADA, CONTRATO_EN_FIRMA, ARRENDADA, FINALIZADA, ARCHIVADA`
2. Agregar valor a `EstadoSolicitud.java`:  
   `EN_ESPERA`
3. Actualizar enums TypeScript equivalentes en Angular
4. Compilar y verificar que compila sin errores

**Archivos afectados:**
- `domain/enumeration/EstadoPublicacion.java`
- `domain/enumeration/EstadoSolicitud.java`
- `webapp/app/entities/enumerations/estado-publicacion.model.ts`
- `webapp/app/entities/enumerations/estado-solicitud.model.ts`

**Nivel de riesgo:** MUY BAJO  
**Probabilidad de romper:** ~2% (solo si hay switch statements sin default)  
**Dependencias:** Fase 0  
**Tiempo estimado:** 2 horas (incluye búsqueda de referencias en el código)  
**Rollback:** Revertir los 4 archivos modificados

---

### FASE 2 — Nuevos enums

**Objetivo:** Crear los nuevos tipos de enumeración requeridos por las entidades nuevas y modificadas.

**Actividades:**
1. Crear `EstadoAcuerdo.java`: BORRADOR | ACTIVO | FINALIZADO | CANCELADO
2. Crear `TipoAncla.java`: CONTRATO_ARRIENDO | ACUERDO_CONVIVENCIA
3. Crear `TipoOcupante.java`: ARRENDATARIO | ROOMIE
4. Crear `EstadoOcupacion.java`: ACTIVA | FINALIZADA | CANCELADA
5. Crear `TipoNotificacion.java`: (16 valores del doc 17)
6. Crear `EstadoDocumento.java`: PENDIENTE | APROBADO | RECHAZADO
7. Crear equivalentes TypeScript para Angular

**Archivos afectados:** Solo nuevos archivos.

**Nivel de riesgo:** NINGUNO  
**Dependencias:** Fase 1  
**Tiempo estimado:** 2 horas  
**Rollback:** Eliminar los nuevos archivos

---

### FASE 3 — Campos nuevos en entidades existentes (backend)

**Objetivo:** Agregar campos nullable a entidades existentes. MongoDB es schemaless — no requiere migración de datos.

**Actividades (por entidad):**

| Entidad | Campos a agregar |
|---|---|
| `PerfilUsuario` | `urlFotoPerfil` |
| `DocumentoUsuario` | `estadoDocumento`, `fechaAprobacion`, `aprobadoPor` |
| `Inmueble` | `activo`, `amenidades`, `codigoCatastral` |
| `PublicacionInmueble` | `republicarAutomaticamente`, `fechaCambioEstado`, `estadoAnterior`, `motivoCambioEstado` |
| `MultimediaInmueble` | `orden`, `fechaCarga` |
| `SolicitudArriendo` | `razonRechazo`, `fechaRespuesta`, `fechaCambioEstado`, `estadoAnterior` |
| `VisitaProgramada` | `razonCancelacion`, `fechaCambioEstado` |
| `ContratoArriendo` | `fechaLimiteCalificacion`, `razonCancelacion`, `penalizacion`, `fechaCambioEstado`, `estadoAnterior`, `motivoCambioEstado` |
| `PublicacionRoomie` | `areaHabitacion`, `banoPrivado`, `fechaCambioEstado` |
| `SolicitudRoomie` | `razonRechazo`, `fechaRespuesta` |
| `Calificacion` | `tipoAncla` (campo), `acuerdoConvivencia` (relación — se agrega en Fase 5) |

**Archivos afectados:** 11 archivos `.java` de dominio.

**Nivel de riesgo:** BAJO  
**Probabilidad de romper:** ~5% (si hay constructores sin valor por defecto)  
**Dependencias:** Fases 1 y 2  
**Tiempo estimado:** 4 horas  
**Rollback:** `git checkout` de los 11 archivos modificados

---

### FASE 4 — Nuevas entidades vía JHipster

**Objetivo:** Generar el stack completo (Domain, Repository, Service, Resource, Angular) para las 4 entidades nuevas.

**Estrategia:** Actualizar el JDL con las 4 nuevas entidades y usar `jhipster import-jdl` con flag `--fork` o `--skip-conflict` para que JHipster SOLO cree archivos nuevos sin tocar los existentes.

> **ADVERTENCIA CRÍTICA:** El comando `jhipster import-jdl jhipster-jdl.jdl --force` SOBREESCRIBE archivos de entidades existentes. NO usar `--force` en esta fase. Usar `--skip-existing` o importar solo las entidades nuevas.

**Comando sugerido:**
```
jhipster entity Notificacion
jhipster entity AcuerdoConvivencia
jhipster entity OcupacionUnidad
jhipster entity HistorialPrecio
```
(uno a uno, revisando cada resultado antes de continuar)

**Entidades a generar:**
1. `Notificacion` (referencia PerfilUsuario)
2. `AcuerdoConvivencia` (referencia PerfilUsuario x2, PublicacionRoomie, Inmueble)
3. `OcupacionUnidad` (referencia Inmueble, PerfilUsuario, ContratoArriendo, AcuerdoConvivencia)
4. `HistorialPrecio` (referencia PublicacionInmueble, User)

**Archivos nuevos generados por entidad (~16 archivos por entidad):**
- `domain/NuevaEntidad.java`
- `repository/NuevaEntidadRepository.java`
- `service/NuevaEntidadService.java`
- `service/impl/NuevaEntidadServiceImpl.java`
- `web/rest/NuevaEntidadResource.java`
- `webapp/app/entities/nueva-entidad/` (7 archivos Angular)

**Archivos existentes que JHipster PUEDE tocar:**
- `webapp/app/entities/entity.routes.ts` — agrega las nuevas rutas
- `.yo-rc.json` — actualiza lista de entidades

**Nivel de riesgo:** MEDIO (riesgo principal: JHipster regenerando archivos existentes accidentalmente)  
**Probabilidad de romper:** ~20% si no se usa la estrategia correcta  
**Dependencias:** Fases 1, 2, 3  
**Tiempo estimado:** 6-8 horas  
**Rollback:** `git checkout` + eliminar archivos generados accidentalmente

---

### FASE 5 — Lógica de negocio en servicios existentes (backend)

**Objetivo:** Implementar las reglas de negocio críticas en los servicios existentes.

**Actividades por servicio:**

| Servicio | Cambios |
|---|---|
| `ContratoArriendoServiceImpl` | Validar contrato único VIGENTE por inmueble; crear OcupacionUnidad; calcular fechaLimiteCalificacion; auto-rechazar solicitudes; generar notificaciones; actualizar publicación |
| `SolicitudArriendoServiceImpl` | Lógica EN_ESPERA cuando publicación va a RESERVADA; auto-rechazar al crear contrato |
| `PublicacionInmuebleServiceImpl` | Máquina de estados; transición RESERVADA; actualizar al finalizar contrato |
| `SolicitudRoomieServiceImpl` | Al aprobar: crear AcuerdoConvivencia automáticamente |
| `PerfilUsuarioServiceImpl` | Al aprobar primer documento: cambiar `verificado = true` |
| Nuevo: `SchedulerService` | Cron job diario: contratos vencidos → FINALIZADO |

**Archivos afectados:** 6 archivos `*ServiceImpl.java` existentes + 1 nuevo.

**Nivel de riesgo:** ALTO — es la fase con mayor lógica nueva. Un error en `ContratoArriendoServiceImpl` puede afectar el flujo central del negocio.  
**Dependencias:** Fase 4  
**Tiempo estimado:** 12-16 horas  
**Rollback:** `git checkout` de los 6 servicios modificados

---

### FASE 6 — Actualización del frontend Angular (admin panel)

**Objetivo:** Reflejar los nuevos campos y estados en los formularios y vistas Angular existentes.

**Actividades:**
1. Actualizar formularios de 11 entidades con nuevos campos
2. Actualizar listas con nuevos estados y colores de badge
3. Verificar que los 4 nuevos módulos generados en Fase 4 funcionan correctamente

**Archivos afectados:** ~30-40 archivos `.html` y `.ts` en `webapp/app/entities/`

**Nivel de riesgo:** MEDIO  
**Dependencias:** Fases 3, 4, 5  
**Tiempo estimado:** 8-12 horas  
**Rollback:** `git checkout` de archivos específicos

---

### FASE 7 — Actualización del portal React

**Objetivo:** Reflejar los nuevos estados de publicación en el portal público.

**Actividades:**
1. Mostrar badge con estado visible (RESERVADA, ARRENDADA, etc.)
2. Condicionar el botón "Solicitar" según el estado de la publicación
3. Mostrar colores diferenciados por estado

**Archivos afectados:** `src/main/resources/static/portal/` (código fuente React)

**Nivel de riesgo:** BAJO  
**Dependencias:** Fases 5, 6  
**Tiempo estimado:** 4-6 horas  
**Rollback:** `git checkout` de archivos específicos

---

### FASE 8 — Índices MongoDB y validaciones finales

**Objetivo:** Crear índices en MongoDB para las reglas de unicidad y rendimiento.

**Actividades:**
1. Crear índice parcial único `{inmueble, estado=VIGENTE}` en `contrato_arriendo`
2. Crear índice `{usuario, leida}` en `notificacion`
3. Validar que todas las reglas del doc 17 están implementadas

**Herramienta:** Scripts MongoDB ejecutados manualmente contra la instancia local.

**Nivel de riesgo:** BAJO  
**Tiempo estimado:** 2 horas

---

### FASE 9 — Documentación y commit final

**Objetivo:** Actualizar el JDL con el modelo completo y hacer commit del estado final.

**Actividades:**
1. Actualizar `jhipster-jdl.jdl` con todas las entidades y campos
2. Actualizar documentos `14-entidades.md` y `15-roadmap.md`
3. Commit con mensaje convencional

---

## Resumen de riesgos por fase

| Fase | Descripción | Riesgo | Probabilidad de ruptura | Archivos afectados | Tiempo est. | Rollback |
|---|---|---|---|---|---|---|
| 0 | Preparación | NINGUNO | 0% | 0 | 30 min | N/A |
| 1 | Nuevos valores en enums existentes | MUY BAJO | 2% | 4 | 2 h | git checkout 4 archivos |
| 2 | Nuevos enums | NINGUNO | 0% | 14 nuevos | 2 h | Eliminar archivos nuevos |
| 3 | Campos nuevos en entidades | BAJO | 5% | 11 | 4 h | git checkout 11 archivos |
| 4 | Generar entidades nuevas con JHipster | MEDIO | 20% | ~65 nuevos | 8 h | git checkout + rm |
| 5 | Lógica de negocio en servicios | ALTO | 35% | 6 existentes + 1 nuevo | 16 h | git checkout 6 archivos |
| 6 | Frontend Angular | MEDIO | 15% | ~40 | 12 h | git checkout archivos |
| 7 | Portal React | BAJO | 5% | ~10 | 6 h | git checkout archivos |
| 8 | Índices MongoDB | BAJO | 5% | 0 código | 2 h | Eliminar índices |
| 9 | Documentación y commit | NINGUNO | 0% | 3 | 2 h | N/A |

**Total estimado:** 54 horas de trabajo técnico.

---

## Estrategia de regeneración JHipster

### ¿Qué se puede generar con JHipster?

| Componente | Estrategia | Justificación |
|---|---|---|
| Entidades nuevas (Notificacion, AcuerdoConvivencia, OcupacionUnidad, HistorialPrecio) | **GENERAR con JHipster** | Son nuevas, no hay código que perder |
| Enums nuevos | **MANUAL** | JHipster no genera enums solos; se crean en el JDL y se generan con la entidad |
| Campos en entidades existentes | **MANUAL** | Regenerar una entidad existente sobreescribe plantillas personalizadas |
| Lógica de servicios existentes | **MANUAL** | JHipster genera servicios vacíos; la lógica es 100% manual |
| Frontend Angular de entidades existentes | **MANUAL** | Los HTML de tabla rediseñados se perderían si se regenera |

### ¿Qué NO se debe regenerar con JHipster?

| Entidad | Razón para NO regenerar |
|---|---|
| PerfilUsuario | Formulario Angular personalizado |
| Inmueble | Tabla HTML personalizada |
| PublicacionInmueble | Tabla y filtros React personalizados |
| ContratoArriendo | Tabla HTML personalizada |
| Calificacion | Lógica de ancla modificada |
| Todas las entidades existentes | Las tablas rediseñadas en Angular se perderían |

### Secuencia segura para JHipster

```
1. Actualizar JDL (solo documentación)
2. jhipster entity Notificacion         → revisar archivos generados
3. jhipster entity AcuerdoConvivencia   → revisar archivos generados
4. jhipster entity OcupacionUnidad      → revisar archivos generados
5. jhipster entity HistorialPrecio      → revisar archivos generados
6. Verificar entity.routes.ts — NO sobreescribir
7. Compilar todo antes de continuar
```

---

## Matriz de impacto

| Entidad | Tipo de cambio | Riesgo | Regenerar JHipster | Cambios Front | Cambios Back | Prioridad |
|---|---|---|---|---|---|---|
| **ContratoArriendo** | Campos + lógica | ALTO | NO | Sí (formulario) | Sí (servicio crítico) | CRÍTICA |
| **PublicacionInmueble** | Enum + campos + lógica | ALTO | NO | Sí (form + lista) | Sí (máquina de estados) | CRÍTICA |
| **Calificacion** | Relación + campo tipoAncla | ALTO | NO | Sí (form) | Sí (validación) | ALTA |
| **SolicitudArriendo** | Enum + campos + lógica | MEDIO | NO | Sí (lista) | Sí (auto-rechazo) | ALTA |
| **Notificacion** | Nueva entidad | BAJO | SÍ | Sí (nuevo módulo) | Sí (nuevo servicio) | ALTA |
| **AcuerdoConvivencia** | Nueva entidad | BAJO | SÍ | Sí (nuevo módulo) | Sí (nuevo servicio) | ALTA |
| **OcupacionUnidad** | Nueva entidad | BAJO | SÍ | Sí (nuevo módulo) | Sí (nuevo servicio) | ALTA |
| **SolicitudRoomie** | Campos + lógica | MEDIO | NO | Sí (form) | Sí (crear acuerdo) | ALTA |
| **DocumentoUsuario** | Campos + nuevo enum | MEDIO | NO | Sí (form) | Sí (lógica verificado) | MEDIA |
| **HistorialPrecio** | Nueva entidad | BAJO | SÍ | Sí (nuevo módulo) | Sí (interceptar update) | MEDIA |
| **Inmueble** | Campos nullable | BAJO | NO | Sí (form) | No | MEDIA |
| **PerfilUsuario** | Campo nullable | BAJO | NO | Sí (form) | Sí (lógica verificado) | MEDIA |
| **VisitaProgramada** | Campos nullable | BAJO | NO | Sí (form) | No | BAJA |
| **MultimediaInmueble** | Campos nullable | BAJO | NO | Sí (form) | No | BAJA |
| **PublicacionRoomie** | Campos nullable | BAJO | NO | Sí (form) | No | BAJA |
| **EstadoPublicacion** (enum) | +7 valores | MUY BAJO | NO | Sí (badges) | Sí (switch/match) | CRÍTICA |
| **EstadoSolicitud** (enum) | +1 valor | MUY BAJO | NO | Sí (badges) | Sí (switch/match) | ALTA |
| **Portal React** | Estados visuales | BAJO | NO | Sí | No | MEDIA |
| **Spring Security** | Nuevos endpoints | BAJO | No | No | Sí (@PreAuthorize) | MEDIA |
| **MongoDB (índices)** | 3 índices nuevos | BAJO | No | No | No | MEDIA |
| **Scheduler (cron)** | Nuevo servicio | MEDIO | No | No | Sí (nuevo) | ALTA |

---

## Checklist de implementación

### Preparación
- ☐ Crear rama de trabajo: `git checkout -b feature/nuevas-entidades-v2`
- ☐ Verificar que el proyecto compila en rama main actual
- ☐ Documentar SHA del commit actual como punto de restauración
- ☐ Revisar y aprobar este documento antes de empezar

### Fase 1 — Enums existentes
- ☐ Agregar VISITA_AGENDADA a EstadoPublicacion
- ☐ Agregar POSTULANTE_SELECCIONADO a EstadoPublicacion
- ☐ Agregar RESERVADA a EstadoPublicacion
- ☐ Agregar CONTRATO_EN_FIRMA a EstadoPublicacion
- ☐ Agregar ARRENDADA a EstadoPublicacion
- ☐ Agregar FINALIZADA a EstadoPublicacion
- ☐ Agregar ARCHIVADA a EstadoPublicacion
- ☐ Agregar EN_ESPERA a EstadoSolicitud
- ☐ Actualizar enum TypeScript EstadoPublicacion
- ☐ Actualizar enum TypeScript EstadoSolicitud
- ☐ Compilar backend — debe compilar sin errores
- ☐ Compilar frontend — debe compilar sin errores
- ☐ Commit: `feat(domain): add new publication and solicitud states`

### Fase 2 — Nuevos enums
- ☐ Crear EstadoAcuerdo.java
- ☐ Crear TipoAncla.java
- ☐ Crear TipoOcupante.java
- ☐ Crear EstadoOcupacion.java
- ☐ Crear TipoNotificacion.java (16 valores)
- ☐ Crear EstadoDocumento.java
- ☐ Crear equivalentes TypeScript
- ☐ Compilar
- ☐ Commit: `feat(domain): add new enumerations for v2 entities`

### Fase 3 — Campos en entidades existentes
- ☐ PerfilUsuario: agregar urlFotoPerfil
- ☐ DocumentoUsuario: agregar estadoDocumento, fechaAprobacion, aprobadoPor
- ☐ Inmueble: agregar activo, amenidades, codigoCatastral
- ☐ PublicacionInmueble: agregar republicarAutomaticamente, fechaCambioEstado, estadoAnterior, motivoCambioEstado
- ☐ MultimediaInmueble: agregar orden, fechaCarga
- ☐ SolicitudArriendo: agregar razonRechazo, fechaRespuesta, fechaCambioEstado, estadoAnterior
- ☐ VisitaProgramada: agregar razonCancelacion, fechaCambioEstado
- ☐ ContratoArriendo: agregar fechaLimiteCalificacion, razonCancelacion, penalizacion, fechaCambioEstado, estadoAnterior, motivoCambioEstado
- ☐ PublicacionRoomie: agregar areaHabitacion, banoPrivado, fechaCambioEstado
- ☐ SolicitudRoomie: agregar razonRechazo, fechaRespuesta
- ☐ Calificacion: agregar tipoAncla; hacer contrato nullable
- ☐ Compilar backend
- ☐ Commit: `feat(domain): add audit and business fields to existing entities`

### Fase 4 — Nuevas entidades con JHipster
- ☐ Actualizar jhipster-jdl.jdl con nuevas entidades
- ☐ Generar entidad Notificacion
- ☐ Verificar archivos generados — NO regeneró existentes
- ☐ Compilar
- ☐ Generar entidad AcuerdoConvivencia
- ☐ Verificar archivos generados
- ☐ Compilar
- ☐ Generar entidad OcupacionUnidad
- ☐ Verificar archivos generados
- ☐ Compilar
- ☐ Generar entidad HistorialPrecio
- ☐ Verificar archivos generados
- ☐ Compilar backend completo
- ☐ Compilar frontend completo
- ☐ Commit: `feat: add Notificacion, AcuerdoConvivencia, OcupacionUnidad, HistorialPrecio entities`

### Fase 5 — Lógica de negocio
- ☐ ContratoArriendoServiceImpl: validar contrato único VIGENTE por inmueble
- ☐ ContratoArriendoServiceImpl: crear OcupacionUnidad al pasar a VIGENTE
- ☐ ContratoArriendoServiceImpl: calcular fechaLimiteCalificacion al finalizar
- ☐ ContratoArriendoServiceImpl: auto-rechazar solicitudes al pasar a VIGENTE
- ☐ ContratoArriendoServiceImpl: actualizar PublicacionInmueble al finalizar
- ☐ ContratoArriendoServiceImpl: generar notificaciones en cada transición
- ☐ SolicitudArriendoServiceImpl: transición EN_ESPERA cuando pub pasa a RESERVADA
- ☐ SolicitudArriendoServiceImpl: auto-rechazo cuando contrato pasa a VIGENTE
- ☐ PublicacionInmuebleServiceImpl: máquina de estados completa
- ☐ PublicacionInmuebleServiceImpl: transición RESERVADA al aprobar solicitud
- ☐ PublicacionInmuebleServiceImpl: republicar automáticamente según toggle
- ☐ SolicitudRoomieServiceImpl: crear AcuerdoConvivencia al aprobar
- ☐ PerfilUsuarioServiceImpl: verificar al aprobar documento
- ☐ Crear ContratoSchedulerService (cron job expiración)
- ☐ Prueba manual de cada flujo crítico
- ☐ Compilar
- ☐ Commit: `feat(service): implement business rules state machine`

### Fase 6 — Frontend Angular
- ☐ Actualizar formulario PerfilUsuario (urlFotoPerfil)
- ☐ Actualizar formulario DocumentoUsuario (estadoDocumento)
- ☐ Actualizar formulario Inmueble (activo, amenidades)
- ☐ Actualizar formulario y lista PublicacionInmueble (nuevos estados, badge)
- ☐ Actualizar formulario SolicitudArriendo (razonRechazo, EN_ESPERA badge)
- ☐ Actualizar formulario ContratoArriendo (campos audit)
- ☐ Actualizar formulario Calificacion (tipoAncla, acuerdoConvivencia)
- ☐ Verificar módulos nuevos generados (Notificacion, AcuerdoConvivencia, OcupacionUnidad, HistorialPrecio)
- ☐ Compilar frontend
- ☐ Probar cada CRUD en el panel admin
- ☐ Commit: `feat(angular): update existing forms and add new entity modules`

### Fase 7 — Portal React
- ☐ Mostrar badge de estado de publicación con colores
- ☐ Ocultar botón "Solicitar" en RESERVADA, CONTRATO_EN_FIRMA, ARRENDADA
- ☐ Mostrar texto informativo "Esta propiedad está reservada"
- ☐ Compilar React
- ☐ Restaurar portal compilado en target/classes/static/portal
- ☐ Prueba en navegador
- ☐ Commit: `feat(portal): show publication state badges and conditional actions`

### Fase 8 — Base de datos
- ☐ Crear índice único parcial en contrato_arriendo
- ☐ Crear índice en notificacion
- ☐ Crear índice en multimedia_inmueble
- ☐ Verificar que el índice funciona con inserción de contrato duplicado
- ☐ Commit: `feat(db): create MongoDB indexes for business rules enforcement`

### Fase 9 — Documentación final
- ☐ Actualizar jhipster-jdl.jdl con modelo completo
- ☐ Actualizar docs/14-entidades.md
- ☐ Actualizar docs/15-roadmap.md con estado post-implementación
- ☐ Merge a main
- ☐ Push a origin/main
- ☐ Commit final: `docs: update model documentation post v2 implementation`

---

## Orden óptimo de implementación

```
SEMANA 1
  Día 1: Fase 0 + Fase 1 (preparación + enums existentes)
  Día 2: Fase 2 (nuevos enums)
  Día 3-4: Fase 3 (campos en entidades existentes)
  Día 5: Fase 4 (JHipster — entidades nuevas)

SEMANA 2
  Día 6-8: Fase 5 (lógica de negocio — la más compleja)
  Día 9: Verificación y pruebas del backend

SEMANA 3
  Día 10-11: Fase 6 (frontend Angular)
  Día 12: Fase 7 (portal React)
  Día 13: Fase 8 (índices MongoDB)
  Día 14: Fase 9 (documentación y merge)
```

**Regla de oro:** Si al final del día el proyecto no compila, no se pasa al siguiente día. Se hace rollback y se investiga la causa.
