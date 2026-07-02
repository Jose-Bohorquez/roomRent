# CHANGELOG — RoomRent

Historial completo de cambios por versión desde el inicio del proyecto.

---

## [0.3.0-RC1] — 2026-07-02

### Release Candidate 1 — Auditoría y corrección de bugs

**Commits RC1 (Fase de auditoría)**

| Hash | Fecha | Descripción | Impacto |
|------|-------|-------------|---------|
| `a1436f1` | 2026-07-02 | fix(config): enable forward-headers-strategy | Redirects HTTPS→HTTPS correctos, emails con URL https:// |
| `acafa33` | 2026-07-02 | fix(security+ux): multimedia-inmuebles público + filtro por owner | Fotos visibles sin login; Mis Inmuebles solo muestra propios |
| `71022a8` | 2026-07-02 | fix(docker): /app/uploads con permisos roomrent en Dockerfile | Subida de fotos funcional en deploys limpios |
| `c98a1b1` | 2026-07-02 | fix(config): merge duplicate spring: keys en application.yml | SnakeYAML 2.x no crashea en startup |
| `7cb0f25` | 2026-07-02 | fix(build): Path.of() en lugar de Paths.get() | modernizer-maven-plugin deja de rechazar el build |

---

## [0.2.0] — 2026-07-02

### Portal React — Módulo Arrendador completo

| Hash | Fecha | Descripción | Impacto |
|------|-------|-------------|---------|
| `ba833b4` | 2026-07-02 | feat(gallery): lightbox de fotos en PropertyDetail | Galería con navegación y fullscreen |
| `6288972` | 2026-07-02 | feat(arrendador): wizard creación + Mis Inmuebles + rutas | Flujo completo arrendador en React |
| `00aba0c` | 2026-07-02 | feat(api): propiedadApi, multimediaApi, publicacionRawApi, uploadApi | Capa API completa con manejo 401 |
| `ec205ba` | 2026-07-02 | fix(auth): redirect a dashboard según rol | Login lleva al dashboard correcto por rol |
| `7f964b3` | 2026-07-02 | feat(db): índices MongoDB via Mongock (migration 003) | Queries optimizadas en ciudad, estado, principal |
| `4b5d13e` | 2026-07-02 | feat(upload): multipart file upload para multimedia de inmueble | POST /api/uploads/multimedia funcional |
| `7d9a2e5` | 2026-07-02 | fix(security): ROLE_ARRENDADOR y ROLE_ARRENDATARIO en SecurityConfig | Nuevos roles pueden usar sus endpoints |
| `ec24f14` | 2026-07-02 | fix(nginx): Cache-Control no-store para sw.js | PWA se actualiza automáticamente |
| `c52e576` | 2026-07-02 | fix(nginx): MIME type correcto para manifest.webmanifest | Chrome reconoce PWA correctamente |
| `8c9c65b` | 2026-07-02 | feat(pwa): Progressive Web App support en React portal | Installable, offline, iconos, manifest |
| `dbc37b5` | 2026-07-02 | perf(nginx): gzip, cache, canonical redirect | Respuestas comprimidas, headers de cache |
| `856b395` | 2026-07-02 | fix(docker): Maven stage copia proyecto completo + bash | Docker build funcional end-to-end |
| `a9cb347` | 2026-07-02 | fix(portal): rutas relativas en dashboards | Sin dependencia de localhost:8080 hardcodeado |
| `e01fbe4` | 2026-07-02 | feat(deploy): Docker multi-stage + infraestructura producción | Deploy containerizado en EC2 |
| `c91e3f2` | 2026-07-02 | feat(config): secretos en variables de entorno | Sin credenciales en código o git |

---

## [0.1.0] — 2026-07-01 a 2026-07-02

### Documentación de dominio + preparación técnica

| Hash | Fecha | Descripción | Impacto |
|------|-------|-------------|---------|
| `b49232e` | 2026-07-02 | chore(git): restore frontRoomRent source | Fuente React en git (no gitlink) |
| `95d29c2` | 2026-07-02 | docs: análisis multimedia antes de Fase 1 | Decisiones técnicas documentadas |
| `e1c73f7` | 2026-07-02 | refactor(seed): DevDataSeeder con dataset realista | Datos demo coherentes con el dominio |
| `cbc767f` | 2026-07-02 | refactor(domain): normalizar EstadoPublicacion | Estados del ciclo de vida correctos |
| `e22f165` | 2026-07-02 | docs(architecture): gate review pre-implementación | Validación de arquitectura documentada |
| `a59c5fb` | 2026-07-02 | docs(architecture): plan migración técnica v2 | Ruta de evolución del modelo de datos |
| `87000c6` | 2026-07-01 | docs: documentación funcional completa | 21 archivos de dominio y casos de uso |
| `796886f` | 2026-07-01 | docs: JDL actualizado + README reescrito | Modelo de datos formal |

---

## [0.0.2] — 2026-06-30 a 2026-07-01

### Angular Admin Panel + temas visuales

| Hash | Fecha | Descripción | Impacto |
|------|-------|-------------|---------|
| `67c7814` | 2026-07-01 | feat: DataTable moderno para 12 tablas CRUD | UX admin mejorada |
| `6b09b5d` | 2026-06-30 | style: Bootstrap dark theme via Sass | Tema oscuro consistente |
| `b6453bf` | 2026-06-30 | style: rediseño páginas CRUD y login | Layout unificado |
| `26a66b9` | 2026-06-30 | feat: sidebar navigation + app-shell layout | Navegación lateral en admin |
| `1135455` | 2026-06-30 | redesign: dark admin theme completo | Navbar, dashboard, CRUD en dark |
| `b4491e5` | 2026-06-30 | fix: NG0201 TemplateRef + login redirect | Errores Angular resueltos |
| `9feb4b8` | 2026-06-30 | fix: 404 post-login por ruta faltante | Navegación admin correcta |
| `eb87192` | 2026-06-30 | security: CRUDs solo ROLE_ADMIN + paginación | Acceso admin protegido |
| `4d5f4e0` | 2026-06-30 | SMTP config con variables de entorno | Correos de activación funcionan |
| `6c48d16` | 2026-06-30 | Add auditing a SolicitudArriendo | Trazabilidad de solicitudes |

---

## [0.0.1] — 2026-06-25

### Scaffold inicial JHipster + integración React

| Hash | Fecha | Descripción | Impacto |
|------|-------|-------------|---------|
| `894d2ab` | 2026-06-25 | fix: seguridad para servir React frontend | Spring Boot sirve /portal/** |
| `d1508be` | 2026-06-25 | feat: integrar React Vite como interfaz principal | Portal público disponible |
| `15f3bf7` | 2026-06-25 | fix: navbar + Dashboard de arrendador | Dropdowns y navegación funcional |
| `2af48a6` | 2026-06-25 | fix: compilar frontend + linting | Build sin errores |
| `7d618e8` | 2026-06-25 | mejoras UX: traducciones y enumeradores | Labels en español |
| `8ae0a42` | 2026-06-25 | first commit | Scaffold JHipster 9.1.0 inicial |

---

## Resumen estadístico

| Métrica | Valor |
|---------|-------|
| Total commits | 36 |
| Período | 2026-06-25 → 2026-07-02 |
| Bugs corregidos | 8 |
| Features añadidas | 11 |
| Refactors | 3 |
| Documentos | 9 |
| Mejoras perf/style | 5 |
| Rama principal | `main` |
| Repositorio | github.com/Jose-Bohorquez/roomRent |
