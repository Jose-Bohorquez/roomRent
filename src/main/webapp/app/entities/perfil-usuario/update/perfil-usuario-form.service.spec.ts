import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../perfil-usuario.test-samples';

import { PerfilUsuarioFormService } from './perfil-usuario-form.service';

describe('PerfilUsuario Form Service', () => {
  let service: PerfilUsuarioFormService;

  beforeEach(() => {
    service = TestBed.inject(PerfilUsuarioFormService);
  });

  describe('Service methods', () => {
    describe('createPerfilUsuarioFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPerfilUsuarioFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            tipoDocumento: expect.any(Object),
            numeroDocumento: expect.any(Object),
            primerNombre: expect.any(Object),
            segundoNombre: expect.any(Object),
            primerApellido: expect.any(Object),
            segundoApellido: expect.any(Object),
            fechaNacimiento: expect.any(Object),
            genero: expect.any(Object),
            telefono: expect.any(Object),
            direccionActual: expect.any(Object),
            ciudad: expect.any(Object),
            barrio: expect.any(Object),
            profesion: expect.any(Object),
            ocupacion: expect.any(Object),
            empresaTrabajo: expect.any(Object),
            universidad: expect.any(Object),
            biografia: expect.any(Object),
            intereses: expect.any(Object),
            tieneMascotas: expect.any(Object),
            fumador: expect.any(Object),
            verificado: expect.any(Object),
            habilitadoRoomie: expect.any(Object),
            estado: expect.any(Object),
            fechaCreacion: expect.any(Object),
            usuario: expect.any(Object),
          }),
        );
      });

      it('passing IPerfilUsuario should create a new form with FormGroup', () => {
        const formGroup = service.createPerfilUsuarioFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            tipoDocumento: expect.any(Object),
            numeroDocumento: expect.any(Object),
            primerNombre: expect.any(Object),
            segundoNombre: expect.any(Object),
            primerApellido: expect.any(Object),
            segundoApellido: expect.any(Object),
            fechaNacimiento: expect.any(Object),
            genero: expect.any(Object),
            telefono: expect.any(Object),
            direccionActual: expect.any(Object),
            ciudad: expect.any(Object),
            barrio: expect.any(Object),
            profesion: expect.any(Object),
            ocupacion: expect.any(Object),
            empresaTrabajo: expect.any(Object),
            universidad: expect.any(Object),
            biografia: expect.any(Object),
            intereses: expect.any(Object),
            tieneMascotas: expect.any(Object),
            fumador: expect.any(Object),
            verificado: expect.any(Object),
            habilitadoRoomie: expect.any(Object),
            estado: expect.any(Object),
            fechaCreacion: expect.any(Object),
            usuario: expect.any(Object),
          }),
        );
      });
    });

    describe('getPerfilUsuario', () => {
      it('should return NewPerfilUsuario for default PerfilUsuario initial value', () => {
        const formGroup = service.createPerfilUsuarioFormGroup(sampleWithNewData);

        const perfilUsuario = service.getPerfilUsuario(formGroup);

        expect(perfilUsuario).toMatchObject(sampleWithNewData);
      });

      it('should return NewPerfilUsuario for empty PerfilUsuario initial value', () => {
        const formGroup = service.createPerfilUsuarioFormGroup();

        const perfilUsuario = service.getPerfilUsuario(formGroup);

        expect(perfilUsuario).toMatchObject({});
      });

      it('should return IPerfilUsuario', () => {
        const formGroup = service.createPerfilUsuarioFormGroup(sampleWithRequiredData);

        const perfilUsuario = service.getPerfilUsuario(formGroup);

        expect(perfilUsuario).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPerfilUsuario should not enable id FormControl', () => {
        const formGroup = service.createPerfilUsuarioFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPerfilUsuario should disable id FormControl', () => {
        const formGroup = service.createPerfilUsuarioFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
