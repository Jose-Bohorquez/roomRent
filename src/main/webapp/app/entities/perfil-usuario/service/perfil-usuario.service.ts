import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IPerfilUsuario, NewPerfilUsuario } from '../perfil-usuario.model';

export type PartialUpdatePerfilUsuario = Partial<IPerfilUsuario> & Pick<IPerfilUsuario, 'id'>;

type RestOf<T extends IPerfilUsuario | NewPerfilUsuario> = Omit<T, 'fechaNacimiento' | 'fechaCreacion'> & {
  fechaNacimiento?: string | null;
  fechaCreacion?: string | null;
};

export type RestPerfilUsuario = RestOf<IPerfilUsuario>;

export type NewRestPerfilUsuario = RestOf<NewPerfilUsuario>;

export type PartialUpdateRestPerfilUsuario = RestOf<PartialUpdatePerfilUsuario>;

@Injectable()
export class PerfilUsuariosService {
  readonly perfilUsuariosParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly perfilUsuariosResource = httpResource<RestPerfilUsuario[]>(() => {
    const params = this.perfilUsuariosParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of perfilUsuario that have been fetched. It is updated when the perfilUsuariosResource emits a new value.
   * In case of error while fetching the perfilUsuarios, the signal is set to an empty array.
   */
  readonly perfilUsuarios = computed(() =>
    (this.perfilUsuariosResource.hasValue() ? this.perfilUsuariosResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/perfil-usuarios');

  protected convertValueFromServer(restPerfilUsuario: RestPerfilUsuario): IPerfilUsuario {
    return {
      ...restPerfilUsuario,
      fechaNacimiento: restPerfilUsuario.fechaNacimiento ? dayjs(restPerfilUsuario.fechaNacimiento) : undefined,
      fechaCreacion: restPerfilUsuario.fechaCreacion ? dayjs(restPerfilUsuario.fechaCreacion) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class PerfilUsuarioService extends PerfilUsuariosService {
  protected readonly http = inject(HttpClient);

  create(perfilUsuario: NewPerfilUsuario): Observable<IPerfilUsuario> {
    const copy = this.convertValueFromClient(perfilUsuario);
    return this.http.post<RestPerfilUsuario>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(perfilUsuario: IPerfilUsuario): Observable<IPerfilUsuario> {
    const copy = this.convertValueFromClient(perfilUsuario);
    return this.http
      .put<RestPerfilUsuario>(`${this.resourceUrl}/${encodeURIComponent(this.getPerfilUsuarioIdentifier(perfilUsuario))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(perfilUsuario: PartialUpdatePerfilUsuario): Observable<IPerfilUsuario> {
    const copy = this.convertValueFromClient(perfilUsuario);
    return this.http
      .patch<RestPerfilUsuario>(`${this.resourceUrl}/${encodeURIComponent(this.getPerfilUsuarioIdentifier(perfilUsuario))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: string): Observable<IPerfilUsuario> {
    return this.http
      .get<RestPerfilUsuario>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IPerfilUsuario[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPerfilUsuario[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: string): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getPerfilUsuarioIdentifier(perfilUsuario: Pick<IPerfilUsuario, 'id'>): string {
    return perfilUsuario.id;
  }

  comparePerfilUsuario(o1: Pick<IPerfilUsuario, 'id'> | null, o2: Pick<IPerfilUsuario, 'id'> | null): boolean {
    return o1 && o2 ? this.getPerfilUsuarioIdentifier(o1) === this.getPerfilUsuarioIdentifier(o2) : o1 === o2;
  }

  addPerfilUsuarioToCollectionIfMissing<Type extends Pick<IPerfilUsuario, 'id'>>(
    perfilUsuarioCollection: Type[],
    ...perfilUsuariosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const perfilUsuarios: Type[] = perfilUsuariosToCheck.filter(isPresent);
    if (perfilUsuarios.length > 0) {
      const perfilUsuarioCollectionIdentifiers = perfilUsuarioCollection.map(perfilUsuarioItem =>
        this.getPerfilUsuarioIdentifier(perfilUsuarioItem),
      );
      const perfilUsuariosToAdd = perfilUsuarios.filter(perfilUsuarioItem => {
        const perfilUsuarioIdentifier = this.getPerfilUsuarioIdentifier(perfilUsuarioItem);
        if (perfilUsuarioCollectionIdentifiers.includes(perfilUsuarioIdentifier)) {
          return false;
        }
        perfilUsuarioCollectionIdentifiers.push(perfilUsuarioIdentifier);
        return true;
      });
      return [...perfilUsuariosToAdd, ...perfilUsuarioCollection];
    }
    return perfilUsuarioCollection;
  }

  protected convertValueFromClient<T extends IPerfilUsuario | NewPerfilUsuario | PartialUpdatePerfilUsuario>(perfilUsuario: T): RestOf<T> {
    return {
      ...perfilUsuario,
      fechaNacimiento: perfilUsuario.fechaNacimiento?.format(DATE_FORMAT) ?? null,
      fechaCreacion: perfilUsuario.fechaCreacion?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestPerfilUsuario): IPerfilUsuario {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestPerfilUsuario[]): IPerfilUsuario[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
