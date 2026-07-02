# 05 — Progressive Web App (PWA)

**Proyecto:** RoomRent  
**Versión:** RC1  
**Fecha:** 2026-07-02  

---

## 1. Estado

**PWA: FUNCIONAL en producción.**

El portal React (`/portal/`) cumple los criterios de Progressive Web App: manifiesto válido, service worker registrado y activo, íconos de instalación presentes, y modo `standalone` operativo.

---

## 2. Archivos Principales

| Archivo                        | URL de acceso                  | HTTP Status | Notas                                 |
|--------------------------------|--------------------------------|:-----------:|---------------------------------------|
| Manifiesto Web                 | `/portal/manifest.webmanifest` | `200 OK`    | Cache-Control: max-age=86400          |
| Service Worker                 | `/portal/sw.js`                | `200 OK`    | Cache-Control: no-store               |
| Ícono pequeño                  | `/portal/icon-192.png`         | `200 OK`    | PNG 192×192 px                        |
| Ícono grande                   | `/portal/icon-512.png`         | `200 OK`    | PNG 512×512 px                        |

> **Por qué `no-store` en `sw.js`:** El service worker debe ser verificado por el navegador en cada visita para detectar actualizaciones. Si el navegador usa una copia cacheada del SW, las actualizaciones de la aplicación tardarían hasta el TTL del caché en propagarse.

---

## 3. Configuración del Manifiesto

Archivo: `/portal/manifest.webmanifest`

```json
{
  "name": "RoomRent",
  "short_name": "RoomRent",
  "start_url": "/portal/",
  "scope": "/portal/",
  "display": "standalone",
  "background_color": "#1c1917",
  "theme_color": "#f59e0b",
  "lang": "es",
  "icons": [
    {
      "src": "/portal/icon-192.png",
      "sizes": "192x192",
      "type": "image/png",
      "purpose": "any maskable"
    },
    {
      "src": "/portal/icon-512.png",
      "sizes": "512x512",
      "type": "image/png",
      "purpose": "any maskable"
    }
  ]
}
```

**Parámetros clave:**

| Parámetro          | Valor          | Propósito                                                   |
|--------------------|----------------|-------------------------------------------------------------|
| `start_url`        | `/portal/`     | URL que abre la app instalada                               |
| `scope`            | `/portal/`     | Limita el SW a las rutas bajo `/portal/`                    |
| `display`          | `standalone`   | Oculta la barra de dirección del navegador                  |
| `background_color` | `#1c1917`      | Color de fondo durante la pantalla de carga (splash screen) |
| `theme_color`      | `#f59e0b`      | Color ámbar de la barra de estado en Android                |
| `lang`             | `es`           | Idioma principal de la aplicación                           |

---

## 4. Configuración del Service Worker

Archivo: `/portal/sw.js`

**Nombre del caché:** `roomrent-v1`

### 4.1 Estrategia de Precaché

Al instalarse, el SW precachea los recursos esenciales para el funcionamiento offline:

```
/portal/
/portal/index.html
```

### 4.2 Estrategia de Respuesta por Ruta

| Patrón de URL          | Estrategia         | Descripción                                                        |
|------------------------|--------------------|--------------------------------------------------------------------|
| `/portal/**`           | Cache-first        | Sirve desde caché; si no existe, va a la red y guarda la respuesta |
| `/api/**`              | Pass-through (red) | Siempre va a la red; no se cachea (datos dinámicos)                |
| `/management/**`       | Pass-through (red) | Siempre va a la red; endpoints de actuator JHipster                |

**Lógica de Cache-first para `/portal/**`:**

1. El SW intercepta la petición
2. Busca el recurso en `roomrent-v1`
3. Si existe en caché → retorna inmediatamente (sin petición de red)
4. Si no existe → hace fetch a la red → guarda la respuesta en caché → retorna

**Comportamiento offline:** Con caché activo, `/portal/` y sus assets estáticos cargados funcionan sin conexión. Las llamadas a `/api/` fallan (sin red = sin datos), pero la interfaz carga.

---

## 5. Configuración Nginx

Fragmento relevante de `nginx.conf` o del bloque `location` del virtual host:

