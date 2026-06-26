import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IPerfilUsuario } from 'app/entities/perfil-usuario/perfil-usuario.model';
import { PerfilUsuarioService } from 'app/entities/perfil-usuario/service/perfil-usuario.service';
import { SolicitudArriendoService } from 'app/entities/solicitud-arriendo/service/solicitud-arriendo.service';
import { ISolicitudArriendo } from 'app/entities/solicitud-arriendo/solicitud-arriendo.model';
import { VisitaProgramadaService } from '../service/visita-programada.service';
import { IVisitaProgramada } from '../visita-programada.model';

import { VisitaProgramadaFormService } from './visita-programada-form.service';
import { VisitaProgramadaUpdate } from './visita-programada-update';

describe('VisitaProgramada Management Update Component', () => {
  let comp: VisitaProgramadaUpdate;
  let fixture: ComponentFixture<VisitaProgramadaUpdate>;
  let activatedRoute: ActivatedRoute;
  let visitaProgramadaFormService: VisitaProgramadaFormService;
  let visitaProgramadaService: VisitaProgramadaService;
  let perfilUsuarioService: PerfilUsuarioService;
  let solicitudArriendoService: SolicitudArriendoService;

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

    fixture = TestBed.createComponent(VisitaProgramadaUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    visitaProgramadaFormService = TestBed.inject(VisitaProgramadaFormService);
    visitaProgramadaService = TestBed.inject(VisitaProgramadaService);
    perfilUsuarioService = TestBed.inject(PerfilUsuarioService);
    solicitudArriendoService = TestBed.inject(SolicitudArriendoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call PerfilUsuario query and add missing value', () => {
      const visitaProgramada: IVisitaProgramada = { id: '3aa6b388-cba8-4d3d-a6c5-c9d420c3ace7' };
      const visitante: IPerfilUsuario = { id: '3bafd714-d12e-4295-9a25-ef8755df4436' };
      visitaProgramada.visitante = visitante;

      const perfilUsuarioCollection: IPerfilUsuario[] = [{ id: '3bafd714-d12e-4295-9a25-ef8755df4436' }];
      vitest.spyOn(perfilUsuarioService, 'query').mockReturnValue(of(new HttpResponse({ body: perfilUsuarioCollection })));
      const additionalPerfilUsuarios = [visitante];
      const expectedCollection: IPerfilUsuario[] = [...additionalPerfilUsuarios, ...perfilUsuarioCollection];
      vitest.spyOn(perfilUsuarioService, 'addPerfilUsuarioToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ visitaProgramada });
      comp.ngOnInit();

      expect(perfilUsuarioService.query).toHaveBeenCalled();
      expect(perfilUsuarioService.addPerfilUsuarioToCollectionIfMissing).toHaveBeenCalledWith(
        perfilUsuarioCollection,
        ...additionalPerfilUsuarios.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.perfilUsuariosSharedCollection()).toEqual(expectedCollection);
    });

    it('should call SolicitudArriendo query and add missing value', () => {
      const visitaProgramada: IVisitaProgramada = { id: '3aa6b388-cba8-4d3d-a6c5-c9d420c3ace7' };
      const solicitud: ISolicitudArriendo = { id: '1c33f7d7-8f08-4ba7-b15d-c02a593be236' };
      visitaProgramada.solicitud = solicitud;

      const solicitudArriendoCollection: ISolicitudArriendo[] = [{ id: '1c33f7d7-8f08-4ba7-b15d-c02a593be236' }];
      vitest.spyOn(solicitudArriendoService, 'query').mockReturnValue(of(new HttpResponse({ body: solicitudArriendoCollection })));
      const additionalSolicitudArriendos = [solicitud];
      const expectedCollection: ISolicitudArriendo[] = [...additionalSolicitudArriendos, ...solicitudArriendoCollection];
      vitest.spyOn(solicitudArriendoService, 'addSolicitudArriendoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ visitaProgramada });
      comp.ngOnInit();

      expect(solicitudArriendoService.query).toHaveBeenCalled();
      expect(solicitudArriendoService.addSolicitudArriendoToCollectionIfMissing).toHaveBeenCalledWith(
        solicitudArriendoCollection,
        ...additionalSolicitudArriendos.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.solicitudArriendosSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const visitaProgramada: IVisitaProgramada = { id: '3aa6b388-cba8-4d3d-a6c5-c9d420c3ace7' };
      const visitante: IPerfilUsuario = { id: '3bafd714-d12e-4295-9a25-ef8755df4436' };
      visitaProgramada.visitante = visitante;
      const solicitud: ISolicitudArriendo = { id: '1c33f7d7-8f08-4ba7-b15d-c02a593be236' };
      visitaProgramada.solicitud = solicitud;

      activatedRoute.data = of({ visitaProgramada });
      comp.ngOnInit();

      expect(comp.perfilUsuariosSharedCollection()).toContainEqual(visitante);
      expect(comp.solicitudArriendosSharedCollection()).toContainEqual(solicitud);
      expect(comp.visitaProgramada).toEqual(visitaProgramada);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IVisitaProgramada>();
      const visitaProgramada = { id: '14c3d200-8ecb-4df4-9dbe-2c8c538f2668' };
      vitest.spyOn(visitaProgramadaFormService, 'getVisitaProgramada').mockReturnValue(visitaProgramada);
      vitest.spyOn(visitaProgramadaService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ visitaProgramada });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(visitaProgramada);
      saveSubject.complete();

      // THEN
      expect(visitaProgramadaFormService.getVisitaProgramada).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(visitaProgramadaService.update).toHaveBeenCalledWith(expect.objectContaining(visitaProgramada));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IVisitaProgramada>();
      const visitaProgramada = { id: '14c3d200-8ecb-4df4-9dbe-2c8c538f2668' };
      vitest.spyOn(visitaProgramadaFormService, 'getVisitaProgramada').mockReturnValue({ id: null });
      vitest.spyOn(visitaProgramadaService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ visitaProgramada: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(visitaProgramada);
      saveSubject.complete();

      // THEN
      expect(visitaProgramadaFormService.getVisitaProgramada).toHaveBeenCalled();
      expect(visitaProgramadaService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IVisitaProgramada>();
      const visitaProgramada = { id: '14c3d200-8ecb-4df4-9dbe-2c8c538f2668' };
      vitest.spyOn(visitaProgramadaService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ visitaProgramada });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(visitaProgramadaService.update).toHaveBeenCalled();
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

    describe('compareSolicitudArriendo', () => {
      it('should forward to solicitudArriendoService', () => {
        const entity = { id: '1c33f7d7-8f08-4ba7-b15d-c02a593be236' };
        const entity2 = { id: '1a700994-9cbb-44dc-973b-332589a546e1' };
        vitest.spyOn(solicitudArriendoService, 'compareSolicitudArriendo');
        comp.compareSolicitudArriendo(entity, entity2);
        expect(solicitudArriendoService.compareSolicitudArriendo).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
