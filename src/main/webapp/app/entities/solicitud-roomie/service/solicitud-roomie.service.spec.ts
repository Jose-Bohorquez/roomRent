import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ISolicitudRoomie } from '../solicitud-roomie.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../solicitud-roomie.test-samples';

import { RestSolicitudRoomie, SolicitudRoomieService } from './solicitud-roomie.service';

const requireRestSample: RestSolicitudRoomie = {
  ...sampleWithRequiredData,
  fechaCreacion: sampleWithRequiredData.fechaCreacion?.toJSON(),
};

describe('SolicitudRoomie Service', () => {
  let service: SolicitudRoomieService;
  let httpMock: HttpTestingController;
  let expectedResult: ISolicitudRoomie | ISolicitudRoomie[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(SolicitudRoomieService);
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

    it('should create a SolicitudRoomie', () => {
      const solicitudRoomie = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(solicitudRoomie).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SolicitudRoomie', () => {
      const solicitudRoomie = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(solicitudRoomie).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SolicitudRoomie', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SolicitudRoomie', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SolicitudRoomie', () => {
      service.delete('ABC').subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addSolicitudRoomieToCollectionIfMissing', () => {
      it('should add a SolicitudRoomie to an empty array', () => {
        const solicitudRoomie: ISolicitudRoomie = sampleWithRequiredData;
        expectedResult = service.addSolicitudRoomieToCollectionIfMissing([], solicitudRoomie);
        expect(expectedResult).toEqual([solicitudRoomie]);
      });

      it('should not add a SolicitudRoomie to an array that contains it', () => {
        const solicitudRoomie: ISolicitudRoomie = sampleWithRequiredData;
        const solicitudRoomieCollection: ISolicitudRoomie[] = [
          {
            ...solicitudRoomie,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSolicitudRoomieToCollectionIfMissing(solicitudRoomieCollection, solicitudRoomie);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SolicitudRoomie to an array that doesn't contain it", () => {
        const solicitudRoomie: ISolicitudRoomie = sampleWithRequiredData;
        const solicitudRoomieCollection: ISolicitudRoomie[] = [sampleWithPartialData];
        expectedResult = service.addSolicitudRoomieToCollectionIfMissing(solicitudRoomieCollection, solicitudRoomie);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(solicitudRoomie);
      });

      it('should add only unique SolicitudRoomie to an array', () => {
        const solicitudRoomieArray: ISolicitudRoomie[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const solicitudRoomieCollection: ISolicitudRoomie[] = [sampleWithRequiredData];
        expectedResult = service.addSolicitudRoomieToCollectionIfMissing(solicitudRoomieCollection, ...solicitudRoomieArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const solicitudRoomie: ISolicitudRoomie = sampleWithRequiredData;
        const solicitudRoomie2: ISolicitudRoomie = sampleWithPartialData;
        expectedResult = service.addSolicitudRoomieToCollectionIfMissing([], solicitudRoomie, solicitudRoomie2);
        expect(expectedResult).toEqual([solicitudRoomie, solicitudRoomie2]);
      });

      it('should accept null and undefined values', () => {
        const solicitudRoomie: ISolicitudRoomie = sampleWithRequiredData;
        expectedResult = service.addSolicitudRoomieToCollectionIfMissing([], null, solicitudRoomie, undefined);
        expect(expectedResult).toEqual([solicitudRoomie]);
      });

      it('should return initial array if no SolicitudRoomie is added', () => {
        const solicitudRoomieCollection: ISolicitudRoomie[] = [sampleWithRequiredData];
        expectedResult = service.addSolicitudRoomieToCollectionIfMissing(solicitudRoomieCollection, undefined, null);
        expect(expectedResult).toEqual(solicitudRoomieCollection);
      });
    });

    describe('compareSolicitudRoomie', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSolicitudRoomie(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: '7323fa9a-b571-4220-b2ec-5732900ce29a' };
        const entity2 = null;

        const compareResult1 = service.compareSolicitudRoomie(entity1, entity2);
        const compareResult2 = service.compareSolicitudRoomie(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: '7323fa9a-b571-4220-b2ec-5732900ce29a' };
        const entity2 = { id: 'd94206a4-850f-42fb-9e4d-098c9fc04793' };

        const compareResult1 = service.compareSolicitudRoomie(entity1, entity2);
        const compareResult2 = service.compareSolicitudRoomie(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: '7323fa9a-b571-4220-b2ec-5732900ce29a' };
        const entity2 = { id: '7323fa9a-b571-4220-b2ec-5732900ce29a' };

        const compareResult1 = service.compareSolicitudRoomie(entity1, entity2);
        const compareResult2 = service.compareSolicitudRoomie(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
