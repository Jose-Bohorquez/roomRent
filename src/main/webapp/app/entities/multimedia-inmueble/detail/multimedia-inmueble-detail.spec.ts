import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

import { MultimediaInmuebleDetail } from './multimedia-inmueble-detail';

describe('MultimediaInmueble Management Detail Component', () => {
  let comp: MultimediaInmuebleDetail;
  let fixture: ComponentFixture<MultimediaInmuebleDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./multimedia-inmueble-detail').then(m => m.MultimediaInmuebleDetail),
              resolve: { multimediaInmueble: () => of({ id: '95963ca7-cd69-4cfc-a654-20ca871c5539' }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    });
    const library = TestBed.inject(FaIconLibrary);
    library.addIcons(faArrowLeft);
    library.addIcons(faPencilAlt);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MultimediaInmuebleDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load multimediaInmueble on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', MultimediaInmuebleDetail);

      // THEN
      expect(instance.multimediaInmueble()).toEqual(expect.objectContaining({ id: '95963ca7-cd69-4cfc-a654-20ca871c5539' }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      vitest.spyOn(globalThis.history, 'back');
      comp.previousState();
      expect(globalThis.history.back).toHaveBeenCalled();
    });
  });
});
