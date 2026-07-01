# 07 — Flujo del Arrendatario

## Descripción del rol

El arrendatario es el usuario que busca arrendar un inmueble. Puede buscar, filtrar, solicitar, visitar, firmar contratos y calificar arrendadores. También puede, una vez tenga un contrato vigente, publicar habitaciones para roomies si el inmueble lo permite.

---

## Flujo completo del arrendatario

```mermaid
flowchart TD
    INICIO([Arrendatario autenticado]) --> PERFIL[Completar perfil\n+ documentos de verificación]
    PERFIL --> BUSCAR[Buscar inmuebles\nen el portal]
    BUSCAR --> FILTRAR[Aplicar filtros:\nciudad, tipo, precio, características]
    FILTRAR --> DETALLE[Ver detalle de publicación]
    DETALLE --> DECISION{¿Le interesa?}
    DECISION -->|No| BUSCAR
    DECISION -->|Sí| SOLICITUD[Enviar solicitud de arriendo]
    SOLICITUD --> ESPERA{Esperar respuesta\ndel arrendador}
    ESPERA -->|Rechazada| BUSCAR
    ESPERA -->|Aprobada| VISITA[Solicitar visita presencial]
    VISITA --> VISITA_REALIZADA[Asistir a la visita]
    VISITA_REALIZADA --> DECISION_CONTRATO{¿Continúa\nel proceso?}
    DECISION_CONTRATO -->|No| BUSCAR
    DECISION_CONTRATO -->|Sí| FIRMA[Firmar contrato digital]
    FIRMA --> VIGENTE[Contrato VIGENTE\nInmueble habitado]
    VIGENTE --> OPCION_ROOMIE{¿Publicar\nhabitación roomie?}
    OPCION_ROOMIE -->|Sí y el inmueble lo permite| ROOMIE_PUB[Publicar habitación roomie]
    OPCION_ROOMIE -->|No| ESPERA_FIN[Esperar fin de contrato]
    ROOMIE_PUB --> ESPERA_FIN
    ESPERA_FIN --> CALIFICAR[Calificar al arrendador]
    CALIFICAR --> FIN([Ciclo completado])
```

---

## 1. Búsqueda de inmuebles

### Portal público vs. portal autenticado

```mermaid
flowchart LR
    A[Usuario] --> B{¿Autenticado?}
    B -->|No| PORTAL[Portal React\n/portal/\nVer publicaciones\nSin interactuar]
    B -->|Sí| ADMIN[Panel Angular\nBuscar + solicitar]
```

> **Pendiente de validación:** ¿El portal React tiene una búsqueda funcional con filtros en tiempo real, o solo muestra el listado? ¿Está conectado al backend?

### Filtros de búsqueda disponibles

| Filtro | Tipo | Descripción |
|---|---|---|
| Ciudad | Texto/Select | Filtra por ciudad |
| Barrio | Texto | Filtra por barrio |
| Tipo de inmueble | Select | APARTAMENTO, CASA, HABITACION, etc. |
| Canon mínimo | Número | Precio mínimo mensual |
| Canon máximo | Número | Precio máximo mensual |
| Acepta mascotas | Boolean | Solo los que aceptan |
| Permite fumadores | Boolean | Solo los que permiten |
| Estrato | Rango | Del 1 al 6 |
| Fecha disponible | Fecha | Disponible antes de esta fecha |
| Permite roomies | Boolean | Solo los que lo permiten |

> **Pendiente de validación:** ¿Cuáles de estos filtros están implementados en el portal React actual? ¿La búsqueda es client-side o server-side?

---

## 2. Ver detalle de publicación

Al seleccionar una publicación, el arrendatario puede ver:

```mermaid
graph TD
    DETALLE[Detalle de Publicación]
    DETALLE --> INFO[Información básica:\n- Título\n- Descripción\n- Canon de arriendo\n- Depósito\n- Fecha disponible]
    DETALLE --> CONDICIONES[Condiciones:\n- Mascotas, fumadores\n- Roomies, niños\n- Datacredito, seguro]
    DETALLE --> INMUEBLE[Datos del inmueble:\n- Dirección\n- Ciudad / Barrio\n- Tipo / Área / Habitaciones\n- Estrato\n- Mapa si tiene lat/lon]
    DETALLE --> GALERIA[Galería multimedia:\n- Fotos\n- Videos]
    DETALLE --> ARRENDADOR[Perfil del arrendador:\n- Nombre\n- Calificación promedio\n- Contratos completados]
    DETALLE --> ACCION[Botón Solicitar]
```

---

## 3. Enviar solicitud de arriendo

```mermaid
sequenceDiagram
    participant ART as Arrendatario
    participant SYS as Sistema
    participant ARR as Arrendador

    ART->>SYS: Clic en Solicitar arriendo
    SYS->>SYS: Verificar: arrendatario no tiene solicitud activa para esta pub.
    alt Ya tiene solicitud activa
        SYS->>ART: Error: Ya tienes una solicitud activa para este inmueble
    else No tiene
        SYS->>ART: Mostrar formulario de solicitud
        ART->>SYS: Escribir mensaje + aceptar términos
        SYS->>SYS: Crear SolicitudArriendo\nestado=CREADA\nfechaCreacion=now
        SYS->>ARR: Notificar nueva solicitud
        SYS->>ART: Solicitud enviada correctamente
    end
```

