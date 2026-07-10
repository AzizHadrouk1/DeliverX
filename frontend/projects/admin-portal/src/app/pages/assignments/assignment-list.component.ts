import { DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import {
  Assignment,
  AssignmentApiService,
  AssignmentStatus,
  DriverApiService,
  LoadingStateComponent,
  StatusBadgeComponent,
  StatusTonePipe,
  VehicleApiService
} from 'shared';

@Component({
  selector: 'app-assignment-list',
  standalone: true,
  imports: [RouterLink, DatePipe, LoadingStateComponent, StatusBadgeComponent, StatusTonePipe],
  templateUrl: './assignment-list.component.html',
  styleUrl: './assignment-list.component.scss'
})
export class AssignmentListComponent implements OnInit {
  private readonly assignmentApi = inject(AssignmentApiService);
  private readonly driverApi = inject(DriverApiService);
  private readonly vehicleApi = inject(VehicleApiService);

  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly assignments = signal<Assignment[]>([]);
  protected readonly driverNames = signal<Record<number, string>>({});
  protected readonly vehiclePlates = signal<Record<number, string>>({});

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);

    forkJoin({
      assignments: this.assignmentApi.getAll(),
      drivers: this.driverApi.getAll(),
      vehicles: this.vehicleApi.getAll()
    }).subscribe({
      next: ({ assignments, drivers, vehicles }) => {
        this.assignments.set(assignments);
        this.driverNames.set(
          Object.fromEntries(drivers.map((d) => [d.id!, `${d.firstName} ${d.lastName}`]))
        );
        this.vehiclePlates.set(Object.fromEntries(vehicles.map((v) => [v.id!, v.licensePlate])));
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Unable to load assignments. Ensure the gateway and assignment-service are running.');
        this.loading.set(false);
      }
    });
  }

  driverName(id: number): string {
    return this.driverNames()[id] ?? `#${id}`;
  }

  vehiclePlate(id: number): string {
    return this.vehiclePlates()[id] ?? `#${id}`;
  }

  canStart(assignment: Assignment): boolean {
    return assignment.status === 'ASSIGNED';
  }

  canComplete(assignment: Assignment): boolean {
    return assignment.status === 'IN_PROGRESS';
  }

  canCancel(assignment: Assignment): boolean {
    return assignment.status === 'ASSIGNED' || assignment.status === 'IN_PROGRESS';
  }

  canEditOrDelete(assignment: Assignment): boolean {
    return assignment.status === 'ASSIGNED';
  }

  updateStatus(assignment: Assignment, status: AssignmentStatus): void {
    this.assignmentApi.updateStatus(assignment.id!, status).subscribe({
      next: () => this.load(),
      error: () => this.error.set('Failed to update assignment status.')
    });
  }

  deleteAssignment(id: number): void {
    if (!confirm('Delete this assignment?')) {
      return;
    }

    this.assignmentApi.delete(id).subscribe({
      next: () => this.load(),
      error: () => this.error.set('Failed to delete assignment.')
    });
  }
}
