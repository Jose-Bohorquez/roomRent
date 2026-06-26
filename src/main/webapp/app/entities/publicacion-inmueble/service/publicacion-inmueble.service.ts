import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IPublicacionInmueble, NewPublicacionInmueble } from '../publicacion-inmueble.model';

export type PartialUpdatePublicacionInmueble = Partial<IPublicacionInmueble> & Pick<IPublicacionInmueble, 'id'>;

type RestOf<T extends IPublicacionInmueble | NewPublicacionInmueble> = Omit<T, 'fechaDisponible'> & {
  fechaDisponible?: string | null;
};

export type RestPublicacionInmueble = RestOf<IPublicacionInmueble>;

export type NewRestPublicacionInmueble = RestOf<NewPublicacionInmueble>;

export type PartialUpdateRestPublicacionInmueble = RestOf<PartialUpdatePublicacionInmueble>;

@Injectable()
export class PublicacionInmueblesService {
  readonly publicacionInmueblesParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly publicacionInmueblesResource = httpResource<RestPublicacionInmueble[]>(() => {
    const params = this.publicacionInmueblesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of publicacionInmueble that have been fetched. It is updated when the publicacionInmueblesResource emits a new value.
   * In case of error while fetching the publicacionInmuebles, the signal is set to an empty array.
   */
  readonly publicacionInmuebles = computed(() =>
    (this.publicacionInmueblesResource.hasValue() ? this.publicacionInmueblesResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/publicacion-inmuebles');

  protected convertValueFromServer(restPublicacionInmueble: RestPublicacionInmueble): IPublicacionInmueble {
    return {
      ...restPublicacionInmueble,
      fechaDisponible: restPublicacionInmueble.fechaDisponible ? dayjs(restPublicacionInmueble.fechaDisponible) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class PublicacionInmuebleService extends PublicacionInmueblesService {
  protected readonly http = inject(HttpClient);

  create(publicacionInmueble: NewPublicacionInmueble): Observable<IPublicacionInmueble> {
    const copy = this.convertValueFromClient(publicacionInmueble);
    return this.http.post<RestPublicacionInmueble>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(publicacionInmueble: IPublicacionInmueble): Observable<IPublicacionInmueble> {
    const copy = this.convertValueFromClient(publicacionInmueble);
    return this.http
      .put<RestPublicacionInmueble>(
        `${this.resourceUrl}/${encodeURIComponent(this.getPublicacionInmuebleIdentifier(publicacionInmueble))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(publicacionInmueble: PartialUpdatePublicacionInmueble): Observable<IPublicacionInmueble> {
    const copy = this.convertValueFromClient(publicacionInmueble);
    return this.http
      .patch<RestPublicacionInmueble>(
        `${this.resourceUrl}/${encodeURIComponent(this.getPublicacionInmuebleIdentifier(publicacionInmueble))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: string): Observable<IPublicacionInmueble> {
    return this.http
      .get<RestPublicacionInmueble>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IPublicacionInmueble[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPublicacionInmueble[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: string): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getPublicacionInmuebleIdentifier(publicacionInmueble: Pick<IPublicacionInmueble, 'id'>): string {
    return publicacionInmueble.id;
  }

  comparePublicacionInmueble(o1: Pick<IPublicacionInmueble, 'id'> | null, o2: Pick<IPublicacionInmueble, 'id'> | null): boolean {
    return o1 && o2 ? this.getPublicacionInmuebleIdentifier(o1) === this.getPublicacionInmuebleIdentifier(o2) : o1 === o2;
  }

  addPublicacionInmuebleToCollectionIfMissing<Type extends Pick<IPublicacionInmueble, 'id'>>(
    publicacionInmuebleCollection: Type[],
    ...publicacionInmueblesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const publicacionInmuebles: Type[] = publicacionInmueblesToCheck.filter(isPresent);
    if (publicacionInmuebles.length > 0) {
      const publicacionInmuebleCollectionIdentifiers = publicacionInmuebleCollection.map(publicacionInmuebleItem =>
        this.getPublicacionInmuebleIdentifier(publicacionInmuebleItem),
      );
      const publicacionInmueblesToAdd = publicacionInmuebles.filter(publicacionInmuebleItem => {
        const publicacionInmuebleIdentifier = this.getPublicacionInmuebleIdentifier(publicacionInmuebleItem);
        if (publicacionInmuebleCollectionIdentifiers.includes(publicacionInmuebleIdentifier)) {
          return false;
        }
        publicacionInmuebleCollectionIdentifiers.push(publicacionInmuebleIdentifier);
        return true;
      });
      return [...publicacionInmueblesToAdd, ...publicacionInmuebleCollection];
    }
    return publicacionInmuebleCollection;
  }

  protected convertValueFromClient<T extends IPublicacionInmueble | NewPublicacionInmueble | PartialUpdatePublicacionInmueble>(
    publicacionInmueble: T,
  ): RestOf<T> {
    return {
      ...publicacionInmueble,
      fechaDisponible: publicacionInmueble.fechaDisponible?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestPublicacionInmueble): IPublicacionInmueble {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestPublicacionInmueble[]): IPublicacionInmueble[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
