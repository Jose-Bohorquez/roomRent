import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { AccountService } from 'app/core/auth/account.service';

@Component({
  selector: 'jhi-dashboard',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
  imports: [CommonModule, RouterLink, FontAwesomeModule],
})
export default class Dashboard {
  readonly account = inject(AccountService).account;

  entities = [
    { path: '/perfil-usuario',      label: 'Perfiles de Usuario',    icon: 'user-circle' },
    { path: '/documento-usuario',   label: 'Documentos',             icon: 'file-alt' },
    { path: '/inmueble',            label: 'Inmuebles',              icon: 'home' },
    { path: '/publicacion-inmueble',label: 'Publicaciones Inmueble', icon: 'bullhorn' },
    { path: '/multimedia-inmueble', label: 'Multimedia',             icon: 'images' },
    { path: '/solicitud-arriendo',  label: 'Solicitudes Arriendo',   icon: 'envelope-open' },
    { path: '/visita-programada',   label: 'Visitas Programadas',    icon: 'calendar' },
    { path: '/contrato-arriendo',   label: 'Contratos',              icon: 'file-contract' },
    { path: '/publicacion-roomie',  label: 'Publicaciones Roomie',   icon: 'users' },
    { path: '/solicitud-roomie',    label: 'Solicitudes Roomie',     icon: 'handshake' },
    { path: '/calificacion',        label: 'Calificaciones',         icon: 'star' },
  ];
}
