import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { catchError } from 'rxjs/operators';
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";

@Injectable({ providedIn: 'root' })
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService,
              private router: Router,
              private toastr: ToastrService) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = localStorage.getItem('token');
    if (token) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      });
    }

    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        console.log(error);
        if (error.status == 500 && error.error.message.includes('JWT')) {
          this.authService.logout();
          this.router.navigate(['/']);

        }
        if (error.status == 401) {
          console.error('Unauthorized request, logging out...');
          this.authService.logout();
          this.router.navigate(['/login']);
        }
        if (error.error.details){
          this.toastr.error(error.error.details);
        }

        return throwError(() => error);
      })
    );
  }
}
