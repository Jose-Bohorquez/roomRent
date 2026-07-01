import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import CalificacionResolve from './route/calificacion-routing-resolve.service';

const calificacionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/calificacion').then(m => m.Calificacion),
    data: { authorities: ['ROLE_ADMIN'] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/calificacion-detail').then(m => m.CalificacionDetail),
    resolve: {
      calificacion: CalificacionResolve,
    },
    data: { authorities: ['ROLE_ADMIN'] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/calificacion-update').then(m => m.CalificacionUpdate),
    resolve: {
      calificacion: CalificacionResolve,
    },
    data: { authorities: ['ROLE_ADMIN'] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/calificacion-update').then(m => m.CalificacionUpdate),
    resolve: {
      calificacion: CalificacionResolve,
    },
    data: { authorities: ['ROLE_ADMIN'] },
    canActivate: [UserRouteAccessService],
  },
];

export default calificacionRoute;
