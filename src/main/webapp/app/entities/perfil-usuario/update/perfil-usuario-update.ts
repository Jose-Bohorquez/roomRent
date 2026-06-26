import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { TipoDocumento } from 'app/entities/enumerations/tipo-documento.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { IPerfilUsuario } from '../perfil-usuario.model';
import { PerfilUsuarioService } from '../service/perfil-usuario.service';

import { PerfilUsuarioFormGroup, PerfilUsuarioFormService } from './perfil-usuario-form.service';
import { AlertErrorModel } from 'app/shared/alert/alert-error.model';
import { Genero } from 'app/entities/enumerations/genero.model';
import { EstadoUsuario } from 'app/entities/enumerations/estado-usuario.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-perfil-usuario-update',
  templateUrl: './perfil-usuario-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class PerfilUsuarioUpdate implements OnInit {
  readonly isSaving = signal(false);
  perfilUsuario: IPerfilUsuario | null = null;
  tipoDocumentoValues = Object.keys(TipoDocumento);
  generoValues = Object.keys(Genero);
  estadoUsuarioValues = Object.keys(EstadoUsuario);

  usersSharedCollection = signal<IUser[]>([]);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected perfilUsuarioService = inject(PerfilUsuarioService);
  protected perfilUsuarioFormService = inject(PerfilUsuarioFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PerfilUsuarioFormGroup = this.perfilUsuarioFormService.createPerfilUsuarioFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ perfilUsuario }) => {
      this.perfilUsuario = perfilUsuario;
      if (perfilUsuario) {
        this.updateForm(perfilUsuario);
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
    const perfilUsuario = this.perfilUsuarioFormService.getPerfilUsuario(this.editForm);
    if (perfilUsuario.id === null) {
      this.subscribeToSaveResponse(this.perfilUsuarioService.create(perfilUsuario));
    } else {
      this.subscribeToSaveResponse(this.perfilUsuarioService.update(perfilUsuario));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IPerfilUsuario | null>): void {
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

  protected updateForm(perfilUsuario: IPerfilUsuario): void {
    this.perfilUsuario = perfilUsuario;
    this.perfilUsuarioFormService.resetForm(this.editForm, perfilUsuario);

    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, perfilUsuario.usuario));
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.perfilUsuario?.usuario)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));
  }
}
