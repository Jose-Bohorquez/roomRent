import dayjs from 'dayjs/esm';

import { IContratoArriendo, NewContratoArriendo } from './contrato-arriendo.model';

export const sampleWithRequiredData: IContratoArriendo = {
  id: '7e20ce35-52cc-466a-8dda-76e37104d1f8',
  numeroContrato: 'telescope huzzah silent',
  fechaInicio: dayjs('2026-06-17'),
  fechaFin: dayjs('2026-06-17'),
  valorMensual: 27312,
  estado: 'CANCELADO',
};

export const sampleWithPartialData: IContratoArriendo = {
  id: '2fd4d993-d836-427e-a3f6-4b86aef53714',
  numeroContrato: 'pack',
  fechaInicio: dayjs('2026-06-17'),
  fechaFin: dayjs('2026-06-17'),
  valorMensual: 30702,
  valorDeposito: 27981,
  estado: 'VIGENTE',
};

export const sampleWithFullData: IContratoArriendo = {
  id: '91fdcbec-3a4a-4b88-a7c3-356c0e1bb615',
  numeroContrato: 'busily hm modulo',
  urlContratoDigital: 'whereas next',
  fechaInicio: dayjs('2026-06-17'),
  fechaFin: dayjs('2026-06-18'),
  valorMensual: 5541,
  valorDeposito: 953,
  estado: 'BORRADOR',
  fechaFirma: dayjs('2026-06-17T02:21'),
};

export const sampleWithNewData: NewContratoArriendo = {
  numeroContrato: 'ew',
  fechaInicio: dayjs('2026-06-17'),
  fechaFin: dayjs('2026-06-17'),
  valorMensual: 11797,
  estado: 'CANCELADO',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
