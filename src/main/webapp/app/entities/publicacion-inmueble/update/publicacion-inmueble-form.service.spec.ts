import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../publicacion-inmueble.test-samples';

import { PublicacionInmuebleFormService } from './publicacion-inmueble-form.service';

describe('PublicacionInmueble Form Service', () => {
  let service: PublicacionInmuebleFormService;

  beforeEach(() => {
    service = TestBed.inject(PublicacionInmuebleFormService);
  });

  describe('Service methods', () => {
    describe('createPublicacionInmuebleFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPublicacionInmuebleFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titulo: expect.any(Object),
            descripcion: expect.any(Object),
            canonArriendo: expect.any(Object),
            deposito: expect.any(Object),
            requisitos: expect.any(Object),
            seguroRequerido: expect.any(Object),
            datacreditoRequerido: expect.any(Object),
            fechaDisponible: expect.any(Object),
            estado: expect.any(Object),
            permiteRoomies: expect.any(Object),
            aceptaMascotas: expect.any(Object),
            permiteFumadores: expect.any(Object),
            permiteNinos: expect.any(Object),
            permiteVisitas: expect.any(Object),
            permiteParejas: expect.any(Object),
            inmueble: expect.any(Object),
          }),
        );
      });

      it('passing IPublicacionInmueble should create a new form with FormGroup', () => {
        const formGroup = service.createPublicacionInmuebleFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titulo: expect.any(Object),
            descripcion: expect.any(Object),
            canonArriendo: expect.any(Object),
            deposito: expect.any(Object),
            requisitos: expect.any(Object),
            seguroRequerido: expect.any(Object),
            datacreditoRequerido: expect.any(Object),
            fechaDisponible: expect.any(Object),
            estado: expect.any(Object),
            permiteRoomies: expect.any(Object),
            aceptaMascotas: expect.any(Object),
            permiteFumadores: expect.any(Object),
            permiteNinos: expect.any(Object),
            permiteVisitas: expect.any(Object),
            permiteParejas: expect.any(Object),
            inmueble: expect.any(Object),
          }),
        );
      });
    });

    describe('getPublicacionInmueble', () => {
      it('should return NewPublicacionInmueble for default PublicacionInmueble initial value', () => {
        const formGroup = service.createPublicacionInmuebleFormGroup(sampleWithNewData);

        const publicacionInmueble = service.getPublicacionInmueble(formGroup);

        expect(publicacionInmueble).toMatchObject(sampleWithNewData);
      });

      it('should return NewPublicacionInmueble for empty PublicacionInmueble initial value', () => {
        const formGroup = service.createPublicacionInmuebleFormGroup();

        const publicacionInmueble = service.getPublicacionInmueble(formGroup);

        expect(publicacionInmueble).toMatchObject({});
      });

      it('should return IPublicacionInmueble', () => {
        const formGroup = service.createPublicacionInmuebleFormGroup(sampleWithRequiredData);

        const publicacionInmueble = service.getPublicacionInmueble(formGroup);

        expect(publicacionInmueble).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPublicacionInmueble should not enable id FormControl', () => {
        const formGroup = service.createPublicacionInmuebleFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPublicacionInmueble should disable id FormControl', () => {
        const formGroup = service.createPublicacionInmuebleFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
