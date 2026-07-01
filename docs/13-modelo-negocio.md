# 13 — Modelo de Negocio

## Visión del dominio

RoomRent debe soportar múltiples escenarios de arrendamiento que van desde la habitación individual hasta el edificio completo con decenas de unidades. Cada unidad debe poder administrarse de forma completamente independiente: su propia publicación, su propio contrato, sus propios inquilinos, su propia multimedia y su propio historial.

---

## Escenarios soportados

### Escenario 1: Arrendador con múltiples inmuebles independientes

```mermaid
graph TD
    ARR[Arrendador: Carlos Ramírez]
    ARR --> I1[Apartamento Chapinero\nTipoInmueble=APARTAMENTO]
    ARR --> I2[Casa El Nogal\nTipoInmueble=CASA]
    ARR --> I3[Local Galerías\nTipoInmueble=LOCAL]

    I1 --> P1[Publicación activa\n$1.500.000/mes]
    I2 --> P2[Publicación activa\n$3.200.000/mes]
    I3 --> P3[Sin publicación activa\nEn renovación]
```

**Estado actual:** ✅ Completamente soportado. `PerfilUsuario → OneToMany → Inmueble`.

---

### Escenario 2: Edificio con múltiples unidades

```mermaid
graph TD
    ARR[Arrendador: Diana Pérez]
    ARR --> EDI[Edificio Los Álamos\nCl. 95 # 14-20\nBogotá]

    EDI --> APT101[Apartamento 101\n65m² · 2 hab · 1 baño]
    EDI --> APT102[Apartamento 102\n65m² · 2 hab · 1 baño]
    EDI --> APT201[Apartamento 201\n80m² · 3 hab · 2 baños]
    EDI --> APT202[Apartamento 202\n80m² · 3 hab · 2 baños]

    APT101 --> P101[Publicación PUBLICADO\n$1.100.000/mes]
    APT102 --> P102[Sin publicación\nArrendado directamente]
    APT201 --> P201[Publicación ARRENDADO\nContrato vigente]
    APT202 --> P202[Publicación BORRADOR]
```

**Estado actual:** ⚠️ **Parcialmente soportado.** Cada apartamento es un `Inmueble` independiente. No existe una entidad `Edificio` que los agrupe. El arrendador debe registrar 4 `Inmueble`s separados, sin relación entre ellos.

**Problema:** No es posible ver "todos los apartamentos de este edificio" como grupo, ni gestionar características comunes del edificio (portería, ascensor, zonas comunes, parqueaderos del edificio).

---

### Escenario 3: Casa con habitaciones independientes

```mermaid
graph TD
    ARR[Arrendador: Pedro Torres]
    ARR --> CASA[Casa Patio Bonito\nTipoInmueble=CASA]

    CASA --> HAB1[Habitación 1\nTipoInmueble=HABITACION\n12m²]
    CASA --> HAB2[Habitación 2\nTipoInmueble=HABITACION\n10m²]
    CASA --> HAB3[Habitación 3\nTipoInmueble=HABITACION\n14m²]
    CASA --> COMUN[Zonas comunes\n⚠️ No modeladas aún]

    HAB1 --> P1[Publicación $600.000/mes]
    HAB2 --> P2[Arrendada - Contrato vigente]
    HAB3 --> P3[Publicación Roomie $500.000/mes]
```

**Estado actual:** ⚠️ **Parcialmente soportado.** Se pueden registrar 3 `Inmueble`s de tipo HABITACION independientes. No existe una entidad que represente la "casa" como contenedor de esas habitaciones. El arrendador maneja 3 inmuebles sin relación explícita entre ellos.

---

### Escenario 4: Apartamento con arrendatario principal y roomies

```mermaid
graph TD
    ARR[Arrendador: Carlos]
    ART[Arrendatario: María\nContrato VIGENTE]
    ROO1[Roomie: Juan]
    ROO2[Roomie: Laura]

    ARR -->|Propietario| APTO[Apartamento Kennedy\ncontrato vigente con María]
    APTO -->|permiteRoomies=true| PUB_ROO1[PublicacionRoomie\nHabitación Norte]
    APTO -->|permiteRoomies=true| PUB_ROO2[PublicacionRoomie\nHabitación Sur]

    ART -->|Publica| PUB_ROO1
    ART -->|Publica| PUB_ROO2

    PUB_ROO1 -->|Aprobado| ROO1
    PUB_ROO2 -->|Aprobado| ROO2
```

**Estado actual:** ✅ **Completamente soportado.** `PublicacionRoomie` vincula el inmueble con la publicación del arrendatario. Cada roomie tiene su `SolicitudRoomie` independiente.

---

### Escenario 5: Múltiples contratos en el tiempo (rearrendamiento)

