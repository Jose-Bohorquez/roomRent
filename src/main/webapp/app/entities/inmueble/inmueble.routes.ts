import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import InmuebleResolve from './route/inmueble-routing-resolve.service';

const inmuebleRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/inmueble').then(m => m.Inmueble),
    data: {},
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/inmueble-detail').then(m => m.InmuebleDetail),
    resolve: {
      inmueble: InmuebleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/inmueble-update').then(m => m.InmuebleUpdate),
    resolve: {
      inmueble: InmuebleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/inmueble-update').then(m => m.InmuebleUpdate),
    resolve: {
      inmueble: InmuebleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default inmuebleRoute;
