import { Component, OnInit, inject, signal } from '@angular/core';
import { HealthApiService, LoadingStateComponent, ServiceHealth, StatusBadgeComponent } from 'shared';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [LoadingStateComponent, StatusBadgeComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  private readonly healthApi = inject(HealthApiService);

  protected readonly loading = signal(true);
  protected readonly services = signal<ServiceHealth[]>([]);

  ngOnInit(): void {
    this.healthApi.checkAll().subscribe({
      next: (services) => {
        this.services.set(services);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }
}
