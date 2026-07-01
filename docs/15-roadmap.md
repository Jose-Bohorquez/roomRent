# 15 — Roadmap Funcional

## Metodología

El roadmap está organizado en fases. Cada fase se ejecuta solo después de que la fase anterior haya sido completamente revisada y aprobada. Ninguna funcionalidad entra en desarrollo sin documentación y aprobación previa.

```
Documentación → Revisión → Correcciones → Aprobación → Implementación
```

---

## Estado actual del sistema

```mermaid
graph TD
    subgraph "Completamente implementado"
        A1[Autenticación JWT]
        A2[Registro y activación por correo]
        A3[CRUD de los 11 entidades]
        A4[Panel administrativo JHipster]
        A5[Portal React público con listado de publicaciones]
        A6[Búsqueda y filtrado de publicaciones]
    end

    subgraph "Parcialmente implementado"
        B1[Gestión de inmuebles\nsin agrupación por edificio]
        B2[Publicación de inmueble\nsin flujo guiado de estados]
        B3[Galería de multimedia\nsin carga directa de archivos]
        B4[Contrato digital\nsin generación automática de PDF]
    end

    subgraph "No implementado"
        C1[Flujos de solicitud guiados\npor la UI de arrendatario]
        C2[Programación de visitas\ndesde el portal]
        C3[Sistema de pagos]
        C4[Notificaciones in-app]
        C5[Sistema de reputación calculado]
        C6[Generación automática de contrato]
        C7[Panel del arrendador\ncon vista de sus inmuebles]
        C8[Panel del arrendatario\ncon sus contratos y visitas]
    end
```

---

## Módulo 1: Autenticación

**Estado:** ✅ Completamente funcional.

| Funcionalidad | Estado | Notas |
|---|---|---|
| Registro de usuario | ✅ | JHipster estándar |
| Activación por email | ✅ | JHipster estándar |
| Login con JWT | ✅ | Token en `localStorage` |
| Recuperación de contraseña | ✅ | JHipster estándar |
| Cambio de contraseña | ✅ | JHipster estándar |
| Logout | ✅ | JHipster estándar |
| Creación automática de PerfilUsuario al registrarse | ⚠️ Pendiente | El usuario se registra pero debe crear su PerfilUsuario manualmente |
| Perfil completo al primer ingreso (onboarding) | ❌ No implementado | — |

**Roadmap Módulo 1:**

```mermaid
flowchart LR
    F1[FASE 1\nCrear PerfilUsuario automático\nal registrarse] --> F2[FASE 2\nFlujo de onboarding:\ncompletar perfil al primer ingreso]
```

---

## Módulo 2: Landing y Portal Público

**Estado:** ✅ React portal funcional en `/portal/`.

| Funcionalidad | Estado | Notas |
|---|---|---|
| Landing page con hero | ✅ | Implementado en React |
| Listado de publicaciones activas | ✅ | Paginado, con filtros |
| Búsqueda por texto | ✅ | |
| Filtro por ciudad, estrato, tipo de inmueble | ✅ | |
| Detalle de publicación | ⚠️ Parcial | Muestra datos, sin call-to-action para solicitar |
| Galería de fotos en el detalle | ⚠️ Parcial | Muestra URLs, sin carrusel |
| Botón "Solicitar visita" / "Contactar" | ❌ No implementado | Solo disponible si autenticado |
| Filtro por precio (rango) | ⚠️ Pendiente de validación | ¿Incluir este filtro? |

**Roadmap Módulo 2:**

```mermaid
flowchart LR
    F1[FASE 1\nDetalle de publicación completo\ncon galería] --> F2[FASE 2\nCall-to-action para usuarios\nautenticados] --> F3[FASE 3\nFiltros avanzados\npor precio y características]
```

---

## Módulo 3: Administración

**Estado:** ✅ Panel administrativo JHipster completo.

| Funcionalidad | Estado | Notas |
|---|---|---|
| Gestión de usuarios | ✅ | Panel JHipster |
| CRUD de todas las entidades | ✅ | Panel JHipster con tablas rediseñadas |
| Aprobación de documentos de verificación | ⚠️ Parcial | El campo `aprobado` existe; no hay flujo visual dedicado |
| Moderación de calificaciones | ⚠️ Parcial | El campo `visible` existe; no hay cola de moderación |
| Dashboard con estadísticas | ❌ No implementado | — |
| Alertas de acciones pendientes | ❌ No implementado | — |

**Roadmap Módulo 3:**

```mermaid
flowchart LR
    F1[FASE 1\nFlujo de aprobación\nde documentos] --> F2[FASE 2\nCola de moderación\nde calificaciones] --> F3[FASE 3\nDashboard\ncon métricas del negocio]
```

---

## Módulo 4: Gestión de Inmuebles

**Estado:** ⚠️ CRUD completo; sin flujos de arrendador.

