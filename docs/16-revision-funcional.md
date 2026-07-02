# 16 — Revisión Funcional Independiente

> **Auditor:** Arquitecto de Software Senior / Analista Funcional Externo  
> **Metodología:** Lectura crítica de los 15 documentos sin acceso al código. Las observaciones se basan exclusivamente en la documentación producida.  
> **Fecha de revisión:** 2026-07-01  
> **Alcance:** Validación del modelo de negocio. No se propone código. No se modifica nada.

---

## 1. Resumen ejecutivo

La documentación producida cubre correctamente la estructura básica del sistema. Se definen los actores principales, los flujos centrales y las entidades. Sin embargo, después de una revisión crítica de los 15 documentos se identifican **47 observaciones** entre inconsistencias, reglas de negocio faltantes, casos de uso imposibles o incompletos, y riesgos de diseño.

Los problemas más graves son:

1. **El historial no está modelado como tal.** El sistema guarda el estado actual de las cosas pero no los eventos que cambiaron ese estado. Esto afecta publicaciones, contratos, ocupantes e inmuebles.

2. **El ciclo de vida de las publicaciones está incompleto.** Falta el estado `RESERVADA` y el estado `ARCHIVADA`. Esto genera inconsistencias cuando la propiedad está en proceso de ser arrendada pero sigue apareciendo como disponible.

3. **Las calificaciones de roomie están rotas en el modelo actual.** La entidad `Calificacion` tiene un campo obligatorio `contrato → ContratoArriendo`, pero los roomies no tienen contratos. El modelo no puede funcionar sin una corrección explícita.

4. **Las notificaciones mencionadas en todos los flujos no existen.** En cada diagrama de secuencia aparece "SYS notifica" — pero la entidad `Notificacion` no está implementada. Los flujos descritos son funcional y técnicamente incorrectos tal como están.

5. **Existen contradicciones directas entre documentos** sobre quién controla los estados de los contratos, qué condiciones habilitan las visitas y qué pasa cuando un contrato termina.

La documentación es un punto de partida válido, pero **no puede ser aprobada para implementación** en su estado actual sin resolver las observaciones de prioridad CRÍTICA y ALTA descritas en este documento.

---

## 2. Aspectos bien diseñados

Antes de las observaciones, se reconocen los siguientes aspectos del modelo como correctamente planteados:

| Aspecto | Por qué es correcto |
|---|---|
| **Separación User / PerfilUsuario** | Correcta separación de autenticación y dominio de negocio. Permite que JHipster gestione la seguridad sin contaminar el modelo de negocio. |
| **Inmueble → OneToMany → PublicacionInmueble** | El historial de publicaciones está soportado desde el modelo. Varios anuncios a lo largo del tiempo, cada uno con sus datos, es el diseño correcto. |
| **Inmueble → OneToMany → ContratoArriendo** | El historial de contratos está implícito en el modelo. Permite el escenario de múltiples inquilinos sucesivos. |
| **Calificacion vinculada al contrato** | Anclar la calificación a un contrato específico es correcto para arrendador↔arrendatario. Evita calificaciones sin contexto. |
| **Rol Roomie como flujo independiente** | Usar entidades propias (`PublicacionRoomie`, `SolicitudRoomie`) en lugar de reutilizar el flujo de arriendo es la decisión correcta. |
| **Estados en SolicitudArriendo** | El ciclo CREADA → EN_REVISION → APROBADA/RECHAZADA/CANCELADA es un estándar correcto para este tipo de proceso. |
| **TipoInmueble como enum** | Limitar el tipo a un conjunto definido evita datos inconsistentes en búsquedas y filtros. |
| **EstadoContrato con ciclo de vida explícito** | Los estados BORRADOR → PENDIENTE_FIRMA → VIGENTE → FINALIZADO/CANCELADO son correctos y completos para el flujo de contrato. |
| **Campo `verificado` independiente de `habilitadoRoomie`** | Separar la verificación de identidad de la habilitación para roomie es correcto: son dos permisos con lógica diferente. |
| **Principio de "cada unidad es independiente"** | Tratar cada habitación/apartamento/local como un `Inmueble` independiente con su propio ciclo de vida es la decisión correcta para un MVP. |

---

## 3. Inconsistencias encontradas

### INC-01 — El diagrama de estados de PublicacionInmueble es diferente en dos documentos

**Documentos afectados:** `04-flujo-general.md` y `06-arrendador.md`

En `04-flujo-general.md` el diagrama de estados muestra:
```
ARRENDADO → PUBLICADO (cuando contrato finaliza)
```
En `06-arrendador.md` el diagrama muestra estados adicionales y la misma transición pero con diferente origen. Los dos diagramas no son equivalentes. Hay transiciones que aparecen en uno y no en el otro.

**Pregunta crítica:** ¿Cuál es el diagrama autoritativo? ¿El estado `FINALIZADO` de la publicación es terminal o puede volver a `PUBLICADO`?

---

### INC-02 — Las calificaciones de roomie no pueden vincularse al ContratoArriendo del inquilino

**Documentos afectados:** `11-calificaciones.md`, `14-entidades.md`, `08-roomie.md`

El modelo define:
```
Calificacion.contrato → ContratoArriendo
```

