import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IInmueble } from 'app/entities/inmueble/inmueble.model';
import { InmuebleService } from 'app/entities/inmueble/service/inmueble.service';
import { IMultimediaInmueble } from '../multimedia-inmueble.model';
import { MultimediaInmuebleService } from '../service/multimedia-inmueble.service';

import { MultimediaInmuebleFormService } from './multimedia-inmueble-form.service';
import { MultimediaInmuebleUpdate } from './multimedia-inmueble-update';

describe('MultimediaInmueble Management Update Component', () => {
  let comp: MultimediaInmuebleUpdate;
  let fixture: ComponentFixture<MultimediaInmuebleUpdate>;
  let activatedRoute: ActivatedRoute;
  let multimediaInmuebleFormService: MultimediaInmuebleFormService;
  let multimediaInmuebleService: MultimediaInmuebleService;
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

    fixture = TestBed.createComponent(MultimediaInmuebleUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    multimediaInmuebleFormService = TestBed.inject(MultimediaInmuebleFormService);
    multimediaInmuebleService = TestBed.inject(MultimediaInmuebleService);
    inmuebleService = TestBed.inject(InmuebleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Inmueble query and add missing value', () => {
      const multimediaInmueble: IMultimediaInmueble = { id: 'ba0d3791-dc39-4385-9bc1-7e1dc59f61c9' };
      const inmueble: IInmueble = { id: '543d8aac-c8b3-49b6-8d46-362c3dce8741' };
      multimediaInmueble.inmueble = inmueble;

      const inmuebleCollection: IInmueble[] = [{ id: '543d8aac-c8b3-49b6-8d46-362c3dce8741' }];
      vitest.spyOn(inmuebleService, 'query').mockReturnValue(of(new HttpResponse({ body: inmuebleCollection })));
      const additionalInmuebles = [inmueble];
      const expectedCollection: IInmueble[] = [...additionalInmuebles, ...inmuebleCollection];
      vitest.spyOn(inmuebleService, 'addInmuebleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ multimediaInmueble });
      comp.ngOnInit();

      expect(inmuebleService.query).toHaveBeenCalled();
      expect(inmuebleService.addInmuebleToCollectionIfMissing).toHaveBeenCalledWith(
        inmuebleCollection,
        ...additionalInmuebles.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.inmueblesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const multimediaInmueble: IMultimediaInmueble = { id: 'ba0d3791-dc39-4385-9bc1-7e1dc59f61c9' };
      const inmueble: IInmueble = { id: '543d8aac-c8b3-49b6-8d46-362c3dce8741' };
      multimediaInmueble.inmueble = inmueble;

      activatedRoute.data = of({ multimediaInmueble });
      comp.ngOnInit();

      expect(comp.inmueblesSharedCollection()).toContainEqual(inmueble);
      expect(comp.multimediaInmueble).toEqual(multimediaInmueble);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IMultimediaInmueble>();
      const multimediaInmueble = { id: '95963ca7-cd69-4cfc-a654-20ca871c5539' };
      vitest.spyOn(multimediaInmuebleFormService, 'getMultimediaInmueble').mockReturnValue(multimediaInmueble);
      vitest.spyOn(multimediaInmuebleService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ multimediaInmueble });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(multimediaInmueble);
      saveSubject.complete();

      // THEN
      expect(multimediaInmuebleFormService.getMultimediaInmueble).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(multimediaInmuebleService.update).toHaveBeenCalledWith(expect.objectContaining(multimediaInmueble));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IMultimediaInmueble>();
      const multimediaInmueble = { id: '95963ca7-cd69-4cfc-a654-20ca871c5539' };
      vitest.spyOn(multimediaInmuebleFormService, 'getMultimediaInmueble').mockReturnValue({ id: null });
      vitest.spyOn(multimediaInmuebleService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ multimediaInmueble: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(multimediaInmueble);
      saveSubject.complete();

      // THEN
      expect(multimediaInmuebleFormService.getMultimediaInmueble).toHaveBeenCalled();
      expect(multimediaInmuebleService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IMultimediaInmueble>();
      const multimediaInmueble = { id: '95963ca7-cd69-4cfc-a654-20ca871c5539' };
      vitest.spyOn(multimediaInmuebleService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ multimediaInmueble });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(multimediaInmuebleService.update).toHaveBeenCalled();
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
