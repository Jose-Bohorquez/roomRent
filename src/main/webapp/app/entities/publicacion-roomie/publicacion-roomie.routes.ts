import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import PublicacionRoomieResolve from './route/publicacion-roomie-routing-resolve.service';

const publicacionRoomieRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/publicacion-roomie').then(m => m.PublicacionRoomie),
    data: { authorities: ['ROLE_ADMIN'] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/publicacion-roomie-detail').then(m => m.PublicacionRoomieDetail),
    resolve: {
      publicacionRoomie: PublicacionRoomieResolve,
    },
    data: { authorities: ['ROLE_ADMIN'] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/publicacion-roomie-update').then(m => m.PublicacionRoomieUpdate),
    resolve: {
      publicacionRoomie: PublicacionRoomieResolve,
    },
    data: { authorities: ['ROLE_ADMIN'] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/publicacion-roomie-update').then(m => m.PublicacionRoomieUpdate),
    resolve: {
      publicacionRoomie: PublicacionRoomieResolve,
    },
    data: { authorities: ['ROLE_ADMIN'] },
    canActivate: [UserRouteAccessService],
  },
];

export default publicacionRoomieRoute;
