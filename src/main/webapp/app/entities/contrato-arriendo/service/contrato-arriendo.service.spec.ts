import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IContratoArriendo } from '../contrato-arriendo.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../contrato-arriendo.test-samples';

import { ContratoArriendoService, RestContratoArriendo } from './contrato-arriendo.service';

const requireRestSample: RestContratoArriendo = {
  ...sampleWithRequiredData,
  fechaInicio: sampleWithRequiredData.fechaInicio?.format(DATE_FORMAT),
  fechaFin: sampleWithRequiredData.fechaFin?.format(DATE_FORMAT),
  fechaFirma: sampleWithRequiredData.fechaFirma?.toJSON(),
};

describe('ContratoArriendo Service', () => {
  let service: ContratoArriendoService;
  let httpMock: HttpTestingController;
  let expectedResult: IContratoArriendo | IContratoArriendo[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ContratoArriendoService);
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

    it('should create a ContratoArriendo', () => {
      const contratoArriendo = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(contratoArriendo).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ContratoArriendo', () => {
      const contratoArriendo = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(contratoArriendo).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ContratoArriendo', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ContratoArriendo', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ContratoArriendo', () => {
      service.delete('ABC').subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addContratoArriendoToCollectionIfMissing', () => {
      it('should add a ContratoArriendo to an empty array', () => {
        const contratoArriendo: IContratoArriendo = sampleWithRequiredData;
        expectedResult = service.addContratoArriendoToCollectionIfMissing([], contratoArriendo);
        expect(expectedResult).toEqual([contratoArriendo]);
      });

      it('should not add a ContratoArriendo to an array that contains it', () => {
        const contratoArriendo: IContratoArriendo = sampleWithRequiredData;
        const contratoArriendoCollection: IContratoArriendo[] = [
          {
            ...contratoArriendo,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addContratoArriendoToCollectionIfMissing(contratoArriendoCollection, contratoArriendo);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ContratoArriendo to an array that doesn't contain it", () => {
        const contratoArriendo: IContratoArriendo = sampleWithRequiredData;
        const contratoArriendoCollection: IContratoArriendo[] = [sampleWithPartialData];
        expectedResult = service.addContratoArriendoToCollectionIfMissing(contratoArriendoCollection, contratoArriendo);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(contratoArriendo);
      });

      it('should add only unique ContratoArriendo to an array', () => {
        const contratoArriendoArray: IContratoArriendo[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const contratoArriendoCollection: IContratoArriendo[] = [sampleWithRequiredData];
        expectedResult = service.addContratoArriendoToCollectionIfMissing(contratoArriendoCollection, ...contratoArriendoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const contratoArriendo: IContratoArriendo = sampleWithRequiredData;
        const contratoArriendo2: IContratoArriendo = sampleWithPartialData;
        expectedResult = service.addContratoArriendoToCollectionIfMissing([], contratoArriendo, contratoArriendo2);
        expect(expectedResult).toEqual([contratoArriendo, contratoArriendo2]);
      });

      it('should accept null and undefined values', () => {
        const contratoArriendo: IContratoArriendo = sampleWithRequiredData;
        expectedResult = service.addContratoArriendoToCollectionIfMissing([], null, contratoArriendo, undefined);
        expect(expectedResult).toEqual([contratoArriendo]);
      });

      it('should return initial array if no ContratoArriendo is added', () => {
        const contratoArriendoCollection: IContratoArriendo[] = [sampleWithRequiredData];
        expectedResult = service.addContratoArriendoToCollectionIfMissing(contratoArriendoCollection, undefined, null);
        expect(expectedResult).toEqual(contratoArriendoCollection);
      });
    });

    describe('compareContratoArriendo', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareContratoArriendo(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 'f993e4a3-6619-4c5a-be51-34708828262f' };
        const entity2 = null;

        const compareResult1 = service.compareContratoArriendo(entity1, entity2);
        const compareResult2 = service.compareContratoArriendo(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 'f993e4a3-6619-4c5a-be51-34708828262f' };
        const entity2 = { id: 'e8f1f777-29f3-4ba4-b4f1-d1d01d96878f' };

        const compareResult1 = service.compareContratoArriendo(entity1, entity2);
        const compareResult2 = service.compareContratoArriendo(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 'f993e4a3-6619-4c5a-be51-34708828262f' };
        const entity2 = { id: 'f993e4a3-6619-4c5a-be51-34708828262f' };

        const compareResult1 = service.compareContratoArriendo(entity1, entity2);
        const compareResult2 = service.compareContratoArriendo(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
