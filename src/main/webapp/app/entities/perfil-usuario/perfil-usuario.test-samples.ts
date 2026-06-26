import dayjs from 'dayjs/esm';

import { IPerfilUsuario, NewPerfilUsuario } from './perfil-usuario.model';

export const sampleWithRequiredData: IPerfilUsuario = {
  id: '72625b6d-40dc-4477-85c8-66ecf4648685',
  tipoDocumento: 'CE',
  numeroDocumento: 'minister',
  primerNombre: 'breakable sensitize glorious',
  primerApellido: 'plus truly snow',
  fechaNacimiento: dayjs('2026-06-17'),
  telefono: 'overdub hoarse',
  ciudad: 'by beside instead',
  verificado: false,
  habilitadoRoomie: false,
  estado: 'PENDIENTE_VERIFICACION',
  fechaCreacion: dayjs('2026-06-17T08:38'),
};

export const sampleWithPartialData: IPerfilUsuario = {
  id: 'a78346d7-e2ec-4e5f-aebd-57dfc8ee4402',
  tipoDocumento: 'CC',
  numeroDocumento: 'kookily',
  primerNombre: 'geez boo sign',
  primerApellido: 'silently',
  fechaNacimiento: dayjs('2026-06-17'),
  telefono: 'advanced numeracy unlearn',
  direccionActual: 'gosh',
  ciudad: 'instead across nutritious',
  barrio: 'amongst best-seller',
  profesion: 'about well-to-do',
  ocupacion: 'insolence enormously',
  intereses: '../fake-data/blob/hipster.txt',
  verificado: false,
  habilitadoRoomie: true,
  estado: 'BLOQUEADO',
  fechaCreacion: dayjs('2026-06-17T08:07'),
};

export const sampleWithFullData: IPerfilUsuario = {
  id: '0e55a238-de61-450a-820b-792611957562',
  tipoDocumento: 'CC',
  numeroDocumento: 'um',
  primerNombre: 'duh patiently',
  segundoNombre: 'book strident pish',
  primerApellido: 'hutch tragic acceptable',
  segundoApellido: 'when',
  fechaNacimiento: dayjs('2026-06-17'),
  genero: 'FEMENINO',
  telefono: 'what gallivant piglet',
  direccionActual: 'likewise where',
  ciudad: 'pear beneath fatally',
  barrio: 'reproach scarcely',
  profesion: 'though incidentally next',
  ocupacion: 'whistle',
  empresaTrabajo: 'true some',
  universidad: 'psst over',
  biografia: '../fake-data/blob/hipster.txt',
  intereses: '../fake-data/blob/hipster.txt',
  tieneMascotas: true,
  fumador: false,
  verificado: true,
  habilitadoRoomie: false,
  estado: 'BANEADO',
  fechaCreacion: dayjs('2026-06-17T18:09'),
};

export const sampleWithNewData: NewPerfilUsuario = {
  tipoDocumento: 'OTRO',
  numeroDocumento: 'govern vacantly',
  primerNombre: 'design probate',
  primerApellido: 'fondly stay',
  fechaNacimiento: dayjs('2026-06-17'),
  telefono: 'tuba despite absent',
  ciudad: 'over',
  verificado: true,
  habilitadoRoomie: true,
  estado: 'ACTIVO',
  fechaCreacion: dayjs('2026-06-17T09:13'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
