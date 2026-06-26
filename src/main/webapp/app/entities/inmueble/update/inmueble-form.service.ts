import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IInmueble, NewInmueble } from '../inmueble.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IInmueble for edit and NewInmuebleFormGroupInput for create.
 */
type InmuebleFormGroupInput = IInmueble | PartialWithRequiredKeyOf<NewInmueble>;

type InmuebleFormDefaults = Pick<NewInmueble, 'id'>;

type InmuebleFormGroupContent = {
  id: FormControl<IInmueble['id'] | NewInmueble['id']>;
  nombre: FormControl<IInmueble['nombre']>;
  direccion: FormControl<IInmueble['direccion']>;
  ciudad: FormControl<IInmueble['ciudad']>;
  localidad: FormControl<IInmueble['localidad']>;
  barrio: FormControl<IInmueble['barrio']>;
  latitud: FormControl<IInmueble['latitud']>;
  longitud: FormControl<IInmueble['longitud']>;
  tipoInmueble: FormControl<IInmueble['tipoInmueble']>;
  areaMetrosCuadrados: FormControl<IInmueble['areaMetrosCuadrados']>;
  numeroHabitaciones: FormControl<IInmueble['numeroHabitaciones']>;
  numeroBanos: FormControl<IInmueble['numeroBanos']>;
  numeroParqueaderos: FormControl<IInmueble['numeroParqueaderos']>;
  estrato: FormControl<IInmueble['estrato']>;
  propietario: FormControl<IInmueble['propietario']>;
};

export type InmuebleFormGroup = FormGroup<InmuebleFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class InmuebleFormService {
  createInmuebleFormGroup(inmueble?: InmuebleFormGroupInput): InmuebleFormGroup {
    const inmuebleRawValue = {
      ...this.getFormDefaults(),
      ...(inmueble ?? { id: null }),
    };
    return new FormGroup<InmuebleFormGroupContent>({
      id: new FormControl(
        { value: inmuebleRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nombre: new FormControl(inmuebleRawValue.nombre, {
        validators: [Validators.required],
      }),
      direccion: new FormControl(inmuebleRawValue.direccion, {
        validators: [Validators.required],
      }),
      ciudad: new FormControl(inmuebleRawValue.ciudad, {
        validators: [Validators.required],
      }),
      localidad: new FormControl(inmuebleRawValue.localidad),
      barrio: new FormControl(inmuebleRawValue.barrio, {
        validators: [Validators.required],
      }),
      latitud: new FormControl(inmuebleRawValue.latitud),
      longitud: new FormControl(inmuebleRawValue.longitud),
      tipoInmueble: new FormControl(inmuebleRawValue.tipoInmueble, {
        validators: [Validators.required],
      }),
      areaMetrosCuadrados: new FormControl(inmuebleRawValue.areaMetrosCuadrados),
      numeroHabitaciones: new FormControl(inmuebleRawValue.numeroHabitaciones, {
        validators: [Validators.required],
      }),
      numeroBanos: new FormControl(inmuebleRawValue.numeroBanos, {
        validators: [Validators.required],
      }),
      numeroParqueaderos: new FormControl(inmuebleRawValue.numeroParqueaderos),
      estrato: new FormControl(inmuebleRawValue.estrato, {
        validators: [Validators.min(1), Validators.max(6)],
      }),
      propietario: new FormControl(inmuebleRawValue.propietario),
    });
  }

  getInmueble(form: InmuebleFormGroup): IInmueble | NewInmueble {
    return form.getRawValue();
  }

  resetForm(form: InmuebleFormGroup, inmueble: InmuebleFormGroupInput): void {
    const inmuebleRawValue = { ...this.getFormDefaults(), ...inmueble };
    form.reset({
      ...inmuebleRawValue,
      id: { value: inmuebleRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): InmuebleFormDefaults {
    return {
      id: null,
    };
  }
}
