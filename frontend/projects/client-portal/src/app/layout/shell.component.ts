import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import Keycloak from 'keycloak-js';
import { KeycloakSessionService } from '../core/services/keycloak-session.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.scss'
})
export class ShellComponent {
  protected readonly keycloak = inject(Keycloak);
  private readonly session = inject(KeycloakSessionService);
  private readonly router = inject(Router);

  async logout(): Promise<void> {
    await this.session.logout();
    this.router.navigate(['/login']);
  }
}
