import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import SolicitudRoomieResolve from './route/solicitud-roomie-routing-resolve.service';

const solicitudRoomieRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/solicitud-roomie').then(m => m.SolicitudRoomie),
    data: {},
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/solicitud-roomie-detail').then(m => m.SolicitudRoomieDetail),
    resolve: {
      solicitudRoomie: SolicitudRoomieResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/solicitud-roomie-update').then(m => m.SolicitudRoomieUpdate),
    resolve: {
      solicitudRoomie: SolicitudRoomieResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/solicitud-roomie-update').then(m => m.SolicitudRoomieUpdate),
    resolve: {
      solicitudRoomie: SolicitudRoomieResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default solicitudRoomieRoute;
