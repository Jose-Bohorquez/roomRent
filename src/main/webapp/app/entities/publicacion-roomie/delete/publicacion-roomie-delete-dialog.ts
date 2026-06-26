import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { TranslateModule } from '@ngx-translate/core';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IPublicacionRoomie } from '../publicacion-roomie.model';
import { PublicacionRoomieService } from '../service/publicacion-roomie.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './publicacion-roomie-delete-dialog.html',
  imports: [TranslateDirective, TranslateModule, FormsModule, FontAwesomeModule, AlertError],
})
export class PublicacionRoomieDeleteDialog {
  publicacionRoomie?: IPublicacionRoomie;

  protected readonly publicacionRoomieService = inject(PublicacionRoomieService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.publicacionRoomieService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
