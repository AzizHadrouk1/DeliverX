import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import {
  DRIVER_STATUSES,
  Driver,
  DriverApiService
} from 'shared';

@Component({
  selector: 'app-driver-form',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './driver-form.component.html',
  styleUrl: './driver-form.component.scss'
})
export class DriverFormComponent implements OnInit {
  private readonly driverApi = inject(DriverApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly statuses = DRIVER_STATUSES;
  protected readonly error = signal<string | null>(null);
  protected readonly saving = signal(false);
  protected isEdit = false;
  protected driverId?: number;

  protected driver: Driver = {
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    licenseNumber: '',
    status: 'AVAILABLE',
    vehicleId: null
  };

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.driverId = Number(id);
      this.driverApi.getById(this.driverId).subscribe({
        next: (driver) => (this.driver = driver),
        error: () => this.error.set('Driver not found.')
      });
    }
  }

  submit(): void {
    this.saving.set(true);
    this.error.set(null);

    const request = this.isEdit && this.driverId
      ? this.driverApi.update(this.driverId, this.driver)
      : this.driverApi.create(this.driver);

    request.subscribe({
      next: () => this.router.navigate(['/drivers']),
      error: () => {
        this.error.set('Unable to save driver. Check for a duplicate email/license, or that you have admin access.');
        this.saving.set(false);
      }
    });
  }
}
