import dayjs from 'dayjs/esm';

import { EstadoContrato } from 'app/entities/enumerations/estado-contrato.model';
import { IInmueble } from 'app/entities/inmueble/inmueble.model';
import { IPerfilUsuario } from 'app/entities/perfil-usuario/perfil-usuario.model';

export interface IContratoArriendo {
  id: string;
  numeroContrato?: string | null;
  urlContratoDigital?: string | null;
  fechaInicio?: dayjs.Dayjs | null;
  fechaFin?: dayjs.Dayjs | null;
  valorMensual?: number | null;
  valorDeposito?: number | null;
  estado?: keyof typeof EstadoContrato | null;
  fechaFirma?: dayjs.Dayjs | null;
  arrendador?: IPerfilUsuario | null;
  arrendatario?: IPerfilUsuario | null;
  inmueble?: IInmueble | null;
}

export type NewContratoArriendo = Omit<IContratoArriendo, 'id'> & { id: null };
