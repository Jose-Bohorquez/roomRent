import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { IPerfilUsuario } from '../perfil-usuario.model';
import { PerfilUsuarioService } from '../service/perfil-usuario.service';

import { PerfilUsuarioFormService } from './perfil-usuario-form.service';
import { PerfilUsuarioUpdate } from './perfil-usuario-update';

describe('PerfilUsuario Management Update Component', () => {
  let comp: PerfilUsuarioUpdate;
  let fixture: ComponentFixture<PerfilUsuarioUpdate>;
  let activatedRoute: ActivatedRoute;
  let perfilUsuarioFormService: PerfilUsuarioFormService;
  let perfilUsuarioService: PerfilUsuarioService;
  let userService: UserService;

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

    fixture = TestBed.createComponent(PerfilUsuarioUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    perfilUsuarioFormService = TestBed.inject(PerfilUsuarioFormService);
    perfilUsuarioService = TestBed.inject(PerfilUsuarioService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const perfilUsuario: IPerfilUsuario = { id: '56ad7495-56cb-4b2e-a436-e5def8987d42' };
      const usuario: IUser = { id: '1344246c-16a7-46d1-bb61-2043f965c8d5' };
      perfilUsuario.usuario = usuario;

      const userCollection: IUser[] = [{ id: '1344246c-16a7-46d1-bb61-2043f965c8d5' }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [usuario];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ perfilUsuario });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const perfilUsuario: IPerfilUsuario = { id: '56ad7495-56cb-4b2e-a436-e5def8987d42' };
      const usuario: IUser = { id: '1344246c-16a7-46d1-bb61-2043f965c8d5' };
      perfilUsuario.usuario = usuario;

      activatedRoute.data = of({ perfilUsuario });
      comp.ngOnInit();

      expect(comp.usersSharedCollection()).toContainEqual(usuario);
      expect(comp.perfilUsuario).toEqual(perfilUsuario);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPerfilUsuario>();
      const perfilUsuario = { id: '3bafd714-d12e-4295-9a25-ef8755df4436' };
      vitest.spyOn(perfilUsuarioFormService, 'getPerfilUsuario').mockReturnValue(perfilUsuario);
      vitest.spyOn(perfilUsuarioService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ perfilUsuario });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(perfilUsuario);
      saveSubject.complete();

      // THEN
      expect(perfilUsuarioFormService.getPerfilUsuario).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(perfilUsuarioService.update).toHaveBeenCalledWith(expect.objectContaining(perfilUsuario));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPerfilUsuario>();
      const perfilUsuario = { id: '3bafd714-d12e-4295-9a25-ef8755df4436' };
      vitest.spyOn(perfilUsuarioFormService, 'getPerfilUsuario').mockReturnValue({ id: null });
      vitest.spyOn(perfilUsuarioService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ perfilUsuario: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(perfilUsuario);
      saveSubject.complete();

      // THEN
      expect(perfilUsuarioFormService.getPerfilUsuario).toHaveBeenCalled();
      expect(perfilUsuarioService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IPerfilUsuario>();
      const perfilUsuario = { id: '3bafd714-d12e-4295-9a25-ef8755df4436' };
      vitest.spyOn(perfilUsuarioService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ perfilUsuario });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(perfilUsuarioService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: '1344246c-16a7-46d1-bb61-2043f965c8d5' };
        const entity2 = { id: '1e61df13-b2d3-459d-875e-5607a4ccdbdb' };
        vitest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
