import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { KeycloakSessionService } from '../../core/services/keycloak-session.service';

@Component({
  selector: 'app-admin-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './admin-login.component.html',
  styleUrl: './admin-login.component.scss'
})
export class AdminLoginComponent {
  private readonly session = inject(KeycloakSessionService);
  private readonly router = inject(Router);

  protected username = 'admin1';
  protected password = '';
  protected readonly error = signal<string | null>(null);
  protected readonly loading = signal(false);

  async submit(): Promise<void> {
    this.error.set(null);
    this.loading.set(true);

    try {
      await this.session.loginWithPassword(this.username, this.password);

      if (!this.session.adminRoles.includes('admin')) {
        this.error.set('This account does not have admin access.');
        return;
      }

      this.router.navigate(['/dashboard']);
    } catch {
      this.error.set('Invalid username or password.');
    } finally {
      this.loading.set(false);
    }
  }
}
