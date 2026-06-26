import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { ICalificacion } from '../calificacion.model';
import { CalificacionService } from '../service/calificacion.service';

const calificacionResolve = (route: ActivatedRouteSnapshot): Observable<null | ICalificacion> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(CalificacionService);
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

export default calificacionResolve;
