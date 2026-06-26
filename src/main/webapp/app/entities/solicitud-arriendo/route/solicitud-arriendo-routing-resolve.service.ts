import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { SolicitudArriendoService } from '../service/solicitud-arriendo.service';
import { ISolicitudArriendo } from '../solicitud-arriendo.model';

const solicitudArriendoResolve = (route: ActivatedRouteSnapshot): Observable<null | ISolicitudArriendo> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(SolicitudArriendoService);
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

export default solicitudArriendoResolve;