Los tipos de calificación `ARRENDATARIO_A_ROOMIE` y `ROOMIE_A_ARRENDATARIO` son calificaciones entre el arrendatario y el roomie. No existe un `ContratoArriendo` entre ellos — el contrato es entre el arrendador y el arrendatario. Vincular la calificación roomie al contrato del arrendatario es semánticamente incorrecto.

**El mismo `08-roomie.md` lo menciona como pendiente de validación**, pero `14-entidades.md` lo describe como el modelo actual sin reconocer la contradicción.

---

### INC-03 — Las notificaciones descritas en los flujos no existen en el modelo

**Documentos afectados:** Todos los diagramas de secuencia (04, 06, 07, 08, 09)

En cada flujo de secuencia aparece: `SYS->>ARR: Notificación de nueva solicitud` o equivalentes. La entidad `Notificacion` está marcada como "no implementada" en `13-modelo-negocio.md`. Esto significa que todos los flujos que incluyen una notificación describen comportamiento que el sistema **actualmente no puede hacer**.

Los documentos son funcionalmente incorrectos: describen un sistema que notifica, cuando el sistema actual no notifica nada.

---

### INC-04 — Contradicción en la precondición de solicitud de visita

**Documentos afectados:** `03-casos-de-uso.md` (CU-14), `07-arrendatario.md`, `09-visitas.md`

- CU-14 dice: "Precondición: Existe una solicitud en estado **APROBADA o EN_REVISION**"
- `07-arrendatario.md` dice: "El arrendatario solicita visita desde su solicitud **APROBADA**"
- `09-visitas.md` dice: "Visita requiere solicitud" sin especificar el estado requerido

¿Puede el arrendatario solicitar una visita cuando su solicitud aún está en `EN_REVISION`? Es una pregunta de negocio fundamental no resuelta, con tres respuestas distintas en tres documentos.

---

### INC-05 — El flujo de verificación de usuario es inconsistente

**Documentos afectados:** `04-flujo-general.md`, `05-registro-login.md`, `06-arrendador.md`

- `04-flujo-general.md` muestra que el usuario DEBE cargar documentos y esperar aprobación ANTES de poder elegir su rol.
- `05-registro-login.md` muestra que si el usuario no tiene PerfilUsuario, se lo redirige a completarlo — pero plantea esto como "pendiente de validación".
- `06-arrendador.md` muestra un flujo donde la verificación bloquea al arrendador hasta ser aprobado.

¿La verificación de documentos es OBLIGATORIA para operar en la plataforma o es OPCIONAL? ¿Se puede publicar un inmueble sin estar verificado? No hay una respuesta definitiva.

---

### INC-06 — Casos de uso descritos en el diagrama pero sin detalle

**Documento afectado:** `03-casos-de-uso.md`

El diagrama general muestra: CU-22, CU-23, CU-24, CU-32, CU-41, CU-43. Ninguno tiene descripción detallada en el documento. Seis casos de uso están nombrados pero no documentados.

---

### INC-07 — La restricción de eliminación de inmueble está en conflicto

**Documentos afectados:** `02-actores.md`, `06-arrendador.md`

- `02-actores.md` afirma como regla: "Solo si no tiene contratos activos" (capacidad: eliminar inmueble).
- `06-arrendador.md` plantea "Pendiente de validación: ¿Cuáles son exactamente las restricciones para eliminar un inmueble?"

Un documento afirma la regla. El otro pregunta si existe. Son contradictorios.

---

### INC-08 — El campo `arrendador` en ContratoArriendo no es el mismo que el propietario del Inmueble

**Documentos afectados:** `10-contratos.md`, `14-entidades.md`

El contrato tiene `arrendador → PerfilUsuario`. El inmueble tiene `propietario → PerfilUsuario`. En la mayoría de los casos son el mismo usuario, pero el modelo no lo valida. Un escenario posible: el propietario del inmueble es A, pero alguien diferente B es el arrendador del contrato. El sistema lo permite sin restricción.

---

## 4. Casos de uso faltantes

### CU-F01 — Renovación de contrato

El sistema describe el historial de contratos (múltiples contratos por inmueble), pero nunca describe el caso de **renovación**. ¿Qué pasa cuando el mismo inquilino quiere continuar al vencimiento?

**Escenario no documentado:**
1. Contrato actual vence el 31 de diciembre
2. Ambas partes acuerdan continuar
3. El arrendador ¿crea un nuevo `ContratoArriendo` desde cero? ¿Hay un proceso de renovación con precio diferente?
4. ¿El estado de la publicación cambia durante la renovación?

---

### CU-F02 — Expiración automática del contrato

¿Qué ocurre cuando la `fechaFin` del contrato llega y nadie hace nada? El sistema no documenta ningún mecanismo automático. Si el contrato sigue en `VIGENTE` 6 meses después de su `fechaFin`, las calificaciones no se habilitan, la publicación sigue en `ARRENDADO`, y el historial es incorrecto.

---

### CU-F03 — Publicación pausada con solicitudes activas

