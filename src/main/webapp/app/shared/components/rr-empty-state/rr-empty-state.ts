import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'rr-empty-state',
  templateUrl: './rr-empty-state.html',
  styleUrl: './rr-empty-state.scss',
  imports: [RouterLink, FontAwesomeModule],
})
export class RrEmptyState {
  readonly entityLabel = input<string>('registros');
  readonly createRoute = input<string[]>([]);
  readonly createLabel = input<string>('Crear nuevo');
  readonly searchActive = input<boolean>(false);
}
