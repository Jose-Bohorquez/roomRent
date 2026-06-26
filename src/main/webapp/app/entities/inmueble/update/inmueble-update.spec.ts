import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IPerfilUsuario } from 'app/entities/perfil-usuario/perfil-usuario.model';
import { PerfilUsuarioService } from 'app/entities/perfil-usuario/service/perfil-usuario.service';
import { IInmueble } from '../inmueble.model';
import { InmuebleService } from '../service/inmueble.service';

import { InmuebleFormService } from './inmueble-form.service';
import { InmuebleUpdate } from './inmueble-update';

describe('Inmueble Management Update Component', () => {
  let comp: InmuebleUpdate;
  let fixture: ComponentFixture<InmuebleUpdate>;
  let activatedRoute: ActivatedRoute;
  let inmuebleFormService: InmuebleFormService;
  let inmuebleService: InmuebleService;
  let perfilUsuarioService: PerfilUsuarioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(InmuebleUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    inmuebleFormService = TestBed.inject(InmuebleFormService);
    inmuebleService = TestBed.inject(InmuebleService);
    perfilUsuarioService = TestBed.inject(PerfilUsuarioService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call PerfilUsuario query and add missing value', () => {
      const inmueble: IInmueble = { id: 'aac20b9f-f3ab-4368-87e6-341753a78d64' };
      const propietario: IPerfilUsuario = { id: '3bafd714-d12e-4295-9a25-ef8755df4436' };
      inmueble.propietario = propietario;

      const perfilUsuarioCollection: IPerfilUsuario[] = [{ id: '3bafd714-d12e-4295-9a25-ef8755df4436' }];
      vitest.spyOn(perfilUsuarioService, 'query').mockReturnValue(of(new HttpResponse({ body: perfilUsuarioCollection })));
      const additionalPerfilUsuarios = [propietario];
      const expectedCollection: IPerfilUsuario[] = [...additionalPerfilUsuarios, ...perfilUsuarioCollection];
      vitest.spyOn(perfilUsuarioService, 'addPerfilUsuarioToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ inmueble });
      comp.ngOnInit();

      expect(perfilUsuarioService.query).toHaveBeenCalled();
      expect(perfilUsuarioService.addPerfilUsuarioToCollectionIfMissing).toHaveBeenCalledWith(
        perfilUsuarioCollection,
        ...additionalPerfilUsuarios.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.perfilUsuariosSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const inmueble: IInmueble = { id: 'aac20b9f-f3ab-4368-87e6-341753a78d64' };
      const propietario: IPerfilUsuario = { id: '3bafd714-d12e-4295-9a25-ef8755df4436' };
      inmueble.propietario = propietario;

      activatedRoute.data = of({ inmueble });
      comp.ngOnInit();

      expect(comp.perfilUsuariosSharedCollection()).toContainEqual(propietario);
      expect(comp.inmueble).toEqual(inmueble);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IInmueble>();
      const inmueble = { id: '543d8aac-c8b3-49b6-8d46-362c3dce8741' };
      vitest.spyOn(inmuebleFormService, 'getInmueble').mockReturnValue(inmueble);
      vitest.spyOn(inmuebleService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inmueble });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(inmueble);
      saveSubject.complete();

      // THEN
      expect(inmuebleFormService.getInmueble).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(inmuebleService.update).toHaveBeenCalledWith(expect.objectContaining(inmueble));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IInmueble>();
      const inmueble = { id: '543d8aac-c8b3-49b6-8d46-362c3dce8741' };
      vitest.spyOn(inmuebleFormService, 'getInmueble').mockReturnValue({ id: null });
      vitest.spyOn(inmuebleService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inmueble: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(inmueble);
      saveSubject.complete();

      // THEN
      expect(inmuebleFormService.getInmueble).toHaveBeenCalled();
      expect(inmuebleService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IInmueble>();
      const inmueble = { id: '543d8aac-c8b3-49b6-8d46-362c3dce8741' };
      vitest.spyOn(inmuebleService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inmueble });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(inmuebleService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePerfilUsuario', () => {
      it('should forward to perfilUsuarioService', () => {
        const entity = { id: '3bafd714-d12e-4295-9a25-ef8755df4436' };
        const entity2 = { id: '56ad7495-56cb-4b2e-a436-e5def8987d42' };
        vitest.spyOn(perfilUsuarioService, 'comparePerfilUsuario');
        comp.comparePerfilUsuario(entity, entity2);
        expect(perfilUsuarioService.comparePerfilUsuario).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
