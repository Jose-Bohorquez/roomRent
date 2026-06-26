import { IInmueble, NewInmueble } from './inmueble.model';

export const sampleWithRequiredData: IInmueble = {
  id: 'd2cbdba8-11a7-4d96-9c09-dcd2d3e379e9',
  nombre: 'majestic',
  direccion: 'transparency bah',
  ciudad: 'swelter department',
  barrio: 'fatally cheap react',
  tipoInmueble: 'LOCAL',
  numeroHabitaciones: 27355,
  numeroBanos: 325,
};

export const sampleWithPartialData: IInmueble = {
  id: 'a0fbc7d8-474a-428d-8abc-bc4a63057225',
  nombre: 'trick seal',
  direccion: 'gadzooks mortar',
  ciudad: 'with excepting question',
  barrio: 'institute after',
  tipoInmueble: 'APARTAESTUDIO',
  numeroHabitaciones: 1889,
  numeroBanos: 17243,
  numeroParqueaderos: 24290,
};

export const sampleWithFullData: IInmueble = {
  id: '2a06974a-bf97-47e7-9d02-a9cc1a2f75cc',
  nombre: 'athwart',
  direccion: 'since',
  ciudad: 'behind abseil vainly',
  localidad: 'edge gripper',
  barrio: 'subdued usher',
  latitud: 22521.88,
  longitud: 28166.27,
  tipoInmueble: 'LOCAL',
  areaMetrosCuadrados: 24057.24,
  numeroHabitaciones: 2610,
  numeroBanos: 13321,
  numeroParqueaderos: 30186,
  estrato: 2,
};

export const sampleWithNewData: NewInmueble = {
  nombre: 'mill innocently',
  direccion: 'unimpressively',
  ciudad: 'as rapidly consequently',
  barrio: 'piglet disk at',
  tipoInmueble: 'APARTAESTUDIO',
  numeroHabitaciones: 15574,
  numeroBanos: 31531,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
