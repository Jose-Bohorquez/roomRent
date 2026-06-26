import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../publicacion-roomie.test-samples';

import { PublicacionRoomieFormService } from './publicacion-roomie-form.service';

describe('PublicacionRoomie Form Service', () => {
  let service: PublicacionRoomieFormService;

  beforeEach(() => {
    service = TestBed.inject(PublicacionRoomieFormService);
  });

  describe('Service methods', () => {
    describe('createPublicacionRoomieFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPublicacionRoomieFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titulo: expect.any(Object),
            nombreHabitacion: expect.any(Object),
            valorMensual: expect.any(Object),
            serviciosIncluidos: expect.any(Object),
            espaciosCompartidos: expect.any(Object),
            generoPreferido: expect.any(Object),
            fechaDisponible: expect.any(Object),
            estado: expect.any(Object),
            arrendatario: expect.any(Object),
            inmueble: expect.any(Object),
          }),
        );
      });

      it('passing IPublicacionRoomie should create a new form with FormGroup', () => {
        const formGroup = service.createPublicacionRoomieFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titulo: expect.any(Object),
            nombreHabitacion: expect.any(Object),
            valorMensual: expect.any(Object),
            serviciosIncluidos: expect.any(Object),
            espaciosCompartidos: expect.any(Object),
            generoPreferido: expect.any(Object),
            fechaDisponible: expect.any(Object),
            estado: expect.any(Object),
            arrendatario: expect.any(Object),
            inmueble: expect.any(Object),
          }),
        );
      });
    });

    describe('getPublicacionRoomie', () => {
      it('should return NewPublicacionRoomie for default PublicacionRoomie initial value', () => {
        const formGroup = service.createPublicacionRoomieFormGroup(sampleWithNewData);

        const publicacionRoomie = service.getPublicacionRoomie(formGroup);

        expect(publicacionRoomie).toMatchObject(sampleWithNewData);
      });

      it('should return NewPublicacionRoomie for empty PublicacionRoomie initial value', () => {
        const formGroup = service.createPublicacionRoomieFormGroup();

        const publicacionRoomie = service.getPublicacionRoomie(formGroup);

        expect(publicacionRoomie).toMatchObject({});
      });

      it('should return IPublicacionRoomie', () => {
        const formGroup = service.createPublicacionRoomieFormGroup(sampleWithRequiredData);

        const publicacionRoomie = service.getPublicacionRoomie(formGroup);

        expect(publicacionRoomie).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPublicacionRoomie should not enable id FormControl', () => {
        const formGroup = service.createPublicacionRoomieFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPublicacionRoomie should disable id FormControl', () => {
        const formGroup = service.createPublicacionRoomieFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
