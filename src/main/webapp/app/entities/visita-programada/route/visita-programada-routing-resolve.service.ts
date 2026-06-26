import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { VisitaProgramadaService } from '../service/visita-programada.service';
import { IVisitaProgramada } from '../visita-programada.model';

const visitaProgramadaResolve = (route: ActivatedRouteSnapshot): Observable<null | IVisitaProgramada> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(VisitaProgramadaService);
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

export default visitaProgramadaResolve;
