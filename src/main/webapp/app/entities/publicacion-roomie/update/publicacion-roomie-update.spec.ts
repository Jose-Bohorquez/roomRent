import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IInmueble } from 'app/entities/inmueble/inmueble.model';
import { InmuebleService } from 'app/entities/inmueble/service/inmueble.service';
import { IPerfilUsuario } from 'app/entities/perfil-usuario/perfil-usuario.model';
import { PerfilUsuarioService } from 'app/entities/perfil-usuario/service/perfil-usuario.service';
import { IPublicacionRoomie } from '../publicacion-roomie.model';
import { PublicacionRoomieService } from '../service/publicacion-roomie.service';

import { PublicacionRoomieFormService } from './publicacion-roomie-form.service';
import { PublicacionRoomieUpdate } from './publicacion-roomie-update';

describe('PublicacionRoomie Management Update Component', () => {
  let comp: PublicacionRoomieUpdate;
  let fixture: ComponentFixture<PublicacionRoomieUpdate>;
  let activatedRoute: ActivatedRoute;
  let publicacionRoomieFormService: PublicacionRoomieFormService;
  let publicacionRoomieService: PublicacionRoomieService;
  let perfilUsuarioService: PerfilUsuarioService;
  let inmuebleService: InmuebleService;

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

    fixture = TestBed.createComponent(PublicacionRoomieUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    publicacionRoomieFormService = TestBed.inject(PublicacionRoomieFormService);
    publicacionRoomieService = TestBed.inject(PublicacionRoomieService);
    perfilUsuarioService = TestBed.inject(PerfilUsuarioService);
    inmuebleService = TestBed.inject(InmuebleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call PerfilUsuario query and add missing value', () => {
      const publicacionRoomie: IPublicacionRoomie = { id: 'fb311192-a40d-4f42-868b-bcd3f9a52e76' };
      const arrendatario: IPerfilUsuario = { id: '3bafd714-d12e-4295-9a25-ef8755df4436' };
      publicacionRoomie.arrendatario = arrendatario;

      const perfilUsuarioCollection: IPerfilUsuario[] = [{ id: '3bafd714-d12e-4295-9a25-ef8755df4436' }];
      vitest.spyOn(perfilUsuarioService, 'query').mockReturnValue(of(new HttpResponse({ body: perfilUsuarioCollection })));
      const additionalPerfilUsuarios = [arrendatario];
      const expectedCollection: IPerfilUsuario[] = [...additionalPerfilUsuarios, ...perfilUsuarioCollection];
      vitest.spyOn(perfilUsuarioService, 'addPerfilUsuarioToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ publicacionRoomie });
      comp.ngOnInit();

      expect(perfilUsuarioService.query).toHaveBeenCalled();
      expect(perfilUsuarioService.addPerfilUsuarioToCollectionIfMissing).toHaveBeenCalledWith(
        perfilUsuarioCollection,
        ...additionalPerfilUsuarios.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.perfilUsuariosSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Inmueble query and add missing value', () => {
      const publicacionRoomie: IPublicacionRoomie = { id: 'fb311192-a40d-4f42-868b-bcd3f9a52e76' };
      const inmueble: IInmueble = { id: '543d8aac-c8b3-49b6-8d46-362c3dce8741' };
      publicacionRoomie.inmueble = inmueble;

      const inmuebleCollection: IInmueble[] = [{ id: '543d8aac-c8b3-49b6-8d46-362c3dce8741' }];
      vitest.spyOn(inmuebleService, 'query').mockReturnValue(of(new HttpResponse({ body: inmuebleCollection })));
      const additionalInmuebles = [inmueble];
      const expectedCollection: IInmueble[] = [...additionalInmuebles, ...inmuebleCollection];
      vitest.spyOn(inmuebleService, 'addInmuebleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ publicacionRoomie });
      comp.ngOnInit();

      expect(inmuebleService.query).toHaveBeenCalled();
      expect(inmuebleService.addInmuebleToCollectionIfMissing).toHaveBeenCalledWith(
        inmuebleCollection,
        ...additionalInmuebles.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.inmueblesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const publicacionRoomie: IPublicacionRoomie = { id: 'fb311192-a40d-4f42-868b-bcd3f9a52e76' };
      const arrendatario: IPerfilUsuario = { id: '3bafd714-d12e-4295-9a25-ef8755df4436' };
      publicacionRoomie.arrendatario = arrendatario;
      const inmueble: IInmueble = { id: '543d8aac-c8b3-49b6-8d46-362c3dce8741' };
      publicacionRoomie.inmueble = inmueble;

      activatedRoute.data = of({ publicacionRoomie });
      comp.ngOnInit();

      expect(comp.perfilUsuariosSharedCollection()).toContainEqual(arrendatario);
      expect(comp.inmueblesSharedCollection()).toContainEqual(inmueble);
      expect(comp.publicacionRoomie).toEqual(publicacionRoomie);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPublicacionRoomie>();
      const publicacionRoomie = { id: '453c2e6e-bf4a-4f0e-be12-a3257c9b7249' };
      vitest.spyOn(publicacionRoomieFormService, 'getPublicacionRoomie').mockReturnValue(publicacionRoomie);
      vitest.spyOn(publicacionRoomieService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ publicacionRoomie });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(publicacionRoomie);
      saveSubject.complete();

      // THEN
      expect(publicacionRoomieFormService.getPublicacionRoomie).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(publicacionRoomieService.update).toHaveBeenCalledWith(expect.objectContaining(publicacionRoomie));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPublicacionRoomie>();
      const publicacionRoomie = { id: '453c2e6e-bf4a-4f0e-be12-a3257c9b7249' };
      vitest.spyOn(publicacionRoomieFormService, 'getPublicacionRoomie').mockReturnValue({ id: null });
      vitest.spyOn(publicacionRoomieService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ publicacionRoomie: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(publicacionRoomie);
      saveSubject.complete();

      // THEN
      expect(publicacionRoomieFormService.getPublicacionRoomie).toHaveBeenCalled();
      expect(publicacionRoomieService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IPublicacionRoomie>();
      const publicacionRoomie = { id: '453c2e6e-bf4a-4f0e-be12-a3257c9b7249' };
      vitest.spyOn(publicacionRoomieService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ publicacionRoomie });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(publicacionRoomieService.update).toHaveBeenCalled();
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

    describe('compareInmueble', () => {
      it('should forward to inmuebleService', () => {
        const entity = { id: '543d8aac-c8b3-49b6-8d46-362c3dce8741' };
        const entity2 = { id: 'aac20b9f-f3ab-4368-87e6-341753a78d64' };
        vitest.spyOn(inmuebleService, 'compareInmueble');
        comp.compareInmueble(entity, entity2);
        expect(inmuebleService.compareInmueble).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
