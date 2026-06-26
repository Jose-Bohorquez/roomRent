import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { TipoDocumento } from 'app/entities/enumerations/tipo-documento.model';
import { IPerfilUsuario } from 'app/entities/perfil-usuario/perfil-usuario.model';
import { PerfilUsuarioService } from 'app/entities/perfil-usuario/service/perfil-usuario.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { AlertErrorModel } from 'app/shared/alert/alert-error.model';
import { TranslateDirective } from 'app/shared/language';

import { IDocumentoUsuario } from '../documento-usuario.model';
import { DocumentoUsuarioService } from '../service/documento-usuario.service';

import { DocumentoUsuarioFormGroup, DocumentoUsuarioFormService } from './documento-usuario-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-documento-usuario-update',
  templateUrl: './documento-usuario-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class DocumentoUsuarioUpdate implements OnInit {
  readonly isSaving = signal(false);
  documentoUsuario: IDocumentoUsuario | null = null;
  tipoDocumentoValues = Object.keys(TipoDocumento);

  perfilUsuariosSharedCollection = signal<IPerfilUsuario[]>([]);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected documentoUsuarioService = inject(DocumentoUsuarioService);
  protected documentoUsuarioFormService = inject(DocumentoUsuarioFormService);
  protected perfilUsuarioService = inject(PerfilUsuarioService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DocumentoUsuarioFormGroup = this.documentoUsuarioFormService.createDocumentoUsuarioFormGroup();

  comparePerfilUsuario = (o1: IPerfilUsuario | null, o2: IPerfilUsuario | null): boolean =>
    this.perfilUsuarioService.comparePerfilUsuario(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ documentoUsuario }) => {
      this.documentoUsuario = documentoUsuario;
      if (documentoUsuario) {
        this.updateForm(documentoUsuario);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertErrorModel>('roomApp.error', { ...err, key: `error.file.${err.key}` })),
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const documentoUsuario = this.documentoUsuarioFormService.getDocumentoUsuario(this.editForm);
    if (documentoUsuario.id === null) {
      this.subscribeToSaveResponse(this.documentoUsuarioService.create(documentoUsuario));
    } else {
      this.subscribeToSaveResponse(this.documentoUsuarioService.update(documentoUsuario));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IDocumentoUsuario | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(documentoUsuario: IDocumentoUsuario): void {
    this.documentoUsuario = documentoUsuario;
    this.documentoUsuarioFormService.resetForm(this.editForm, documentoUsuario);

    this.perfilUsuariosSharedCollection.update(perfilUsuarios =>
      this.perfilUsuarioService.addPerfilUsuarioToCollectionIfMissing<IPerfilUsuario>(perfilUsuarios, documentoUsuario.perfilUsuario),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.perfilUsuarioService
      .query()
      .pipe(map((res: HttpResponse<IPerfilUsuario[]>) => res.body ?? []))
      .pipe(
        map((perfilUsuarios: IPerfilUsuario[]) =>
          this.perfilUsuarioService.addPerfilUsuarioToCollectionIfMissing<IPerfilUsuario>(
            perfilUsuarios,
            this.documentoUsuario?.perfilUsuario,
          ),
        ),
      )
      .subscribe((perfilUsuarios: IPerfilUsuario[]) => this.perfilUsuariosSharedCollection.set(perfilUsuarios));
  }
}
