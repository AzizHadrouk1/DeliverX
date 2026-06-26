import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'shared';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected email = '';
  protected password = '';
  protected readonly error = signal<string | null>(null);

  submit(): void {
    const success = this.auth.login({ email: this.email, password: this.password }, 'CLIENT');
    if (!success) {
      this.error.set('Invalid credentials. Use any email with password "client".');
      return;
    }

    this.router.navigate(['/track']);
  }
}
