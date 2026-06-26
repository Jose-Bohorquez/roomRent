import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IMultimediaInmueble, NewMultimediaInmueble } from '../multimedia-inmueble.model';

export type PartialUpdateMultimediaInmueble = Partial<IMultimediaInmueble> & Pick<IMultimediaInmueble, 'id'>;

@Injectable()
export class MultimediaInmueblesService {
  readonly multimediaInmueblesParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly multimediaInmueblesResource = httpResource<IMultimediaInmueble[]>(() => {
    const params = this.multimediaInmueblesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of multimediaInmueble that have been fetched. It is updated when the multimediaInmueblesResource emits a new value.
   * In case of error while fetching the multimediaInmuebles, the signal is set to an empty array.
   */
  readonly multimediaInmuebles = computed(() =>
    this.multimediaInmueblesResource.hasValue() ? this.multimediaInmueblesResource.value() : [],
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/multimedia-inmuebles');
}

@Injectable({ providedIn: 'root' })
export class MultimediaInmuebleService extends MultimediaInmueblesService {
  protected readonly http = inject(HttpClient);

  create(multimediaInmueble: NewMultimediaInmueble): Observable<IMultimediaInmueble> {
    return this.http.post<IMultimediaInmueble>(this.resourceUrl, multimediaInmueble);
  }

  update(multimediaInmueble: IMultimediaInmueble): Observable<IMultimediaInmueble> {
    return this.http.put<IMultimediaInmueble>(
      `${this.resourceUrl}/${encodeURIComponent(this.getMultimediaInmuebleIdentifier(multimediaInmueble))}`,
      multimediaInmueble,
    );
  }

  partialUpdate(multimediaInmueble: PartialUpdateMultimediaInmueble): Observable<IMultimediaInmueble> {
    return this.http.patch<IMultimediaInmueble>(
      `${this.resourceUrl}/${encodeURIComponent(this.getMultimediaInmuebleIdentifier(multimediaInmueble))}`,
      multimediaInmueble,
    );
  }

  find(id: string): Observable<IMultimediaInmueble> {
    return this.http.get<IMultimediaInmueble>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IMultimediaInmueble[]>> {
    const options = createRequestOption(req);
    return this.http.get<IMultimediaInmueble[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getMultimediaInmuebleIdentifier(multimediaInmueble: Pick<IMultimediaInmueble, 'id'>): string {
    return multimediaInmueble.id;
  }

  compareMultimediaInmueble(o1: Pick<IMultimediaInmueble, 'id'> | null, o2: Pick<IMultimediaInmueble, 'id'> | null): boolean {
    return o1 && o2 ? this.getMultimediaInmuebleIdentifier(o1) === this.getMultimediaInmuebleIdentifier(o2) : o1 === o2;
  }

  addMultimediaInmuebleToCollectionIfMissing<Type extends Pick<IMultimediaInmueble, 'id'>>(
    multimediaInmuebleCollection: Type[],
    ...multimediaInmueblesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const multimediaInmuebles: Type[] = multimediaInmueblesToCheck.filter(isPresent);
    if (multimediaInmuebles.length > 0) {
      const multimediaInmuebleCollectionIdentifiers = multimediaInmuebleCollection.map(multimediaInmuebleItem =>
        this.getMultimediaInmuebleIdentifier(multimediaInmuebleItem),
      );
      const multimediaInmueblesToAdd = multimediaInmuebles.filter(multimediaInmuebleItem => {
        const multimediaInmuebleIdentifier = this.getMultimediaInmuebleIdentifier(multimediaInmuebleItem);
        if (multimediaInmuebleCollectionIdentifiers.includes(multimediaInmuebleIdentifier)) {
          return false;
        }
        multimediaInmuebleCollectionIdentifiers.push(multimediaInmuebleIdentifier);
        return true;
      });
      return [...multimediaInmueblesToAdd, ...multimediaInmuebleCollection];
    }
    return multimediaInmuebleCollection;
  }
}
