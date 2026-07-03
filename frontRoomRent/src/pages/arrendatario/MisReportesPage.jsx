import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { contratoApi, solicitudArriendoApi, visitaApi } from "../../services/api";

function StatCard({ icon, label, value, sub, color = "text-stone-800 dark:text-zinc-200" }) {
  return (
    <div className="card p-5 flex flex-col gap-2">
      <span className="text-2xl">{icon}</span>
      <p className={`text-3xl font-black ${color}`}>{value ?? "—"}</p>
      <div>
        <p className="text-sm font-semibold text-stone-700 dark:text-zinc-300">{label}</p>
        {sub && <p className="text-xs text-stone-400 dark:text-zinc-500 mt-0.5">{sub}</p>}
      </div>
    </div>
  );
}

function Skeleton() {
  return (
    <div className="card p-5 animate-pulse space-y-3">
      <div className="h-6 w-8 bg-stone-200 dark:bg-zinc-700 rounded" />
      <div className="h-8 w-12 bg-stone-200 dark:bg-zinc-700 rounded" />
      <div className="h-4 w-2/3 bg-stone-200 dark:bg-zinc-700 rounded" />
    </div>
  );
}

function fmt(dateStr) {
  if (!dateStr) return null;
  return new Date(dateStr).toLocaleDateString("es-CO", { day: "2-digit", month: "long", year: "numeric" });
}

function daysLeft(dateStr) {
  if (!dateStr) return null;
  const diff = new Date(dateStr) - new Date();
  const days = Math.ceil(diff / 86_400_000);
  if (days < 0) return "Vencido";
  if (days === 0) return "Hoy";
  return `${days} días`;
}

export default function MisReportesPage() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const login = user?.login;

    Promise.allSettled([
      contratoApi.getAll("size=100"),
      solicitudArriendoApi.getAll("size=100"),
      visitaApi.getAll("size=100"),
    ]).then(([cRes, sRes, vRes]) => {
      const contratos   = cRes.status === "fulfilled" ? cRes.value.data.filter(c => c.createdBy === login) : [];
      const solicitudes = sRes.status === "fulfilled" ? sRes.value.data.filter(s => s.createdBy === login) : [];
      const visitas     = vRes.status === "fulfilled" ? vRes.value.data.filter(v => v.createdBy === login) : [];

      const vigente = contratos.find(c => c.estado === "VIGENTE");
      const proximaVisita = visitas
        .filter(v => v.estado === "CONFIRMADA" && v.fechaSolicitada)
        .sort((a, b) => new Date(a.fechaSolicitada) - new Date(b.fechaSolicitada))[0];

      setStats({
        totalSolicitudes:   solicitudes.length,
        solicitudesActivas: solicitudes.filter(s => ["CREADA", "EN_REVISION"].includes(s.estado)).length,
        solicitudesAprobadas: solicitudes.filter(s => s.estado === "APROBADA").length,
        contratosVigentes:  contratos.filter(c => c.estado === "VIGENTE").length,
        contratoFin:        vigente?.fechaFin ?? null,
        canonActual:        vigente?.valorMensual ?? null,
        totalVisitas:       visitas.length,
        proximaVisita:      proximaVisita?.fechaSolicitada ?? null,
        visitasPendientes:  visitas.filter(v => v.estado === "SOLICITADA").length,
      });
    }).finally(() => setLoading(false));
  }, [user]);

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
            <span className="text-sm font-bold text-stone-800 dark:text-white">Mis Reportes</span>
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

        <div className="mb-6">
          <p className="section-label mb-1">Resumen</p>
          <h2 className="text-xl font-bold text-stone-900 dark:text-white">
            Tu actividad en RoomRent
          </h2>
        </div>

        {loading ? (
          <div className="grid grid-cols-2 sm:grid-cols-3 gap-4">
            {[1, 2, 3, 4, 5, 6].map(i => <Skeleton key={i} />)}
          </div>
        ) : !stats || (stats.totalSolicitudes === 0 && stats.contratosVigentes === 0 && stats.totalVisitas === 0) ? (
          <div className="text-center py-24 flex flex-col items-center gap-4">
            <span className="text-6xl">📊</span>
            <p className="text-base font-semibold text-stone-700 dark:text-zinc-300">
              Aún no hay actividad registrada
            </p>
            <p className="text-sm text-stone-500 dark:text-zinc-400 max-w-xs text-center">
              Aquí verás métricas de tus solicitudes, visitas y contratos
              una vez que empieces a usar la plataforma.
            </p>
            <button onClick={() => navigate("/properties")} className="btn-primary mt-2">
              Explorar propiedades
            </button>
          </div>
        ) : (
          <>
            {/* Sección solicitudes */}
            <div className="mb-4">
              <p className="text-xs font-semibold text-stone-400 dark:text-zinc-500 uppercase tracking-widest mb-3">
                Solicitudes de arriendo
              </p>
              <div className="grid grid-cols-2 sm:grid-cols-3 gap-4">
                <StatCard icon="📨" label="Enviadas" value={stats.totalSolicitudes}
                          sub="histórico total" />
                <StatCard icon="🔍" label="En proceso" value={stats.solicitudesActivas}
                          sub="esperando respuesta"
                          color={stats.solicitudesActivas > 0 ? "text-amber-600 dark:text-amber-400" : undefined} />
                <StatCard icon="✅" label="Aprobadas" value={stats.solicitudesAprobadas}
                          sub="por arrendadores"
                          color={stats.solicitudesAprobadas > 0 ? "text-green-700 dark:text-green-400" : undefined} />
              </div>
            </div>

            {/* Sección contrato activo */}
            <div className="mb-4">
              <p className="text-xs font-semibold text-stone-400 dark:text-zinc-500 uppercase tracking-widest mb-3">
                Contrato actual
              </p>
              <div className="grid grid-cols-2 sm:grid-cols-3 gap-4">
                <StatCard icon="📄" label="Contratos vigentes" value={stats.contratosVigentes}
                          color={stats.contratosVigentes > 0 ? "text-green-700 dark:text-green-400" : undefined} />
                <StatCard icon="📅" label="Vence en"
                          value={stats.contratoFin ? daysLeft(stats.contratoFin) : "—"}
                          sub={stats.contratoFin ? fmt(stats.contratoFin) : "sin contrato vigente"} />
                <StatCard icon="💵" label="Canon mensual"
                          value={stats.canonActual ? `$${Number(stats.canonActual).toLocaleString("es-CO")}` : "—"}
                          sub="COP / mes" />
              </div>
            </div>

            {/* Sección visitas */}
            <div>
              <p className="text-xs font-semibold text-stone-400 dark:text-zinc-500 uppercase tracking-widest mb-3">
                Visitas
              </p>
              <div className="grid grid-cols-2 sm:grid-cols-3 gap-4">
                <StatCard icon="🏠" label="Visitas totales" value={stats.totalVisitas}
                          sub="realizadas o agendadas" />
                <StatCard icon="⏳" label="Pendientes conf." value={stats.visitasPendientes}
                          sub="esperando al arrendador"
                          color={stats.visitasPendientes > 0 ? "text-amber-600 dark:text-amber-400" : undefined} />
                <StatCard icon="📆" label="Próxima visita"
                          value={stats.proximaVisita ? fmt(stats.proximaVisita) : "—"}
                          sub={stats.proximaVisita ? "confirmada" : "ninguna agendada"} />
              </div>
            </div>
          </>
        )}
      </main>
    </div>
  );
}
