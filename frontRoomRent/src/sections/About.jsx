import aboutimg from "../assets/images/sala.jpg";

export default function About() {
  return (
    <section id="about" className="section-alt w-full py-24">
      <div className="container-page grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">

        <div data-aos="fade-right">
          <img
            src={aboutimg}
            alt="Sala de una propiedad"
            className="rounded-2xl w-full object-cover shadow-xl"
            style={{ maxHeight: "480px" }}
          />
        </div>

        <div className="flex flex-col gap-6">
          <p data-aos="fade-up" className="section-label">Quiénes somos</p>
          <h2 data-aos="fade-up" data-aos-delay="100" className="section-title">
            Tu aliado para encontrar o publicar propiedades de forma segura.
          </h2>
          <p
            data-aos="fade-up"
            data-aos-delay="200"
            className="text-stone-500 dark:text-zinc-400 leading-relaxed text-base"
          >
            Somos una plataforma creada para conectar personas con espacios ideales, ofreciendo
            procesos de arriendo claros, seguros y sin complicaciones. Creemos en la transparencia,
            la confianza y la tecnología como herramientas para transformar la forma en que
            propietarios y arrendatarios se encuentran.
          </p>
          <div
            data-aos="fade-up"
            data-aos-delay="280"
            className="flex items-center gap-4"
          >
            <button className="btn-primary">Ver más</button>
            <div className="flex items-center gap-2">
              <div className="flex -space-x-2">
                {["🙂","😊","🙃"].map((e, i) => (
                  <div key={i} className="w-8 h-8 rounded-full bg-brand-100 dark:bg-brand-900/40 flex items-center justify-center text-sm ring-2 ring-white dark:ring-zinc-900">
                    {e}
                  </div>
                ))}
              </div>
              <span className="text-xs text-stone-500 dark:text-zinc-400">+800 familias encontraron su hogar</span>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