### Estados de una solicitud desde la perspectiva del arrendatario

```mermaid
stateDiagram-v2
    [*] --> CREADA : Arrendatario envía
    CREADA --> EN_REVISION : Arrendador la revisa
    CREADA --> CANCELADA : Arrendatario cancela
    EN_REVISION --> APROBADA : Arrendador aprueba
    EN_REVISION --> RECHAZADA : Arrendador rechaza
    APROBADA --> [*] : Proceso continúa con visita/contrato
    RECHAZADA --> [*] : Arrendatario puede buscar otro inmueble
    CANCELADA --> [*]
```

---

## 4. Solicitar y asistir a visitas

```mermaid
flowchart TD
    A([Solicitud APROBADA]) --> B[Arrendatario solicita visita]
    B --> C[Seleccionar fecha y hora preferida]
    C --> D[Crear VisitaProgramada\nestado=SOLICITADA]
    D --> E{Arrendador responde}
    E -->|Confirma| F[Estado: CONFIRMADA\nFecha acordada]
    E -->|Cancela| G[Estado: CANCELADA]
    E -->|No responde| H[⚠️ Pendiente de validación:\n¿Hay timeout? ¿Se puede reprogramar?]
    F --> I[Arrendatario asiste a la visita]
    I --> J[Arrendador finaliza la visita\ncon notas]
    J --> K[Estado: FINALIZADA]
    K --> L[Arrendatario puede continuar\nhacia el contrato]
```

> **Pendiente de validación:** ¿El arrendatario puede solicitar una visita sin que la solicitud esté en estado APROBADA? ¿O es requisito?

---

## 5. Firmar contrato

```mermaid
sequenceDiagram
    participant ARR as Arrendador
    participant SYS as Sistema
    participant ART as Arrendatario

    ARR->>SYS: Genera contrato (BORRADOR)
    ARR->>SYS: Adjunta documento digital (URL)
    SYS->>SYS: Estado: PENDIENTE_FIRMA
    SYS->>ART: Notificación: contrato listo para revisar

    ART->>SYS: Acceder al contrato
    ART->>SYS: Revisar términos y documento
    ART->>SYS: Confirmar firma

    ARR->>SYS: Registrar fecha de firma
    SYS->>SYS: Estado: VIGENTE
    SYS->>ART: Contrato vigente confirmado
    SYS->>ARR: Contrato vigente confirmado
```

### Acceso al historial de contratos

El arrendatario puede ver todos sus contratos:
- Vigentes (en curso)
- Finalizados (historial)
- Cancelados (con razón)

---

## 6. Publicar habitación para roomie

Cuando el contrato está VIGENTE y la publicación del inmueble tiene `permiteRoomies = true`, el arrendatario puede publicar una habitación:

```mermaid
flowchart TD
    A([Arrendatario con contrato VIGENTE]) --> B{¿La publicación\npermite roomies?}
    B -->|No| C[No puede publicar habitación]
    B -->|Sí| D[Crear PublicacionRoomie]
    D --> E[Ingresa:\n- Título\n- Nombre de la habitación\n- Valor mensual\n- Servicios incluidos\n- Espacios compartidos\n- Género preferido\n- Fecha disponible]
    E --> F[Estado: PUBLICADO o BORRADOR]
    F --> G[Candidatos pueden ver\ny postularse]
    G --> H[Arrendatario gestiona\npostulaciones]
    H --> I[Aprobar / Rechazar candidato]
```

---

## 7. Calificaciones

Al finalizar el contrato, el arrendatario puede:

| Acción | Tipo | Descripción |
|---|---|---|
| Calificar al arrendador | `ARRENDATARIO_A_ARRENDADOR` | Puntaje 1-5 + comentario |
| Calificar a roomies | `ARRENDATARIO_A_ROOMIE` | Si tuvo roomies durante el contrato |

El arrendatario también puede ver su propia reputación:
- Promedio de calificaciones recibidas
- Historial de comentarios
- Contratos completados vs cancelados

---

## 8. Perfil del arrendatario

El `PerfilUsuario` del arrendatario contiene información relevante para los arrendadores:

| Campo | Relevancia para el arrendador |
|---|---|
| Nombre completo | Identificación |
| Tipo y número de documento | Verificación de identidad |
| Ciudad y barrio actual | Proximidad |
| Profesión / Ocupación | Estabilidad económica |
| Empresa / Universidad | Referencia laboral o académica |
| Tiene mascotas | Compatibilidad con el inmueble |
| Es fumador | Compatibilidad con el inmueble |
| Verified | Si el admin aprobó los documentos |
| Estado | ACTIVO, SUSPENDIDO, etc. |
| Calificaciones previas | Historial como inquilino |
| Habilitado como roomie | Si puede publicar habitaciones |

---

## 9. Notas de compatibilidad de convivencia

> **Pendiente de validación:** ¿El sistema debe implementar un sistema de compatibilidad automática entre los criterios del arrendatario y las condiciones del inmueble? Por ejemplo: si el arrendatario tiene mascotas, filtrar automáticamente solo los que `aceptaMascotas = true`.
