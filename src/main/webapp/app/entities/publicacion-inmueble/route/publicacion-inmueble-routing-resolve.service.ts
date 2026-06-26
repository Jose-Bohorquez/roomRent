import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IPublicacionInmueble } from '../publicacion-inmueble.model';
import { PublicacionInmuebleService } from '../service/publicacion-inmueble.service';

const publicacionInmuebleResolve = (route: ActivatedRouteSnapshot): Observable<null | IPublicacionInmueble> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(PublicacionInmuebleService);
    return service.find(id).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404) {
          router.navigate(['404']);
        } else {
          router.navigate(['error']);
        }
        return EMPTY;
      }),
    );
  }

  return of(null);
};

export default publicacionInmuebleResolve;