Si el arrendador pausa una publicación (PUBLICADO → PAUSADO) y hay 3 solicitudes en estado `CREADA`:
- ¿Las solicitudes siguen vigentes?
- ¿El arrendador puede seguir procesándolas?
- ¿Se notifica a los candidatos?
- ¿Los candidatos pueden cancelar?

No está documentado.

---

### CU-F04 — Eliminación de publicación con solicitudes en curso

Si el arrendador finaliza o elimina una publicación que tiene solicitudes en estado `CREADA` o `EN_REVISION`:
- ¿Qué pasa con esas solicitudes?
- ¿Se cancelan automáticamente?
- ¿Se notifica a los candidatos?

No está documentado.

---

### CU-F05 — Salida de un roomie antes de que el contrato del arrendatario termine

El flujo del roomie describe "fin de convivencia → calificación". Pero nunca describe qué activa ese "fin de convivencia". Si el roomie abandona voluntariamente, o si el arrendatario lo expulsa, ¿cómo se registra en el sistema?

**Problema concreto:** El `SolicitudRoomie` llega a `APROBADA` y... no hay más estados. No hay manera de saber si el roomie actualmente vive ahí o se fue hace 3 meses.

---

### CU-F06 — Múltiples roomies en el mismo inmueble — ¿quién califica a quién?

Si en un apartamento hay 2 roomies aprobados (Juan y Laura), y el arrendatario termina su contrato:
- ¿El arrendatario debe calificar a cada roomie individualmente?
- ¿Cada roomie califica al arrendatario por separado?
- ¿Los roomies deben salir cuando el arrendatario sale?
- El flujo de calificación de roomie no cubre grupos de roomies simultáneos.

---

### CU-F07 — Incumplimiento del arrendatario (impago, daños)

No existe ningún flujo que documente qué hace el arrendador cuando el inquilino incumple. En el negocio real esto incluye:
- Reporte de incumplimiento
- Notificación al administrador de la plataforma
- Posible terminación anticipada del contrato
- Impacto en la reputación del inquilino

---

### CU-F08 — Arrendador que quiere ceder la administración de un inmueble

El modelo asume que el propietario del inmueble siempre lo gestiona directamente. En el mercado real colombiano es común que un propietario use una inmobiliaria o un administrador delegado. El sistema no soporta este escenario ni lo descarta explícitamente.

---

### CU-F09 — Candidatos rechazados que vuelven a postular

Si un arrendatario es rechazado por un arrendador para una publicación, ¿puede volver a enviar solicitud a la misma publicación? CU-21 solo documenta que no puede tener una solicitud activa duplicada, pero no prohibe una segunda solicitud después de que la primera fue RECHAZADA.

---

### CU-F10 — Validación de disponibilidad del inmueble al crear contrato

Cuando el arrendador crea un contrato, ¿el sistema valida que no existe otro contrato VIGENTE para el mismo inmueble? Esto se menciona como "regla de negocio pendiente" pero no tiene un caso de uso documentado que describa qué pasa si se viola.

---

## 5. Reglas de negocio faltantes

### RN-01 — ¿El contrato pasa a FINALIZADO automáticamente al vencer?

No hay respuesta. Si es manual (el arrendador lo hace), el estado puede permanecer en VIGENTE indefinidamente. Si es automático, se requiere un scheduler — que no existe en el modelo técnico.

**Impacto:** Si no se resuelve, habrá contratos técnicamente vencidos pero marcados como VIGENTE. Las publicaciones seguirán en estado ARRENDADO para inmuebles vacíos.

---

### RN-02 — ¿Solo un contrato VIGENTE por inmueble simultáneamente?

Esta regla se menciona como "debería" en múltiples documentos pero nunca se declara como regla definitiva con mecanismo de enforcement. El modelo actual permite crear dos contratos VIGENTES para el mismo inmueble.

---

### RN-03 — ¿Qué pasa con las solicitudes restantes cuando se firma un contrato?

Si hay 5 solicitudes CREADA o EN_REVISION para una publicación, y el arrendador aprueba a uno y firma el contrato, ¿las otras 4 se cancelan automáticamente? ¿O quedan en su estado actual?

---

### RN-04 — Plazo para emitir calificación

`11-calificaciones.md` lo menciona como "Pendiente de validación: ¿Cuántos días después del cierre del contrato puede calificarse?" Esto NO es pendiente de validación — es una regla de negocio crítica. Sin plazo, un usuario puede calificar un contrato de hace 3 años. Con plazo muy corto, los usuarios pierden la oportunidad.

---

### RN-05 — ¿La publicación debe volver a PUBLICADO automáticamente cuando el contrato termina?

Si el contrato pasa de VIGENTE a FINALIZADO, la publicación está en estado ARRENDADO. ¿Vuelve a PUBLICADO automáticamente? ¿O queda en ARRENDADO hasta que el arrendador la reactive manualmente? Ambas opciones tienen consecuencias de negocio distintas.

---

### RN-06 — ¿Quién puede crear, editar y eliminar cada entidad?

Los documentos describen lo que cada actor "puede hacer" en general, pero no hay una matriz de permisos CRUD explícita por entidad. Por ejemplo:
- ¿Puede el arrendatario editar el contrato una vez creado?
- ¿Puede el arrendador eliminar una VisitaProgramada FINALIZADA?
- ¿Puede el administrador editar una Calificacion?

