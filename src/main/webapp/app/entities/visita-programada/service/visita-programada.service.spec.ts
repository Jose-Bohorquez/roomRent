import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IVisitaProgramada } from '../visita-programada.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../visita-programada.test-samples';

import { RestVisitaProgramada, VisitaProgramadaService } from './visita-programada.service';

const requireRestSample: RestVisitaProgramada = {
  ...sampleWithRequiredData,
  fechaSolicitada: sampleWithRequiredData.fechaSolicitada?.toJSON(),
  fechaConfirmada: sampleWithRequiredData.fechaConfirmada?.toJSON(),
};

describe('VisitaProgramada Service', () => {
  let service: VisitaProgramadaService;
  let httpMock: HttpTestingController;
  let expectedResult: IVisitaProgramada | IVisitaProgramada[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(VisitaProgramadaService);
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

    it('should create a VisitaProgramada', () => {
      const visitaProgramada = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(visitaProgramada).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a VisitaProgramada', () => {
      const visitaProgramada = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(visitaProgramada).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a VisitaProgramada', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of VisitaProgramada', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a VisitaProgramada', () => {
      service.delete('ABC').subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addVisitaProgramadaToCollectionIfMissing', () => {
      it('should add a VisitaProgramada to an empty array', () => {
        const visitaProgramada: IVisitaProgramada = sampleWithRequiredData;
        expectedResult = service.addVisitaProgramadaToCollectionIfMissing([], visitaProgramada);
        expect(expectedResult).toEqual([visitaProgramada]);
      });

      it('should not add a VisitaProgramada to an array that contains it', () => {
        const visitaProgramada: IVisitaProgramada = sampleWithRequiredData;
        const visitaProgramadaCollection: IVisitaProgramada[] = [
          {
            ...visitaProgramada,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addVisitaProgramadaToCollectionIfMissing(visitaProgramadaCollection, visitaProgramada);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a VisitaProgramada to an array that doesn't contain it", () => {
        const visitaProgramada: IVisitaProgramada = sampleWithRequiredData;
        const visitaProgramadaCollection: IVisitaProgramada[] = [sampleWithPartialData];
        expectedResult = service.addVisitaProgramadaToCollectionIfMissing(visitaProgramadaCollection, visitaProgramada);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(visitaProgramada);
      });

      it('should add only unique VisitaProgramada to an array', () => {
        const visitaProgramadaArray: IVisitaProgramada[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const visitaProgramadaCollection: IVisitaProgramada[] = [sampleWithRequiredData];
        expectedResult = service.addVisitaProgramadaToCollectionIfMissing(visitaProgramadaCollection, ...visitaProgramadaArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const visitaProgramada: IVisitaProgramada = sampleWithRequiredData;
        const visitaProgramada2: IVisitaProgramada = sampleWithPartialData;
        expectedResult = service.addVisitaProgramadaToCollectionIfMissing([], visitaProgramada, visitaProgramada2);
        expect(expectedResult).toEqual([visitaProgramada, visitaProgramada2]);
      });

      it('should accept null and undefined values', () => {
        const visitaProgramada: IVisitaProgramada = sampleWithRequiredData;
        expectedResult = service.addVisitaProgramadaToCollectionIfMissing([], null, visitaProgramada, undefined);
        expect(expectedResult).toEqual([visitaProgramada]);
      });

      it('should return initial array if no VisitaProgramada is added', () => {
        const visitaProgramadaCollection: IVisitaProgramada[] = [sampleWithRequiredData];
        expectedResult = service.addVisitaProgramadaToCollectionIfMissing(visitaProgramadaCollection, undefined, null);
        expect(expectedResult).toEqual(visitaProgramadaCollection);
      });
    });

    describe('compareVisitaProgramada', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareVisitaProgramada(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: '14c3d200-8ecb-4df4-9dbe-2c8c538f2668' };
        const entity2 = null;

        const compareResult1 = service.compareVisitaProgramada(entity1, entity2);
        const compareResult2 = service.compareVisitaProgramada(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: '14c3d200-8ecb-4df4-9dbe-2c8c538f2668' };
        const entity2 = { id: '3aa6b388-cba8-4d3d-a6c5-c9d420c3ace7' };

        const compareResult1 = service.compareVisitaProgramada(entity1, entity2);
        const compareResult2 = service.compareVisitaProgramada(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: '14c3d200-8ecb-4df4-9dbe-2c8c538f2668' };
        const entity2 = { id: '14c3d200-8ecb-4df4-9dbe-2c8c538f2668' };

        const compareResult1 = service.compareVisitaProgramada(entity1, entity2);
        const compareResult2 = service.compareVisitaProgramada(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
