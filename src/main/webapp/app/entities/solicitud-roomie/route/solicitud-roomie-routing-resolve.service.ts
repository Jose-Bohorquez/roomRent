import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { SolicitudRoomieService } from '../service/solicitud-roomie.service';
import { ISolicitudRoomie } from '../solicitud-roomie.model';

const solicitudRoomieResolve = (route: ActivatedRouteSnapshot): Observable<null | ISolicitudRoomie> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(SolicitudRoomieService);
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

export default solicitudRoomieResolve;
