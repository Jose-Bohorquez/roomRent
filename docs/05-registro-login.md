# 05 — Registro, autenticación y gestión de cuenta

## Estado actual de implementación

El sistema utiliza el módulo de autenticación estándar de JHipster 9 con JWT. El flujo de registro incluye activación por correo electrónico (implementado por JHipster vía `MailService`). Existe un formulario de recuperación de contraseña y cambio de contraseña.

---

## 1. Registro de nueva cuenta

```mermaid
flowchart TD
    A([Usuario en el portal]) --> B[Clic en Crear cuenta]
    B --> C[Formulario de registro:\nusuario, email, contraseña]
    C --> D{Validación del\nformulario}
    D -->|Campos inválidos| C
    D -->|Válido| E{¿Email o usuario\nya en uso?}
    E -->|Email en uso| ERR1[Error: El email ya existe\nSugerencia: recuperar contraseña]
    E -->|Usuario en uso| ERR2[Error: El nombre de usuario\nya está tomado]
    E -->|Disponible| F[Crear cuenta\nestado: INACTIVO]
    F --> G[Enviar correo\nde activación]
    G --> H[Pantalla: Revisa tu correo]
    H --> I{Usuario hace clic\nen el enlace}
    I -->|Enlace válido| J[Activar cuenta\nestado: ACTIVO]
    I -->|Enlace expirado| K[Pantalla: Enlace expirado\nReenviar correo]
    K --> G
    J --> L[Redirigir al login]
    ERR1 --> C
    ERR2 --> C
```

### Campos del formulario de registro

| Campo | Requerido | Validación |
|---|---|---|
| Nombre de usuario | Sí | Único, 1-50 chars, sin espacios |
| Email | Sí | Único, formato email válido |
| Contraseña | Sí | Mínimo 4 caracteres |
| Confirmar contraseña | Sí | Debe coincidir con contraseña |

### Notas importantes

- La cuenta recién creada tiene el rol `ROLE_USER` por defecto.
- El perfil extendido (`PerfilUsuario`) aún no existe en este punto.
- El campo `activated` en `User` pasa de `false` a `true` al hacer clic en el enlace.

> **Pendiente de validación:** ¿El sistema debe solicitar el `PerfilUsuario` inmediatamente después del primer login, o puede el usuario navegar sin completarlo?

---

## 2. Activación de cuenta por correo

```mermaid
sequenceDiagram
    participant U as Usuario
    participant SYS as Sistema
    participant MAIL as Servidor de correo

    U->>SYS: Completa formulario de registro
    SYS->>SYS: Valida datos únicos
    SYS->>SYS: Crea User (activated=false, activationKey=UUID)
    SYS->>MAIL: Envía correo con enlace de activación
    MAIL->>U: Correo recibido

    U->>SYS: Clic en enlace: /activate?key=UUID
    SYS->>SYS: Busca User por activationKey
    alt Key válida y no expirada
        SYS->>SYS: activated=true, activationKey=null
        SYS->>U: Cuenta activada → redirigir a login
    else Key expirada o inválida
        SYS->>U: Error: enlace inválido
        U->>SYS: Solicita reenvío de correo
        SYS->>MAIL: Nuevo correo con nueva key
    end
```

---

## 3. Inicio de sesión

```mermaid
flowchart TD
    A([Usuario en /login]) --> B[Ingresa usuario\ny contraseña]
    B --> C{Validar\ncredenciales}
    C -->|Incorrectas| ERR1[Error: credenciales inválidas\nFormulario se limpia]
    ERR1 --> B
    C -->|Cuenta inactiva| ERR2[Error: Activa tu cuenta\nenlace para reenviar correo]
    C -->|Cuenta bloqueada| ERR3[Error: Tu cuenta fue bloqueada\nContacta soporte]
    C -->|Cuenta baneada| ERR4[Error: Acceso permanentemente revocado]
    C -->|Válidas| D[Generar JWT token]
    D --> E[Almacenar token en localStorage\nclave: jhi-authenticationToken]
    E --> F{¿Tiene rol ADMIN?}
    F -->|Sí| G[Redirigir al Dashboard\npanel de administración]
    F -->|No| H{¿Tiene PerfilUsuario\ncompleto?}
    H -->|Sí| G
    H -->|No| I[Redirigir a completar perfil]
```

