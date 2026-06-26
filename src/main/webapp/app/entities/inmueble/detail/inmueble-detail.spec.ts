import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

import { InmuebleDetail } from './inmueble-detail';

describe('Inmueble Management Detail Component', () => {
  let comp: InmuebleDetail;
  let fixture: ComponentFixture<InmuebleDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./inmueble-detail').then(m => m.InmuebleDetail),
              resolve: { inmueble: () => of({ id: '543d8aac-c8b3-49b6-8d46-362c3dce8741' }) },
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
    fixture = TestBed.createComponent(InmuebleDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load inmueble on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', InmuebleDetail);

      // THEN
      expect(instance.inmueble()).toEqual(expect.objectContaining({ id: '543d8aac-c8b3-49b6-8d46-362c3dce8741' }));
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
