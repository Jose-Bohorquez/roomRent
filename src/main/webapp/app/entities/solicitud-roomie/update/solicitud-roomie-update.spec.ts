import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IPerfilUsuario } from 'app/entities/perfil-usuario/perfil-usuario.model';
import { PerfilUsuarioService } from 'app/entities/perfil-usuario/service/perfil-usuario.service';
import { IPublicacionRoomie } from 'app/entities/publicacion-roomie/publicacion-roomie.model';
import { PublicacionRoomieService } from 'app/entities/publicacion-roomie/service/publicacion-roomie.service';
import { SolicitudRoomieService } from '../service/solicitud-roomie.service';
import { ISolicitudRoomie } from '../solicitud-roomie.model';

import { SolicitudRoomieFormService } from './solicitud-roomie-form.service';
import { SolicitudRoomieUpdate } from './solicitud-roomie-update';

describe('SolicitudRoomie Management Update Component', () => {
  let comp: SolicitudRoomieUpdate;
  let fixture: ComponentFixture<SolicitudRoomieUpdate>;
  let activatedRoute: ActivatedRoute;
  let solicitudRoomieFormService: SolicitudRoomieFormService;
  let solicitudRoomieService: SolicitudRoomieService;
  let perfilUsuarioService: PerfilUsuarioService;
  let publicacionRoomieService: PublicacionRoomieService;

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

    fixture = TestBed.createComponent(SolicitudRoomieUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    solicitudRoomieFormService = TestBed.inject(SolicitudRoomieFormService);
    solicitudRoomieService = TestBed.inject(SolicitudRoomieService);
    perfilUsuarioService = TestBed.inject(PerfilUsuarioService);
    publicacionRoomieService = TestBed.inject(PublicacionRoomieService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call PerfilUsuario query and add missing value', () => {
      const solicitudRoomie: ISolicitudRoomie = { id: 'd94206a4-850f-42fb-9e4d-098c9fc04793' };
      const postulante: IPerfilUsuario = { id: '3bafd714-d12e-4295-9a25-ef8755df4436' };
      solicitudRoomie.postulante = postulante;

      const perfilUsuarioCollection: IPerfilUsuario[] = [{ id: '3bafd714-d12e-4295-9a25-ef8755df4436' }];
      vitest.spyOn(perfilUsuarioService, 'query').mockReturnValue(of(new HttpResponse({ body: perfilUsuarioCollection })));
      const additionalPerfilUsuarios = [postulante];
      const expectedCollection: IPerfilUsuario[] = [...additionalPerfilUsuarios, ...perfilUsuarioCollection];
      vitest.spyOn(perfilUsuarioService, 'addPerfilUsuarioToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ solicitudRoomie });
      comp.ngOnInit();

      expect(perfilUsuarioService.query).toHaveBeenCalled();
      expect(perfilUsuarioService.addPerfilUsuarioToCollectionIfMissing).toHaveBeenCalledWith(
        perfilUsuarioCollection,
        ...additionalPerfilUsuarios.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.perfilUsuariosSharedCollection()).toEqual(expectedCollection);
    });

    it('should call PublicacionRoomie query and add missing value', () => {
      const solicitudRoomie: ISolicitudRoomie = { id: 'd94206a4-850f-42fb-9e4d-098c9fc04793' };
      const publicacionRoomie: IPublicacionRoomie = { id: '453c2e6e-bf4a-4f0e-be12-a3257c9b7249' };
      solicitudRoomie.publicacionRoomie = publicacionRoomie;

      const publicacionRoomieCollection: IPublicacionRoomie[] = [{ id: '453c2e6e-bf4a-4f0e-be12-a3257c9b7249' }];
      vitest.spyOn(publicacionRoomieService, 'query').mockReturnValue(of(new HttpResponse({ body: publicacionRoomieCollection })));
      const additionalPublicacionRoomies = [publicacionRoomie];
      const expectedCollection: IPublicacionRoomie[] = [...additionalPublicacionRoomies, ...publicacionRoomieCollection];
      vitest.spyOn(publicacionRoomieService, 'addPublicacionRoomieToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ solicitudRoomie });
      comp.ngOnInit();

      expect(publicacionRoomieService.query).toHaveBeenCalled();
      expect(publicacionRoomieService.addPublicacionRoomieToCollectionIfMissing).toHaveBeenCalledWith(
        publicacionRoomieCollection,
        ...additionalPublicacionRoomies.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.publicacionRoomiesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const solicitudRoomie: ISolicitudRoomie = { id: 'd94206a4-850f-42fb-9e4d-098c9fc04793' };
      const postulante: IPerfilUsuario = { id: '3bafd714-d12e-4295-9a25-ef8755df4436' };
      solicitudRoomie.postulante = postulante;
      const publicacionRoomie: IPublicacionRoomie = { id: '453c2e6e-bf4a-4f0e-be12-a3257c9b7249' };
      solicitudRoomie.publicacionRoomie = publicacionRoomie;

      activatedRoute.data = of({ solicitudRoomie });
      comp.ngOnInit();

      expect(comp.perfilUsuariosSharedCollection()).toContainEqual(postulante);
      expect(comp.publicacionRoomiesSharedCollection()).toContainEqual(publicacionRoomie);
      expect(comp.solicitudRoomie).toEqual(solicitudRoomie);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ISolicitudRoomie>();
      const solicitudRoomie = { id: '7323fa9a-b571-4220-b2ec-5732900ce29a' };
      vitest.spyOn(solicitudRoomieFormService, 'getSolicitudRoomie').mockReturnValue(solicitudRoomie);
      vitest.spyOn(solicitudRoomieService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ solicitudRoomie });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(solicitudRoomie);
      saveSubject.complete();

      // THEN
      expect(solicitudRoomieFormService.getSolicitudRoomie).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(solicitudRoomieService.update).toHaveBeenCalledWith(expect.objectContaining(solicitudRoomie));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ISolicitudRoomie>();
      const solicitudRoomie = { id: '7323fa9a-b571-4220-b2ec-5732900ce29a' };
      vitest.spyOn(solicitudRoomieFormService, 'getSolicitudRoomie').mockReturnValue({ id: null });
      vitest.spyOn(solicitudRoomieService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ solicitudRoomie: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(solicitudRoomie);
      saveSubject.complete();

      // THEN
      expect(solicitudRoomieFormService.getSolicitudRoomie).toHaveBeenCalled();
      expect(solicitudRoomieService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ISolicitudRoomie>();
      const solicitudRoomie = { id: '7323fa9a-b571-4220-b2ec-5732900ce29a' };
      vitest.spyOn(solicitudRoomieService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ solicitudRoomie });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(solicitudRoomieService.update).toHaveBeenCalled();
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

    describe('comparePublicacionRoomie', () => {
      it('should forward to publicacionRoomieService', () => {
        const entity = { id: '453c2e6e-bf4a-4f0e-be12-a3257c9b7249' };
        const entity2 = { id: 'fb311192-a40d-4f42-868b-bcd3f9a52e76' };
        vitest.spyOn(publicacionRoomieService, 'comparePublicacionRoomie');
        comp.comparePublicacionRoomie(entity, entity2);
        expect(publicacionRoomieService.comparePublicacionRoomie).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
