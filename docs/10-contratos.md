# 10 — Contratos Digitales

## Visión

El contrato digital es el eje central del modelo de negocio. La visión de RoomRent es **eliminar completamente el papel** del proceso de arrendamiento. Esto implica:

1. Generar contratos desde la plataforma
2. Adjuntar el documento en formato digital
3. Registrar la firma de ambas partes
4. Mantener el historial completo y accesible en todo momento
5. Vincular el contrato con el inmueble, el arrendador y el arrendatario

---

## Ciclo de vida del contrato

```mermaid
stateDiagram-v2
    [*] --> BORRADOR : Arrendador crea el contrato
    BORRADOR --> PENDIENTE_FIRMA : Arrendador adjunta el documento
    PENDIENTE_FIRMA --> VIGENTE : Firma registrada por ambas partes
    PENDIENTE_FIRMA --> CANCELADO : Proceso no se concreta
    BORRADOR --> CANCELADO : Decisión de no continuar
    VIGENTE --> FINALIZADO : Fecha de fin alcanzada
    VIGENTE --> CANCELADO : Terminación anticipada acordada
    FINALIZADO --> [*] : Habilita calificaciones
    CANCELADO --> [*] : Habilita calificaciones
```

---

## Flujo completo de un contrato

```mermaid
flowchart TD
    INICIO([SolicitudArriendo APROBADA]) --> PASO1[PASO 1: Arrendador genera contrato]
    PASO1 --> FORM[Formulario del contrato:\n- Número único\n- Inmueble\n- Arrendador y arrendatario\n- Fechas inicio y fin\n- Valor mensual\n- Valor depósito]
    FORM --> BORRADOR[Estado: BORRADOR\nEl contrato existe pero sin documento]

    BORRADOR --> PASO2[PASO 2: Preparar el documento]
    PASO2 --> PREP{¿Cómo se genera\nel documento?}

    PREP -->|Externo| DOC_EXT[Documento preparado fuera del sistema\nWord/PDF por el arrendador o notaría]
    PREP -->|Interno| DOC_INT[⚠️ Pendiente de validación:\n¿El sistema generará el PDF automáticamente\ndesde los datos del contrato?]

    DOC_EXT --> URL[Arrendador adjunta URL del documento]
    DOC_INT --> URL
    URL --> PEND_FIRMA[Estado: PENDIENTE_FIRMA]

    PEND_FIRMA --> PASO3[PASO 3: Revisión del arrendatario]
    PASO3 --> REVISION{¿El arrendatario\naceptó los términos?}
    REVISION -->|No acepta| NEGOCIACION[Negociación fuera del sistema\n⚠️ Pendiente: ¿el sistema debe\nsoportar rondas de negociación?]
    NEGOCIACION --> REVISION
    REVISION -->|Acepta| FIRMA_ART[Arrendatario firma\nel documento]

    FIRMA_ART --> PASO4[PASO 4: Registro de firma]
    PASO4 --> FIRMA_ARR[Arrendador registra la fecha de firma]
    FIRMA_ARR --> VIGENTE[Estado: VIGENTE\nContrato activo]

    VIGENTE --> TRANSCURSO[Transcurre el periodo de arriendo]
    TRANSCURSO --> FIN_NORMAL{¿Cómo termina?}
    FIN_NORMAL -->|Fecha de fin llegó| FINALIZADO[Estado: FINALIZADO]
    FIN_NORMAL -->|Terminación anticipada| CANCELADO[Estado: CANCELADO]

    FINALIZADO --> CAL[Habilitar calificaciones]
    CANCELADO --> CAL
```

---

## Datos del contrato

| Campo | Tipo | Requerido | Descripción |
|---|---|---|---|
| `numeroContrato` | String (único) | Sí | Identificador del contrato. Formato sugerido: `CONT-YYYY-NNN` |
| `urlContratoDigital` | String | No | URL al documento firmado |
| `fechaInicio` | LocalDate | Sí | Inicio del periodo de arriendo |
| `fechaFin` | LocalDate | Sí | Fin del periodo acordado |
| `valorMensual` | Long | Sí | Canon mensual en pesos colombianos |
| `valorDeposito` | Long | No | Monto del depósito (usualmente 1-3 meses) |
| `estado` | EstadoContrato | Sí | Ver ciclo de vida |
| `fechaFirma` | Instant | No | Fecha y hora de firma del documento |
| `arrendador` | → PerfilUsuario | Sí | Quién arrienda el inmueble |
| `arrendatario` | → PerfilUsuario | Sí | Quién toma en arriendo |
| `inmueble` | → Inmueble | Sí | El inmueble arrendado |