| Funcionalidad | Estado | Notas |
|---|---|---|
| Crear inmueble | ✅ | CRUD disponible |
| Editar inmueble | ✅ | CRUD disponible |
| Eliminar inmueble | ✅ | Sin restricción si tiene contrato vigente |
| Ver mis inmuebles (panel arrendador) | ❌ No implementado | Solo accesible desde el panel admin |
| Agrupación por edificio | ❌ No implementado | Requiere entidad `Edificio` |
| Restricción de borrado con contrato vigente | ❌ No implementado | Regla de negocio pendiente |

**Roadmap Módulo 4:**

```mermaid
flowchart LR
    F1[FASE 1\nPanel del arrendador:\nver mis inmuebles] --> F2[FASE 2\nValidaciones de negocio:\nno borrar con contrato activo] --> F3[FASE 3\nEntidad Edificio\npara agrupar unidades]
```

---

## Módulo 5: Publicaciones de Inmuebles

**Estado:** ⚠️ CRUD completo; sin flujo de estados guiado.

| Funcionalidad | Estado | Notas |
|---|---|---|
| Crear publicación | ✅ | CRUD disponible |
| Editar publicación | ✅ | CRUD disponible |
| Cambio de estado (BORRADOR → PUBLICADO) | ⚠️ Parcial | Campo editable, sin flujo guiado |
| Una sola publicación activa por inmueble | ❌ No implementado | Regla de negocio pendiente |
| Historial de publicaciones | ⚠️ Parcial | Los datos están; sin vista dedicada |
| Métricas de visualización | ❌ No implementado | Campo `cantidadVistas` no existe |

**Roadmap Módulo 5:**

```mermaid
flowchart LR
    F1[FASE 1\nFlujo guiado de estados\nBORRADOR → PUBLICADO → ARRENDADO] --> F2[FASE 2\nValidación: una publicación\nactiva por inmueble] --> F3[FASE 3\nMétricas de visitas\npor publicación]
```

---

## Módulo 6: Multimedia

**Estado:** ⚠️ CRUD de URLs; sin carga directa de archivos.

| Funcionalidad | Estado | Notas |
|---|---|---|
| Registrar URL de multimedia | ✅ | CRUD disponible |
| Marcar foto como principal | ✅ | Campo `principal` editable |
| Galería ordenable | ❌ No implementado | Campo `orden` no existe |
| Carga directa de archivos | ❌ No implementado | Requiere integración con almacenamiento externo |
| Validación de un solo `principal=true` | ❌ No implementado | Regla de negocio pendiente |
| Documentos de verificación del usuario | ⚠️ Parcial | `DocumentoUsuario` existe; sin flujo de carga en frontend de usuario |
| Foto de perfil de usuario | ❌ No implementado | Campo no existe en `PerfilUsuario` |

**Roadmap Módulo 6:**

```mermaid
flowchart LR
    F1[FASE 1\nFlujo de carga de\ndocumentos de verificación] --> F2[FASE 2\nGalería con orden\ny foto principal] --> F3[FASE 3\nCarga directa\nde archivos S3]
```

---

## Módulo 7: Solicitudes y Visitas

**Estado:** ❌ Entidades existen; sin flujos de usuario en el portal.

| Funcionalidad | Estado | Notas |
|---|---|---|
| Enviar solicitud de arriendo desde la publicación | ❌ No implementado | Solo CRUD desde panel admin |
| Ver mis solicitudes enviadas (arrendatario) | ❌ No implementado | — |
| Ver solicitudes recibidas (arrendador) | ❌ No implementado | — |
| Aprobar / rechazar solicitud | ❌ No implementado | — |
| Programar visita desde la solicitud | ❌ No implementado | — |
| Confirmar / cancelar visita | ❌ No implementado | — |
| Notificaciones de cambio de estado | ❌ No implementado | — |

**Roadmap Módulo 7:**

```mermaid
flowchart LR
    F1[FASE 1\nFlujo de solicitud para\nel arrendatario desde el portal] --> F2[FASE 2\nPanel del arrendador:\ngestionar solicitudes recibidas] --> F3[FASE 3\nProgramación de visitas\ny notificaciones]
```

---

## Módulo 8: Contratos

**Estado:** ⚠️ CRUD completo; sin generación automática de documentos.

| Funcionalidad | Estado | Notas |
|---|---|---|
| Crear contrato | ✅ | CRUD disponible |
| Adjuntar URL del documento | ✅ | Campo `urlContratoDigital` |
| Cambio de estado del contrato | ⚠️ Parcial | Campo editable, sin flujo guiado |
| Vista del contrato para el arrendatario | ❌ No implementado | — |
| Generación automática del PDF | ❌ No implementado | Propuesta futura |
| Firma electrónica | ❌ No implementado | Propuesta futura |
| Registro de pagos mensuales | ❌ No implementado | Entidad `PagoArriendo` no implementada |
| Historial de contratos por inmueble | ⚠️ Parcial | Datos disponibles; sin vista dedicada |

