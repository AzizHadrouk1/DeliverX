import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import Keycloak from 'keycloak-js';

export const roleGuard = (role: string): CanActivateFn => {
  return () => {
    const keycloak = inject(Keycloak);
    const router = inject(Router);

    const clientRoles: string[] = keycloak.tokenParsed?.resource_access?.['driver-client-service']?.roles ?? [];
    if (keycloak.authenticated && clientRoles.includes(role.toLowerCase())) {
      return true;
    }

    return router.createUrlTree(['/login']);
  };
};
