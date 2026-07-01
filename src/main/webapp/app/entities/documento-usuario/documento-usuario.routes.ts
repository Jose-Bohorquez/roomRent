import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import DocumentoUsuarioResolve from './route/documento-usuario-routing-resolve.service';

const documentoUsuarioRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/documento-usuario').then(m => m.DocumentoUsuario),
    data: { authorities: ['ROLE_ADMIN'] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/documento-usuario-detail').then(m => m.DocumentoUsuarioDetail),
    resolve: {
      documentoUsuario: DocumentoUsuarioResolve,
    },
    data: { authorities: ['ROLE_ADMIN'] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/documento-usuario-update').then(m => m.DocumentoUsuarioUpdate),
    resolve: {
      documentoUsuario: DocumentoUsuarioResolve,
    },
    data: { authorities: ['ROLE_ADMIN'] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/documento-usuario-update').then(m => m.DocumentoUsuarioUpdate),
    resolve: {
      documentoUsuario: DocumentoUsuarioResolve,
    },
    data: { authorities: ['ROLE_ADMIN'] },
    canActivate: [UserRouteAccessService],
  },
];

export default documentoUsuarioRoute;
