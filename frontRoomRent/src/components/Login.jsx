import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { authApi } from "../services/api";
import ResetPasswordModal from "./ResetPasswordModal";
import bgVideo from "../assets/videos/bogota.mp4";

export default function Login() {
  const [isLoginMode, setIsLoginMode] = useState(true);
  const [openModal, setOpenModal] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    setLoading(true);
    const form = e.target;

    try {
      if (isLoginMode) {
        const account = await login(form.username.value, form.password.value);
        const roles = account.authorities ?? [];
        if (roles.includes("ROLE_ADMIN"))           navigate("/admin");
        else if (roles.includes("ROLE_ARRENDADOR"))  navigate("/arrendador");
        else                                         navigate("/arrendatario");
      } else {
        await authApi.register({
          login:    form.username.value,
          email:    form.email.value,
          password: form.password.value,
          langKey:  "es",
        });
        setSuccess("Cuenta creada. Revisa tu correo para activarla.");
        setIsLoginMode(true);
      }
    } catch (err) {
      setError(err.message || "Ocurrió un error. Intenta nuevamente.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="relative min-h-screen flex items-center justify-center overflow-hidden">

      {/* Video de fondo */}
      <video
        className="absolute inset-0 w-full h-full object-cover"
        src={bgVideo}
        autoPlay loop muted playsInline
      />
      <div className="absolute inset-0 bg-stone-950/65" />

      {/* Formulario */}
      <div className="relative z-10 w-full max-w-sm mx-4">
        <div className="bg-white dark:bg-zinc-900 rounded-2xl shadow-2xl p-8">

          {/* Logo / marca */}
          <div className="text-center mb-6">
            <h1 className="text-xl font-extrabold text-stone-900 dark:text-white tracking-tight">
              RoomRent
            </h1>
            <p className="text-xs text-stone-400 dark:text-zinc-500 mt-0.5">
              {isLoginMode ? "Ingresa a tu cuenta" : "Crea tu cuenta"}
            </p>
          </div>

          {/* Toggle */}
          <div className="relative flex h-10 mb-6 rounded-lg overflow-hidden bg-stone-100 dark:bg-zinc-800 p-1">
            <button
              type="button"
              onClick={() => setIsLoginMode(true)}
              className={`w-1/2 text-sm font-semibold rounded-md z-10 transition-colors duration-200
                ${isLoginMode ? "text-white" : "text-stone-500 dark:text-zinc-400"}`}
            >
              Ingresar
            </button>
            <button
              type="button"
              onClick={() => setIsLoginMode(false)}
              className={`w-1/2 text-sm font-semibold rounded-md z-10 transition-colors duration-200
                ${!isLoginMode ? "text-white" : "text-stone-500 dark:text-zinc-400"}`}
            >
              Registrarse
            </button>
            <div
              className={`absolute top-1 bottom-1 w-[calc(50%-4px)] rounded-md bg-brand-700
                          transition-all duration-300 ${isLoginMode ? "left-1" : "left-[calc(50%+3px)]"}`}
            />
          </div>

          {/* Feedback */}
          {error && (
            <div className="mb-4 px-4 py-3 rounded-lg bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800">
              <p className="text-sm text-red-600 dark:text-red-400">{error}</p>
            </div>
          )}
          {success && (
            <div className="mb-4 px-4 py-3 rounded-lg bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-800">
              <p className="text-sm text-green-700 dark:text-green-400">{success}</p>
            </div>
          )}

          {/* Formulario */}
          <form className="flex flex-col gap-4" onSubmit={handleSubmit}>
            <div>
              <label className="block text-xs font-semibold text-stone-500 dark:text-zinc-400 mb-1.5 uppercase tracking-wide">
                Usuario
              </label>
              <input name="username" type="text" placeholder="nombre de usuario"
                required autoComplete="username" className="input-base" />
            </div>

            {!isLoginMode && (
              <div>
                <label className="block text-xs font-semibold text-stone-500 dark:text-zinc-400 mb-1.5 uppercase tracking-wide">
                  Correo
                </label>
                <input name="email" type="email" placeholder="correo@ejemplo.com"
                  required autoComplete="email" className="input-base" />
              </div>
            )}

            <div>
              <label className="block text-xs font-semibold text-stone-500 dark:text-zinc-400 mb-1.5 uppercase tracking-wide">
                Contraseña
              </label>
              <input name="password" type="password" placeholder="••••••••"
                required autoComplete={isLoginMode ? "current-password" : "new-password"}
                className="input-base" />
            </div>

            {isLoginMode && (
              <button
                type="button"
                onClick={() => setOpenModal(true)}
                className="text-xs text-brand-700 hover:text-brand-800 dark:text-brand-500 text-left hover:underline"
              >
                ¿Olvidaste tu contraseña?
              </button>
            )}

            <button type="submit" disabled={loading} className="btn-primary w-full mt-1">
              {loading ? "Cargando..." : isLoginMode ? "Ingresar" : "Crear cuenta"}
            </button>

            <p className="text-center text-xs text-stone-400 dark:text-zinc-500">
              {isLoginMode ? "¿No tienes cuenta? " : "¿Ya tienes cuenta? "}
              <button
                type="button"
                onClick={() => { setIsLoginMode(!isLoginMode); setError(""); setSuccess(""); }}
                className="text-brand-700 dark:text-brand-500 font-semibold hover:underline"
              >
                {isLoginMode ? "Regístrate" : "Inicia sesión"}
              </button>
            </p>
          </form>
        </div>
      </div>

      {openModal && <ResetPasswordModal onClose={() => setOpenModal(false)} />}
    </div>
  );
}
