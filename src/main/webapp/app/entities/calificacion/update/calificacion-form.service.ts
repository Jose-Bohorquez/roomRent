import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICalificacion, NewCalificacion } from '../calificacion.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICalificacion for edit and NewCalificacionFormGroupInput for create.
 */
type CalificacionFormGroupInput = ICalificacion | PartialWithRequiredKeyOf<NewCalificacion>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICalificacion | NewCalificacion> = Omit<T, 'fechaCreacion'> & {
  fechaCreacion?: string | null;
};

type CalificacionFormRawValue = FormValueOf<ICalificacion>;

type NewCalificacionFormRawValue = FormValueOf<NewCalificacion>;

type CalificacionFormDefaults = Pick<NewCalificacion, 'id' | 'fechaCreacion' | 'visible'>;

type CalificacionFormGroupContent = {
  id: FormControl<CalificacionFormRawValue['id'] | NewCalificacion['id']>;
  tipoCalificacion: FormControl<CalificacionFormRawValue['tipoCalificacion']>;
  puntaje: FormControl<CalificacionFormRawValue['puntaje']>;
  comentario: FormControl<CalificacionFormRawValue['comentario']>;
  fechaCreacion: FormControl<CalificacionFormRawValue['fechaCreacion']>;
  visible: FormControl<CalificacionFormRawValue['visible']>;
  autor: FormControl<CalificacionFormRawValue['autor']>;
  calificado: FormControl<CalificacionFormRawValue['calificado']>;
  contrato: FormControl<CalificacionFormRawValue['contrato']>;
};

export type CalificacionFormGroup = FormGroup<CalificacionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CalificacionFormService {
  createCalificacionFormGroup(calificacion?: CalificacionFormGroupInput): CalificacionFormGroup {
    const calificacionRawValue = this.convertCalificacionToCalificacionRawValue({
      ...this.getFormDefaults(),
      ...(calificacion ?? { id: null }),
    });
    return new FormGroup<CalificacionFormGroupContent>({
      id: new FormControl(
        { value: calificacionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      tipoCalificacion: new FormControl(calificacionRawValue.tipoCalificacion, {
        validators: [Validators.required],
      }),
      puntaje: new FormControl(calificacionRawValue.puntaje, {
        validators: [Validators.required, Validators.min(1), Validators.max(5)],
      }),
      comentario: new FormControl(calificacionRawValue.comentario),
      fechaCreacion: new FormControl(calificacionRawValue.fechaCreacion, {
        validators: [Validators.required],
      }),
      visible: new FormControl(calificacionRawValue.visible, {
        validators: [Validators.required],
      }),
      autor: new FormControl(calificacionRawValue.autor),
      calificado: new FormControl(calificacionRawValue.calificado),
      contrato: new FormControl(calificacionRawValue.contrato),
    });
  }

  getCalificacion(form: CalificacionFormGroup): ICalificacion | NewCalificacion {
    return this.convertCalificacionRawValueToCalificacion(form.getRawValue());
  }

  resetForm(form: CalificacionFormGroup, calificacion: CalificacionFormGroupInput): void {
    const calificacionRawValue = this.convertCalificacionToCalificacionRawValue({ ...this.getFormDefaults(), ...calificacion });
    form.reset({
      ...calificacionRawValue,
      id: { value: calificacionRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): CalificacionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      fechaCreacion: currentTime,
      visible: false,
    };
  }

  private convertCalificacionRawValueToCalificacion(
    rawCalificacion: CalificacionFormRawValue | NewCalificacionFormRawValue,
  ): ICalificacion | NewCalificacion {
    return {
      ...rawCalificacion,
      fechaCreacion: dayjs(rawCalificacion.fechaCreacion, DATE_TIME_FORMAT),
    };
  }

  private convertCalificacionToCalificacionRawValue(
    calificacion: ICalificacion | (Partial<NewCalificacion> & CalificacionFormDefaults),
  ): CalificacionFormRawValue | PartialWithRequiredKeyOf<NewCalificacionFormRawValue> {
    return {
      ...calificacion,
      fechaCreacion: calificacion.fechaCreacion ? calificacion.fechaCreacion.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
