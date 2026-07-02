import { useState, useEffect, useCallback } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  FaBath, FaBed, FaLocationDot, FaArrowLeft,
  FaChevronLeft, FaChevronRight, FaXmark,
  FaMaximize, FaDog, FaSmoking, FaChild, FaUsers
} from "react-icons/fa6";
import { MdSpaceDashboard } from "react-icons/md";
import { inmuebleApi } from "../services/api";

/* ── Galería lightbox ── */
function Lightbox({ images, start, onClose }) {
  const [idx, setIdx] = useState(start);
  const total = images.length;

  const prev = useCallback(() => setIdx(i => (i - 1 + total) % total), [total]);
  const next = useCallback(() => setIdx(i => (i + 1) % total), [total]);

  useEffect(() => {
    const onKey = (e) => {
      if (e.key === "ArrowLeft")  prev();
      if (e.key === "ArrowRight") next();
      if (e.key === "Escape")     onClose();
    };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [prev, next, onClose]);

  return (
    <div
      className="fixed inset-0 z-50 bg-stone-950/95 flex flex-col"
      onClick={onClose}
    >
      {/* Close */}
      <div className="flex justify-between items-center px-4 py-3">
        <span className="text-white/70 text-sm">{idx + 1} / {total}</span>
        <button onClick={onClose} className="text-white/70 hover:text-white p-2">
          <FaXmark className="size-5" />
        </button>
      </div>

      {/* Image */}
      <div
        className="flex-1 flex items-center justify-center px-12 relative"
        onClick={e => e.stopPropagation()}
      >
        <img
          src={images[idx]}
          alt={`Foto ${idx + 1}`}
          className="max-h-full max-w-full object-contain rounded-lg"
        />
        {total > 1 && (
          <>
            <button
              onClick={prev}
              className="absolute left-2 top-1/2 -translate-y-1/2
                         p-3 bg-white/10 hover:bg-white/20 rounded-full text-white transition-colors"
            >
              <FaChevronLeft className="size-5" />
            </button>
            <button
              onClick={next}
              className="absolute right-2 top-1/2 -translate-y-1/2
                         p-3 bg-white/10 hover:bg-white/20 rounded-full text-white transition-colors"
            >
              <FaChevronRight className="size-5" />
            </button>
          </>
        )}
      </div>

      {/* Thumbnails */}
      {total > 1 && (
        <div className="flex gap-2 px-4 py-4 overflow-x-auto justify-center"
          onClick={e => e.stopPropagation()}>
          {images.map((src, i) => (
            <button
              key={i}
              onClick={() => setIdx(i)}
              className={`flex-shrink-0 w-14 h-14 rounded-md overflow-hidden
                          border-2 transition-all ${i === idx
                            ? "border-brand-500 opacity-100"
                            : "border-transparent opacity-50 hover:opacity-75"}`}
            >
              <img src={src} alt="" className="w-full h-full object-cover" />
            </button>
          ))}
        </div>
      )}
    </div>
  );
}

/* ── Gallery strip ── */
function Gallery({ images }) {
  const [lightbox, setLightbox] = useState(null);
  if (!images || images.length === 0) return null;

  const main = images[0];
  const rest = images.slice(1, 5);
  const extra = images.length - 5;

  return (
    <>
      <div className="rounded-xl overflow-hidden mb-8 shadow-md">
        {images.length === 1 ? (
          <div
            className="h-72 md:h-[420px] cursor-zoom-in"
            onClick={() => setLightbox(0)}
          >
            <img src={main} alt="Foto principal" className="w-full h-full object-cover" />
          </div>
        ) : (
          <div className="grid grid-cols-4 grid-rows-2 gap-1 h-72 md:h-[420px]">
            {/* Imagen principal — ocupa 2 cols y 2 rows */}
            <div
              className="col-span-2 row-span-2 relative cursor-zoom-in group overflow-hidden"
              onClick={() => setLightbox(0)}
            >
              <img src={main} alt="Foto principal" className="w-full h-full object-cover
                group-hover:scale-105 transition-transform duration-300" />
              <div className="absolute inset-0 bg-stone-950/0 group-hover:bg-stone-950/10 transition-colors" />
            </div>
            {/* Resto de imágenes — 2x2 grid */}
            {rest.map((src, i) => (
              <div
                key={i}
                className="relative cursor-zoom-in group overflow-hidden"
                onClick={() => setLightbox(i + 1)}
              >
                <img src={src} alt={`Foto ${i + 2}`}
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" />
                {i === 3 && extra > 0 && (
                  <div className="absolute inset-0 bg-stone-950/60 flex items-center justify-center">
                    <span className="text-white font-bold text-lg">+{extra + 1}</span>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}

        {/* Ver todas */}
        {images.length > 1 && (
          <button
            onClick={() => setLightbox(0)}
            className="flex items-center gap-2 mt-3 text-xs font-medium text-stone-500
                       dark:text-zinc-400 hover:text-brand-700 dark:hover:text-brand-500 transition-colors"
          >
            <FaMaximize className="size-3" />
            Ver las {images.length} fotos
          </button>
        )}
      </div>

      {lightbox !== null && (
        <Lightbox images={images} start={lightbox} onClose={() => setLightbox(null)} />
      )}
    </>
  );
}

/* ── Badge de regla ── */
function RuleBadge({ condition, trueLabel, falseLabel, icon: Icon }) {
  if (condition === null || condition === undefined) return null;
  return (
    <span className={`flex items-center gap-1.5 text-xs px-2.5 py-1 rounded-full
                      ${condition
                        ? "bg-green-50 text-green-700 dark:bg-green-900/20 dark:text-green-400"
                        : "bg-red-50 text-red-600 dark:bg-red-900/20 dark:text-red-400"}`}>
      {Icon && <Icon className="size-3" />}
      {condition ? trueLabel : falseLabel}
    </span>
  );
}

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

        {/* Galería */}
        <Gallery images={property.images ?? []} />

        {/* Info */}
        <div className="card p-8 flex flex-col gap-6">
          <div>
            <h1 className="text-2xl md:text-3xl font-extrabold text-stone-900 dark:text-white">
              {property.title}
            </h1>
            {property.location && (
              <p className="flex items-center gap-1.5 text-stone-500 dark:text-zinc-400 text-sm mt-2">
                <FaLocationDot className="size-4 text-brand-600 dark:text-brand-500" />
                {property.location}
              </p>
            )}
          </div>

          <div className="flex items-baseline gap-2">
            <p className="text-3xl font-extrabold text-brand-700 dark:text-brand-500">
              ${property.price?.toLocaleString()}
            </p>
            <span className="text-sm text-stone-400 dark:text-zinc-500">/mes</span>
            {property.deposito && (
              <span className="text-sm text-stone-400 dark:text-zinc-500 ml-3">
                Depósito: ${Number(property.deposito).toLocaleString()}
              </span>
            )}
          </div>

          {/* Características */}
          {(property.bath != null || property.bed != null || property.area || property.parking || property.estrato) && (
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
              {property.parking != null && property.parking > 0 && (
                <span className="flex items-center gap-2">
                  🚗 {property.parking} parqueadero{property.parking !== 1 ? "s" : ""}
                </span>
              )}
              {property.estrato && (
                <span className="flex items-center gap-2">
                  🏙️ Estrato {property.estrato}
                </span>
              )}
            </div>
          )}

          {/* Descripción */}
          {property.description && (
            <p className="text-stone-600 dark:text-zinc-400 leading-relaxed">
              {property.description}
            </p>
          )}

          {/* Requisitos */}
          {property.requisitos && (
            <div>
              <p className="text-xs font-semibold text-stone-500 dark:text-zinc-400 uppercase tracking-wide mb-2">
                Requisitos
              </p>
              <p className="text-sm text-stone-600 dark:text-zinc-400">{property.requisitos}</p>
            </div>
          )}

          {/* Fecha disponible */}
          {property.fechaDisponible && (
            <p className="text-sm text-stone-500 dark:text-zinc-400">
              Disponible desde: <strong className="text-stone-700 dark:text-zinc-200">{property.fechaDisponible}</strong>
            </p>
          )}

          {/* Reglas */}
          {(property.aceptaMascotas !== undefined || property.permiteRoomies !== undefined
            || property.permiteFumadores !== undefined || property.permiteNinos !== undefined) && (
            <div>
              <p className="text-xs font-semibold text-stone-500 dark:text-zinc-400 uppercase tracking-wide mb-3">
                Reglas del inmueble
              </p>
              <div className="flex flex-wrap gap-2">
                <RuleBadge condition={property.aceptaMascotas}   trueLabel="Acepta mascotas"   falseLabel="Sin mascotas"   icon={FaDog}   />
                <RuleBadge condition={property.permiteRoomies}   trueLabel="Permite roomies"   falseLabel="Sin roomies"    icon={FaUsers} />
                <RuleBadge condition={property.permiteFumadores} trueLabel="Permite fumadores" falseLabel="No fumadores"   icon={FaSmoking} />
                <RuleBadge condition={property.permiteNinos}     trueLabel="Permite niños"     falseLabel="Sin niños"     icon={FaChild} />
                <RuleBadge condition={property.permiteParejas}   trueLabel="Permite parejas"   falseLabel="Sin parejas"   icon={FaUsers} />
                <RuleBadge condition={property.permiteVisitas}   trueLabel="Permite visitas"   falseLabel="Sin visitas frecuentes" icon={null} />
              </div>
            </div>
          )}

          {/* Verificaciones */}
          {(property.datacreditoReq || property.seguroRequerido) && (
            <div className="flex gap-2 flex-wrap">
              {property.datacreditoReq && (
                <span className="text-xs px-2.5 py-1 bg-amber-50 dark:bg-amber-900/20
                                 text-amber-700 dark:text-amber-400 rounded-full">
                  Requiere Datacredito
                </span>
              )}
              {property.seguroRequerido && (
                <span className="text-xs px-2.5 py-1 bg-amber-50 dark:bg-amber-900/20
                                 text-amber-700 dark:text-amber-400 rounded-full">
                  Requiere seguro de arriendo
                </span>
              )}
            </div>
          )}

          <button className="btn-primary self-start">
            Solicitar visita
          </button>
        </div>
      </div>
    </div>
  );
}
