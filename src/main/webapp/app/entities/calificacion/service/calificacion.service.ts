import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ICalificacion, NewCalificacion } from '../calificacion.model';

export type PartialUpdateCalificacion = Partial<ICalificacion> & Pick<ICalificacion, 'id'>;

type RestOf<T extends ICalificacion | NewCalificacion> = Omit<T, 'fechaCreacion'> & {
  fechaCreacion?: string | null;
};

export type RestCalificacion = RestOf<ICalificacion>;

export type NewRestCalificacion = RestOf<NewCalificacion>;

export type PartialUpdateRestCalificacion = RestOf<PartialUpdateCalificacion>;

@Injectable()
export class CalificacionsService {
  readonly calificacionsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly calificacionsResource = httpResource<RestCalificacion[]>(() => {
    const params = this.calificacionsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of calificacion that have been fetched. It is updated when the calificacionsResource emits a new value.
   * In case of error while fetching the calificacions, the signal is set to an empty array.
   */
  readonly calificacions = computed(() =>
    (this.calificacionsResource.hasValue() ? this.calificacionsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/calificacions');

  protected convertValueFromServer(restCalificacion: RestCalificacion): ICalificacion {
    return {
      ...restCalificacion,
      fechaCreacion: restCalificacion.fechaCreacion ? dayjs(restCalificacion.fechaCreacion) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class CalificacionService extends CalificacionsService {
  protected readonly http = inject(HttpClient);

  create(calificacion: NewCalificacion): Observable<ICalificacion> {
    const copy = this.convertValueFromClient(calificacion);
    return this.http.post<RestCalificacion>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(calificacion: ICalificacion): Observable<ICalificacion> {
    const copy = this.convertValueFromClient(calificacion);
    return this.http
      .put<RestCalificacion>(`${this.resourceUrl}/${encodeURIComponent(this.getCalificacionIdentifier(calificacion))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(calificacion: PartialUpdateCalificacion): Observable<ICalificacion> {
    const copy = this.convertValueFromClient(calificacion);
    return this.http
      .patch<RestCalificacion>(`${this.resourceUrl}/${encodeURIComponent(this.getCalificacionIdentifier(calificacion))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: string): Observable<ICalificacion> {
    return this.http
      .get<RestCalificacion>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ICalificacion[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCalificacion[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: string): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getCalificacionIdentifier(calificacion: Pick<ICalificacion, 'id'>): string {
    return calificacion.id;
  }

  compareCalificacion(o1: Pick<ICalificacion, 'id'> | null, o2: Pick<ICalificacion, 'id'> | null): boolean {
    return o1 && o2 ? this.getCalificacionIdentifier(o1) === this.getCalificacionIdentifier(o2) : o1 === o2;
  }

  addCalificacionToCollectionIfMissing<Type extends Pick<ICalificacion, 'id'>>(
    calificacionCollection: Type[],
    ...calificacionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const calificacions: Type[] = calificacionsToCheck.filter(isPresent);
    if (calificacions.length > 0) {
      const calificacionCollectionIdentifiers = calificacionCollection.map(calificacionItem =>
        this.getCalificacionIdentifier(calificacionItem),
      );
      const calificacionsToAdd = calificacions.filter(calificacionItem => {
        const calificacionIdentifier = this.getCalificacionIdentifier(calificacionItem);
        if (calificacionCollectionIdentifiers.includes(calificacionIdentifier)) {
          return false;
        }
        calificacionCollectionIdentifiers.push(calificacionIdentifier);
        return true;
      });
      return [...calificacionsToAdd, ...calificacionCollection];
    }
    return calificacionCollection;
  }

  protected convertValueFromClient<T extends ICalificacion | NewCalificacion | PartialUpdateCalificacion>(calificacion: T): RestOf<T> {
    return {
      ...calificacion,
      fechaCreacion: calificacion.fechaCreacion?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestCalificacion): ICalificacion {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestCalificacion[]): ICalificacion[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
