import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ICalificacion } from '../calificacion.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../calificacion.test-samples';

import { CalificacionService, RestCalificacion } from './calificacion.service';

const requireRestSample: RestCalificacion = {
  ...sampleWithRequiredData,
  fechaCreacion: sampleWithRequiredData.fechaCreacion?.toJSON(),
};

describe('Calificacion Service', () => {
  let service: CalificacionService;
  let httpMock: HttpTestingController;
  let expectedResult: ICalificacion | ICalificacion[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CalificacionService);
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

    it('should create a Calificacion', () => {
      const calificacion = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(calificacion).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Calificacion', () => {
      const calificacion = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(calificacion).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Calificacion', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Calificacion', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Calificacion', () => {
      service.delete('ABC').subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addCalificacionToCollectionIfMissing', () => {
      it('should add a Calificacion to an empty array', () => {
        const calificacion: ICalificacion = sampleWithRequiredData;
        expectedResult = service.addCalificacionToCollectionIfMissing([], calificacion);
        expect(expectedResult).toEqual([calificacion]);
      });

      it('should not add a Calificacion to an array that contains it', () => {
        const calificacion: ICalificacion = sampleWithRequiredData;
        const calificacionCollection: ICalificacion[] = [
          {
            ...calificacion,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCalificacionToCollectionIfMissing(calificacionCollection, calificacion);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Calificacion to an array that doesn't contain it", () => {
        const calificacion: ICalificacion = sampleWithRequiredData;
        const calificacionCollection: ICalificacion[] = [sampleWithPartialData];
        expectedResult = service.addCalificacionToCollectionIfMissing(calificacionCollection, calificacion);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(calificacion);
      });

      it('should add only unique Calificacion to an array', () => {
        const calificacionArray: ICalificacion[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const calificacionCollection: ICalificacion[] = [sampleWithRequiredData];
        expectedResult = service.addCalificacionToCollectionIfMissing(calificacionCollection, ...calificacionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const calificacion: ICalificacion = sampleWithRequiredData;
        const calificacion2: ICalificacion = sampleWithPartialData;
        expectedResult = service.addCalificacionToCollectionIfMissing([], calificacion, calificacion2);
        expect(expectedResult).toEqual([calificacion, calificacion2]);
      });

      it('should accept null and undefined values', () => {
        const calificacion: ICalificacion = sampleWithRequiredData;
        expectedResult = service.addCalificacionToCollectionIfMissing([], null, calificacion, undefined);
        expect(expectedResult).toEqual([calificacion]);
      });

      it('should return initial array if no Calificacion is added', () => {
        const calificacionCollection: ICalificacion[] = [sampleWithRequiredData];
        expectedResult = service.addCalificacionToCollectionIfMissing(calificacionCollection, undefined, null);
        expect(expectedResult).toEqual(calificacionCollection);
      });
    });

    describe('compareCalificacion', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCalificacion(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: '08e53216-3dd8-41d0-878d-1342a0f024ae' };
        const entity2 = null;

        const compareResult1 = service.compareCalificacion(entity1, entity2);
        const compareResult2 = service.compareCalificacion(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: '08e53216-3dd8-41d0-878d-1342a0f024ae' };
        const entity2 = { id: 'f70b8935-bc54-4466-a147-ab60ec4c120f' };

        const compareResult1 = service.compareCalificacion(entity1, entity2);
        const compareResult2 = service.compareCalificacion(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: '08e53216-3dd8-41d0-878d-1342a0f024ae' };
        const entity2 = { id: '08e53216-3dd8-41d0-878d-1342a0f024ae' };

        const compareResult1 = service.compareCalificacion(entity1, entity2);
        const compareResult2 = service.compareCalificacion(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
