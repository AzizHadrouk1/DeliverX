import { FormsModule } from '@angular/forms';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import {
  LoadingStateComponent,
  StatusBadgeComponent,
  StatusTonePipe,
  Vehicle,
  VehicleApiService,
  VehicleStatus,
  VEHICLE_STATUSES
} from 'shared';

@Component({
  selector: 'app-vehicle-list',
  standalone: true,
  imports: [FormsModule, RouterLink, LoadingStateComponent, StatusBadgeComponent, StatusTonePipe],
  templateUrl: './vehicle-list.component.html',
  styleUrl: './vehicle-list.component.scss'
})
export class VehicleListComponent implements OnInit {
  private readonly vehicleApi = inject(VehicleApiService);

  protected readonly statuses = VEHICLE_STATUSES;
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly vehicles = signal<Vehicle[]>([]);
  protected selectedStatus: VehicleStatus | '' = '';

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);

    this.vehicleApi.getAll(this.selectedStatus || undefined).subscribe({
      next: (vehicles) => {
        this.vehicles.set(vehicles);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Unable to load vehicles. Ensure the gateway and vehicle-service are running.');
        this.loading.set(false);
      }
    });
  }

  deleteVehicle(id: number): void {
    if (!confirm('Delete this vehicle?')) {
      return;
    }

    this.vehicleApi.delete(id).subscribe({
      next: () => this.load(),
      error: () => this.error.set('Failed to delete vehicle.')
    });
  }
}
