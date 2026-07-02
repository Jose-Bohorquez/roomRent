RoomRent: Plataforma de Gestión de Arriendos

Plataforma de Gestión de Arriendos desarrollada en React + Vite, diseñada para administrar propiedades, reservas, pagos y comunicación entre inquilinos y propietarios.

---

Tecnologías principales
* React 18
* Vite
* JavaScript / JSX
* React Router DOM
* Axios
* TailwindCSS (si aplica)
* ESLint configurado por Vite
* API REST externa (backend independiente)

---

Requisitos previos
* Node.js 18 o superior
* npm o yarn instalado

---

Clonar el repositorio

git clone https://github.com/tu-usuario/roomrent.git
cd roomrent

Instalación de dependencias

npm install
npm install @tailwindcss/vite --save-dev
npm install react-router-dom

Ejecutar en modo desarrollo

npm run dev

La aplicación estará disponible en:
http://localhost:5173

---

Crear build para producción

npm run build

La carpeta final será:
dist/

Previsualizar la build

npm run preview

---

Despliegue (Hostings recomendados)

RoomRent puede desplegarse fácilmente en:
* Vercel
* Netlify
* Render
* AWS S3 + CloudFront
* Hostinger VPS
* Google Cloud Platform
* GitHub Pages

Configuración estándar:
Build command: npm run build
Output directory: dist

---

Estructura de carpetas sugerida

roomrent/
├── public/
├── src/
│   ├── assets/
│   ├── components/
│   ├── hooks/
│   ├── pages/
│   ├── services/
│   ├── utils/
│   ├── App.jsx
│   └── main.jsx
├── package.json
├── vite.config.js
└── .eslintrc.cjs

---

Plugins oficiales de React para Vite
* @vitejs/plugin-react: Utiliza Babel u oxc para Fast Refresh.
* @vitejs/plugin-react-swc: Utiliza SWC, con mejor rendimiento en desarrollo.

---

React Compiler

Este template no habilita React Compiler por el impacto en rendimiento.

Si deseas activarlo, consulta la documentación oficial:
https://react.dev/learn/react-compiler

---

ESLint recomendado

Para producción se recomienda usar:
* TypeScript
* Reglas type-aware de ESLint

Puedes revisar la plantilla oficial Vite + TypeScript para una futura migración.

---

Sobre el proyecto RoomRent

RoomRent permite gestionar digitalmente procesos relacionados con arriendos:
* Gestión de habitaciones y propiedades
* Perfiles de usuario (inquilino / propietario)
* Calendarios de disponibilidad
* Sistema de reservas
* Pagos y facturación
* Panel administrativo
* Notificaciones (en desarrollo)
* Chat interno (planificado)

Objetivo principal: automatizar y centralizar la administración de arriendos.

---

Contribuciones

git checkout -b feature/nueva-funcionalidad
git commit -m "Descripcion clara"
git push origin feature/nueva-funcionalidad

Luego crea un Pull Request.

---

Licencia

Proyecto privado.

Todos los derechos reservados.
