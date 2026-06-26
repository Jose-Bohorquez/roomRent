import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IPerfilUsuario } from 'app/entities/perfil-usuario/perfil-usuario.model';
import { PerfilUsuarioService } from 'app/entities/perfil-usuario/service/perfil-usuario.service';
import { IDocumentoUsuario } from '../documento-usuario.model';
import { DocumentoUsuarioService } from '../service/documento-usuario.service';

import { DocumentoUsuarioFormService } from './documento-usuario-form.service';
import { DocumentoUsuarioUpdate } from './documento-usuario-update';

describe('DocumentoUsuario Management Update Component', () => {
  let comp: DocumentoUsuarioUpdate;
  let fixture: ComponentFixture<DocumentoUsuarioUpdate>;
  let activatedRoute: ActivatedRoute;
  let documentoUsuarioFormService: DocumentoUsuarioFormService;
  let documentoUsuarioService: DocumentoUsuarioService;
  let perfilUsuarioService: PerfilUsuarioService;

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

    fixture = TestBed.createComponent(DocumentoUsuarioUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    documentoUsuarioFormService = TestBed.inject(DocumentoUsuarioFormService);
    documentoUsuarioService = TestBed.inject(DocumentoUsuarioService);
    perfilUsuarioService = TestBed.inject(PerfilUsuarioService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call PerfilUsuario query and add missing value', () => {
      const documentoUsuario: IDocumentoUsuario = { id: '7de17601-1fd9-4064-912a-62b160a7f1dd' };
      const perfilUsuario: IPerfilUsuario = { id: '3bafd714-d12e-4295-9a25-ef8755df4436' };
      documentoUsuario.perfilUsuario = perfilUsuario;

      const perfilUsuarioCollection: IPerfilUsuario[] = [{ id: '3bafd714-d12e-4295-9a25-ef8755df4436' }];
      vitest.spyOn(perfilUsuarioService, 'query').mockReturnValue(of(new HttpResponse({ body: perfilUsuarioCollection })));
      const additionalPerfilUsuarios = [perfilUsuario];
      const expectedCollection: IPerfilUsuario[] = [...additionalPerfilUsuarios, ...perfilUsuarioCollection];
      vitest.spyOn(perfilUsuarioService, 'addPerfilUsuarioToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ documentoUsuario });
      comp.ngOnInit();

      expect(perfilUsuarioService.query).toHaveBeenCalled();
      expect(perfilUsuarioService.addPerfilUsuarioToCollectionIfMissing).toHaveBeenCalledWith(
        perfilUsuarioCollection,
        ...additionalPerfilUsuarios.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.perfilUsuariosSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const documentoUsuario: IDocumentoUsuario = { id: '7de17601-1fd9-4064-912a-62b160a7f1dd' };
      const perfilUsuario: IPerfilUsuario = { id: '3bafd714-d12e-4295-9a25-ef8755df4436' };
      documentoUsuario.perfilUsuario = perfilUsuario;

      activatedRoute.data = of({ documentoUsuario });
      comp.ngOnInit();

      expect(comp.perfilUsuariosSharedCollection()).toContainEqual(perfilUsuario);
      expect(comp.documentoUsuario).toEqual(documentoUsuario);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IDocumentoUsuario>();
      const documentoUsuario = { id: 'db348ca9-68bd-4486-8a92-f8cef80f77c7' };
      vitest.spyOn(documentoUsuarioFormService, 'getDocumentoUsuario').mockReturnValue(documentoUsuario);
      vitest.spyOn(documentoUsuarioService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ documentoUsuario });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(documentoUsuario);
      saveSubject.complete();

      // THEN
      expect(documentoUsuarioFormService.getDocumentoUsuario).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(documentoUsuarioService.update).toHaveBeenCalledWith(expect.objectContaining(documentoUsuario));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IDocumentoUsuario>();
      const documentoUsuario = { id: 'db348ca9-68bd-4486-8a92-f8cef80f77c7' };
      vitest.spyOn(documentoUsuarioFormService, 'getDocumentoUsuario').mockReturnValue({ id: null });
      vitest.spyOn(documentoUsuarioService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ documentoUsuario: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(documentoUsuario);
      saveSubject.complete();

      // THEN
      expect(documentoUsuarioFormService.getDocumentoUsuario).toHaveBeenCalled();
      expect(documentoUsuarioService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IDocumentoUsuario>();
      const documentoUsuario = { id: 'db348ca9-68bd-4486-8a92-f8cef80f77c7' };
      vitest.spyOn(documentoUsuarioService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ documentoUsuario });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(documentoUsuarioService.update).toHaveBeenCalled();
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
  });
});
