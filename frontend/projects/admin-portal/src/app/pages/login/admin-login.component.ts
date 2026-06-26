import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'shared';

@Component({
  selector: 'app-admin-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './admin-login.component.html',
  styleUrl: './admin-login.component.scss'
})
export class AdminLoginComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected email = 'admin@deliverx.com';
  protected password = '';
  protected readonly error = signal<string | null>(null);

  submit(): void {
    const success = this.auth.login({ email: this.email, password: this.password }, 'ADMIN');
    if (!success) {
      this.error.set('Invalid admin credentials.');
      return;
    }

    this.router.navigate(['/dashboard']);
  }
}