Sin esta matriz, la implementación tomará decisiones de permiso sin respaldo funcional.

---

### RN-07 — Validación de documentos requeridos para la verificación

`12-multimedia.md` propone qué documentos son requeridos por tipo de usuario, pero esto está marcado como propuesta, no como regla. ¿Cuántos documentos y de qué tipo debe tener un usuario para que `verificado = true`? ¿Lo define el admin caso por caso? ¿O hay una lista predefinida?

---

### RN-08 — ¿Puede un arrendatario con `verificado = false` enviar solicitudes?

Los flujos muestran que el arrendatario debe verificarse antes de actuar, pero esto no está claramente establecido como restricción en todos los documentos. `02-actores.md` dice que el arrendatario puede enviar solicitudes (sin mencionar el requisito de verificación), mientras `04-flujo-general.md` muestra la verificación como prerequisito.

---

### RN-09 — ¿Qué pasa con la multimedia cuando se elimina un inmueble?

Si se borra un `Inmueble`, ¿se borran en cascada sus `MultimediaInmueble`? ¿Y las URLs que apuntan a archivos externos? El modelo no define el comportamiento de cascada.

---

### RN-10 — Concurrencia: dos arrendatarios solicitan el mismo inmueble simultáneamente

Si dos arrendatarios envían solicitud a la misma publicación al mismo tiempo, el sistema crea dos `SolicitudArriendo`. Esto es correcto. Pero cuando el arrendador aprueba la primera solicitud y firma el contrato, ¿el sistema bloquea la segunda automáticamente o la deja en `CREADA` indefinidamente?

---

### RN-11 — ¿Cuál es el estado inicial de `habilitadoRoomie`?

Cuando se crea un `PerfilUsuario`, ¿`habilitadoRoomie` es `false` por defecto? ¿Puede el usuario activarlo él mismo o requiere aprobación del admin? Hay tres documentos con tres respuestas diferentes a esto.

---

### RN-12 — Historial de precios de publicación

Si el arrendador edita `canonArriendo` en una publicación existente, el precio anterior se pierde. Para un candidato que vio el inmueble a $1.200.000 y un mes después el precio es $1.500.000, no hay forma de verificar el precio original. El sistema no tiene historial de precios.

---

### RN-13 — ¿Puede el arrendatario cancelar una solicitud APROBADA?

Los estados de `SolicitudArriendo` incluyen `CANCELADA`, y el arrendatario puede cancelar en estado `CREADA` y `EN_REVISION`. Pero: ¿puede el arrendatario cancelar una solicitud que ya fue `APROBADA`, antes de que se firme el contrato? El documento `07-arrendatario.md` describe que puede, pero no dice qué pasa con el proceso de contrato en ese caso.

---

## 6. Riesgos del modelo actual

### RIESGO-01 — Crítico: Ciclo de vida de publicación incompleto

| Atributo | Valor |
|---|---|
| **Prioridad** | CRÍTICA |
| **Categoría** | Diseño de estados |

El ciclo de vida documentado es: BORRADOR → PUBLICADO → PAUSADO/ARRENDADO → FINALIZADO.

**Falta el estado `RESERVADA`:** Cuando una solicitud es APROBADA pero el contrato no está firmado aún, la publicación sigue en `PUBLICADO`. Esto permite que otros arrendatarios sigan enviando solicitudes a un inmueble que ya tiene un candidato seleccionado. Esto genera solicitudes fantasma que el arrendador debe gestionar ineficientemente.

**Falta el estado `ARCHIVADA`:** Las publicaciones FINALIZADAS quedan en el historial pero sin distinción entre "esta publicación se cerró normalmente" y "esta publicación fue archivada manualmente". No hay forma de separarlas en la UI.

**Impacto si no se corrige:** Múltiples candidatos procesados simultáneamente para el mismo inmueble cuando ya hay un candidato elegido. Confusión en el portal.

**Propuesta:** Agregar transición PUBLICADO → RESERVADA cuando una solicitud pasa a APROBADA. Agregar ARCHIVADA como estado terminal después de FINALIZADA.

---

### RIESGO-02 — Crítico: Calificación roomie rompe el modelo relacional

| Atributo | Valor |
|---|---|
| **Prioridad** | CRÍTICA |
| **Categoría** | Integridad del modelo |

La entidad `Calificacion` tiene `contrato → ContratoArriendo` marcado como relación (no nullable implícitamente en el flujo). Los tipos `ARRENDATARIO_A_ROOMIE` y `ROOMIE_A_ARRENDATARIO` no corresponden a ningún `ContratoArriendo` entre esas partes.

**Opciones para resolver (no implementar aún):**
1. Hacer `contrato` nullable en `Calificacion` y agregar `solicitudRoomie → SolicitudRoomie` opcional.
2. Crear una entidad `AcuerdoConvivencia` que actúe como ancla para las calificaciones roomie.
3. Agregar `tipoAncla: CONTRATO | SOLICITUD_ROOMIE` y un campo `idAncla: String` en `Calificacion`.

---

### RIESGO-03 — Alto: El historial no está modelado como eventos

| Atributo | Valor |
|---|---|
| **Prioridad** | ALTA |
| **Categoría** | Trazabilidad |

