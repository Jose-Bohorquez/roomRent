import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IMultimediaInmueble } from '../multimedia-inmueble.model';
import { MultimediaInmuebleService } from '../service/multimedia-inmueble.service';

const multimediaInmuebleResolve = (route: ActivatedRouteSnapshot): Observable<null | IMultimediaInmueble> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(MultimediaInmuebleService);
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

export default multimediaInmuebleResolve;
