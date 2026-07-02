import { useState, useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { FaPlus, FaPenToSquare, FaTrash, FaEye } from "react-icons/fa6";
import { propiedadApi, publicacionRawApi } from "../../services/api";

function SkeletonCard() {
  return (
    <div className="card overflow-hidden animate-pulse">
      <div className="h-44 bg-stone-200 dark:bg-zinc-700" />
      <div className="p-4 space-y-2">
        <div className="h-3 bg-stone-200 dark:bg-zinc-700 rounded w-2/3" />
        <div className="h-4 bg-stone-200 dark:bg-zinc-700 rounded w-1/3" />
      </div>
    </div>
  );
}

function EstadoBadge({ estado }) {
  const MAP = {
    PUBLICADA:              { label: "Publicada",     cls: "bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400" },
    BORRADOR:               { label: "Borrador",      cls: "bg-stone-100 text-stone-600 dark:bg-zinc-700 dark:text-zinc-400" },
    ARRENDADA:              { label: "Arrendada",     cls: "bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400" },
    VISITA_AGENDADA:        { label: "Visita agend.", cls: "bg-amber-100 text-amber-800 dark:bg-amber-900/30 dark:text-amber-400" },
    POSTULANTE_SELECCIONADO:{ label: "Postulante",   cls: "bg-indigo-100 text-indigo-800 dark:bg-indigo-900/30 dark:text-indigo-400" },
    RESERVADA:              { label: "Reservada",     cls: "bg-purple-100 text-purple-800 dark:bg-purple-900/30 dark:text-purple-400" },
    FINALIZADA:             { label: "Finalizada",    cls: "bg-stone-100 text-stone-500 dark:bg-zinc-700 dark:text-zinc-500" },
    ARCHIVADA:              { label: "Archivada",     cls: "bg-stone-100 text-stone-400 dark:bg-zinc-700 dark:text-zinc-600" },
  };
  const { label, cls } = MAP[estado] ?? { label: estado, cls: "bg-stone-100 text-stone-600" };
  return (
    <span className={`text-xs font-semibold px-2 py-0.5 rounded-md ${cls}`}>
      {label}
    </span>
  );
}

export default function MisInmueblesPage() {
  const navigate = useNavigate();
  const [params] = useSearchParams();
  const createdId = params.get("created");

  const [inmuebles, setInmuebles] = useState([]);
  const [publicaciones, setPublicaciones] = useState({});
  const [loading, setLoading] = useState(true);
  const [deleteConfirm, setDeleteConfirm] = useState(null);
  const [deletingId, setDeletingId] = useState(null);
  const [toast, setToast] = useState(createdId ? "¡Inmueble publicado correctamente!" : null);

  useEffect(() => {
    if (toast) {
      const t = setTimeout(() => setToast(null), 4000);
      return () => clearTimeout(t);
    }
  }, [toast]);

  useEffect(() => {
    const load = async () => {
      try {
        // Cargar todos los inmuebles (sin filtro por propietario por ahora)
        const { data } = await propiedadApi.getAll("size=50&sort=id,desc");
        setInmuebles(data);

        // Cargar publicaciones para obtener estado y precio
        const { data: pubs } = await publicacionRawApi.getAll("size=100");
        const pubMap = {};
        for (const pub of pubs) {
          const inmId = pub.inmueble?.id;
          if (inmId && !pubMap[inmId]) pubMap[inmId] = pub;
        }
        setPublicaciones(pubMap);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  const handleDelete = async (inmueble) => {
    setDeletingId(inmueble.id);
    try {
      // Eliminar publicación si existe
      const pub = publicaciones[inmueble.id];
      if (pub) await publicacionRawApi.remove(pub.id);
      // Eliminar inmueble
      await propiedadApi.remove(inmueble.id);
      setInmuebles(prev => prev.filter(i => i.id !== inmueble.id));
      setDeleteConfirm(null);
      setToast("Inmueble eliminado.");
    } catch (err) {
      setToast("Error al eliminar: " + err.message);
    } finally {
      setDeletingId(null);
    }
  };

  const getImage = (inm) => {
    const meds = inm.multimedias ?? [];
    if (meds.length === 0) return null;
    const principal = meds.find(m => m.principal) ?? meds[0];
    return principal?.urlMedia ?? null;
  };

  return (
    <div className="min-h-screen bg-surface-light dark:bg-surface-dark">

      {/* Header */}
      <header className="sticky top-0 z-30 bg-white/95 dark:bg-zinc-900/95 backdrop-blur-sm
                         border-b border-stone-200 dark:border-zinc-800 shadow-sm">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <button onClick={() => navigate("/arrendador")}
              className="text-xs text-stone-500 hover:text-brand-700 dark:text-zinc-400
                         dark:hover:text-brand-500 transition-colors">
              ← Panel
            </button>
            <span className="text-stone-300 dark:text-zinc-700">|</span>
            <span className="text-sm font-bold text-stone-800 dark:text-white">Mis Inmuebles</span>
          </div>
          <button
            onClick={() => navigate("/crear-inmueble")}
            className="btn-primary flex items-center gap-2 text-sm"
          >
            <FaPlus className="size-3.5" />
            Publicar inmueble
          </button>
        </div>
      </header>

      {/* Toast */}
      {toast && (
        <div className="fixed bottom-6 left-1/2 -translate-x-1/2 z-50
                        bg-stone-900 text-white text-sm font-medium
                        px-5 py-3 rounded-xl shadow-lg">
          {toast}
        </div>
      )}

      {/* Delete confirm modal */}
      {deleteConfirm && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-stone-950/50">
          <div className="bg-white dark:bg-zinc-900 rounded-2xl shadow-2xl p-8 max-w-sm w-full mx-4">
            <p className="text-base font-bold text-stone-900 dark:text-white mb-2">
              ¿Eliminar inmueble?
            </p>
            <p className="text-sm text-stone-500 dark:text-zinc-400 mb-6">
              Se eliminará <strong>{deleteConfirm.nombre}</strong> y su publicación.
              Esta acción no se puede deshacer.
            </p>
            <div className="flex gap-3 justify-end">
              <button onClick={() => setDeleteConfirm(null)}
                className="btn-secondary" disabled={!!deletingId}>Cancelar</button>
              <button onClick={() => handleDelete(deleteConfirm)}
                className="btn-danger" disabled={!!deletingId}>
                {deletingId ? "Eliminando…" : "Eliminar"}
              </button>
            </div>
          </div>
        </div>
      )}

      <main className="max-w-6xl mx-auto px-4 sm:px-6 py-8">

        {loading ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
            {[1, 2, 3].map(i => <SkeletonCard key={i} />)}
          </div>
        ) : inmuebles.length === 0 ? (
          <div className="text-center py-24 flex flex-col items-center gap-4">
            <p className="text-5xl">🏠</p>
            <p className="text-stone-600 dark:text-zinc-400 text-base font-medium">
              Aún no has publicado ningún inmueble.
            </p>
            <button onClick={() => navigate("/crear-inmueble")} className="btn-primary flex items-center gap-2">
              <FaPlus className="size-3.5" /> Publicar mi primer inmueble
            </button>
          </div>
        ) : (
          <>
            <p className="text-sm text-stone-500 dark:text-zinc-400 mb-6">
              {inmuebles.length} inmueble{inmuebles.length !== 1 ? "s" : ""} registrado{inmuebles.length !== 1 ? "s" : ""}
            </p>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
              {inmuebles.map(inm => {
                const pub = publicaciones[inm.id];
                const img = getImage(inm);
                return (
                  <article key={inm.id} className="card overflow-hidden flex flex-col">
                    {/* Imagen */}
                    <div className="h-44 overflow-hidden relative bg-stone-100 dark:bg-zinc-800">
                      {img ? (
                        <img src={img} alt={inm.nombre} className="w-full h-full object-cover" />
                      ) : (
                        <div className="w-full h-full flex items-center justify-center text-4xl text-stone-300 dark:text-zinc-600">
                          🏠
                        </div>
                      )}
                      {pub && (
                        <div className="absolute top-2.5 left-2.5">
                          <EstadoBadge estado={pub.estado} />
                        </div>
                      )}
                    </div>

                    {/* Info */}
                    <div className="p-4 flex flex-col gap-2 flex-1">
                      <h3 className="font-bold text-stone-900 dark:text-white text-sm line-clamp-1">
                        {inm.nombre}
                      </h3>
                      <p className="text-xs text-stone-500 dark:text-zinc-400 line-clamp-1">
                        {[inm.barrio, inm.localidad, inm.ciudad].filter(Boolean).join(", ")}
                      </p>
                      {pub && (
                        <p className="text-brand-700 dark:text-brand-500 font-extrabold text-lg">
                          ${Number(pub.canonArriendo).toLocaleString()}
                          <span className="text-xs font-normal text-stone-400 dark:text-zinc-500 ml-1">/mes</span>
                        </p>
                      )}
                      <div className="flex flex-wrap gap-2 text-xs text-stone-500 dark:text-zinc-400">
                        {inm.tipoInmueble && (
                          <span className="px-2 py-0.5 bg-stone-100 dark:bg-zinc-700 rounded-md">
                            {inm.tipoInmueble}
                          </span>
                        )}
                        {inm.numeroHabitaciones != null && (
                          <span>{inm.numeroHabitaciones} hab.</span>
                        )}
                        {inm.numeroBanos != null && (
                          <span>{inm.numeroBanos} baños</span>
                        )}
                        {inm.areaMetrosCuadrados && (
                          <span>{inm.areaMetrosCuadrados} m²</span>
                        )}
                      </div>
                    </div>

                    {/* Acciones */}
                    <div className="px-4 pb-4 flex gap-2 border-t border-stone-100 dark:border-zinc-700 pt-3">
                      {pub && (
                        <button
                          onClick={() => navigate(`/properties/${pub.id}`)}
                          className="flex items-center gap-1.5 text-xs font-medium px-3 py-1.5
                                     rounded-md border border-stone-200 dark:border-zinc-600
                                     text-stone-600 dark:text-zinc-300
                                     hover:bg-stone-50 dark:hover:bg-zinc-800 transition-colors"
                        >
                          <FaEye className="size-3" /> Ver
                        </button>
                      )}
                      <button
                        onClick={() => navigate(`/editar-inmueble/${inm.id}`)}
                        className="flex items-center gap-1.5 text-xs font-medium px-3 py-1.5
                                   rounded-md border border-stone-200 dark:border-zinc-600
                                   text-stone-600 dark:text-zinc-300
                                   hover:bg-stone-50 dark:hover:bg-zinc-800 transition-colors"
                      >
                        <FaPenToSquare className="size-3" /> Editar
                      </button>
                      <button
                        onClick={() => setDeleteConfirm(inm)}
                        className="flex items-center gap-1.5 text-xs font-medium px-3 py-1.5
                                   rounded-md border border-red-200 dark:border-red-800
                                   text-red-600 dark:text-red-400
                                   hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors ml-auto"
                      >
                        <FaTrash className="size-3" /> Eliminar
                      </button>
                    </div>
                  </article>
                );
              })}

              {/* CTA crear nuevo */}
              <button
                onClick={() => navigate("/crear-inmueble")}
                className="border-2 border-dashed border-stone-300 dark:border-zinc-600
                           rounded-xl flex flex-col items-center justify-center gap-3 p-8
                           hover:border-brand-400 dark:hover:border-brand-500
                           transition-colors min-h-[260px]"
              >
                <FaPlus className="size-6 text-stone-300 dark:text-zinc-600" />
                <span className="text-sm font-medium text-stone-400 dark:text-zinc-500">
                  Publicar otro inmueble
                </span>
              </button>
            </div>
          </>
        )}
      </main>
    </div>
  );
}
