import {CanActivateFn, Router} from '@angular/router';
import {inject} from "@angular/core";
import {ToastrService} from "ngx-toastr";
import {AuthService} from "../../services/auth.service";

export const adminGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const toastr = inject(ToastrService);
  const authService = inject(AuthService);

  const isAuthenticated = authService.isLoggedIn();
  const userRole = authService.getUserRole();
  if (isAuthenticated) {
    if (userRole != 'ADMIN')
    {
      toastr.error('You are unauthorized for this resource');
      router.navigate(['/']);
      return false;
    }else {
      return true;
    }
  }
  toastr.error('You are not logged in');
  router.navigate(['/login']);
  return false;
};
