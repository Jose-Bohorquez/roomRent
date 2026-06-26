import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IContratoArriendo } from 'app/entities/contrato-arriendo/contrato-arriendo.model';
import { ContratoArriendoService } from 'app/entities/contrato-arriendo/service/contrato-arriendo.service';
import { IPerfilUsuario } from 'app/entities/perfil-usuario/perfil-usuario.model';
import { PerfilUsuarioService } from 'app/entities/perfil-usuario/service/perfil-usuario.service';
import { ICalificacion } from '../calificacion.model';
import { CalificacionService } from '../service/calificacion.service';

import { CalificacionFormService } from './calificacion-form.service';
import { CalificacionUpdate } from './calificacion-update';

describe('Calificacion Management Update Component', () => {
  let comp: CalificacionUpdate;
  let fixture: ComponentFixture<CalificacionUpdate>;
  let activatedRoute: ActivatedRoute;
  let calificacionFormService: CalificacionFormService;
  let calificacionService: CalificacionService;
  let perfilUsuarioService: PerfilUsuarioService;
  let contratoArriendoService: ContratoArriendoService;

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

    fixture = TestBed.createComponent(CalificacionUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    calificacionFormService = TestBed.inject(CalificacionFormService);
    calificacionService = TestBed.inject(CalificacionService);
    perfilUsuarioService = TestBed.inject(PerfilUsuarioService);
    contratoArriendoService = TestBed.inject(ContratoArriendoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call PerfilUsuario query and add missing value', () => {
      const calificacion: ICalificacion = { id: 'f70b8935-bc54-4466-a147-ab60ec4c120f' };
      const autor: IPerfilUsuario = { id: '3bafd714-d12e-4295-9a25-ef8755df4436' };
      calificacion.autor = autor;
      const calificado: IPerfilUsuario = { id: '3bafd714-d12e-4295-9a25-ef8755df4436' };
      calificacion.calificado = calificado;

      const perfilUsuarioCollection: IPerfilUsuario[] = [{ id: '3bafd714-d12e-4295-9a25-ef8755df4436' }];
      vitest.spyOn(perfilUsuarioService, 'query').mockReturnValue(of(new HttpResponse({ body: perfilUsuarioCollection })));
      const additionalPerfilUsuarios = [autor, calificado];
      const expectedCollection: IPerfilUsuario[] = [...additionalPerfilUsuarios, ...perfilUsuarioCollection];
      vitest.spyOn(perfilUsuarioService, 'addPerfilUsuarioToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ calificacion });
      comp.ngOnInit();

      expect(perfilUsuarioService.query).toHaveBeenCalled();
      expect(perfilUsuarioService.addPerfilUsuarioToCollectionIfMissing).toHaveBeenCalledWith(
        perfilUsuarioCollection,
        ...additionalPerfilUsuarios.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.perfilUsuariosSharedCollection()).toEqual(expectedCollection);
    });

    it('should call ContratoArriendo query and add missing value', () => {
      const calificacion: ICalificacion = { id: 'f70b8935-bc54-4466-a147-ab60ec4c120f' };
      const contrato: IContratoArriendo = { id: 'f993e4a3-6619-4c5a-be51-34708828262f' };
      calificacion.contrato = contrato;

      const contratoArriendoCollection: IContratoArriendo[] = [{ id: 'f993e4a3-6619-4c5a-be51-34708828262f' }];
      vitest.spyOn(contratoArriendoService, 'query').mockReturnValue(of(new HttpResponse({ body: contratoArriendoCollection })));
      const additionalContratoArriendos = [contrato];
      const expectedCollection: IContratoArriendo[] = [...additionalContratoArriendos, ...contratoArriendoCollection];
      vitest.spyOn(contratoArriendoService, 'addContratoArriendoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ calificacion });
      comp.ngOnInit();

      expect(contratoArriendoService.query).toHaveBeenCalled();
      expect(contratoArriendoService.addContratoArriendoToCollectionIfMissing).toHaveBeenCalledWith(
        contratoArriendoCollection,
        ...additionalContratoArriendos.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.contratoArriendosSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const calificacion: ICalificacion = { id: 'f70b8935-bc54-4466-a147-ab60ec4c120f' };
      const autor: IPerfilUsuario = { id: '3bafd714-d12e-4295-9a25-ef8755df4436' };
      calificacion.autor = autor;
      const calificado: IPerfilUsuario = { id: '3bafd714-d12e-4295-9a25-ef8755df4436' };
      calificacion.calificado = calificado;
      const contrato: IContratoArriendo = { id: 'f993e4a3-6619-4c5a-be51-34708828262f' };
      calificacion.contrato = contrato;

      activatedRoute.data = of({ calificacion });
      comp.ngOnInit();

      expect(comp.perfilUsuariosSharedCollection()).toContainEqual(autor);
      expect(comp.perfilUsuariosSharedCollection()).toContainEqual(calificado);
      expect(comp.contratoArriendosSharedCollection()).toContainEqual(contrato);
      expect(comp.calificacion).toEqual(calificacion);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICalificacion>();
      const calificacion = { id: '08e53216-3dd8-41d0-878d-1342a0f024ae' };
      vitest.spyOn(calificacionFormService, 'getCalificacion').mockReturnValue(calificacion);
      vitest.spyOn(calificacionService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ calificacion });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(calificacion);
      saveSubject.complete();

      // THEN
      expect(calificacionFormService.getCalificacion).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(calificacionService.update).toHaveBeenCalledWith(expect.objectContaining(calificacion));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICalificacion>();
      const calificacion = { id: '08e53216-3dd8-41d0-878d-1342a0f024ae' };
      vitest.spyOn(calificacionFormService, 'getCalificacion').mockReturnValue({ id: null });
      vitest.spyOn(calificacionService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ calificacion: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(calificacion);
      saveSubject.complete();

      // THEN
      expect(calificacionFormService.getCalificacion).toHaveBeenCalled();
      expect(calificacionService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICalificacion>();
      const calificacion = { id: '08e53216-3dd8-41d0-878d-1342a0f024ae' };
      vitest.spyOn(calificacionService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ calificacion });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(calificacionService.update).toHaveBeenCalled();
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

    describe('compareContratoArriendo', () => {
      it('should forward to contratoArriendoService', () => {
        const entity = { id: 'f993e4a3-6619-4c5a-be51-34708828262f' };
        const entity2 = { id: 'e8f1f777-29f3-4ba4-b4f1-d1d01d96878f' };
        vitest.spyOn(contratoArriendoService, 'compareContratoArriendo');
        comp.compareContratoArriendo(entity, entity2);
        expect(contratoArriendoService.compareContratoArriendo).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
