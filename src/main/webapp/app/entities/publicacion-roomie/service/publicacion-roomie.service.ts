import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IPublicacionRoomie, NewPublicacionRoomie } from '../publicacion-roomie.model';

export type PartialUpdatePublicacionRoomie = Partial<IPublicacionRoomie> & Pick<IPublicacionRoomie, 'id'>;

type RestOf<T extends IPublicacionRoomie | NewPublicacionRoomie> = Omit<T, 'fechaDisponible'> & {
  fechaDisponible?: string | null;
};

export type RestPublicacionRoomie = RestOf<IPublicacionRoomie>;

export type NewRestPublicacionRoomie = RestOf<NewPublicacionRoomie>;

export type PartialUpdateRestPublicacionRoomie = RestOf<PartialUpdatePublicacionRoomie>;

@Injectable()
export class PublicacionRoomiesService {
  readonly publicacionRoomiesParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly publicacionRoomiesResource = httpResource<RestPublicacionRoomie[]>(() => {
    const params = this.publicacionRoomiesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of publicacionRoomie that have been fetched. It is updated when the publicacionRoomiesResource emits a new value.
   * In case of error while fetching the publicacionRoomies, the signal is set to an empty array.
   */
  readonly publicacionRoomies = computed(() =>
    (this.publicacionRoomiesResource.hasValue() ? this.publicacionRoomiesResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/publicacion-roomies');

  protected convertValueFromServer(restPublicacionRoomie: RestPublicacionRoomie): IPublicacionRoomie {
    return {
      ...restPublicacionRoomie,
      fechaDisponible: restPublicacionRoomie.fechaDisponible ? dayjs(restPublicacionRoomie.fechaDisponible) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class PublicacionRoomieService extends PublicacionRoomiesService {
  protected readonly http = inject(HttpClient);

  create(publicacionRoomie: NewPublicacionRoomie): Observable<IPublicacionRoomie> {
    const copy = this.convertValueFromClient(publicacionRoomie);
    return this.http.post<RestPublicacionRoomie>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(publicacionRoomie: IPublicacionRoomie): Observable<IPublicacionRoomie> {
    const copy = this.convertValueFromClient(publicacionRoomie);
    return this.http
      .put<RestPublicacionRoomie>(`${this.resourceUrl}/${encodeURIComponent(this.getPublicacionRoomieIdentifier(publicacionRoomie))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(publicacionRoomie: PartialUpdatePublicacionRoomie): Observable<IPublicacionRoomie> {
    const copy = this.convertValueFromClient(publicacionRoomie);
    return this.http
      .patch<RestPublicacionRoomie>(
        `${this.resourceUrl}/${encodeURIComponent(this.getPublicacionRoomieIdentifier(publicacionRoomie))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: string): Observable<IPublicacionRoomie> {
    return this.http
      .get<RestPublicacionRoomie>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IPublicacionRoomie[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPublicacionRoomie[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: string): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getPublicacionRoomieIdentifier(publicacionRoomie: Pick<IPublicacionRoomie, 'id'>): string {
    return publicacionRoomie.id;
  }

  comparePublicacionRoomie(o1: Pick<IPublicacionRoomie, 'id'> | null, o2: Pick<IPublicacionRoomie, 'id'> | null): boolean {
    return o1 && o2 ? this.getPublicacionRoomieIdentifier(o1) === this.getPublicacionRoomieIdentifier(o2) : o1 === o2;
  }

  addPublicacionRoomieToCollectionIfMissing<Type extends Pick<IPublicacionRoomie, 'id'>>(
    publicacionRoomieCollection: Type[],
    ...publicacionRoomiesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const publicacionRoomies: Type[] = publicacionRoomiesToCheck.filter(isPresent);
    if (publicacionRoomies.length > 0) {
      const publicacionRoomieCollectionIdentifiers = publicacionRoomieCollection.map(publicacionRoomieItem =>
        this.getPublicacionRoomieIdentifier(publicacionRoomieItem),
      );
      const publicacionRoomiesToAdd = publicacionRoomies.filter(publicacionRoomieItem => {
        const publicacionRoomieIdentifier = this.getPublicacionRoomieIdentifier(publicacionRoomieItem);
        if (publicacionRoomieCollectionIdentifiers.includes(publicacionRoomieIdentifier)) {
          return false;
        }
        publicacionRoomieCollectionIdentifiers.push(publicacionRoomieIdentifier);
        return true;
      });
      return [...publicacionRoomiesToAdd, ...publicacionRoomieCollection];
    }
    return publicacionRoomieCollection;
  }

  protected convertValueFromClient<T extends IPublicacionRoomie | NewPublicacionRoomie | PartialUpdatePublicacionRoomie>(
    publicacionRoomie: T,
  ): RestOf<T> {
    return {
      ...publicacionRoomie,
      fechaDisponible: publicacionRoomie.fechaDisponible?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestPublicacionRoomie): IPublicacionRoomie {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestPublicacionRoomie[]): IPublicacionRoomie[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
