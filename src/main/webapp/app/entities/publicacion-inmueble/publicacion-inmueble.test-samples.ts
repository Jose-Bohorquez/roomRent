import dayjs from 'dayjs/esm';

import { IPublicacionInmueble, NewPublicacionInmueble } from './publicacion-inmueble.model';

export const sampleWithRequiredData: IPublicacionInmueble = {
  id: '834d42c7-e5a2-4bd6-be46-96d2c5848dbf',
  titulo: 'bah or',
  canonArriendo: 3058,
  estado: 'PAUSADO',
  permiteRoomies: true,
  aceptaMascotas: true,
  permiteFumadores: true,
  permiteNinos: true,
  permiteVisitas: true,
  permiteParejas: false,
};

export const sampleWithPartialData: IPublicacionInmueble = {
  id: 'd4c48c90-9586-425a-85fa-75bed8cc82ec',
  titulo: 'exasperation',
  canonArriendo: 26472,
  deposito: 2598,
  datacreditoRequerido: false,
  fechaDisponible: dayjs('2026-06-17'),
  estado: 'FINALIZADO',
  permiteRoomies: true,
  aceptaMascotas: true,
  permiteFumadores: false,
  permiteNinos: true,
  permiteVisitas: true,
  permiteParejas: true,
};

export const sampleWithFullData: IPublicacionInmueble = {
  id: '220d304c-2cbf-4e48-a3a5-482cc9480b68',
  titulo: 'aw obesity',
  descripcion: '../fake-data/blob/hipster.txt',
  canonArriendo: 19824,
  deposito: 17325,
  requisitos: '../fake-data/blob/hipster.txt',
  seguroRequerido: true,
  datacreditoRequerido: false,
  fechaDisponible: dayjs('2026-06-17'),
  estado: 'PAUSADO',
  permiteRoomies: true,
  aceptaMascotas: false,
  permiteFumadores: false,
  permiteNinos: true,
  permiteVisitas: true,
  permiteParejas: false,
};

export const sampleWithNewData: NewPublicacionInmueble = {
  titulo: 'chapel accurate',
  canonArriendo: 7119,
  estado: 'BORRADOR',
  permiteRoomies: true,
  aceptaMascotas: true,
  permiteFumadores: true,
  permiteNinos: false,
  permiteVisitas: true,
  permiteParejas: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
