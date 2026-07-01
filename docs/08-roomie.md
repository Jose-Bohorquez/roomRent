# 08 — Flujo del Roomie

## Descripción del rol

Un roomie es una persona que busca co-habitar en un espacio ya arrendado. No arrienda directamente al propietario sino que comparte con el arrendatario principal. El flujo del roomie es completamente independiente del flujo de arriendo estándar y opera sobre entidades propias (`PublicacionRoomie`, `SolicitudRoomie`).

---

## Diagrama de actores del flujo roomie

```mermaid
graph TD
    subgraph "Flujo estándar"
        PROP[Propietario\nArrendador]
        INM[Inmueble]
        PUB_ARR[PublicacionInmueble]
        SOL_ARR[SolicitudArriendo]
        CONT[ContratoArriendo]
        ART[Arrendatario principal]
    end

    subgraph "Flujo Roomie"
        PUB_ROO[PublicacionRoomie]
        SOL_ROO[SolicitudRoomie]
        ROO[Candidato Roomie]
        CAL_ROO[CalificacionRoomie]
    end

    PROP --> INM
    INM --> PUB_ARR
    PUB_ARR --> SOL_ARR
    SOL_ARR --> CONT
    CONT --> ART
    ART -->|Si permiteRoomies y tiene contrato vigente| PUB_ROO
    PUB_ROO --> SOL_ROO
    SOL_ROO --> ROO
    ROO --> CAL_ROO
    ART --> CAL_ROO
```

---

## Flujo completo del Roomie

```mermaid
flowchart TD
    INICIO([Candidato Roomie]) --> REGISTRO[Registrarse en la plataforma]
    REGISTRO --> PERFIL[Completar PerfilUsuario\nhabilitadoRoomie = true]
    PERFIL --> BUSCAR[Buscar habitaciones disponibles]
    BUSCAR --> FILTRAR[Filtrar por:\nciudad, precio, género, fecha]
    FILTRAR --> DETALLE[Ver detalle de habitación]
    DETALLE --> DECISION{¿Le interesa?}
    DECISION -->|No| BUSCAR
    DECISION -->|Sí| POSTULAR[Postularse a la habitación]
    POSTULAR --> ESPERA{Esperar respuesta\ndel anfitrión}
    ESPERA -->|Rechazada| BUSCAR
    ESPERA -->|Aprobada| ACUERDO[Establecer acuerdo de convivencia]
    ACUERDO --> CONVIVENCIA[Período de convivencia activa]
    CONVIVENCIA --> FIN[Fin de la convivencia]
    FIN --> CALIFICAR[Calificar al arrendatario anfitrión]
    CALIFICAR --> END([Ciclo completado])
```

---

## 1. Habilitación como Roomie

Para que un usuario pueda postularse como roomie, su perfil debe tener `habilitadoRoomie = true`. Este campo puede ser:

- **Activado por el propio usuario** al completar su perfil (pendiente de validación)
- **Activado por el administrador** tras verificar sus documentos

```mermaid
flowchart LR
    A([Usuario registrado]) --> B{habilitadoRoomie}
    B -->|false| C[No puede postularse\na habitaciones roomie]
    B -->|true| D[Puede buscar y postularse\na habitaciones roomie]
```

> **Pendiente de validación:** ¿Quién controla el campo `habilitadoRoomie`? ¿El propio usuario lo activa en su perfil, o requiere aprobación del admin? ¿Hay requisitos adicionales para activarlo?

---

## 2. Buscar y explorar habitaciones

El roomie accede al catálogo de publicaciones roomie. Cada publicación muestra:

```mermaid
graph TD
    CARD[Tarjeta de habitación roomie]
    CARD --> T[Título]
    CARD --> HAB[Nombre de la habitación]
    CARD --> VAL[Valor mensual]
    CARD --> SERV[Servicios incluidos\nÁrea estimada si aplica]
    CARD --> ESP[Espacios compartidos\ncocina, sala, baño]
    CARD --> GEN[Género preferido]
    CARD --> DISP[Fecha disponible]
    CARD --> EST[Estado de la publicación]
    CARD --> ANFITRION[Perfil del anfitrión:\nnombre, calificación, verified]
    CARD --> INMUEBLE_INFO[Inmueble:\nciudad, barrio, tipo]
```

### Filtros de búsqueda roomie

| Filtro | Descripción |
|---|---|
| Ciudad | Ubicación del inmueble |
| Precio máximo | Valor mensual máximo |
| Género preferido | MASCULINO, FEMENINO, OTRO, PREFIERO_NO_DECIR |
| Fecha disponible | Disponible antes de la fecha seleccionada |
| Acepta mascotas | Si el anfitrión lo permite |
| Permite fumadores | Si el anfitrión lo permite |

