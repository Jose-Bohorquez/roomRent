import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'rr-table-toolbar',
  templateUrl: './rr-table-toolbar.html',
  styleUrl: './rr-table-toolbar.scss',
  imports: [RouterLink, FontAwesomeModule],
})
export class RrTableToolbar {
  readonly title = input.required<string>();
  readonly description = input<string>('');
  readonly createRoute = input<string[]>([]);
  readonly createLabel = input<string>('Crear');
  readonly isLoading = input<boolean>(false);
  readonly totalCount = input<number>(0);
  readonly searchPlaceholder = input<string>('Buscar...');

  readonly searchChange = output<string>();
  readonly refresh = output<void>();

  onSearch(event: Event): void {
    this.searchChange.emit((event.target as HTMLInputElement).value);
  }

  onRefresh(): void {
    this.refresh.emit();
  }
}
