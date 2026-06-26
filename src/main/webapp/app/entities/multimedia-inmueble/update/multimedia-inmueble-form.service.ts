import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IMultimediaInmueble, NewMultimediaInmueble } from '../multimedia-inmueble.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMultimediaInmueble for edit and NewMultimediaInmuebleFormGroupInput for create.
 */
type MultimediaInmuebleFormGroupInput = IMultimediaInmueble | PartialWithRequiredKeyOf<NewMultimediaInmueble>;

type MultimediaInmuebleFormDefaults = Pick<NewMultimediaInmueble, 'id' | 'principal'>;

type MultimediaInmuebleFormGroupContent = {
  id: FormControl<IMultimediaInmueble['id'] | NewMultimediaInmueble['id']>;
  urlMedia: FormControl<IMultimediaInmueble['urlMedia']>;
  tipoMedia: FormControl<IMultimediaInmueble['tipoMedia']>;
  principal: FormControl<IMultimediaInmueble['principal']>;
  titulo: FormControl<IMultimediaInmueble['titulo']>;
  inmueble: FormControl<IMultimediaInmueble['inmueble']>;
};

export type MultimediaInmuebleFormGroup = FormGroup<MultimediaInmuebleFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MultimediaInmuebleFormService {
  createMultimediaInmuebleFormGroup(multimediaInmueble?: MultimediaInmuebleFormGroupInput): MultimediaInmuebleFormGroup {
    const multimediaInmuebleRawValue = {
      ...this.getFormDefaults(),
      ...(multimediaInmueble ?? { id: null }),
    };
    return new FormGroup<MultimediaInmuebleFormGroupContent>({
      id: new FormControl(
        { value: multimediaInmuebleRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      urlMedia: new FormControl(multimediaInmuebleRawValue.urlMedia, {
        validators: [Validators.required],
      }),
      tipoMedia: new FormControl(multimediaInmuebleRawValue.tipoMedia, {
        validators: [Validators.required],
      }),
      principal: new FormControl(multimediaInmuebleRawValue.principal, {
        validators: [Validators.required],
      }),
      titulo: new FormControl(multimediaInmuebleRawValue.titulo),
      inmueble: new FormControl(multimediaInmuebleRawValue.inmueble),
    });
  }

  getMultimediaInmueble(form: MultimediaInmuebleFormGroup): IMultimediaInmueble | NewMultimediaInmueble {
    return form.getRawValue();
  }

  resetForm(form: MultimediaInmuebleFormGroup, multimediaInmueble: MultimediaInmuebleFormGroupInput): void {
    const multimediaInmuebleRawValue = { ...this.getFormDefaults(), ...multimediaInmueble };
    form.reset({
      ...multimediaInmuebleRawValue,
      id: { value: multimediaInmuebleRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): MultimediaInmuebleFormDefaults {
    return {
      id: null,
      principal: false,
    };
  }
}
