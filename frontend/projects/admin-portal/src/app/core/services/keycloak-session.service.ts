import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import Keycloak from 'keycloak-js';

interface TokenResponse {
  access_token: string;
  refresh_token: string;
  id_token?: string;
}

const KEYCLOAK_BASE = 'http://localhost:8080/realms/deliverx/protocol/openid-connect';
const CLIENT_ID = 'admin-portal';
const STORAGE_KEY = 'deliverx.admin.refresh_token';

/**
 * Wraps Keycloak's Direct Access Grants flow (password + refresh_token grants) so the
 * app's own login form can authenticate without a browser redirect, while still keeping
 * the injected `Keycloak` instance (guards, HTTP bearer interceptor) in a normal state.
 */
@Injectable({ providedIn: 'root' })
export class KeycloakSessionService {
  private readonly keycloak = inject(Keycloak);
  private readonly http = inject(HttpClient);

  async loginWithPassword(username: string, password: string): Promise<void> {
    const body = new URLSearchParams();
    body.set('grant_type', 'password');
    body.set('client_id', CLIENT_ID);
    body.set('username', username);
    body.set('password', password);
    body.set('scope', 'openid');

    const tokens = await firstValueFrom(
      this.http.post<TokenResponse>(`${KEYCLOAK_BASE}/token`, body.toString(), {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
      })
    );

    this.applyTokens(tokens);
  }

  /** Called at app startup: if a refresh token survived a page reload, use it to get a fresh session. */
  async restoreSession(): Promise<void> {
    const refreshToken = sessionStorage.getItem(STORAGE_KEY);
    if (!refreshToken) {
      return;
    }

    const body = new URLSearchParams();
    body.set('grant_type', 'refresh_token');
    body.set('client_id', CLIENT_ID);
    body.set('refresh_token', refreshToken);

    try {
      const tokens = await firstValueFrom(
        this.http.post<TokenResponse>(`${KEYCLOAK_BASE}/token`, body.toString(), {
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        })
      );
      this.applyTokens(tokens);
    } catch {
      // Refresh token expired or was revoked (e.g. logged out elsewhere) - just clear it.
      sessionStorage.removeItem(STORAGE_KEY);
    }
  }

  /** Invalidates the session server-side (so the refresh token can't be reused) and clears local state. */
  async logout(): Promise<void> {
    const refreshToken = this.keycloak.refreshToken;
    this.clear();

    if (!refreshToken) {
      return;
    }

    const body = new URLSearchParams();
    body.set('client_id', CLIENT_ID);
    body.set('refresh_token', refreshToken);

    try {
      await firstValueFrom(
        this.http.post(`${KEYCLOAK_BASE}/logout`, body.toString(), {
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        })
      );
    } catch {
      // Best-effort server-side invalidation - local state is already cleared either way.
    }
  }

  get adminRoles(): string[] {
    return this.keycloak.tokenParsed?.resource_access?.['driver-client-service']?.roles ?? [];
  }

  private applyTokens(tokens: TokenResponse): void {
    this.keycloak.token = tokens.access_token;
    this.keycloak.refreshToken = tokens.refresh_token;
    this.keycloak.idToken = tokens.id_token;
    this.keycloak.tokenParsed = this.decodeJwt(tokens.access_token);
    this.keycloak.authenticated = true;
    sessionStorage.setItem(STORAGE_KEY, tokens.refresh_token);
  }

  private clear(): void {
    this.keycloak.token = undefined;
    this.keycloak.refreshToken = undefined;
    this.keycloak.idToken = undefined;
    this.keycloak.tokenParsed = undefined;
    this.keycloak.authenticated = false;
    sessionStorage.removeItem(STORAGE_KEY);
  }

  private decodeJwt(token: string): Record<string, any> {
    const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    const padded = payload.padEnd(payload.length + ((4 - (payload.length % 4)) % 4), '=');
    return JSON.parse(atob(padded));
  }
}
