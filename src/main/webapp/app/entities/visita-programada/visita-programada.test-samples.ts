import dayjs from 'dayjs/esm';

import { IVisitaProgramada, NewVisitaProgramada } from './visita-programada.model';

export const sampleWithRequiredData: IVisitaProgramada = {
  id: '1ce6de79-e5c1-4ed3-9355-9e06b51938f3',
  fechaSolicitada: dayjs('2026-06-17T07:25'),
  estado: 'CANCELADA',
};

export const sampleWithPartialData: IVisitaProgramada = {
  id: '4a35bc50-198c-498f-a00b-cac9a44a04bc',
  fechaSolicitada: dayjs('2026-06-17T09:54'),
  fechaConfirmada: dayjs('2026-06-17T07:14'),
  notas: '../fake-data/blob/hipster.txt',
  estado: 'SOLICITADA',
};

export const sampleWithFullData: IVisitaProgramada = {
  id: 'bfa3a072-5e39-4281-bb4d-181c6bf4a38b',
  fechaSolicitada: dayjs('2026-06-17T05:03'),
  fechaConfirmada: dayjs('2026-06-17T04:05'),
  notas: '../fake-data/blob/hipster.txt',
  estado: 'FINALIZADA',
};

export const sampleWithNewData: NewVisitaProgramada = {
  fechaSolicitada: dayjs('2026-06-17T05:17'),
  estado: 'FINALIZADA',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