```nginx
# Service Worker — nunca cachear
location = /portal/sw.js {
    add_header Cache-Control "no-store, no-cache, must-revalidate";
    add_header Pragma "no-cache";
    try_files $uri =404;
}

# Manifiesto — cachear 24 horas
location ~* /portal/.*\.webmanifest$ {
    add_header Cache-Control "public, max-age=86400";
    try_files $uri =404;
}

# Assets estáticos con fingerprint — cachear 1 año (immutable)
location ~* /portal/.*\.(js|css)$ {
    add_header Cache-Control "public, max-age=31536000, immutable";
    try_files $uri =404;
}

# Imágenes y fuentes — cachear 30 días
location ~* /portal/.*\.(png|jpg|jpeg|webp|svg|woff2)$ {
    add_header Cache-Control "public, max-age=2592000";
    try_files $uri =404;
}

# SPA fallback — todo lo de /portal/ sirve index.html
location /portal/ {
    try_files $uri $uri/ /portal/index.html;
}
```

**Resumen de política de caché:**

| Tipo de recurso            | `Cache-Control`                          | TTL         |
|----------------------------|------------------------------------------|-------------|
| `sw.js`                    | `no-store, no-cache, must-revalidate`    | 0 (siempre red) |
| `*.webmanifest`            | `public, max-age=86400`                  | 24 horas    |
| `*.js`, `*.css` (con hash) | `public, max-age=31536000, immutable`    | 1 año       |
| Imágenes, fuentes          | `public, max-age=2592000`                | 30 días     |

---

## 6. Plataformas Probadas

| Plataforma               | Estado      | Versión probada | Notas                                    |
|--------------------------|-------------|-----------------|------------------------------------------|
| Chrome Desktop (Linux)   | Funcional   | 136+            | Install prompt disponible, standalone OK |

---

## 7. Instalación (Install Prompt)

Chrome muestra el banner de instalación cuando se cumplen todos los criterios PWA:

1. Sitio servido por HTTPS (o `localhost` en desarrollo)
2. Manifiesto válido con `name`, `start_url`, íconos de 192 y 512 px
3. Service Worker registrado y activo con un fetch handler
4. El usuario ha interactuado con el sitio al menos 30 segundos

Cuando se cumplen los criterios, Chrome emite el evento `beforeinstallprompt`. El portal captura este evento y muestra un botón "Instalar aplicación" en la barra de navegación. Al hacer clic, se invoca `prompt()` del evento diferido.

**En desarrollo (`localhost`):** El install prompt funciona normalmente. No se requiere HTTPS.

**En producción:** El servidor debe tener un certificado TLS válido. Sin HTTPS el SW no se registra y la PWA no es instalable.

---

## 8. Funcionamiento Offline

Con la aplicación instalada y el caché de `roomrent-v1` populado:

| Recurso                          | Disponible offline | Comportamiento                              |
|----------------------------------|--------------------|---------------------------------------------|
| `/portal/` (shell React)         | Sí                 | Carga desde caché del SW                    |
| `/portal/index.html`             | Sí                 | Precacheado en instalación del SW           |
| Assets JS/CSS                    | Sí                 | Cacheados en primera visita                 |
| `/api/publicacion-inmuebles`     | No                 | Requiere red; muestra error en la UI        |
| `/api/authenticate`              | No                 | Requiere red; login no disponible offline   |

---

## 9. Ciclo de Actualización del Service Worker

Cuando se despliega una nueva versión del portal:

1. El build genera nuevos hashes en los nombres de archivos JS/CSS
2. El `sw.js` actualizado referencia los nuevos archivos en su precaché
3. En la siguiente visita del usuario, Chrome descarga `sw.js` (por `no-store`)
4. Chrome detecta que el contenido de `sw.js` cambió
5. El nuevo SW entra en estado `waiting` (el SW anterior sigue activo)
6. Al cerrar todas las pestañas de la app y reabrir, el nuevo SW toma control
7. El caché `roomrent-v1` del SW anterior se elimina; se crea el nuevo caché

**Sin intervención del usuario:** La actualización es automática y silenciosa tras cerrar y reabrir la aplicación. No se requiere desinstalar la PWA.

---

## 10. Limitaciones Conocidas

| Limitación                              | Detalle                                                                          |
|-----------------------------------------|----------------------------------------------------------------------------------|
| iOS Safari no probado                   | iOS tiene restricciones sobre SW y almacenamiento; comportamiento no verificado  |
| Android nativo (Chrome Android) no probado | No se ha validado el install prompt ni la experiencia standalone en Android  |
| Sin push notifications                  | No implementado en RC1; requiere VAPID keys y backend de notificaciones          |
| Sin background sync                     | Solicitudes fallidas offline no se reintenta automáticamente al recuperar red    |
| Sin periodic background sync           | No se actualiza el contenido en segundo plano mientras la app está cerrada       |
| Sin indexedDB offline cache para datos  | Solo se cachean assets estáticos; los datos de la API no están disponibles offline |
