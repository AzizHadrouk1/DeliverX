import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import {
  CLIENT_STATUSES,
  CLIENT_TYPES,
  Client,
  ClientApiService
} from 'shared';

@Component({
  selector: 'app-client-form',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './client-form.component.html',
  styleUrl: './client-form.component.scss'
})
export class ClientFormComponent implements OnInit {
  private readonly clientApi = inject(ClientApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly statuses = CLIENT_STATUSES;
  protected readonly types = CLIENT_TYPES;
  protected readonly error = signal<string | null>(null);
  protected readonly saving = signal(false);
  protected isEdit = false;
  protected clientId?: number;

  protected client: Client = {
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    companyName: '',
    address: '',
    city: '',
    type: 'INDIVIDUAL',
    status: 'ACTIVE'
  };

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.clientId = Number(id);
      this.clientApi.getById(this.clientId).subscribe({
        next: (client) => (this.client = client),
        error: () => this.error.set('Client not found.')
      });
    }
  }

  submit(): void {
    this.saving.set(true);
    this.error.set(null);

    const request = this.isEdit && this.clientId
      ? this.clientApi.update(this.clientId, this.client)
      : this.clientApi.create(this.client);

    request.subscribe({
      next: () => this.router.navigate(['/clients']),
      error: () => {
        this.error.set('Unable to save client. Check for a duplicate email, or that you have admin access.');
        this.saving.set(false);
      }
    });
  }
}