### Estructura del JWT

El token JWT contiene:
- `sub`: nombre de usuario
- `auth`: roles (`ROLE_ADMIN`, `ROLE_USER`)
- `exp`: timestamp de expiración

El token se almacena en `localStorage` bajo la clave `jhi-authenticationToken` (con o sin comillas JSON dependiendo de la opción "Recordarme").

---

## 4. Recuperación de contraseña

```mermaid
flowchart TD
    A([Clic en ¿Olvidaste tu contraseña?]) --> B[Formulario: ingresa tu email]
    B --> C{¿Email existe\nen el sistema?}
    C -->|No existe| D[Respuesta genérica:\nSi el email existe recibirás instrucciones]
    C -->|Existe| E[Generar resetKey con vigencia de 24h]
    E --> F[Enviar correo con enlace\n/reset/finish?key=RESET_KEY]
    F --> D
    D --> G[Usuario revisa su correo]
    G --> H[Clic en enlace de recuperación]
    H --> I{¿Key válida\ny no expirada?}
    I -->|Inválida o expirada| ERR[Error: enlace expirado\nSolicitar nuevo]
    ERR --> B
    I -->|Válida| J[Formulario: nueva contraseña\nconfirmar contraseña]
    J --> K{Validar\ncontraseñas}
    K -->|No coinciden o muy corta| J
    K -->|Válidas| L[Actualizar contraseña\nresetKey = null]
    L --> M[Redirigir al login\ncon mensaje de éxito]
```

> **Nota de seguridad:** El sistema siempre responde con el mismo mensaje ("si el email existe...") para no revelar si un email está registrado.

---

## 5. Cambio de contraseña (autenticado)

```mermaid
flowchart TD
    A([Usuario autenticado → Configuración de cuenta]) --> B[Formulario:\nContraseña actual\nNueva contraseña\nConfirmar nueva]
    B --> C{Validar\ncontraseña actual}
    C -->|Incorrecta| ERR[Error: La contraseña actual no es correcta]
    ERR --> B
    C -->|Correcta| D{¿Nueva contraseña\nválida y coincide?}
    D -->|No| B
    D -->|Sí| E[Actualizar contraseña]
    E --> F[Mensaje de éxito]
```

---

## 6. Cerrar sesión

```mermaid
flowchart LR
    A([Usuario hace clic en Cerrar sesión]) --> B[Eliminar token del localStorage]
    B --> C[Limpiar estado de sesión en Angular]
    C --> D[Redirigir al portal público /]
```

El cierre de sesión es puramente del lado del cliente (eliminar el JWT del `localStorage`). El token JWT no se invalida en el servidor — expirará naturalmente. Si se requiere invalidación inmediata, se necesitaría una lista de tokens revocados en el servidor.

> **Pendiente de validación:** ¿Se requiere invalidación del token en el servidor al hacer logout? ¿Se manejarán sesiones concurrentes desde múltiples dispositivos?

---

## 7. Resumen de endpoints de autenticación (API REST)

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/authenticate` | Login, devuelve JWT |
| `POST` | `/api/register` | Registro de nueva cuenta |
| `GET` | `/api/activate?key=` | Activación de cuenta |
| `POST` | `/api/account/reset-password/init` | Solicitar reset de contraseña |
| `POST` | `/api/account/reset-password/finish` | Finalizar reset con nueva clave |
| `POST` | `/api/account/change-password` | Cambiar contraseña (autenticado) |
| `GET` | `/api/account` | Obtener datos de la cuenta actual |
| `POST` | `/api/account` | Actualizar datos de la cuenta |
