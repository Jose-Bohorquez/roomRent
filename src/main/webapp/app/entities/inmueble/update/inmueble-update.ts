import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { TipoInmueble } from 'app/entities/enumerations/tipo-inmueble.model';
import { IPerfilUsuario } from 'app/entities/perfil-usuario/perfil-usuario.model';
import { PerfilUsuarioService } from 'app/entities/perfil-usuario/service/perfil-usuario.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IInmueble } from '../inmueble.model';
import { InmuebleService } from '../service/inmueble.service';

import { InmuebleFormGroup, InmuebleFormService } from './inmueble-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-inmueble-update',
  templateUrl: './inmueble-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class InmuebleUpdate implements OnInit {
  readonly isSaving = signal(false);
  inmueble: IInmueble | null = null;
  tipoInmuebleValues = Object.keys(TipoInmueble);

  perfilUsuariosSharedCollection = signal<IPerfilUsuario[]>([]);

  protected inmuebleService = inject(InmuebleService);
  protected inmuebleFormService = inject(InmuebleFormService);
  protected perfilUsuarioService = inject(PerfilUsuarioService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: InmuebleFormGroup = this.inmuebleFormService.createInmuebleFormGroup();

  comparePerfilUsuario = (o1: IPerfilUsuario | null, o2: IPerfilUsuario | null): boolean =>
    this.perfilUsuarioService.comparePerfilUsuario(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ inmueble }) => {
      this.inmueble = inmueble;
      if (inmueble) {
        this.updateForm(inmueble);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const inmueble = this.inmuebleFormService.getInmueble(this.editForm);
    if (inmueble.id === null) {
      this.subscribeToSaveResponse(this.inmuebleService.create(inmueble));
    } else {
      this.subscribeToSaveResponse(this.inmuebleService.update(inmueble));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IInmueble | null>): void {
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

  protected updateForm(inmueble: IInmueble): void {
    this.inmueble = inmueble;
    this.inmuebleFormService.resetForm(this.editForm, inmueble);

    this.perfilUsuariosSharedCollection.update(perfilUsuarios =>
      this.perfilUsuarioService.addPerfilUsuarioToCollectionIfMissing<IPerfilUsuario>(perfilUsuarios, inmueble.propietario),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.perfilUsuarioService
      .query()
      .pipe(map((res: HttpResponse<IPerfilUsuario[]>) => res.body ?? []))
      .pipe(
        map((perfilUsuarios: IPerfilUsuario[]) =>
          this.perfilUsuarioService.addPerfilUsuarioToCollectionIfMissing<IPerfilUsuario>(perfilUsuarios, this.inmueble?.propietario),
        ),
      )
      .subscribe((perfilUsuarios: IPerfilUsuario[]) => this.perfilUsuariosSharedCollection.set(perfilUsuarios));
  }
}