El modelo guarda el estado actual de las entidades pero no el historial de cambios. Esto afecta:

| Entidad | Dato histórico perdido |
|---|---|
| `PublicacionInmueble` | Historial de precios, por qué cambió de estado |
| `Inmueble` | Historial de cambios de características (área, habitaciones) |
| `SolicitudArriendo` | Por qué fue rechazada, quién la procesó y cuándo |
| `ContratoArriendo` | Por qué fue cancelado, quién lo canceló |
| `DocumentoUsuario` | Historial de versiones de documentos (usuario cargó versión 1, fue rechazada, cargó versión 2) |
| `SolicitudRoomie` | Historial de cambios |

**Impacto si no se corrige:** Imposible hacer auditorías. En caso de disputa entre arrendador y arrendatario, no hay evidencia del estado histórico del sistema.

**Propuesta:** Agregar campos de auditoría mínimos en cada entidad que tiene estados: `fechaUltimoCambioEstado: Instant`, `motivoCambioEstado: String`, `modificadoPor: → User`. Para un historial completo, considerar Event Sourcing o una tabla `EventoEntidad` genérica.

---

### RIESGO-04 — Alto: Roomie no tiene ciclo de vida post-aprobación

| Atributo | Valor |
|---|---|
| **Prioridad** | ALTA |
| **Categoría** | Modelo incompleto |

La `SolicitudRoomie` termina en `APROBADA`. Después de eso, el roomie existe en el inmueble pero el sistema no lo sabe. No hay:
- Fecha de ingreso del roomie
- Fecha de salida del roomie
- Estado actual: ¿está viviendo ahí o ya se fue?
- Registro de qué habitación específica ocupa

**Consecuencia directa:** El arrendatario no puede saber cuántos roomies tiene activos en su inmueble. No puede ver "mis roomies actuales" vs "roomies pasados".

**Propuesta:** Agregar estados a `SolicitudRoomie`: APROBADA → ACTIVA → FINALIZADA. Agregar `fechaIngreso` y `fechaSalida` a la solicitud aprobada. O crear una entidad `ConvivenciaRoomie` post-aprobación.

---

### RIESGO-05 — Alto: Sin mecanismo de expiración automática de estados

| Atributo | Valor |
|---|---|
| **Prioridad** | ALTA |
| **Categoría** | Consistencia de datos |

Varios escenarios dependen de transiciones de estado que nadie activa manualmente:
- Contrato cuya `fechaFin` pasó pero sigue `VIGENTE`
- Publicación que lleva meses en `BORRADOR` sin publicarse
- Visita `CONFIRMADA` cuya fecha ya pasó sin ser marcada como `FINALIZADA`
- Solicitud `CREADA` que lleva semanas sin respuesta

**Impacto:** Los datos históricos son incorrectos. Los paneles muestran información falsa (contratos vigentes que ya no lo son, inmuebles ocupados que están vacíos).

**Propuesta:** Documentar explícitamente cada transición de estado automática que se necesita, y proponer un scheduler (cron job) para ejecutarlas. Incluir en el roadmap como funcionalidad crítica.

---

### RIESGO-06 — Alto: La publicación puede tener múltiples PUBLICADAS simultáneamente

| Atributo | Valor |
|---|---|
| **Prioridad** | ALTA |
| **Categoría** | Integridad de datos |

El modelo permite múltiples `PublicacionInmueble` por `Inmueble`. La regla "solo una publicación PUBLICADA simultáneamente" está marcada como "Pendiente de validación" y "Regla de negocio, no implementada en el modelo actual."

Esto significa que actualmente el arrendador puede crear dos publicaciones activas del mismo inmueble con precios diferentes. Los dos anuncios aparecen en el portal. Los arrendatarios pueden enviar solicitudes a ambos sin saber que son el mismo inmueble.

---

### RIESGO-07 — Medio: Seguridad del JWT en localStorage

| Atributo | Valor |
|---|---|
| **Prioridad** | MEDIA |
| **Categoría** | Seguridad |

`05-registro-login.md` describe explícitamente: "El token JWT se almacena en `localStorage`." Esto es un riesgo de seguridad conocido (XSS). El logout no invalida el token en el servidor. Múltiples sesiones simultáneas no se controlan.

Para una plataforma que maneja contratos y datos personales de colombianos, esto debería evaluarse contra el marco legal de protección de datos personales (Ley 1581 de 2012 y Decreto 1377 de 2013).

**Propuesta:** Documentar explícitamente la decisión técnica de usar localStorage y sus riesgos. Evaluar migración a HttpOnly cookies o token de revocación en el servidor.

---

### RIESGO-08 — Medio: PropietarioInmueble ≠ ArrendadorContrato — sin validación

| Atributo | Valor |
|---|---|
| **Prioridad** | MEDIA |
| **Categoría** | Integridad de negocio |

El modelo permite que el `arrendador` en un `ContratoArriendo` sea un usuario diferente al `propietario` del `Inmueble`. Esto podría ser intencional (delegación) o podría ser un error. No hay ninguna validación ni regla que lo controle.

---

### RIESGO-09 — Medio: La foto principal no tiene unicidad garantizada en el modelo

