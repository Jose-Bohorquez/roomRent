import dayjs from 'dayjs/esm';

import { IPublicacionRoomie, NewPublicacionRoomie } from './publicacion-roomie.model';

export const sampleWithRequiredData: IPublicacionRoomie = {
  id: '7f524200-37ba-4186-a52c-869a3072bb14',
  titulo: 'so unlike trivial',
  nombreHabitacion: 'charm zowie or',
  valorMensual: 24593,
  estado: 'FINALIZADO',
};

export const sampleWithPartialData: IPublicacionRoomie = {
  id: '6babc079-d760-487e-9928-02bfcf5a0114',
  titulo: 'gosh consequently er',
  nombreHabitacion: 'huzzah remorseful lazy',
  valorMensual: 29985,
  estado: 'PAUSADO',
};

export const sampleWithFullData: IPublicacionRoomie = {
  id: 'bf0c8aad-5bef-4cee-bc8d-f6c684f9b510',
  titulo: 'until',
  nombreHabitacion: 'mockingly an commercial',
  valorMensual: 24867,
  serviciosIncluidos: '../fake-data/blob/hipster.txt',
  espaciosCompartidos: '../fake-data/blob/hipster.txt',
  generoPreferido: 'FEMENINO',
  fechaDisponible: dayjs('2026-06-17'),
  estado: 'ARRENDADO',
};

export const sampleWithNewData: NewPublicacionRoomie = {
  titulo: 'solicit',
  nombreHabitacion: 'inure request sorrowful',
  valorMensual: 25669,
  estado: 'PUBLICADO',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
