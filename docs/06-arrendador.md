# 06 — Flujo del Arrendador

## Descripción del rol

El arrendador es el usuario que posee o administra inmuebles y los ofrece en arriendo. Puede tener múltiples inmuebles, y cada inmueble puede tener múltiples unidades arrendables de forma independiente.

---

## Flujo completo del arrendador

```mermaid
flowchart TD
    INICIO([Arrendador autenticado]) --> PERFIL[Completar PerfilUsuario\n+ cargar documentos]
    PERFIL --> VERIFICACION{¿Perfil\nverificado?}
    VERIFICACION -->|No| ESPERA[Esperar aprobación\ndel administrador]
    ESPERA --> VERIFICACION
    VERIFICACION -->|Sí| PANEL[Panel del Arrendador]

    PANEL --> OP1[Gestionar inmuebles]
    PANEL --> OP2[Gestionar publicaciones]
    PANEL --> OP3[Gestionar multimedia]
    PANEL --> OP4[Gestionar solicitudes]
    PANEL --> OP5[Gestionar visitas]
    PANEL --> OP6[Gestionar contratos]
    PANEL --> OP7[Ver mis calificaciones]

    OP1 --> INMUEBLE_CRUD[Crear / Editar / Eliminar\ninmuebles]
    OP2 --> PUB_CRUD[Crear publicaciones\nGestionar estados]
    OP3 --> MEDIA_CRUD[Subir fotos/videos\nDefinir portada]
    OP4 --> SOL_GESTION[Revisar, aprobar\no rechazar solicitudes]
    OP5 --> VIS_GESTION[Confirmar, cancelar\no finalizar visitas]
    OP6 --> CONT_GESTION[Generar, adjuntar\ny firmar contratos]
    OP7 --> CAL_VER[Ver calificaciones\nrecibidas y promedio]
```

---

## 1. Gestión de inmuebles

### Crear un inmueble

```mermaid
flowchart TD
    A([Menú → Inmuebles → Nuevo]) --> B[Formulario de inmueble]
    B --> C[Ingresa:\n- Nombre identificador\n- Dirección completa\n- Ciudad / Localidad / Barrio\n- Latitud y Longitud opcionales\n- Tipo de inmueble\n- Área m²\n- Habitaciones / Baños / Parqueaderos\n- Estrato 1-6]
    C --> D{Validar\nformulario}
    D -->|Inválido| C
    D -->|Válido| E[Guardar inmueble\npropietario = PerfilUsuario actual]
    E --> F[Redirigir al detalle\ndel inmueble]
    F --> G{¿Qué sigue?}
    G --> H[Agregar multimedia]
    G --> I[Crear publicación]
```

### Tipos de inmueble disponibles

| Tipo | Descripción |
|---|---|
| `APARTAMENTO` | Unidad en edificio o conjunto |
| `CASA` | Inmueble independiente |
| `HABITACION` | Cuarto dentro de casa o apartamento |
| `APARTAESTUDIO` | Unidad compacta integrada |
| `LOCAL` | Espacio comercial |
| `OFICINA` | Espacio de trabajo |
| `OTRO` | No clasificable en los anteriores |

> **Nota del modelo:** El sistema actual trata cada unidad (apartamento, habitación, local) como un `Inmueble` independiente. Un edificio con 10 apartamentos requiere 10 registros de `Inmueble`. Ver [13-modelo-negocio.md](13-modelo-negocio.md) para la evolución propuesta con entidad `Edificio`.

---

## 2. Gestión de publicaciones

Una publicación es el anuncio activo de un inmueble. Un inmueble puede tener múltiples publicaciones a lo largo del tiempo (rearrendamiento), pero solo debería haber una PUBLICADA simultáneamente.

### Ciclo de vida de una publicación

