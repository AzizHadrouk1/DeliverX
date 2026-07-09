import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { KeycloakSessionService } from '../../core/services/keycloak-session.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private readonly session = inject(KeycloakSessionService);
  private readonly router = inject(Router);

  protected username = 'client1';
  protected password = '';
  protected readonly error = signal<string | null>(null);
  protected readonly loading = signal(false);

  async submit(): Promise<void> {
    this.error.set(null);
    this.loading.set(true);

    try {
      await this.session.loginWithPassword(this.username, this.password);

      if (!this.session.roles.includes('user')) {
        this.error.set('This account does not have client access.');
        return;
      }

      this.router.navigate(['/track']);
    } catch {
      this.error.set('Invalid username or password.');
    } finally {
      this.loading.set(false);
    }
  }
}
