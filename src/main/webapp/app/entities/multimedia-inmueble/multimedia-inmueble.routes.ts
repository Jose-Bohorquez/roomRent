import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import MultimediaInmuebleResolve from './route/multimedia-inmueble-routing-resolve.service';

const multimediaInmuebleRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/multimedia-inmueble').then(m => m.MultimediaInmueble),
    data: { authorities: ['ROLE_ADMIN'] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/multimedia-inmueble-detail').then(m => m.MultimediaInmuebleDetail),
    resolve: {
      multimediaInmueble: MultimediaInmuebleResolve,
    },
    data: { authorities: ['ROLE_ADMIN'] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/multimedia-inmueble-update').then(m => m.MultimediaInmuebleUpdate),
    resolve: {
      multimediaInmueble: MultimediaInmuebleResolve,
    },
    data: { authorities: ['ROLE_ADMIN'] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/multimedia-inmueble-update').then(m => m.MultimediaInmuebleUpdate),
    resolve: {
      multimediaInmueble: MultimediaInmuebleResolve,
    },
    data: { authorities: ['ROLE_ADMIN'] },
    canActivate: [UserRouteAccessService],
  },
];

export default multimediaInmuebleRoute;
