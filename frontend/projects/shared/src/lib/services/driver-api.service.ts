import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL, API_ROUTES } from '../config/api.config';
import { Driver, DriverStatus } from '../models/driver.model';

@Injectable({ providedIn: 'root' })
export class DriverApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}${API_ROUTES.drivers}`;

  getAll(status?: DriverStatus): Observable<Driver[]> {
    let params = new HttpParams();
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get<Driver[]>(this.baseUrl, { params });
  }

  getById(id: number): Observable<Driver> {
    return this.http.get<Driver>(`${this.baseUrl}/${id}`);
  }

  create(driver: Driver): Observable<Driver> {
    return this.http.post<Driver>(`${this.baseUrl}/create`, driver);
  }

  update(id: number, driver: Driver): Observable<Driver> {
    return this.http.put<Driver>(`${this.baseUrl}/${id}`, driver);
  }

  updateStatus(id: number, status: DriverStatus): Observable<Driver> {
    return this.http.post<Driver>(`${this.baseUrl}/${id}/status`, { status });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
