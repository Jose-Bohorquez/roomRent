import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { EstadoVisita } from 'app/entities/enumerations/estado-visita.model';
import { IPerfilUsuario } from 'app/entities/perfil-usuario/perfil-usuario.model';
import { PerfilUsuarioService } from 'app/entities/perfil-usuario/service/perfil-usuario.service';
import { ISolicitudArriendo } from 'app/entities/solicitud-arriendo/solicitud-arriendo.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { VisitaProgramadaService } from '../service/visita-programada.service';
import { IVisitaProgramada } from '../visita-programada.model';

import { VisitaProgramadaFormGroup, VisitaProgramadaFormService } from './visita-programada-form.service';
import { AlertErrorModel } from 'app/shared/alert/alert-error.model';
import { SolicitudArriendoService } from 'app/entities/solicitud-arriendo/service/solicitud-arriendo.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-visita-programada-update',
  templateUrl: './visita-programada-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class VisitaProgramadaUpdate implements OnInit {
  readonly isSaving = signal(false);
  visitaProgramada: IVisitaProgramada | null = null;
  estadoVisitaValues = Object.keys(EstadoVisita);

  perfilUsuariosSharedCollection = signal<IPerfilUsuario[]>([]);
  solicitudArriendosSharedCollection = signal<ISolicitudArriendo[]>([]);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected visitaProgramadaService = inject(VisitaProgramadaService);
  protected visitaProgramadaFormService = inject(VisitaProgramadaFormService);
  protected perfilUsuarioService = inject(PerfilUsuarioService);
  protected solicitudArriendoService = inject(SolicitudArriendoService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: VisitaProgramadaFormGroup = this.visitaProgramadaFormService.createVisitaProgramadaFormGroup();

  comparePerfilUsuario = (o1: IPerfilUsuario | null, o2: IPerfilUsuario | null): boolean =>
    this.perfilUsuarioService.comparePerfilUsuario(o1, o2);

  compareSolicitudArriendo = (o1: ISolicitudArriendo | null, o2: ISolicitudArriendo | null): boolean =>
    this.solicitudArriendoService.compareSolicitudArriendo(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ visitaProgramada }) => {
      this.visitaProgramada = visitaProgramada;
      if (visitaProgramada) {
        this.updateForm(visitaProgramada);
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
    const visitaProgramada = this.visitaProgramadaFormService.getVisitaProgramada(this.editForm);
    if (visitaProgramada.id === null) {
      this.subscribeToSaveResponse(this.visitaProgramadaService.create(visitaProgramada));
    } else {
      this.subscribeToSaveResponse(this.visitaProgramadaService.update(visitaProgramada));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IVisitaProgramada | null>): void {
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

  protected updateForm(visitaProgramada: IVisitaProgramada): void {
    this.visitaProgramada = visitaProgramada;
    this.visitaProgramadaFormService.resetForm(this.editForm, visitaProgramada);

    this.perfilUsuariosSharedCollection.update(perfilUsuarios =>
      this.perfilUsuarioService.addPerfilUsuarioToCollectionIfMissing<IPerfilUsuario>(perfilUsuarios, visitaProgramada.visitante),
    );
    this.solicitudArriendosSharedCollection.update(solicitudArriendos =>
      this.solicitudArriendoService.addSolicitudArriendoToCollectionIfMissing<ISolicitudArriendo>(
        solicitudArriendos,
        visitaProgramada.solicitud,
      ),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.perfilUsuarioService
      .query()
      .pipe(map((res: HttpResponse<IPerfilUsuario[]>) => res.body ?? []))
      .pipe(
        map((perfilUsuarios: IPerfilUsuario[]) =>
          this.perfilUsuarioService.addPerfilUsuarioToCollectionIfMissing<IPerfilUsuario>(perfilUsuarios, this.visitaProgramada?.visitante),
        ),
      )
      .subscribe((perfilUsuarios: IPerfilUsuario[]) => this.perfilUsuariosSharedCollection.set(perfilUsuarios));

    this.solicitudArriendoService
      .query()
      .pipe(map((res: HttpResponse<ISolicitudArriendo[]>) => res.body ?? []))
      .pipe(
        map((solicitudArriendos: ISolicitudArriendo[]) =>
          this.solicitudArriendoService.addSolicitudArriendoToCollectionIfMissing<ISolicitudArriendo>(
            solicitudArriendos,
            this.visitaProgramada?.solicitud,
          ),
        ),
      )
      .subscribe((solicitudArriendos: ISolicitudArriendo[]) => this.solicitudArriendosSharedCollection.set(solicitudArriendos));
  }
}
