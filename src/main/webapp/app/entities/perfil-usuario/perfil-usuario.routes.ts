import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import PerfilUsuarioResolve from './route/perfil-usuario-routing-resolve.service';

const perfilUsuarioRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/perfil-usuario').then(m => m.PerfilUsuario),
    data: {},
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/perfil-usuario-detail').then(m => m.PerfilUsuarioDetail),
    resolve: {
      perfilUsuario: PerfilUsuarioResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/perfil-usuario-update').then(m => m.PerfilUsuarioUpdate),
    resolve: {
      perfilUsuario: PerfilUsuarioResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/perfil-usuario-update').then(m => m.PerfilUsuarioUpdate),
    resolve: {
      perfilUsuario: PerfilUsuarioResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default perfilUsuarioRoute;
