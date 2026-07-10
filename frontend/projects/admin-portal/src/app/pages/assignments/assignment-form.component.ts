import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import {
  Assignment,
  AssignmentApiService,
  Delivery,
  DeliveryApiService,
  Driver,
  DriverApiService,
  Vehicle,
  VehicleApiService
} from 'shared';

@Component({
  selector: 'app-assignment-form',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './assignment-form.component.html',
  styleUrl: './assignment-form.component.scss'
})
export class AssignmentFormComponent implements OnInit {
  private readonly assignmentApi = inject(AssignmentApiService);
  private readonly deliveryApi = inject(DeliveryApiService);
  private readonly driverApi = inject(DriverApiService);
  private readonly vehicleApi = inject(VehicleApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly error = signal<string | null>(null);
  protected readonly saving = signal(false);
  protected readonly deliveries = signal<Delivery[]>([]);
  protected readonly drivers = signal<Driver[]>([]);
  protected readonly vehicles = signal<Vehicle[]>([]);
  protected isEdit = false;
  protected assignmentId?: number;

  protected assignment: Assignment = {
    deliveryId: 0,
    driverId: 0,
    vehicleId: 0
  };

  ngOnInit(): void {
    this.deliveryApi.getAll('PENDING').subscribe({
      next: (page) => this.deliveries.set(page.content)
    });
    this.driverApi.getAll('AVAILABLE').subscribe({
      next: (drivers) => this.drivers.set(drivers)
    });
    this.vehicleApi.getAll('AVAILABLE').subscribe({
      next: (vehicles) => this.vehicles.set(vehicles)
    });

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.assignmentId = Number(id);
      this.assignmentApi.getById(this.assignmentId).subscribe({
        next: (assignment) => (this.assignment = assignment),
        error: () => this.error.set('Assignment not found.')
      });
    }
  }

  submit(): void {
    this.saving.set(true);
    this.error.set(null);

    const request = this.isEdit && this.assignmentId
      ? this.assignmentApi.update(this.assignmentId, this.assignment)
      : this.assignmentApi.create(this.assignment);

    request.subscribe({
      next: () => this.router.navigate(['/assignments']),
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Unable to save assignment. Check driver/vehicle availability.');
        this.saving.set(false);
      }
    });
  }
}
