# 19 — Gate Review: Cierre de la Etapa de Análisis

> **Tipo:** Documento de cierre oficial  
> **Fecha:** 2026-07-02  
> **Comité:** Arquitectura de Software — Revisión Pre-Implementación  
> **Alcance:** 18 documentos + código fuente + configuración JHipster

---

## 1. Estado general del proyecto

### Documentación

| # | Documento | Líneas | Estado |
|---|---|---|---|
| 01 | Introducción | 149 | ✅ |
| 02 | Actores | 331 | ✅ |
| 03 | Casos de uso | 424 | ✅ |
| 04 | Flujo general | 179 | ✅ |
| 05 | Registro y login | 178 | ✅ |
| 06 | Arrendador | 266 | ✅ |
| 07 | Arrendatario | 233 | ✅ |
| 08 | Roomie | 230 | ✅ |
| 09 | Visitas | 179 | ✅ |
| 10 | Contratos | 197 | ✅ |
| 11 | Calificaciones | 198 | ✅ |
| 12 | Multimedia | 213 | ✅ |
| 13 | Modelo de negocio | 214 | ✅ |
| 14 | Entidades | 328 | ✅ |
| 15 | Roadmap | 318 | ✅ |
| 16 | Revisión funcional | 709 | ✅ |
| 17 | Reglas de negocio definitivas | 643 | ✅ |
| 18 | Plan de migración técnica | 1059 | ✅ |

**Total:** 6.048 líneas de documentación auditada.

### Proyecto en código

| Componente | Verificado | Estado |
|---|---|---|
| Spring Boot 4.0.6 | Compila y arranca | ✅ |
| Angular 21 | Compila y funciona | ✅ |
| React 18 portal `/portal/` | Funciona | ✅ |
| MongoDB | Conectado, 11 colecciones | ✅ |
| JWT + Spring Security | Activo | ✅ |
| 11 entidades JHipster | CRUD completo en admin | ✅ |
| 11 ServiceImpl | Presentes, sin lógica de negocio aún | ✅ |
| 9 enums Java | Definidos, compilados | ✅ |
| JDL actualizado | Refleja el modelo actual | ✅ |

---

## 2. Checklist de aprobación

| Pregunta | Respuesta | Sustento |
|---|---|---|
| ¿El modelo de negocio está completamente definido? | **SÍ** | Jerarquía de dominio, 9 estados de publicación, AcuerdoConvivencia, acceso escalonado — todos en doc 17 |
| ¿Las reglas de negocio son suficientes? | **SÍ** | Doc 17 cubre cada transición de estado, validación de negocio, ventana de calificación, expiración automática, permisos CRUD |
| ¿Las relaciones están correctamente planteadas? | **SÍ** | JDL verificado. Las 4 entidades nuevas tienen relaciones definidas en doc 18. La relación `PublicacionRoomie → ContratoArriendo` es mejora futura, no bloqueante |
| ¿Los actores están completamente definidos? | **SÍ** | 5 actores (Admin, Arrendador, Arrendatario, Roomie/Postulante, Visitante) en docs 02 y 17 con casos de uso por actor |
| ¿Los permisos están correctamente definidos? | **SÍ** | Modelo escalonado Nivel 0-3, matriz CRUD completa en doc 17 |
| ¿Los estados del sistema están completos? | **SÍ** | EstadoPublicacion (9), EstadoSolicitud (6), EstadoContrato (5), EstadoVisita (4), EstadoAcuerdo (4), EstadoOcupacion (3) |
| ¿Las entidades soportan el negocio? | **SÍ** | 11 existentes + 4 nuevas cubren todos los flujos documentados |
| ¿Las nuevas entidades propuestas son suficientes? | **SÍ** | Notificacion, AcuerdoConvivencia, OcupacionUnidad, HistorialPrecio resuelven las 4 brechas identificadas en la auditoría (doc 16) |
| ¿El plan de migración es coherente? | **SÍ** | 9 fases con rollback definido por fase, cada fase deja el proyecto compilable |
| ¿La estrategia de regeneración de JHipster es segura? | **SÍ** | Entidades nuevas se generan con JHipster; entidades existentes se modifican manualmente. Las plantillas personalizadas no se sobreescriben |
| ¿El roadmap es viable? | **SÍ** | 54 horas estimadas en 14 días hábiles, fases de menor a mayor riesgo |
| ¿La arquitectura soportará crecimiento? | **SÍ** | MongoDB schemaless permite agregar campos sin migración; patrón `service all with serviceImpl` es extensible; dual-SPA permite evolucionar portal sin afectar admin |
| ¿El proyecto está listo para implementación? | **SÍ** | Sujeto a resolución de los riesgos bloqueantes listados a continuación |

