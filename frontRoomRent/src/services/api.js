const API_BASE = import.meta.env.VITE_API_BASE ?? '';

/* Ordena multimedias: principal primero, extrae urlMedia */
function buildImages(multimedias) {
  if (!Array.isArray(multimedias) || multimedias.length === 0) return [];
  return [...multimedias]
    .sort((a, b) => (b.principal ? 1 : 0) - (a.principal ? 1 : 0))
    .map((m) => m.urlMedia)
    .filter(Boolean);
}

/* ── Adaptador de campos backend → frontend ─────────────────────────────────
   JHipster devuelve nombres en español (titulo, canonArriendo, inmueble.direccion…)
   El frontend usa nombres en inglés (title, price, address…).
   Esta función normaliza la respuesta sin modificar el backend.              */
function transformPublicacion(raw) {
  if (!raw) return null;
  const inm = raw.inmueble ?? {};
  return {
    id:               raw.id,
    title:            raw.titulo            ?? '',
    about:            raw.descripcion       ?? '',
    description:      raw.descripcion       ?? '',   // alias usado en PropertyDetail
    price:            raw.canonArriendo,
    estado:           raw.estado,                    // BORRADOR | PUBLICADA | VISITA_AGENDADA | POSTULANTE_SELECCIONADO | RESERVADA | CONTRATO_EN_FIRMA | ARRENDADA | FINALIZADA | ARCHIVADA
    address:          inm.direccion         ?? '',
    location:         [inm.barrio, inm.localidad, inm.ciudad].filter(Boolean).join(', '),
    city:             inm.ciudad            ?? '',
    bed:              inm.numeroHabitaciones,
    bath:             inm.numeroBanos,
    area:             inm.areaMetrosCuadrados,
    type:             inm.tipoInmueble      ?? '',
    estrato:          inm.estrato,
    parking:          inm.numeroParqueaderos,
    images:           buildImages(inm.multimedias),
    // Campos de detalle
    deposito:         raw.deposito,
    requisitos:       raw.requisitos        ?? '',
    fechaDisponible:  raw.fechaDisponible,
    seguroRequerido:  raw.seguroRequerido,
    datacreditoReq:   raw.datacreditoRequerido,
    permiteRoomies:   raw.permiteRoomies,
    aceptaMascotas:   raw.aceptaMascotas,
    permiteFumadores: raw.permiteFumadores,
    permiteNinos:     raw.permiteNinos,
    permiteVisitas:   raw.permiteVisitas,
    permiteParejas:   raw.permiteParejas,
    _raw:             raw,
  };
}

/* ── Fetch base — Bearer token + manejo de 401 ── */
export async function apiFetch(path, options = {}) {
  const token = localStorage.getItem('token');
  const headers = { 'Content-Type': 'application/json', ...options.headers };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(`${API_BASE}${path}`, { ...options, headers });

  // Token expirado o inválido — limpia sesión y recarga
  if (res.status === 401) {
    localStorage.removeItem('token');
    window.dispatchEvent(new Event('auth:logout'));
    throw new Error('Sesión expirada. Por favor inicia sesión nuevamente.');
  }

  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.detail || err.message || `Error ${res.status}`);
  }

  return res.status === 204 ? null : res.json();
}

/* ── Paginación — JHipster devuelve array con header X-Total-Count ──
   Retorna { data: [], total: number }                                 */
export async function apiFetchPaged(path, options = {}) {
  const token = localStorage.getItem('token');
  const headers = { 'Content-Type': 'application/json', ...options.headers };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(`${API_BASE}${path}`, { ...options, headers });

  if (res.status === 401) {
    localStorage.removeItem('token');
    window.dispatchEvent(new Event('auth:logout'));
    throw new Error('Sesión expirada.');
  }
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.detail || err.message || `Error ${res.status}`);
  }

  const data  = await res.json();
  const total = parseInt(res.headers.get('X-Total-Count') ?? '0', 10);
  return { data: Array.isArray(data) ? data : [], total };
}

/* ── Auth ── */
export const authApi = {
  login: (username, password) =>
    apiFetch('/api/authenticate', {
      method: 'POST',
      body: JSON.stringify({ username, password, rememberMe: false }),
    }),

  getAccount: () => apiFetch('/api/account'),

  register: (data) =>
    apiFetch('/api/register', { method: 'POST', body: JSON.stringify(data) }),

  resetPasswordInit: (email) =>
    apiFetch('/api/account/reset-password/init', {
      method: 'POST',
      headers: { 'Content-Type': 'text/plain' },
      body: email,
    }),
};

