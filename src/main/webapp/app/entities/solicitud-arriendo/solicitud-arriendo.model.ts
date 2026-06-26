import dayjs from 'dayjs/esm';

import { EstadoSolicitud } from 'app/entities/enumerations/estado-solicitud.model';
import { IPerfilUsuario } from 'app/entities/perfil-usuario/perfil-usuario.model';
import { IPublicacionInmueble } from 'app/entities/publicacion-inmueble/publicacion-inmueble.model';

export interface ISolicitudArriendo {
  id: string;
  mensaje?: string | null;
  aceptaTerminos?: boolean | null;
  estado?: keyof typeof EstadoSolicitud | null;
  fechaCreacion?: dayjs.Dayjs | null;
  arrendatario?: IPerfilUsuario | null;
  publicacion?: IPublicacionInmueble | null;
}

export type NewSolicitudArriendo = Omit<ISolicitudArriendo, 'id'> & { id: null };
