# ROADMAP — RoomRent

Funcionalidades planificadas y deuda técnica pendiente post-RC1.

Fecha: 2026-07-02 | Versión: RC1

---

## Versión 0.1.0 — Arrendatario MVP

**Objetivo**: Cerrar el flujo completo arrendatario (buscar → contactar → solicitar → visitar)

### Funcionalidades

| Feature | Descripción | Esfuerzo | Dependencias |
|---------|-------------|----------|-------------|
| Solicitud de arriendo (React) | Formulario en PropertyDetail para enviar solicitud | 2 días | Backend ya existe |
| Visitas programadas (React) | Agendar visita desde PropertyDetail | 1-2 días | Backend ya existe |
| Mis solicitudes (arrendatario) | Panel del arrendatario: ver estado de solicitudes | 1 día | — |
| Mis visitas (arrendatario) | Ver agenda de visitas del arrendatario | 1 día | — |
| Notificación al arrendador | Email cuando llega solicitud/visita | 1 día | SMTP en .env |
| Filtros en listado | Filtrar por ciudad, tipo, precio, habitaciones | 1 día | — |

**Total estimado**: 7-10 días

---

## Versión 0.2.0 — Calidad y gestión

**Objetivo**: Mejorar experiencia del arrendador y add confianza con calificaciones

### Funcionalidades

| Feature | Descripción | Esfuerzo |
|---------|-------------|----------|
| Calificaciones y reseñas | Rating post-visita/estadía | 2 días |
| Editar/eliminar fotos en MisInmueblesPage | Gestión de multimedia existente | 1 día |
| Perfil de usuario (React) | Ver/editar datos personales | 1-2 días |
| Panel de contratos (admin) | Gestión de ContratoArriendo | 3 días |
| Documentos de usuario | Subida de documentos (cédula, extractos) | 2 días |

**Total estimado**: 9-12 días

---

## Versión 0.3.0 — Roomies y mapa

**Objetivo**: Funcionalidad de búsqueda de compañero + geolocalización

### Funcionalidades

| Feature | Descripción | Esfuerzo |
|---------|-------------|----------|
| PublicacionRoomie (React) | Buscar/postular compañero de habitación | 4-5 días |
| Mapa en PropertyDetail | Google Maps / Leaflet con lat/lng | 1-2 días + CSP |
| Búsqueda por mapa | Ver inmuebles en mapa interactivo | 2-3 días |
| Notificaciones en tiempo real | WebSocket para alertas al arrendador | 3-5 días |

**Total estimado**: 10-15 días

---

## Versión 1.0.0 — Producción real

**Objetivo**: Sistema listo para usuarios reales con toda la infraestructura productiva

### Funcionalidades y mejoras

| Item | Descripción | Prioridad |
|------|-------------|-----------|
| JWT secret rotado | Reemplazar el de `secret-samples` | CRÍTICA |
| Email activación funcional | Configurar SMTP en .env | ALTA |
| Backup automático | Script cron diario MongoDB + uploads | ALTA |
| Lighthouse PWA ≥ 90 | Accesibilidad, performance, SEO | ALTA |
| Tests de integración | Suites API automatizadas | MEDIA |
| Dominio propio registrado | Renovación anual, no academia | ALTA |
| EC2 con restart policy | `restart: unless-stopped` en docker-compose.yml | ALTA |
| Monitoreo (UptimeRobot) | Alerta cuando la app cae | MEDIA |
| CDN para fotos | S3 + CloudFront para imágenes | MEDIA |

---

## Deuda técnica

### Alta prioridad

| Item | Descripción | Impacto |
|------|-------------|---------|
| JWT_BASE64_SECRET en .env | Actual secret está en git público (secret-samples profile) | Seguridad crítica en producción real |
| Email SMTP funcional | SPRING_MAIL_PASSWORD sin configurar | Registro sin activación |
| MisInmueblesPage thumbnails | Falta fetch de multimedia por inmueble | UX arrendador pobre |
| restart: unless-stopped | Contenedores no auto-inician post-EC2-reboot | Disponibilidad |

### Media prioridad

| Item | Descripción | Impacto |
|------|-------------|---------|
| Paginación server-side real | Varios endpoints usan size=200 client-side filter | Escalabilidad |
| OpenAPI pública | `/v3/api-docs` solo accesible para ROLE_ADMIN | DX integraciones |
| BUG-007 thumbnails | MisInmueblesPage no muestra foto de inmueble | UX |
| Log rotation | Logs Docker pueden crecer indefinidamente | Operación |
| .env backup automático | Solo documentado, no automatizado | Recuperación |

### Baja prioridad

| Item | Descripción |
|------|-------------|
| Tests unitarios de dominio | Cero tests en proyecto actualmente |
| Tests de integración Spring | Cero tests de API automatizados |
| SAST / dependabot | Sin análisis de vulnerabilidades automatizado |
| Compresión de imágenes | Fotos se suben sin resize/compress |
| Lazy loading React | PropertyDetail carga todo inmediatamente |

---

## Arquitectura futura (2.0.0)

Si el proyecto crece a escala real, considerar:

```
Actual (RC1):          Futura (2.0.0):
─────────────          ──────────────
EC2 t2.micro           EC2 t3.medium
1 Docker host          ECS Fargate (auto-scaling)
MongoDB local          MongoDB Atlas
/uploads local         S3 + CloudFront
Sin CI/CD              GitHub Actions pipeline
Sin monitoreo          CloudWatch + PagerDuty
nginx manual           ALB + WAF
```

**Migración incremental recomendada:**
1. S3 para uploads (sencillo, gran impacto)
2. GitHub Actions CI/CD (reduce tiempo de deploy)
3. MongoDB Atlas (elimina gestión de BD)
4. ECS Fargate (último paso, mayor complejidad)

---

## Calendario tentativo

| Versión | Meta | Funcionalidad principal |
|---------|------|------------------------|
| 0.1.0 | Agosto 2026 | Flujo arrendatario completo |
| 0.2.0 | Septiembre 2026 | Calificaciones + gestión avanzada |
| 0.3.0 | Octubre 2026 | Roomies + mapa |
| 1.0.0 | Noviembre 2026 | Producción real con infraestructura robusta |

---

## Contexto académico

RoomRent es un proyecto del programa ADSO (Análisis y Desarrollo de Software) del SENA, trimestre 4, ficha 3311941. La arquitectura fue generada con JHipster 9.1.0 y el desarrollo se realizó como trabajo de aprendizaje, por lo que:

- Las credenciales de desarrollo (JWT secret, MongoDB) son para ambiente de prueba
- La instancia EC2 es del plan de estudiantes de AWS Academy (se apaga periódicamente)
- La cuenta Gmail es de prueba para la funcionalidad de email
- El dominio room-rent.xyz está registrado para el proyecto académico

Para un despliegue en producción real con usuarios reales, se requeriría rotar TODOS los secretos y migrar a infraestructura de producción.
