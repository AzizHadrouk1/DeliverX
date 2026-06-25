import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL, API_ROUTES } from '../config/api.config';
import { DeliveryPackageResponse } from '../models/delivery.model';

@Injectable({ providedIn: 'root' })
export class DeliveryApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}${API_ROUTES.deliveries}`;

  getDeliveryForPackage(id: number): Observable<DeliveryPackageResponse> {
    return this.http.get<DeliveryPackageResponse>(`${this.baseUrl}/package/${id}`);
  }
}
