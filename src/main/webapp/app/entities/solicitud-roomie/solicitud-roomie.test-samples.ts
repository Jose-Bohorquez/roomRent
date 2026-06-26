import dayjs from 'dayjs/esm';

import { ISolicitudRoomie, NewSolicitudRoomie } from './solicitud-roomie.model';

export const sampleWithRequiredData: ISolicitudRoomie = {
  id: '03a50a5e-847f-4566-846a-5e8e55b23c25',
  estado: 'APROBADA',
  fechaCreacion: dayjs('2026-06-17T10:45'),
};

export const sampleWithPartialData: ISolicitudRoomie = {
  id: '5c3684c3-f893-431d-bf8c-25284474a524',
  estado: 'CANCELADA',
  fechaCreacion: dayjs('2026-06-17T14:37'),
};

export const sampleWithFullData: ISolicitudRoomie = {
  id: '417b135a-0456-417b-8d96-a8a74ddff931',
  mensaje: '../fake-data/blob/hipster.txt',
  referencias: '../fake-data/blob/hipster.txt',
  estado: 'APROBADA',
  fechaCreacion: dayjs('2026-06-17T18:32'),
};

export const sampleWithNewData: NewSolicitudRoomie = {
  estado: 'RECHAZADA',
  fechaCreacion: dayjs('2026-06-17T10:37'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
