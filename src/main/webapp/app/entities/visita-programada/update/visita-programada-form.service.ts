import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IVisitaProgramada, NewVisitaProgramada } from '../visita-programada.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IVisitaProgramada for edit and NewVisitaProgramadaFormGroupInput for create.
 */
type VisitaProgramadaFormGroupInput = IVisitaProgramada | PartialWithRequiredKeyOf<NewVisitaProgramada>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IVisitaProgramada | NewVisitaProgramada> = Omit<T, 'fechaSolicitada' | 'fechaConfirmada'> & {
  fechaSolicitada?: string | null;
  fechaConfirmada?: string | null;
};

type VisitaProgramadaFormRawValue = FormValueOf<IVisitaProgramada>;

type NewVisitaProgramadaFormRawValue = FormValueOf<NewVisitaProgramada>;

type VisitaProgramadaFormDefaults = Pick<NewVisitaProgramada, 'id' | 'fechaSolicitada' | 'fechaConfirmada'>;

type VisitaProgramadaFormGroupContent = {
  id: FormControl<VisitaProgramadaFormRawValue['id'] | NewVisitaProgramada['id']>;
  fechaSolicitada: FormControl<VisitaProgramadaFormRawValue['fechaSolicitada']>;
  fechaConfirmada: FormControl<VisitaProgramadaFormRawValue['fechaConfirmada']>;
  notas: FormControl<VisitaProgramadaFormRawValue['notas']>;
  estado: FormControl<VisitaProgramadaFormRawValue['estado']>;
  visitante: FormControl<VisitaProgramadaFormRawValue['visitante']>;
  solicitud: FormControl<VisitaProgramadaFormRawValue['solicitud']>;
};

export type VisitaProgramadaFormGroup = FormGroup<VisitaProgramadaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class VisitaProgramadaFormService {
  createVisitaProgramadaFormGroup(visitaProgramada?: VisitaProgramadaFormGroupInput): VisitaProgramadaFormGroup {
    const visitaProgramadaRawValue = this.convertVisitaProgramadaToVisitaProgramadaRawValue({
      ...this.getFormDefaults(),
      ...(visitaProgramada ?? { id: null }),
    });
    return new FormGroup<VisitaProgramadaFormGroupContent>({
      id: new FormControl(
        { value: visitaProgramadaRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      fechaSolicitada: new FormControl(visitaProgramadaRawValue.fechaSolicitada, {
        validators: [Validators.required],
      }),
      fechaConfirmada: new FormControl(visitaProgramadaRawValue.fechaConfirmada),
      notas: new FormControl(visitaProgramadaRawValue.notas),
      estado: new FormControl(visitaProgramadaRawValue.estado, {
        validators: [Validators.required],
      }),
      visitante: new FormControl(visitaProgramadaRawValue.visitante),
      solicitud: new FormControl(visitaProgramadaRawValue.solicitud),
    });
  }

  getVisitaProgramada(form: VisitaProgramadaFormGroup): IVisitaProgramada | NewVisitaProgramada {
    return this.convertVisitaProgramadaRawValueToVisitaProgramada(form.getRawValue());
  }

  resetForm(form: VisitaProgramadaFormGroup, visitaProgramada: VisitaProgramadaFormGroupInput): void {
    const visitaProgramadaRawValue = this.convertVisitaProgramadaToVisitaProgramadaRawValue({
      ...this.getFormDefaults(),
      ...visitaProgramada,
    });
    form.reset({
      ...visitaProgramadaRawValue,
      id: { value: visitaProgramadaRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): VisitaProgramadaFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      fechaSolicitada: currentTime,
      fechaConfirmada: currentTime,
    };
  }

  private convertVisitaProgramadaRawValueToVisitaProgramada(
    rawVisitaProgramada: VisitaProgramadaFormRawValue | NewVisitaProgramadaFormRawValue,
  ): IVisitaProgramada | NewVisitaProgramada {
    return {
      ...rawVisitaProgramada,
      fechaSolicitada: dayjs(rawVisitaProgramada.fechaSolicitada, DATE_TIME_FORMAT),
      fechaConfirmada: dayjs(rawVisitaProgramada.fechaConfirmada, DATE_TIME_FORMAT),
    };
  }

  private convertVisitaProgramadaToVisitaProgramadaRawValue(
    visitaProgramada: IVisitaProgramada | (Partial<NewVisitaProgramada> & VisitaProgramadaFormDefaults),
  ): VisitaProgramadaFormRawValue | PartialWithRequiredKeyOf<NewVisitaProgramadaFormRawValue> {
    return {
      ...visitaProgramada,
      fechaSolicitada: visitaProgramada.fechaSolicitada ? visitaProgramada.fechaSolicitada.format(DATE_TIME_FORMAT) : undefined,
      fechaConfirmada: visitaProgramada.fechaConfirmada ? visitaProgramada.fechaConfirmada.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
