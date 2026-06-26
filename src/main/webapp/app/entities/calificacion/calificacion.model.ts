import dayjs from 'dayjs/esm';

import { IContratoArriendo } from 'app/entities/contrato-arriendo/contrato-arriendo.model';
import { TipoCalificacion } from 'app/entities/enumerations/tipo-calificacion.model';
import { IPerfilUsuario } from 'app/entities/perfil-usuario/perfil-usuario.model';

export interface ICalificacion {
  id: string;
  tipoCalificacion?: keyof typeof TipoCalificacion | null;
  puntaje?: number | null;
  comentario?: string | null;
  fechaCreacion?: dayjs.Dayjs | null;
  visible?: boolean | null;
  autor?: IPerfilUsuario | null;
  calificado?: IPerfilUsuario | null;
  contrato?: IContratoArriendo | null;
}

export type NewCalificacion = Omit<ICalificacion, 'id'> & { id: null };
