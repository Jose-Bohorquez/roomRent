import dayjs from 'dayjs/esm';

import { ISolicitudArriendo, NewSolicitudArriendo } from './solicitud-arriendo.model';

export const sampleWithRequiredData: ISolicitudArriendo = {
  id: 'ac4050e4-7041-4158-b634-3b82da3fd766',
  aceptaTerminos: false,
  estado: 'RECHAZADA',
  fechaCreacion: dayjs('2026-06-17T16:25'),
};

export const sampleWithPartialData: ISolicitudArriendo = {
  id: '45aa4f8f-703e-4121-8fe2-8475dd6af998',
  mensaje: '../fake-data/blob/hipster.txt',
  aceptaTerminos: false,
  estado: 'RECHAZADA',
  fechaCreacion: dayjs('2026-06-17T14:53'),
};

export const sampleWithFullData: ISolicitudArriendo = {
  id: 'f84611bc-0b2d-44d1-94d2-54d6be6c1703',
  mensaje: '../fake-data/blob/hipster.txt',
  aceptaTerminos: false,
  estado: 'RECHAZADA',
  fechaCreacion: dayjs('2026-06-17T04:37'),
};

export const sampleWithNewData: NewSolicitudArriendo = {
  aceptaTerminos: true,
  estado: 'APROBADA',
  fechaCreacion: dayjs('2026-06-17T08:31'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
