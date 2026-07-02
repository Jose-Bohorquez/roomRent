import {
  FaFacebookF, FaThreads, FaInstagram, FaYoutube, FaXTwitter,
  FaArrowUp, FaFireFlameCurved,
} from "react-icons/fa6";
import { Link } from "react-scroll";
import sena from "../assets/images/Sena.png";

const socialLinks = [
  { icon: FaFacebookF, label: "Facebook" },
  { icon: FaInstagram, label: "Instagram" },
  { icon: FaXTwitter,  label: "Twitter/X" },
  { icon: FaYoutube,   label: "YouTube"   },
  { icon: FaThreads,   label: "Threads"   },
];

const authors = ["Ronal Cucariano", "Briant Grippa", "Jose Bohorquez", "Santiago Basto"];

export default function Footer() {
  return (
    <>
      <footer className="bg-stone-950 dark:bg-zinc-950 border-t border-white/5">
        <div className="container-page py-14 grid grid-cols-1 md:grid-cols-3 gap-10">

          {/* Redes */}
          <div className="flex flex-col gap-4">
            <h2 className="text-white text-sm font-bold uppercase tracking-wider">Síguenos</h2>
            <p className="text-stone-400 text-sm leading-relaxed">
              Tips, novedades y oportunidades de arriendo en nuestras redes.
            </p>
            <div className="flex gap-2 mt-1">
              {socialLinks.map(({ icon: Icon, label }) => (
                <button
                  key={label}
                  aria-label={label}
                  className="w-9 h-9 rounded-lg bg-white/[0.06] text-stone-400
                             hover:bg-brand-700 hover:text-white
                             flex items-center justify-center
                             transition-colors duration-150"
                >
                  <Icon className="size-3.5" />
                </button>
              ))}
            </div>
            <p className="text-stone-600 text-xs mt-2">© 2025 RoomRent. Todos los derechos reservados.</p>
          </div>

          {/* Equipo */}
          <div className="flex flex-col gap-4">
            <h2 className="text-white text-sm font-bold uppercase tracking-wider">Equipo</h2>
            <ul className="flex flex-col gap-2.5">
              {authors.map((name) => (
                <li key={name} className="flex items-center gap-2.5 text-stone-400 text-sm">
                  <FaFireFlameCurved className="size-3.5 text-brand-500 flex-shrink-0" />
                  {name}
                </li>
              ))}
            </ul>
          </div>

          {/* SENA */}
          <div className="flex flex-col items-start md:items-center gap-4">
            <img
              src={sena}
              alt="SENA"
              className="w-24 rounded-lg opacity-70 hover:opacity-100 transition-opacity"
            />
            <div className="md:text-center">
              <p className="text-white text-sm font-semibold">Servicio Nacional de Aprendizaje</p>
              <p className="text-stone-400 text-xs mt-0.5">Ficha 3311941 · Tecnólogo ADSO</p>
            </div>
          </div>
        </div>
      </footer>

      {/* FAB */}
      <Link to="home" spy smooth offset={-72}>
        <button
          aria-label="Volver arriba"
          className="fixed bottom-6 right-6 z-50
                     w-11 h-11 rounded-full
                     bg-brand-700 hover:bg-brand-800 text-white
                     shadow-lg shadow-brand-900/40
                     flex items-center justify-center
                     transition-all duration-150"
        >
          <FaArrowUp className="size-4" />
        </button>
      </Link>
    </>
  );
}
