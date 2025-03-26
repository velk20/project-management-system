import {CanActivateFn, Router} from '@angular/router';
import {inject} from "@angular/core";
import {ToastrService} from "ngx-toastr";
import {AuthService} from "../services/auth.service";

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const toastr = inject(ToastrService);
  const authService = inject(AuthService);

  const isAuthenticated = authService.isLoggedIn()
  if (!isAuthenticated) {
    toastr.error('You are not logged in');
    router.navigate(['/login']);
  }
  return isAuthenticated;
};
