import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { solicitudArriendoApi } from "../../services/api";

const ESTADO_MAP = {
  CREADA:      { label: "Enviada",     cls: "bg-stone-100 text-stone-600 dark:bg-zinc-700 dark:text-zinc-300",   icon: "📨" },
  EN_REVISION: { label: "En revisión", cls: "bg-amber-100 text-amber-800 dark:bg-amber-900/30 dark:text-amber-400", icon: "🔍" },
  APROBADA:    { label: "Aprobada",    cls: "bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400",  icon: "✅" },
  RECHAZADA:   { label: "Rechazada",   cls: "bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400",     icon: "❌" },
  CANCELADA:   { label: "Cancelada",   cls: "bg-stone-100 text-stone-400 dark:bg-zinc-700 dark:text-zinc-500",   icon: "🚫" },
};

function EstadoBadge({ estado }) {
  const { label, cls } = ESTADO_MAP[estado] ?? { label: estado, cls: "bg-stone-100 text-stone-600" };
  return (
    <span className={`text-xs font-semibold px-2 py-0.5 rounded-md ${cls}`}>
      {label}
    </span>
  );
}

function Skeleton() {
  return (
    <div className="card p-5 animate-pulse space-y-3">
      <div className="flex justify-between">
        <div className="h-4 bg-stone-200 dark:bg-zinc-700 rounded w-1/2" />
        <div className="h-5 bg-stone-200 dark:bg-zinc-700 rounded w-20" />
      </div>
      <div className="h-3 bg-stone-200 dark:bg-zinc-700 rounded w-1/3" />
    </div>
  );
}

function fmt(dateStr) {
  if (!dateStr) return "—";
  return new Date(dateStr).toLocaleDateString("es-CO", { day: "2-digit", month: "short", year: "numeric" });
}

export default function MisPagosPage() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [solicitudes, setSolicitudes] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    solicitudArriendoApi
      .getAll("size=100&sort=fechaCreacion,desc")
      .then(({ data }) => {
        const mine = data.filter(s => s.createdBy === user?.login);
        setSolicitudes(mine);
      })
      .catch(() => setSolicitudes([]))
      .finally(() => setLoading(false));
  }, [user]);

  const aprobadas = solicitudes.filter(s => s.estado === "APROBADA").length;
  const pendientes = solicitudes.filter(s => ["CREADA", "EN_REVISION"].includes(s.estado)).length;

  return (
    <div className="min-h-screen bg-surface-light dark:bg-surface-dark">

      <header className="sticky top-0 z-30 bg-white/95 dark:bg-zinc-900/95 backdrop-blur-sm
                         border-b border-stone-200 dark:border-zinc-800 shadow-sm">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <button
              onClick={() => navigate("/arrendatario")}
              className="text-xs text-stone-500 hover:text-brand-700 dark:text-zinc-400 dark:hover:text-brand-400 transition-colors"
            >
              ← Panel
            </button>
            <span className="text-stone-300 dark:text-zinc-700">|</span>
            <span className="text-sm font-bold text-stone-800 dark:text-white">Mis Solicitudes</span>
          </div>
          <div className="flex items-center gap-3">
            <span className="text-xs text-stone-500 dark:text-zinc-400 hidden sm:block">
              {user?.firstName || user?.login}
            </span>
            <button onClick={() => { logout(); navigate("/"); }} className="btn-danger text-xs">Salir</button>
          </div>
        </div>
      </header>

      <main className="max-w-4xl mx-auto px-4 sm:px-6 py-8">

        {/* Aviso módulo de pagos */}
        <div className="mb-6 p-4 rounded-xl border border-amber-200 dark:border-amber-800/50
                        bg-amber-50 dark:bg-amber-900/10 flex items-start gap-3">
          <span className="text-xl mt-0.5">💡</span>
          <div>
            <p className="text-sm font-semibold text-amber-800 dark:text-amber-400">
              Módulo de pagos en desarrollo
            </p>
            <p className="text-xs text-amber-700 dark:text-amber-500 mt-0.5">
              El registro de pagos estará disponible próximamente. Por ahora puedes revisar
              el estado de tus solicitudes de arriendo.
            </p>
          </div>
        </div>

        {/* Resumen */}
        {!loading && solicitudes.length > 0 && (
          <div className="grid grid-cols-3 gap-4 mb-8">
            {[
              { label: "Total solicitudes", value: solicitudes.length, color: "text-stone-700 dark:text-zinc-300" },
              { label: "Aprobadas",         value: aprobadas,          color: "text-green-700 dark:text-green-400" },
              { label: "En proceso",        value: pendientes,         color: "text-amber-700 dark:text-amber-400" },
            ].map(stat => (
              <div key={stat.label} className="card p-4 text-center">
                <p className={`text-2xl font-black ${stat.color}`}>{stat.value}</p>
                <p className="text-xs text-stone-500 dark:text-zinc-400 mt-0.5">{stat.label}</p>
              </div>
            ))}
          </div>
        )}

        {/* Lista de solicitudes */}
        {loading ? (
          <div className="space-y-4">
            {[1, 2].map(i => <Skeleton key={i} />)}
          </div>
        ) : solicitudes.length === 0 ? (
          <div className="text-center py-24 flex flex-col items-center gap-4">
            <span className="text-6xl">💳</span>
            <p className="text-base font-semibold text-stone-700 dark:text-zinc-300">
              Aún no tienes solicitudes
            </p>
            <p className="text-sm text-stone-500 dark:text-zinc-400 max-w-xs text-center">
              Cuando envíes una solicitud de arriendo podrás hacer seguimiento
              aquí en tiempo real.
            </p>
            <button onClick={() => navigate("/properties")} className="btn-primary mt-2">
              Explorar propiedades
            </button>
          </div>
        ) : (
          <div className="space-y-4">
            <p className="text-xs font-semibold text-stone-500 dark:text-zinc-400 uppercase tracking-wide mb-2">
              Historial de solicitudes
            </p>
            {solicitudes.map(s => {
              const { icon } = ESTADO_MAP[s.estado] ?? { icon: "📋" };
              return (
                <article key={s.id} className="card p-5 flex flex-col gap-3">
                  <div className="flex items-start justify-between gap-3">
                    <div className="flex items-start gap-3">
                      <span className="text-xl mt-0.5">{icon}</span>
                      <div>
                        <p className="text-sm font-bold text-stone-900 dark:text-white">
                          {s.publicacion?.titulo ?? "Propiedad"}
                        </p>
                        <p className="text-xs text-stone-500 dark:text-zinc-400 mt-0.5">
                          Enviada el {fmt(s.fechaCreacion)}
                        </p>
                      </div>
                    </div>
                    <EstadoBadge estado={s.estado} />
                  </div>

                  {s.mensaje && (
                    <p className="text-xs text-stone-500 dark:text-zinc-400 italic border-l-2
                                  border-stone-200 dark:border-zinc-700 pl-3">
                      "{s.mensaje}"
                    </p>
                  )}
                </article>
              );
            })}
          </div>
        )}
      </main>
    </div>
  );
}