| Atributo | Valor |
|---|---|
| **Prioridad** | MEDIA |
| **Categoría** | Integridad de datos |

`MultimediaInmueble.principal = Boolean`. El modelo no garantiza que solo exista una `MultimediaInmueble` con `principal = true` por inmueble. La regla existe en la documentación pero no en el modelo de datos. Si falla la lógica de actualización, pueden quedar 3 fotos como "principal".

---

### RIESGO-10 — Medio: `numeroContrato` único solo por convención

| Atributo | Valor |
|---|---|
| **Prioridad** | MEDIA |
| **Categoría** | Integridad de datos |

El campo `numeroContrato` se describe como "único en toda la base de datos" pero MongoDB no tiene un constraint UNIQUE automático. Si no se crea un índice único explícito en MongoDB, dos contratos pueden tener el mismo número, especialmente bajo concurrencia.

---

## 7. Recomendaciones

### REC-01 — Definir el diagrama de estados autoritativo para PublicacionInmueble

Existe contradicción entre documentos. Propongo el siguiente ciclo de vida unificado:

```
BORRADOR → PUBLICADA → RESERVADA → ARRENDADA → FINALIZADA → ARCHIVADA
                  ↓                ↓
               PAUSADA         PUBLICADA (si se libera sin contrato)
                  ↓
              FINALIZADA
```

**Reglas de transición:**
- PUBLICADA → RESERVADA: cuando una `SolicitudArriendo` pasa a `APROBADA`
- RESERVADA → ARRENDADA: cuando el `ContratoArriendo` pasa a `VIGENTE`
- ARRENDADA → PUBLICADA: cuando el contrato pasa a FINALIZADO (automático si el arrendador quiere volver a publicar, o puede ir directo a FINALIZADA)
- Cualquier estado → ARCHIVADA: acción manual del arrendador, estado terminal
- RESERVADA → PUBLICADA: si la solicitud aprobada no genera contrato y se cancela

---

### REC-02 — Resolver el anclaje de Calificacion para roomies antes de implementar

Antes de implementar el flujo de calificación, se debe decidir una de estas opciones:

**Opción A:** Hacer el campo `contrato` nullable en `Calificacion`. Para calificaciones de arriendo, `contrato` es requerido. Para calificaciones roomie, `solicitudRoomie` es requerido. Agregar campo `solicitudRoomie → SolicitudRoomie` nullable.

**Opción B:** Crear una entidad `PeriodoConvivencia` que registre el ingreso y salida del roomie, y anclar las calificaciones roomie a ese período.

**Opción C (no recomendada):** Usar el contrato del arrendatario como ancla para las calificaciones roomie. Semánticamente incorrecto pero técnicamente funcional a corto plazo.

---

### REC-03 — Agregar estado `ACTIVA` y `FINALIZADA` al ciclo de vida del Roomie

La `SolicitudRoomie` debe extenderse o crear `ConvivenciaRoomie` con:
- `fechaIngreso: LocalDate`
- `fechaSalida: LocalDate`
- `estado: ACTIVA | FINALIZADA`
- `motivoSalida: String` (voluntaria, solicitada por anfitrión, fin de contrato)

---

### REC-04 — Agregar campos de auditoría mínimos a entidades con estados

En todas las entidades que tienen un campo `estado` (PublicacionInmueble, SolicitudArriendo, ContratoArriendo, VisitaProgramada, SolicitudRoomie), agregar:
- `fechaUltimoCambioEstado: Instant`
- `motivoCambioEstado: String` (nullable, texto libre del usuario)

---

### REC-05 — Documentar y resolver las 5 reglas de negocio críticas antes de implementar

Las siguientes reglas DEBEN tener respuesta definitiva antes de escribir código:

1. ¿El contrato vence automáticamente o lo marca el arrendador?
2. ¿La publicación vuelve a PUBLICADA automáticamente cuando el contrato termina?
3. ¿Las solicitudes restantes se cancelan cuando se firma un contrato?
4. ¿Cuál es el plazo en días para emitir calificaciones?
5. ¿La verificación de documentos es obligatoria para operar en la plataforma?

---

### REC-06 — Crear una matriz de permisos CRUD por rol y entidad

Antes de implementar cualquier UI, se debe tener una tabla que indique para cada entidad quién puede Create / Read / Update / Delete. Ejemplo:

| Entidad | Visitante | Arrendatario | Arrendador | Admin |
|---|---|---|---|---|
| Inmueble | — | — | CRU (propio), D (con restricciones) | CRUD |
| PublicacionInmueble | R (solo PUBLICADAS) | R | CRU (propia) | CRUD |
| SolicitudArriendo | — | CRD (propia) | RU (recibidas) | CRUD |
| ... | ... | ... | ... | ... |

Esta tabla no existe y es imprescindible.

---

### REC-07 — Decidir el mecanismo de transiciones automáticas de estado

Debe documentarse explícitamente cuáles transiciones son automáticas (scheduler) y cuáles son manuales (acción del usuario). Un scheduler requiere infraestructura adicional que no está en el stack actual.

---

### REC-08 — Definir índices de MongoDB requeridos

