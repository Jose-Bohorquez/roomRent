import { FaEnvelope, FaPhone, FaMapMarkerAlt } from "react-icons/fa";

const info = [
  { icon: FaEnvelope,      label: "Email",      value: "contacto@roomrent.co"  },
  { icon: FaPhone,         label: "Teléfono",   value: "+57 300 123 4567"      },
  { icon: FaMapMarkerAlt,  label: "Dirección",  value: "Bogotá, Colombia"      },
];

export default function Contact() {
  return (
    <section
      id="contact"
      className="bg-surface-light dark:bg-surface-dark w-full py-24 pb-28"
    >
      <div className="container-page grid grid-cols-1 lg:grid-cols-2 gap-16 items-start">

        {/* Columna izquierda — texto + info */}
        <div className="flex flex-col gap-8">
          <div className="flex flex-col gap-4">
            <p data-aos="fade-up" className="section-label">Contáctanos</p>
            <h2 data-aos="fade-up" data-aos-delay="80" className="section-title">
              ¿Tienes dudas o quieres saber más?
            </h2>
            <p data-aos="fade-up" data-aos-delay="160" className="text-stone-500 dark:text-zinc-400 leading-relaxed">
              Escríbenos sin miedo, estamos aquí para ayudarte. Nos encanta
              escuchar nuevas ideas y acompañarte en cada paso de tu arriendo.
            </p>
          </div>

          <div data-aos="fade-up" data-aos-delay="240" className="flex flex-col gap-4">
            {info.map(({ icon: Icon, label, value }) => (
              <div key={label} className="flex items-center gap-4">
                <div className="w-10 h-10 rounded-lg bg-brand-100 dark:bg-brand-900/30 flex items-center justify-center flex-shrink-0">
                  <Icon className="size-4 text-brand-700 dark:text-brand-500" />
                </div>
                <div>
                  <p className="text-xs font-semibold text-stone-400 dark:text-zinc-500 uppercase tracking-wide">{label}</p>
                  <p className="text-sm font-medium text-stone-800 dark:text-zinc-200">{value}</p>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Columna derecha — formulario */}
        <div data-aos="fade-up" data-aos-delay="120" className="card p-8 flex flex-col gap-4">
          <h3 className="text-lg font-bold text-stone-900 dark:text-white">
            Envíanos tu mensaje
          </h3>
          <input  type="text"  placeholder="Nombre completo"        className="input-base" />
          <input  type="email" placeholder="Correo electrónico"     className="input-base" />
          <input  type="tel"   placeholder="Número de contacto"     className="input-base" />
          <textarea rows={4}   placeholder="Escribe tu mensaje..."  className="input-base resize-none" />
          <button className="btn-primary w-full mt-1">Enviar mensaje</button>
        </div>
      </div>
    </section>
  );
}
