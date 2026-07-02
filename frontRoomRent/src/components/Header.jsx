import { useState } from "react";
import { Link as ScrollLink } from "react-scroll";
import { Link as RouterLink, useLocation, useNavigate } from "react-router-dom";
import { FaXmark, FaBars, FaMoon, FaSun } from "react-icons/fa6";
import logo from "../assets/images/roomrent.png";
import useDarkMode from "./useDarkMode";
import { useAuth } from "../context/AuthContext";

const navItems = [
  { label: "Inicio",       path: "home",        type: "scroll" },
  { label: "Nosotros",     path: "about",        type: "scroll" },
  { label: "Propiedades",  path: "/properties",  type: "route"  },
  { label: "Servicios",    path: "services",     type: "scroll" },
  { label: "Testimonios",  path: "testimonials", type: "scroll" },
  { label: "Contacto",     path: "contact",      type: "scroll" },
];

export default function Header() {
  const { darkMode, toggleDarkMode } = useDarkMode();
  const { user, logout } = useAuth();
  const [menuOpen, setMenuOpen] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();
  const isHome = location.pathname === "/";

  const handleLogout = () => {
    logout();
    navigate("/");
    setMenuOpen(false);
  };

  const linkCls =
    "text-sm font-medium px-3 py-2 rounded-md " +
    "text-stone-600 dark:text-zinc-400 " +
    "hover:text-stone-900 dark:hover:text-white " +
    "hover:bg-stone-100 dark:hover:bg-zinc-800 " +
    "transition-colors duration-150 cursor-pointer";

  const mobileLinkCls =
    "block text-sm font-medium px-4 py-3 rounded-md " +
    "text-stone-600 dark:text-zinc-300 " +
    "hover:text-stone-900 dark:hover:text-white " +
    "hover:bg-stone-50 dark:hover:bg-zinc-800 " +
    "transition-colors duration-150 cursor-pointer";

  const renderItem = ({ label, path, type }, mobile = false) => {
    const cls = mobile ? mobileLinkCls : linkCls;
    const close = () => setMenuOpen(false);

    if (type === "route") {
      return <RouterLink key={label} to={path} className={cls} onClick={close}>{label}</RouterLink>;
    }
    if (!isHome) {
      return (
        <RouterLink key={label} to="/" state={{ scrollTo: path }} className={cls} onClick={close}>
          {label}
        </RouterLink>
      );
    }
    return (
      <ScrollLink key={label} to={path} smooth offset={-72} className={cls} onClick={close}>
        {label}
      </ScrollLink>
    );
  };

  return (
    <nav className="sticky top-0 z-40
                    bg-white/95 dark:bg-zinc-900/95
                    backdrop-blur-sm
                    border-b border-stone-200 dark:border-zinc-800
                    shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">

          {/* Logo */}
          <RouterLink to="/" className="flex items-center gap-2.5 flex-shrink-0">
            <img src={logo} alt="RoomRent" className="h-8 w-auto dark:invert" />
            <span className="hidden sm:block text-sm font-bold text-stone-800 dark:text-white tracking-tight">
              RoomRent
            </span>
          </RouterLink>

          {/* Nav desktop */}
          <div className="hidden lg:flex items-center gap-0.5">
            {navItems.map((item) => renderItem(item))}
          </div>

          {/* Acciones desktop */}
          <div className="hidden lg:flex items-center gap-2">
            <button
              onClick={toggleDarkMode}
              aria-label="Cambiar modo"
              className="p-2 rounded-md text-stone-500 dark:text-zinc-400
                         hover:text-stone-700 dark:hover:text-white
                         hover:bg-stone-100 dark:hover:bg-zinc-800
                         transition-colors duration-150"
            >
              {darkMode ? <FaSun size={16} /> : <FaMoon size={16} />}
            </button>

            {user ? (
              <div className="flex items-center gap-2">
                <span className="text-xs font-medium text-stone-500 dark:text-zinc-400 max-w-[90px] truncate">
                  {user.firstName || user.login}
                </span>
                {user.authorities?.includes("ROLE_ADMIN") ? (
                  <a
                    href="/inmueble"
                    className="text-xs font-semibold px-3 py-1.5 rounded-md
                               bg-amber-500 hover:bg-amber-600 text-white
                               transition-colors duration-150"
                  >
                    Panel Admin
                  </a>
                ) : (
                  <RouterLink
                    to={user.authorities?.includes("ROLE_ARRENDADOR") ? "/arrendador" : "/arrendatario"}
                    className="text-xs font-semibold px-3 py-1.5 rounded-md
                               bg-amber-100 hover:bg-amber-200 text-amber-800
                               transition-colors duration-150"
                  >
                    {user.authorities?.includes("ROLE_ARRENDADOR") ? "Panel Arrendador" : "Mi Panel"}
                  </RouterLink>
                )}
                <button onClick={handleLogout} className="btn-danger">Salir</button>
              </div>
            ) : (
              <RouterLink to="/login" className="btn-primary">
                Iniciar sesión
              </RouterLink>
            )}
          </div>

          {/* Mobile */}
          <div className="flex lg:hidden items-center gap-1">
            <button
              onClick={toggleDarkMode}
              aria-label="Cambiar modo"
              className="p-2 rounded-md text-stone-500 dark:text-zinc-400 hover:bg-stone-100 dark:hover:bg-zinc-800 transition-colors"
            >
              {darkMode ? <FaSun size={16} /> : <FaMoon size={16} />}
            </button>
            <button
              onClick={() => setMenuOpen(!menuOpen)}
              aria-label="Menú"
              className="p-2 rounded-md text-stone-500 dark:text-zinc-400 hover:bg-stone-100 dark:hover:bg-zinc-800 transition-colors"
            >
              {menuOpen ? <FaXmark size={18} /> : <FaBars size={18} />}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile menu */}
      {menuOpen && (
        <div className="lg:hidden bg-white dark:bg-zinc-900 border-t border-stone-100 dark:border-zinc-800 shadow-lg">
          <div className="px-3 py-3 flex flex-col gap-0.5">
            {navItems.map((item) => renderItem(item, true))}
            <div className="mt-3 pt-3 border-t border-stone-100 dark:border-zinc-800">
              {user ? (
                <div className="flex flex-col gap-2 px-1">
                  <span className="text-xs text-zinc-400 truncate">{user.firstName || user.login}</span>
                  {user.authorities?.includes("ROLE_ADMIN") ? (
                    <a
                      href="/inmueble"
                      className="text-sm font-semibold px-3 py-2 rounded-md text-center
                                 bg-amber-500 hover:bg-amber-600 text-white transition-colors"
                    >
                      Panel Admin
                    </a>
                  ) : (
                    <RouterLink
                      to={user.authorities?.includes("ROLE_ARRENDADOR") ? "/arrendador" : "/arrendatario"}
                      onClick={() => setMenuOpen(false)}
                      className="text-sm font-semibold px-3 py-2 rounded-md text-center
                                 bg-amber-100 hover:bg-amber-200 text-amber-800 transition-colors"
                    >
                      {user.authorities?.includes("ROLE_ARRENDADOR") ? "Panel Arrendador" : "Mi Panel"}
                    </RouterLink>
                  )}
                  <button onClick={handleLogout} className="btn-danger w-full">Salir</button>
                </div>
              ) : (
                <RouterLink to="/login" onClick={() => setMenuOpen(false)} className="btn-primary w-full justify-center">
                  Iniciar sesión
                </RouterLink>
              )}
            </div>
          </div>
        </div>
      )}
    </nav>
  );
}
