import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../multimedia-inmueble.test-samples';

import { MultimediaInmuebleFormService } from './multimedia-inmueble-form.service';

describe('MultimediaInmueble Form Service', () => {
  let service: MultimediaInmuebleFormService;

  beforeEach(() => {
    service = TestBed.inject(MultimediaInmuebleFormService);
  });

  describe('Service methods', () => {
    describe('createMultimediaInmuebleFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMultimediaInmuebleFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            urlMedia: expect.any(Object),
            tipoMedia: expect.any(Object),
            principal: expect.any(Object),
            titulo: expect.any(Object),
            inmueble: expect.any(Object),
          }),
        );
      });

      it('passing IMultimediaInmueble should create a new form with FormGroup', () => {
        const formGroup = service.createMultimediaInmuebleFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            urlMedia: expect.any(Object),
            tipoMedia: expect.any(Object),
            principal: expect.any(Object),
            titulo: expect.any(Object),
            inmueble: expect.any(Object),
          }),
        );
      });
    });

    describe('getMultimediaInmueble', () => {
      it('should return NewMultimediaInmueble for default MultimediaInmueble initial value', () => {
        const formGroup = service.createMultimediaInmuebleFormGroup(sampleWithNewData);

        const multimediaInmueble = service.getMultimediaInmueble(formGroup);

        expect(multimediaInmueble).toMatchObject(sampleWithNewData);
      });

      it('should return NewMultimediaInmueble for empty MultimediaInmueble initial value', () => {
        const formGroup = service.createMultimediaInmuebleFormGroup();

        const multimediaInmueble = service.getMultimediaInmueble(formGroup);

        expect(multimediaInmueble).toMatchObject({});
      });

      it('should return IMultimediaInmueble', () => {
        const formGroup = service.createMultimediaInmuebleFormGroup(sampleWithRequiredData);

        const multimediaInmueble = service.getMultimediaInmueble(formGroup);

        expect(multimediaInmueble).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMultimediaInmueble should not enable id FormControl', () => {
        const formGroup = service.createMultimediaInmuebleFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMultimediaInmueble should disable id FormControl', () => {
        const formGroup = service.createMultimediaInmuebleFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
