import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IContratoArriendo, NewContratoArriendo } from '../contrato-arriendo.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IContratoArriendo for edit and NewContratoArriendoFormGroupInput for create.
 */
type ContratoArriendoFormGroupInput = IContratoArriendo | PartialWithRequiredKeyOf<NewContratoArriendo>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IContratoArriendo | NewContratoArriendo> = Omit<T, 'fechaFirma'> & {
  fechaFirma?: string | null;
};

type ContratoArriendoFormRawValue = FormValueOf<IContratoArriendo>;

type NewContratoArriendoFormRawValue = FormValueOf<NewContratoArriendo>;

type ContratoArriendoFormDefaults = Pick<NewContratoArriendo, 'id' | 'fechaFirma'>;

type ContratoArriendoFormGroupContent = {
  id: FormControl<ContratoArriendoFormRawValue['id'] | NewContratoArriendo['id']>;
  numeroContrato: FormControl<ContratoArriendoFormRawValue['numeroContrato']>;
  urlContratoDigital: FormControl<ContratoArriendoFormRawValue['urlContratoDigital']>;
  fechaInicio: FormControl<ContratoArriendoFormRawValue['fechaInicio']>;
  fechaFin: FormControl<ContratoArriendoFormRawValue['fechaFin']>;
  valorMensual: FormControl<ContratoArriendoFormRawValue['valorMensual']>;
  valorDeposito: FormControl<ContratoArriendoFormRawValue['valorDeposito']>;
  estado: FormControl<ContratoArriendoFormRawValue['estado']>;
  fechaFirma: FormControl<ContratoArriendoFormRawValue['fechaFirma']>;
  arrendador: FormControl<ContratoArriendoFormRawValue['arrendador']>;
  arrendatario: FormControl<ContratoArriendoFormRawValue['arrendatario']>;
  inmueble: FormControl<ContratoArriendoFormRawValue['inmueble']>;
};

export type ContratoArriendoFormGroup = FormGroup<ContratoArriendoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ContratoArriendoFormService {
  createContratoArriendoFormGroup(contratoArriendo?: ContratoArriendoFormGroupInput): ContratoArriendoFormGroup {
    const contratoArriendoRawValue = this.convertContratoArriendoToContratoArriendoRawValue({
      ...this.getFormDefaults(),
      ...(contratoArriendo ?? { id: null }),
    });
    return new FormGroup<ContratoArriendoFormGroupContent>({
      id: new FormControl(
        { value: contratoArriendoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      numeroContrato: new FormControl(contratoArriendoRawValue.numeroContrato, {
        validators: [Validators.required],
      }),
      urlContratoDigital: new FormControl(contratoArriendoRawValue.urlContratoDigital),
      fechaInicio: new FormControl(contratoArriendoRawValue.fechaInicio, {
        validators: [Validators.required],
      }),
      fechaFin: new FormControl(contratoArriendoRawValue.fechaFin, {
        validators: [Validators.required],
      }),
      valorMensual: new FormControl(contratoArriendoRawValue.valorMensual, {
        validators: [Validators.required],
      }),
      valorDeposito: new FormControl(contratoArriendoRawValue.valorDeposito),
      estado: new FormControl(contratoArriendoRawValue.estado, {
        validators: [Validators.required],
      }),
      fechaFirma: new FormControl(contratoArriendoRawValue.fechaFirma),
      arrendador: new FormControl(contratoArriendoRawValue.arrendador),
      arrendatario: new FormControl(contratoArriendoRawValue.arrendatario),
      inmueble: new FormControl(contratoArriendoRawValue.inmueble),
    });
  }

  getContratoArriendo(form: ContratoArriendoFormGroup): IContratoArriendo | NewContratoArriendo {
    return this.convertContratoArriendoRawValueToContratoArriendo(form.getRawValue());
  }

  resetForm(form: ContratoArriendoFormGroup, contratoArriendo: ContratoArriendoFormGroupInput): void {
    const contratoArriendoRawValue = this.convertContratoArriendoToContratoArriendoRawValue({
      ...this.getFormDefaults(),
      ...contratoArriendo,
    });
    form.reset({
      ...contratoArriendoRawValue,
      id: { value: contratoArriendoRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ContratoArriendoFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      fechaFirma: currentTime,
    };
  }

  private convertContratoArriendoRawValueToContratoArriendo(
    rawContratoArriendo: ContratoArriendoFormRawValue | NewContratoArriendoFormRawValue,
  ): IContratoArriendo | NewContratoArriendo {
    return {
      ...rawContratoArriendo,
      fechaFirma: dayjs(rawContratoArriendo.fechaFirma, DATE_TIME_FORMAT),
    };
  }

  private convertContratoArriendoToContratoArriendoRawValue(
    contratoArriendo: IContratoArriendo | (Partial<NewContratoArriendo> & ContratoArriendoFormDefaults),
  ): ContratoArriendoFormRawValue | PartialWithRequiredKeyOf<NewContratoArriendoFormRawValue> {
    return {
      ...contratoArriendo,
      fechaFirma: contratoArriendo.fechaFirma ? contratoArriendo.fechaFirma.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
