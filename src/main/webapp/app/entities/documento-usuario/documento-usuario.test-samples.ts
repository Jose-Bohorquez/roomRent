import dayjs from 'dayjs/esm';

import { IDocumentoUsuario, NewDocumentoUsuario } from './documento-usuario.model';

export const sampleWithRequiredData: IDocumentoUsuario = {
  id: '4c5cea50-208c-46b7-85f2-98711ee8b638',
  tipoDocumento: 'TI',
  nombreDocumento: 'beside',
  urlArchivo: 'lovely unsightly object',
  fechaCarga: dayjs('2026-06-17T23:55'),
};

export const sampleWithPartialData: IDocumentoUsuario = {
  id: '0587e0cf-a7ca-456e-9857-b53f88ef67fa',
  tipoDocumento: 'PASSPORT',
  nombreDocumento: 'conservative after pushy',
  urlArchivo: 'accept scorn',
  tamanoArchivo: 28899,
  fechaCarga: dayjs('2026-06-17T09:23'),
  observaciones: '../fake-data/blob/hipster.txt',
};

export const sampleWithFullData: IDocumentoUsuario = {
  id: '38cfe1f4-e49d-421d-8ecf-bb80be7c8607',
  tipoDocumento: 'CC',
  nombreDocumento: 'pixellate',
  urlArchivo: 'brush below pleased',
  tipoMime: 'barring only dependable',
  tamanoArchivo: 14468,
  fechaCarga: dayjs('2026-06-17T14:50'),
  aprobado: false,
  observaciones: '../fake-data/blob/hipster.txt',
};

export const sampleWithNewData: NewDocumentoUsuario = {
  tipoDocumento: 'NIT',
  nombreDocumento: 'swath supposing drat',
  urlArchivo: 'destock',
  fechaCarga: dayjs('2026-06-17T16:44'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
