import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISolicitudRoomie, NewSolicitudRoomie } from '../solicitud-roomie.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISolicitudRoomie for edit and NewSolicitudRoomieFormGroupInput for create.
 */
type SolicitudRoomieFormGroupInput = ISolicitudRoomie | PartialWithRequiredKeyOf<NewSolicitudRoomie>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ISolicitudRoomie | NewSolicitudRoomie> = Omit<T, 'fechaCreacion'> & {
  fechaCreacion?: string | null;
};

type SolicitudRoomieFormRawValue = FormValueOf<ISolicitudRoomie>;

type NewSolicitudRoomieFormRawValue = FormValueOf<NewSolicitudRoomie>;

type SolicitudRoomieFormDefaults = Pick<NewSolicitudRoomie, 'id' | 'fechaCreacion'>;

type SolicitudRoomieFormGroupContent = {
  id: FormControl<SolicitudRoomieFormRawValue['id'] | NewSolicitudRoomie['id']>;
  mensaje: FormControl<SolicitudRoomieFormRawValue['mensaje']>;
  referencias: FormControl<SolicitudRoomieFormRawValue['referencias']>;
  estado: FormControl<SolicitudRoomieFormRawValue['estado']>;
  fechaCreacion: FormControl<SolicitudRoomieFormRawValue['fechaCreacion']>;
  postulante: FormControl<SolicitudRoomieFormRawValue['postulante']>;
  publicacionRoomie: FormControl<SolicitudRoomieFormRawValue['publicacionRoomie']>;
};

export type SolicitudRoomieFormGroup = FormGroup<SolicitudRoomieFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SolicitudRoomieFormService {
  createSolicitudRoomieFormGroup(solicitudRoomie?: SolicitudRoomieFormGroupInput): SolicitudRoomieFormGroup {
    const solicitudRoomieRawValue = this.convertSolicitudRoomieToSolicitudRoomieRawValue({
      ...this.getFormDefaults(),
      ...(solicitudRoomie ?? { id: null }),
    });
    return new FormGroup<SolicitudRoomieFormGroupContent>({
      id: new FormControl(
        { value: solicitudRoomieRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      mensaje: new FormControl(solicitudRoomieRawValue.mensaje),
      referencias: new FormControl(solicitudRoomieRawValue.referencias),
      estado: new FormControl(solicitudRoomieRawValue.estado, {
        validators: [Validators.required],
      }),
      fechaCreacion: new FormControl(solicitudRoomieRawValue.fechaCreacion, {
        validators: [Validators.required],
      }),
      postulante: new FormControl(solicitudRoomieRawValue.postulante),
      publicacionRoomie: new FormControl(solicitudRoomieRawValue.publicacionRoomie),
    });
  }

  getSolicitudRoomie(form: SolicitudRoomieFormGroup): ISolicitudRoomie | NewSolicitudRoomie {
    return this.convertSolicitudRoomieRawValueToSolicitudRoomie(form.getRawValue());
  }

  resetForm(form: SolicitudRoomieFormGroup, solicitudRoomie: SolicitudRoomieFormGroupInput): void {
    const solicitudRoomieRawValue = this.convertSolicitudRoomieToSolicitudRoomieRawValue({ ...this.getFormDefaults(), ...solicitudRoomie });
    form.reset({
      ...solicitudRoomieRawValue,
      id: { value: solicitudRoomieRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): SolicitudRoomieFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      fechaCreacion: currentTime,
    };
  }

  private convertSolicitudRoomieRawValueToSolicitudRoomie(
    rawSolicitudRoomie: SolicitudRoomieFormRawValue | NewSolicitudRoomieFormRawValue,
  ): ISolicitudRoomie | NewSolicitudRoomie {
    return {
      ...rawSolicitudRoomie,
      fechaCreacion: dayjs(rawSolicitudRoomie.fechaCreacion, DATE_TIME_FORMAT),
    };
  }

  private convertSolicitudRoomieToSolicitudRoomieRawValue(
    solicitudRoomie: ISolicitudRoomie | (Partial<NewSolicitudRoomie> & SolicitudRoomieFormDefaults),
  ): SolicitudRoomieFormRawValue | PartialWithRequiredKeyOf<NewSolicitudRoomieFormRawValue> {
    return {
      ...solicitudRoomie,
      fechaCreacion: solicitudRoomie.fechaCreacion ? solicitudRoomie.fechaCreacion.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