---

## Diseño del contrato digital

### Visión actual (implementada)

El sistema registra la URL de un documento externo. El documento en sí se genera y firma fuera del sistema (Word, PDF enviado por correo, etc.), y la URL queda como referencia.

### Visión futura (propuesta, sin implementar)

```mermaid
graph TD
    subgraph "Generación del documento"
        T[Template de contrato\nen el sistema] --> DATOS[Datos del contrato\nArrendador, Arrendatario, Inmueble]
        DATOS --> PDF[PDF generado automáticamente]
    end

    subgraph "Firma digital"
        PDF --> FIRMA_DIGITAL{Tipo de firma}
        FIRMA_DIGITAL -->|Básica| CONFIRMACION[Confirmación digital\n en la plataforma\ncon registro de fecha/IP]
        FIRMA_DIGITAL -->|Avanzada| PROVIDER[Integración con proveedor\nde firma electrónica\nCertificado digital]
    end

    subgraph "Almacenamiento"
        CONFIRMACION --> STORAGE[Almacenamiento seguro\nS3 o equivalente]
        PROVIDER --> STORAGE
        STORAGE --> URL_FINAL[URL permanente en ContratoArriendo]
    end
```

> **Pendiente de validación:** La firma electrónica con validez legal en Colombia requiere un certificado digital emitido por una entidad certificadora autorizada. ¿La plataforma debe integrar un proveedor de firma electrónica? Si no, ¿cuál es el valor legal del contrato digital generado?

---

## Historial de contratos por inmueble

Un inmueble puede tener múltiples contratos a lo largo del tiempo (rearrendamientos sucesivos):

```mermaid
gantt
    title Ejemplo: Historial de contratos del Inmueble "Apto Chapinero"
    dateFormat YYYY-MM-DD
    section Contratos
    CONT-2024-001 FINALIZADO :done, 2024-01-01, 2024-12-31
    CONT-2025-002 VIGENTE :active, 2025-02-01, 2026-01-31
    CONT-2026-003 BORRADOR :crit, 2026-03-01, 2027-02-28
```

El sistema permite ver el historial completo de contratos de un inmueble:
- Contratos pasados (FINALIZADO, CANCELADO)
- Contrato actual (VIGENTE)
- Contratos futuros (BORRADOR, PENDIENTE_FIRMA)

---

## Terminación anticipada del contrato

```mermaid
flowchart TD
    A([Contrato VIGENTE]) --> B{¿Quién inicia\nla terminación?}
    B -->|Arrendador| C[Arrendador solicita terminación\ncon razón documentada]
    B -->|Arrendatario| D[Arrendatario solicita terminación\ncon razón documentada]
    B -->|Acuerdo mutuo| E[Ambas partes acuerdan\nla terminación]
    C --> F{¿Aplican\npenalizaciones?}
    D --> F
    E --> G[Contrato → CANCELADO]
    F -->|Sí| H[⚠️ Pendiente de validación:\n¿El sistema calcula penalizaciones\npor terminación anticipada?]
    F -->|No acordadas| G
    G --> I[Habilitar calificaciones mutuas]
```

> **Pendiente de validación:** ¿El sistema debe soportar el cálculo de penalizaciones por terminación anticipada según las leyes colombianas de arriendo (Ley 820 de 2003)?

---

## Reglas de negocio del contrato

| Regla | Descripción |
|---|---|
| Número único | `numeroContrato` debe ser único en toda la base de datos |
| Un contrato vigente por inmueble | El inmueble no debería tener dos contratos VIGENTES simultáneamente |
| Arrendador ≠ Arrendatario | No se puede contratar consigo mismo |
| Fecha fin > Fecha inicio | Validación básica de fechas |
| VIGENTE antes de calificar | Solo contratos FINALIZADO o CANCELADO habilitan calificaciones |

---

## Perspectiva de cada actor sobre el contrato

```mermaid
graph LR
    subgraph "Arrendador"
        A1[Ver mis contratos\nvigentes y finalizados]
        A2[Crear nuevo contrato]
        A3[Adjuntar documento]
        A4[Registrar fecha de firma]
        A5[Finalizar / Cancelar contrato]
    end

    subgraph "Arrendatario"
        T1[Ver contratos\ncon mis datos]
        T2[Confirmar firma]
        T3[Ver historial completo]
    end

    subgraph "Administrador"
        D1[Ver todos los contratos]
        D2[Auditoría del sistema]
        D3[Intervenir en conflictos]
    end
```
