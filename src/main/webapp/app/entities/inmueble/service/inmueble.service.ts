import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IInmueble, NewInmueble } from '../inmueble.model';

export type PartialUpdateInmueble = Partial<IInmueble> & Pick<IInmueble, 'id'>;

@Injectable()
export class InmueblesService {
  readonly inmueblesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly inmueblesResource = httpResource<IInmueble[]>(() => {
    const params = this.inmueblesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of inmueble that have been fetched. It is updated when the inmueblesResource emits a new value.
   * In case of error while fetching the inmuebles, the signal is set to an empty array.
   */
  readonly inmuebles = computed(() => (this.inmueblesResource.hasValue() ? this.inmueblesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/inmuebles');
}

@Injectable({ providedIn: 'root' })
export class InmuebleService extends InmueblesService {
  protected readonly http = inject(HttpClient);

  create(inmueble: NewInmueble): Observable<IInmueble> {
    return this.http.post<IInmueble>(this.resourceUrl, inmueble);
  }

  update(inmueble: IInmueble): Observable<IInmueble> {
    return this.http.put<IInmueble>(`${this.resourceUrl}/${encodeURIComponent(this.getInmuebleIdentifier(inmueble))}`, inmueble);
  }

  partialUpdate(inmueble: PartialUpdateInmueble): Observable<IInmueble> {
    return this.http.patch<IInmueble>(`${this.resourceUrl}/${encodeURIComponent(this.getInmuebleIdentifier(inmueble))}`, inmueble);
  }

  find(id: string): Observable<IInmueble> {
    return this.http.get<IInmueble>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IInmueble[]>> {
    const options = createRequestOption(req);
    return this.http.get<IInmueble[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getInmuebleIdentifier(inmueble: Pick<IInmueble, 'id'>): string {
    return inmueble.id;
  }

  compareInmueble(o1: Pick<IInmueble, 'id'> | null, o2: Pick<IInmueble, 'id'> | null): boolean {
    return o1 && o2 ? this.getInmuebleIdentifier(o1) === this.getInmuebleIdentifier(o2) : o1 === o2;
  }

  addInmuebleToCollectionIfMissing<Type extends Pick<IInmueble, 'id'>>(
    inmuebleCollection: Type[],
    ...inmueblesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const inmuebles: Type[] = inmueblesToCheck.filter(isPresent);
    if (inmuebles.length > 0) {
      const inmuebleCollectionIdentifiers = inmuebleCollection.map(inmuebleItem => this.getInmuebleIdentifier(inmuebleItem));
      const inmueblesToAdd = inmuebles.filter(inmuebleItem => {
        const inmuebleIdentifier = this.getInmuebleIdentifier(inmuebleItem);
        if (inmuebleCollectionIdentifiers.includes(inmuebleIdentifier)) {
          return false;
        }
        inmuebleCollectionIdentifiers.push(inmuebleIdentifier);
        return true;
      });
      return [...inmueblesToAdd, ...inmuebleCollection];
    }
    return inmuebleCollection;
  }
}
