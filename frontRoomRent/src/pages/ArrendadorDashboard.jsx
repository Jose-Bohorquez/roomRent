import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import DashCard from "../components/DashCard";

const cards = [
  { title: "Mis inmuebles",    desc: "Gestiona y edita tus propiedades publicadas.",               href: "/mis-inmuebles",  icon: "🏠", internal: true },
  { title: "Publicar nuevo",   desc: "Crea una nueva publicación con fotos y precio.",             href: "/crear-inmueble", icon: "➕", internal: true },
  { title: "Ver propiedades",  desc: "Explora todas las propiedades disponibles en la plataforma.",href: "/properties",     icon: "🔍", internal: true },
  { title: "Panel Admin",      desc: "Acceso a la administración completa del sistema.",           href: "/inmueble",       icon: "⚙️" },
];

export default function ArrendadorDashboard() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const handleLogout = () => { logout(); navigate("/"); };

  return (
    <div className="min-h-screen bg-surface-light dark:bg-surface-dark">

      <header className="sticky top-0 z-30 bg-white/95 dark:bg-zinc-900/95 backdrop-blur-sm
                         border-b border-stone-200 dark:border-zinc-800 shadow-sm">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <span className="text-lg">🏠</span>
            <span className="text-sm font-bold text-stone-800 dark:text-white">Panel Arrendador</span>
          </div>
          <div className="flex items-center gap-3">
            <span className="text-xs text-stone-500 dark:text-zinc-400 hidden sm:block">
              {user?.firstName || user?.login}
            </span>
            <button onClick={handleLogout} className="btn-danger">Salir</button>
          </div>
        </div>
      </header>

      <main className="max-w-6xl mx-auto px-4 sm:px-6 py-12">
        <div className="mb-10">
          <p className="section-label mb-1">Gestión</p>
          <h2 className="section-title">¿Qué quieres hacer hoy?</h2>
          <p className="text-stone-500 dark:text-zinc-400 mt-2 text-sm">
            Gestiona tus propiedades y publicaciones desde aquí.
          </p>
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
          {cards.map((card) => <DashCard key={card.title} {...card} />)}
        </div>
      </main>
    </div>
  );
}
