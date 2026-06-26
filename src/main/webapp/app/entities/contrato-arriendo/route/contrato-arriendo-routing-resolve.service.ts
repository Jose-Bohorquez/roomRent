import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IContratoArriendo } from '../contrato-arriendo.model';
import { ContratoArriendoService } from '../service/contrato-arriendo.service';

const contratoArriendoResolve = (route: ActivatedRouteSnapshot): Observable<null | IContratoArriendo> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(ContratoArriendoService);
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

export default contratoArriendoResolve;
