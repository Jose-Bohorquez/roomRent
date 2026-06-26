import dayjs from 'dayjs/esm';

import { ICalificacion, NewCalificacion } from './calificacion.model';

export const sampleWithRequiredData: ICalificacion = {
  id: '8c6d4fc6-e43d-4295-8727-7d0244902699',
  tipoCalificacion: 'ARRENDATARIO_A_ROOMIE',
  puntaje: 1,
  fechaCreacion: dayjs('2026-06-18T00:14'),
  visible: false,
};

export const sampleWithPartialData: ICalificacion = {
  id: 'e0914d1d-83c4-44ef-8045-9726809f6ba9',
  tipoCalificacion: 'ARRENDATARIO_A_ARRENDADOR',
  puntaje: 3,
  fechaCreacion: dayjs('2026-06-17T17:14'),
  visible: false,
};

export const sampleWithFullData: ICalificacion = {
  id: 'c6cf0cf2-5725-406c-a422-5fedfbe88d0f',
  tipoCalificacion: 'ROOMIE_A_ARRENDATARIO',
  puntaje: 3,
  comentario: '../fake-data/blob/hipster.txt',
  fechaCreacion: dayjs('2026-06-17T07:05'),
  visible: true,
};

export const sampleWithNewData: NewCalificacion = {
  tipoCalificacion: 'ARRENDADOR_A_ARRENDATARIO',
  puntaje: 5,
  fechaCreacion: dayjs('2026-06-17T22:18'),
  visible: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
