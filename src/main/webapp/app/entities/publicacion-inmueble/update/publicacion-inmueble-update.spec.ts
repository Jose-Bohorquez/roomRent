import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IInmueble } from 'app/entities/inmueble/inmueble.model';
import { InmuebleService } from 'app/entities/inmueble/service/inmueble.service';
import { IPublicacionInmueble } from '../publicacion-inmueble.model';
import { PublicacionInmuebleService } from '../service/publicacion-inmueble.service';

import { PublicacionInmuebleFormService } from './publicacion-inmueble-form.service';
import { PublicacionInmuebleUpdate } from './publicacion-inmueble-update';

describe('PublicacionInmueble Management Update Component', () => {
  let comp: PublicacionInmuebleUpdate;
  let fixture: ComponentFixture<PublicacionInmuebleUpdate>;
  let activatedRoute: ActivatedRoute;
  let publicacionInmuebleFormService: PublicacionInmuebleFormService;
  let publicacionInmuebleService: PublicacionInmuebleService;
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

    fixture = TestBed.createComponent(PublicacionInmuebleUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    publicacionInmuebleFormService = TestBed.inject(PublicacionInmuebleFormService);
    publicacionInmuebleService = TestBed.inject(PublicacionInmuebleService);
    inmuebleService = TestBed.inject(InmuebleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Inmueble query and add missing value', () => {
      const publicacionInmueble: IPublicacionInmueble = { id: 'f7b8901c-e157-4257-ade2-d6306fd5d98d' };
      const inmueble: IInmueble = { id: '543d8aac-c8b3-49b6-8d46-362c3dce8741' };
      publicacionInmueble.inmueble = inmueble;

      const inmuebleCollection: IInmueble[] = [{ id: '543d8aac-c8b3-49b6-8d46-362c3dce8741' }];
      vitest.spyOn(inmuebleService, 'query').mockReturnValue(of(new HttpResponse({ body: inmuebleCollection })));
      const additionalInmuebles = [inmueble];
      const expectedCollection: IInmueble[] = [...additionalInmuebles, ...inmuebleCollection];
      vitest.spyOn(inmuebleService, 'addInmuebleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ publicacionInmueble });
      comp.ngOnInit();

      expect(inmuebleService.query).toHaveBeenCalled();
      expect(inmuebleService.addInmuebleToCollectionIfMissing).toHaveBeenCalledWith(
        inmuebleCollection,
        ...additionalInmuebles.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.inmueblesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const publicacionInmueble: IPublicacionInmueble = { id: 'f7b8901c-e157-4257-ade2-d6306fd5d98d' };
      const inmueble: IInmueble = { id: '543d8aac-c8b3-49b6-8d46-362c3dce8741' };
      publicacionInmueble.inmueble = inmueble;

      activatedRoute.data = of({ publicacionInmueble });
      comp.ngOnInit();

      expect(comp.inmueblesSharedCollection()).toContainEqual(inmueble);
      expect(comp.publicacionInmueble).toEqual(publicacionInmueble);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPublicacionInmueble>();
      const publicacionInmueble = { id: '39817e9d-1a0a-463a-b680-3dedcd5663fd' };
      vitest.spyOn(publicacionInmuebleFormService, 'getPublicacionInmueble').mockReturnValue(publicacionInmueble);
      vitest.spyOn(publicacionInmuebleService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ publicacionInmueble });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(publicacionInmueble);
      saveSubject.complete();

      // THEN
      expect(publicacionInmuebleFormService.getPublicacionInmueble).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(publicacionInmuebleService.update).toHaveBeenCalledWith(expect.objectContaining(publicacionInmueble));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPublicacionInmueble>();
      const publicacionInmueble = { id: '39817e9d-1a0a-463a-b680-3dedcd5663fd' };
      vitest.spyOn(publicacionInmuebleFormService, 'getPublicacionInmueble').mockReturnValue({ id: null });
      vitest.spyOn(publicacionInmuebleService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ publicacionInmueble: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(publicacionInmueble);
      saveSubject.complete();

      // THEN
      expect(publicacionInmuebleFormService.getPublicacionInmueble).toHaveBeenCalled();
      expect(publicacionInmuebleService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IPublicacionInmueble>();
      const publicacionInmueble = { id: '39817e9d-1a0a-463a-b680-3dedcd5663fd' };
      vitest.spyOn(publicacionInmuebleService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ publicacionInmueble });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(publicacionInmuebleService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
