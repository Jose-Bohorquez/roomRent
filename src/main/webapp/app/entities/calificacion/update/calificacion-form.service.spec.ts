import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../calificacion.test-samples';

import { CalificacionFormService } from './calificacion-form.service';

describe('Calificacion Form Service', () => {
  let service: CalificacionFormService;

  beforeEach(() => {
    service = TestBed.inject(CalificacionFormService);
  });

  describe('Service methods', () => {
    describe('createCalificacionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCalificacionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            tipoCalificacion: expect.any(Object),
            puntaje: expect.any(Object),
            comentario: expect.any(Object),
            fechaCreacion: expect.any(Object),
            visible: expect.any(Object),
            autor: expect.any(Object),
            calificado: expect.any(Object),
            contrato: expect.any(Object),
          }),
        );
      });

      it('passing ICalificacion should create a new form with FormGroup', () => {
        const formGroup = service.createCalificacionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            tipoCalificacion: expect.any(Object),
            puntaje: expect.any(Object),
            comentario: expect.any(Object),
            fechaCreacion: expect.any(Object),
            visible: expect.any(Object),
            autor: expect.any(Object),
            calificado: expect.any(Object),
            contrato: expect.any(Object),
          }),
        );
      });
    });

    describe('getCalificacion', () => {
      it('should return NewCalificacion for default Calificacion initial value', () => {
        const formGroup = service.createCalificacionFormGroup(sampleWithNewData);

        const calificacion = service.getCalificacion(formGroup);

        expect(calificacion).toMatchObject(sampleWithNewData);
      });

      it('should return NewCalificacion for empty Calificacion initial value', () => {
        const formGroup = service.createCalificacionFormGroup();

        const calificacion = service.getCalificacion(formGroup);

        expect(calificacion).toMatchObject({});
      });

      it('should return ICalificacion', () => {
        const formGroup = service.createCalificacionFormGroup(sampleWithRequiredData);

        const calificacion = service.getCalificacion(formGroup);

        expect(calificacion).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICalificacion should not enable id FormControl', () => {
        const formGroup = service.createCalificacionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCalificacion should disable id FormControl', () => {
        const formGroup = service.createCalificacionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
