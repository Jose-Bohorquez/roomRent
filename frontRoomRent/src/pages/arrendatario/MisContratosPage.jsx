import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { contratoApi } from "../../services/api";

const ESTADO_MAP = {
  BORRADOR:        { label: "Borrador",         cls: "bg-stone-100 text-stone-600 dark:bg-zinc-700 dark:text-zinc-400" },
  PENDIENTE_FIRMA: { label: "Pendiente firma",  cls: "bg-amber-100 text-amber-800 dark:bg-amber-900/30 dark:text-amber-400" },
  VIGENTE:         { label: "Vigente",           cls: "bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400" },
  FINALIZADO:      { label: "Finalizado",        cls: "bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400" },
  CANCELADO:       { label: "Cancelado",         cls: "bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400" },
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
        <div className="h-4 bg-stone-200 dark:bg-zinc-700 rounded w-1/3" />
        <div className="h-5 bg-stone-200 dark:bg-zinc-700 rounded w-24" />
      </div>
      <div className="h-3 bg-stone-200 dark:bg-zinc-700 rounded w-2/3" />
      <div className="h-3 bg-stone-200 dark:bg-zinc-700 rounded w-1/2" />
    </div>
  );
}

function fmt(dateStr) {
  if (!dateStr) return "—";
  return new Date(dateStr).toLocaleDateString("es-CO", { day: "2-digit", month: "short", year: "numeric" });
}

export default function MisContratosPage() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [contratos, setContratos] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    contratoApi
      .getAll("size=100&sort=createdDate,desc")
      .then(({ data }) => {
        const mine = data.filter(c => c.createdBy === user?.login);
        setContratos(mine);
      })
      .catch(() => setContratos([]))
      .finally(() => setLoading(false));
  }, [user]);

  const active = contratos.filter(c => c.estado === "VIGENTE").length;
  const pending = contratos.filter(c => c.estado === "PENDIENTE_FIRMA").length;

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
            <span className="text-sm font-bold text-stone-800 dark:text-white">Mis Contratos</span>
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

        {/* Resumen rápido */}
        {!loading && contratos.length > 0 && (
          <div className="grid grid-cols-3 gap-4 mb-8">
            {[
              { label: "Total",            value: contratos.length, color: "text-stone-700 dark:text-zinc-300" },
              { label: "Vigentes",         value: active,           color: "text-green-700 dark:text-green-400" },
              { label: "Pend. de firma",   value: pending,          color: "text-amber-700 dark:text-amber-400" },
            ].map(stat => (
              <div key={stat.label} className="card p-4 text-center">
                <p className={`text-2xl font-black ${stat.color}`}>{stat.value}</p>
                <p className="text-xs text-stone-500 dark:text-zinc-400 mt-0.5">{stat.label}</p>
              </div>
            ))}
          </div>
        )}

        {/* Lista */}
        {loading ? (
          <div className="space-y-4">
            {[1, 2, 3].map(i => <Skeleton key={i} />)}
          </div>
        ) : contratos.length === 0 ? (
          <div className="text-center py-24 flex flex-col items-center gap-4">
            <span className="text-6xl">📄</span>
            <p className="text-base font-semibold text-stone-700 dark:text-zinc-300">
              Sin contratos por el momento
            </p>
            <p className="text-sm text-stone-500 dark:text-zinc-400 max-w-xs text-center">
              Cuando un arrendador apruebe tu solicitud y firme el contrato,
              aparecerá aquí con todos los detalles.
            </p>
            <button onClick={() => navigate("/properties")} className="btn-primary mt-2">
              Ver propiedades disponibles
            </button>
          </div>
        ) : (
          <div className="space-y-4">
            {contratos.map(c => (
              <article key={c.id} className="card p-5 flex flex-col gap-3">
                <div className="flex items-start justify-between gap-3">
                  <div>
                    <p className="text-xs text-stone-400 dark:text-zinc-500 font-mono">
                      #{c.numeroContrato}
                    </p>
                    <h3 className="text-sm font-bold text-stone-900 dark:text-white mt-0.5">
                      {c.inmueble?.nombre ?? "Inmueble"}
                    </h3>
                    {c.inmueble?.ciudad && (
                      <p className="text-xs text-stone-500 dark:text-zinc-400">
                        {[c.inmueble.barrio, c.inmueble.ciudad].filter(Boolean).join(", ")}
                      </p>
                    )}
                  </div>
                  <EstadoBadge estado={c.estado} />
                </div>

                <div className="grid grid-cols-2 sm:grid-cols-3 gap-3 text-xs">
                  <div>
                    <p className="text-stone-400 dark:text-zinc-500">Inicio</p>
                    <p className="font-semibold text-stone-800 dark:text-zinc-200">{fmt(c.fechaInicio)}</p>
                  </div>
                  <div>
                    <p className="text-stone-400 dark:text-zinc-500">Vencimiento</p>
                    <p className="font-semibold text-stone-800 dark:text-zinc-200">{fmt(c.fechaFin)}</p>
                  </div>
                  <div>
                    <p className="text-stone-400 dark:text-zinc-500">Canon mensual</p>
                    <p className="font-extrabold text-brand-700 dark:text-brand-400 text-sm">
                      ${Number(c.valorMensual ?? 0).toLocaleString("es-CO")}
                    </p>
                  </div>
                </div>

                {c.urlContratoDigital && (
                  <a
                    href={c.urlContratoDigital}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="text-xs text-brand-700 dark:text-brand-400 underline self-start"
                  >
                    Ver contrato digital
                  </a>
                )}
              </article>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}