/* ── Publicaciones de inmuebles (público GET, autenticado POST/PUT/DELETE) ── */
export const inmuebleApi = {
  getAll:  async (params = '') => {
    const { data, total } = await apiFetchPaged(`/api/publicacion-inmuebles?${params}`);
    return { data: data.map(transformPublicacion), total };
  },
  getOne:  async (id) => transformPublicacion(await apiFetch(`/api/publicacion-inmuebles/${id}`)),
  create:  (data)        => apiFetch('/api/publicacion-inmuebles',     { method: 'POST',  body: JSON.stringify(data) }),
  update:  (id, data)    => apiFetch(`/api/publicacion-inmuebles/${id}`, { method: 'PUT',   body: JSON.stringify(data) }),
  remove:  (id)          => apiFetch(`/api/publicacion-inmuebles/${id}`, { method: 'DELETE' }),
};

/* ── Publicaciones de roomies ── */
export const roomieApi = {
  getAll:  (params = '') => apiFetchPaged(`/api/publicacion-roomies?${params}`),
  getOne:  (id)          => apiFetch(`/api/publicacion-roomies/${id}`),
  create:  (data)        => apiFetch('/api/publicacion-roomies',      { method: 'POST',  body: JSON.stringify(data) }),
  update:  (id, data)    => apiFetch(`/api/publicacion-roomies/${id}`, { method: 'PUT',   body: JSON.stringify(data) }),
  remove:  (id)          => apiFetch(`/api/publicacion-roomies/${id}`, { method: 'DELETE' }),
};

/* ── Solicitudes de arriendo ── */
export const solicitudArriendoApi = {
  getAll:  (params = '') => apiFetchPaged(`/api/solicitud-arriendos?${params}`),
  getOne:  (id)          => apiFetch(`/api/solicitud-arriendos/${id}`),
  create:  (data)        => apiFetch('/api/solicitud-arriendos',       { method: 'POST',  body: JSON.stringify(data) }),
  update:  (id, data)    => apiFetch(`/api/solicitud-arriendos/${id}`, { method: 'PUT',   body: JSON.stringify(data) }),
  remove:  (id)          => apiFetch(`/api/solicitud-arriendos/${id}`, { method: 'DELETE' }),
};

/* ── Visitas programadas ── */
export const visitaApi = {
  getAll:  (params = '') => apiFetchPaged(`/api/visita-programadas?${params}`),
  getOne:  (id)          => apiFetch(`/api/visita-programadas/${id}`),
  create:  (data)        => apiFetch('/api/visita-programadas',        { method: 'POST',  body: JSON.stringify(data) }),
  update:  (id, data)    => apiFetch(`/api/visita-programadas/${id}`,  { method: 'PUT',   body: JSON.stringify(data) }),
  remove:  (id)          => apiFetch(`/api/visita-programadas/${id}`,  { method: 'DELETE' }),
};

/* ── Contratos de arriendo ── */
export const contratoApi = {
  getAll:  (params = '') => apiFetchPaged(`/api/contrato-arriendos?${params}`),
  getOne:  (id)          => apiFetch(`/api/contrato-arriendos/${id}`),
  create:  (data)        => apiFetch('/api/contrato-arriendos',        { method: 'POST',  body: JSON.stringify(data) }),
  update:  (id, data)    => apiFetch(`/api/contrato-arriendos/${id}`,  { method: 'PUT',   body: JSON.stringify(data) }),
  remove:  (id)          => apiFetch(`/api/contrato-arriendos/${id}`,  { method: 'DELETE' }),
};

/* ── Perfil de usuario ── */
export const perfilApi = {
  getAll:  (params = '') => apiFetchPaged(`/api/perfil-usuarios?${params}`),
  getOne:  (id)          => apiFetch(`/api/perfil-usuarios/${id}`),
  update:  (id, data)    => apiFetch(`/api/perfil-usuarios/${id}`,     { method: 'PUT',   body: JSON.stringify(data) }),
};

/* ── Calificaciones ── */
export const calificacionApi = {
  getAll:  (params = '') => apiFetchPaged(`/api/calificacions?${params}`),
  getOne:  (id)          => apiFetch(`/api/calificacions/${id}`),
  create:  (data)        => apiFetch('/api/calificacions',             { method: 'POST',  body: JSON.stringify(data) }),
  remove:  (id)          => apiFetch(`/api/calificacions/${id}`,       { method: 'DELETE' }),
};
