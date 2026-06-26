import dayjs from 'dayjs/esm';

import { EstadoUsuario } from 'app/entities/enumerations/estado-usuario.model';
import { Genero } from 'app/entities/enumerations/genero.model';
import { TipoDocumento } from 'app/entities/enumerations/tipo-documento.model';
import { IUser } from 'app/entities/user/user.model';

export interface IPerfilUsuario {
  id: string;
  tipoDocumento?: keyof typeof TipoDocumento | null;
  numeroDocumento?: string | null;
  primerNombre?: string | null;
  segundoNombre?: string | null;
  primerApellido?: string | null;
  segundoApellido?: string | null;
  fechaNacimiento?: dayjs.Dayjs | null;
  genero?: keyof typeof Genero | null;
  telefono?: string | null;
  direccionActual?: string | null;
  ciudad?: string | null;
  barrio?: string | null;
  profesion?: string | null;
  ocupacion?: string | null;
  empresaTrabajo?: string | null;
  universidad?: string | null;
  biografia?: string | null;
  intereses?: string | null;
  tieneMascotas?: boolean | null;
  fumador?: boolean | null;
  verificado?: boolean | null;
  habilitadoRoomie?: boolean | null;
  estado?: keyof typeof EstadoUsuario | null;
  fechaCreacion?: dayjs.Dayjs | null;
  usuario?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewPerfilUsuario = Omit<IPerfilUsuario, 'id'> & { id: null };
