import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { EstadoSolicitud } from 'app/entities/enumerations/estado-solicitud.model';
import { IPerfilUsuario } from 'app/entities/perfil-usuario/perfil-usuario.model';
import { PerfilUsuarioService } from 'app/entities/perfil-usuario/service/perfil-usuario.service';
import { IPublicacionRoomie } from 'app/entities/publicacion-roomie/publicacion-roomie.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { SolicitudRoomieService } from '../service/solicitud-roomie.service';
import { ISolicitudRoomie } from '../solicitud-roomie.model';

import { SolicitudRoomieFormGroup, SolicitudRoomieFormService } from './solicitud-roomie-form.service';
import { AlertErrorModel } from 'app/shared/alert/alert-error.model';
import { PublicacionRoomieService } from 'app/entities/publicacion-roomie/service/publicacion-roomie.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-solicitud-roomie-update',
  templateUrl: './solicitud-roomie-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class SolicitudRoomieUpdate implements OnInit {
  readonly isSaving = signal(false);
  solicitudRoomie: ISolicitudRoomie | null = null;
  estadoSolicitudValues = Object.keys(EstadoSolicitud);

  perfilUsuariosSharedCollection = signal<IPerfilUsuario[]>([]);
  publicacionRoomiesSharedCollection = signal<IPublicacionRoomie[]>([]);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected solicitudRoomieService = inject(SolicitudRoomieService);
  protected solicitudRoomieFormService = inject(SolicitudRoomieFormService);
  protected perfilUsuarioService = inject(PerfilUsuarioService);
  protected publicacionRoomieService = inject(PublicacionRoomieService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SolicitudRoomieFormGroup = this.solicitudRoomieFormService.createSolicitudRoomieFormGroup();

  comparePerfilUsuario = (o1: IPerfilUsuario | null, o2: IPerfilUsuario | null): boolean =>
    this.perfilUsuarioService.comparePerfilUsuario(o1, o2);

  comparePublicacionRoomie = (o1: IPublicacionRoomie | null, o2: IPublicacionRoomie | null): boolean =>
    this.publicacionRoomieService.comparePublicacionRoomie(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ solicitudRoomie }) => {
      this.solicitudRoomie = solicitudRoomie;
      if (solicitudRoomie) {
        this.updateForm(solicitudRoomie);
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
    const solicitudRoomie = this.solicitudRoomieFormService.getSolicitudRoomie(this.editForm);
    if (solicitudRoomie.id === null) {
      this.subscribeToSaveResponse(this.solicitudRoomieService.create(solicitudRoomie));
    } else {
      this.subscribeToSaveResponse(this.solicitudRoomieService.update(solicitudRoomie));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ISolicitudRoomie | null>): void {
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

  protected updateForm(solicitudRoomie: ISolicitudRoomie): void {
    this.solicitudRoomie = solicitudRoomie;
    this.solicitudRoomieFormService.resetForm(this.editForm, solicitudRoomie);

    this.perfilUsuariosSharedCollection.update(perfilUsuarios =>
      this.perfilUsuarioService.addPerfilUsuarioToCollectionIfMissing<IPerfilUsuario>(perfilUsuarios, solicitudRoomie.postulante),
    );
    this.publicacionRoomiesSharedCollection.update(publicacionRoomies =>
      this.publicacionRoomieService.addPublicacionRoomieToCollectionIfMissing<IPublicacionRoomie>(
        publicacionRoomies,
        solicitudRoomie.publicacionRoomie,
      ),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.perfilUsuarioService
      .query()
      .pipe(map((res: HttpResponse<IPerfilUsuario[]>) => res.body ?? []))
      .pipe(
        map((perfilUsuarios: IPerfilUsuario[]) =>
          this.perfilUsuarioService.addPerfilUsuarioToCollectionIfMissing<IPerfilUsuario>(perfilUsuarios, this.solicitudRoomie?.postulante),
        ),
      )
      .subscribe((perfilUsuarios: IPerfilUsuario[]) => this.perfilUsuariosSharedCollection.set(perfilUsuarios));

    this.publicacionRoomieService
      .query()
      .pipe(map((res: HttpResponse<IPublicacionRoomie[]>) => res.body ?? []))
      .pipe(
        map((publicacionRoomies: IPublicacionRoomie[]) =>
          this.publicacionRoomieService.addPublicacionRoomieToCollectionIfMissing<IPublicacionRoomie>(
            publicacionRoomies,
            this.solicitudRoomie?.publicacionRoomie,
          ),
        ),
      )
      .subscribe((publicacionRoomies: IPublicacionRoomie[]) => this.publicacionRoomiesSharedCollection.set(publicacionRoomies));
  }
}
