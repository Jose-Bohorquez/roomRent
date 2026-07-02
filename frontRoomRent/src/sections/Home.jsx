import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaSearch } from "react-icons/fa";
import cityimg from "../assets/images/city.png";

const localidades = [
  "Antonio Nariño","Barrios Unidos","Bosa","Chapinero","Engativá",
  "Fontibón","Kennedy","Usaquén","Santa Fe","San Cristóbal","Usme",
  "Tunjuelito","Suba","Teusaquillo","Los Mártires","Puente Aranda",
  "Rafael Uribe Uribe","Ciudad Bolívar","La Candelaria",
];

export default function Home() {
  const navigate = useNavigate();
  const [locationFilter, setLocationFilter] = useState("");
  const [type, setType] = useState("");
  const [price, setPrice] = useState("");

  const handleSearch = () => {
    navigate(`/properties?location=${locationFilter}&type=${type}&price=${price}`);
  };

  return (
    <div id="home">

      {/* Hero — full bleed, sin bordes */}
      <section
        className="relative w-full h-[560px] md:h-[640px] overflow-hidden"
        style={{ backgroundImage: `url(${cityimg})`, backgroundSize: "cover", backgroundPosition: "center" }}
      >
        {/* Overlay fuerte — garantiza legibilidad */}
        <div className="absolute inset-0 bg-gradient-to-r from-stone-950/80 via-stone-950/55 to-stone-950/20" />

        <div className="relative z-10 h-full container-page flex flex-col justify-center">
          <div className="max-w-lg">
            <span
              data-aos="fade-up"
              className="inline-block text-xs font-bold tracking-[0.18em] uppercase text-brand-400 mb-5"
            >
              Bogotá · Arriendos y propiedades
            </span>
            <h1
              data-aos="fade-up"
              data-aos-delay="80"
              className="text-5xl md:text-6xl lg:text-7xl font-extrabold text-white leading-[1.05] tracking-tight"
            >
              Encuentra<br />tu hogar<br />
              <span className="text-brand-400">ideal.</span>
            </h1>
            <p
              data-aos="fade-up"
              data-aos-delay="180"
              className="text-stone-300 text-base md:text-lg mt-6 leading-relaxed max-w-sm"
            >
              Conectamos propietarios y arrendatarios en Bogotá con transparencia y confianza.
            </p>
          </div>
        </div>
      </section>

      {/* Buscador — card flotante sobre el hero */}
      <div className="bg-surface-light dark:bg-surface-dark">
        <div className="container-page">
          <div
            data-aos="fade-up"
            data-aos-delay="80"
            className="card -mt-10 w-full lg:w-[85%] mx-auto
                       grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 p-6"
          >
            <div>
              <label className="block text-xs font-semibold text-stone-500 dark:text-zinc-400 mb-1.5 uppercase tracking-wide">
                Localidad
              </label>
              <select value={locationFilter} onChange={(e) => setLocationFilter(e.target.value)} className="select-base">
                <option value="">Todas las localidades</option>
                {localidades.map((loc) => (
                  <option key={loc} value={loc}>{loc}</option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-stone-500 dark:text-zinc-400 mb-1.5 uppercase tracking-wide">
                Tipo
              </label>
              <select value={type} onChange={(e) => setType(e.target.value)} className="select-base">
                <option value="">Todos los tipos</option>
                <option value="Casa">Casa</option>
                <option value="Apartamento">Apartamento</option>
                <option value="Habitación">Habitación</option>
                <option value="Roomie">Roomie</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-stone-500 dark:text-zinc-400 mb-1.5 uppercase tracking-wide">
                Precio
              </label>
              <select value={price} onChange={(e) => setPrice(e.target.value)} className="select-base">
                <option value="">Cualquier precio</option>
                <option value="0-500000">Hasta $500.000</option>
                <option value="500000-1000000">$500.000 – $1.000.000</option>
                <option value="1000000-1500000">$1.000.000 – $1.500.000</option>
                <option value="1500000+">Más de $1.500.000</option>
              </select>
            </div>

            <div className="flex items-end">
              <button onClick={handleSearch} className="btn-primary w-full gap-2">
                <FaSearch className="size-3.5" />
                Buscar
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
