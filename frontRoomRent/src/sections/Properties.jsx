import { useState, useEffect } from "react";
import { Link, useLocation } from "react-router-dom";
import { FaBath, FaBed, FaMapMarkerAlt } from "react-icons/fa";
import { MdSpaceDashboard } from "react-icons/md";
import { inmuebleApi } from "../services/api";

function SkeletonCard() {
  return (
    <div className="card overflow-hidden animate-pulse">
      <div className="h-52 bg-stone-200 dark:bg-zinc-700" />
      <div className="p-5 space-y-3">
        <div className="h-3 bg-stone-200 dark:bg-zinc-700 rounded w-2/3" />
        <div className="h-5 bg-stone-200 dark:bg-zinc-700 rounded w-1/3" />
        <div className="h-3 bg-stone-200 dark:bg-zinc-700 rounded w-full" />
      </div>
    </div>
  );
}

export default function Properties() {
  const [properties, setProperties] = useState([]);
  const [loading, setLoading] = useState(true);
  const locationHook = useLocation();

  useEffect(() => {
    const fetch = async () => {
      setLoading(true);
      try {
        const params = new URLSearchParams(locationHook.search);
        const { data } = await inmuebleApi.getAll(params.toString());
        setProperties(data);
      } catch {
        setProperties([]);
      } finally {
        setLoading(false);
      }
    };
    fetch();
  }, [locationHook.search]);

  return (
    <div className="bg-surface-light dark:bg-surface-dark min-h-screen">
      <section id="properties" className="container-page py-16">

        <div className="flex flex-col gap-1.5 mb-12">
          <p className="section-label">Inmuebles</p>
          <h1 className="section-title">Explora los inmuebles disponibles</h1>
        </div>

        {loading ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {Array.from({ length: 6 }).map((_, i) => <SkeletonCard key={i} />)}
          </div>
        ) : properties.length === 0 ? (
          <div className="text-center py-24">
            <p className="text-5xl mb-4">🔍</p>
            <p className="text-stone-500 dark:text-zinc-400 text-lg">
              No se encontraron propiedades con esos filtros.
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {properties.map((item) => (
              <Link
                key={item._id || item.id}
                to={`/properties/${item._id || item.id}`}
                className="card card-hover overflow-hidden group block"
              >
                {/* Imagen */}
                <div
                  className="relative h-52 overflow-hidden"
                  style={{
                    background: item.images?.[0]
                      ? `url(${item.images[0]}) center/cover`
                      : "linear-gradient(135deg, #f5f5f4, #e7e5e4)"
                  }}
                >
                  <div className="absolute inset-0 bg-gradient-to-t from-stone-950/60 via-stone-950/10 to-transparent" />
                  <div className="absolute top-3 left-3">
                    <span className="px-2.5 py-1 bg-brand-700 text-white text-xs font-bold rounded-md">
                      {item.type || "Inmueble"}
                    </span>
                  </div>
                  {item.address && (
                    <div className="absolute bottom-3 left-3 flex items-center gap-1.5">
                      <FaMapMarkerAlt className="size-3 text-white/70" />
                      <span className="text-white text-xs truncate max-w-[160px]">{item.address}</span>
                    </div>
                  )}
                </div>

                {/* Contenido */}
                <div className="p-5 flex flex-col gap-3">
                  <h2 className="font-bold text-stone-900 dark:text-white text-sm line-clamp-1">
                    {item.title}
                  </h2>
                  <p className="text-brand-700 dark:text-brand-500 font-extrabold text-xl leading-none">
                    ${item.price?.toLocaleString()}
                    <span className="text-xs font-normal text-stone-400 dark:text-zinc-500 ml-1">/mes</span>
                  </p>

                  {item.about && (
                    <p className="text-xs text-stone-500 dark:text-zinc-400 line-clamp-2 leading-relaxed">
                      {item.about}
                    </p>
                  )}

                  <div className="flex gap-4 text-xs text-stone-500 dark:text-zinc-400 pt-3
                                  border-t border-stone-100 dark:border-zinc-700">
                    {item.bath != null && (
                      <span className="flex items-center gap-1.5">
                        <FaBath className="size-3.5 text-brand-600 dark:text-brand-500" /> {item.bath} baños
                      </span>
                    )}
                    {item.bed != null && (
                      <span className="flex items-center gap-1.5">
                        <FaBed className="size-3.5 text-brand-600 dark:text-brand-500" /> {item.bed} hab.
                      </span>
                    )}
                    {item.area && (
                      <span className="flex items-center gap-1.5">
                        <MdSpaceDashboard className="size-3.5 text-brand-600 dark:text-brand-500" /> {item.area} m²
                      </span>
                    )}
                  </div>
                </div>
              </Link>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}