Para las reglas de unicidad (`numeroContrato`, `principal = true` por inmueble) y para el rendimiento de búsquedas frecuentes, se deben documentar los índices necesarios. Esto no es código — es parte del diseño del modelo de datos.

---

## 8. Prioridad de cada observación

| ID | Tipo | Descripción corta | Prioridad |
|---|---|---|---|
| INC-02 | Inconsistencia | Calificación roomie sin contrato | CRÍTICA |
| RIESGO-01 | Riesgo | Estado RESERVADA faltante | CRÍTICA |
| RIESGO-02 | Riesgo | Calificación roomie rompe modelo | CRÍTICA |
| RN-02 | Regla faltante | Un solo contrato VIGENTE por inmueble | CRÍTICA |
| INC-03 | Inconsistencia | Notificaciones no implementadas pero descritas | ALTA |
| INC-04 | Inconsistencia | Precondición visita contradictoria | ALTA |
| INC-05 | Inconsistencia | Verificación obligatoria o no | ALTA |
| RIESGO-03 | Riesgo | Sin historial de eventos | ALTA |
| RIESGO-04 | Riesgo | Roomie sin ciclo post-aprobación | ALTA |
| RIESGO-05 | Riesgo | Sin expiración automática de estados | ALTA |
| RIESGO-06 | Riesgo | Múltiples publicaciones PUBLICADAS del mismo inmueble | ALTA |
| RN-01 | Regla faltante | Contrato expira automático o manual | ALTA |
| RN-04 | Regla faltante | Plazo para calificar | ALTA |
| RN-05 | Regla faltante | Publicación vuelve a PUBLICADA automáticamente | ALTA |
| CU-F01 | Caso faltante | Renovación de contrato | ALTA |
| CU-F02 | Caso faltante | Expiración automática del contrato | ALTA |
| CU-F05 | Caso faltante | Salida del roomie antes de que termine el contrato | ALTA |
| INC-01 | Inconsistencia | Estado de publicación diferente en dos documentos | MEDIA |
| INC-07 | Inconsistencia | Restricción de eliminación de inmueble contradictoria | MEDIA |
| INC-08 | Inconsistencia | Propietario ≠ Arrendador sin validación | MEDIA |
| RIESGO-07 | Riesgo | JWT en localStorage | MEDIA |
| RIESGO-08 | Riesgo | Arrendador contrato ≠ propietario inmueble | MEDIA |
| RIESGO-09 | Riesgo | Foto principal sin unicidad en modelo | MEDIA |
| RIESGO-10 | Riesgo | numeroContrato sin índice único | MEDIA |
| RN-03 | Regla faltante | Solicitudes restantes al firmar contrato | MEDIA |
| RN-06 | Regla faltante | Matriz de permisos CRUD | MEDIA |
| RN-07 | Regla faltante | Documentos requeridos para verificación | MEDIA |
| RN-08 | Regla faltante | Verificado=false puede enviar solicitudes | MEDIA |
| RN-09 | Regla faltante | Cascada al eliminar inmueble | MEDIA |
| RN-10 | Regla faltante | Concurrencia: dos solicitudes simultáneas | MEDIA |
| CU-F03 | Caso faltante | Publicación pausada con solicitudes activas | MEDIA |
| CU-F04 | Caso faltante | Eliminar publicación con solicitudes en curso | MEDIA |
| CU-F06 | Caso faltante | Múltiples roomies — quién califica a quién | MEDIA |
| CU-F07 | Caso faltante | Incumplimiento del arrendatario | MEDIA |
| INC-06 | Inconsistencia | 6 casos de uso sin detalle (CU-22,23,24,32,41,43) | BAJA |
| RN-11 | Regla faltante | Estado inicial de habilitadoRoomie | BAJA |
| RN-12 | Regla faltante | Historial de precios | BAJA |
| RN-13 | Regla faltante | Arrendatario cancela solicitud APROBADA | BAJA |
| CU-F08 | Caso faltante | Delegación de administración de inmueble | BAJA |
| CU-F09 | Caso faltante | Candidato rechazado que vuelve a postular | BAJA |
| CU-F10 | Caso faltante | Validar disponibilidad al crear contrato | BAJA |

---

## 9. Impacto si no se corrige

### Correcciones CRÍTICAS — Sin estas, el sistema no puede funcionar correctamente

| Observación | Consecuencia de no corregir |
|---|---|
| INC-02 / RIESGO-02: Calificación roomie sin contrato | Error en base de datos al intentar guardar calificaciones roomie. Funcionalidad bloqueada. |
| RIESGO-01: Estado RESERVADA faltante | Los candidatos siguen enviando solicitudes a un inmueble ya seleccionado. El arrendador recibe y procesa candidatos innecesarios. Confusión en el portal. |
| RN-02: Un solo contrato VIGENTE por inmueble | Dos inquilinos pueden tener contratos VIGENTES del mismo inmueble simultáneamente. Error de negocio crítico. |

### Correcciones ALTAS — Sin estas, el sistema tiene deuda funcional que se acumula

