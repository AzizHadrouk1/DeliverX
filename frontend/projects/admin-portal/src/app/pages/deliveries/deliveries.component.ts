import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  DeliveryApiService,
  DeliveryPackageResponse,
  LoadingStateComponent,
  StatusBadgeComponent,
  StatusTonePipe
} from 'shared';

@Component({
  selector: 'app-deliveries',
  standalone: true,
  imports: [FormsModule, LoadingStateComponent, StatusBadgeComponent, StatusTonePipe],
  templateUrl: './deliveries.component.html',
  styleUrl: './deliveries.component.scss'
})
export class DeliveriesComponent {
  private readonly deliveryApi = inject(DeliveryApiService);

  protected packageId = '1';
  protected readonly loading = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly result = signal<DeliveryPackageResponse | null>(null);

  prepare(): void {
    const id = Number(this.packageId);
    if (!id) {
      this.error.set('Enter a valid package ID.');
      return;
    }

    this.loading.set(true);
    this.error.set(null);

    this.deliveryApi.getDeliveryForPackage(id).subscribe({
      next: (response) => {
        this.result.set(response);
        this.loading.set(false);
      },
      error: () => {
        this.result.set(null);
        this.error.set('Unable to prepare delivery for this package.');
        this.loading.set(false);
      }
    });
  }
}
