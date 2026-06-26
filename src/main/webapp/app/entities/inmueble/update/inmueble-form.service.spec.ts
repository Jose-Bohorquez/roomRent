import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../inmueble.test-samples';

import { InmuebleFormService } from './inmueble-form.service';

describe('Inmueble Form Service', () => {
  let service: InmuebleFormService;

  beforeEach(() => {
    service = TestBed.inject(InmuebleFormService);
  });

  describe('Service methods', () => {
    describe('createInmuebleFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createInmuebleFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombre: expect.any(Object),
            direccion: expect.any(Object),
            ciudad: expect.any(Object),
            localidad: expect.any(Object),
            barrio: expect.any(Object),
            latitud: expect.any(Object),
            longitud: expect.any(Object),
            tipoInmueble: expect.any(Object),
            areaMetrosCuadrados: expect.any(Object),
            numeroHabitaciones: expect.any(Object),
            numeroBanos: expect.any(Object),
            numeroParqueaderos: expect.any(Object),
            estrato: expect.any(Object),
            propietario: expect.any(Object),
          }),
        );
      });

      it('passing IInmueble should create a new form with FormGroup', () => {
        const formGroup = service.createInmuebleFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombre: expect.any(Object),
            direccion: expect.any(Object),
            ciudad: expect.any(Object),
            localidad: expect.any(Object),
            barrio: expect.any(Object),
            latitud: expect.any(Object),
            longitud: expect.any(Object),
            tipoInmueble: expect.any(Object),
            areaMetrosCuadrados: expect.any(Object),
            numeroHabitaciones: expect.any(Object),
            numeroBanos: expect.any(Object),
            numeroParqueaderos: expect.any(Object),
            estrato: expect.any(Object),
            propietario: expect.any(Object),
          }),
        );
      });
    });

    describe('getInmueble', () => {
      it('should return NewInmueble for default Inmueble initial value', () => {
        const formGroup = service.createInmuebleFormGroup(sampleWithNewData);

        const inmueble = service.getInmueble(formGroup);

        expect(inmueble).toMatchObject(sampleWithNewData);
      });

      it('should return NewInmueble for empty Inmueble initial value', () => {
        const formGroup = service.createInmuebleFormGroup();

        const inmueble = service.getInmueble(formGroup);

        expect(inmueble).toMatchObject({});
      });

      it('should return IInmueble', () => {
        const formGroup = service.createInmuebleFormGroup(sampleWithRequiredData);

        const inmueble = service.getInmueble(formGroup);

        expect(inmueble).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IInmueble should not enable id FormControl', () => {
        const formGroup = service.createInmuebleFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewInmueble should disable id FormControl', () => {
        const formGroup = service.createInmuebleFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
