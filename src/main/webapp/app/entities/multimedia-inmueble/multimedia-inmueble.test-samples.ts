import { IMultimediaInmueble, NewMultimediaInmueble } from './multimedia-inmueble.model';

export const sampleWithRequiredData: IMultimediaInmueble = {
  id: 'e7cb0019-8af7-400a-9fa5-aa372fc3493c',
  urlMedia: 'minor eek aha',
  tipoMedia: 'hmph',
  principal: true,
};

export const sampleWithPartialData: IMultimediaInmueble = {
  id: '56032a8b-f290-404b-b44a-0c6b62ff6677',
  urlMedia: 'wasteful everlasting',
  tipoMedia: 'pop',
  principal: false,
  titulo: 'where brr',
};

export const sampleWithFullData: IMultimediaInmueble = {
  id: 'c78e9d32-d97d-4a12-9aa5-9fda2feabee0',
  urlMedia: 'concerning',
  tipoMedia: 'brr modulo futon',
  principal: false,
  titulo: 'sheepishly',
};

export const sampleWithNewData: NewMultimediaInmueble = {
  urlMedia: 'aboard joyously',
  tipoMedia: 'judicious once uselessly',
  principal: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
