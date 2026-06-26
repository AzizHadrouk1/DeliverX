import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService, UserRole } from 'shared';

export const roleGuard = (role: UserRole): CanActivateFn => {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);

    if (auth.isAuthenticated(role)) {
      return true;
    }

    return router.createUrlTree(['/login']);
  };
};
