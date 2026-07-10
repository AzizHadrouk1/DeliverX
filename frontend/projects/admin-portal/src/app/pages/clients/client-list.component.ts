import { FormsModule } from '@angular/forms';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import {
  CLIENT_STATUSES,
  CLIENT_TYPES,
  Client,
  ClientApiService,
  ClientStatus,
  ClientType,
  LoadingStateComponent,
  StatusBadgeComponent,
  StatusTonePipe
} from 'shared';

@Component({
  selector: 'app-client-list',
  standalone: true,
  imports: [FormsModule, RouterLink, LoadingStateComponent, StatusBadgeComponent, StatusTonePipe],
  templateUrl: './client-list.component.html',
  styleUrl: './client-list.component.scss'
})
export class ClientListComponent implements OnInit {
  private readonly clientApi = inject(ClientApiService);

  protected readonly statuses = CLIENT_STATUSES;
  protected readonly types = CLIENT_TYPES;
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly clients = signal<Client[]>([]);
  protected selectedStatus: ClientStatus | '' = '';
  protected selectedType: ClientType | '' = '';

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);

    this.clientApi.getAll(this.selectedStatus || undefined, this.selectedType || undefined).subscribe({
      next: (clients) => {
        this.clients.set(clients);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Unable to load clients. Ensure the gateway and driver-client-service are running.');
        this.loading.set(false);
      }
    });
  }

  deleteClient(id: number): void {
    if (!confirm('Delete this client?')) {
      return;
    }

    this.clientApi.delete(id).subscribe({
      next: () => this.load(),
      error: () => this.error.set('Failed to delete client. Admin write access may require authentication.')
    });
  }
}
