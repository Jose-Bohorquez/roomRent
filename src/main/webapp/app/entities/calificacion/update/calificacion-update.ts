import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { IContratoArriendo } from 'app/entities/contrato-arriendo/contrato-arriendo.model';
import { ContratoArriendoService } from 'app/entities/contrato-arriendo/service/contrato-arriendo.service';
import { TipoCalificacion } from 'app/entities/enumerations/tipo-calificacion.model';
import { IPerfilUsuario } from 'app/entities/perfil-usuario/perfil-usuario.model';
import { PerfilUsuarioService } from 'app/entities/perfil-usuario/service/perfil-usuario.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { AlertErrorModel } from 'app/shared/alert/alert-error.model';
import { TranslateDirective } from 'app/shared/language';
import { ICalificacion } from '../calificacion.model';
import { CalificacionService } from '../service/calificacion.service';

import { CalificacionFormGroup, CalificacionFormService } from './calificacion-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-calificacion-update',
  templateUrl: './calificacion-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CalificacionUpdate implements OnInit {
  readonly isSaving = signal(false);
  calificacion: ICalificacion | null = null;
  tipoCalificacionValues = Object.keys(TipoCalificacion);

  perfilUsuariosSharedCollection = signal<IPerfilUsuario[]>([]);
  contratoArriendosSharedCollection = signal<IContratoArriendo[]>([]);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected calificacionService = inject(CalificacionService);
  protected calificacionFormService = inject(CalificacionFormService);
  protected perfilUsuarioService = inject(PerfilUsuarioService);
  protected contratoArriendoService = inject(ContratoArriendoService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CalificacionFormGroup = this.calificacionFormService.createCalificacionFormGroup();

  comparePerfilUsuario = (o1: IPerfilUsuario | null, o2: IPerfilUsuario | null): boolean =>
    this.perfilUsuarioService.comparePerfilUsuario(o1, o2);

  compareContratoArriendo = (o1: IContratoArriendo | null, o2: IContratoArriendo | null): boolean =>
    this.contratoArriendoService.compareContratoArriendo(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ calificacion }) => {
      this.calificacion = calificacion;
      if (calificacion) {
        this.updateForm(calificacion);
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
    const calificacion = this.calificacionFormService.getCalificacion(this.editForm);
    if (calificacion.id === null) {
      this.subscribeToSaveResponse(this.calificacionService.create(calificacion));
    } else {
      this.subscribeToSaveResponse(this.calificacionService.update(calificacion));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICalificacion | null>): void {
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

  protected updateForm(calificacion: ICalificacion): void {
    this.calificacion = calificacion;
    this.calificacionFormService.resetForm(this.editForm, calificacion);

    this.perfilUsuariosSharedCollection.update(perfilUsuarios =>
      this.perfilUsuarioService.addPerfilUsuarioToCollectionIfMissing<IPerfilUsuario>(
        perfilUsuarios,
        calificacion.autor,
        calificacion.calificado,
      ),
    );
    this.contratoArriendosSharedCollection.update(contratoArriendos =>
      this.contratoArriendoService.addContratoArriendoToCollectionIfMissing<IContratoArriendo>(contratoArriendos, calificacion.contrato),
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
            this.calificacion?.autor,
            this.calificacion?.calificado,
          ),
        ),
      )
      .subscribe((perfilUsuarios: IPerfilUsuario[]) => this.perfilUsuariosSharedCollection.set(perfilUsuarios));

    this.contratoArriendoService
      .query()
      .pipe(map((res: HttpResponse<IContratoArriendo[]>) => res.body ?? []))
      .pipe(
        map((contratoArriendos: IContratoArriendo[]) =>
          this.contratoArriendoService.addContratoArriendoToCollectionIfMissing<IContratoArriendo>(
            contratoArriendos,
            this.calificacion?.contrato,
          ),
        ),
      )
      .subscribe((contratoArriendos: IContratoArriendo[]) => this.contratoArriendosSharedCollection.set(contratoArriendos));
  }
}
