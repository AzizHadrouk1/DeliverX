import { Injectable, signal } from '@angular/core';
import { AuthUser, LoginRequest, UserRole } from '../models/auth.model';

const STORAGE_KEY = 'deliverx.auth';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly userSignal = signal<AuthUser | null>(this.readStoredUser());

  readonly user = this.userSignal.asReadonly();

  login(request: LoginRequest, expectedRole: UserRole): boolean {
    const isAdmin = expectedRole === 'ADMIN';
    const validAdmin = request.email === 'admin@deliverx.com' && request.password === 'admin';
    const validClient = request.email.length > 3 && request.password === 'client';

    if ((isAdmin && !validAdmin) || (!isAdmin && !validClient)) {
      return false;
    }

    const user: AuthUser = {
      email: request.email,
      name: isAdmin ? 'Administrator' : request.email.split('@')[0],
      role: expectedRole
    };

    localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
    this.userSignal.set(user);
    return true;
  }

  logout(): void {
    localStorage.removeItem(STORAGE_KEY);
    this.userSignal.set(null);
  }

  isAuthenticated(role?: UserRole): boolean {
    const user = this.userSignal();
    if (!user) {
      return false;
    }
    return role ? user.role === role : true;
  }

  private readStoredUser(): AuthUser | null {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) {
      return null;
    }

    try {
      return JSON.parse(raw) as AuthUser;
    } catch {
      localStorage.removeItem(STORAGE_KEY);
      return null;
    }
  }
}
