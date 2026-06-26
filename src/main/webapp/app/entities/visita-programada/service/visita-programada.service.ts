import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IVisitaProgramada, NewVisitaProgramada } from '../visita-programada.model';

export type PartialUpdateVisitaProgramada = Partial<IVisitaProgramada> & Pick<IVisitaProgramada, 'id'>;

type RestOf<T extends IVisitaProgramada | NewVisitaProgramada> = Omit<T, 'fechaSolicitada' | 'fechaConfirmada'> & {
  fechaSolicitada?: string | null;
  fechaConfirmada?: string | null;
};

export type RestVisitaProgramada = RestOf<IVisitaProgramada>;

export type NewRestVisitaProgramada = RestOf<NewVisitaProgramada>;

export type PartialUpdateRestVisitaProgramada = RestOf<PartialUpdateVisitaProgramada>;

@Injectable()
export class VisitaProgramadasService {
  readonly visitaProgramadasParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly visitaProgramadasResource = httpResource<RestVisitaProgramada[]>(() => {
    const params = this.visitaProgramadasParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of visitaProgramada that have been fetched. It is updated when the visitaProgramadasResource emits a new value.
   * In case of error while fetching the visitaProgramadas, the signal is set to an empty array.
   */
  readonly visitaProgramadas = computed(() =>
    (this.visitaProgramadasResource.hasValue() ? this.visitaProgramadasResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/visita-programadas');

  protected convertValueFromServer(restVisitaProgramada: RestVisitaProgramada): IVisitaProgramada {
    return {
      ...restVisitaProgramada,
      fechaSolicitada: restVisitaProgramada.fechaSolicitada ? dayjs(restVisitaProgramada.fechaSolicitada) : undefined,
      fechaConfirmada: restVisitaProgramada.fechaConfirmada ? dayjs(restVisitaProgramada.fechaConfirmada) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class VisitaProgramadaService extends VisitaProgramadasService {
  protected readonly http = inject(HttpClient);

  create(visitaProgramada: NewVisitaProgramada): Observable<IVisitaProgramada> {
    const copy = this.convertValueFromClient(visitaProgramada);
    return this.http.post<RestVisitaProgramada>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(visitaProgramada: IVisitaProgramada): Observable<IVisitaProgramada> {
    const copy = this.convertValueFromClient(visitaProgramada);
    return this.http
      .put<RestVisitaProgramada>(`${this.resourceUrl}/${encodeURIComponent(this.getVisitaProgramadaIdentifier(visitaProgramada))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(visitaProgramada: PartialUpdateVisitaProgramada): Observable<IVisitaProgramada> {
    const copy = this.convertValueFromClient(visitaProgramada);
    return this.http
      .patch<RestVisitaProgramada>(`${this.resourceUrl}/${encodeURIComponent(this.getVisitaProgramadaIdentifier(visitaProgramada))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: string): Observable<IVisitaProgramada> {
    return this.http
      .get<RestVisitaProgramada>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IVisitaProgramada[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestVisitaProgramada[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: string): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getVisitaProgramadaIdentifier(visitaProgramada: Pick<IVisitaProgramada, 'id'>): string {
    return visitaProgramada.id;
  }

  compareVisitaProgramada(o1: Pick<IVisitaProgramada, 'id'> | null, o2: Pick<IVisitaProgramada, 'id'> | null): boolean {
    return o1 && o2 ? this.getVisitaProgramadaIdentifier(o1) === this.getVisitaProgramadaIdentifier(o2) : o1 === o2;
  }

  addVisitaProgramadaToCollectionIfMissing<Type extends Pick<IVisitaProgramada, 'id'>>(
    visitaProgramadaCollection: Type[],
    ...visitaProgramadasToCheck: (Type | null | undefined)[]
  ): Type[] {
    const visitaProgramadas: Type[] = visitaProgramadasToCheck.filter(isPresent);
    if (visitaProgramadas.length > 0) {
      const visitaProgramadaCollectionIdentifiers = visitaProgramadaCollection.map(visitaProgramadaItem =>
        this.getVisitaProgramadaIdentifier(visitaProgramadaItem),
      );
      const visitaProgramadasToAdd = visitaProgramadas.filter(visitaProgramadaItem => {
        const visitaProgramadaIdentifier = this.getVisitaProgramadaIdentifier(visitaProgramadaItem);
        if (visitaProgramadaCollectionIdentifiers.includes(visitaProgramadaIdentifier)) {
          return false;
        }
        visitaProgramadaCollectionIdentifiers.push(visitaProgramadaIdentifier);
        return true;
      });
      return [...visitaProgramadasToAdd, ...visitaProgramadaCollection];
    }
    return visitaProgramadaCollection;
  }

  protected convertValueFromClient<T extends IVisitaProgramada | NewVisitaProgramada | PartialUpdateVisitaProgramada>(
    visitaProgramada: T,
  ): RestOf<T> {
    return {
      ...visitaProgramada,
      fechaSolicitada: visitaProgramada.fechaSolicitada?.toJSON() ?? null,
      fechaConfirmada: visitaProgramada.fechaConfirmada?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestVisitaProgramada): IVisitaProgramada {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestVisitaProgramada[]): IVisitaProgramada[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
