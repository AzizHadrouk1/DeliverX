import { FormsModule } from '@angular/forms';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import {
  DRIVER_STATUSES,
  Driver,
  DriverApiService,
  DriverStatus,
  LoadingStateComponent,
  StatusBadgeComponent,
  StatusTonePipe
} from 'shared';

@Component({
  selector: 'app-driver-list',
  standalone: true,
  imports: [FormsModule, RouterLink, LoadingStateComponent, StatusBadgeComponent, StatusTonePipe],
  templateUrl: './driver-list.component.html',
  styleUrl: './driver-list.component.scss'
})
export class DriverListComponent implements OnInit {
  private readonly driverApi = inject(DriverApiService);

  protected readonly statuses = DRIVER_STATUSES;
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly drivers = signal<Driver[]>([]);
  protected selectedStatus: DriverStatus | '' = '';

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);

    this.driverApi.getAll(this.selectedStatus || undefined).subscribe({
      next: (drivers) => {
        this.drivers.set(drivers);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Unable to load drivers. Ensure the gateway and driver-client-service are running.');
        this.loading.set(false);
      }
    });
  }

  deleteDriver(id: number): void {
    if (!confirm('Delete this driver?')) {
      return;
    }

    this.driverApi.delete(id).subscribe({
      next: () => this.load(),
      error: () => this.error.set('Failed to delete driver. Admin write access may require authentication.')
    });
  }
}
