import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../solicitud-arriendo.test-samples';

import { SolicitudArriendoFormService } from './solicitud-arriendo-form.service';

describe('SolicitudArriendo Form Service', () => {
  let service: SolicitudArriendoFormService;

  beforeEach(() => {
    service = TestBed.inject(SolicitudArriendoFormService);
  });

  describe('Service methods', () => {
    describe('createSolicitudArriendoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSolicitudArriendoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            mensaje: expect.any(Object),
            aceptaTerminos: expect.any(Object),
            estado: expect.any(Object),
            fechaCreacion: expect.any(Object),
            arrendatario: expect.any(Object),
            publicacion: expect.any(Object),
          }),
        );
      });

      it('passing ISolicitudArriendo should create a new form with FormGroup', () => {
        const formGroup = service.createSolicitudArriendoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            mensaje: expect.any(Object),
            aceptaTerminos: expect.any(Object),
            estado: expect.any(Object),
            fechaCreacion: expect.any(Object),
            arrendatario: expect.any(Object),
            publicacion: expect.any(Object),
          }),
        );
      });
    });

    describe('getSolicitudArriendo', () => {
      it('should return NewSolicitudArriendo for default SolicitudArriendo initial value', () => {
        const formGroup = service.createSolicitudArriendoFormGroup(sampleWithNewData);

        const solicitudArriendo = service.getSolicitudArriendo(formGroup);

        expect(solicitudArriendo).toMatchObject(sampleWithNewData);
      });

      it('should return NewSolicitudArriendo for empty SolicitudArriendo initial value', () => {
        const formGroup = service.createSolicitudArriendoFormGroup();

        const solicitudArriendo = service.getSolicitudArriendo(formGroup);

        expect(solicitudArriendo).toMatchObject({});
      });

      it('should return ISolicitudArriendo', () => {
        const formGroup = service.createSolicitudArriendoFormGroup(sampleWithRequiredData);

        const solicitudArriendo = service.getSolicitudArriendo(formGroup);

        expect(solicitudArriendo).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISolicitudArriendo should not enable id FormControl', () => {
        const formGroup = service.createSolicitudArriendoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSolicitudArriendo should disable id FormControl', () => {
        const formGroup = service.createSolicitudArriendoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
