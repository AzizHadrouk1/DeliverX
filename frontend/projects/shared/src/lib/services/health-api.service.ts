import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, forkJoin, map, of } from 'rxjs';
import { API_BASE_URL } from '../config/api.config';
import { HealthStatus, ServiceHealth } from '../models/health.model';

const MONITORED_SERVICES: Array<{ name: string; path: string }> = [
  { name: 'Assignment', path: '/assignment/health' },
  { name: 'Driver & Client', path: '/drivers/health' },
  { name: 'Vehicle', path: '/vehicles/health' },
  { name: 'Delivery', path: '/deliveries/health' },
  { name: 'Package', path: '/packages/health' }
];

@Injectable({ providedIn: 'root' })
export class HealthApiService {
  private readonly http = inject(HttpClient);

  checkService(path: string): Observable<ServiceHealth> {
    const name = MONITORED_SERVICES.find((service) => service.path === path)?.name ?? path;
    return this.http.get<HealthStatus>(`${API_BASE_URL}${path}`).pipe(
      map((status) => ({ name, path, status, online: status.status === 'UP' })),
      catchError((error) =>
        of({
          name,
          path,
          online: false,
          error: error?.message ?? 'Service unavailable'
        })
      )
    );
  }

  checkAll(): Observable<ServiceHealth[]> {
    return forkJoin(MONITORED_SERVICES.map((service) => this.checkService(service.path)));
  }
}
