import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

import { ContratoArriendoDetail } from './contrato-arriendo-detail';

describe('ContratoArriendo Management Detail Component', () => {
  let comp: ContratoArriendoDetail;
  let fixture: ComponentFixture<ContratoArriendoDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./contrato-arriendo-detail').then(m => m.ContratoArriendoDetail),
              resolve: { contratoArriendo: () => of({ id: 'f993e4a3-6619-4c5a-be51-34708828262f' }) },
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
    fixture = TestBed.createComponent(ContratoArriendoDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load contratoArriendo on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ContratoArriendoDetail);

      // THEN
      expect(instance.contratoArriendo()).toEqual(expect.objectContaining({ id: 'f993e4a3-6619-4c5a-be51-34708828262f' }));
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