```mermaid
gantt
    title Inmueble: Apto Chapinero - Historial de contratos
    dateFormat YYYY-MM
    section Arrendatarios
    Carlos Rueda (FINALIZADO) :done, 2023-01, 2023-12
    María González (FINALIZADO) :done, 2024-02, 2024-11
    Andrés Castro (VIGENTE) :active, 2025-01, 2026-01
```

**Estado actual:** ✅ **Completamente soportado.** `Inmueble → OneToMany → ContratoArriendo`. El historial completo se conserva.

---

## Análisis del modelo actual vs. el modelo ideal

### Modelo actual (implementado)

```mermaid
erDiagram
    PerfilUsuario ||--o{ Inmueble : "propietario"
    Inmueble ||--o{ PublicacionInmueble : "publicaciones"
    Inmueble ||--o{ MultimediaInmueble : "multimedia"
    Inmueble ||--o{ ContratoArriendo : "contratos"
    PublicacionInmueble ||--o{ SolicitudArriendo : "solicitudes"
    SolicitudArriendo ||--o{ VisitaProgramada : "visitas"
    ContratoArriendo }o--|| PerfilUsuario : "arrendador"
    ContratoArriendo }o--|| PerfilUsuario : "arrendatario"
    PublicacionRoomie ||--o{ SolicitudRoomie : "solicitudes"
    PublicacionRoomie }o--|| PerfilUsuario : "arrendatario"
    PublicacionRoomie }o--|| Inmueble : "inmueble"
    Calificacion }o--|| PerfilUsuario : "autor"
    Calificacion }o--|| PerfilUsuario : "calificado"
    Calificacion }o--|| ContratoArriendo : "contrato"
    PerfilUsuario ||--o{ DocumentoUsuario : "documentos"
    PerfilUsuario ||--|| User : "usuario"
```

### Modelo extendido propuesto (no implementado)

Las siguientes entidades se proponen para versiones futuras:

```mermaid
erDiagram
    Edificio {
        string id
        string nombre
        string direccion
        string ciudad
        string barrio
        int numPisos
        int numUnidades
        string amenidades
    }
    Edificio ||--o{ Inmueble : "contiene"

    PagoArriendo {
        string id
        LocalDate fechaPago
        long monto
        string comprobante
        string estado
        string observaciones
    }
    ContratoArriendo ||--o{ PagoArriendo : "pagos"

    OcupanteContrato {
        string id
        LocalDate fechaIngreso
        LocalDate fechaSalida
        string rol
    }
    ContratoArriendo ||--o{ OcupanteContrato : "ocupantes"
    PerfilUsuario ||--o{ OcupanteContrato : "persona"

    Notificacion {
        string id
        string tipo
        string mensaje
        boolean leida
        Instant fechaCreacion
    }
    PerfilUsuario ||--o{ Notificacion : "notificaciones"
```

---

## Gaps identificados en el modelo actual

| Gap | Descripción | Impacto | Propuesta |
|---|---|---|---|
| **Sin entidad Edificio** | No se pueden agrupar unidades del mismo edificio | Medio | Agregar `Edificio → OneToMany → Inmueble` |
| **Sin tracking de pagos** | No hay registro de pagos mensuales | Alto | Agregar `PagoArriendo → ManyToOne → ContratoArriendo` |
| **Un solo arrendatario por contrato** | No soporta múltiples titulares en el mismo contrato | Medio | Agregar `OcupanteContrato` como tabla intermedia |
| **Sin notificaciones** | No hay sistema in-app de alertas | Alto | Agregar `Notificacion → ManyToOne → PerfilUsuario` |
| **Sin foto de perfil** | `PerfilUsuario` no tiene campo de imagen | Bajo | Agregar `urlFotoPerfil: String` a `PerfilUsuario` |
| **Sin subarriendo formal** | El roomie no tiene un "contrato" propio | Medio | Definir si SolicitudRoomie aprobada debe generar un acuerdo formal |
| **Sin zonas comunes** | No se modelan amenidades o espacios compartidos del edificio | Bajo | Parte de la futura entidad `Edificio` |
| **Sin penalizaciones** | No hay cálculo de multas por terminación anticipada | Bajo | Campo o entidad `PenalizacionContrato` |

---

## Principios del modelo de negocio

1. **Cada unidad es independiente.** Una habitación, un apartamento, un local — cada uno tiene su propio ciclo de vida completo (publicación, contrato, multimedia, historial).

2. **Un inmueble puede tener múltiples contratos en el tiempo**, pero solo uno vigente simultáneamente.

3. **El arrendador es siempre el propietario** (o su representante autorizado) del inmueble.

4. **El roomie no tiene relación directa con el propietario** — su relación es con el arrendatario principal.

5. **La reputación es permanente.** Las calificaciones no se eliminan y reflejan el comportamiento histórico real.

6. **El contrato es la fuente de verdad.** Solicitudes, visitas y calificaciones giran alrededor del contrato.