| Observación | Consecuencia de no corregir |
|---|---|
| INC-03: Notificaciones no implementadas | El flujo completo (solicitud → visita → contrato) requiere que el usuario refresque manualmente para ver cambios. Experiencia de usuario degradada. |
| RIESGO-03: Sin historial | Imposible auditar cambios. En caso de disputa, no hay evidencia. Riesgo legal. |
| RIESGO-04: Roomie sin ciclo post-aprobación | No se puede saber quiénes son los roomies actuales de un inmueble. No se puede emitir calificaciones con contexto correcto. |
| RIESGO-05: Sin expiración automática | Datos incorrectos que se acumulan con el tiempo. Contratos "zombie" que no finalizaron. Publicaciones "zombie" en ARRENDADO. |
| RN-01: Contrato expira automático o manual | Si es manual y el arrendador no lo hace, el sistema es inconsistente. Si se decide automático, requiere infraestructura adicional no planificada. |
| CU-F01: Renovación de contrato | El arrendador no tiene un flujo para renovar. Debe crear un contrato nuevo de cero, perdiendo el vínculo con el anterior. |

### Correcciones MEDIAS — Deuda técnica y de UX que afecta la calidad del sistema

| Observación | Consecuencia de no corregir |
|---|---|
| RIESGO-06: Múltiples publicaciones PUBLICADAS | Anuncios duplicados del mismo inmueble en el portal. |
| RN-06: Matriz de permisos faltante | La implementación tomará decisiones de seguridad ad-hoc. Probable inconsistencia entre lo que distintos devs implementen. |
| RIESGO-09/10: Unicidad sin enforcement en DB | Datos inconsistentes que rompen reglas de negocio sin error explícito. |

---

## 10. Propuesta de corrección (sin implementar)

Las siguientes correcciones deben ser documentadas, revisadas y aprobadas ANTES de entrar a implementación.

### CORR-01 — Estado RESERVADA en PublicacionInmueble

**Alcance:** Modificar el enum `EstadoPublicacion` para agregar `RESERVADA`.  
**Regla:** PUBLICADA → RESERVADA automáticamente cuando una `SolicitudArriendo` pasa a `APROBADA`.  
**Reversión:** RESERVADA → PUBLICADA si la solicitud aprobada es cancelada sin llegar a contrato.

### CORR-02 — Ancla de Calificacion para roomie

**Alcance:** Modificar la entidad `Calificacion` para soportar dos tipos de ancla:
- Para arriendo: campo `contrato → ContratoArriendo`
- Para roomie: campo `periodoConvivencia → ConvivenciaRoomie` (nueva entidad) o `solicitudRoomie → SolicitudRoomie`

### CORR-03 — Ciclo de vida del Roomie post-aprobación

**Alcance:** Extender `SolicitudRoomie` con estados adicionales `ACTIVA` y `FINALIZADA`, o crear entidad `ConvivenciaRoomie` que registre el periodo real de convivencia del roomie.

### CORR-04 — Campos de auditoría en entidades con estado

**Alcance:** Agregar a todas las entidades con campo `estado`: `fechaUltimoCambioEstado: Instant`, `motivoCambioEstado: String`.

### CORR-05 — Documentar y decidir las 5 reglas críticas

Requiere respuesta explícita del propietario del negocio (no del desarrollador):
1. ¿Expiración automática del contrato al vencer la fecha?
2. ¿Publicación vuelve a PUBLICADA automáticamente al finalizar contrato?
3. ¿Solicitudes restantes se cancelan cuando se firma contrato?
4. ¿Plazo en días para emitir calificaciones?
5. ¿Verificación de documentos es obligatoria para operar?

### CORR-06 — Completar los 6 casos de uso faltantes

Documentar el detalle de: CU-22 (programar visita), CU-23 (firmar contrato — arrendatario), CU-24 (calificar arrendador), CU-32 (calificar al anfitrión), CU-41 (moderar publicaciones), CU-43 (moderar calificaciones).

### CORR-07 — Documentar la matriz de permisos CRUD

Crear un documento `17-matriz-permisos.md` con la tabla completa de permisos por rol y entidad.

### CORR-08 — Documentar el comportamiento de expiración de estados

Crear una sección en el roadmap que especifique:
- Qué estados expiran automáticamente (y en cuánto tiempo)
- Qué estados requieren acción manual
- Qué infraestructura adicional se necesita para las transiciones automáticas

---

## Cierre de la revisión

De las 47 observaciones identificadas:

- **3 son CRÍTICAS:** Deben resolverse antes de cualquier implementación
- **16 son ALTAS:** Deben resolverse antes de la primera versión estable
- **20 son MEDIAS:** Deben planificarse en el roadmap de forma explícita
- **8 son BAJAS:** Pueden diferirse a versiones futuras, pero deben documentarse

**Veredicto de la revisión:** La arquitectura general del sistema es sólida. El modelo de entidades tiene buena base. Sin embargo, el modelo de estados, el ciclo de vida del roomie, el anclaje de calificaciones y la ausencia de historial de eventos son deficiencias que, si se implementan tal como están documentadas, generarán deuda técnica severa y comportamiento incorrecto en producción.

**Recomendación:** Antes de iniciar la implementación, revisar con el propietario del negocio las correcciones CORR-01 a CORR-05, producir las correcciones a la documentación, y obtener aprobación formal. Luego proceder con el roadmap de implementación.
