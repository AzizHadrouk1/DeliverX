import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import Keycloak from 'keycloak-js';

export const adminGuestGuard: CanActivateFn = () => {
  const keycloak = inject(Keycloak);
  const router = inject(Router);

  const clientRoles: string[] = keycloak.tokenParsed?.resource_access?.['driver-client-service']?.roles ?? [];
  if (keycloak.authenticated && clientRoles.includes('admin')) {
    return router.createUrlTree(['/dashboard']);
  }

  return true;
};
