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
import { IContratoArriendo } from '../contrato-arriendo.model';
import { ContratoArriendoService } from '../service/contrato-arriendo.service';

import { ContratoArriendoFormService } from './contrato-arriendo-form.service';
import { ContratoArriendoUpdate } from './contrato-arriendo-update';

describe('ContratoArriendo Management Update Component', () => {
  let comp: ContratoArriendoUpdate;
  let fixture: ComponentFixture<ContratoArriendoUpdate>;
  let activatedRoute: ActivatedRoute;
  let contratoArriendoFormService: ContratoArriendoFormService;
  let contratoArriendoService: ContratoArriendoService;
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

    fixture = TestBed.createComponent(ContratoArriendoUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    contratoArriendoFormService = TestBed.inject(ContratoArriendoFormService);
    contratoArriendoService = TestBed.inject(ContratoArriendoService);
    perfilUsuarioService = TestBed.inject(PerfilUsuarioService);
    inmuebleService = TestBed.inject(InmuebleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call PerfilUsuario query and add missing value', () => {
      const contratoArriendo: IContratoArriendo = { id: 'e8f1f777-29f3-4ba4-b4f1-d1d01d96878f' };
      const arrendador: IPerfilUsuario = { id: '3bafd714-d12e-4295-9a25-ef8755df4436' };
      contratoArriendo.arrendador = arrendador;
      const arrendatario: IPerfilUsuario = { id: '3bafd714-d12e-4295-9a25-ef8755df4436' };
      contratoArriendo.arrendatario = arrendatario;

      const perfilUsuarioCollection: IPerfilUsuario[] = [{ id: '3bafd714-d12e-4295-9a25-ef8755df4436' }];
      vitest.spyOn(perfilUsuarioService, 'query').mockReturnValue(of(new HttpResponse({ body: perfilUsuarioCollection })));
      const additionalPerfilUsuarios = [arrendador, arrendatario];
      const expectedCollection: IPerfilUsuario[] = [...additionalPerfilUsuarios, ...perfilUsuarioCollection];
      vitest.spyOn(perfilUsuarioService, 'addPerfilUsuarioToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ contratoArriendo });
      comp.ngOnInit();

      expect(perfilUsuarioService.query).toHaveBeenCalled();
      expect(perfilUsuarioService.addPerfilUsuarioToCollectionIfMissing).toHaveBeenCalledWith(
        perfilUsuarioCollection,
        ...additionalPerfilUsuarios.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.perfilUsuariosSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Inmueble query and add missing value', () => {
      const contratoArriendo: IContratoArriendo = { id: 'e8f1f777-29f3-4ba4-b4f1-d1d01d96878f' };
      const inmueble: IInmueble = { id: '543d8aac-c8b3-49b6-8d46-362c3dce8741' };
      contratoArriendo.inmueble = inmueble;

      const inmuebleCollection: IInmueble[] = [{ id: '543d8aac-c8b3-49b6-8d46-362c3dce8741' }];
      vitest.spyOn(inmuebleService, 'query').mockReturnValue(of(new HttpResponse({ body: inmuebleCollection })));
      const additionalInmuebles = [inmueble];
      const expectedCollection: IInmueble[] = [...additionalInmuebles, ...inmuebleCollection];
      vitest.spyOn(inmuebleService, 'addInmuebleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ contratoArriendo });
      comp.ngOnInit();

      expect(inmuebleService.query).toHaveBeenCalled();
      expect(inmuebleService.addInmuebleToCollectionIfMissing).toHaveBeenCalledWith(
        inmuebleCollection,
        ...additionalInmuebles.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.inmueblesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const contratoArriendo: IContratoArriendo = { id: 'e8f1f777-29f3-4ba4-b4f1-d1d01d96878f' };
      const arrendador: IPerfilUsuario = { id: '3bafd714-d12e-4295-9a25-ef8755df4436' };
      contratoArriendo.arrendador = arrendador;
      const arrendatario: IPerfilUsuario = { id: '3bafd714-d12e-4295-9a25-ef8755df4436' };
      contratoArriendo.arrendatario = arrendatario;
      const inmueble: IInmueble = { id: '543d8aac-c8b3-49b6-8d46-362c3dce8741' };
      contratoArriendo.inmueble = inmueble;

      activatedRoute.data = of({ contratoArriendo });
      comp.ngOnInit();

      expect(comp.perfilUsuariosSharedCollection()).toContainEqual(arrendador);
      expect(comp.perfilUsuariosSharedCollection()).toContainEqual(arrendatario);
      expect(comp.inmueblesSharedCollection()).toContainEqual(inmueble);
      expect(comp.contratoArriendo).toEqual(contratoArriendo);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IContratoArriendo>();
      const contratoArriendo = { id: 'f993e4a3-6619-4c5a-be51-34708828262f' };
      vitest.spyOn(contratoArriendoFormService, 'getContratoArriendo').mockReturnValue(contratoArriendo);
      vitest.spyOn(contratoArriendoService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ contratoArriendo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(contratoArriendo);
      saveSubject.complete();

      // THEN
      expect(contratoArriendoFormService.getContratoArriendo).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(contratoArriendoService.update).toHaveBeenCalledWith(expect.objectContaining(contratoArriendo));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IContratoArriendo>();
      const contratoArriendo = { id: 'f993e4a3-6619-4c5a-be51-34708828262f' };
      vitest.spyOn(contratoArriendoFormService, 'getContratoArriendo').mockReturnValue({ id: null });
      vitest.spyOn(contratoArriendoService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ contratoArriendo: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(contratoArriendo);
      saveSubject.complete();

      // THEN
      expect(contratoArriendoFormService.getContratoArriendo).toHaveBeenCalled();
      expect(contratoArriendoService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IContratoArriendo>();
      const contratoArriendo = { id: 'f993e4a3-6619-4c5a-be51-34708828262f' };
      vitest.spyOn(contratoArriendoService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ contratoArriendo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(contratoArriendoService.update).toHaveBeenCalled();
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
