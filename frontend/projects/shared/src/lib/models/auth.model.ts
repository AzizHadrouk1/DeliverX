export type UserRole = 'CLIENT' | 'ADMIN';

export interface AuthUser {
  email: string;
  name: string;
  role: UserRole;
}

export interface LoginRequest {
  email: string;
  password: string;
}
