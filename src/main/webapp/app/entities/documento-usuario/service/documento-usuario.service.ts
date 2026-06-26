import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IDocumentoUsuario, NewDocumentoUsuario } from '../documento-usuario.model';

export type PartialUpdateDocumentoUsuario = Partial<IDocumentoUsuario> & Pick<IDocumentoUsuario, 'id'>;

type RestOf<T extends IDocumentoUsuario | NewDocumentoUsuario> = Omit<T, 'fechaCarga'> & {
  fechaCarga?: string | null;
};

export type RestDocumentoUsuario = RestOf<IDocumentoUsuario>;

export type NewRestDocumentoUsuario = RestOf<NewDocumentoUsuario>;

export type PartialUpdateRestDocumentoUsuario = RestOf<PartialUpdateDocumentoUsuario>;

@Injectable()
export class DocumentoUsuariosService {
  readonly documentoUsuariosParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly documentoUsuariosResource = httpResource<RestDocumentoUsuario[]>(() => {
    const params = this.documentoUsuariosParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of documentoUsuario that have been fetched. It is updated when the documentoUsuariosResource emits a new value.
   * In case of error while fetching the documentoUsuarios, the signal is set to an empty array.
   */
  readonly documentoUsuarios = computed(() =>
    (this.documentoUsuariosResource.hasValue() ? this.documentoUsuariosResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/documento-usuarios');

  protected convertValueFromServer(restDocumentoUsuario: RestDocumentoUsuario): IDocumentoUsuario {
    return {
      ...restDocumentoUsuario,
      fechaCarga: restDocumentoUsuario.fechaCarga ? dayjs(restDocumentoUsuario.fechaCarga) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class DocumentoUsuarioService extends DocumentoUsuariosService {
  protected readonly http = inject(HttpClient);

  create(documentoUsuario: NewDocumentoUsuario): Observable<IDocumentoUsuario> {
    const copy = this.convertValueFromClient(documentoUsuario);
    return this.http.post<RestDocumentoUsuario>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(documentoUsuario: IDocumentoUsuario): Observable<IDocumentoUsuario> {
    const copy = this.convertValueFromClient(documentoUsuario);
    return this.http
      .put<RestDocumentoUsuario>(`${this.resourceUrl}/${encodeURIComponent(this.getDocumentoUsuarioIdentifier(documentoUsuario))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(documentoUsuario: PartialUpdateDocumentoUsuario): Observable<IDocumentoUsuario> {
    const copy = this.convertValueFromClient(documentoUsuario);
    return this.http
      .patch<RestDocumentoUsuario>(`${this.resourceUrl}/${encodeURIComponent(this.getDocumentoUsuarioIdentifier(documentoUsuario))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: string): Observable<IDocumentoUsuario> {
    return this.http
      .get<RestDocumentoUsuario>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IDocumentoUsuario[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDocumentoUsuario[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: string): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getDocumentoUsuarioIdentifier(documentoUsuario: Pick<IDocumentoUsuario, 'id'>): string {
    return documentoUsuario.id;
  }

  compareDocumentoUsuario(o1: Pick<IDocumentoUsuario, 'id'> | null, o2: Pick<IDocumentoUsuario, 'id'> | null): boolean {
    return o1 && o2 ? this.getDocumentoUsuarioIdentifier(o1) === this.getDocumentoUsuarioIdentifier(o2) : o1 === o2;
  }

  addDocumentoUsuarioToCollectionIfMissing<Type extends Pick<IDocumentoUsuario, 'id'>>(
    documentoUsuarioCollection: Type[],
    ...documentoUsuariosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const documentoUsuarios: Type[] = documentoUsuariosToCheck.filter(isPresent);
    if (documentoUsuarios.length > 0) {
      const documentoUsuarioCollectionIdentifiers = documentoUsuarioCollection.map(documentoUsuarioItem =>
        this.getDocumentoUsuarioIdentifier(documentoUsuarioItem),
      );
      const documentoUsuariosToAdd = documentoUsuarios.filter(documentoUsuarioItem => {
        const documentoUsuarioIdentifier = this.getDocumentoUsuarioIdentifier(documentoUsuarioItem);
        if (documentoUsuarioCollectionIdentifiers.includes(documentoUsuarioIdentifier)) {
          return false;
        }
        documentoUsuarioCollectionIdentifiers.push(documentoUsuarioIdentifier);
        return true;
      });
      return [...documentoUsuariosToAdd, ...documentoUsuarioCollection];
    }
    return documentoUsuarioCollection;
  }

  protected convertValueFromClient<T extends IDocumentoUsuario | NewDocumentoUsuario | PartialUpdateDocumentoUsuario>(
    documentoUsuario: T,
  ): RestOf<T> {
    return {
      ...documentoUsuario,
      fechaCarga: documentoUsuario.fechaCarga?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestDocumentoUsuario): IDocumentoUsuario {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestDocumentoUsuario[]): IDocumentoUsuario[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
