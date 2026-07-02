import { useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { FaArrowLeft, FaCamera, FaTrash, FaCheck, FaSpinner } from "react-icons/fa6";
import { propiedadApi, multimediaApi, publicacionRawApi, uploadApi } from "../../services/api";

const TIPO_OPTIONS = [
  { value: "APARTAMENTO",    label: "Apartamento",    icon: "🏢" },
  { value: "CASA",           label: "Casa",            icon: "🏠" },
  { value: "HABITACION",     label: "Habitación",      icon: "🛏️" },
  { value: "APARTAESTUDIO",  label: "Apartaestudio",   icon: "🏘️" },
  { value: "LOCAL",          label: "Local",           icon: "🏪" },
  { value: "OFICINA",        label: "Oficina",         icon: "💼" },
  { value: "OTRO",           label: "Otro",            icon: "🏗️" },
];

const ACCEPTED = "image/png,image/jpeg,image/webp,image/heic,image/heif";
const MAX_MB = 20;

function SectionTitle({ n, label }) {
  return (
    <div className="flex items-center gap-3 mb-5">
      <span className="flex items-center justify-center w-7 h-7 rounded-full
                       bg-brand-700 text-white text-xs font-bold flex-shrink-0">
        {n}
      </span>
      <h2 className="text-base font-bold text-stone-900 dark:text-white">{label}</h2>
    </div>
  );
}

function FieldLabel({ children, required }) {
  return (
    <label className="block text-xs font-semibold text-stone-500 dark:text-zinc-400
                      mb-1.5 uppercase tracking-wide">
      {children} {required && <span className="text-red-500">*</span>}
    </label>
  );
}

function Counter({ value, onChange, min = 0, max = 20 }) {
  return (
    <div className="flex items-center gap-2">
      <button type="button"
        onClick={() => onChange(Math.max(min, value - 1))}
        className="w-8 h-8 rounded-md border border-stone-300 dark:border-zinc-600
                   flex items-center justify-center text-stone-600 dark:text-zinc-300
                   hover:bg-stone-100 dark:hover:bg-zinc-700 font-bold text-lg transition-colors"
      >−</button>
      <span className="w-8 text-center font-bold text-stone-900 dark:text-white text-sm">{value}</span>
      <button type="button"
        onClick={() => onChange(Math.min(max, value + 1))}
        className="w-8 h-8 rounded-md border border-stone-300 dark:border-zinc-600
                   flex items-center justify-center text-stone-600 dark:text-zinc-300
                   hover:bg-stone-100 dark:hover:bg-zinc-700 font-bold text-lg transition-colors"
      >+</button>
    </div>
  );
}

function Toggle({ checked, onChange, label }) {
  return (
    <label className="flex items-center gap-3 cursor-pointer select-none">
      <div
        onClick={() => onChange(!checked)}
        className={`relative w-10 h-5 rounded-full transition-colors duration-200
                    ${checked ? "bg-brand-700" : "bg-stone-300 dark:bg-zinc-600"}`}
      >
        <div className={`absolute top-0.5 w-4 h-4 rounded-full bg-white shadow
                         transition-transform duration-200
                         ${checked ? "translate-x-5" : "translate-x-0.5"}`} />
      </div>
      <span className="text-sm text-stone-700 dark:text-zinc-300">{label}</span>
    </label>
  );
}

const INITIAL = {
  nombre: "", ciudad: "", localidad: "", barrio: "", direccion: "",
  latitud: "", longitud: "",
  tipoInmueble: "",
  areaMetrosCuadrados: "", numeroHabitaciones: 1, numeroBanos: 1,
  numeroParqueaderos: 0, estrato: "",
  titulo: "", descripcion: "", canonArriendo: "", deposito: "",
  requisitos: "", seguroRequerido: false, datacreditoRequerido: false,
  fechaDisponible: "",
  estado: "PUBLICADA",
  permiteRoomies: false, aceptaMascotas: false, permiteFumadores: false,
  permiteNinos: true, permiteVisitas: true, permiteParejas: true,
};

export default function InmuebleFormPage() {
  const navigate = useNavigate();
  const fileRef = useRef(null);

  const [form, setForm] = useState(INITIAL);
  const [photos, setPhotos] = useState([]); // { file, preview, id? }
  const [saving, setSaving] = useState(false);
  const [progress, setProgress] = useState(""); // mensaje de progreso
  const [errors, setErrors] = useState({});
  const [globalError, setGlobalError] = useState(null);

  const set = (field, value) => {
    setForm(prev => ({ ...prev, [field]: value }));
    setErrors(prev => { const e = { ...prev }; delete e[field]; return e; });
  };

  /* ── Fotos ── */
  const handleFiles = (fileList) => {
    const existing = photos.length;
    const newPhotos = [];
    for (const file of fileList) {
      if (!file.type.startsWith("image/")) {
        setGlobalError(`"${file.name}" no es una imagen válida.`);
        return;
      }
      if (file.size > MAX_MB * 1024 * 1024) {
        setGlobalError(`"${file.name}" excede ${MAX_MB} MB.`);
        return;
      }
      if (existing + newPhotos.length >= 20) break;
      newPhotos.push({ file, preview: URL.createObjectURL(file) });
    }
    setPhotos(prev => [...prev, ...newPhotos]);
    setGlobalError(null);
  };

  const removePhoto = (idx) => {
    setPhotos(prev => {
      URL.revokeObjectURL(prev[idx].preview);
      return prev.filter((_, i) => i !== idx);
    });
  };

  const moveFirst = (idx) => {
    if (idx === 0) return;
    setPhotos(prev => {
      const next = [...prev];
      const [item] = next.splice(idx, 1);
      return [item, ...next];
    });
  };

  /* ── Validación ── */
  const validate = () => {
    const e = {};
    if (!form.tipoInmueble)    e.tipoInmueble    = "Selecciona el tipo";
    if (!form.nombre.trim())   e.nombre          = "Requerido";
    if (!form.ciudad.trim())   e.ciudad          = "Requerido";
    if (!form.barrio.trim())   e.barrio          = "Requerido";
    if (!form.direccion.trim()) e.direccion       = "Requerido";
    if (form.estrato && (Number(form.estrato) < 1 || Number(form.estrato) > 6))
      e.estrato = "Debe ser entre 1 y 6";
    if (!form.titulo.trim())   e.titulo          = "Requerido";
    if (!form.canonArriendo || Number(form.canonArriendo) <= 0)
      e.canonArriendo = "Ingresa el precio mensual";
    if (photos.length === 0)   e.photos          = "Sube al menos 1 foto";
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  /* ── Submit ── */
  const handleSubmit = async (e) => {
    e.preventDefault();
    setGlobalError(null);
    if (!validate()) { window.scrollTo(0, 0); return; }

    setSaving(true);
    try {
      // 1. Crear Inmueble
      setProgress("Guardando datos del inmueble…");
      const inmuebleBody = {
        nombre:              form.nombre.trim(),
        direccion:           form.direccion.trim(),
        ciudad:              form.ciudad.trim(),
        localidad:           form.localidad.trim() || null,
        barrio:              form.barrio.trim(),
        tipoInmueble:        form.tipoInmueble,
        numeroHabitaciones:  Number(form.numeroHabitaciones),
        numeroBanos:         Number(form.numeroBanos),
        ...(form.areaMetrosCuadrados ? { areaMetrosCuadrados: Number(form.areaMetrosCuadrados) } : {}),
        ...(form.numeroParqueaderos  ? { numeroParqueaderos:  Number(form.numeroParqueaderos)  } : {}),
        ...(form.estrato             ? { estrato:             Number(form.estrato)             } : {}),
        ...(form.latitud             ? { latitud:             Number(form.latitud)             } : {}),
        ...(form.longitud            ? { longitud:            Number(form.longitud)            } : {}),
      };
      const inmueble = await propiedadApi.create(inmuebleBody);
      const inmuebleId = inmueble.id;

      // 2. Subir fotos y registrar en BD
      const uploadedUrls = [];
      for (let i = 0; i < photos.length; i++) {
        setProgress(`Subiendo foto ${i + 1} de ${photos.length}…`);
        try {
          const { url } = await uploadApi.upload(photos[i].file);
          await multimediaApi.create({
            urlMedia:  url,
            tipoMedia: photos[i].file.type,
            principal: i === 0,
            titulo:    null,
            inmueble:  { id: inmuebleId },
          });
          uploadedUrls.push(url);
        } catch (err) {
          console.error("Error subiendo foto", i, err);
        }
      }

      // 3. Crear Publicación
      setProgress("Publicando inmueble…");
      const pubBody = {
        titulo:               form.titulo.trim(),
        descripcion:          form.descripcion.trim() || null,
        canonArriendo:        Number(form.canonArriendo),
        ...(form.deposito         ? { deposito:         Number(form.deposito) }           : {}),
        ...(form.requisitos.trim() ? { requisitos:       form.requisitos.trim() }          : {}),
        seguroRequerido:      form.seguroRequerido,
        datacreditoRequerido: form.datacreditoRequerido,
        ...(form.fechaDisponible  ? { fechaDisponible:  form.fechaDisponible }            : {}),
        estado:               form.estado,
        permiteRoomies:       form.permiteRoomies,
        aceptaMascotas:       form.aceptaMascotas,
        permiteFumadores:     form.permiteFumadores,
        permiteNinos:         form.permiteNinos,
        permiteVisitas:       form.permiteVisitas,
        permiteParejas:       form.permiteParejas,
        inmueble:             { id: inmuebleId },
      };
      const pub = await publicacionRawApi.create(pubBody);

      setProgress("¡Listo!");
      navigate(`/mis-inmuebles?created=${pub.id}`);
    } catch (err) {
      setGlobalError(err.message || "Error al guardar. Intenta nuevamente.");
      setSaving(false);
      setProgress("");
    }
  };

  const inputCls = (field) =>
    `input-base ${errors[field] ? "border-red-400 dark:border-red-600" : ""}`;

  return (
    <div className="bg-surface-light dark:bg-surface-dark min-h-screen">
      <div className="max-w-3xl mx-auto px-4 sm:px-6 py-10">

        {/* Header */}
        <button
          onClick={() => navigate("/mis-inmuebles")}
          className="flex items-center gap-2 text-sm text-stone-500 dark:text-zinc-400
                     hover:text-brand-700 dark:hover:text-brand-500 mb-8 transition-colors"
        >
          <FaArrowLeft className="size-3.5" /> Volver a mis inmuebles
        </button>

        <div className="mb-8">
          <p className="section-label mb-1">Arrendador</p>
          <h1 className="section-title">Publicar inmueble</h1>
          <p className="text-sm text-stone-500 dark:text-zinc-400 mt-1">
            Completa todos los campos para que tu propiedad aparezca visible en la plataforma.
          </p>
        </div>

        {globalError && (
          <div className="mb-6 px-4 py-3 rounded-lg bg-red-50 dark:bg-red-900/20
                          border border-red-200 dark:border-red-800">
            <p className="text-sm text-red-600 dark:text-red-400">{globalError}</p>
          </div>
        )}

        <form onSubmit={handleSubmit} className="flex flex-col gap-8">

          {/* ── 1. Tipo de inmueble ── */}
          <section className="card p-6">
            <SectionTitle n="1" label="¿Qué tipo de inmueble es?" />
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
              {TIPO_OPTIONS.map(opt => (
                <button
                  key={opt.value}
                  type="button"
                  onClick={() => set("tipoInmueble", opt.value)}
                  className={`flex flex-col items-center gap-2 p-4 rounded-xl border-2 transition-all
                              ${form.tipoInmueble === opt.value
                                ? "border-brand-700 bg-brand-50 dark:bg-brand-900/20"
                                : "border-stone-200 dark:border-zinc-700 hover:border-brand-300"}`}
                >
                  <span className="text-2xl">{opt.icon}</span>
                  <span className="text-xs font-semibold text-stone-700 dark:text-zinc-300">
                    {opt.label}
                  </span>
                  {form.tipoInmueble === opt.value && (
                    <FaCheck className="size-3 text-brand-700 dark:text-brand-500" />
                  )}
                </button>
              ))}
            </div>
            {errors.tipoInmueble && (
              <p className="text-xs text-red-500 mt-2">{errors.tipoInmueble}</p>
            )}
          </section>

          {/* ── 2. Datos básicos ── */}
          <section className="card p-6">
            <SectionTitle n="2" label="Datos del inmueble" />
            <div className="flex flex-col gap-4">
              <div>
                <FieldLabel required>Nombre del inmueble</FieldLabel>
                <input
                  className={inputCls("nombre")}
                  placeholder="Ej. Apartamento luminoso en Chapinero"
                  value={form.nombre}
                  onChange={e => set("nombre", e.target.value)}
                />
                {errors.nombre && <p className="text-xs text-red-500 mt-1">{errors.nombre}</p>}
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <FieldLabel required>Ciudad</FieldLabel>
                  <input className={inputCls("ciudad")} placeholder="Bogotá"
                    value={form.ciudad} onChange={e => set("ciudad", e.target.value)} />
                  {errors.ciudad && <p className="text-xs text-red-500 mt-1">{errors.ciudad}</p>}
                </div>
                <div>
                  <FieldLabel>Localidad</FieldLabel>
                  <input className="input-base" placeholder="Chapinero"
                    value={form.localidad} onChange={e => set("localidad", e.target.value)} />
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <FieldLabel required>Barrio</FieldLabel>
                  <input className={inputCls("barrio")} placeholder="Chapinero Central"
                    value={form.barrio} onChange={e => set("barrio", e.target.value)} />
                  {errors.barrio && <p className="text-xs text-red-500 mt-1">{errors.barrio}</p>}
                </div>
                <div>
                  <FieldLabel required>Dirección exacta</FieldLabel>
                  <input className={inputCls("direccion")} placeholder="Calle 67 #4-50 Apto 301"
                    value={form.direccion} onChange={e => set("direccion", e.target.value)} />
                  {errors.direccion && <p className="text-xs text-red-500 mt-1">{errors.direccion}</p>}
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <FieldLabel>Latitud (opcional)</FieldLabel>
                  <input className="input-base" placeholder="4.7110"
                    type="number" step="any"
                    value={form.latitud} onChange={e => set("latitud", e.target.value)} />
                </div>
                <div>
                  <FieldLabel>Longitud (opcional)</FieldLabel>
                  <input className="input-base" placeholder="-74.0721"
                    type="number" step="any"
                    value={form.longitud} onChange={e => set("longitud", e.target.value)} />
                </div>
              </div>
            </div>
          </section>

          {/* ── 3. Características ── */}
          <section className="card p-6">
            <SectionTitle n="3" label="Características" />
            <div className="flex flex-col gap-6">

              <div className="grid grid-cols-1 sm:grid-cols-3 gap-6">
                <div>
                  <FieldLabel required>Habitaciones</FieldLabel>
                  <Counter value={form.numeroHabitaciones} min={0} max={20}
                    onChange={v => set("numeroHabitaciones", v)} />
                </div>
                <div>
                  <FieldLabel required>Baños</FieldLabel>
                  <Counter value={form.numeroBanos} min={0} max={10}
                    onChange={v => set("numeroBanos", v)} />
                </div>
                <div>
                  <FieldLabel>Parqueaderos</FieldLabel>
                  <Counter value={form.numeroParqueaderos} min={0} max={5}
                    onChange={v => set("numeroParqueaderos", v)} />
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <FieldLabel>Área (m²)</FieldLabel>
                  <input className="input-base" placeholder="75"
                    type="number" min="1" step="0.5"
                    value={form.areaMetrosCuadrados}
                    onChange={e => set("areaMetrosCuadrados", e.target.value)} />
                </div>
                <div>
                  <FieldLabel>Estrato (1–6)</FieldLabel>
                  <input className={inputCls("estrato")} placeholder="4"
                    type="number" min="1" max="6"
                    value={form.estrato}
                    onChange={e => set("estrato", e.target.value)} />
                  {errors.estrato && <p className="text-xs text-red-500 mt-1">{errors.estrato}</p>}
                </div>
              </div>
            </div>
          </section>

          {/* ── 4. Fotos ── */}
          <section className="card p-6">
            <SectionTitle n="4" label="Fotos del inmueble" />
            <p className="text-xs text-stone-400 dark:text-zinc-500 mb-4">
              PNG, JPG, WEBP o HEIC · Máx {MAX_MB} MB por foto · La primera foto será la imagen principal
            </p>

            {/* Drop / picker area */}
            <div
              onClick={() => fileRef.current?.click()}
              onDragOver={e => e.preventDefault()}
              onDrop={e => { e.preventDefault(); handleFiles(e.dataTransfer.files); }}
              className="border-2 border-dashed border-stone-300 dark:border-zinc-600
                         rounded-xl p-8 flex flex-col items-center gap-3 cursor-pointer
                         hover:border-brand-400 dark:hover:border-brand-500
                         transition-colors duration-150"
            >
              <FaCamera className="size-8 text-stone-300 dark:text-zinc-600" />
              <p className="text-sm font-medium text-stone-500 dark:text-zinc-400">
                Haz clic o arrastra tus fotos aquí
              </p>
              <p className="text-xs text-stone-400 dark:text-zinc-500">Hasta 20 imágenes</p>
            </div>
            <input
              ref={fileRef}
              type="file"
              accept={ACCEPTED}
              multiple
              className="hidden"
              onChange={e => handleFiles(e.target.files)}
            />

            {errors.photos && (
              <p className="text-xs text-red-500 mt-2">{errors.photos}</p>
            )}

            {/* Preview grid */}
            {photos.length > 0 && (
              <div className="mt-4 grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3">
                {photos.map((p, idx) => (
                  <div key={idx} className="relative group rounded-lg overflow-hidden aspect-video bg-stone-100 dark:bg-zinc-800">
                    <img src={p.preview} alt="" className="w-full h-full object-cover" />
                    {idx === 0 && (
                      <div className="absolute top-1.5 left-1.5 bg-brand-700 text-white
                                      text-xs font-bold px-1.5 py-0.5 rounded">
                        Principal
                      </div>
                    )}
                    <div className="absolute inset-0 bg-stone-950/40 opacity-0 group-hover:opacity-100
                                    transition-opacity flex items-center justify-center gap-2">
                      {idx !== 0 && (
                        <button type="button" onClick={() => moveFirst(idx)}
                          title="Hacer principal"
                          className="p-1.5 bg-white/90 rounded-md text-brand-700 hover:bg-white text-xs font-bold">
                          ★
                        </button>
                      )}
                      <button type="button" onClick={() => removePhoto(idx)}
                        title="Eliminar"
                        className="p-1.5 bg-white/90 rounded-md text-red-600 hover:bg-white">
                        <FaTrash className="size-3" />
                      </button>
                    </div>
                  </div>
                ))}
                {photos.length < 20 && (
                  <button
                    type="button"
                    onClick={() => fileRef.current?.click()}
                    className="aspect-video border-2 border-dashed border-stone-300 dark:border-zinc-600
                               rounded-lg flex items-center justify-center
                               hover:border-brand-400 transition-colors"
                  >
                    <span className="text-stone-400 text-2xl">+</span>
                  </button>
                )}
              </div>
            )}
          </section>

          {/* ── 5. Publicación ── */}
          <section className="card p-6">
            <SectionTitle n="5" label="Información de la publicación" />
            <div className="flex flex-col gap-4">
              <div>
                <FieldLabel required>Título de la publicación</FieldLabel>
                <input className={inputCls("titulo")} placeholder="Hermoso apartamento con vista a la ciudad"
                  value={form.titulo} onChange={e => set("titulo", e.target.value)} />
                {errors.titulo && <p className="text-xs text-red-500 mt-1">{errors.titulo}</p>}
              </div>
              <div>
                <FieldLabel>Descripción</FieldLabel>
                <textarea className="input-base resize-none" rows={4}
                  placeholder="Describe las características del inmueble, ventajas, cercanía a zonas importantes..."
                  value={form.descripcion} onChange={e => set("descripcion", e.target.value)} />
              </div>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <FieldLabel required>Canon de arriendo ($/mes)</FieldLabel>
                  <input className={inputCls("canonArriendo")} placeholder="1500000"
                    type="number" min="1"
                    value={form.canonArriendo} onChange={e => set("canonArriendo", e.target.value)} />
                  {errors.canonArriendo && <p className="text-xs text-red-500 mt-1">{errors.canonArriendo}</p>}
                </div>
                <div>
                  <FieldLabel>Depósito ($)</FieldLabel>
                  <input className="input-base" placeholder="3000000"
                    type="number" min="0"
                    value={form.deposito} onChange={e => set("deposito", e.target.value)} />
                </div>
              </div>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <FieldLabel>Fecha disponible</FieldLabel>
                  <input className="input-base" type="date"
                    value={form.fechaDisponible} onChange={e => set("fechaDisponible", e.target.value)} />
                </div>
                <div>
                  <FieldLabel>Estado</FieldLabel>
                  <select className="input-base" value={form.estado} onChange={e => set("estado", e.target.value)}>
                    <option value="PUBLICADA">Publicada (visible al público)</option>
                    <option value="BORRADOR">Borrador (no visible)</option>
                  </select>
                </div>
              </div>
              <div>
                <FieldLabel>Requisitos del arrendatario</FieldLabel>
                <textarea className="input-base resize-none" rows={3}
                  placeholder="Ej. Trabajo estable, referencias personales, no mascotas..."
                  value={form.requisitos} onChange={e => set("requisitos", e.target.value)} />
              </div>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <Toggle checked={form.datacreditoRequerido}
                  onChange={v => set("datacreditoRequerido", v)}
                  label="Requiere Datacredito" />
                <Toggle checked={form.seguroRequerido}
                  onChange={v => set("seguroRequerido", v)}
                  label="Requiere seguro de arriendo" />
              </div>
            </div>
          </section>

          {/* ── 6. Reglas ── */}
          <section className="card p-6">
            <SectionTitle n="6" label="Reglas del inmueble" />
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Toggle checked={form.aceptaMascotas}   onChange={v => set("aceptaMascotas", v)}   label="Acepta mascotas" />
              <Toggle checked={form.permiteRoomies}   onChange={v => set("permiteRoomies", v)}   label="Permite roomies" />
              <Toggle checked={form.permiteFumadores} onChange={v => set("permiteFumadores", v)} label="Permite fumadores" />
              <Toggle checked={form.permiteNinos}     onChange={v => set("permiteNinos", v)}     label="Permite niños" />
              <Toggle checked={form.permiteVisitas}   onChange={v => set("permiteVisitas", v)}   label="Permite visitas frecuentes" />
              <Toggle checked={form.permiteParejas}   onChange={v => set("permiteParejas", v)}   label="Permite parejas" />
            </div>
          </section>

          {/* ── Submit ── */}
          <div className="flex flex-col sm:flex-row gap-3 justify-end pb-4">
            <button type="button" onClick={() => navigate("/mis-inmuebles")}
              className="btn-secondary" disabled={saving}>
              Cancelar
            </button>
            <button type="submit" className="btn-primary flex items-center gap-2" disabled={saving}>
              {saving ? (
                <>
                  <FaSpinner className="size-4 animate-spin" />
                  {progress || "Guardando…"}
                </>
              ) : "Publicar inmueble"}
            </button>
          </div>

        </form>
      </div>
    </div>
  );
}
