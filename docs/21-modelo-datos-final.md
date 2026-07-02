# 21 — Modelo de Datos Final — Revisión Pre-Generación

> **Tipo:** Diseño técnico para revisión antes de ejecutar JHipster  
> **Estado:** PENDIENTE DE APROBACIÓN  
> **Fecha:** 2026-07-02  
> **Referencia:** `18-plan-migracion-tecnica.md`, `17-reglas-negocio-definitivas.md`  
>
> **RESTRICCIÓN ABSOLUTA:** Este documento es de solo lectura.  
> Ningún archivo del proyecto será modificado hasta que este modelo sea aprobado.

---

## Tabla de contenido

1. [Contexto y decisiones de diseño](#1-contexto-y-decisiones-de-diseño)
2. [Inventario de cambios](#2-inventario-de-cambios)
3. [Nuevos enums](#3-nuevos-enums)
4. [NE-01 — Notificacion](#4-ne-01--notificacion)
5. [NE-02 — AcuerdoConvivencia](#5-ne-02--acuerdoconvivencia)
6. [NE-03 — OcupacionUnidad](#6-ne-03--ocupacionunidad)
7. [NE-04 — HistorialPrecio](#7-ne-04--historialprecio)
8. [NE-05 — Propiedad (Fase 2)](#8-ne-05--propiedad-fase-2)
9. [Análisis de relaciones clave](#9-análisis-de-relaciones-clave)
10. [Matriz de impacto en entidades existentes](#10-matriz-de-impacto-en-entidades-existentes)
11. [JDL propuesto — solo adiciones](#11-jdl-propuesto--solo-adiciones)
12. [Decisiones pendientes de aprobación](#12-decisiones-pendientes-de-aprobación)

---

## 1. Contexto y decisiones de diseño

### Modelo de dominio actual (11 entidades)

```
PerfilUsuario ──────────────────────────────────────────────┐
│ OneToOne User                                              │
│ OneToMany DocumentoUsuario                                 │
│ OneToMany Inmueble (propietario)                           │
│                                                            │
Inmueble ───────────────────────────────────────────────────┤
│ OneToMany PublicacionInmueble                              │
│ OneToMany MultimediaInmueble                               │
│ OneToMany ContratoArriendo                                 │
│                                                            │
PublicacionInmueble ────────────────────────────────────────┤
│ OneToMany SolicitudArriendo                                │
│                                                            │
SolicitudArriendo ──────────────────────────────────────────┤
│ OneToMany VisitaProgramada                                 │
│                                                            │
ContratoArriendo ───────────────────────────────────────────┤
│ ManyToOne arrendador (PerfilUsuario)                       │
│ ManyToOne arrendatario (PerfilUsuario)                     │
│                                                            │
PublicacionRoomie ──────────────────────────────────────────┤
│ OneToMany SolicitudRoomie                                  │
│ ManyToOne arrendatario (PerfilUsuario)                     │
│ ManyToOne inmueble                                         │
│                                                            │
Calificacion ───────────────────────────────────────────────┘
│ ManyToOne autor (PerfilUsuario)
│ ManyToOne calificado (PerfilUsuario)
│ ManyToOne contrato (ContratoArriendo)
```

### Decisiones de diseño que aplican a todas las nuevas entidades

**D-01 — Sin OneToMany en entidades existentes.**  
Las nuevas entidades declaran `ManyToOne` hacia las existentes pero NO se declara el lado inverso `OneToMany`. Esto evita que JHipster regenere archivos de entidades existentes que tienen customizaciones.  
Consecuencia: las colecciones se obtienen por `Repository.findBy...()`, no por `entidad.getColeccion()`.

**D-02 — Las entidades nuevas son completamente generables por JHipster.**  
Solo `Notificacion`, `AcuerdoConvivencia`, `OcupacionUnidad`, `HistorialPrecio` se generan con JHipster. Las modificaciones manuales a servicios existentes se hacen DESPUÉS de la generación, en Fase 2.

**D-03 — MongoDB schemaless.**  
No se requiere migración de datos existentes para ninguna de las nuevas entidades. Los campos nullable nuevos que se añadan a entidades existentes (Fase 2 únicamente) serán `null` en documentos históricos sin error.

**D-04 — `Propiedad` es Fase 2.**  
Se diseña aquí para no rediseñar después, pero NO se genera en Fase 1. Su JDL se escribe pero no se ejecuta.

**D-05 — Sin eliminación física.**  
Ninguna de las nuevas entidades permite DELETE en producción. Solo cambio de estado (soft-delete semántico).

---

## 2. Inventario de cambios

### Entidades nuevas — Fase 1 (para generar con JHipster)

| ID | Entidad | Colección MongoDB | Genera JHipster |
|---|---|---|---|
| NE-01 | `Notificacion` | `notificacion` | SÍ |
| NE-02 | `AcuerdoConvivencia` | `acuerdo_convivencia` | SÍ |
| NE-03 | `OcupacionUnidad` | `ocupacion_unidad` | SÍ |
| NE-04 | `HistorialPrecio` | `historial_precio` | SÍ |

### Entidades nuevas — Fase 2 (diseñadas aquí, generadas después)

| ID | Entidad | Colección MongoDB | Genera JHipster |
|---|---|---|---|
| NE-05 | `Propiedad` | `propiedad` | Fase 2 |

### Enums nuevos — Fase 1

| Enum | Valores | Entidad que lo usa |
|---|---|---|
| `TipoNotificacion` | 16 valores | Notificacion |
| `EstadoNotificacion` | 3 valores | Notificacion |
| `EstadoAcuerdo` | 4 valores | AcuerdoConvivencia |
| `EstadoOcupacion` | 3 valores | OcupacionUnidad |
| `TipoRegistroPrecio` | 2 valores | HistorialPrecio |

### Enums nuevos — Fase 2

| Enum | Valores | Entidad que lo usa |
|---|---|---|
| `TipoPropiedad` | 5 valores | Propiedad |
| `EstadoPropiedad` | 2 valores | Propiedad |

### Entidades existentes modificadas

| Entidad | Cambio | Fase |
|---|---|---|
| `Calificacion` | Añadir `acuerdoConvivencia` nullable + `tipoAncla` | 2 |
| `Inmueble` | Añadir `propiedad` nullable (FK a Propiedad) | 2 |

---

## 3. Nuevos enums

### TipoNotificacion

```
SOLICITUD_RECIBIDA        // Arrendador: recibiste una solicitud de arriendo
SOLICITUD_APROBADA        // Arrendatario: tu solicitud fue aprobada
SOLICITUD_RECHAZADA       // Arrendatario: tu solicitud fue rechazada
SOLICITUD_CANCELADA       // Arrendador: el solicitante canceló
VISITA_CONFIRMADA         // Visitante: tu visita fue confirmada
VISITA_CANCELADA          // Ambas partes: la visita fue cancelada
VISITA_RECORDATORIO       // Recordatorio 24 h antes de la visita (cron, Fase 2)
POSTULANTE_SELECCIONADO   // Arrendatario: fuiste seleccionado como candidato
CONTRATO_GENERADO         // Arrendatario: el contrato está listo para revisar
CONTRATO_FIRMADO          // Arrendador: el arrendatario firmó el contrato
CONTRATO_POR_VENCER       // Ambas partes: contrato vence en 30/15/7 días (cron, Fase 2)
CONTRATO_VENCIDO          // Ambas partes: el contrato venció
CALIFICACION_RECIBIDA     // Calificado: alguien te calificó
CALIFICACION_PENDIENTE    // Recordatorio: tienes 15 días para calificar
ACUERDO_CONVIVENCIA_ACTIVO // Roomie: el acuerdo de convivencia está vigente
SISTEMA                   // Notificación genérica del sistema
```

### EstadoNotificacion

```
NUEVA       // Recibida, no leída
LEIDA       // Leída por el destinatario
ARCHIVADA   // Archivada (no aparece en listado principal)
```

### EstadoAcuerdo

```
BORRADOR    // Generado automáticamente; roomie no ha aceptado aún
VIGENTE     // Roomie aceptó; convivencia activa
FINALIZADO  // Terminó normalmente; habilita calificaciones (ventana 15 días)
CANCELADO   // Cancelado antes o durante la vigencia
```

> **Por qué no reutilizar EstadoContrato:** AcuerdoConvivencia omite deliberadamente
> `PENDIENTE_FIRMA` porque en la relación roomie-anfitrión no hay firma legal formal.
> La aceptación es digital (checkbox). Futuras versiones podrían añadir este estado.

### EstadoOcupacion

```
ACTIVA      // Persona está actualmente en el inmueble
FINALIZADA  // Salió normalmente al terminar el contrato/acuerdo
CANCELADA   // Salió por cancelación anticipada del contrato/acuerdo
```

### TipoRegistroPrecio

```
INMUEBLE    // Cambio en PublicacionInmueble.canonArriendo
ROOMIE      // Cambio en PublicacionRoomie.valorMensual
```

### TipoPropiedad (Fase 2)

```
EDIFICIO                // Edificio de apartamentos con múltiples unidades
CONJUNTO_RESIDENCIAL    // Conjunto cerrado con múltiples casas o aptos
CASA_MULTIFAMILIAR      // Casa dividida en unidades independientes (habitaciones)
COMPLEJO_COMERCIAL      // Complejo de locales u oficinas
OTRO
```

### EstadoPropiedad (Fase 2)

```
ACTIVA      // Registrada y con unidades activas
INACTIVA    // Desactivada por el propietario
```

---

## 4. NE-01 — Notificacion

### Responsabilidad

Almacena las notificaciones in-app entregadas a un `PerfilUsuario` cuando ocurre un evento relevante en el sistema. Es el mecanismo que comunica cambios de estado al usuario sin requerir que recargue la pantalla.

### Justificación

Todos los flujos del sistema describen que "el sistema notifica" (solicitud recibida, visita confirmada, contrato por vencer). Actualmente este mecanismo no existe. Sin `Notificacion`, los usuarios no tienen forma de saber que algo cambió sin navegar manualmente por las secciones.

### Ciclo de vida

```
[Evento del sistema]
        ↓
   NUEVA ─── Usuario la lee ──→ LEIDA ─── Usuario archiva ──→ ARCHIVADA
                                            (o cron 90 días)
```

Regla: una notificación no puede volver de `LEIDA` a `NUEVA`. No se eliminan físicamente.

### Campos

| Campo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `id` | String | PK | MongoDB ObjectId |
| `tipo` | TipoNotificacion | required | Categoría del evento |
| `titulo` | String | required, max 200 | Título legible por el usuario |
| `mensaje` | TextBlob | required | Cuerpo completo del mensaje |
| `estado` | EstadoNotificacion | required, default NUEVA | Estado de lectura |
| `fechaCreacion` | Instant | required | Momento de generación |
| `fechaLectura` | Instant | nullable | Momento en que fue leída |
| `urlAccion` | String | nullable, max 500 | Deep link a la entidad relacionada |
| `entidadId` | String | nullable, max 100 | ID de la entidad que disparó el evento |
| `entidadTipo` | String | nullable, max 50 | Tipo: "SOLICITUD", "VISITA", "CONTRATO"… |

### Relaciones

| Relación | Tipo | Cardinalidad | Nullable |
|---|---|---|---|
| `destinatario` → PerfilUsuario | ManyToOne | N:1 | NO — siempre hay destinatario |
| `remitente` → PerfilUsuario | ManyToOne | N:1 | SÍ — null cuando la crea el sistema |

> Cuando `remitente` es null, la notificación fue generada por lógica de negocio
> (cron job, cambio de estado automático). Cuando no es null, fue disparada por
> la acción de otro usuario (ej. arrendador aprobó solicitud).

### Cardinalidades

- Un `PerfilUsuario` recibe **0..N** notificaciones (OneToMany implícito)
- Una `Notificacion` tiene exactamente **1** destinatario
- Una `Notificacion` tiene **0..1** remitente (null = sistema)
- Estimación de volumen: 10-50 notificaciones por usuario por mes

### Índices recomendados

```
// Más frecuente: notificaciones no leídas del usuario activo
{ destinatario: 1, estado: 1 }

// Listado paginado del usuario
{ destinatario: 1, fechaCreacion: -1 }

// Dashboard admin
{ fechaCreacion: -1 }
{ tipo: 1, fechaCreacion: -1 }
```

### Restricciones

- `destinatario` no puede ser null
- `estado` nace siempre como `NUEVA`
- `fechaLectura` solo se establece cuando `estado` → `LEIDA`
- `estado` no puede retroceder (`LEIDA` → `NUEVA` prohibido)
- No existe endpoint DELETE público

### Validaciones

- `titulo`: not blank, max 200 chars
- `mensaje`: not blank
- `tipo`: valor válido del enum
- `estado`: valor válido del enum
- `fechaCreacion`: not null, not future

### Reglas de negocio

1. Solo el sistema (capa de servicio) puede crear notificaciones. No hay POST público.
2. Solo el `destinatario` puede marcar como leída o archivar su notificación.
3. El admin puede consultar notificaciones de cualquier usuario (GET con filtros).
4. La campanita del navbar muestra el count de notificaciones en estado `NUEVA`.
5. Al marcar una sola como leída: `estado = LEIDA`, `fechaLectura = now`.
6. "Marcar todas como leídas" actualiza todas las `NUEVA` del destinatario.
7. Archivado automático (Fase 2, cron): notificaciones `LEIDA` con más de 90 días → `ARCHIVADA`.

### APIs necesarias

| Método | Endpoint | Descripción | Quién accede |
|---|---|---|---|
| GET | `/api/notificaciones` | Lista propia paginada (filtrable por estado) | Usuario autenticado |
| GET | `/api/notificaciones/no-leidas-count` | Count de NUEVA del usuario | Usuario autenticado |
| PUT | `/api/notificaciones/{id}/marcar-leida` | Marca como LEIDA | Solo destinatario |
| PUT | `/api/notificaciones/marcar-todas-leidas` | Marca todas NUEVA como LEIDA | Usuario autenticado |
| PUT | `/api/notificaciones/{id}/archivar` | Marca como ARCHIVADA | Solo destinatario |
| GET | `/api/notificaciones` (admin) | Lista global con filtros | ROLE_ADMIN |

> Los endpoints de creación son internos (llamados desde servicios, no expuestos públicamente).

### Eventos que la crean

| Evento de dominio | Tipo notificación | Destinatario |
|---|---|---|
| SolicitudArriendo creada | SOLICITUD_RECIBIDA | Arrendador |
| SolicitudArriendo → APROBADA | SOLICITUD_APROBADA | Arrendatario |
| SolicitudArriendo → RECHAZADA | SOLICITUD_RECHAZADA | Arrendatario |
| SolicitudArriendo → CANCELADA | SOLICITUD_CANCELADA | Arrendador |
| VisitaProgramada → CONFIRMADA | VISITA_CONFIRMADA | Visitante |
| VisitaProgramada → CANCELADA | VISITA_CANCELADA | Visitante + Arrendador |
| PublicacionInmueble → POSTULANTE_SELECCIONADO | POSTULANTE_SELECCIONADO | Arrendatario seleccionado |
| ContratoArriendo → PENDIENTE_FIRMA | CONTRATO_GENERADO | Arrendatario |
| ContratoArriendo → VIGENTE | CONTRATO_FIRMADO | Arrendador |
| ContratoArriendo → FINALIZADO | CALIFICACION_PENDIENTE | Ambas partes |
| AcuerdoConvivencia → VIGENTE | ACUERDO_CONVIVENCIA_ACTIVO | Roomie |
| AcuerdoConvivencia → FINALIZADO | CALIFICACION_PENDIENTE | Anfitrión + Roomie |
| Calificacion creada | CALIFICACION_RECIBIDA | Calificado |
| Cron: contrato vence en 30/15/7 días | CONTRATO_POR_VENCER | Ambas partes (Fase 2) |

### Eventos que la actualizan

- Usuario lee → `estado: NUEVA → LEIDA`, `fechaLectura: now`
- Usuario archiva → `estado: → ARCHIVADA`
- Cron 90 días → `LEIDA → ARCHIVADA` (Fase 2)

### Eventos que la eliminan

Ninguno. Las notificaciones son permanentes (soft lifecycle solo).

---

## 5. NE-02 — AcuerdoConvivencia

### Responsabilidad

Documento formal que formaliza la convivencia entre quien ofrece una habitación (anfitrión) y quien la ocupa (roomie). Es el equivalente funcional de `ContratoArriendo` para la relación arrendatario↔roomie: da un ciclo de vida formal, fechas de inicio/fin y base para calificaciones.

### Justificación

En el modelo actual, cuando `SolicitudRoomie` pasa a `APROBADA`, el roomie queda en estado indeterminado:
- No hay documento que registre cuándo ingresó ni cuándo saldrá
- Las calificaciones `ARRENDATARIO_A_ROOMIE` y `ROOMIE_A_ARRENDATARIO` no tienen ancla (hoy están ancladas a `ContratoArriendo`, lo que es semánticamente incorrecto: un roomie no tiene contrato con el propietario)
- No hay manera de saber si un roomie está activo, fue reemplazado, o cuándo terminó la convivencia

### Ciclo de vida

```
SolicitudRoomie → APROBADA
        ↓ (automático)
   BORRADOR  ← Sistema lo crea; anfitrión puede revisar términos
        ↓ roomie acepta digitalmente
   VIGENTE   ← Convivencia activa; crea OcupacionUnidad
        ↓ cualquiera de las partes finaliza
   FINALIZADO ← Habilita calificaciones (15 días) — crea Notificacion CALIFICACION_PENDIENTE
        
   CANCELADO ← Cancelado en cualquier estado antes de finalizar normalmente
```

### Campos

| Campo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `id` | String | PK | MongoDB ObjectId |
| `fechaInicio` | LocalDate | required | Primer día de convivencia acordado |
| `fechaFin` | LocalDate | nullable | Último día acordado (null = indefinido) |
| `estado` | EstadoAcuerdo | required | Estado del acuerdo |
| `reglasConvivencia` | TextBlob | nullable | Reglas específicas acordadas (no fumar, mascotas, horarios) |
| `aceptaTerminosAnfitrion` | Boolean | required | true siempre al crear (anfitrión inicia el acuerdo) |
| `aceptaTerminosRoomie` | Boolean | required | false al crear; true cuando roomie acepta |
| `fechaLimiteCalificacion` | Instant | nullable | `fechaFin + 15 días` — calculado al FINALIZAR |
| `observaciones` | TextBlob | nullable | Notas internas |
| `fechaCreacion` | Instant | required | Timestamp de creación |

### Relaciones

| Relación | Tipo | Cardinalidad | Nullable | Descripción |
|---|---|---|---|---|
| `anfitrion` → PerfilUsuario | ManyToOne | N:1 | NO | Quien ofrece el espacio |
| `roomie` → PerfilUsuario | ManyToOne | N:1 | NO | Quien ocupa el espacio |
| `publicacionRoomie` → PublicacionRoomie | ManyToOne | N:1 | NO | Publicación de origen |
| `inmueble` → Inmueble | ManyToOne | N:1 | NO | Inmueble donde se da la convivencia |

> **¿Por qué `inmueble` si se puede derivar de `publicacionRoomie.inmueble`?**  
> Porque `PublicacionRoomie` puede archivarse mientras el acuerdo está vigente.
> El FK directo a `Inmueble` garantiza acceso eficiente sin joins adicionales
> y permite la consulta: "¿quién está viviendo en este inmueble ahora mismo?"

> **¿Por qué no hay FK a `ContratoArriendo`?**  
> El anfitrión puede ser el propietario del inmueble (sin contrato de arriendo propio)
> o un arrendatario que subarrinda. En el segundo caso el FK existiría, pero
> en el primero no. La relación con `ContratoArriendo` se resuelve a través de
> `Inmueble` cuando sea necesario, evitando un FK nullable ambiguo.

### Cardinalidades

- `PublicacionRoomie` → **0..N** AcuerdoConvivencia (roomies secuenciales a lo largo del tiempo)
- `Inmueble` → **0..N** AcuerdoConvivencia; regla de negocio: máximo 1 `VIGENTE` a la vez
- `PerfilUsuario` (anfitrión) → **0..N** AcuerdoConvivencia como anfitrión
- `PerfilUsuario` (roomie) → **0..N** AcuerdoConvivencia a lo largo del tiempo; regla: máximo 1 `VIGENTE`

### Índices recomendados

```
// Acuerdo activo de un inmueble
{ inmueble: 1, estado: 1 }

// Todos los acuerdos de un anfitrión
{ anfitrion: 1, estado: 1 }

// Historial de un roomie
{ roomie: 1, fechaInicio: -1 }

// Por publicación origen
{ publicacionRoomie: 1 }
```

### Restricciones

- `anfitrion` ≠ `roomie` (validar en servicio)
- `fechaFin > fechaInicio` cuando `fechaFin` no es null
- `fechaLimiteCalificacion` solo se setea cuando `estado → FINALIZADO`
- Solo 1 `AcuerdoConvivencia` en estado `VIGENTE` por `Inmueble` (regla en servicio)
- `aceptaTerminosAnfitrion` = true siempre al crear (no se puede crear un acuerdo sin la aprobación del anfitrión)

### Validaciones

- `anfitrion` required, not null
- `roomie` required, not null
- `publicacionRoomie` required, not null
- `inmueble` required, not null
- `fechaInicio` required
- `aceptaTerminosAnfitrion` must be true (server-side validation al crear)
- Al finalizar: `fechaFin` debe ser no-null o setear la fecha de hoy como `fechaFin`

### Reglas de negocio

1. Se crea automáticamente por `SolicitudRoomieServiceImpl` cuando `SolicitudRoomie → APROBADA`.
2. `aceptaTerminosRoomie = false` al crear; el roomie lo acepta mediante endpoint dedicado.
3. Al aceptar el roomie: `estado: BORRADOR → VIGENTE`, crea `OcupacionUnidad` del roomie.
4. Al finalizar: `estado: VIGENTE → FINALIZADO`, calcula `fechaLimiteCalificacion = fechaFin + 15 días`, genera `Notificacion(CALIFICACION_PENDIENTE)` para ambas partes.
5. Calificaciones `ARRENDATARIO_A_ROOMIE` / `ROOMIE_A_ARRENDATARIO` solo se permiten si existe un `AcuerdoConvivencia(FINALIZADO)` entre las mismas partes (validación en `CalificacionServiceImpl`, Fase 2).
6. Cancelar: disponible desde `BORRADOR` o `VIGENTE`, genera notificación a la otra parte.

### APIs necesarias

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/acuerdo-convivencias` | Lista (admin o propios) paginada |
| GET | `/api/acuerdo-convivencias/{id}` | Detalle |
| PUT | `/api/acuerdo-convivencias/{id}/aceptar-roomie` | Roomie acepta → VIGENTE |
| PUT | `/api/acuerdo-convivencias/{id}/finalizar` | Termina la convivencia → FINALIZADO |
| PUT | `/api/acuerdo-convivencias/{id}/cancelar` | Cancela → CANCELADO |
| POST | `/api/acuerdo-convivencias` | Solo interno desde SolicitudRoomieService |

### Eventos que crean

- `SolicitudRoomie → APROBADA` → `AcuerdoConvivencia(BORRADOR)` automático

### Eventos que actualizan

- Roomie acepta digitalmente → `BORRADOR → VIGENTE`, `aceptaTerminosRoomie = true`
- Fin de convivencia → `VIGENTE → FINALIZADO`, `fechaLimiteCalificacion = hoy + 15 días`
- Cancelación → `BORRADOR|VIGENTE → CANCELADO`

### Eventos que eliminan

Ninguno. Registro permanente para historial y auditoría.

---

## 6. NE-03 — OcupacionUnidad

### Responsabilidad

Registro histórico de quién habitó físicamente cada `Inmueble` y durante qué período. Separa "quién firmó el contrato" de "quién vivió en el inmueble". Es el único lugar del sistema donde se puede consultar el historial completo de ocupantes de una unidad.

### Justificación

El `ContratoArriendo` registra quién firmó, pero no quién habitó físicamente. Escenarios que requieren esta separación:
- Pareja en que solo uno firmó: ambos ocupan físicamente
- Roomie que vive bajo el contrato de otra persona (pero con AcuerdoConvivencia propio)
- Registro de quién estuvo en el inmueble para auditoría o reclamos
- Consultas históricas: "¿quién ocupó el Apto 501 entre 2024 y 2025?"

### Ciclo de vida

```
ContratoArriendo → VIGENTE           AcuerdoConvivencia → VIGENTE
        ↓ (automático)                       ↓ (automático)
   OcupacionUnidad(ACTIVA)
   esPrincipal = true                   esPrincipal = false (roomie)
        ↓                                       ↓
   Contrato/Acuerdo → FINALIZADO    Contrato/Acuerdo → CANCELADO
        ↓                                       ↓
   FINALIZADA (fechaSalida = hoy)    CANCELADA (fechaSalida = hoy)
```

### Campos

| Campo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `id` | String | PK | MongoDB ObjectId |
| `fechaIngreso` | LocalDate | required | Primer día de ocupación física |
| `fechaSalida` | LocalDate | nullable | Último día de ocupación (null = sigue activo) |
| `esPrincipal` | Boolean | required | true = firmante del contrato, false = coadyuvante o roomie |
| `estado` | EstadoOcupacion | required | ACTIVA / FINALIZADA / CANCELADA |
| `observaciones` | TextBlob | nullable | Notas sobre el ingreso o salida |
| `fechaCreacion` | Instant | required | Timestamp de creación del registro |

### Relaciones

| Relación | Tipo | Cardinalidad | Nullable | Descripción |
|---|---|---|---|---|
| `inmueble` → Inmueble | ManyToOne | N:1 | NO | Unidad ocupada |
| `ocupante` → PerfilUsuario | ManyToOne | N:1 | NO | Persona que habita |
| `contrato` → ContratoArriendo | ManyToOne | N:1 | SÍ | Contrato base (null si viene de acuerdo) |
| `acuerdoConvivencia` → AcuerdoConvivencia | ManyToOne | N:1 | SÍ | Acuerdo base (null si viene de contrato) |

> **Regla:** exactamente uno de `contrato` o `acuerdoConvivencia` debe ser no-null.  
> Esta regla no es expresable en MongoDB como constraint de DB; se valida en la capa de servicio.

### Cardinalidades

- `Inmueble` → **0..N** OcupacionUnidad (historial completo)
- `Inmueble` → **máximo 1** OcupacionUnidad `ACTIVA` con `esPrincipal = true` (regla de negocio)
- `ContratoArriendo` → **1..N** OcupacionUnidad (mínimo 1 para el titular; más para coadyuvantes en Fase 2)
- `AcuerdoConvivencia` → **1** OcupacionUnidad (el roomie)
- `PerfilUsuario` → **0..N** OcupacionUnidad a lo largo del tiempo

### Índices recomendados

```
// Ocupante actual de un inmueble
{ inmueble: 1, estado: 1, esPrincipal: 1 }

// Historial cronológico de una unidad
{ inmueble: 1, fechaIngreso: -1 }

// Historial de una persona
{ ocupante: 1, fechaIngreso: -1 }

// Por contrato (para encontrar todos los ocupantes de un contrato)
{ contrato: 1 }

// Por acuerdo de convivencia
{ acuerdoConvivencia: 1 }
```

### Restricciones

- Solo 1 OcupacionUnidad `ACTIVA` con `esPrincipal = true` por Inmueble (servicio)
- `fechaSalida > fechaIngreso` cuando no es null
- Cuando `estado → FINALIZADA` o `CANCELADA`: `fechaSalida` debe ser no-null
- No se puede pasar de `FINALIZADA` a `ACTIVA` (sin posibilidad de reactivar)

### Validaciones

- `inmueble` required
- `ocupante` required
- `fechaIngreso` required
- `esPrincipal` required
- Al crear: al menos uno de `contrato` o `acuerdoConvivencia` debe estar presente (servicio)
- Al finalizar/cancelar: `fechaSalida` required (si no se provee, se usa la fecha actual)

### Reglas de negocio

1. Se crea automáticamente por `ContratoArriendoServiceImpl` cuando `ContratoArriendo → VIGENTE` (`esPrincipal = true`, el arrendatario del contrato).
2. Se crea automáticamente por `AcuerdoConvivenciaServiceImpl` cuando `AcuerdoConvivencia → VIGENTE` (`esPrincipal = false`, el roomie).
3. Al `ContratoArriendo → FINALIZADO`: todas las OcupacionUnidad del contrato → `FINALIZADA`, `fechaSalida = hoy`.
4. Al `ContratoArriendo → CANCELADO`: todas las OcupacionUnidad del contrato → `CANCELADA`, `fechaSalida = hoy`.
5. Al `AcuerdoConvivencia → FINALIZADO/CANCELADO`: OcupacionUnidad del acuerdo → estado correspondiente, `fechaSalida = hoy`.
6. En Fase 1 solo se crea 1 OcupacionUnidad por ContratArriendo (el titular). Múltiples ocupantes es Fase 2.

### APIs necesarias

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/ocupacion-unidades` | Lista completa (admin, paginada) |
| GET | `/api/ocupacion-unidades/{id}` | Detalle |
| GET | `/api/ocupacion-unidades?inmuebleId={id}` | Historial de un inmueble |
| GET | `/api/ocupacion-unidades?ocupanteId={id}` | Historial de un ocupante |
| GET | `/api/ocupacion-unidades/activa/{inmuebleId}` | Ocupante actual de un inmueble |

> No hay POST público. Se crea solo desde `ContratoArriendoServiceImpl` y `AcuerdoConvivenciaServiceImpl`.

### Eventos que crean

- `ContratoArriendo → VIGENTE` → `OcupacionUnidad(ACTIVA, esPrincipal=true, contrato=contrato)`
- `AcuerdoConvivencia → VIGENTE` → `OcupacionUnidad(ACTIVA, esPrincipal=false, acuerdoConvivencia=acuerdo)`

### Eventos que actualizan

- `ContratoArriendo → FINALIZADO` → `OcupacionUnidad(FINALIZADA, fechaSalida=hoy)`
- `ContratoArriendo → CANCELADO` → `OcupacionUnidad(CANCELADA, fechaSalida=hoy)`
- `AcuerdoConvivencia → FINALIZADO` → `OcupacionUnidad(FINALIZADA, fechaSalida=hoy)`
- `AcuerdoConvivencia → CANCELADO` → `OcupacionUnidad(CANCELADA, fechaSalida=hoy)`

### Eventos que eliminan

Ninguno. Registro de auditoría permanente.

---

## 7. NE-04 — HistorialPrecio

### Responsabilidad

Log inmutable de cada cambio de precio en una `PublicacionInmueble` o `PublicacionRoomie`. Garantiza que el precio histórico nunca se pierda cuando un arrendador edita su publicación.

### Justificación

Cuando el arrendador hace `PUT /api/publicacion-inmuebles/{id}` y cambia `canonArriendo`, el valor anterior desaparece. No existe forma de saber si el arrendatario vio la publicación a $1.2M y el precio luego subió a $1.5M. `HistorialPrecio` preserva esta información para:
- Transparencia ante disputas
- Análisis de tendencias de precios por zona
- Auditoría de comportamiento de arrendadores

> **¿Por qué `PublicacionInmueble` y no `ContratoArriendo`?**  
> El precio del contrato (`ContratoArriendo.valorMensual`) es el precio ACORDADO y firmado.  
> No debe cambiar después de la firma (es un documento legal). El precio que cambia  
> es el precio de oferta en la publicación (`PublicacionInmueble.canonArriendo`).  
> `HistorialPrecio` registra los cambios de precio ANTES de que se firme un contrato.

### Ciclo de vida

```
PublicacionInmueble creada (precio inicial)
        ↓ automático
   HistorialPrecio(precioAnterior=0, precioNuevo=canonArriendo)

Arrendador edita canonArriendo
        ↓ automático, ANTES de guardar el nuevo valor
   HistorialPrecio(precioAnterior=valorActual, precioNuevo=valorNuevo)
```

Sin estados. Es un log inmutable. Nunca se actualiza ni elimina.

### Campos

| Campo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `id` | String | PK | MongoDB ObjectId |
| `precioAnterior` | Long | required, min(0) | Precio vigente ANTES del cambio |
| `precioNuevo` | Long | required, min(1) | Precio aplicado DESPUÉS del cambio |
| `motivo` | String | nullable, max 300 | Razón del cambio (opcional, ingresada por el usuario) |
| `tipo` | TipoRegistroPrecio | required | INMUEBLE o ROOMIE |
| `fechaCambio` | Instant | required | Timestamp exacto del cambio |

### Relaciones

| Relación | Tipo | Cardinalidad | Nullable | Descripción |
|---|---|---|---|---|
| `publicacionInmueble` → PublicacionInmueble | ManyToOne | N:1 | SÍ | Set cuando tipo = INMUEBLE |
| `publicacionRoomie` → PublicacionRoomie | ManyToOne | N:1 | SÍ | Set cuando tipo = ROOMIE |

> **Regla:** exactamente uno de los dos FK debe ser no-null.  
> Se valida en servicio. El campo `tipo` es redundante (podría derivarse del FK no-null)  
> pero lo incluimos para simplificar queries sin necesidad de inspeccionar ambos FK.

### Cardinalidades

- `PublicacionInmueble` → **0..N** HistorialPrecio (0 = publicación sin cambios desde creación)
- `PublicacionRoomie` → **0..N** HistorialPrecio
- El primer registro por publicación tiene `precioAnterior = 0` y representa el precio inicial

### Índices recomendados

```
// Historial de precios de una publicación de inmueble
{ publicacionInmueble: 1, fechaCambio: -1 }

// Historial de precios de una publicación roomie
{ publicacionRoomie: 1, fechaCambio: -1 }

// Análisis de precios recientes (analytics)
{ fechaCambio: -1 }
{ tipo: 1, fechaCambio: -1 }
```

### Restricciones

- `precioNuevo ≠ precioAnterior` (no-op no registrado — validar en servicio)
- `precioNuevo > 0` siempre
- Exactamente 1 FK no-null (validar en servicio)
- **INMUTABLE**: no existe PUT ni PATCH. No existe DELETE.

### Validaciones

- `precioAnterior` required, min(0)
- `precioNuevo` required, min(1)
- `tipo` required, valor válido
- `fechaCambio` required, not null
- `precioNuevo ≠ precioAnterior` (servicio rechaza cambios sin diferencia de precio)

### Reglas de negocio

1. Se crea automáticamente en `PublicacionInmuebleServiceImpl.save()` (publicación nueva): `precioAnterior = 0`, `precioNuevo = canonArriendo`.
2. Se crea automáticamente en `PublicacionInmuebleServiceImpl.update()` cuando `canonArriendo` cambia, ANTES de persistir el nuevo valor.
3. Misma lógica en `PublicacionRoomieServiceImpl` para `valorMensual`.
4. Solo el sistema puede crear registros. No hay POST público para usuarios.
5. No existe endpoint de modificación ni eliminación.
6. El admin puede consultar el historial completo.
7. El arrendador puede ver el historial de SUS publicaciones.

### APIs necesarias

| Método | Endpoint | Descripción | Acceso |
|---|---|---|---|
| GET | `/api/historial-precios?publicacionInmuebleId={id}` | Historial de una publicación | Arrendador propietario, admin |
| GET | `/api/historial-precios?publicacionRoomieId={id}` | Historial de publicación roomie | Arrendatario propietario, admin |
| GET | `/api/historial-precios` | Lista global (admin) | Solo ROLE_ADMIN |

> No existe POST, PUT, PATCH ni DELETE públicos.

### Eventos que crean

- `PublicacionInmueble` se crea → primer `HistorialPrecio`
- `PublicacionInmueble.canonArriendo` cambia → nuevo `HistorialPrecio`
- `PublicacionRoomie` se crea → primer `HistorialPrecio`
- `PublicacionRoomie.valorMensual` cambia → nuevo `HistorialPrecio`

### Eventos que actualizan

Ninguno. Inmutable.

### Eventos que eliminan

Ninguno.

---

## 8. NE-05 — Propiedad (Fase 2)

### Responsabilidad

Agrupa múltiples `Inmueble` que forman parte físicamente del mismo edificio, conjunto o complejo. Formaliza el "escenario edificio" que actualmente se maneja informalmente por dirección compartida (Torre Norte: Apt 501, 502, 601 en el seed).

### Justificación

En el seed actual, los 3 apartamentos de Torre Norte tienen la misma dirección manual (`Carrera 7 #127-50 Apt 501/502/601`). Esto es una convención frágil: el sistema no sabe que son unidades del mismo edificio. Con `Propiedad`:
- El sistema entiende que 501, 502 y 601 son unidades de "Torre Norte"
- Permite mostrar amenidades a nivel de edificio (gimnasio, BBQ, portería)
- Habilita el cargo de administración a nivel de propiedad
- Permite vista de portafolio: "todos mis apartamentos en Torre Norte"
- Escenario LOCAL/OFICINA: "Centro Empresarial Zona Rosa" como propiedad con Local 101, Local 102 y Oficina 301

### Ciclo de vida

```
Arrendador registra Propiedad (ACTIVA)
        ↓
Arrendador vincula Inmuebles existentes a la Propiedad
        ↓
[Publicaciones, contratos, etc. siguen en Inmueble — sin cambio]
        ↓
Arrendador desactiva Propiedad (INACTIVA)
— Los Inmuebles vinculados NO se afectan —
```

### Campos

| Campo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `id` | String | PK | MongoDB ObjectId |
| `nombre` | String | required, max 200 | Nombre del edificio/conjunto |
| `descripcion` | TextBlob | nullable | Descripción general |
| `direccion` | String | required, max 300 | Dirección exacta |
| `ciudad` | String | required | Ciudad |
| `localidad` | String | nullable | Localidad o comuna |
| `barrio` | String | required | Barrio |
| `latitud` | Double | nullable | Coordenada geográfica |
| `longitud` | Double | nullable | Coordenada geográfica |
| `tipo` | TipoPropiedad | required | EDIFICIO, CONJUNTO_RESIDENCIAL, etc. |
| `totalUnidades` | Integer | nullable, min(1) | Total de unidades arrendables |
| `totalPisos` | Integer | nullable, min(1) | Número de pisos |
| `anoContruccion` | Integer | nullable, min(1900), max(2100) | Año de construcción |
| `valorAdministracion` | Long | nullable, min(0) | Cuota mensual de administración |
| `amenidades` | TextBlob | nullable | Lista de amenidades (JSON o CSV) |
| `estado` | EstadoPropiedad | required | ACTIVA / INACTIVA |
| `fechaRegistro` | Instant | required | Timestamp de registro en sistema |

### Relaciones

| Relación | Tipo | Cardinalidad | Nullable | Descripción |
|---|---|---|---|---|
| `propietario` → PerfilUsuario | ManyToOne | N:1 | NO | Dueño o administrador |
| ← `inmuebles` (Inmueble.propiedad) | OneToMany | 1:N | — | Unidades de esta propiedad |

> **Impacto en `Inmueble` (Fase 2):**  
> Se añade campo `propiedad` nullable (ManyToOne a Propiedad).  
> En MongoDB no requiere migración: los documentos existentes tendrán `propiedad = null`,
> lo que los trata como unidades independientes sin propiedad padre.  
> Esta modificación se hace **MANUALMENTE** en `Inmueble.java` y en el JDL.
> NO se regenera Inmueble con JHipster (para no perder customizaciones).

### Cardinalidades

- `PerfilUsuario` → **0..N** Propiedad (un arrendador puede tener varios edificios)
- `Propiedad` → **1..N** Inmueble (al menos una unidad para tener sentido)
- `Inmueble` → **0..1** Propiedad (nullable — puede ser una unidad independiente)

### Índices recomendados

```
// Propiedades de un arrendador
{ propietario: 1, estado: 1 }

// Búsqueda geográfica
{ ciudad: 1, barrio: 1 }

// Por tipo
{ tipo: 1, estado: 1 }
```

### Restricciones

- Un `Inmueble` pertenece a máximo 1 `Propiedad`
- El `propietario` de `Inmueble` debe coincidir con `Propiedad.propietario` (validar en servicio)
- No se puede eliminar `Propiedad` si alguna unidad tiene publicaciones activas

### Validaciones

- `nombre` required
- `direccion` required
- `ciudad` required
- `barrio` required
- `tipo` required
- `propietario` required

### Reglas de negocio

1. Crear `Propiedad` no crea ni modifica `Inmueble` automáticamente.
2. El arrendador vincula un `Inmueble` existente a una `Propiedad` mediante `PUT /api/inmuebles/{id}` con el campo `propiedad`.
3. Desactivar `Propiedad` (→ INACTIVA) no afecta a los `Inmueble` vinculados ni sus publicaciones.
4. Eliminar `Propiedad` está bloqueado si algún `Inmueble` vinculado tiene publicaciones en estado activo (PUBLICADA, VISITA_AGENDADA, RESERVADA, CONTRATO_EN_FIRMA, ARRENDADA).
5. El propietario de todos los `Inmueble` que se vinculen debe ser el mismo que `Propiedad.propietario`.

### APIs necesarias

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/propiedades` | Lista paginada (propias o todas para admin) |
| GET | `/api/propiedades/{id}` | Detalle con sus unidades |
| GET | `/api/propiedades/{id}/inmuebles` | Unidades de esta propiedad |
| POST | `/api/propiedades` | Crear nueva propiedad |
| PUT | `/api/propiedades/{id}` | Editar datos |
| DELETE | `/api/propiedades/{id}` | Solo si sin publicaciones activas |

---

## 9. Análisis de relaciones clave

### 9.1 Usuario → Notificaciones

```
PerfilUsuario (destinatario)
    │
    └──── 0..N  Notificacion
               ├── NUEVA      (no leída)
               ├── LEIDA      (leída)
               └── ARCHIVADA  (archivada)
```

**¿Cuántas?** Sin límite técnico. Estimación: 10-50 por usuario por mes en uso normal.

**¿Quién las crea?** Exclusivamente la capa de servicio del backend. No existe endpoint POST público. El `remitente` puede ser otro `PerfilUsuario` (cuando su acción disparó el evento) o null (cuando es el sistema/cron).

**¿Quién las lee?** El destinatario ve sus propias. El admin puede ver todas. Nunca un arrendador ve las del arrendatario.

**¿Se eliminan?** No. Ciclo de vida unidireccional: NUEVA → LEIDA → ARCHIVADA.

**¿Se archivan?** Manualmente por el usuario o automáticamente por cron a los 90 días (`LEIDA → ARCHIVADA`). El archivado es el estado final.

---

### 9.2 PublicacionInmueble → HistorialPrecio

```
PublicacionInmueble (canonArriendo)
    │
    └──── 0..N  HistorialPrecio
               ├── [0] precioAnterior=0    precioNuevo=1_500_000  (precio inicial)
               ├── [1] precioAnterior=1_500_000  precioNuevo=1_800_000
               └── [2] precioAnterior=1_800_000  precioNuevo=1_600_000
```

**¿Cuándo se crea?** Dos momentos:
1. Cuando se crea la publicación (precio inicial, `precioAnterior = 0`)
2. Cuando se edita `canonArriendo` y el valor cambia (registra el anterior antes de sobrescribir)

**¿Cuándo cambia?** Nunca. Es inmutable. El historial se construye añadiendo registros, no editando existentes.

**¿Qué conserva?** Precio anterior, precio nuevo, timestamp exacto, motivo (opcional), tipo (INMUEBLE o ROOMIE).

**¿Es sobre ContratoArriendo?** No. El precio del contrato firmado no debería cambiar. Lo que cambia es el precio de oferta en la publicación. Si un arrendador renegocia el precio de un contrato existente, ese cambio se documenta en `ContratoArriendo.observaciones` o genera un nuevo contrato — no en `HistorialPrecio`.

---

### 9.3 Inmueble → OcupacionUnidad

```
Inmueble (unidad física)
    │
    └──── 0..N  OcupacionUnidad
               ├── ACTIVA    (ocupante actual)   fechaSalida = null
               ├── FINALIZADA (ocupante anterior) fechaSalida = 2025-06-30
               └── CANCELADA  (salida anticipada) fechaSalida = 2024-11-15
```

**¿Puede existir más de una?** Sí, en el tiempo (historial). En un mismo momento, regla de negocio: solo 1 `ACTIVA` con `esPrincipal = true` por Inmueble.

**¿Puede existir histórico?** Sí. Ese es el propósito principal. El historial completo de quién ocupó la unidad y cuándo.

**¿Cómo termina?** Al transicionar `ContratoArriendo → FINALIZADO|CANCELADO` o `AcuerdoConvivencia → FINALIZADO|CANCELADO`, el servicio correspondiente actualiza `OcupacionUnidad.estado` y setea `fechaSalida = hoy`. La OcupacionUnidad nunca se elimina.

**Fase 1 vs. Fase 2:**
- Fase 1: 1 OcupacionUnidad por contrato (solo el titular, `esPrincipal = true`)
- Fase 2: múltiples ocupantes por contrato (cónyuge, hijos mayores como coadyuvantes)

---

### 9.4 AcuerdoConvivencia → Roomie → Contrato

```
SolicitudRoomie (APROBADA)
        │ automático
        ↓
AcuerdoConvivencia ──────────────────────────────────────────────────────────┐
│ anfitrion = PerfilUsuario (quien ofrece la habitación)                     │
│ roomie    = PerfilUsuario (quien ocupa la habitación)                      │
│ inmueble  = Inmueble (donde ocurre la convivencia)                         │
│ publicacionRoomie = PublicacionRoomie (origen)                             │
│                                                                             │
│ [Estado VIGENTE] → crea OcupacionUnidad(roomie, esPrincipal=false)         │
│                                                                             │
│ [Estado FINALIZADO] → habilita Calificacion (ventana 15 días):             │
│   - ARRENDATARIO_A_ROOMIE: anfitrión califica al roomie                   │
│   - ROOMIE_A_ARRENDATARIO: roomie califica al anfitrión                   │
└─────────────────────────────────────────────────────────────────────────────┘

¿Cómo se relacionan con ContratoArriendo?
El ContratoArriendo es del anfitrión con el PROPIETARIO del inmueble.
El AcuerdoConvivencia es del anfitrión con el ROOMIE.
Son contratos paralelos, no jerárquicos.

Caso A: Propietario ofrece habitación en su propia casa
  → Propiedad: PerfilUsuario (propietario)
  → No hay ContratoArriendo para la casa
  → Hay AcuerdoConvivencia entre propietario y roomie

Caso B: Arrendatario subarrinda una habitación
  → Hay ContratoArriendo entre propietario y arrendatario (para toda la unidad)
  → Hay AcuerdoConvivencia entre arrendatario y roomie (para la habitación)
  → Los dos son independientes en el modelo de datos
  → La relación implícita es por Inmueble (mismo inmueble en ambos)
```

---

## 10. Matriz de impacto en entidades existentes

### Fase 1 — Sin modificaciones a entidades existentes

| Entidad existente | ¿Se modifica en Fase 1? | Nota |
|---|---|---|
| PerfilUsuario | NO | Las notificaciones se obtienen por query |
| Inmueble | NO | OcupacionUnidad referencia Inmueble, no al revés |
| PublicacionInmueble | NO | HistorialPrecio referencia Publicacion, no al revés |
| PublicacionRoomie | NO | Mismo criterio |
| SolicitudArriendo | NO | — |
| SolicitudRoomie | NO | (Fase 2: SolicitudRoomieServiceImpl crea AcuerdoConvivencia) |
| VisitaProgramada | NO | — |
| ContratoArriendo | NO | (Fase 2: ContratoArriendoServiceImpl crea OcupacionUnidad) |
| Calificacion | NO | (Fase 2: añadir `acuerdoConvivencia` nullable) |
| MultimediaInmueble | NO | — |
| DocumentoUsuario | NO | — |

> **Conclusión:** La generación de Fase 1 con JHipster es completamente no-destructiva.
> JHipster solo crea archivos nuevos. No regenera archivos existentes.

### Fase 2 — Modificaciones manuales planificadas

| Entidad | Campo añadido | Tipo | Nullable | Razón |
|---|---|---|---|---|
| `Calificacion` | `acuerdoConvivencia` | ManyToOne → AcuerdoConvivencia | SÍ | Ancla para calificaciones roomie |
| `Inmueble` | `propiedad` | ManyToOne → Propiedad | SÍ | Agrupa unidades del mismo edificio |

> Ambas modificaciones son backward-compatible en MongoDB: los documentos existentes
> tendrán el campo ausente (= null), lo que no causa errores en Spring Data MongoDB.

---

## 11. JDL propuesto — solo adiciones

El siguiente bloque es el fragmento que se **añadiría** al final del `jhipster-jdl.jdl` existente.
No reemplaza nada. No modifica nada existente.

```jdl
// ════════════════════════════════════════════════════════════
// FASE 1 — NUEVAS ENTIDADES
// Añadir a la lista de application { entities ... }:
//   Notificacion, AcuerdoConvivencia, OcupacionUnidad, HistorialPrecio
// ════════════════════════════════════════════════════════════

// ── Enums nuevos Fase 1 ──────────────────────────────────────────────────

/** Categorías de notificación in-app */
enum TipoNotificacion {
  SOLICITUD_RECIBIDA,        // Arrendador recibió solicitud de arriendo
  SOLICITUD_APROBADA,        // Arrendatario: solicitud aprobada
  SOLICITUD_RECHAZADA,       // Arrendatario: solicitud rechazada
  SOLICITUD_CANCELADA,       // Arrendador: solicitante canceló
  VISITA_CONFIRMADA,         // Visitante: visita confirmada
  VISITA_CANCELADA,          // Ambas partes: visita cancelada
  VISITA_RECORDATORIO,       // Recordatorio 24 h antes (cron, Fase 2)
  POSTULANTE_SELECCIONADO,   // Arrendatario: fue seleccionado
  CONTRATO_GENERADO,         // Arrendatario: contrato listo para revisar
  CONTRATO_FIRMADO,          // Arrendador: arrendatario firmó
  CONTRATO_POR_VENCER,       // Ambas partes: vence en 30/15/7 días (cron, Fase 2)
  CONTRATO_VENCIDO,          // Ambas partes: contrato venció
  CALIFICACION_RECIBIDA,     // Calificado: alguien lo calificó
  CALIFICACION_PENDIENTE,    // Recordatorio: 15 días para calificar
  ACUERDO_CONVIVENCIA_ACTIVO, // Roomie: acuerdo de convivencia activo
  SISTEMA                    // Notificación genérica del sistema
}

/** Estado de lectura de una notificación */
enum EstadoNotificacion {
  NUEVA,      // No leída
  LEIDA,      // Leída por el destinatario
  ARCHIVADA   // Archivada (no aparece en listado principal)
}

/** Estado de un acuerdo de convivencia roomie */
enum EstadoAcuerdo {
  BORRADOR,   // Generado automáticamente; roomie no ha aceptado
  VIGENTE,    // Roomie aceptó; convivencia activa
  FINALIZADO, // Terminó normalmente; habilita calificaciones
  CANCELADO   // Cancelado antes o durante la vigencia
}

/** Estado de ocupación física de una unidad */
enum EstadoOcupacion {
  ACTIVA,     // Persona ocupa actualmente la unidad
  FINALIZADA, // Salió al terminar normalmente el contrato/acuerdo
  CANCELADA   // Salió por cancelación anticipada
}

/** Tipo de publicación cuyo precio se registra */
enum TipoRegistroPrecio {
  INMUEBLE,  // Cambio en PublicacionInmueble.canonArriendo
  ROOMIE     // Cambio en PublicacionRoomie.valorMensual
}

// ── Entidades nuevas Fase 1 ──────────────────────────────────────────────

/**
 * Notificación in-app entregada a un usuario cuando ocurre
 * un evento relevante. Creadas exclusivamente por la capa de servicio.
 * No se eliminan físicamente; ciclo de vida: NUEVA → LEIDA → ARCHIVADA.
 */
entity Notificacion {
  tipo            TipoNotificacion  required
  titulo          String            required maxlength(200)
  mensaje         TextBlob          required
  estado          EstadoNotificacion required
  fechaCreacion   Instant           required
  fechaLectura    Instant
  urlAccion       String            maxlength(500)
  entidadId       String            maxlength(100)
  entidadTipo     String            maxlength(50)
}

/**
 * Acuerdo de convivencia entre quien ofrece una habitación (anfitrión)
 * y quien la ocupa (roomie). Equivalente funcional del ContratoArriendo
 * para la relación arrendatario↔roomie. Se genera automáticamente
 * cuando SolicitudRoomie → APROBADA.
 */
entity AcuerdoConvivencia {
  fechaInicio             LocalDate     required
  fechaFin                LocalDate
  estado                  EstadoAcuerdo required
  reglasConvivencia       TextBlob
  aceptaTerminosAnfitrion Boolean       required
  aceptaTerminosRoomie    Boolean       required
  fechaLimiteCalificacion Instant
  observaciones           TextBlob
  fechaCreacion           Instant       required
}

/**
 * Registro histórico de quién habitó físicamente cada Inmueble
 * y durante qué período. Separa el concepto "quién firmó el contrato"
 * de "quién vivió en el inmueble". Creado automáticamente cuando
 * ContratoArriendo o AcuerdoConvivencia pasan a VIGENTE.
 */
entity OcupacionUnidad {
  fechaIngreso    LocalDate       required
  fechaSalida     LocalDate
  esPrincipal     Boolean         required
  estado          EstadoOcupacion required
  observaciones   TextBlob
  fechaCreacion   Instant         required
}

/**
 * Log inmutable de cambios de precio en una publicación.
 * Se crea automáticamente por el servicio antes de modificar
 * canonArriendo o valorMensual. Nunca se actualiza ni elimina.
 */
entity HistorialPrecio {
  precioAnterior  Long              required min(0)
  precioNuevo     Long              required min(1)
  motivo          String            maxlength(300)
  tipo            TipoRegistroPrecio required
  fechaCambio     Instant           required
}

// ── Relaciones nuevas Fase 1 ─────────────────────────────────────────────

relationship ManyToOne {
  // Notificacion
  Notificacion{destinatario(primerNombre) required} to PerfilUsuario
  Notificacion{remitente(primerNombre)} to PerfilUsuario

  // AcuerdoConvivencia
  AcuerdoConvivencia{anfitrion(primerNombre) required} to PerfilUsuario
  AcuerdoConvivencia{roomie(primerNombre) required}    to PerfilUsuario
  AcuerdoConvivencia{publicacionRoomie(titulo) required} to PublicacionRoomie
  AcuerdoConvivencia{inmueble(nombre) required}        to Inmueble

  // OcupacionUnidad
  OcupacionUnidad{inmueble(nombre) required}           to Inmueble
  OcupacionUnidad{ocupante(primerNombre) required}     to PerfilUsuario
  OcupacionUnidad{contrato(numeroContrato)}            to ContratoArriendo
  OcupacionUnidad{acuerdoConvivencia}                  to AcuerdoConvivencia

  // HistorialPrecio
  HistorialPrecio{publicacionInmueble(titulo)}         to PublicacionInmueble
  HistorialPrecio{publicacionRoomie(titulo)}           to PublicacionRoomie
}

// ── Opciones de generación — nuevas entidades ────────────────────────────

paginate Notificacion, AcuerdoConvivencia, OcupacionUnidad, HistorialPrecio
  with pagination

service Notificacion, AcuerdoConvivencia, OcupacionUnidad, HistorialPrecio
  with serviceImpl


// ════════════════════════════════════════════════════════════
// FASE 2 — PROPIEDAD (diseñada, NO generar aún)
// ════════════════════════════════════════════════════════════

// ── Enums nuevos Fase 2 ──────────────────────────────────────────────────

/** Tipo de propiedad inmobiliaria */
enum TipoPropiedad {
  EDIFICIO,               // Edificio de apartamentos
  CONJUNTO_RESIDENCIAL,   // Conjunto cerrado
  CASA_MULTIFAMILIAR,     // Casa con unidades independientes
  COMPLEJO_COMERCIAL,     // Locales u oficinas en un mismo inmueble
  OTRO
}

/** Estado de registro de una propiedad */
enum EstadoPropiedad {
  ACTIVA,    // Registrada y con unidades activas
  INACTIVA   // Desactivada por el propietario
}

// ── Entidad Propiedad (NO generar en Fase 1) ─────────────────────────────

/**
 * Agrupa múltiples Inmueble que pertenecen al mismo edificio o complejo.
 * Formaliza el "escenario Torre Norte": Apt 501, 502 y 601 son unidades
 * del mismo edificio. Requiere modificar Inmueble (añadir FK propiedad).
 * GENERAR EN FASE 2.
 */
// entity Propiedad {
//   nombre             String          required maxlength(200)
//   descripcion        TextBlob
//   direccion          String          required maxlength(300)
//   ciudad             String          required
//   localidad          String
//   barrio             String          required
//   latitud            Double
//   longitud           Double
//   tipo               TipoPropiedad   required
//   totalUnidades      Integer         min(1)
//   totalPisos         Integer         min(1)
//   anoContruccion     Integer         min(1900) max(2100)
//   valorAdministracion Long           min(0)
//   amenidades         TextBlob
//   estado             EstadoPropiedad required
//   fechaRegistro      Instant         required
// }

// ── Relaciones Propiedad (comentadas — Fase 2) ───────────────────────────

// relationship ManyToOne {
//   Propiedad{propietario(primerNombre) required} to PerfilUsuario
// }

// Modificación manual a Inmueble en Fase 2 (sin regenerar con JHipster):
// Añadir campo: @DBRef private Propiedad propiedad;
// En JDL (Fase 2):
// relationship ManyToOne {
//   Inmueble{propiedad(nombre)} to Propiedad
// }

// paginate Propiedad with pagination
// service Propiedad with serviceImpl
```

---

## 12. Decisiones pendientes de aprobación

Las siguientes decisiones de diseño requieren validación antes de generar el código:

### D-A: `Notificacion.remitente` — ¿incluir o no?

| Opción | Ventaja | Desventaja |
|---|---|---|
| **A — Incluir `remitente` nullable** | Muestra "Carlos Ramírez aprobó tu solicitud" en la notificación | Requiere pasar el PerfilUsuario del actor en cada llamada al NotificacionService |
| **B — Omitir `remitente`** | Más simple; el destinatario ve el título y acciona | La UI no puede mostrar quién generó la acción sin un join adicional |

**Recomendación:** Incluir `remitente` nullable. Agrega valor UX y el costo de implementación es bajo.

---

### D-B: `AcuerdoConvivencia` — ¿incluir `fechaFin` obligatoria?

| Opción | Ventaja | Desventaja |
|---|---|---|
| **A — `fechaFin` nullable (acuerdos indefinidos)** | Realista: muchos roomies no tienen fecha de salida fija | Complica el cálculo automático de `fechaLimiteCalificacion` |
| **B — `fechaFin` required** | Simplifica la lógica de negocio | No refleja la realidad: roomies indefinidos son comunes |

**Recomendación:** `fechaFin` nullable. Al finalizar manualmente, el servicio setea `fechaSalida = hoy` y calcula `fechaLimiteCalificacion = hoy + 15 días`.

---

### D-C: `HistorialPrecio` — ¿registrar el precio inicial al crear publicación?

| Opción | Ventaja | Desventaja |
|---|---|---|
| **A — Sí, primer registro con `precioAnterior = 0`** | Historial completo desde el inicio | El registro con `precioAnterior = 0` puede confundir |
| **B — Solo registrar cambios (no el precio inicial)** | Más limpio semánticamente | El historial puede parecer vacío para publicaciones sin cambios |

**Recomendación:** Opción A. El primer registro con `precioAnterior = 0` documenta explícitamente el precio de lanzamiento. Es útil para analytics.

---

### D-D: `OcupacionUnidad` — ¿existe en Fase 1 o es solo Fase 2?

El problema: en Fase 1 se genera `OcupacionUnidad` como entidad Java, pero la lógica que la CREA (en `ContratoArriendoServiceImpl`) se implementa en Fase 2 (cuando se modifican servicios existentes).

**Consecuencia:** En Fase 1, la entidad existe como Java + MongoDB + API, pero estará vacía hasta que Fase 2 conecte los eventos.

**Recomendación:** Aceptar este estado. La entidad se genera en Fase 1, los datos se empiezan a crear en Fase 2. Es el modelo correcto.

---

### D-E: ¿Añadir `Propiedad` al seed de datos (DevDataSeeder)?

Los seeds actuales tienen Torre Norte con 3 apartamentos en la misma dirección manual. Con `Propiedad` en Fase 2, habría que actualizar el seed.

**Recomendación:** Sí, actualizar el seed en Fase 2 cuando se genere `Propiedad`. No tocar seeds ahora.

---

*Documento listo para revisión. Ningún archivo del proyecto fue modificado al crear este documento.*  
*Una vez aprobado, el siguiente paso es actualizar `jhipster-jdl.jdl` y ejecutar la generación de Fase 1.*