---

## 3. Riesgos bloqueantes

Se identificó **un único riesgo bloqueante**. No requiere nuevo documento. Requiere una decisión del desarrollador antes de ejecutar el Paso 1 de la Fase 1.

---

### RB-01 — Inconsistencia de valores en EstadoPublicacion

**Nivel:** BLOQUEANTE OPERACIONAL (Fase 1, Día 1)

**Descripción:**

El enum `EstadoPublicacion.java` en el código fuente actual tiene estos valores:
```
BORRADOR, PUBLICADO, PAUSADO, ARRENDADO, FINALIZADO
```

El documento de reglas de negocio definitivas (doc 17) define el ciclo de vida con estos valores:
```
BORRADOR, PUBLICADA, VISITA_AGENDADA, POSTULANTE_SELECCIONADO,
RESERVADA, CONTRATO_EN_FIRMA, ARRENDADA, FINALIZADA, ARCHIVADA
```

Las diferencias son:
- `PUBLICADO` (código) ≠ `PUBLICADA` (doc 17)
- `PAUSADO` (código) — no existe en doc 17
- `ARRENDADO` (código) ≠ `ARRENDADA` (doc 17)
- `FINALIZADO` (código) ≠ `FINALIZADA` (doc 17)

**Por qué bloquea:**

El doc 18 presenta dos instrucciones contradictorias en la misma sección:
1. La tabla de estados dice: "ARRENDADO → ARRENDADA (renombrar)"
2. La ALERTA CRÍTICA dice: "NO renombrar los valores existentes"

Si el desarrollador sigue la instrucción 2 (no renombrar), el resultado es un enum con valores semánticamente duplicados: `ARRENDADO` y `ARRENDADA` coexistirían como dos constantes para el mismo estado. La máquina de estados de la Fase 5 tendría que validar contra ambos valores en cada condición, generando deuda técnica desde el primer commit.

Si el desarrollador sigue la instrucción 1 (renombrar), debe ejecutar una migración de datos sobre MongoDB antes de cambiar el enum.

**Resolución requerida (una decisión, no un documento):**

Este proyecto está en etapa de desarrollo. La base de datos contiene únicamente datos de prueba. La resolución técnica correcta es:

> **Decisión:** Renombrar los valores del enum a los definidos en doc 17 (`PUBLICADA`, `ARRENDADA`, `FINALIZADA`). Eliminar `PAUSADO`. Ejecutar la migración de datos de prueba con dos comandos MongoDB antes de modificar el enum Java. Esta operación toma menos de 10 minutos sobre datos de desarrollo.

El desarrollador debe tomar esta decisión antes de abrir `EstadoPublicacion.java` el Día 1.

---

## 4. Decisión final

```
╔══════════════════════════════════════════════════════════════╗
║                                                              ║
║        APROBADO PARA IMPLEMENTACIÓN                         ║
║                                                              ║
╚══════════════════════════════════════════════════════════════╝
```

**Condición previa al Paso 1 de la Fase 1:**

Antes de modificar cualquier archivo de código, el desarrollador debe resolver el riesgo bloqueante RB-01: decidir explícitamente si renombra los valores inconsistentes del enum `EstadoPublicacion` o los mantiene con valores paralelos. La recomendación del comité es renombrar, dado que el proyecto está en etapa de desarrollo sin datos de producción.

Una vez resuelta esa decisión, el desarrollador puede iniciar la implementación siguiendo el orden de fases definido en `docs/18-plan-migracion-tecnica.md`.

---

**Fin de la etapa de análisis. Inicio de la etapa de construcción.**
