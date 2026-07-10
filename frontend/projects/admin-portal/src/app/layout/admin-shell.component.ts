import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import Keycloak from 'keycloak-js';
import { KeycloakSessionService } from '../core/services/keycloak-session.service';

@Component({
  selector: 'app-admin-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './admin-shell.component.html',
  styleUrl: './admin-shell.component.scss'
})
export class AdminShellComponent {
  protected readonly keycloak = inject(Keycloak);
  private readonly session = inject(KeycloakSessionService);
  private readonly router = inject(Router);

  async logout(): Promise<void> {
    await this.session.logout();
    this.router.navigate(['/login']);
  }
}
