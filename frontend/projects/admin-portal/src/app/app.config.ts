import { ApplicationConfig, inject, provideAppInitializer, provideZoneChangeDetection } from '@angular/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import {
  provideKeycloak,
  createInterceptorCondition,
  IncludeBearerTokenCondition,
  INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
  includeBearerTokenInterceptor
} from 'keycloak-angular';
import { routes } from './app.routes';
import { KeycloakSessionService } from './core/services/keycloak-session.service';

// Only attach the Keycloak access token to requests going through our API Gateway.
const bearerCondition = createInterceptorCondition<IncludeBearerTokenCondition>({
  urlPattern: /^http:\/\/localhost:8090(\/.*)?$/,
  bearerPrefix: 'Bearer'
});

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideKeycloak({
      config: {
        url: 'http://localhost:8080',
        realm: 'deliverx',
        clientId: 'admin-portal'
      },
      initOptions: {
        // No 'onLoad' -> Keycloak initializes silently, without forcing a redirect.
        // Our own /login page decides when to send the user to Keycloak.
        checkLoginIframe: false
      }
    }),
    {
      provide: INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
      useValue: [bearerCondition]
    },
    provideHttpClient(withInterceptors([includeBearerTokenInterceptor])),
    // Runs before the app renders: if a refresh token survived a page reload
    // (sessionStorage), silently restore the session instead of forcing a re-login.
    provideAppInitializer(() => inject(KeycloakSessionService).restoreSession())
  ]
};
