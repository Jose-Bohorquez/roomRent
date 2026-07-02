import { clients } from "../data/clients";
import { FaStar } from "react-icons/fa";

export default function Clients() {
  return (
    <section id="testimonials" className="section-alt w-full py-24">
      <div className="container-page flex flex-col gap-14">

        <div className="flex flex-col gap-3 max-w-xl">
          <p data-aos="fade-up" className="section-label">Testimonios</p>
          <h2 data-aos="fade-up" data-aos-delay="80" className="section-title">
            Lo que dicen quienes nos eligieron
          </h2>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {clients.map((item, index) => (
            <div
              key={index}
              data-aos="fade-up"
              data-aos-delay={index * 80}
              className="card card-hover p-7 flex flex-col gap-5"
            >
              {/* Estrellas arriba */}
              <div className="flex gap-1">
                {Array.from({ length: 5 }).map((_, i) => (
                  <FaStar key={i} className="size-3.5 text-brand-500" />
                ))}
              </div>

              {/* Texto del testimonio */}
              <p className="text-stone-600 dark:text-zinc-400 text-sm leading-relaxed flex-1">
                "{item.feedback}"
              </p>

              {/* Autor */}
              <div className="flex items-center gap-3 pt-4 border-t border-stone-100 dark:border-zinc-700">
                <img
                  src={item.image}
                  alt={item.name}
                  className="w-10 h-10 rounded-full object-cover ring-2 ring-brand-200 dark:ring-brand-800"
                />
                <div>
                  <p className="text-sm font-semibold text-stone-900 dark:text-white">{item.name}</p>
                  <p className="text-xs text-stone-400 dark:text-zinc-500">{item.text}</p>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
