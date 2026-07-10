import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL, API_ROUTES } from '../config/api.config';
import { Delivery, DeliveryPackageResponse, DeliveryStatus, PageResponse } from '../models/delivery.model';

@Injectable({ providedIn: 'root' })
export class DeliveryApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}${API_ROUTES.deliveries}`;

  getDeliveryForPackage(id: number): Observable<DeliveryPackageResponse> {
    return this.http.get<DeliveryPackageResponse>(`${this.baseUrl}/package/${id}`);
  }

  getAll(status?: DeliveryStatus, size = 100): Observable<PageResponse<Delivery>> {
    let params = new HttpParams().set('size', size);
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get<PageResponse<Delivery>>(`${this.baseUrl}/api/deliveries`, { params });
  }
}
