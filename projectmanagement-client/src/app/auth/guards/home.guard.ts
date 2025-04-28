import {CanActivateFn, Router} from '@angular/router';
import {inject} from "@angular/core";
import {AuthService} from "../../services/auth.service";

export const homeGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const authService = inject(AuthService);

  const isAuthenticated = authService.isLoggedIn()
  if (isAuthenticated) {
    router.navigate(['/dashboard']);
    return false;
  }
  return true;
};
