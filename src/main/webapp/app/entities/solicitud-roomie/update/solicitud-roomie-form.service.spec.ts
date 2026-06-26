import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../solicitud-roomie.test-samples';

import { SolicitudRoomieFormService } from './solicitud-roomie-form.service';

describe('SolicitudRoomie Form Service', () => {
  let service: SolicitudRoomieFormService;

  beforeEach(() => {
    service = TestBed.inject(SolicitudRoomieFormService);
  });

  describe('Service methods', () => {
    describe('createSolicitudRoomieFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSolicitudRoomieFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            mensaje: expect.any(Object),
            referencias: expect.any(Object),
            estado: expect.any(Object),
            fechaCreacion: expect.any(Object),
            postulante: expect.any(Object),
            publicacionRoomie: expect.any(Object),
          }),
        );
      });

      it('passing ISolicitudRoomie should create a new form with FormGroup', () => {
        const formGroup = service.createSolicitudRoomieFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            mensaje: expect.any(Object),
            referencias: expect.any(Object),
            estado: expect.any(Object),
            fechaCreacion: expect.any(Object),
            postulante: expect.any(Object),
            publicacionRoomie: expect.any(Object),
          }),
        );
      });
    });

    describe('getSolicitudRoomie', () => {
      it('should return NewSolicitudRoomie for default SolicitudRoomie initial value', () => {
        const formGroup = service.createSolicitudRoomieFormGroup(sampleWithNewData);

        const solicitudRoomie = service.getSolicitudRoomie(formGroup);

        expect(solicitudRoomie).toMatchObject(sampleWithNewData);
      });

      it('should return NewSolicitudRoomie for empty SolicitudRoomie initial value', () => {
        const formGroup = service.createSolicitudRoomieFormGroup();

        const solicitudRoomie = service.getSolicitudRoomie(formGroup);

        expect(solicitudRoomie).toMatchObject({});
      });

      it('should return ISolicitudRoomie', () => {
        const formGroup = service.createSolicitudRoomieFormGroup(sampleWithRequiredData);

        const solicitudRoomie = service.getSolicitudRoomie(formGroup);

        expect(solicitudRoomie).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISolicitudRoomie should not enable id FormControl', () => {
        const formGroup = service.createSolicitudRoomieFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSolicitudRoomie should disable id FormControl', () => {
        const formGroup = service.createSolicitudRoomieFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
