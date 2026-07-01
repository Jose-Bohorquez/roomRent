# 11 — Sistema de Reputación y Calificaciones

## Visión

El sistema de reputación va más allá de las estrellas. RoomRent construye un perfil de confianza para cada usuario basado en su historial real de contratos: cuántos completó, cuántos canceló, qué dijeron las otras partes, y cuánto tiempo lleva activo en la plataforma.

La reputación es un activo del usuario que se construye a lo largo del tiempo y no puede reiniciarse.

---

## Actores y tipos de calificación

```mermaid
graph TD
    subgraph "Finalización de contrato de arriendo"
        ARR[Arrendador] -->|ARRENDADOR_A_ARRENDATARIO| ART[Arrendatario]
        ART -->|ARRENDATARIO_A_ARRENDADOR| ARR
    end

    subgraph "Finalización de convivencia roomie"
        ART2[Arrendatario Anfitrión] -->|ARRENDATARIO_A_ROOMIE| ROO[Roomie]
        ROO -->|ROOMIE_A_ARRENDATARIO| ART2
    end
```

---

## Estructura de una calificación

| Campo | Tipo | Descripción |
|---|---|---|
| `tipoCalificacion` | TipoCalificacion | Quién califica a quién |
| `puntaje` | Integer (1-5) | Puntuación numérica |
| `comentario` | TextBlob | Texto libre con la experiencia |
| `fechaCreacion` | Instant | Cuándo se emitió |
| `visible` | Boolean | Si es pública o fue moderada |
| `autor` | → PerfilUsuario | Quien emite la calificación |
| `calificado` | → PerfilUsuario | Quien la recibe |
| `contrato` | → ContratoArriendo | El contrato que origina la calificación |

---

## Flujo de calificación

```mermaid
flowchart TD
    A([Contrato FINALIZADO o CANCELADO]) --> B[Sistema habilita calificaciones]
    B --> C{¿Quién califica?}
    C --> ARR_CAL[Arrendador califica al arrendatario]
    C --> ART_CAL[Arrendatario califica al arrendador]
    ARR_CAL --> FORM1[Formulario:\n- Puntaje 1-5\n- Comentario\n- Tipo: ARRENDADOR_A_ARRENDATARIO]
    ART_CAL --> FORM2[Formulario:\n- Puntaje 1-5\n- Comentario\n- Tipo: ARRENDATARIO_A_ARRENDADOR]
    FORM1 --> GUARDAR[Guardar con visible=true]
    FORM2 --> GUARDAR
    GUARDAR --> REPUTACION[Actualizar índice de reputación]
    REPUTACION --> MODERACION{¿Requiere moderación?}
    MODERACION -->|Reporte de abuso| ADMIN_MOD[Administrador revisa]
    ADMIN_MOD -->|Inapropiada| OCULTAR[visible=false]
    ADMIN_MOD -->|Válida| MANTENER[Mantener visible=true]
```

---

## Índice de reputación propuesto

El índice de reputación es un número compuesto que va más allá del promedio de estrellas.

### Componentes del índice

```mermaid
graph TD
    IDX[Índice de Reputación]
    IDX --> A[Promedio de puntaje\nPeso: 40%]
    IDX --> B[Contratos completados\n÷ total contratos\nPeso: 30%]
    IDX --> C[Antigüedad en la plataforma\nPeso: 10%]
    IDX --> D[Documentos verificados\nPeso: 10%]
    IDX --> E[Perfil completo\nPeso: 10%]
```

> **Pendiente de validación:** Este cálculo de índice compuesto no está implementado. Se propone para una versión futura. ¿Se aprueba este modelo de ponderación? ¿Hay otros factores a incluir?

### Niveles de confianza (propuesta)

| Nivel | Condición | Insignia |
|---|---|---|
| **Nuevo** | Menos de 2 contratos | Sin insignia |
| **Confiable** | 2+ contratos, puntaje ≥ 4.0 | 🔵 |
| **Verificado** | Documentos aprobados + 3+ contratos | ✅ |
| **Experimentado** | 5+ contratos, puntaje ≥ 4.5 | ⭐ |
| **Premium** | 10+ contratos, puntaje ≥ 4.8, nunca cancelado | 🏆 |

---

## Perfil de reputación de un usuario

El perfil público de cualquier usuario autenticado mostrará:

