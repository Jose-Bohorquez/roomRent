import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ContratoArriendoResolve from './route/contrato-arriendo-routing-resolve.service';

const contratoArriendoRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/contrato-arriendo').then(m => m.ContratoArriendo),
    data: {},
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/contrato-arriendo-detail').then(m => m.ContratoArriendoDetail),
    resolve: {
      contratoArriendo: ContratoArriendoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/contrato-arriendo-update').then(m => m.ContratoArriendoUpdate),
    resolve: {
      contratoArriendo: ContratoArriendoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/contrato-arriendo-update').then(m => m.ContratoArriendoUpdate),
    resolve: {
      contratoArriendo: ContratoArriendoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default contratoArriendoRoute;