```mermaid
stateDiagram-v2
    [*] --> BORRADOR : Crear publicación
    BORRADOR --> PUBLICADO : Publicar
    PUBLICADO --> PAUSADO : Pausar temporalmente
    PAUSADO --> PUBLICADO : Reactivar
    PUBLICADO --> ARRENDADO : Contrato VIGENTE creado
    ARRENDADO --> PUBLICADO : Contrato finaliza
    PUBLICADO --> FINALIZADO : Cerrar definitivamente
    PAUSADO --> FINALIZADO : Cerrar definitivamente
    BORRADOR --> FINALIZADO : Cerrar sin publicar
    FINALIZADO --> [*]
```

### Campos de una publicación

| Campo | Requerido | Descripción |
|---|---|---|
| Título | Sí | Nombre atractivo para el anuncio |
| Descripción | No | Texto detallado del inmueble |
| Canon de arriendo | Sí | Valor mensual en pesos colombianos |
| Depósito | No | Meses de depósito requeridos |
| Requisitos | No | Condiciones para el arrendatario |
| Seguro requerido | No | Si requiere póliza de seguros |
| Datacredito requerido | No | Si se consultará historial crediticio |
| Fecha disponible | No | Desde cuándo está disponible |
| Estado | Sí | BORRADOR / PUBLICADO |
| Permite roomies | Sí | Si el arrendatario puede subarrendar habitaciones |
| Acepta mascotas | Sí | |
| Permite fumadores | Sí | |
| Permite niños | Sí | |
| Permite visitas | Sí | Si los inquilinos pueden recibir visitas |
| Permite parejas | Sí | |

---

## 3. Gestión de multimedia

```mermaid
flowchart TD
    A([Inmueble → Multimedia]) --> B[Ver galería actual]
    B --> C{¿Acción?}
    C -->|Agregar| D[Ingresar URL del archivo\nTipo: image/video\nTítulo opcional\nPrincipal: Sí/No]
    D --> E{¿Es principal?}
    E -->|Sí| F[Desmarcar el anterior como principal\nMarcar este como principal]
    E -->|No| G[Guardar sin cambiar la principal]
    F --> G
    G --> B
    C -->|Eliminar| H[Confirmar eliminación]
    H --> B
    C -->|Cambiar principal| I[Marcar como principal]
    I --> F
```

### Tipos de multimedia

| Tipo | tipoMedia | Descripción |
|---|---|---|
| Foto | `image/jpeg`, `image/png`, `image/webp` | Fotos del inmueble |
| Video | `video/mp4`, `video/webm` | Recorridos virtuales |
| Plano | `application/pdf`, `image/png` | Planos arquitectónicos |

> **Pendiente de validación:** ¿El sistema gestionará el almacenamiento de archivos (upload real) o solo registrará URLs de archivos ya almacenados en un servicio externo (S3, Cloudinary, etc.)?

---

## 4. Gestión de solicitudes recibidas

```mermaid
flowchart TD
    A([Publicación → Solicitudes]) --> B[Listar solicitudes\nCREADA / EN_REVISION / APROBADA / RECHAZADA]
    B --> C[Seleccionar solicitud]
    C --> D[Ver detalle:\n- Mensaje del arrendatario\n- Fecha de solicitud\n- Perfil del candidato\n- Calificaciones previas]
    D --> E{Acción del\narrendador}
    E -->|Marcar EN_REVISION| F[Estado: EN_REVISION]
    F --> G{Decisión final}
    G -->|Rechazar| H[Estado: RECHAZADA\nRazón opcional]
    G -->|Aprobar| I[Estado: APROBADA]
    I --> J[Habilitar creación\nde contrato]
    H --> B
    J --> B
```

### Notas sobre solicitudes

- El arrendador puede tener múltiples solicitudes APROBADAS simultáneamente, pero solo debe generar un contrato a la vez.
- Las solicitudes no aprobadas quedan en el historial del sistema.
- El arrendatario recibe notificación del cambio de estado.