```mermaid
graph TD
    PERFIL[Perfil de Reputación]
    PERFIL --> PROMEDIO[Puntaje promedio\n⭐ 4.7 / 5]
    PERFIL --> TOTAL[Total de calificaciones\n23 reseñas]
    PERFIL --> HIST_CONT[Historial de contratos:\n- Completados: 18\n- Cancelados: 2\n- Vigentes: 1\n- Total: 21]
    PERFIL --> HIST_CAL[Historial de calificaciones\nOrdenado por fecha]
    PERFIL --> NIVEL[Nivel de confianza:\n🔵 Confiable]
    PERFIL --> VERIFICADO[Estado de verificación:\n✅ Documentos aprobados]
    PERFIL --> ANTIGUEDAD[Miembro desde:\nEnero 2024]
```

### Distribución de puntajes (histograma)

```mermaid
graph LR
    subgraph "Distribución de calificaciones"
        E5["⭐⭐⭐⭐⭐ 5 estrellas: ████████████ 14"]
        E4["⭐⭐⭐⭐ 4 estrellas: ██████ 6"]
        E3["⭐⭐⭐ 3 estrellas: ██ 2"]
        E2["⭐⭐ 2 estrellas: █ 1"]
        E1["⭐ 1 estrella: 0"]
    end
```

---

## Calificaciones visibles en el perfil

Cada calificación en el historial público muestra:

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
⭐⭐⭐⭐⭐  Enero 2026
Calificado por: Carlos R. (Arrendador)

"Excelente inquilino, muy puntual con los pagos y 
cuidadoso con el inmueble. Totalmente recomendado."

Contrato: CONT-2025-001
Inmueble: Apto Chapinero Alto
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

## Reglas de negocio del sistema de reputación

| Regla | Descripción |
|---|---|
| Solo al cerrar contrato | No se puede calificar si el contrato sigue VIGENTE |
| Una calificación por contrato por dirección | El arrendador califica al arrendatario una sola vez por contrato |
| Calificación bidireccional | Ambas partes pueden calificar; ninguna puede obligar a la otra |
| Plazo de calificación | ⚠️ **Pendiente de validación:** ¿Cuántos días después del cierre del contrato puede calificarse? |
| Moderación | El administrador puede ocultar (`visible=false`) calificaciones inapropiadas |
| No anonimato | La calificación siempre identifica al autor |
| Historial permanente | Las calificaciones no se eliminan; solo se ocultan si son inapropiadas |

---

## Impacto de la reputación en el sistema

```mermaid
graph LR
    REPUTACION[Alta reputación] --> B1[Mayor visibilidad en el listado]
    REPUTACION --> B2[Arrendadores prefieren candidatos confiables]
    REPUTACION --> B3[Acceso a funciones premium\nfuturas]

    MALA_REP[Baja reputación] --> C1[Menor visibilidad]
    MALA_REP --> C2[Arrendadores pueden rechazar solicitudes]
    MALA_REP --> C3[Posible suspensión por el admin]
```

---

## Tipos de calificación: resumen completo

| TipoCalificacion | Quién | A quién | Contexto |
|---|---|---|---|
| `ARRENDADOR_A_ARRENDATARIO` | Arrendador | Arrendatario | Al cierre de ContratoArriendo |
| `ARRENDATARIO_A_ARRENDADOR` | Arrendatario | Arrendador | Al cierre de ContratoArriendo |
| `ARRENDATARIO_A_ROOMIE` | Arrendatario (anfitrión) | Roomie | Al cierre de convivencia roomie |
| `ROOMIE_A_ARRENDATARIO` | Roomie | Arrendatario (anfitrión) | Al cierre de convivencia roomie |

---

## Moderación por el administrador

```mermaid
flowchart TD
    A([Calificación recibida con visible=true]) --> B{¿Se reporta\ncomo inapropiada?}
    B -->|No| C[Permanece visible]
    B -->|Sí| D[Administrador recibe reporte]
    D --> E[Admin revisa el contenido]
    E --> F{¿Es inapropiada?}
    F -->|No| G[Mantener visible=true\nInformar al reportador]
    F -->|Sí| H[visible=false\nCalificación oculta del perfil público]
    H --> I[Se notifica al autor]
```

> **Pendiente de validación:** ¿El sistema debe implementar un mecanismo de reporte de calificaciones? Actualmente solo el administrador puede cambiar `visible` manualmente.
