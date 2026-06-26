import dayjs from 'dayjs/esm';

import { EstadoSolicitud } from 'app/entities/enumerations/estado-solicitud.model';
import { IPerfilUsuario } from 'app/entities/perfil-usuario/perfil-usuario.model';
import { IPublicacionRoomie } from 'app/entities/publicacion-roomie/publicacion-roomie.model';

export interface ISolicitudRoomie {
  id: string;
  mensaje?: string | null;
  referencias?: string | null;
  estado?: keyof typeof EstadoSolicitud | null;
  fechaCreacion?: dayjs.Dayjs | null;
  postulante?: IPerfilUsuario | null;
  publicacionRoomie?: IPublicacionRoomie | null;
}

export type NewSolicitudRoomie = Omit<ISolicitudRoomie, 'id'> & { id: null };
