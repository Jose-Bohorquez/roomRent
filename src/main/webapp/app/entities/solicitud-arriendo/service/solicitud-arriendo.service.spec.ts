import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ISolicitudArriendo } from '../solicitud-arriendo.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../solicitud-arriendo.test-samples';

import { RestSolicitudArriendo, SolicitudArriendoService } from './solicitud-arriendo.service';

const requireRestSample: RestSolicitudArriendo = {
  ...sampleWithRequiredData,
  fechaCreacion: sampleWithRequiredData.fechaCreacion?.toJSON(),
};

describe('SolicitudArriendo Service', () => {
  let service: SolicitudArriendoService;
  let httpMock: HttpTestingController;
  let expectedResult: ISolicitudArriendo | ISolicitudArriendo[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(SolicitudArriendoService);
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

    it('should create a SolicitudArriendo', () => {
      const solicitudArriendo = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(solicitudArriendo).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SolicitudArriendo', () => {
      const solicitudArriendo = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(solicitudArriendo).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SolicitudArriendo', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SolicitudArriendo', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SolicitudArriendo', () => {
      service.delete('ABC').subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addSolicitudArriendoToCollectionIfMissing', () => {
      it('should add a SolicitudArriendo to an empty array', () => {
        const solicitudArriendo: ISolicitudArriendo = sampleWithRequiredData;
        expectedResult = service.addSolicitudArriendoToCollectionIfMissing([], solicitudArriendo);
        expect(expectedResult).toEqual([solicitudArriendo]);
      });

      it('should not add a SolicitudArriendo to an array that contains it', () => {
        const solicitudArriendo: ISolicitudArriendo = sampleWithRequiredData;
        const solicitudArriendoCollection: ISolicitudArriendo[] = [
          {
            ...solicitudArriendo,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSolicitudArriendoToCollectionIfMissing(solicitudArriendoCollection, solicitudArriendo);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SolicitudArriendo to an array that doesn't contain it", () => {
        const solicitudArriendo: ISolicitudArriendo = sampleWithRequiredData;
        const solicitudArriendoCollection: ISolicitudArriendo[] = [sampleWithPartialData];
        expectedResult = service.addSolicitudArriendoToCollectionIfMissing(solicitudArriendoCollection, solicitudArriendo);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(solicitudArriendo);
      });

      it('should add only unique SolicitudArriendo to an array', () => {
        const solicitudArriendoArray: ISolicitudArriendo[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const solicitudArriendoCollection: ISolicitudArriendo[] = [sampleWithRequiredData];
        expectedResult = service.addSolicitudArriendoToCollectionIfMissing(solicitudArriendoCollection, ...solicitudArriendoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const solicitudArriendo: ISolicitudArriendo = sampleWithRequiredData;
        const solicitudArriendo2: ISolicitudArriendo = sampleWithPartialData;
        expectedResult = service.addSolicitudArriendoToCollectionIfMissing([], solicitudArriendo, solicitudArriendo2);
        expect(expectedResult).toEqual([solicitudArriendo, solicitudArriendo2]);
      });

      it('should accept null and undefined values', () => {
        const solicitudArriendo: ISolicitudArriendo = sampleWithRequiredData;
        expectedResult = service.addSolicitudArriendoToCollectionIfMissing([], null, solicitudArriendo, undefined);
        expect(expectedResult).toEqual([solicitudArriendo]);
      });

      it('should return initial array if no SolicitudArriendo is added', () => {
        const solicitudArriendoCollection: ISolicitudArriendo[] = [sampleWithRequiredData];
        expectedResult = service.addSolicitudArriendoToCollectionIfMissing(solicitudArriendoCollection, undefined, null);
        expect(expectedResult).toEqual(solicitudArriendoCollection);
      });
    });

    describe('compareSolicitudArriendo', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSolicitudArriendo(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: '1c33f7d7-8f08-4ba7-b15d-c02a593be236' };
        const entity2 = null;

        const compareResult1 = service.compareSolicitudArriendo(entity1, entity2);
        const compareResult2 = service.compareSolicitudArriendo(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: '1c33f7d7-8f08-4ba7-b15d-c02a593be236' };
        const entity2 = { id: '1a700994-9cbb-44dc-973b-332589a546e1' };

        const compareResult1 = service.compareSolicitudArriendo(entity1, entity2);
        const compareResult2 = service.compareSolicitudArriendo(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: '1c33f7d7-8f08-4ba7-b15d-c02a593be236' };
        const entity2 = { id: '1c33f7d7-8f08-4ba7-b15d-c02a593be236' };

        const compareResult1 = service.compareSolicitudArriendo(entity1, entity2);
        const compareResult2 = service.compareSolicitudArriendo(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
