import { useNavigate } from "react-router-dom";
import { FaBath, FaBed } from "react-icons/fa";
import { MdSpaceDashboard } from "react-icons/md";
import { FaMapMarkerAlt } from "react-icons/fa";

export default function PropertyCard({ property }) {
  const navigate = useNavigate();

  return (
    <article
      onClick={() => navigate(`/properties/${property.id}`)}
      className="card overflow-hidden hover:shadow-md transition-all duration-300 cursor-pointer group"
    >
      {/* Imagen */}
      <div
        className="relative h-52 overflow-hidden"
        style={{
          background: property.images?.[0]
            ? `url(${property.images[0]}) center/cover`
            : "linear-gradient(135deg, #f5f5f4, #e7e5e4)"
        }}
      >
        <div className="absolute inset-0 bg-gradient-to-t from-stone-950/50 via-stone-950/10 to-transparent" />
        {property.type && (
          <div className="absolute top-3 left-3">
            <span className="px-2.5 py-1 bg-brand-700 text-white text-xs font-bold rounded-md">
              {property.type}
            </span>
          </div>
        )}
        {property.address && (
          <div className="absolute bottom-3 left-3 flex items-center gap-1.5">
            <FaMapMarkerAlt className="size-3 text-white/70" />
            <span className="text-white text-xs truncate max-w-[160px]">{property.address}</span>
          </div>
        )}
      </div>

      {/* Contenido */}
      <div className="p-5 flex flex-col gap-3">
        <h3 className="font-bold text-stone-900 dark:text-white text-sm line-clamp-1">
          {property.title || property.name}
        </h3>

        {property.price && (
          <p className="text-brand-700 dark:text-brand-500 font-extrabold text-xl leading-none">
            ${property.price?.toLocaleString()}
            <span className="text-xs font-normal text-stone-400 dark:text-zinc-500 ml-1">/mes</span>
          </p>
        )}

        {property.about && (
          <p className="text-xs text-stone-500 dark:text-zinc-400 line-clamp-2 leading-relaxed">
            {property.about}
          </p>
        )}

        {(property.bed || property.bath || property.area) && (
          <div className="flex gap-4 text-xs text-stone-500 dark:text-zinc-400 pt-3
                          border-t border-stone-100 dark:border-zinc-700">
            {property.bath && (
              <span className="flex items-center gap-1.5">
                <FaBath className="size-3.5 text-brand-600 dark:text-brand-500" /> {property.bath} baños
              </span>
            )}
            {property.bed && (
              <span className="flex items-center gap-1.5">
                <FaBed className="size-3.5 text-brand-600 dark:text-brand-500" /> {property.bed} hab.
              </span>
            )}
            {property.area && (
              <span className="flex items-center gap-1.5">
                <MdSpaceDashboard className="size-3.5 text-brand-600 dark:text-brand-500" /> {property.area} m²
              </span>
            )}
          </div>
        )}
      </div>
    </article>
  );
}
