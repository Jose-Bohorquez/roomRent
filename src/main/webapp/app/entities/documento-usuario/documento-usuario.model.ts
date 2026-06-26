import dayjs from 'dayjs/esm';

import { TipoDocumento } from 'app/entities/enumerations/tipo-documento.model';
import { IPerfilUsuario } from 'app/entities/perfil-usuario/perfil-usuario.model';

export interface IDocumentoUsuario {
  id: string;
  tipoDocumento?: keyof typeof TipoDocumento | null;
  nombreDocumento?: string | null;
  urlArchivo?: string | null;
  tipoMime?: string | null;
  tamanoArchivo?: number | null;
  fechaCarga?: dayjs.Dayjs | null;
  aprobado?: boolean | null;
  observaciones?: string | null;
  perfilUsuario?: IPerfilUsuario | null;
}

export type NewDocumentoUsuario = Omit<IDocumentoUsuario, 'id'> & { id: null };
