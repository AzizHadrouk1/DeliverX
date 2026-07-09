import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Client, ClientApiService } from 'shared';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {
  private readonly clientApi = inject(ClientApiService);

  protected readonly client = signal<Client | null>(null);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly saved = signal(false);

  ngOnInit(): void {
    this.clientApi.getMe().subscribe({
      next: (client) => {
        this.client.set(client);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('We could not find a client profile linked to this account.');
        this.loading.set(false);
      }
    });
  }

  submit(): void {
    const current = this.client();
    if (!current) {
      return;
    }

    this.saving.set(true);
    this.saved.set(false);
    this.error.set(null);

    this.clientApi.updateMe(current).subscribe({
      next: (updated) => {
        this.client.set(updated);
        this.saving.set(false);
        this.saved.set(true);
      },
      error: () => {
        this.error.set('Could not save your changes. Please try again.');
        this.saving.set(false);
      }
    });
  }
}
