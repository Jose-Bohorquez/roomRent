import area1 from "../assets/images/House.jpg";
import area2 from "../assets/images/Apartment.jpg";
import area3 from "../assets/images/Room.jpeg";

const types = [
  { img: area1, label: "Casas",        delay: "0"   },
  { img: area2, label: "Apartamentos", delay: "80"  },
  { img: area3, label: "Habitaciones", delay: "160" },
];

const stats = [
  { value: "5.000",  label: "Propiedades disponibles"          },
  { value: "+1.000", label: "Arrendadores que confían en nosotros" },
  { value: "+800",   label: "Familias que encontraron su hogar"    },
];

export default function PopularAreas() {
  return (
    <section id="popular" className="bg-surface-light dark:bg-surface-dark w-full py-24">
      <div className="container-page flex flex-col gap-16">

        {/* Header */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-10 items-start">
          <div className="flex flex-col gap-4">
            <p data-aos="fade-up" className="section-label">Tipos de arriendo</p>
            <h2 data-aos="fade-up" data-aos-delay="80" className="section-title">
              Explora todos los inmuebles disponibles
            </h2>
          </div>

          <div className="lg:col-span-2 grid grid-cols-1 sm:grid-cols-3 gap-4">
            {types.map(({ img, label, delay }) => (
              <div
                key={label}
                data-aos="fade-up"
                data-aos-delay={delay}
                className="group relative h-64 rounded-xl overflow-hidden shadow-md
                           hover:shadow-xl hover:scale-[1.02]
                           transition-all duration-300 cursor-pointer"
                style={{ backgroundImage: `url(${img})`, backgroundSize: "cover", backgroundPosition: "center" }}
              >
                <div className="absolute inset-0 bg-gradient-to-t from-stone-950/70 via-stone-950/20 to-transparent" />
                <span className="absolute bottom-4 left-4 px-3 py-1 bg-brand-700 text-white text-xs font-bold rounded-md tracking-wide">
                  {label}
                </span>
              </div>
            ))}
          </div>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-10 pt-10 border-t border-stone-200 dark:border-zinc-800">
          {stats.map(({ value, label }, i) => (
            <div
              key={value}
              data-aos="fade-up"
              data-aos-delay={i * 100}
              className="flex flex-col gap-2"
            >
              <span className="text-5xl font-extrabold text-brand-700 dark:text-brand-500 leading-none">
                {value}
              </span>
              <span className="text-sm text-stone-500 dark:text-zinc-400 leading-snug">
                {label}
              </span>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
