import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { AccountService } from 'app/core/auth/account.service';
import { TranslateDirective } from 'app/shared/language';

@Component({
  selector: 'jhi-dashboard',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
  imports: [CommonModule, RouterLink, FontAwesomeModule, TranslateDirective],
})
export default class Dashboard {
  readonly account = inject(AccountService).account;
  private readonly router = inject(Router);

  entities = [
    { path: '/perfil-usuario', label: 'Mi Perfil', icon: 'user-circle', color: 'primary' },
    { path: '/documento-usuario', label: 'Mis Documentos', icon: 'file-pdf', color: 'info' },
    { path: '/inmueble', label: 'Mis Inmuebles', icon: 'home', color: 'success' },
    { path: '/publicacion-inmueble', label: 'Publicaciones', icon: 'bullhorn', color: 'warning' },
    { path: '/multimedia-inmueble', label: 'Multimedia', icon: 'images', color: 'secondary' },
    { path: '/solicitud-arriendo', label: 'Solicitudes', icon: 'envelope-open', color: 'danger' },
    { path: '/visita-programada', label: 'Visitas', icon: 'calendar', color: 'primary' },
    { path: '/contrato-arriendo', label: 'Contratos', icon: 'file-contract', color: 'info' },
    { path: '/publicacion-roomie', label: 'Buscar Roomies', icon: 'users', color: 'success' },
    { path: '/solicitud-roomie', label: 'Solicitudes Roomie', icon: 'handshake', color: 'warning' },
    { path: '/calificacion', label: 'Calificaciones', icon: 'star', color: 'secondary' },
  ];

  navigate(path: string): void {
    this.router.navigate([path]);
  }
}
