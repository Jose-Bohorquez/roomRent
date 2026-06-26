import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IMultimediaInmueble } from '../multimedia-inmueble.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../multimedia-inmueble.test-samples';

import { MultimediaInmuebleService } from './multimedia-inmueble.service';

const requireRestSample: IMultimediaInmueble = {
  ...sampleWithRequiredData,
};

describe('MultimediaInmueble Service', () => {
  let service: MultimediaInmuebleService;
  let httpMock: HttpTestingController;
  let expectedResult: IMultimediaInmueble | IMultimediaInmueble[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(MultimediaInmuebleService);
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

    it('should create a MultimediaInmueble', () => {
      const multimediaInmueble = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(multimediaInmueble).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MultimediaInmueble', () => {
      const multimediaInmueble = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(multimediaInmueble).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MultimediaInmueble', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MultimediaInmueble', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a MultimediaInmueble', () => {
      service.delete('ABC').subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addMultimediaInmuebleToCollectionIfMissing', () => {
      it('should add a MultimediaInmueble to an empty array', () => {
        const multimediaInmueble: IMultimediaInmueble = sampleWithRequiredData;
        expectedResult = service.addMultimediaInmuebleToCollectionIfMissing([], multimediaInmueble);
        expect(expectedResult).toEqual([multimediaInmueble]);
      });

      it('should not add a MultimediaInmueble to an array that contains it', () => {
        const multimediaInmueble: IMultimediaInmueble = sampleWithRequiredData;
        const multimediaInmuebleCollection: IMultimediaInmueble[] = [
          {
            ...multimediaInmueble,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMultimediaInmuebleToCollectionIfMissing(multimediaInmuebleCollection, multimediaInmueble);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MultimediaInmueble to an array that doesn't contain it", () => {
        const multimediaInmueble: IMultimediaInmueble = sampleWithRequiredData;
        const multimediaInmuebleCollection: IMultimediaInmueble[] = [sampleWithPartialData];
        expectedResult = service.addMultimediaInmuebleToCollectionIfMissing(multimediaInmuebleCollection, multimediaInmueble);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(multimediaInmueble);
      });

      it('should add only unique MultimediaInmueble to an array', () => {
        const multimediaInmuebleArray: IMultimediaInmueble[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const multimediaInmuebleCollection: IMultimediaInmueble[] = [sampleWithRequiredData];
        expectedResult = service.addMultimediaInmuebleToCollectionIfMissing(multimediaInmuebleCollection, ...multimediaInmuebleArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const multimediaInmueble: IMultimediaInmueble = sampleWithRequiredData;
        const multimediaInmueble2: IMultimediaInmueble = sampleWithPartialData;
        expectedResult = service.addMultimediaInmuebleToCollectionIfMissing([], multimediaInmueble, multimediaInmueble2);
        expect(expectedResult).toEqual([multimediaInmueble, multimediaInmueble2]);
      });

      it('should accept null and undefined values', () => {
        const multimediaInmueble: IMultimediaInmueble = sampleWithRequiredData;
        expectedResult = service.addMultimediaInmuebleToCollectionIfMissing([], null, multimediaInmueble, undefined);
        expect(expectedResult).toEqual([multimediaInmueble]);
      });

      it('should return initial array if no MultimediaInmueble is added', () => {
        const multimediaInmuebleCollection: IMultimediaInmueble[] = [sampleWithRequiredData];
        expectedResult = service.addMultimediaInmuebleToCollectionIfMissing(multimediaInmuebleCollection, undefined, null);
        expect(expectedResult).toEqual(multimediaInmuebleCollection);
      });
    });

    describe('compareMultimediaInmueble', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMultimediaInmueble(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: '95963ca7-cd69-4cfc-a654-20ca871c5539' };
        const entity2 = null;

        const compareResult1 = service.compareMultimediaInmueble(entity1, entity2);
        const compareResult2 = service.compareMultimediaInmueble(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: '95963ca7-cd69-4cfc-a654-20ca871c5539' };
        const entity2 = { id: 'ba0d3791-dc39-4385-9bc1-7e1dc59f61c9' };

        const compareResult1 = service.compareMultimediaInmueble(entity1, entity2);
        const compareResult2 = service.compareMultimediaInmueble(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: '95963ca7-cd69-4cfc-a654-20ca871c5539' };
        const entity2 = { id: '95963ca7-cd69-4cfc-a654-20ca871c5539' };

        const compareResult1 = service.compareMultimediaInmueble(entity1, entity2);
        const compareResult2 = service.compareMultimediaInmueble(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
