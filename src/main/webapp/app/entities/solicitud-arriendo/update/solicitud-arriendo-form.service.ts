import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISolicitudArriendo, NewSolicitudArriendo } from '../solicitud-arriendo.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISolicitudArriendo for edit and NewSolicitudArriendoFormGroupInput for create.
 */
type SolicitudArriendoFormGroupInput = ISolicitudArriendo | PartialWithRequiredKeyOf<NewSolicitudArriendo>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ISolicitudArriendo | NewSolicitudArriendo> = Omit<T, 'fechaCreacion'> & {
  fechaCreacion?: string | null;
};

type SolicitudArriendoFormRawValue = FormValueOf<ISolicitudArriendo>;

type NewSolicitudArriendoFormRawValue = FormValueOf<NewSolicitudArriendo>;

type SolicitudArriendoFormDefaults = Pick<NewSolicitudArriendo, 'id' | 'aceptaTerminos' | 'fechaCreacion'>;

type SolicitudArriendoFormGroupContent = {
  id: FormControl<SolicitudArriendoFormRawValue['id'] | NewSolicitudArriendo['id']>;
  mensaje: FormControl<SolicitudArriendoFormRawValue['mensaje']>;
  aceptaTerminos: FormControl<SolicitudArriendoFormRawValue['aceptaTerminos']>;
  estado: FormControl<SolicitudArriendoFormRawValue['estado']>;
  fechaCreacion: FormControl<SolicitudArriendoFormRawValue['fechaCreacion']>;
  arrendatario: FormControl<SolicitudArriendoFormRawValue['arrendatario']>;
  publicacion: FormControl<SolicitudArriendoFormRawValue['publicacion']>;
};

export type SolicitudArriendoFormGroup = FormGroup<SolicitudArriendoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SolicitudArriendoFormService {
  createSolicitudArriendoFormGroup(solicitudArriendo?: SolicitudArriendoFormGroupInput): SolicitudArriendoFormGroup {
    const solicitudArriendoRawValue = this.convertSolicitudArriendoToSolicitudArriendoRawValue({
      ...this.getFormDefaults(),
      ...(solicitudArriendo ?? { id: null }),
    });
    return new FormGroup<SolicitudArriendoFormGroupContent>({
      id: new FormControl(
        { value: solicitudArriendoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      mensaje: new FormControl(solicitudArriendoRawValue.mensaje),
      aceptaTerminos: new FormControl(solicitudArriendoRawValue.aceptaTerminos, {
        validators: [Validators.required],
      }),
      estado: new FormControl(solicitudArriendoRawValue.estado, {
        validators: [Validators.required],
      }),
      fechaCreacion: new FormControl(solicitudArriendoRawValue.fechaCreacion, {
        validators: [Validators.required],
      }),
      arrendatario: new FormControl(solicitudArriendoRawValue.arrendatario),
      publicacion: new FormControl(solicitudArriendoRawValue.publicacion),
    });
  }

  getSolicitudArriendo(form: SolicitudArriendoFormGroup): ISolicitudArriendo | NewSolicitudArriendo {
    return this.convertSolicitudArriendoRawValueToSolicitudArriendo(form.getRawValue());
  }

  resetForm(form: SolicitudArriendoFormGroup, solicitudArriendo: SolicitudArriendoFormGroupInput): void {
    const solicitudArriendoRawValue = this.convertSolicitudArriendoToSolicitudArriendoRawValue({
      ...this.getFormDefaults(),
      ...solicitudArriendo,
    });
    form.reset({
      ...solicitudArriendoRawValue,
      id: { value: solicitudArriendoRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): SolicitudArriendoFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      aceptaTerminos: false,
      fechaCreacion: currentTime,
    };
  }

  private convertSolicitudArriendoRawValueToSolicitudArriendo(
    rawSolicitudArriendo: SolicitudArriendoFormRawValue | NewSolicitudArriendoFormRawValue,
  ): ISolicitudArriendo | NewSolicitudArriendo {
    return {
      ...rawSolicitudArriendo,
      fechaCreacion: dayjs(rawSolicitudArriendo.fechaCreacion, DATE_TIME_FORMAT),
    };
  }

  private convertSolicitudArriendoToSolicitudArriendoRawValue(
    solicitudArriendo: ISolicitudArriendo | (Partial<NewSolicitudArriendo> & SolicitudArriendoFormDefaults),
  ): SolicitudArriendoFormRawValue | PartialWithRequiredKeyOf<NewSolicitudArriendoFormRawValue> {
    return {
      ...solicitudArriendo,
      fechaCreacion: solicitudArriendo.fechaCreacion ? solicitudArriendo.fechaCreacion.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
