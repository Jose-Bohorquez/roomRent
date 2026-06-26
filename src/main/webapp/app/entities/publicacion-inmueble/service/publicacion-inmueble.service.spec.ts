import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IPublicacionInmueble } from '../publicacion-inmueble.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../publicacion-inmueble.test-samples';

import { PublicacionInmuebleService, RestPublicacionInmueble } from './publicacion-inmueble.service';

const requireRestSample: RestPublicacionInmueble = {
  ...sampleWithRequiredData,
  fechaDisponible: sampleWithRequiredData.fechaDisponible?.format(DATE_FORMAT),
};

describe('PublicacionInmueble Service', () => {
  let service: PublicacionInmuebleService;
  let httpMock: HttpTestingController;
  let expectedResult: IPublicacionInmueble | IPublicacionInmueble[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PublicacionInmuebleService);
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

    it('should create a PublicacionInmueble', () => {
      const publicacionInmueble = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(publicacionInmueble).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PublicacionInmueble', () => {
      const publicacionInmueble = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(publicacionInmueble).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PublicacionInmueble', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PublicacionInmueble', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PublicacionInmueble', () => {
      service.delete('ABC').subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addPublicacionInmuebleToCollectionIfMissing', () => {
      it('should add a PublicacionInmueble to an empty array', () => {
        const publicacionInmueble: IPublicacionInmueble = sampleWithRequiredData;
        expectedResult = service.addPublicacionInmuebleToCollectionIfMissing([], publicacionInmueble);
        expect(expectedResult).toEqual([publicacionInmueble]);
      });

      it('should not add a PublicacionInmueble to an array that contains it', () => {
        const publicacionInmueble: IPublicacionInmueble = sampleWithRequiredData;
        const publicacionInmuebleCollection: IPublicacionInmueble[] = [
          {
            ...publicacionInmueble,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPublicacionInmuebleToCollectionIfMissing(publicacionInmuebleCollection, publicacionInmueble);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PublicacionInmueble to an array that doesn't contain it", () => {
        const publicacionInmueble: IPublicacionInmueble = sampleWithRequiredData;
        const publicacionInmuebleCollection: IPublicacionInmueble[] = [sampleWithPartialData];
        expectedResult = service.addPublicacionInmuebleToCollectionIfMissing(publicacionInmuebleCollection, publicacionInmueble);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(publicacionInmueble);
      });

      it('should add only unique PublicacionInmueble to an array', () => {
        const publicacionInmuebleArray: IPublicacionInmueble[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const publicacionInmuebleCollection: IPublicacionInmueble[] = [sampleWithRequiredData];
        expectedResult = service.addPublicacionInmuebleToCollectionIfMissing(publicacionInmuebleCollection, ...publicacionInmuebleArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const publicacionInmueble: IPublicacionInmueble = sampleWithRequiredData;
        const publicacionInmueble2: IPublicacionInmueble = sampleWithPartialData;
        expectedResult = service.addPublicacionInmuebleToCollectionIfMissing([], publicacionInmueble, publicacionInmueble2);
        expect(expectedResult).toEqual([publicacionInmueble, publicacionInmueble2]);
      });

      it('should accept null and undefined values', () => {
        const publicacionInmueble: IPublicacionInmueble = sampleWithRequiredData;
        expectedResult = service.addPublicacionInmuebleToCollectionIfMissing([], null, publicacionInmueble, undefined);
        expect(expectedResult).toEqual([publicacionInmueble]);
      });

      it('should return initial array if no PublicacionInmueble is added', () => {
        const publicacionInmuebleCollection: IPublicacionInmueble[] = [sampleWithRequiredData];
        expectedResult = service.addPublicacionInmuebleToCollectionIfMissing(publicacionInmuebleCollection, undefined, null);
        expect(expectedResult).toEqual(publicacionInmuebleCollection);
      });
    });

    describe('comparePublicacionInmueble', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePublicacionInmueble(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: '39817e9d-1a0a-463a-b680-3dedcd5663fd' };
        const entity2 = null;

        const compareResult1 = service.comparePublicacionInmueble(entity1, entity2);
        const compareResult2 = service.comparePublicacionInmueble(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: '39817e9d-1a0a-463a-b680-3dedcd5663fd' };
        const entity2 = { id: 'f7b8901c-e157-4257-ade2-d6306fd5d98d' };

        const compareResult1 = service.comparePublicacionInmueble(entity1, entity2);
        const compareResult2 = service.comparePublicacionInmueble(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: '39817e9d-1a0a-463a-b680-3dedcd5663fd' };
        const entity2 = { id: '39817e9d-1a0a-463a-b680-3dedcd5663fd' };

        const compareResult1 = service.comparePublicacionInmueble(entity1, entity2);
        const compareResult2 = service.comparePublicacionInmueble(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
