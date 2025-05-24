import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthenticationService } from '../service/authentication.service';

export const adminAuthGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthenticationService);
  const router = inject(Router);

  // The authority 'user:create' is used as an example for admin access.
  // This aligns with the backend @PreAuthorize("hasAuthority('user:create')")
  // on the AdminController.
  if (authService.isUserLoggedIn() && authService.hasAuthority('user:create')) {
    return true;
  } else {
    // Redirect to login page if not logged in or not an admin
    router.navigate(['/login']); 
    return false;
  }
};
