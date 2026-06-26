import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import SolicitudArriendoResolve from './route/solicitud-arriendo-routing-resolve.service';

const solicitudArriendoRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/solicitud-arriendo').then(m => m.SolicitudArriendo),
    data: {},
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/solicitud-arriendo-detail').then(m => m.SolicitudArriendoDetail),
    resolve: {
      solicitudArriendo: SolicitudArriendoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/solicitud-arriendo-update').then(m => m.SolicitudArriendoUpdate),
    resolve: {
      solicitudArriendo: SolicitudArriendoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/solicitud-arriendo-update').then(m => m.SolicitudArriendoUpdate),
    resolve: {
      solicitudArriendo: SolicitudArriendoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default solicitudArriendoRoute;
