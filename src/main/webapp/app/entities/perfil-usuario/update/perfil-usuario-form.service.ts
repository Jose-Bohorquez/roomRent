import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPerfilUsuario, NewPerfilUsuario } from '../perfil-usuario.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPerfilUsuario for edit and NewPerfilUsuarioFormGroupInput for create.
 */
type PerfilUsuarioFormGroupInput = IPerfilUsuario | PartialWithRequiredKeyOf<NewPerfilUsuario>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPerfilUsuario | NewPerfilUsuario> = Omit<T, 'fechaCreacion'> & {
  fechaCreacion?: string | null;
};

type PerfilUsuarioFormRawValue = FormValueOf<IPerfilUsuario>;

type NewPerfilUsuarioFormRawValue = FormValueOf<NewPerfilUsuario>;

type PerfilUsuarioFormDefaults = Pick<
  NewPerfilUsuario,
  'id' | 'tieneMascotas' | 'fumador' | 'verificado' | 'habilitadoRoomie' | 'fechaCreacion'
>;

type PerfilUsuarioFormGroupContent = {
  id: FormControl<PerfilUsuarioFormRawValue['id'] | NewPerfilUsuario['id']>;
  tipoDocumento: FormControl<PerfilUsuarioFormRawValue['tipoDocumento']>;
  numeroDocumento: FormControl<PerfilUsuarioFormRawValue['numeroDocumento']>;
  primerNombre: FormControl<PerfilUsuarioFormRawValue['primerNombre']>;
  segundoNombre: FormControl<PerfilUsuarioFormRawValue['segundoNombre']>;
  primerApellido: FormControl<PerfilUsuarioFormRawValue['primerApellido']>;
  segundoApellido: FormControl<PerfilUsuarioFormRawValue['segundoApellido']>;
  fechaNacimiento: FormControl<PerfilUsuarioFormRawValue['fechaNacimiento']>;
  genero: FormControl<PerfilUsuarioFormRawValue['genero']>;
  telefono: FormControl<PerfilUsuarioFormRawValue['telefono']>;
  direccionActual: FormControl<PerfilUsuarioFormRawValue['direccionActual']>;
  ciudad: FormControl<PerfilUsuarioFormRawValue['ciudad']>;
  barrio: FormControl<PerfilUsuarioFormRawValue['barrio']>;
  profesion: FormControl<PerfilUsuarioFormRawValue['profesion']>;
  ocupacion: FormControl<PerfilUsuarioFormRawValue['ocupacion']>;
  empresaTrabajo: FormControl<PerfilUsuarioFormRawValue['empresaTrabajo']>;
  universidad: FormControl<PerfilUsuarioFormRawValue['universidad']>;
  biografia: FormControl<PerfilUsuarioFormRawValue['biografia']>;
  intereses: FormControl<PerfilUsuarioFormRawValue['intereses']>;
  tieneMascotas: FormControl<PerfilUsuarioFormRawValue['tieneMascotas']>;
  fumador: FormControl<PerfilUsuarioFormRawValue['fumador']>;
  verificado: FormControl<PerfilUsuarioFormRawValue['verificado']>;
  habilitadoRoomie: FormControl<PerfilUsuarioFormRawValue['habilitadoRoomie']>;
  estado: FormControl<PerfilUsuarioFormRawValue['estado']>;
  fechaCreacion: FormControl<PerfilUsuarioFormRawValue['fechaCreacion']>;
  usuario: FormControl<PerfilUsuarioFormRawValue['usuario']>;
};

