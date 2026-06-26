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
import { IPublicacionInmueble } from 'app/entities/publicacion-inmueble/publicacion-inmueble.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { SolicitudArriendoService } from '../service/solicitud-arriendo.service';
import { ISolicitudArriendo } from '../solicitud-arriendo.model';

import { SolicitudArriendoFormGroup, SolicitudArriendoFormService } from './solicitud-arriendo-form.service';
import { AlertErrorModel } from 'app/shared/alert/alert-error.model';
import { PublicacionInmuebleService } from 'app/entities/publicacion-inmueble/service/publicacion-inmueble.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-solicitud-arriendo-update',
  templateUrl: './solicitud-arriendo-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class SolicitudArriendoUpdate implements OnInit {
  readonly isSaving = signal(false);
  solicitudArriendo: ISolicitudArriendo | null = null;
  estadoSolicitudValues = Object.keys(EstadoSolicitud);

  perfilUsuariosSharedCollection = signal<IPerfilUsuario[]>([]);
  publicacionInmueblesSharedCollection = signal<IPublicacionInmueble[]>([]);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected solicitudArriendoService = inject(SolicitudArriendoService);
  protected solicitudArriendoFormService = inject(SolicitudArriendoFormService);
  protected perfilUsuarioService = inject(PerfilUsuarioService);
  protected publicacionInmuebleService = inject(PublicacionInmuebleService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SolicitudArriendoFormGroup = this.solicitudArriendoFormService.createSolicitudArriendoFormGroup();

  comparePerfilUsuario = (o1: IPerfilUsuario | null, o2: IPerfilUsuario | null): boolean =>
    this.perfilUsuarioService.comparePerfilUsuario(o1, o2);

  comparePublicacionInmueble = (o1: IPublicacionInmueble | null, o2: IPublicacionInmueble | null): boolean =>
    this.publicacionInmuebleService.comparePublicacionInmueble(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ solicitudArriendo }) => {
      this.solicitudArriendo = solicitudArriendo;
      if (solicitudArriendo) {
        this.updateForm(solicitudArriendo);
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
    const solicitudArriendo = this.solicitudArriendoFormService.getSolicitudArriendo(this.editForm);
    if (solicitudArriendo.id === null) {
      this.subscribeToSaveResponse(this.solicitudArriendoService.create(solicitudArriendo));
    } else {
      this.subscribeToSaveResponse(this.solicitudArriendoService.update(solicitudArriendo));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ISolicitudArriendo | null>): void {
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

  protected updateForm(solicitudArriendo: ISolicitudArriendo): void {
    this.solicitudArriendo = solicitudArriendo;
    this.solicitudArriendoFormService.resetForm(this.editForm, solicitudArriendo);

    this.perfilUsuariosSharedCollection.update(perfilUsuarios =>
      this.perfilUsuarioService.addPerfilUsuarioToCollectionIfMissing<IPerfilUsuario>(perfilUsuarios, solicitudArriendo.arrendatario),
    );
    this.publicacionInmueblesSharedCollection.update(publicacionInmuebles =>
      this.publicacionInmuebleService.addPublicacionInmuebleToCollectionIfMissing<IPublicacionInmueble>(
        publicacionInmuebles,
        solicitudArriendo.publicacion,
      ),
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
            this.solicitudArriendo?.arrendatario,
          ),
        ),
      )
      .subscribe((perfilUsuarios: IPerfilUsuario[]) => this.perfilUsuariosSharedCollection.set(perfilUsuarios));

    this.publicacionInmuebleService
      .query()
      .pipe(map((res: HttpResponse<IPublicacionInmueble[]>) => res.body ?? []))
      .pipe(
        map((publicacionInmuebles: IPublicacionInmueble[]) =>
          this.publicacionInmuebleService.addPublicacionInmuebleToCollectionIfMissing<IPublicacionInmueble>(
            publicacionInmuebles,
            this.solicitudArriendo?.publicacion,
          ),
        ),
      )
      .subscribe((publicacionInmuebles: IPublicacionInmueble[]) => this.publicacionInmueblesSharedCollection.set(publicacionInmuebles));
  }
}
