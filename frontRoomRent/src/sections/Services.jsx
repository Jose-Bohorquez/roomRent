import { services } from "../data/services";

export default function Services() {
  return (
    /* Sección oscura — contraste fuerte que rompe el ritmo visual */
    <section
      id="services"
      className="bg-stone-950 dark:bg-zinc-950 w-full py-24"
    >
      <div className="container-page flex flex-col gap-14">

        <div className="flex flex-col gap-4 max-w-xl">
          <p data-aos="fade-up" className="text-xs font-bold tracking-[0.14em] uppercase text-brand-500">
            Nuestros servicios
          </p>
          <h2 data-aos="fade-up" data-aos-delay="80" className="text-3xl md:text-4xl font-extrabold text-white leading-tight">
            Los mejores servicios para ti
          </h2>
          <p data-aos="fade-up" data-aos-delay="160" className="text-stone-400 leading-relaxed">
            Diseñados para hacer el proceso de arriendo más simple, seguro y transparente.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {services.map((service, index) => (
            <div
              key={index}
              data-aos="fade-up"
              data-aos-delay={index * 80}
              className="group bg-white/[0.04] hover:bg-brand-700
                         border border-white/10 hover:border-brand-600
                         rounded-xl px-7 py-8 flex flex-col gap-5
                         transition-all duration-200 cursor-pointer"
            >
              <div className="p-3 rounded-lg bg-white/[0.07] group-hover:bg-white/15 w-fit transition-colors duration-200">
                <service.icon className="size-7 text-brand-400 group-hover:text-white transition-colors" />
              </div>
              <h3 className="font-semibold text-white text-base leading-snug">
                {service.title}
              </h3>
              <p className="text-sm text-stone-400 group-hover:text-white/80 leading-relaxed transition-colors">
                {service.desc}
              </p>
              <span className="text-xs font-semibold text-brand-400 group-hover:text-white/80 uppercase tracking-wider w-fit mt-auto transition-colors">
                Leer más →
              </span>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
