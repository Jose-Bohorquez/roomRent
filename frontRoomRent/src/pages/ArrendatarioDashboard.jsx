import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import DashCard from "../components/DashCard";

const cards = [
  { title: "Buscar propiedades", desc: "Explora todas las propiedades disponibles para arrendar.",  href: "/properties",                           icon: "🔍", internal: true },
  { title: "Mis contratos",      desc: "Revisa el estado de tus contratos de arriendo.",            href: "http://localhost:8080/contrato-arriendo", icon: "📄" },
  { title: "Mis pagos",          desc: "Consulta el historial de pagos realizados.",                href: "http://localhost:8080/pago",              icon: "💳" },
  { title: "Reportes",           desc: "Genera reportes sobre el estado de los inmuebles.",         href: "http://localhost:8080/reporte",           icon: "📊" },
];

export default function ArrendatarioDashboard() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const handleLogout = () => { logout(); navigate("/"); };

  return (
    <div className="min-h-screen bg-surface-light dark:bg-surface-dark">

      <header className="sticky top-0 z-30 bg-white/95 dark:bg-zinc-900/95 backdrop-blur-sm
                         border-b border-stone-200 dark:border-zinc-800 shadow-sm">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <span className="text-lg">🏡</span>
            <span className="text-sm font-bold text-stone-800 dark:text-white">Panel Arrendatario</span>
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
          <p className="section-label mb-1">Tu espacio</p>
          <h2 className="section-title">Bienvenido de nuevo</h2>
          <p className="text-stone-500 dark:text-zinc-400 mt-2 text-sm">
            Encuentra tu próximo hogar o gestiona tus arriendos activos.
          </p>
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
          {cards.map((card) => <DashCard key={card.title} {...card} />)}
        </div>
      </main>
    </div>
  );
}
