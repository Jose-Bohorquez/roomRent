# PENDIENTES — RoomRent RC1

Funcionalidades planificadas que no están implementadas en esta versión.

Fecha: 2026-07-02 | Versión: RC1

---

## Prioridad ALTA

### P1 — Flujo Arrendatario completo (React)
- **Estado**: No implementado en React portal
- **Existe en**: Angular admin panel (CRUD básico)
- **Falta**: Página de solicitud de arriendo desde detalle de inmueble
- **Endpoint disponible**: `POST /api/solicitud-arriendos` (backend funcional)
- **Estimado**: 2-3 días de desarrollo

### P2 — Visitas programadas (React)
- **Estado**: No implementado en React portal
- **Existe en**: Angular admin (CRUD)
- **Falta**: Formulario para agendar visita, listado de visitas del arrendatario
- **Endpoint disponible**: `POST /api/visita-programadas` (backend funcional)
- **Estimado**: 1-2 días

### P3 — Filtros de búsqueda en listado de inmuebles
- **Estado**: La página `/properties` muestra grid pero sin filtros
- **Falta**: Filtrar por ciudad, tipo, precio, número de habitaciones
- **Backend**: La paginación existe pero no hay endpoint de búsqueda con criterios
- **Estimado**: 1 día frontend + posible endpoint backend

### P4 — Activación de email en producción
- **Estado**: La lógica está implementada (Spring Mail + resetKey en MongoDB)
- **Falta**: SMTP_PASSWORD en .env de producción. La cuenta gmail bd567358546@gmail.com necesita tener la app password configurada correctamente.
- **Síntoma**: POST /api/register devuelve 200 pero el email no llega
- **Acción**: Configurar `SPRING_MAIL_PASSWORD` en `/opt/roomrent/.env`

### P5 — Perfil de usuario (React)
- **Estado**: No hay página de perfil en React portal
- **Existe en**: Angular admin (CRUD básico)
- **Falta**: Ver/editar nombre, foto de perfil, teléfono (PerfilUsuario entity)
- **Estimado**: 1-2 días

---

## Prioridad MEDIA

### M1 — Contratos de arriendo
- **Estado**: Entidad `ContratoArriendo` existe, endpoints disponibles
- **Falta**: UI completa en React (crear contrato, firmar, gestionar)
- **Estimado**: 3-4 días

### M2 — Calificaciones (ratings)
- **Estado**: Entidad `Calificacion` existe, endpoints disponibles
- **Falta**: UI de calificación post-estadía, visualización en PropertyDetail
- **Estimado**: 2 días

### M3 — Publicaciones de Roomie
- **Estado**: Entidad `PublicacionRoomie` y `SolicitudRoomie` existen
- **Falta**: Flujo completo de búsqueda de compañero de habitación
- **Estimado**: 3-5 días

### M4 — Documentos de usuario
- **Estado**: Entidad `DocumentoUsuario` existe
- **Falta**: Subida de documentos (cédula, extractos bancarios) para solicitudes
- **Estimado**: 2 días

### M5 — Mapa en PropertyDetail
- **Estado**: Campos `latitud` y `longitud` existen en Inmueble
- **Falta**: Integración con Google Maps o Leaflet para mostrar ubicación
- **Bloqueante**: CSP actual restringe Google Maps scripts
- **Estimado**: 1-2 días + ajuste CSP

### M6 — Imágenes: miniatura principal en MisInmueblesPage
- **Estado**: La página carga inmuebles pero no hace fetch de sus multimedia
- **Falta**: Fetch adicional de multimedia o incluir multimedia en response de inmuebles
- **Estimado**: 4 horas

---

## Prioridad BAJA

### B1 — Backups automáticos
- **Estado**: No hay script de backup automatizado
- **Falta**: Script que haga `mongodump` + tar de uploads diariamente
- **Estimado**: 4 horas (ver 04-BACKUPS.md)

### B2 — Lighthouse PWA score ≥ 90
- **Estado**: PWA funcional pero sin test formal Lighthouse
- **Falta**: Ejecutar Lighthouse, corregir issues de accesibilidad y performance
- **Estimado**: 1-2 días

### B3 — HTTPS en redirect de /
- **Estado**: CORREGIDO en RC1 (commit a1436f1, pendiente deploy rebuild)
- **Verificación**: Confirmado post-deploy del rebuild en curso

### B4 — Panel de administración de multimedia (arrendador)
- **Estado**: Arrendador puede subir fotos al crear inmueble
- **Falta**: Editar/eliminar fotos de inmuebles existentes en MisInmueblesPage
- **Estimado**: 1 día

### B5 — Notificaciones en tiempo real
- **Estado**: No implementado
- **Falta**: WebSocket para notificar al arrendador nuevas solicitudes
- **Estimado**: 3-5 días (requiere Spring WebSocket)

---

## Deuda técnica

| Item | Descripción | Prioridad |
|------|-------------|-----------|
| JWT_BASE64_SECRET | Usando secret de `secret-samples` profile. Debe rotarse en producción real | ALTA |
| MisInmueblesPage imágenes | No carga thumbnail sin fetch adicional de multimedia | MEDIA |
| Paginación real en APIs | Algunos endpoints usan size=200 client-side filter vs server-side pagination | MEDIA |
| Tests unitarios | No hay tests de dominio/servicio en el proyecto | BAJA |
| Tests de integración | No hay tests de API automatizados | BAJA |
| Documentación OpenAPI | `/v3/api-docs` disponible solo para ROLE_ADMIN | BAJA |