**Roadmap Módulo 8:**

```mermaid
flowchart LR
    F1[FASE 1\nFlujo guiado de estados\ndel contrato] --> F2[FASE 2\nVista del contrato\npara el arrendatario] --> F3[FASE 3\nRegistro de pagos\nmensuales] --> F4[FASE 4\nGeneración\nautomática del PDF]
```

---

## Módulo 9: Calificaciones

**Estado:** ⚠️ CRUD completo; sin flujo post-contrato en portal.

| Funcionalidad | Estado | Notas |
|---|---|---|
| Crear calificación | ✅ | CRUD disponible |
| Ver calificaciones de un usuario | ⚠️ Parcial | No hay perfil público de reputación |
| Habilitación de calificación al cerrar contrato | ❌ No implementado | Regla de negocio pendiente |
| Índice de reputación calculado | ❌ No implementado | Solo promedio manual |
| Niveles de confianza (Confiable, Verificado, etc.) | ❌ No implementado | Propuesta pendiente de validación |
| Moderación de calificaciones por admin | ⚠️ Parcial | Campo `visible` existe; sin cola de moderación |

**Roadmap Módulo 9:**

```mermaid
flowchart LR
    F1[FASE 1\nFlujo de calificación\npost-contrato en el portal] --> F2[FASE 2\nPerfil público\nde reputación] --> F3[FASE 3\nÍndice de reputación\ncalculado automáticamente]
```

---

## Módulo 10: Roomies

**Estado:** ⚠️ CRUD completo; sin flujos guiados en portal.

| Funcionalidad | Estado | Notas |
|---|---|---|
| Publicar habitación para roomie | ✅ | CRUD disponible |
| Buscar habitaciones roomie desde portal | ⚠️ Parcial | No hay sección dedicada en el portal |
| Enviar solicitud de roomie | ❌ No implementado | Solo CRUD desde panel admin |
| Gestionar solicitudes recibidas (arrendatario) | ❌ No implementado | — |
| Habilitación de la función roomie (habilitadoRoomie) | ⚠️ Parcial | Campo existe; sin flujo de activación |
| Compatibilidad automática de perfiles | ❌ No implementado | Propuesta futura |

**Roadmap Módulo 10:**

```mermaid
flowchart LR
    F1[FASE 1\nSección de búsqueda\nde roomies en el portal] --> F2[FASE 2\nFlujo de solicitud\npara postulantes] --> F3[FASE 3\nGestión de solicitudes\npara el arrendatario anfitrión]
```

---

## Vista consolidada del roadmap

```mermaid
gantt
    title RoomRent - Roadmap por fases
    dateFormat YYYY-MM
    section Fase 1: Arrendador
    Panel del arrendador (inmuebles) :f1a, 2026-07, 1M
    Flujo BORRADOR → PUBLICADO :f1b, 2026-07, 1M
    Onboarding al registrarse :f1c, 2026-07, 1M
    section Fase 2: Arrendatario
    Detalle publicación + solicitar :f2a, 2026-08, 1M
    Panel del arrendatario :f2b, 2026-08, 1M
    Programación de visitas :f2c, 2026-08, 1M
    section Fase 3: Contratos y Calificaciones
    Flujo guiado del contrato :f3a, 2026-09, 1M
    Calificación post-contrato :f3b, 2026-09, 1M
    Perfil de reputación :f3c, 2026-09, 1M
    section Fase 4: Roomies y Multimedia
    Sección roomie en portal :f4a, 2026-10, 1M
    Carga directa de archivos :f4b, 2026-10, 1M
    section Fase 5: Avanzado
    Entidad Edificio :f5a, 2026-11, 2M
    Registro de pagos :f5b, 2026-11, 2M
    Generación de PDF :f5c, 2026-11, 2M
    Notificaciones in-app :f5d, 2026-11, 2M
```

---

## Prioridades inmediatas (post-aprobación de la documentación)

Estas son las funcionalidades de mayor impacto que deberían implementarse primero:

| Prioridad | Funcionalidad | Razón |
|---|---|---|
| 1 | Panel del arrendador para gestionar sus inmuebles | Sin esto, el flujo del arrendador es imposible sin acceso al panel admin |
| 2 | Detalle de publicación con "Solicitar arriendo" | Sin esto, el portal es solo informativo, no transaccional |
| 3 | Flujo de solicitud para el arrendatario | Core del negocio |
| 4 | Flujo de solicitud para el arrendador (aprobar/rechazar) | Complementa el flujo del arrendatario |
| 5 | Onboarding: crear PerfilUsuario al registrarse | Fricción alta para el nuevo usuario hoy |
| 6 | Flujo de contrato guiado | Formaliza la transacción |
| 7 | Calificación post-contrato en portal | Habilita el sistema de reputación |
| 8 | Sección roomie en portal | Diferenciador del producto |
