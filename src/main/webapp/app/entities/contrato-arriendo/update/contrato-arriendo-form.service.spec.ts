import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../contrato-arriendo.test-samples';

import { ContratoArriendoFormService } from './contrato-arriendo-form.service';

describe('ContratoArriendo Form Service', () => {
  let service: ContratoArriendoFormService;

  beforeEach(() => {
    service = TestBed.inject(ContratoArriendoFormService);
  });

  describe('Service methods', () => {
    describe('createContratoArriendoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createContratoArriendoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            numeroContrato: expect.any(Object),
            urlContratoDigital: expect.any(Object),
            fechaInicio: expect.any(Object),
            fechaFin: expect.any(Object),
            valorMensual: expect.any(Object),
            valorDeposito: expect.any(Object),
            estado: expect.any(Object),
            fechaFirma: expect.any(Object),
            arrendador: expect.any(Object),
            arrendatario: expect.any(Object),
            inmueble: expect.any(Object),
          }),
        );
      });

      it('passing IContratoArriendo should create a new form with FormGroup', () => {
        const formGroup = service.createContratoArriendoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            numeroContrato: expect.any(Object),
            urlContratoDigital: expect.any(Object),
            fechaInicio: expect.any(Object),
            fechaFin: expect.any(Object),
            valorMensual: expect.any(Object),
            valorDeposito: expect.any(Object),
            estado: expect.any(Object),
            fechaFirma: expect.any(Object),
            arrendador: expect.any(Object),
            arrendatario: expect.any(Object),
            inmueble: expect.any(Object),
          }),
        );
      });
    });

    describe('getContratoArriendo', () => {
      it('should return NewContratoArriendo for default ContratoArriendo initial value', () => {
        const formGroup = service.createContratoArriendoFormGroup(sampleWithNewData);

        const contratoArriendo = service.getContratoArriendo(formGroup);

        expect(contratoArriendo).toMatchObject(sampleWithNewData);
      });

      it('should return NewContratoArriendo for empty ContratoArriendo initial value', () => {
        const formGroup = service.createContratoArriendoFormGroup();

        const contratoArriendo = service.getContratoArriendo(formGroup);

        expect(contratoArriendo).toMatchObject({});
      });

      it('should return IContratoArriendo', () => {
        const formGroup = service.createContratoArriendoFormGroup(sampleWithRequiredData);

        const contratoArriendo = service.getContratoArriendo(formGroup);

        expect(contratoArriendo).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IContratoArriendo should not enable id FormControl', () => {
        const formGroup = service.createContratoArriendoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewContratoArriendo should disable id FormControl', () => {
        const formGroup = service.createContratoArriendoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
