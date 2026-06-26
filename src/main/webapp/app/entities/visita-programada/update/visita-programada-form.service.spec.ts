import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../visita-programada.test-samples';

import { VisitaProgramadaFormService } from './visita-programada-form.service';

describe('VisitaProgramada Form Service', () => {
  let service: VisitaProgramadaFormService;

  beforeEach(() => {
    service = TestBed.inject(VisitaProgramadaFormService);
  });

  describe('Service methods', () => {
    describe('createVisitaProgramadaFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createVisitaProgramadaFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fechaSolicitada: expect.any(Object),
            fechaConfirmada: expect.any(Object),
            notas: expect.any(Object),
            estado: expect.any(Object),
            visitante: expect.any(Object),
            solicitud: expect.any(Object),
          }),
        );
      });

      it('passing IVisitaProgramada should create a new form with FormGroup', () => {
        const formGroup = service.createVisitaProgramadaFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fechaSolicitada: expect.any(Object),
            fechaConfirmada: expect.any(Object),
            notas: expect.any(Object),
            estado: expect.any(Object),
            visitante: expect.any(Object),
            solicitud: expect.any(Object),
          }),
        );
      });
    });

    describe('getVisitaProgramada', () => {
      it('should return NewVisitaProgramada for default VisitaProgramada initial value', () => {
        const formGroup = service.createVisitaProgramadaFormGroup(sampleWithNewData);

        const visitaProgramada = service.getVisitaProgramada(formGroup);

        expect(visitaProgramada).toMatchObject(sampleWithNewData);
      });

      it('should return NewVisitaProgramada for empty VisitaProgramada initial value', () => {
        const formGroup = service.createVisitaProgramadaFormGroup();

        const visitaProgramada = service.getVisitaProgramada(formGroup);

        expect(visitaProgramada).toMatchObject({});
      });

      it('should return IVisitaProgramada', () => {
        const formGroup = service.createVisitaProgramadaFormGroup(sampleWithRequiredData);

        const visitaProgramada = service.getVisitaProgramada(formGroup);

        expect(visitaProgramada).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IVisitaProgramada should not enable id FormControl', () => {
        const formGroup = service.createVisitaProgramadaFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewVisitaProgramada should disable id FormControl', () => {
        const formGroup = service.createVisitaProgramadaFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
