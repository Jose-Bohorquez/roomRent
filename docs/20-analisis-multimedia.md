# Análisis de Implementación Multimedia — RoomRent

**Fecha:** 2026-07-02  
**Estado:** Análisis completado — pendiente decisión de implementación

---

## 1. Estado Actual

### MultimediaInmueble
```java
@Field("url_media")   private String urlMedia;   // URL externa o ruta interna
@Field("tipo_media")  private String tipoMedia;  // MIME type ("image/jpeg")
@Field("principal")   private Boolean principal;
@Field("titulo")      private String titulo;
```
**Solo guarda URLs externas** (actualmente Pexels para seeds). No existe mecanismo para cargar archivos al servidor.

### DocumentoUsuario
```java
@Field("url_archivo")    private String urlArchivo;  // URL externa ("https://storage.roomrent.dev/...")
@Field("tipo_mime")      private String tipoMime;
@Field("tamano_archivo") private Long tamanoArchivo; // solo informativo
```
**Solo guarda URLs externas**. El sistema asume que los archivos viven en un storage externo que no existe en el proyecto.

### PerfilUsuario
No tiene campo de foto de perfil. La foto de perfil tampoco está en el JDL.

---

## 2. Necesidades Identificadas

| Tipo de archivo | Entidad relacionada | Tamaño típico | Acceso |
|---|---|---|---|
| Foto de perfil | PerfilUsuario | < 2 MB | Público |
| Fotos de inmueble | MultimediaInmueble | 1-5 MB c/u | Público |
| Videos de inmueble | MultimediaInmueble | 50-500 MB | Público |
| Documentos de usuario | DocumentoUsuario | 200 KB - 5 MB | Privado |
| Contratos PDF | ContratoArriendo | 1-10 MB | Privado |

---

## 3. Opciones Evaluadas

### Opción A: JHipster Blob Fields (base64 en MongoDB)

**Cómo funciona:** JHipster genera campos `ImageBlob` o `AnyBlob` que almacenan el archivo codificado en base64 directamente en el documento MongoDB.

**Ventajas:**
- Cero configuración adicional — JHipster genera todo (formulario, API, preview)
- Todo en la misma base de datos
- Sin dependencias externas
- Ideal para fotos de perfil pequeñas

**Limitaciones críticas:**
- Límite de 16 MB por documento MongoDB (límite BSON)
- Base64 infla el tamaño ~33% (un archivo de 5 MB ocupa ~6.7 MB en DB)
- Videos son imposibles (típicamente 50-500 MB)
- Los blobs se cargan completos en memoria en cada consulta
- MongoDB no está optimizado para streaming de binarios grandes
- **INCOMPATIBLE con videos y contratos grandes**

**Veredicto:** ✅ Válido solo para fotos de perfil. ❌ Inviable para videos, galerías y documentos.

---

### Opción B: MongoDB GridFS

**Cómo funciona:** GridFS es una especificación MongoDB para almacenar archivos grandes dividiéndolos en chunks (255 KB por defecto). Spring Data MongoDB incluye `GridFsTemplate`.

**Ventajas:**
- Sin dependencia externa — usa la misma instancia MongoDB
- Sin límite de tamaño de archivo (maneja videos)
- Streaming nativo (no carga el archivo completo en memoria)
- Integración nativa con Spring

**Limitaciones:**
- JHipster no genera nada automáticamente — implementación 100% manual
- Requiere endpoints REST personalizados (`/api/multimedia/upload`, `/api/multimedia/{id}`)
- Requiere capa de servicio nueva: `GridFsService`
- No separable del ciclo de vida de MongoDB (backup/restore incluye los archivos)
- Performance menor que MinIO para alto volumen
- Debugging más complejo

**Veredicto:** ✅ Viable para un monolito sin infraestructura adicional. ⚠️ Complejidad de implementación media-alta.

---

### Opción C: MinIO (Object Storage S3-compatible)

**Cómo funciona:** Servidor de almacenamiento de objetos independiente, compatible con la API de Amazon S3. El backend genera URLs firmadas para acceso seguro.

**Ventajas:**
- Diseñado específicamente para almacenamiento de archivos
- Streaming nativo eficiente
- URLs presignadas con expiración (seguridad para documentos privados)
- Separación de concerns: base de datos vs archivos
- Escala horizontalmente
- Excelente para videos, galerías, contratos PDF
- Mismo patrón que producción (S3/GCS/Azure Blob)

