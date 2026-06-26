import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import {
  VEHICLE_STATUSES,
  VEHICLE_TYPES,
  Vehicle,
  VehicleApiService
} from 'shared';

@Component({
  selector: 'app-vehicle-form',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './vehicle-form.component.html',
  styleUrl: './vehicle-form.component.scss'
})
export class VehicleFormComponent implements OnInit {
  private readonly vehicleApi = inject(VehicleApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly types = VEHICLE_TYPES;
  protected readonly statuses = VEHICLE_STATUSES;
  protected readonly error = signal<string | null>(null);
  protected readonly saving = signal(false);
  protected isEdit = false;
  protected vehicleId?: number;

  protected vehicle: Vehicle = {
    licensePlate: '',
    brand: '',
    model: '',
    type: 'VAN',
    status: 'AVAILABLE',
    maxWeightCapacity: 0,
    maxVolumeCapacity: 0,
    manufacturingYear: new Date().getFullYear()
  };

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.vehicleId = Number(id);
      this.vehicleApi.getById(this.vehicleId).subscribe({
        next: (vehicle) => (this.vehicle = vehicle),
        error: () => this.error.set('Vehicle not found.')
      });
    }
  }

  submit(): void {
    this.saving.set(true);
    this.error.set(null);

    const request = this.isEdit && this.vehicleId
      ? this.vehicleApi.update(this.vehicleId, this.vehicle)
      : this.vehicleApi.create(this.vehicle);

    request.subscribe({
      next: () => this.router.navigate(['/vehicles']),
      error: () => {
        this.error.set('Unable to save vehicle. Check for duplicate license plate.');
        this.saving.set(false);
      }
    });
  }
}
