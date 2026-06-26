import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IDocumentoUsuario, NewDocumentoUsuario } from '../documento-usuario.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDocumentoUsuario for edit and NewDocumentoUsuarioFormGroupInput for create.
 */
type DocumentoUsuarioFormGroupInput = IDocumentoUsuario | PartialWithRequiredKeyOf<NewDocumentoUsuario>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IDocumentoUsuario | NewDocumentoUsuario> = Omit<T, 'fechaCarga'> & {
  fechaCarga?: string | null;
};

type DocumentoUsuarioFormRawValue = FormValueOf<IDocumentoUsuario>;

type NewDocumentoUsuarioFormRawValue = FormValueOf<NewDocumentoUsuario>;

type DocumentoUsuarioFormDefaults = Pick<NewDocumentoUsuario, 'id' | 'fechaCarga' | 'aprobado'>;

type DocumentoUsuarioFormGroupContent = {
  id: FormControl<DocumentoUsuarioFormRawValue['id'] | NewDocumentoUsuario['id']>;
  tipoDocumento: FormControl<DocumentoUsuarioFormRawValue['tipoDocumento']>;
  nombreDocumento: FormControl<DocumentoUsuarioFormRawValue['nombreDocumento']>;
  urlArchivo: FormControl<DocumentoUsuarioFormRawValue['urlArchivo']>;
  tipoMime: FormControl<DocumentoUsuarioFormRawValue['tipoMime']>;
  tamanoArchivo: FormControl<DocumentoUsuarioFormRawValue['tamanoArchivo']>;
  fechaCarga: FormControl<DocumentoUsuarioFormRawValue['fechaCarga']>;
  aprobado: FormControl<DocumentoUsuarioFormRawValue['aprobado']>;
  observaciones: FormControl<DocumentoUsuarioFormRawValue['observaciones']>;
  perfilUsuario: FormControl<DocumentoUsuarioFormRawValue['perfilUsuario']>;
};

export type DocumentoUsuarioFormGroup = FormGroup<DocumentoUsuarioFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DocumentoUsuarioFormService {
  createDocumentoUsuarioFormGroup(documentoUsuario?: DocumentoUsuarioFormGroupInput): DocumentoUsuarioFormGroup {
    const documentoUsuarioRawValue = this.convertDocumentoUsuarioToDocumentoUsuarioRawValue({
      ...this.getFormDefaults(),
      ...(documentoUsuario ?? { id: null }),
    });
    return new FormGroup<DocumentoUsuarioFormGroupContent>({
      id: new FormControl(
        { value: documentoUsuarioRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      tipoDocumento: new FormControl(documentoUsuarioRawValue.tipoDocumento, {
        validators: [Validators.required],
      }),
      nombreDocumento: new FormControl(documentoUsuarioRawValue.nombreDocumento, {
        validators: [Validators.required],
      }),
      urlArchivo: new FormControl(documentoUsuarioRawValue.urlArchivo, {
        validators: [Validators.required],
      }),
      tipoMime: new FormControl(documentoUsuarioRawValue.tipoMime),
      tamanoArchivo: new FormControl(documentoUsuarioRawValue.tamanoArchivo),
      fechaCarga: new FormControl(documentoUsuarioRawValue.fechaCarga, {
        validators: [Validators.required],
      }),
      aprobado: new FormControl(documentoUsuarioRawValue.aprobado),
      observaciones: new FormControl(documentoUsuarioRawValue.observaciones),
      perfilUsuario: new FormControl(documentoUsuarioRawValue.perfilUsuario),
    });
  }

  getDocumentoUsuario(form: DocumentoUsuarioFormGroup): IDocumentoUsuario | NewDocumentoUsuario {
    return this.convertDocumentoUsuarioRawValueToDocumentoUsuario(form.getRawValue());
  }

  resetForm(form: DocumentoUsuarioFormGroup, documentoUsuario: DocumentoUsuarioFormGroupInput): void {
    const documentoUsuarioRawValue = this.convertDocumentoUsuarioToDocumentoUsuarioRawValue({
      ...this.getFormDefaults(),
      ...documentoUsuario,
    });
    form.reset({
      ...documentoUsuarioRawValue,
      id: { value: documentoUsuarioRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): DocumentoUsuarioFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      fechaCarga: currentTime,
      aprobado: false,
    };
  }

  private convertDocumentoUsuarioRawValueToDocumentoUsuario(
    rawDocumentoUsuario: DocumentoUsuarioFormRawValue | NewDocumentoUsuarioFormRawValue,
  ): IDocumentoUsuario | NewDocumentoUsuario {
    return {
      ...rawDocumentoUsuario,
      fechaCarga: dayjs(rawDocumentoUsuario.fechaCarga, DATE_TIME_FORMAT),
    };
  }

  private convertDocumentoUsuarioToDocumentoUsuarioRawValue(
    documentoUsuario: IDocumentoUsuario | (Partial<NewDocumentoUsuario> & DocumentoUsuarioFormDefaults),
  ): DocumentoUsuarioFormRawValue | PartialWithRequiredKeyOf<NewDocumentoUsuarioFormRawValue> {
    return {
      ...documentoUsuario,
      fechaCarga: documentoUsuario.fechaCarga ? documentoUsuario.fechaCarga.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
