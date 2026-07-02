import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { FaBath, FaBed, FaMapMarkerAlt, FaArrowLeft } from "react-icons/fa";
import { MdSpaceDashboard } from "react-icons/md";
import { inmuebleApi } from "../services/api";

export default function PropertyDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [property, setProperty] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    inmuebleApi.getOne(id)
      .then(setProperty)
      .catch(() => setError(true))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) {
    return (
      <div className="bg-surface-light dark:bg-surface-dark min-h-screen">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 py-12 animate-pulse">
          <div className="h-80 bg-stone-200 dark:bg-zinc-700 rounded-xl mb-8" />
          <div className="space-y-3">
            <div className="h-7 bg-stone-200 dark:bg-zinc-700 rounded w-2/3" />
            <div className="h-4 bg-stone-200 dark:bg-zinc-700 rounded w-1/2" />
            <div className="h-4 bg-stone-200 dark:bg-zinc-700 rounded w-full" />
          </div>
        </div>
      </div>
    );
  }

  if (error || !property) {
    return (
      <div className="bg-surface-light dark:bg-surface-dark min-h-screen flex items-center justify-center">
        <div className="text-center">
          <p className="text-6xl mb-4">🏚️</p>
          <p className="text-stone-600 dark:text-zinc-400 text-lg mb-6">
            Propiedad no encontrada.
          </p>
          <button onClick={() => navigate("/properties")} className="btn-primary">
            Volver al listado
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-surface-light dark:bg-surface-dark min-h-screen">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 py-10">

        <button
          onClick={() => navigate(-1)}
          className="flex items-center gap-2 text-sm text-stone-500 dark:text-zinc-400
                     hover:text-brand-700 dark:hover:text-brand-500 mb-7 transition-colors"
        >
          <FaArrowLeft className="size-3.5" /> Volver
        </button>

        {/* Imagen */}
        <div className="rounded-xl overflow-hidden h-72 md:h-[400px] bg-stone-200 dark:bg-zinc-800 mb-8 shadow-md">
          {property.images?.[0] ? (
            <img src={property.images[0]} alt={property.title} className="w-full h-full object-cover" />
          ) : (
            <div className="w-full h-full flex items-center justify-center text-stone-400 dark:text-zinc-600 text-4xl">🏠</div>
          )}
        </div>

        {/* Info */}
        <div className="card p-8 flex flex-col gap-6">
          <div>
            <h1 className="text-2xl md:text-3xl font-extrabold text-stone-900 dark:text-white">
              {property.title}
            </h1>
            {property.location && (
              <p className="flex items-center gap-1.5 text-stone-500 dark:text-zinc-400 text-sm mt-2">
                <FaMapMarkerAlt className="size-4 text-brand-600 dark:text-brand-500" />
                {property.location}
              </p>
            )}
          </div>

          <div className="flex items-baseline gap-2">
            <p className="text-3xl font-extrabold text-brand-700 dark:text-brand-500">
              ${property.price?.toLocaleString()}
            </p>
            <span className="text-sm text-stone-400 dark:text-zinc-500">/mes</span>
          </div>

          {(property.bath != null || property.bed != null || property.area) && (
            <div className="flex flex-wrap gap-6 text-sm text-stone-600 dark:text-zinc-400
                            py-5 border-y border-stone-100 dark:border-zinc-700">
              {property.bath != null && (
                <span className="flex items-center gap-2">
                  <FaBath className="size-4 text-brand-600 dark:text-brand-500" /> {property.bath} baños
                </span>
              )}
              {property.bed != null && (
                <span className="flex items-center gap-2">
                  <FaBed className="size-4 text-brand-600 dark:text-brand-500" /> {property.bed} habitaciones
                </span>
              )}
              {property.area && (
                <span className="flex items-center gap-2">
                  <MdSpaceDashboard className="size-4 text-brand-600 dark:text-brand-500" /> {property.area} m²
                </span>
              )}
            </div>
          )}

          {property.description && (
            <p className="text-stone-600 dark:text-zinc-400 leading-relaxed">
              {property.description}
            </p>
          )}

          <button className="btn-primary self-start">
            Solicitar visita
          </button>
        </div>
      </div>
    </div>
  );
}
