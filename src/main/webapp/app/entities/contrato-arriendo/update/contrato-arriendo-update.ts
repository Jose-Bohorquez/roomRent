import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { EstadoContrato } from 'app/entities/enumerations/estado-contrato.model';
import { IInmueble } from 'app/entities/inmueble/inmueble.model';
import { InmuebleService } from 'app/entities/inmueble/service/inmueble.service';
import { IPerfilUsuario } from 'app/entities/perfil-usuario/perfil-usuario.model';
import { PerfilUsuarioService } from 'app/entities/perfil-usuario/service/perfil-usuario.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { IContratoArriendo } from '../contrato-arriendo.model';
import { ContratoArriendoService } from '../service/contrato-arriendo.service';

import { ContratoArriendoFormGroup, ContratoArriendoFormService } from './contrato-arriendo-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-contrato-arriendo-update',
  templateUrl: './contrato-arriendo-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class ContratoArriendoUpdate implements OnInit {
  readonly isSaving = signal(false);
  contratoArriendo: IContratoArriendo | null = null;
  estadoContratoValues = Object.keys(EstadoContrato);

  perfilUsuariosSharedCollection = signal<IPerfilUsuario[]>([]);
  inmueblesSharedCollection = signal<IInmueble[]>([]);

  protected contratoArriendoService = inject(ContratoArriendoService);
  protected contratoArriendoFormService = inject(ContratoArriendoFormService);
  protected perfilUsuarioService = inject(PerfilUsuarioService);
  protected inmuebleService = inject(InmuebleService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ContratoArriendoFormGroup = this.contratoArriendoFormService.createContratoArriendoFormGroup();

  comparePerfilUsuario = (o1: IPerfilUsuario | null, o2: IPerfilUsuario | null): boolean =>
    this.perfilUsuarioService.comparePerfilUsuario(o1, o2);

  compareInmueble = (o1: IInmueble | null, o2: IInmueble | null): boolean => this.inmuebleService.compareInmueble(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ contratoArriendo }) => {
      this.contratoArriendo = contratoArriendo;
      if (contratoArriendo) {
        this.updateForm(contratoArriendo);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const contratoArriendo = this.contratoArriendoFormService.getContratoArriendo(this.editForm);
    if (contratoArriendo.id === null) {
      this.subscribeToSaveResponse(this.contratoArriendoService.create(contratoArriendo));
    } else {
      this.subscribeToSaveResponse(this.contratoArriendoService.update(contratoArriendo));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IContratoArriendo | null>): void {
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

  protected updateForm(contratoArriendo: IContratoArriendo): void {
    this.contratoArriendo = contratoArriendo;
    this.contratoArriendoFormService.resetForm(this.editForm, contratoArriendo);

    this.perfilUsuariosSharedCollection.update(perfilUsuarios =>
      this.perfilUsuarioService.addPerfilUsuarioToCollectionIfMissing<IPerfilUsuario>(
        perfilUsuarios,
        contratoArriendo.arrendador,
        contratoArriendo.arrendatario,
      ),
    );
    this.inmueblesSharedCollection.update(inmuebles =>
      this.inmuebleService.addInmuebleToCollectionIfMissing<IInmueble>(inmuebles, contratoArriendo.inmueble),
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
            this.contratoArriendo?.arrendador,
            this.contratoArriendo?.arrendatario,
          ),
        ),
      )
      .subscribe((perfilUsuarios: IPerfilUsuario[]) => this.perfilUsuariosSharedCollection.set(perfilUsuarios));

    this.inmuebleService
      .query()
      .pipe(map((res: HttpResponse<IInmueble[]>) => res.body ?? []))
      .pipe(
        map((inmuebles: IInmueble[]) =>
          this.inmuebleService.addInmuebleToCollectionIfMissing<IInmueble>(inmuebles, this.contratoArriendo?.inmueble),
        ),
      )
      .subscribe((inmuebles: IInmueble[]) => this.inmueblesSharedCollection.set(inmuebles));
  }
}
