import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IDocumentoUsuario } from '../documento-usuario.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../documento-usuario.test-samples';

import { DocumentoUsuarioService, RestDocumentoUsuario } from './documento-usuario.service';

const requireRestSample: RestDocumentoUsuario = {
  ...sampleWithRequiredData,
  fechaCarga: sampleWithRequiredData.fechaCarga?.toJSON(),
};

describe('DocumentoUsuario Service', () => {
  let service: DocumentoUsuarioService;
  let httpMock: HttpTestingController;
  let expectedResult: IDocumentoUsuario | IDocumentoUsuario[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(DocumentoUsuarioService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find('ABC').subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a DocumentoUsuario', () => {
      const documentoUsuario = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(documentoUsuario).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a DocumentoUsuario', () => {
      const documentoUsuario = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(documentoUsuario).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a DocumentoUsuario', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of DocumentoUsuario', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a DocumentoUsuario', () => {
      service.delete('ABC').subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addDocumentoUsuarioToCollectionIfMissing', () => {
      it('should add a DocumentoUsuario to an empty array', () => {
        const documentoUsuario: IDocumentoUsuario = sampleWithRequiredData;
        expectedResult = service.addDocumentoUsuarioToCollectionIfMissing([], documentoUsuario);
        expect(expectedResult).toEqual([documentoUsuario]);
      });

      it('should not add a DocumentoUsuario to an array that contains it', () => {
        const documentoUsuario: IDocumentoUsuario = sampleWithRequiredData;
        const documentoUsuarioCollection: IDocumentoUsuario[] = [
          {
            ...documentoUsuario,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDocumentoUsuarioToCollectionIfMissing(documentoUsuarioCollection, documentoUsuario);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a DocumentoUsuario to an array that doesn't contain it", () => {
        const documentoUsuario: IDocumentoUsuario = sampleWithRequiredData;
        const documentoUsuarioCollection: IDocumentoUsuario[] = [sampleWithPartialData];
        expectedResult = service.addDocumentoUsuarioToCollectionIfMissing(documentoUsuarioCollection, documentoUsuario);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(documentoUsuario);
      });

      it('should add only unique DocumentoUsuario to an array', () => {
        const documentoUsuarioArray: IDocumentoUsuario[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const documentoUsuarioCollection: IDocumentoUsuario[] = [sampleWithRequiredData];
        expectedResult = service.addDocumentoUsuarioToCollectionIfMissing(documentoUsuarioCollection, ...documentoUsuarioArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const documentoUsuario: IDocumentoUsuario = sampleWithRequiredData;
        const documentoUsuario2: IDocumentoUsuario = sampleWithPartialData;
        expectedResult = service.addDocumentoUsuarioToCollectionIfMissing([], documentoUsuario, documentoUsuario2);
        expect(expectedResult).toEqual([documentoUsuario, documentoUsuario2]);
      });

      it('should accept null and undefined values', () => {
        const documentoUsuario: IDocumentoUsuario = sampleWithRequiredData;
        expectedResult = service.addDocumentoUsuarioToCollectionIfMissing([], null, documentoUsuario, undefined);
        expect(expectedResult).toEqual([documentoUsuario]);
      });

      it('should return initial array if no DocumentoUsuario is added', () => {
        const documentoUsuarioCollection: IDocumentoUsuario[] = [sampleWithRequiredData];
        expectedResult = service.addDocumentoUsuarioToCollectionIfMissing(documentoUsuarioCollection, undefined, null);
        expect(expectedResult).toEqual(documentoUsuarioCollection);
      });
    });

    describe('compareDocumentoUsuario', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDocumentoUsuario(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 'db348ca9-68bd-4486-8a92-f8cef80f77c7' };
        const entity2 = null;

        const compareResult1 = service.compareDocumentoUsuario(entity1, entity2);
        const compareResult2 = service.compareDocumentoUsuario(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 'db348ca9-68bd-4486-8a92-f8cef80f77c7' };
        const entity2 = { id: '7de17601-1fd9-4064-912a-62b160a7f1dd' };

        const compareResult1 = service.compareDocumentoUsuario(entity1, entity2);
        const compareResult2 = service.compareDocumentoUsuario(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 'db348ca9-68bd-4486-8a92-f8cef80f77c7' };
        const entity2 = { id: 'db348ca9-68bd-4486-8a92-f8cef80f77c7' };

        const compareResult1 = service.compareDocumentoUsuario(entity1, entity2);
        const compareResult2 = service.compareDocumentoUsuario(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