**Limitaciones:**
- Requiere un servicio adicional (Docker container en dev)
- Requiere `docker-compose.yml` con servicio `minio`
- Implementación manual de endpoints y servicio
- Añade complejidad operacional

**Veredicto:** ✅ La opción más correcta arquitectónicamente. ✅ Lista para producción. ⚠️ Requiere Docker en dev.

---

## 4. Recomendación: Enfoque Híbrido por Tipo

| Tipo de archivo | Solución recomendada | Razón |
|---|---|---|
| **Foto de perfil** | JHipster ImageBlob en PerfilUsuario | Pequeña, JHipster la genera completa, sin infraestructura |
| **Fotos de inmueble** | GridFS o MinIO | Múltiples archivos, acceso público, tamaño moderado |
| **Videos de inmueble** | MinIO | Videos superan el límite BSON |
| **Documentos de usuario** | MinIO | Privados, requieren URLs firmadas |
| **Contratos PDF** | MinIO | Críticos, requieren acceso controlado |

### Para el contexto académico del proyecto:

**Recomendación concreta: GridFS para Fase 1**

GridFS es la opción más adecuada porque:
1. No requiere infraestructura adicional (sin Docker extra)
2. La aplicación ya usa MongoDB — GridFS está incluido
3. Soporta todos los tipos de archivo incluyendo videos y PDFs
4. La implementación es completamente personalizada (demuestra más que una integración de terceros)
5. Suficiente para el volumen de un proyecto académico

MinIO sería la elección en producción real, pero agrega complejidad operacional innecesaria para este contexto.

---

## 5. Plan de Implementación GridFS (para Fase 3 — APIs)

> **NOTA:** Esta implementación va en Fase 3 (APIs), no en Fase 1 (Infrastructure).  
> Fase 1 solo crea entidades Java. Los endpoints de upload se crean en Fase 3.

### Cambios de dominio necesarios

**PerfilUsuario** — añadir foto de perfil:
```java
// En JDL:
entity PerfilUsuario {
  ...
  fotoPerfil    AnyBlob   // JHipster genera upload form automáticamente
  ...
}
```
> Alternativa: guardar GridFS fileId como String en PerfilUsuario. Requiere más código pero más flexible.

**MultimediaInmueble** — añadir soporte para archivos internos:
```java
// Añadir campo opcional para ID de GridFS (nullable):
@Field("gridfs_id")
private String gridfsId;   // null si es URL externa, presente si es archivo interno
```
El campo `urlMedia` existente se mantiene para retrocompatibilidad.

**DocumentoUsuario** — mismo patrón:
```java
@Field("gridfs_id")
private String gridfsId;   // ID en GridFS cuando el archivo está almacenado internamente
```

### Nuevos componentes necesarios (Fase 3)

```
service/
  GridFsService.java           — upload, download, delete via GridFsTemplate
web/rest/
  FileUploadResource.java      — POST /api/files/upload, GET /api/files/{id}
                                  DELETE /api/files/{id}
```

### Impacto en entidades existentes

| Entidad | Campo nuevo | Campo existente | Migración |
|---|---|---|---|
| PerfilUsuario | `fotoPerfil` (AnyBlob o gridfsId) | ninguno | Nullable, sin migración |
| MultimediaInmueble | `gridfsId` (String, nullable) | `urlMedia` (se mantiene) | Sin migración |
| DocumentoUsuario | `gridfsId` (String, nullable) | `urlArchivo` (se mantiene) | Sin migración |
| ContratoArriendo | `gridfsId` (String, nullable) | `urlContratoDigital` (se mantiene) | Sin migración |

**MongoDB es schemaless: todos los campos nuevos son nullable y no requieren migración de datos.**

---

## 6. Decisión para Implementación

- **Fase 1 (actual):** No se modifican entidades multimedia. Se crean solo las 4 entidades nuevas (Notificacion, AcuerdoConvivencia, OcupacionUnidad, HistorialPrecio).
- **Fase 3:** Se implementa GridFS con endpoints de upload para fotos de perfil, fotos de inmueble y documentos.
- **Seeds:** Continúan usando URLs externas (Pexels) hasta que Fase 3 esté completa.

---

*Documento generado como prerequisito al inicio de Fase 1 de implementación.*
