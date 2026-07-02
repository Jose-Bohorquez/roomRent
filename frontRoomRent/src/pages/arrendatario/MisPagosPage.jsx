import { useAuth } from "../../context/AuthContext";
import { useNavigate } from "react-router-dom";

export default function MisPagosPage() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-surface-light dark:bg-surface-dark">
      <header className="sticky top-0 z-30 bg-white/95 dark:bg-zinc-900/95 backdrop-blur-sm
                         border-b border-stone-200 dark:border-zinc-800 shadow-sm">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <button
              onClick={() => navigate("/arrendatario")}
              className="text-stone-500 hover:text-stone-800 dark:text-zinc-400 dark:hover:text-white transition-colors"
              aria-label="Volver"
            >
              ←
            </button>
            <span className="text-sm font-bold text-stone-800 dark:text-white">Mis Pagos</span>
          </div>
          <div className="flex items-center gap-3">
            <span className="text-xs text-stone-500 dark:text-zinc-400 hidden sm:block">
              {user?.firstName || user?.login}
            </span>
            <button onClick={() => { logout(); navigate("/"); }} className="btn-danger">Salir</button>
          </div>
        </div>
      </header>

      <main className="max-w-6xl mx-auto px-4 sm:px-6 py-16">
        <div className="text-center py-20">
          <div className="text-5xl mb-4">💳</div>
          <h2 className="text-xl font-bold text-stone-800 dark:text-white mb-2">
            Mis Pagos
          </h2>
          <p className="text-stone-500 dark:text-zinc-400 text-sm max-w-sm mx-auto">
            Aquí verás el historial de pagos de tus arriendos activos.
            Esta funcionalidad estará disponible próximamente.
          </p>
          <button
            onClick={() => navigate("/arrendatario")}
            className="btn-primary mt-8"
          >
            Volver al panel
          </button>
        </div>
      </main>
    </div>
  );
}
