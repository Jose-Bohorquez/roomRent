import dayjs from 'dayjs/esm';

import { EstadoVisita } from 'app/entities/enumerations/estado-visita.model';
import { IPerfilUsuario } from 'app/entities/perfil-usuario/perfil-usuario.model';
import { ISolicitudArriendo } from 'app/entities/solicitud-arriendo/solicitud-arriendo.model';

export interface IVisitaProgramada {
  id: string;
  fechaSolicitada?: dayjs.Dayjs | null;
  fechaConfirmada?: dayjs.Dayjs | null;
  notas?: string | null;
  estado?: keyof typeof EstadoVisita | null;
  visitante?: IPerfilUsuario | null;
  solicitud?: ISolicitudArriendo | null;
}

export type NewVisitaProgramada = Omit<IVisitaProgramada, 'id'> & { id: null };
