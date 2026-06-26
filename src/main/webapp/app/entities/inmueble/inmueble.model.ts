import { TipoInmueble } from 'app/entities/enumerations/tipo-inmueble.model';
import { IPerfilUsuario } from 'app/entities/perfil-usuario/perfil-usuario.model';

export interface IInmueble {
  id: string;
  nombre?: string | null;
  direccion?: string | null;
  ciudad?: string | null;
  localidad?: string | null;
  barrio?: string | null;
  latitud?: number | null;
  longitud?: number | null;
  tipoInmueble?: keyof typeof TipoInmueble | null;
  areaMetrosCuadrados?: number | null;
  numeroHabitaciones?: number | null;
  numeroBanos?: number | null;
  numeroParqueaderos?: number | null;
  estrato?: number | null;
  propietario?: IPerfilUsuario | null;
}

export type NewInmueble = Omit<IInmueble, 'id'> & { id: null };