---

## 3. Proceso de postulación

```mermaid
sequenceDiagram
    participant ROO as Candidato Roomie
    participant SYS as Sistema
    participant ART as Arrendatario Anfitrión

    ROO->>SYS: Ver detalle de habitación
    ROO->>SYS: Clic en Postularme
    SYS->>SYS: Verificar: habilitadoRoomie = true
    alt No habilitado
        SYS->>ROO: Debes habilitar tu perfil roomie primero
    else Habilitado
        SYS->>ROO: Formulario de postulación
        ROO->>SYS: Mensaje de presentación + referencias
        SYS->>SYS: Crear SolicitudRoomie\nestado=CREADA
        SYS->>ART: Notificar nueva postulación
        SYS->>ROO: Postulación enviada
    end
```

### Estados de la postulación roomie

```mermaid
stateDiagram-v2
    [*] --> CREADA : Roomie envía postulación
    CREADA --> EN_REVISION : Anfitrión la revisa
    CREADA --> CANCELADA : Roomie cancela
    EN_REVISION --> APROBADA : Anfitrión aprueba
    EN_REVISION --> RECHAZADA : Anfitrión rechaza
    APROBADA --> [*] : Inicio de convivencia
    RECHAZADA --> [*]
    CANCELADA --> [*]
```

---

## 4. Campos de la solicitud roomie

| Campo | Descripción |
|---|---|
| Mensaje | Presentación personal del candidato |
| Referencias | Contactos o vínculos que respaldan al candidato |
| Estado | CREADA, EN_REVISION, APROBADA, RECHAZADA, CANCELADA |
| Fecha de creación | Registro automático |
| Postulante | Referencia al PerfilUsuario del candidato |
| PublicacionRoomie | Habitación a la que se postula |

---

## 5. Perfil del candidato roomie

El anfitrión puede revisar el perfil del candidato antes de aprobar. Los datos más relevantes son:

| Campo | Relevancia |
|---|---|
| Nombre completo | Identificación |
| Biografía | Descripción personal |
| Intereses | Compatibilidad de estilo de vida |
| Tiene mascotas | Compatibilidad |
| Fumador | Compatibilidad |
| Habilitado roomie | Indica que el perfil está activo como roomie |
| Calificaciones previas como roomie | Historial de convivencias anteriores |
| Verificado | Documentos aprobados por el admin |

---

## 6. Calificación al finalizar la convivencia

Al terminar la convivencia, ambas partes pueden calificarse:

| Acción | Actor | Tipo de calificación |
|---|---|---|
| Calificar al roomie | Arrendatario anfitrión | `ARRENDATARIO_A_ROOMIE` |
| Calificar al anfitrión | Candidato roomie | `ROOMIE_A_ARRENDATARIO` |

Ambas calificaciones incluyen:
- Puntaje: 1 a 5 estrellas
- Comentario: texto libre
- Vinculado al contrato correspondiente

> **Pendiente de validación:** Las calificaciones roomie actualmente están vinculadas a un `ContratoArriendo`. ¿Debería existir algún tipo de "acuerdo de convivencia" formal para los roomies (independiente del contrato principal del arrendatario)?

---

## 7. Restricciones del flujo roomie

- El roomie no puede postularse a la misma habitación dos veces simultáneamente.
- El roomie solo puede postularse a publicaciones en estado `PUBLICADO`.
- El roomie no puede ver datos de contacto directos del anfitrión hasta que la postulación esté APROBADA.
- La habitación roomie debe estar vinculada a un inmueble con contrato VIGENTE del anfitrión.

---

## 8. Diferencia clave: Roomie vs Arrendatario

```mermaid
graph LR
    subgraph "Arrendatario"
        A1[Arrienda al PROPIETARIO]
        A2[Firma contrato formal]
        A3[Paga directamente al propietario]
        A4[Tiene derechos legales sobre el inmueble]
    end

    subgraph "Roomie"
        R1[Arrienda al ARRENDATARIO]
        R2[Acuerdo informal o subarriendo]
        R3[Paga al arrendatario]
        R4[Derechos limitados al acuerdo privado]
    end
```

> **Nota legal (pendiente de validación):** El subarriendo en Colombia requiere autorización explícita del propietario. El campo `permiteRoomies` en `PublicacionInmueble` intenta capturar esta autorización, pero deberían considerarse las implicaciones legales del modelo de roomie.
