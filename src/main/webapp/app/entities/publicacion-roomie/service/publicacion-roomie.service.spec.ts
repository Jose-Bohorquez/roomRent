import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IPublicacionRoomie } from '../publicacion-roomie.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../publicacion-roomie.test-samples';

import { PublicacionRoomieService, RestPublicacionRoomie } from './publicacion-roomie.service';

const requireRestSample: RestPublicacionRoomie = {
  ...sampleWithRequiredData,
  fechaDisponible: sampleWithRequiredData.fechaDisponible?.format(DATE_FORMAT),
};

describe('PublicacionRoomie Service', () => {
  let service: PublicacionRoomieService;
  let httpMock: HttpTestingController;
  let expectedResult: IPublicacionRoomie | IPublicacionRoomie[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PublicacionRoomieService);
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

    it('should create a PublicacionRoomie', () => {
      const publicacionRoomie = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(publicacionRoomie).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PublicacionRoomie', () => {
      const publicacionRoomie = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(publicacionRoomie).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PublicacionRoomie', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PublicacionRoomie', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PublicacionRoomie', () => {
      service.delete('ABC').subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addPublicacionRoomieToCollectionIfMissing', () => {
      it('should add a PublicacionRoomie to an empty array', () => {
        const publicacionRoomie: IPublicacionRoomie = sampleWithRequiredData;
        expectedResult = service.addPublicacionRoomieToCollectionIfMissing([], publicacionRoomie);
        expect(expectedResult).toEqual([publicacionRoomie]);
      });

      it('should not add a PublicacionRoomie to an array that contains it', () => {
        const publicacionRoomie: IPublicacionRoomie = sampleWithRequiredData;
        const publicacionRoomieCollection: IPublicacionRoomie[] = [
          {
            ...publicacionRoomie,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPublicacionRoomieToCollectionIfMissing(publicacionRoomieCollection, publicacionRoomie);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PublicacionRoomie to an array that doesn't contain it", () => {
        const publicacionRoomie: IPublicacionRoomie = sampleWithRequiredData;
        const publicacionRoomieCollection: IPublicacionRoomie[] = [sampleWithPartialData];
        expectedResult = service.addPublicacionRoomieToCollectionIfMissing(publicacionRoomieCollection, publicacionRoomie);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(publicacionRoomie);
      });

      it('should add only unique PublicacionRoomie to an array', () => {
        const publicacionRoomieArray: IPublicacionRoomie[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const publicacionRoomieCollection: IPublicacionRoomie[] = [sampleWithRequiredData];
        expectedResult = service.addPublicacionRoomieToCollectionIfMissing(publicacionRoomieCollection, ...publicacionRoomieArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const publicacionRoomie: IPublicacionRoomie = sampleWithRequiredData;
        const publicacionRoomie2: IPublicacionRoomie = sampleWithPartialData;
        expectedResult = service.addPublicacionRoomieToCollectionIfMissing([], publicacionRoomie, publicacionRoomie2);
        expect(expectedResult).toEqual([publicacionRoomie, publicacionRoomie2]);
      });

      it('should accept null and undefined values', () => {
        const publicacionRoomie: IPublicacionRoomie = sampleWithRequiredData;
        expectedResult = service.addPublicacionRoomieToCollectionIfMissing([], null, publicacionRoomie, undefined);
        expect(expectedResult).toEqual([publicacionRoomie]);
      });

      it('should return initial array if no PublicacionRoomie is added', () => {
        const publicacionRoomieCollection: IPublicacionRoomie[] = [sampleWithRequiredData];
        expectedResult = service.addPublicacionRoomieToCollectionIfMissing(publicacionRoomieCollection, undefined, null);
        expect(expectedResult).toEqual(publicacionRoomieCollection);
      });
    });

    describe('comparePublicacionRoomie', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePublicacionRoomie(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: '453c2e6e-bf4a-4f0e-be12-a3257c9b7249' };
        const entity2 = null;

        const compareResult1 = service.comparePublicacionRoomie(entity1, entity2);
        const compareResult2 = service.comparePublicacionRoomie(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: '453c2e6e-bf4a-4f0e-be12-a3257c9b7249' };
        const entity2 = { id: 'fb311192-a40d-4f42-868b-bcd3f9a52e76' };

        const compareResult1 = service.comparePublicacionRoomie(entity1, entity2);
        const compareResult2 = service.comparePublicacionRoomie(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: '453c2e6e-bf4a-4f0e-be12-a3257c9b7249' };
        const entity2 = { id: '453c2e6e-bf4a-4f0e-be12-a3257c9b7249' };

        const compareResult1 = service.comparePublicacionRoomie(entity1, entity2);
        const compareResult2 = service.comparePublicacionRoomie(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
