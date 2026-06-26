import dayjs from 'dayjs/esm';

import { EstadoPublicacion } from 'app/entities/enumerations/estado-publicacion.model';
import { Genero } from 'app/entities/enumerations/genero.model';
import { IInmueble } from 'app/entities/inmueble/inmueble.model';
import { IPerfilUsuario } from 'app/entities/perfil-usuario/perfil-usuario.model';

export interface IPublicacionRoomie {
  id: string;
  titulo?: string | null;
  nombreHabitacion?: string | null;
  valorMensual?: number | null;
  serviciosIncluidos?: string | null;
  espaciosCompartidos?: string | null;
  generoPreferido?: keyof typeof Genero | null;
  fechaDisponible?: dayjs.Dayjs | null;
  estado?: keyof typeof EstadoPublicacion | null;
  arrendatario?: IPerfilUsuario | null;
  inmueble?: IInmueble | null;
}

export type NewPublicacionRoomie = Omit<IPublicacionRoomie, 'id'> & { id: null };
