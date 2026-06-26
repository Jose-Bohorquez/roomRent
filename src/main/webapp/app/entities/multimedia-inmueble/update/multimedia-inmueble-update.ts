import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IInmueble } from 'app/entities/inmueble/inmueble.model';
import { InmuebleService } from 'app/entities/inmueble/service/inmueble.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IMultimediaInmueble } from '../multimedia-inmueble.model';
import { MultimediaInmuebleService } from '../service/multimedia-inmueble.service';

import { MultimediaInmuebleFormGroup, MultimediaInmuebleFormService } from './multimedia-inmueble-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-multimedia-inmueble-update',
  templateUrl: './multimedia-inmueble-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class MultimediaInmuebleUpdate implements OnInit {
  readonly isSaving = signal(false);
  multimediaInmueble: IMultimediaInmueble | null = null;

  inmueblesSharedCollection = signal<IInmueble[]>([]);

  protected multimediaInmuebleService = inject(MultimediaInmuebleService);
  protected multimediaInmuebleFormService = inject(MultimediaInmuebleFormService);
  protected inmuebleService = inject(InmuebleService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MultimediaInmuebleFormGroup = this.multimediaInmuebleFormService.createMultimediaInmuebleFormGroup();

  compareInmueble = (o1: IInmueble | null, o2: IInmueble | null): boolean => this.inmuebleService.compareInmueble(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ multimediaInmueble }) => {
      this.multimediaInmueble = multimediaInmueble;
      if (multimediaInmueble) {
        this.updateForm(multimediaInmueble);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const multimediaInmueble = this.multimediaInmuebleFormService.getMultimediaInmueble(this.editForm);
    if (multimediaInmueble.id === null) {
      this.subscribeToSaveResponse(this.multimediaInmuebleService.create(multimediaInmueble));
    } else {
      this.subscribeToSaveResponse(this.multimediaInmuebleService.update(multimediaInmueble));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IMultimediaInmueble | null>): void {
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

  protected updateForm(multimediaInmueble: IMultimediaInmueble): void {
    this.multimediaInmueble = multimediaInmueble;
    this.multimediaInmuebleFormService.resetForm(this.editForm, multimediaInmueble);

    this.inmueblesSharedCollection.update(inmuebles =>
      this.inmuebleService.addInmuebleToCollectionIfMissing<IInmueble>(inmuebles, multimediaInmueble.inmueble),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.inmuebleService
      .query()
      .pipe(map((res: HttpResponse<IInmueble[]>) => res.body ?? []))
      .pipe(
        map((inmuebles: IInmueble[]) =>
          this.inmuebleService.addInmuebleToCollectionIfMissing<IInmueble>(inmuebles, this.multimediaInmueble?.inmueble),
        ),
      )
      .subscribe((inmuebles: IInmueble[]) => this.inmueblesSharedCollection.set(inmuebles));
  }
}
