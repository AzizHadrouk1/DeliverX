import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import Keycloak from 'keycloak-js';

export const clientGuestGuard: CanActivateFn = () => {
  const keycloak = inject(Keycloak);
  const router = inject(Router);

  const roles: string[] = keycloak.tokenParsed?.resource_access?.['driver-client-service']?.roles ?? [];
  if (keycloak.authenticated && roles.includes('user')) {
    return router.createUrlTree(['/track']);
  }

  return true;
};
