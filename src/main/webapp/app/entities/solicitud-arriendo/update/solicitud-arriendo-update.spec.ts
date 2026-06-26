import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IPerfilUsuario } from 'app/entities/perfil-usuario/perfil-usuario.model';
import { PerfilUsuarioService } from 'app/entities/perfil-usuario/service/perfil-usuario.service';
import { IPublicacionInmueble } from 'app/entities/publicacion-inmueble/publicacion-inmueble.model';
import { PublicacionInmuebleService } from 'app/entities/publicacion-inmueble/service/publicacion-inmueble.service';
import { SolicitudArriendoService } from '../service/solicitud-arriendo.service';
import { ISolicitudArriendo } from '../solicitud-arriendo.model';

import { SolicitudArriendoFormService } from './solicitud-arriendo-form.service';
import { SolicitudArriendoUpdate } from './solicitud-arriendo-update';

describe('SolicitudArriendo Management Update Component', () => {
  let comp: SolicitudArriendoUpdate;
  let fixture: ComponentFixture<SolicitudArriendoUpdate>;
  let activatedRoute: ActivatedRoute;
  let solicitudArriendoFormService: SolicitudArriendoFormService;
  let solicitudArriendoService: SolicitudArriendoService;
  let perfilUsuarioService: PerfilUsuarioService;
  let publicacionInmuebleService: PublicacionInmuebleService;

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

    fixture = TestBed.createComponent(SolicitudArriendoUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    solicitudArriendoFormService = TestBed.inject(SolicitudArriendoFormService);
    solicitudArriendoService = TestBed.inject(SolicitudArriendoService);
    perfilUsuarioService = TestBed.inject(PerfilUsuarioService);
    publicacionInmuebleService = TestBed.inject(PublicacionInmuebleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call PerfilUsuario query and add missing value', () => {
      const solicitudArriendo: ISolicitudArriendo = { id: '1a700994-9cbb-44dc-973b-332589a546e1' };
      const arrendatario: IPerfilUsuario = { id: '3bafd714-d12e-4295-9a25-ef8755df4436' };
      solicitudArriendo.arrendatario = arrendatario;

      const perfilUsuarioCollection: IPerfilUsuario[] = [{ id: '3bafd714-d12e-4295-9a25-ef8755df4436' }];
      vitest.spyOn(perfilUsuarioService, 'query').mockReturnValue(of(new HttpResponse({ body: perfilUsuarioCollection })));
      const additionalPerfilUsuarios = [arrendatario];
      const expectedCollection: IPerfilUsuario[] = [...additionalPerfilUsuarios, ...perfilUsuarioCollection];
      vitest.spyOn(perfilUsuarioService, 'addPerfilUsuarioToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ solicitudArriendo });
      comp.ngOnInit();

      expect(perfilUsuarioService.query).toHaveBeenCalled();
      expect(perfilUsuarioService.addPerfilUsuarioToCollectionIfMissing).toHaveBeenCalledWith(
        perfilUsuarioCollection,
        ...additionalPerfilUsuarios.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.perfilUsuariosSharedCollection()).toEqual(expectedCollection);
    });

    it('should call PublicacionInmueble query and add missing value', () => {
      const solicitudArriendo: ISolicitudArriendo = { id: '1a700994-9cbb-44dc-973b-332589a546e1' };
      const publicacion: IPublicacionInmueble = { id: '39817e9d-1a0a-463a-b680-3dedcd5663fd' };
      solicitudArriendo.publicacion = publicacion;

      const publicacionInmuebleCollection: IPublicacionInmueble[] = [{ id: '39817e9d-1a0a-463a-b680-3dedcd5663fd' }];
      vitest.spyOn(publicacionInmuebleService, 'query').mockReturnValue(of(new HttpResponse({ body: publicacionInmuebleCollection })));
      const additionalPublicacionInmuebles = [publicacion];
      const expectedCollection: IPublicacionInmueble[] = [...additionalPublicacionInmuebles, ...publicacionInmuebleCollection];
      vitest.spyOn(publicacionInmuebleService, 'addPublicacionInmuebleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ solicitudArriendo });
      comp.ngOnInit();

      expect(publicacionInmuebleService.query).toHaveBeenCalled();
      expect(publicacionInmuebleService.addPublicacionInmuebleToCollectionIfMissing).toHaveBeenCalledWith(
        publicacionInmuebleCollection,
        ...additionalPublicacionInmuebles.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.publicacionInmueblesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const solicitudArriendo: ISolicitudArriendo = { id: '1a700994-9cbb-44dc-973b-332589a546e1' };
      const arrendatario: IPerfilUsuario = { id: '3bafd714-d12e-4295-9a25-ef8755df4436' };
      solicitudArriendo.arrendatario = arrendatario;
      const publicacion: IPublicacionInmueble = { id: '39817e9d-1a0a-463a-b680-3dedcd5663fd' };
      solicitudArriendo.publicacion = publicacion;

      activatedRoute.data = of({ solicitudArriendo });
      comp.ngOnInit();

      expect(comp.perfilUsuariosSharedCollection()).toContainEqual(arrendatario);
      expect(comp.publicacionInmueblesSharedCollection()).toContainEqual(publicacion);
      expect(comp.solicitudArriendo).toEqual(solicitudArriendo);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ISolicitudArriendo>();
      const solicitudArriendo = { id: '1c33f7d7-8f08-4ba7-b15d-c02a593be236' };
      vitest.spyOn(solicitudArriendoFormService, 'getSolicitudArriendo').mockReturnValue(solicitudArriendo);
      vitest.spyOn(solicitudArriendoService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ solicitudArriendo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(solicitudArriendo);
      saveSubject.complete();

      // THEN
      expect(solicitudArriendoFormService.getSolicitudArriendo).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(solicitudArriendoService.update).toHaveBeenCalledWith(expect.objectContaining(solicitudArriendo));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ISolicitudArriendo>();
      const solicitudArriendo = { id: '1c33f7d7-8f08-4ba7-b15d-c02a593be236' };
      vitest.spyOn(solicitudArriendoFormService, 'getSolicitudArriendo').mockReturnValue({ id: null });
      vitest.spyOn(solicitudArriendoService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ solicitudArriendo: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(solicitudArriendo);
      saveSubject.complete();

      // THEN
      expect(solicitudArriendoFormService.getSolicitudArriendo).toHaveBeenCalled();
      expect(solicitudArriendoService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ISolicitudArriendo>();
      const solicitudArriendo = { id: '1c33f7d7-8f08-4ba7-b15d-c02a593be236' };
      vitest.spyOn(solicitudArriendoService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ solicitudArriendo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(solicitudArriendoService.update).toHaveBeenCalled();
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

    describe('comparePublicacionInmueble', () => {
      it('should forward to publicacionInmuebleService', () => {
        const entity = { id: '39817e9d-1a0a-463a-b680-3dedcd5663fd' };
        const entity2 = { id: 'f7b8901c-e157-4257-ade2-d6306fd5d98d' };
        vitest.spyOn(publicacionInmuebleService, 'comparePublicacionInmueble');
        comp.comparePublicacionInmueble(entity, entity2);
        expect(publicacionInmuebleService.comparePublicacionInmueble).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
