import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { IContratoArriendo } from '../contrato-arriendo.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-contrato-arriendo-detail',
  templateUrl: './contrato-arriendo-detail.html',
  imports: [
    FontAwesomeModule,
    Alert,
    AlertError,
    TranslateDirective,
    TranslateModule,
    RouterLink,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
  ],
})
export class ContratoArriendoDetail {
  readonly contratoArriendo = input<IContratoArriendo | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
