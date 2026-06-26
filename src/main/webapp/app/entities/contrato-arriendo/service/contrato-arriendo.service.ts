import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IContratoArriendo, NewContratoArriendo } from '../contrato-arriendo.model';

export type PartialUpdateContratoArriendo = Partial<IContratoArriendo> & Pick<IContratoArriendo, 'id'>;

type RestOf<T extends IContratoArriendo | NewContratoArriendo> = Omit<T, 'fechaInicio' | 'fechaFin' | 'fechaFirma'> & {
  fechaInicio?: string | null;
  fechaFin?: string | null;
  fechaFirma?: string | null;
};

export type RestContratoArriendo = RestOf<IContratoArriendo>;

export type NewRestContratoArriendo = RestOf<NewContratoArriendo>;

export type PartialUpdateRestContratoArriendo = RestOf<PartialUpdateContratoArriendo>;

@Injectable()
export class ContratoArriendosService {
  readonly contratoArriendosParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly contratoArriendosResource = httpResource<RestContratoArriendo[]>(() => {
    const params = this.contratoArriendosParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of contratoArriendo that have been fetched. It is updated when the contratoArriendosResource emits a new value.
   * In case of error while fetching the contratoArriendos, the signal is set to an empty array.
   */
  readonly contratoArriendos = computed(() =>
    (this.contratoArriendosResource.hasValue() ? this.contratoArriendosResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/contrato-arriendos');

  protected convertValueFromServer(restContratoArriendo: RestContratoArriendo): IContratoArriendo {
    return {
      ...restContratoArriendo,
      fechaInicio: restContratoArriendo.fechaInicio ? dayjs(restContratoArriendo.fechaInicio) : undefined,
      fechaFin: restContratoArriendo.fechaFin ? dayjs(restContratoArriendo.fechaFin) : undefined,
      fechaFirma: restContratoArriendo.fechaFirma ? dayjs(restContratoArriendo.fechaFirma) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class ContratoArriendoService extends ContratoArriendosService {
  protected readonly http = inject(HttpClient);

  create(contratoArriendo: NewContratoArriendo): Observable<IContratoArriendo> {
    const copy = this.convertValueFromClient(contratoArriendo);
    return this.http.post<RestContratoArriendo>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(contratoArriendo: IContratoArriendo): Observable<IContratoArriendo> {
    const copy = this.convertValueFromClient(contratoArriendo);
    return this.http
      .put<RestContratoArriendo>(`${this.resourceUrl}/${encodeURIComponent(this.getContratoArriendoIdentifier(contratoArriendo))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(contratoArriendo: PartialUpdateContratoArriendo): Observable<IContratoArriendo> {
    const copy = this.convertValueFromClient(contratoArriendo);
    return this.http
      .patch<RestContratoArriendo>(`${this.resourceUrl}/${encodeURIComponent(this.getContratoArriendoIdentifier(contratoArriendo))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: string): Observable<IContratoArriendo> {
    return this.http
      .get<RestContratoArriendo>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IContratoArriendo[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestContratoArriendo[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: string): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getContratoArriendoIdentifier(contratoArriendo: Pick<IContratoArriendo, 'id'>): string {
    return contratoArriendo.id;
  }

  compareContratoArriendo(o1: Pick<IContratoArriendo, 'id'> | null, o2: Pick<IContratoArriendo, 'id'> | null): boolean {
    return o1 && o2 ? this.getContratoArriendoIdentifier(o1) === this.getContratoArriendoIdentifier(o2) : o1 === o2;
  }

  addContratoArriendoToCollectionIfMissing<Type extends Pick<IContratoArriendo, 'id'>>(
    contratoArriendoCollection: Type[],
    ...contratoArriendosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const contratoArriendos: Type[] = contratoArriendosToCheck.filter(isPresent);
    if (contratoArriendos.length > 0) {
      const contratoArriendoCollectionIdentifiers = contratoArriendoCollection.map(contratoArriendoItem =>
        this.getContratoArriendoIdentifier(contratoArriendoItem),
      );
      const contratoArriendosToAdd = contratoArriendos.filter(contratoArriendoItem => {
        const contratoArriendoIdentifier = this.getContratoArriendoIdentifier(contratoArriendoItem);
        if (contratoArriendoCollectionIdentifiers.includes(contratoArriendoIdentifier)) {
          return false;
        }
        contratoArriendoCollectionIdentifiers.push(contratoArriendoIdentifier);
        return true;
      });
      return [...contratoArriendosToAdd, ...contratoArriendoCollection];
    }
    return contratoArriendoCollection;
  }

  protected convertValueFromClient<T extends IContratoArriendo | NewContratoArriendo | PartialUpdateContratoArriendo>(
    contratoArriendo: T,
  ): RestOf<T> {
    return {
      ...contratoArriendo,
      fechaInicio: contratoArriendo.fechaInicio?.format(DATE_FORMAT) ?? null,
      fechaFin: contratoArriendo.fechaFin?.format(DATE_FORMAT) ?? null,
      fechaFirma: contratoArriendo.fechaFirma?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestContratoArriendo): IContratoArriendo {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestContratoArriendo[]): IContratoArriendo[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
