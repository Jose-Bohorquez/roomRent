import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { TranslateModule } from '@ngx-translate/core';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IMultimediaInmueble } from '../multimedia-inmueble.model';
import { MultimediaInmuebleService } from '../service/multimedia-inmueble.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './multimedia-inmueble-delete-dialog.html',
  imports: [TranslateDirective, TranslateModule, FormsModule, FontAwesomeModule, AlertError],
})
export class MultimediaInmuebleDeleteDialog {
  multimediaInmueble?: IMultimediaInmueble;

  protected readonly multimediaInmuebleService = inject(MultimediaInmuebleService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.multimediaInmuebleService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
