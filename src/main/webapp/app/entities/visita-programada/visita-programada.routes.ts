import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import VisitaProgramadaResolve from './route/visita-programada-routing-resolve.service';

const visitaProgramadaRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/visita-programada').then(m => m.VisitaProgramada),
    data: { authorities: ['ROLE_ADMIN'] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/visita-programada-detail').then(m => m.VisitaProgramadaDetail),
    resolve: {
      visitaProgramada: VisitaProgramadaResolve,
    },
    data: { authorities: ['ROLE_ADMIN'] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/visita-programada-update').then(m => m.VisitaProgramadaUpdate),
    resolve: {
      visitaProgramada: VisitaProgramadaResolve,
    },
    data: { authorities: ['ROLE_ADMIN'] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/visita-programada-update').then(m => m.VisitaProgramadaUpdate),
    resolve: {
      visitaProgramada: VisitaProgramadaResolve,
    },
    data: { authorities: ['ROLE_ADMIN'] },
    canActivate: [UserRouteAccessService],
  },
];

export default visitaProgramadaRoute;