export type PerfilUsuarioFormGroup = FormGroup<PerfilUsuarioFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PerfilUsuarioFormService {
  createPerfilUsuarioFormGroup(perfilUsuario?: PerfilUsuarioFormGroupInput): PerfilUsuarioFormGroup {
    const perfilUsuarioRawValue = this.convertPerfilUsuarioToPerfilUsuarioRawValue({
      ...this.getFormDefaults(),
      ...(perfilUsuario ?? { id: null }),
    });
    return new FormGroup<PerfilUsuarioFormGroupContent>({
      id: new FormControl(
        { value: perfilUsuarioRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      tipoDocumento: new FormControl(perfilUsuarioRawValue.tipoDocumento, {
        validators: [Validators.required],
      }),
      numeroDocumento: new FormControl(perfilUsuarioRawValue.numeroDocumento, {
        validators: [Validators.required],
      }),
      primerNombre: new FormControl(perfilUsuarioRawValue.primerNombre, {
        validators: [Validators.required],
      }),
      segundoNombre: new FormControl(perfilUsuarioRawValue.segundoNombre),
      primerApellido: new FormControl(perfilUsuarioRawValue.primerApellido, {
        validators: [Validators.required],
      }),
      segundoApellido: new FormControl(perfilUsuarioRawValue.segundoApellido),
      fechaNacimiento: new FormControl(perfilUsuarioRawValue.fechaNacimiento, {
        validators: [Validators.required],
      }),
      genero: new FormControl(perfilUsuarioRawValue.genero),
      telefono: new FormControl(perfilUsuarioRawValue.telefono, {
        validators: [Validators.required],
      }),
      direccionActual: new FormControl(perfilUsuarioRawValue.direccionActual),
      ciudad: new FormControl(perfilUsuarioRawValue.ciudad, {
        validators: [Validators.required],
      }),
      barrio: new FormControl(perfilUsuarioRawValue.barrio),
      profesion: new FormControl(perfilUsuarioRawValue.profesion),
      ocupacion: new FormControl(perfilUsuarioRawValue.ocupacion),
      empresaTrabajo: new FormControl(perfilUsuarioRawValue.empresaTrabajo),
      universidad: new FormControl(perfilUsuarioRawValue.universidad),
      biografia: new FormControl(perfilUsuarioRawValue.biografia),
      intereses: new FormControl(perfilUsuarioRawValue.intereses),
      tieneMascotas: new FormControl(perfilUsuarioRawValue.tieneMascotas),
      fumador: new FormControl(perfilUsuarioRawValue.fumador),
      verificado: new FormControl(perfilUsuarioRawValue.verificado, {
        validators: [Validators.required],
      }),
      habilitadoRoomie: new FormControl(perfilUsuarioRawValue.habilitadoRoomie, {
        validators: [Validators.required],
      }),
      estado: new FormControl(perfilUsuarioRawValue.estado, {
        validators: [Validators.required],
      }),
      fechaCreacion: new FormControl(perfilUsuarioRawValue.fechaCreacion, {
        validators: [Validators.required],
      }),
      usuario: new FormControl(perfilUsuarioRawValue.usuario),
    });
  }

  getPerfilUsuario(form: PerfilUsuarioFormGroup): IPerfilUsuario | NewPerfilUsuario {
    return this.convertPerfilUsuarioRawValueToPerfilUsuario(form.getRawValue());
  }

  resetForm(form: PerfilUsuarioFormGroup, perfilUsuario: PerfilUsuarioFormGroupInput): void {
    const perfilUsuarioRawValue = this.convertPerfilUsuarioToPerfilUsuarioRawValue({ ...this.getFormDefaults(), ...perfilUsuario });
    form.reset({
      ...perfilUsuarioRawValue,
      id: { value: perfilUsuarioRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): PerfilUsuarioFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      tieneMascotas: false,
      fumador: false,
      verificado: false,
      habilitadoRoomie: false,
      fechaCreacion: currentTime,
    };
  }

  private convertPerfilUsuarioRawValueToPerfilUsuario(
    rawPerfilUsuario: PerfilUsuarioFormRawValue | NewPerfilUsuarioFormRawValue,
  ): IPerfilUsuario | NewPerfilUsuario {
    return {
      ...rawPerfilUsuario,
      fechaCreacion: dayjs(rawPerfilUsuario.fechaCreacion, DATE_TIME_FORMAT),
    };
  }

  private convertPerfilUsuarioToPerfilUsuarioRawValue(
    perfilUsuario: IPerfilUsuario | (Partial<NewPerfilUsuario> & PerfilUsuarioFormDefaults),
  ): PerfilUsuarioFormRawValue | PartialWithRequiredKeyOf<NewPerfilUsuarioFormRawValue> {
    return {
      ...perfilUsuario,
      fechaCreacion: perfilUsuario.fechaCreacion ? perfilUsuario.fechaCreacion.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
