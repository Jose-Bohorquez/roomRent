import { IInmueble } from 'app/entities/inmueble/inmueble.model';

export interface IMultimediaInmueble {
  id: string;
  urlMedia?: string | null;
  tipoMedia?: string | null;
  principal?: boolean | null;
  titulo?: string | null;
  inmueble?: IInmueble | null;
}

export type NewMultimediaInmueble = Omit<IMultimediaInmueble, 'id'> & { id: null };
