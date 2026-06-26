import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IPublicacionRoomie } from '../publicacion-roomie.model';
import { PublicacionRoomieService } from '../service/publicacion-roomie.service';

const publicacionRoomieResolve = (route: ActivatedRouteSnapshot): Observable<null | IPublicacionRoomie> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(PublicacionRoomieService);
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

export default publicacionRoomieResolve;
