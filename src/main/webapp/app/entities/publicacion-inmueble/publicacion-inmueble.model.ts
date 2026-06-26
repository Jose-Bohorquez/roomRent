import dayjs from 'dayjs/esm';

import { EstadoPublicacion } from 'app/entities/enumerations/estado-publicacion.model';
import { IInmueble } from 'app/entities/inmueble/inmueble.model';

export interface IPublicacionInmueble {
  id: string;
  titulo?: string | null;
  descripcion?: string | null;
  canonArriendo?: number | null;
  deposito?: number | null;
  requisitos?: string | null;
  seguroRequerido?: boolean | null;
  datacreditoRequerido?: boolean | null;
  fechaDisponible?: dayjs.Dayjs | null;
  estado?: keyof typeof EstadoPublicacion | null;
  permiteRoomies?: boolean | null;
  aceptaMascotas?: boolean | null;
  permiteFumadores?: boolean | null;
  permiteNinos?: boolean | null;
  permiteVisitas?: boolean | null;
  permiteParejas?: boolean | null;
  inmueble?: IInmueble | null;
}

export type NewPublicacionInmueble = Omit<IPublicacionInmueble, 'id'> & { id: null };
