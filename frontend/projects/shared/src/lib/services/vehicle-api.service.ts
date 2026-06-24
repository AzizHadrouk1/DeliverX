import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL, API_ROUTES } from '../config/api.config';
import { Vehicle, VehicleStatus } from '../models/vehicle.model';

@Injectable({ providedIn: 'root' })
export class VehicleApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}${API_ROUTES.vehicles}`;

  getAll(status?: VehicleStatus): Observable<Vehicle[]> {
    let params = new HttpParams();
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get<Vehicle[]>(this.baseUrl, { params });
  }

  getById(id: number): Observable<Vehicle> {
    return this.http.get<Vehicle>(`${this.baseUrl}/${id}`);
  }

  create(vehicle: Vehicle): Observable<Vehicle> {
    return this.http.post<Vehicle>(`${this.baseUrl}/create`, vehicle);
  }

  update(id: number, vehicle: Vehicle): Observable<Vehicle> {
    return this.http.put<Vehicle>(`${this.baseUrl}/${id}`, vehicle);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
