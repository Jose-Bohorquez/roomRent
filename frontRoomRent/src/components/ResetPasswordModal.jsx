import { useState } from "react";
import { FaXmark } from "react-icons/fa6";
import { authApi } from "../services/api";

export default function ResetPasswordModal({ onClose }) {
  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(false);
  const [sent, setSent] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await authApi.resetPasswordInit(email);
      setSent(true);
    } catch {
      setError("No pudimos enviar el correo. Verifica la dirección.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div className="absolute inset-0 bg-black/60 backdrop-blur-sm" onClick={onClose} />
      <div className="relative z-10 bg-white dark:bg-zinc-900 rounded-2xl shadow-2xl w-full max-w-sm p-8">

        <div className="flex items-start justify-between mb-6">
          <div>
            <h2 className="text-lg font-bold text-stone-900 dark:text-white">
              Recuperar contraseña
            </h2>
            <p className="text-sm text-stone-500 dark:text-zinc-400 mt-1">
              Te enviaremos un enlace a tu correo.
            </p>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="p-1.5 rounded-lg text-stone-400 hover:bg-stone-100 dark:hover:bg-zinc-800 transition-colors"
          >
            <FaXmark size={16} />
          </button>
        </div>

        {sent ? (
          <div className="text-center py-2">
            <p className="text-green-600 dark:text-green-400 font-medium text-sm">
              ✓ Enlace enviado. Revisa tu correo.
            </p>
            <button type="button" onClick={onClose} className="btn-primary w-full mt-6">
              Cerrar
            </button>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="flex flex-col gap-4">
            {error && (
              <div className="px-4 py-3 rounded-xl bg-red-50 dark:bg-red-900/30 border border-red-200 dark:border-red-800">
                <p className="text-sm text-red-600 dark:text-red-400">{error}</p>
              </div>
            )}
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wide text-stone-500 dark:text-zinc-400 mb-1.5">
                Correo electrónico
              </label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="correo@ejemplo.com"
                required
                className="input-base"
              />
            </div>
            <button type="submit" disabled={loading} className="btn-primary w-full">
              {loading ? "Enviando..." : "Enviar enlace"}
            </button>
            <button type="button" onClick={onClose} className="btn-ghost w-full">
              Cancelar
            </button>
          </form>
        )}
      </div>
    </div>
  );
}
