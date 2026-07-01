import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import PublicacionInmuebleResolve from './route/publicacion-inmueble-routing-resolve.service';

const publicacionInmuebleRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/publicacion-inmueble').then(m => m.PublicacionInmueble),
    data: { authorities: ['ROLE_ADMIN'] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/publicacion-inmueble-detail').then(m => m.PublicacionInmuebleDetail),
    resolve: {
      publicacionInmueble: PublicacionInmuebleResolve,
    },
    data: { authorities: ['ROLE_ADMIN'] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/publicacion-inmueble-update').then(m => m.PublicacionInmuebleUpdate),
    resolve: {
      publicacionInmueble: PublicacionInmuebleResolve,
    },
    data: { authorities: ['ROLE_ADMIN'] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/publicacion-inmueble-update').then(m => m.PublicacionInmuebleUpdate),
    resolve: {
      publicacionInmueble: PublicacionInmuebleResolve,
    },
    data: { authorities: ['ROLE_ADMIN'] },
    canActivate: [UserRouteAccessService],
  },
];

export default publicacionInmuebleRoute;
