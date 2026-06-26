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
import { EstadoPublicacion } from 'app/entities/enumerations/estado-publicacion.model';
import { IInmueble } from 'app/entities/inmueble/inmueble.model';
import { InmuebleService } from 'app/entities/inmueble/service/inmueble.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { IPublicacionInmueble } from '../publicacion-inmueble.model';
import { PublicacionInmuebleService } from '../service/publicacion-inmueble.service';

import { PublicacionInmuebleFormGroup, PublicacionInmuebleFormService } from './publicacion-inmueble-form.service';
import { AlertErrorModel } from 'app/shared/alert/alert-error.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-publicacion-inmueble-update',
  templateUrl: './publicacion-inmueble-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class PublicacionInmuebleUpdate implements OnInit {
  readonly isSaving = signal(false);
  publicacionInmueble: IPublicacionInmueble | null = null;
  estadoPublicacionValues = Object.keys(EstadoPublicacion);

  inmueblesSharedCollection = signal<IInmueble[]>([]);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected publicacionInmuebleService = inject(PublicacionInmuebleService);
  protected publicacionInmuebleFormService = inject(PublicacionInmuebleFormService);
  protected inmuebleService = inject(InmuebleService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PublicacionInmuebleFormGroup = this.publicacionInmuebleFormService.createPublicacionInmuebleFormGroup();

  compareInmueble = (o1: IInmueble | null, o2: IInmueble | null): boolean => this.inmuebleService.compareInmueble(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ publicacionInmueble }) => {
      this.publicacionInmueble = publicacionInmueble;
      if (publicacionInmueble) {
        this.updateForm(publicacionInmueble);
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
    const publicacionInmueble = this.publicacionInmuebleFormService.getPublicacionInmueble(this.editForm);
    if (publicacionInmueble.id === null) {
      this.subscribeToSaveResponse(this.publicacionInmuebleService.create(publicacionInmueble));
    } else {
      this.subscribeToSaveResponse(this.publicacionInmuebleService.update(publicacionInmueble));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IPublicacionInmueble | null>): void {
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

  protected updateForm(publicacionInmueble: IPublicacionInmueble): void {
    this.publicacionInmueble = publicacionInmueble;
    this.publicacionInmuebleFormService.resetForm(this.editForm, publicacionInmueble);

    this.inmueblesSharedCollection.update(inmuebles =>
      this.inmuebleService.addInmuebleToCollectionIfMissing<IInmueble>(inmuebles, publicacionInmueble.inmueble),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.inmuebleService
      .query()
      .pipe(map((res: HttpResponse<IInmueble[]>) => res.body ?? []))
      .pipe(
        map((inmuebles: IInmueble[]) =>
          this.inmuebleService.addInmuebleToCollectionIfMissing<IInmueble>(inmuebles, this.publicacionInmueble?.inmueble),
        ),
      )
      .subscribe((inmuebles: IInmueble[]) => this.inmueblesSharedCollection.set(inmuebles));
  }
}
