import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ISolicitudRoomie, NewSolicitudRoomie } from '../solicitud-roomie.model';

export type PartialUpdateSolicitudRoomie = Partial<ISolicitudRoomie> & Pick<ISolicitudRoomie, 'id'>;

type RestOf<T extends ISolicitudRoomie | NewSolicitudRoomie> = Omit<T, 'fechaCreacion'> & {
  fechaCreacion?: string | null;
};

export type RestSolicitudRoomie = RestOf<ISolicitudRoomie>;

export type NewRestSolicitudRoomie = RestOf<NewSolicitudRoomie>;

export type PartialUpdateRestSolicitudRoomie = RestOf<PartialUpdateSolicitudRoomie>;

@Injectable()
export class SolicitudRoomiesService {
  readonly solicitudRoomiesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly solicitudRoomiesResource = httpResource<RestSolicitudRoomie[]>(() => {
    const params = this.solicitudRoomiesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of solicitudRoomie that have been fetched. It is updated when the solicitudRoomiesResource emits a new value.
   * In case of error while fetching the solicitudRoomies, the signal is set to an empty array.
   */
  readonly solicitudRoomies = computed(() =>
    (this.solicitudRoomiesResource.hasValue() ? this.solicitudRoomiesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/solicitud-roomies');

  protected convertValueFromServer(restSolicitudRoomie: RestSolicitudRoomie): ISolicitudRoomie {
    return {
      ...restSolicitudRoomie,
      fechaCreacion: restSolicitudRoomie.fechaCreacion ? dayjs(restSolicitudRoomie.fechaCreacion) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class SolicitudRoomieService extends SolicitudRoomiesService {
  protected readonly http = inject(HttpClient);

  create(solicitudRoomie: NewSolicitudRoomie): Observable<ISolicitudRoomie> {
    const copy = this.convertValueFromClient(solicitudRoomie);
    return this.http.post<RestSolicitudRoomie>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(solicitudRoomie: ISolicitudRoomie): Observable<ISolicitudRoomie> {
    const copy = this.convertValueFromClient(solicitudRoomie);
    return this.http
      .put<RestSolicitudRoomie>(`${this.resourceUrl}/${encodeURIComponent(this.getSolicitudRoomieIdentifier(solicitudRoomie))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(solicitudRoomie: PartialUpdateSolicitudRoomie): Observable<ISolicitudRoomie> {
    const copy = this.convertValueFromClient(solicitudRoomie);
    return this.http
      .patch<RestSolicitudRoomie>(`${this.resourceUrl}/${encodeURIComponent(this.getSolicitudRoomieIdentifier(solicitudRoomie))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: string): Observable<ISolicitudRoomie> {
    return this.http
      .get<RestSolicitudRoomie>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ISolicitudRoomie[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestSolicitudRoomie[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: string): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getSolicitudRoomieIdentifier(solicitudRoomie: Pick<ISolicitudRoomie, 'id'>): string {
    return solicitudRoomie.id;
  }

  compareSolicitudRoomie(o1: Pick<ISolicitudRoomie, 'id'> | null, o2: Pick<ISolicitudRoomie, 'id'> | null): boolean {
    return o1 && o2 ? this.getSolicitudRoomieIdentifier(o1) === this.getSolicitudRoomieIdentifier(o2) : o1 === o2;
  }

  addSolicitudRoomieToCollectionIfMissing<Type extends Pick<ISolicitudRoomie, 'id'>>(
    solicitudRoomieCollection: Type[],
    ...solicitudRoomiesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const solicitudRoomies: Type[] = solicitudRoomiesToCheck.filter(isPresent);
    if (solicitudRoomies.length > 0) {
      const solicitudRoomieCollectionIdentifiers = solicitudRoomieCollection.map(solicitudRoomieItem =>
        this.getSolicitudRoomieIdentifier(solicitudRoomieItem),
      );
      const solicitudRoomiesToAdd = solicitudRoomies.filter(solicitudRoomieItem => {
        const solicitudRoomieIdentifier = this.getSolicitudRoomieIdentifier(solicitudRoomieItem);
        if (solicitudRoomieCollectionIdentifiers.includes(solicitudRoomieIdentifier)) {
          return false;
        }
        solicitudRoomieCollectionIdentifiers.push(solicitudRoomieIdentifier);
        return true;
      });
      return [...solicitudRoomiesToAdd, ...solicitudRoomieCollection];
    }
    return solicitudRoomieCollection;
  }

  protected convertValueFromClient<T extends ISolicitudRoomie | NewSolicitudRoomie | PartialUpdateSolicitudRoomie>(
    solicitudRoomie: T,
  ): RestOf<T> {
    return {
      ...solicitudRoomie,
      fechaCreacion: solicitudRoomie.fechaCreacion?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestSolicitudRoomie): ISolicitudRoomie {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestSolicitudRoomie[]): ISolicitudRoomie[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
