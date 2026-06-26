import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPublicacionInmueble, NewPublicacionInmueble } from '../publicacion-inmueble.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPublicacionInmueble for edit and NewPublicacionInmuebleFormGroupInput for create.
 */
type PublicacionInmuebleFormGroupInput = IPublicacionInmueble | PartialWithRequiredKeyOf<NewPublicacionInmueble>;

type PublicacionInmuebleFormDefaults = Pick<
  NewPublicacionInmueble,
  | 'id'
  | 'seguroRequerido'
  | 'datacreditoRequerido'
  | 'permiteRoomies'
  | 'aceptaMascotas'
  | 'permiteFumadores'
  | 'permiteNinos'
  | 'permiteVisitas'
  | 'permiteParejas'
>;

type PublicacionInmuebleFormGroupContent = {
  id: FormControl<IPublicacionInmueble['id'] | NewPublicacionInmueble['id']>;
  titulo: FormControl<IPublicacionInmueble['titulo']>;
  descripcion: FormControl<IPublicacionInmueble['descripcion']>;
  canonArriendo: FormControl<IPublicacionInmueble['canonArriendo']>;
  deposito: FormControl<IPublicacionInmueble['deposito']>;
  requisitos: FormControl<IPublicacionInmueble['requisitos']>;
  seguroRequerido: FormControl<IPublicacionInmueble['seguroRequerido']>;
  datacreditoRequerido: FormControl<IPublicacionInmueble['datacreditoRequerido']>;
  fechaDisponible: FormControl<IPublicacionInmueble['fechaDisponible']>;
  estado: FormControl<IPublicacionInmueble['estado']>;
  permiteRoomies: FormControl<IPublicacionInmueble['permiteRoomies']>;
  aceptaMascotas: FormControl<IPublicacionInmueble['aceptaMascotas']>;
  permiteFumadores: FormControl<IPublicacionInmueble['permiteFumadores']>;
  permiteNinos: FormControl<IPublicacionInmueble['permiteNinos']>;
  permiteVisitas: FormControl<IPublicacionInmueble['permiteVisitas']>;
  permiteParejas: FormControl<IPublicacionInmueble['permiteParejas']>;
  inmueble: FormControl<IPublicacionInmueble['inmueble']>;
};

export type PublicacionInmuebleFormGroup = FormGroup<PublicacionInmuebleFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PublicacionInmuebleFormService {
  createPublicacionInmuebleFormGroup(publicacionInmueble?: PublicacionInmuebleFormGroupInput): PublicacionInmuebleFormGroup {
    const publicacionInmuebleRawValue = {
      ...this.getFormDefaults(),
      ...(publicacionInmueble ?? { id: null }),
    };
    return new FormGroup<PublicacionInmuebleFormGroupContent>({
      id: new FormControl(
        { value: publicacionInmuebleRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      titulo: new FormControl(publicacionInmuebleRawValue.titulo, {
        validators: [Validators.required],
      }),
      descripcion: new FormControl(publicacionInmuebleRawValue.descripcion),
      canonArriendo: new FormControl(publicacionInmuebleRawValue.canonArriendo, {
        validators: [Validators.required],
      }),
      deposito: new FormControl(publicacionInmuebleRawValue.deposito),
      requisitos: new FormControl(publicacionInmuebleRawValue.requisitos),
      seguroRequerido: new FormControl(publicacionInmuebleRawValue.seguroRequerido),
      datacreditoRequerido: new FormControl(publicacionInmuebleRawValue.datacreditoRequerido),
      fechaDisponible: new FormControl(publicacionInmuebleRawValue.fechaDisponible),
      estado: new FormControl(publicacionInmuebleRawValue.estado, {
        validators: [Validators.required],
      }),
      permiteRoomies: new FormControl(publicacionInmuebleRawValue.permiteRoomies, {
        validators: [Validators.required],
      }),
      aceptaMascotas: new FormControl(publicacionInmuebleRawValue.aceptaMascotas, {
        validators: [Validators.required],
      }),
      permiteFumadores: new FormControl(publicacionInmuebleRawValue.permiteFumadores, {
        validators: [Validators.required],
      }),
      permiteNinos: new FormControl(publicacionInmuebleRawValue.permiteNinos, {
        validators: [Validators.required],
      }),
      permiteVisitas: new FormControl(publicacionInmuebleRawValue.permiteVisitas, {
        validators: [Validators.required],
      }),
      permiteParejas: new FormControl(publicacionInmuebleRawValue.permiteParejas, {
        validators: [Validators.required],
      }),
      inmueble: new FormControl(publicacionInmuebleRawValue.inmueble),
    });
  }

  getPublicacionInmueble(form: PublicacionInmuebleFormGroup): IPublicacionInmueble | NewPublicacionInmueble {
    return form.getRawValue();
  }

  resetForm(form: PublicacionInmuebleFormGroup, publicacionInmueble: PublicacionInmuebleFormGroupInput): void {
    const publicacionInmuebleRawValue = { ...this.getFormDefaults(), ...publicacionInmueble };
    form.reset({
      ...publicacionInmuebleRawValue,
      id: { value: publicacionInmuebleRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): PublicacionInmuebleFormDefaults {
    return {
      id: null,
      seguroRequerido: false,
      datacreditoRequerido: false,
      permiteRoomies: false,
      aceptaMascotas: false,
      permiteFumadores: false,
      permiteNinos: false,
      permiteVisitas: false,
      permiteParejas: false,
    };
  }
}
