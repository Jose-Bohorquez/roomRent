import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ISolicitudArriendo, NewSolicitudArriendo } from '../solicitud-arriendo.model';

export type PartialUpdateSolicitudArriendo = Partial<ISolicitudArriendo> & Pick<ISolicitudArriendo, 'id'>;

type RestOf<T extends ISolicitudArriendo | NewSolicitudArriendo> = Omit<T, 'fechaCreacion'> & {
  fechaCreacion?: string | null;
};

export type RestSolicitudArriendo = RestOf<ISolicitudArriendo>;

export type NewRestSolicitudArriendo = RestOf<NewSolicitudArriendo>;

export type PartialUpdateRestSolicitudArriendo = RestOf<PartialUpdateSolicitudArriendo>;

@Injectable()
export class SolicitudArriendosService {
  readonly solicitudArriendosParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly solicitudArriendosResource = httpResource<RestSolicitudArriendo[]>(() => {
    const params = this.solicitudArriendosParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of solicitudArriendo that have been fetched. It is updated when the solicitudArriendosResource emits a new value.
   * In case of error while fetching the solicitudArriendos, the signal is set to an empty array.
   */
  readonly solicitudArriendos = computed(() =>
    (this.solicitudArriendosResource.hasValue() ? this.solicitudArriendosResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/solicitud-arriendos');

  protected convertValueFromServer(restSolicitudArriendo: RestSolicitudArriendo): ISolicitudArriendo {
    return {
      ...restSolicitudArriendo,
      fechaCreacion: restSolicitudArriendo.fechaCreacion ? dayjs(restSolicitudArriendo.fechaCreacion) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class SolicitudArriendoService extends SolicitudArriendosService {
  protected readonly http = inject(HttpClient);

  create(solicitudArriendo: NewSolicitudArriendo): Observable<ISolicitudArriendo> {
    const copy = this.convertValueFromClient(solicitudArriendo);
    return this.http.post<RestSolicitudArriendo>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(solicitudArriendo: ISolicitudArriendo): Observable<ISolicitudArriendo> {
    const copy = this.convertValueFromClient(solicitudArriendo);
    return this.http
      .put<RestSolicitudArriendo>(`${this.resourceUrl}/${encodeURIComponent(this.getSolicitudArriendoIdentifier(solicitudArriendo))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(solicitudArriendo: PartialUpdateSolicitudArriendo): Observable<ISolicitudArriendo> {
    const copy = this.convertValueFromClient(solicitudArriendo);
    return this.http
      .patch<RestSolicitudArriendo>(
        `${this.resourceUrl}/${encodeURIComponent(this.getSolicitudArriendoIdentifier(solicitudArriendo))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: string): Observable<ISolicitudArriendo> {
    return this.http
      .get<RestSolicitudArriendo>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ISolicitudArriendo[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestSolicitudArriendo[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: string): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getSolicitudArriendoIdentifier(solicitudArriendo: Pick<ISolicitudArriendo, 'id'>): string {
    return solicitudArriendo.id;
  }

  compareSolicitudArriendo(o1: Pick<ISolicitudArriendo, 'id'> | null, o2: Pick<ISolicitudArriendo, 'id'> | null): boolean {
    return o1 && o2 ? this.getSolicitudArriendoIdentifier(o1) === this.getSolicitudArriendoIdentifier(o2) : o1 === o2;
  }

  addSolicitudArriendoToCollectionIfMissing<Type extends Pick<ISolicitudArriendo, 'id'>>(
    solicitudArriendoCollection: Type[],
    ...solicitudArriendosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const solicitudArriendos: Type[] = solicitudArriendosToCheck.filter(isPresent);
    if (solicitudArriendos.length > 0) {
      const solicitudArriendoCollectionIdentifiers = solicitudArriendoCollection.map(solicitudArriendoItem =>
        this.getSolicitudArriendoIdentifier(solicitudArriendoItem),
      );
      const solicitudArriendosToAdd = solicitudArriendos.filter(solicitudArriendoItem => {
        const solicitudArriendoIdentifier = this.getSolicitudArriendoIdentifier(solicitudArriendoItem);
        if (solicitudArriendoCollectionIdentifiers.includes(solicitudArriendoIdentifier)) {
          return false;
        }
        solicitudArriendoCollectionIdentifiers.push(solicitudArriendoIdentifier);
        return true;
      });
      return [...solicitudArriendosToAdd, ...solicitudArriendoCollection];
    }
    return solicitudArriendoCollection;
  }

  protected convertValueFromClient<T extends ISolicitudArriendo | NewSolicitudArriendo | PartialUpdateSolicitudArriendo>(
    solicitudArriendo: T,
  ): RestOf<T> {
    return {
      ...solicitudArriendo,
      fechaCreacion: solicitudArriendo.fechaCreacion?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestSolicitudArriendo): ISolicitudArriendo {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestSolicitudArriendo[]): ISolicitudArriendo[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
