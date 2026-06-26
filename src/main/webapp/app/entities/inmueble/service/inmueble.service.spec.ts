import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IInmueble } from '../inmueble.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../inmueble.test-samples';

import { InmuebleService } from './inmueble.service';

const requireRestSample: IInmueble = {
  ...sampleWithRequiredData,
};

describe('Inmueble Service', () => {
  let service: InmuebleService;
  let httpMock: HttpTestingController;
  let expectedResult: IInmueble | IInmueble[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(InmuebleService);
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

    it('should create a Inmueble', () => {
      const inmueble = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(inmueble).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Inmueble', () => {
      const inmueble = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(inmueble).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Inmueble', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Inmueble', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Inmueble', () => {
      service.delete('ABC').subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addInmuebleToCollectionIfMissing', () => {
      it('should add a Inmueble to an empty array', () => {
        const inmueble: IInmueble = sampleWithRequiredData;
        expectedResult = service.addInmuebleToCollectionIfMissing([], inmueble);
        expect(expectedResult).toEqual([inmueble]);
      });

      it('should not add a Inmueble to an array that contains it', () => {
        const inmueble: IInmueble = sampleWithRequiredData;
        const inmuebleCollection: IInmueble[] = [
          {
            ...inmueble,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addInmuebleToCollectionIfMissing(inmuebleCollection, inmueble);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Inmueble to an array that doesn't contain it", () => {
        const inmueble: IInmueble = sampleWithRequiredData;
        const inmuebleCollection: IInmueble[] = [sampleWithPartialData];
        expectedResult = service.addInmuebleToCollectionIfMissing(inmuebleCollection, inmueble);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(inmueble);
      });

      it('should add only unique Inmueble to an array', () => {
        const inmuebleArray: IInmueble[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const inmuebleCollection: IInmueble[] = [sampleWithRequiredData];
        expectedResult = service.addInmuebleToCollectionIfMissing(inmuebleCollection, ...inmuebleArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const inmueble: IInmueble = sampleWithRequiredData;
        const inmueble2: IInmueble = sampleWithPartialData;
        expectedResult = service.addInmuebleToCollectionIfMissing([], inmueble, inmueble2);
        expect(expectedResult).toEqual([inmueble, inmueble2]);
      });

      it('should accept null and undefined values', () => {
        const inmueble: IInmueble = sampleWithRequiredData;
        expectedResult = service.addInmuebleToCollectionIfMissing([], null, inmueble, undefined);
        expect(expectedResult).toEqual([inmueble]);
      });

      it('should return initial array if no Inmueble is added', () => {
        const inmuebleCollection: IInmueble[] = [sampleWithRequiredData];
        expectedResult = service.addInmuebleToCollectionIfMissing(inmuebleCollection, undefined, null);
        expect(expectedResult).toEqual(inmuebleCollection);
      });
    });

    describe('compareInmueble', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareInmueble(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: '543d8aac-c8b3-49b6-8d46-362c3dce8741' };
        const entity2 = null;

        const compareResult1 = service.compareInmueble(entity1, entity2);
        const compareResult2 = service.compareInmueble(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: '543d8aac-c8b3-49b6-8d46-362c3dce8741' };
        const entity2 = { id: 'aac20b9f-f3ab-4368-87e6-341753a78d64' };

        const compareResult1 = service.compareInmueble(entity1, entity2);
        const compareResult2 = service.compareInmueble(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: '543d8aac-c8b3-49b6-8d46-362c3dce8741' };
        const entity2 = { id: '543d8aac-c8b3-49b6-8d46-362c3dce8741' };

        const compareResult1 = service.compareInmueble(entity1, entity2);
        const compareResult2 = service.compareInmueble(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