> **Pendiente de validación:** ¿El sistema debe bloquear automáticamente las solicitudes restantes cuando ya hay un contrato VIGENTE para esa publicación?

---

## 5. Gestión de visitas

```mermaid
sequenceDiagram
    participant ART as Arrendatario
    participant SYS as Sistema
    participant ARR as Arrendador

    ART->>SYS: Solicitar visita (fecha y hora)
    SYS->>SYS: Crear visita: estado SOLICITADA
    SYS->>ARR: Notificación de nueva visita

    ARR->>SYS: Ver visita SOLICITADA
    ARR->>SYS: Confirmar fecha

    alt Fecha OK
        SYS->>SYS: Estado: CONFIRMADA
        SYS->>ART: Visita confirmada
    else Proponer otra fecha
        ARR->>SYS: Proponer nueva fecha + notas
        SYS->>ART: Nueva propuesta de fecha
        ART->>SYS: Aceptar o cancelar
    end

    Note over ART,ARR: Transcurre la visita

    ARR->>SYS: Finalizar visita con notas
    SYS->>SYS: Estado: FINALIZADA
```

---

## 6. Generación y gestión de contratos

```mermaid
flowchart TD
    A([Solicitud APROBADA]) --> B[Arrendador crea contrato]
    B --> C[Formulario del contrato:\n- Número único CONT-YYYY-NNN\n- Fecha inicio\n- Fecha fin\n- Valor mensual\n- Valor depósito\n- Arrendador y Arrendatario]
    C --> D[Estado: BORRADOR]
    D --> E[Arrendador adjunta\nURL del documento digital]
    E --> F[Estado: PENDIENTE_FIRMA]
    F --> G{Arrendatario\nfirma}
    G -->|Firma| H[Arrendador registra fecha de firma]
    H --> I[Estado: VIGENTE]
    G -->|No firma / desiste| J[Estado: CANCELADO]
    I --> K{Transcurre el contrato}
    K -->|Fecha fin alcanzada| L[Estado: FINALIZADO]
    K -->|Terminación anticipada| J
    L --> M[Habilitar calificaciones]
    J --> M
```

### Campos del contrato

| Campo | Requerido | Descripción |
|---|---|---|
| Número de contrato | Sí | Único, formato CONT-YYYY-NNN |
| URL contrato digital | No | Enlace al documento firmado |
| Fecha inicio | Sí | Inicio del periodo de arriendo |
| Fecha fin | Sí | Fin del periodo acordado |
| Valor mensual | Sí | Canon de arriendo en pesos |
| Valor depósito | No | Monto del depósito |
| Estado | Sí | Ver ciclo de vida |
| Fecha de firma | No | Cuando ambas partes firman |

---

## 7. Calificaciones como arrendador

Al finalizar un contrato, el arrendador puede calificar al arrendatario:

- **Tipo:** `ARRENDADOR_A_ARRENDATARIO`
- **Puntaje:** 1 a 5 estrellas
- **Comentario:** Texto libre (visible para todos si `visible = true`)
- **Vinculado a:** El contrato finalizado

El arrendador también puede ver cómo lo calificaron los arrendatarios anteriores, incluyendo:
- Promedio de puntaje
- Historial de calificaciones con comentarios
- Contratos completados vs cancelados

---

## 8. Consideraciones especiales

### Múltiples inmuebles

Un arrendador puede tener varios inmuebles bajo su perfil. El sistema no limita la cantidad. Cada inmueble tiene su propio ciclo de vida independiente.

### Inmueble con restricción de eliminación

Un inmueble no debería poder eliminarse si tiene:
- Contratos en estado VIGENTE
- Solicitudes en estado APROBADA o EN_REVISION
- Publicaciones en estado PUBLICADO o ARRENDADO

> **Pendiente de validación:** ¿Cuáles son exactamente las restricciones para eliminar un inmueble? ¿Se elimina en cascada o solo se desactiva?
