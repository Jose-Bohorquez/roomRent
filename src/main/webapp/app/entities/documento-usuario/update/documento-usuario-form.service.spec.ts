import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../documento-usuario.test-samples';

import { DocumentoUsuarioFormService } from './documento-usuario-form.service';

describe('DocumentoUsuario Form Service', () => {
  let service: DocumentoUsuarioFormService;

  beforeEach(() => {
    service = TestBed.inject(DocumentoUsuarioFormService);
  });

  describe('Service methods', () => {
    describe('createDocumentoUsuarioFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDocumentoUsuarioFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            tipoDocumento: expect.any(Object),
            nombreDocumento: expect.any(Object),
            urlArchivo: expect.any(Object),
            tipoMime: expect.any(Object),
            tamanoArchivo: expect.any(Object),
            fechaCarga: expect.any(Object),
            aprobado: expect.any(Object),
            observaciones: expect.any(Object),
            perfilUsuario: expect.any(Object),
          }),
        );
      });

      it('passing IDocumentoUsuario should create a new form with FormGroup', () => {
        const formGroup = service.createDocumentoUsuarioFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            tipoDocumento: expect.any(Object),
            nombreDocumento: expect.any(Object),
            urlArchivo: expect.any(Object),
            tipoMime: expect.any(Object),
            tamanoArchivo: expect.any(Object),
            fechaCarga: expect.any(Object),
            aprobado: expect.any(Object),
            observaciones: expect.any(Object),
            perfilUsuario: expect.any(Object),
          }),
        );
      });
    });

    describe('getDocumentoUsuario', () => {
      it('should return NewDocumentoUsuario for default DocumentoUsuario initial value', () => {
        const formGroup = service.createDocumentoUsuarioFormGroup(sampleWithNewData);

        const documentoUsuario = service.getDocumentoUsuario(formGroup);

        expect(documentoUsuario).toMatchObject(sampleWithNewData);
      });

      it('should return NewDocumentoUsuario for empty DocumentoUsuario initial value', () => {
        const formGroup = service.createDocumentoUsuarioFormGroup();

        const documentoUsuario = service.getDocumentoUsuario(formGroup);

        expect(documentoUsuario).toMatchObject({});
      });

      it('should return IDocumentoUsuario', () => {
        const formGroup = service.createDocumentoUsuarioFormGroup(sampleWithRequiredData);

        const documentoUsuario = service.getDocumentoUsuario(formGroup);

        expect(documentoUsuario).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDocumentoUsuario should not enable id FormControl', () => {
        const formGroup = service.createDocumentoUsuarioFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDocumentoUsuario should disable id FormControl', () => {
        const formGroup = service.createDocumentoUsuarioFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
