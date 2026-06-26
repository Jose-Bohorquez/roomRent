import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPublicacionRoomie, NewPublicacionRoomie } from '../publicacion-roomie.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPublicacionRoomie for edit and NewPublicacionRoomieFormGroupInput for create.
 */
type PublicacionRoomieFormGroupInput = IPublicacionRoomie | PartialWithRequiredKeyOf<NewPublicacionRoomie>;

type PublicacionRoomieFormDefaults = Pick<NewPublicacionRoomie, 'id'>;

type PublicacionRoomieFormGroupContent = {
  id: FormControl<IPublicacionRoomie['id'] | NewPublicacionRoomie['id']>;
  titulo: FormControl<IPublicacionRoomie['titulo']>;
  nombreHabitacion: FormControl<IPublicacionRoomie['nombreHabitacion']>;
  valorMensual: FormControl<IPublicacionRoomie['valorMensual']>;
  serviciosIncluidos: FormControl<IPublicacionRoomie['serviciosIncluidos']>;
  espaciosCompartidos: FormControl<IPublicacionRoomie['espaciosCompartidos']>;
  generoPreferido: FormControl<IPublicacionRoomie['generoPreferido']>;
  fechaDisponible: FormControl<IPublicacionRoomie['fechaDisponible']>;
  estado: FormControl<IPublicacionRoomie['estado']>;
  arrendatario: FormControl<IPublicacionRoomie['arrendatario']>;
  inmueble: FormControl<IPublicacionRoomie['inmueble']>;
};

export type PublicacionRoomieFormGroup = FormGroup<PublicacionRoomieFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PublicacionRoomieFormService {
  createPublicacionRoomieFormGroup(publicacionRoomie?: PublicacionRoomieFormGroupInput): PublicacionRoomieFormGroup {
    const publicacionRoomieRawValue = {
      ...this.getFormDefaults(),
      ...(publicacionRoomie ?? { id: null }),
    };
    return new FormGroup<PublicacionRoomieFormGroupContent>({
      id: new FormControl(
        { value: publicacionRoomieRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      titulo: new FormControl(publicacionRoomieRawValue.titulo, {
        validators: [Validators.required],
      }),
      nombreHabitacion: new FormControl(publicacionRoomieRawValue.nombreHabitacion, {
        validators: [Validators.required],
      }),
      valorMensual: new FormControl(publicacionRoomieRawValue.valorMensual, {
        validators: [Validators.required],
      }),
      serviciosIncluidos: new FormControl(publicacionRoomieRawValue.serviciosIncluidos),
      espaciosCompartidos: new FormControl(publicacionRoomieRawValue.espaciosCompartidos),
      generoPreferido: new FormControl(publicacionRoomieRawValue.generoPreferido),
      fechaDisponible: new FormControl(publicacionRoomieRawValue.fechaDisponible),
      estado: new FormControl(publicacionRoomieRawValue.estado, {
        validators: [Validators.required],
      }),
      arrendatario: new FormControl(publicacionRoomieRawValue.arrendatario),
      inmueble: new FormControl(publicacionRoomieRawValue.inmueble),
    });
  }

  getPublicacionRoomie(form: PublicacionRoomieFormGroup): IPublicacionRoomie | NewPublicacionRoomie {
    return form.getRawValue();
  }

  resetForm(form: PublicacionRoomieFormGroup, publicacionRoomie: PublicacionRoomieFormGroupInput): void {
    const publicacionRoomieRawValue = { ...this.getFormDefaults(), ...publicacionRoomie };
    form.reset({
      ...publicacionRoomieRawValue,
      id: { value: publicacionRoomieRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): PublicacionRoomieFormDefaults {
    return {
      id: null,
    };
  }
}
