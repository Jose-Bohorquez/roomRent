import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { AccountService } from 'app/core/auth/account.service';
import { LayoutService } from 'app/core/layout/layout.service';

@Component({
  selector: 'rr-sidebar',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss',
  imports: [RouterLink, RouterLinkActive, FontAwesomeModule],
})
export default class Sidebar {
  readonly account = inject(AccountService).account;
  readonly layout = inject(LayoutService);
  readonly isAdmin = computed(() => this.account()?.authorities?.includes('ROLE_ADMIN') ?? false);
  readonly userInitial = computed(() => (this.account()?.login ?? '?').charAt(0).toUpperCase());

  close(): void {
    this.layout.closeSidebar();
  }
}
