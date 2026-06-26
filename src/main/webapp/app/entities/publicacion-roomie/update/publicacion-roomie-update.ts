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
import { Genero } from 'app/entities/enumerations/genero.model';
import { IInmueble } from 'app/entities/inmueble/inmueble.model';
import { IPerfilUsuario } from 'app/entities/perfil-usuario/perfil-usuario.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { IPublicacionRoomie } from '../publicacion-roomie.model';
import { PublicacionRoomieService } from '../service/publicacion-roomie.service';

import { PublicacionRoomieFormGroup, PublicacionRoomieFormService } from './publicacion-roomie-form.service';
import { AlertErrorModel } from 'app/shared/alert/alert-error.model';
import { PerfilUsuarioService } from 'app/entities/perfil-usuario/service/perfil-usuario.service';
import { InmuebleService } from 'app/entities/inmueble/service/inmueble.service';
import { EstadoPublicacion } from 'app/entities/enumerations/estado-publicacion.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-publicacion-roomie-update',
  templateUrl: './publicacion-roomie-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class PublicacionRoomieUpdate implements OnInit {
  readonly isSaving = signal(false);
  publicacionRoomie: IPublicacionRoomie | null = null;
  generoValues = Object.keys(Genero);
  estadoPublicacionValues = Object.keys(EstadoPublicacion);

  perfilUsuariosSharedCollection = signal<IPerfilUsuario[]>([]);
  inmueblesSharedCollection = signal<IInmueble[]>([]);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected publicacionRoomieService = inject(PublicacionRoomieService);
  protected publicacionRoomieFormService = inject(PublicacionRoomieFormService);
  protected perfilUsuarioService = inject(PerfilUsuarioService);
  protected inmuebleService = inject(InmuebleService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PublicacionRoomieFormGroup = this.publicacionRoomieFormService.createPublicacionRoomieFormGroup();

  comparePerfilUsuario = (o1: IPerfilUsuario | null, o2: IPerfilUsuario | null): boolean =>
    this.perfilUsuarioService.comparePerfilUsuario(o1, o2);

  compareInmueble = (o1: IInmueble | null, o2: IInmueble | null): boolean => this.inmuebleService.compareInmueble(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ publicacionRoomie }) => {
      this.publicacionRoomie = publicacionRoomie;
      if (publicacionRoomie) {
        this.updateForm(publicacionRoomie);
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
    const publicacionRoomie = this.publicacionRoomieFormService.getPublicacionRoomie(this.editForm);
    if (publicacionRoomie.id === null) {
      this.subscribeToSaveResponse(this.publicacionRoomieService.create(publicacionRoomie));
    } else {
      this.subscribeToSaveResponse(this.publicacionRoomieService.update(publicacionRoomie));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IPublicacionRoomie | null>): void {
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

  protected updateForm(publicacionRoomie: IPublicacionRoomie): void {
    this.publicacionRoomie = publicacionRoomie;
    this.publicacionRoomieFormService.resetForm(this.editForm, publicacionRoomie);

    this.perfilUsuariosSharedCollection.update(perfilUsuarios =>
      this.perfilUsuarioService.addPerfilUsuarioToCollectionIfMissing<IPerfilUsuario>(perfilUsuarios, publicacionRoomie.arrendatario),
    );
    this.inmueblesSharedCollection.update(inmuebles =>
      this.inmuebleService.addInmuebleToCollectionIfMissing<IInmueble>(inmuebles, publicacionRoomie.inmueble),
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
            this.publicacionRoomie?.arrendatario,
          ),
        ),
      )
      .subscribe((perfilUsuarios: IPerfilUsuario[]) => this.perfilUsuariosSharedCollection.set(perfilUsuarios));

    this.inmuebleService
      .query()
      .pipe(map((res: HttpResponse<IInmueble[]>) => res.body ?? []))
      .pipe(
        map((inmuebles: IInmueble[]) =>
          this.inmuebleService.addInmuebleToCollectionIfMissing<IInmueble>(inmuebles, this.publicacionRoomie?.inmueble),
        ),
      )
      .subscribe((inmuebles: IInmueble[]) => this.inmueblesSharedCollection.set(inmuebles));
  }
}
