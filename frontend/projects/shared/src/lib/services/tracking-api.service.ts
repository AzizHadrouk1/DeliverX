import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api.config';
import { EtaResponse, RouteOptimizationResponse, TrackingEvent } from '../models/tracking.model';

@Injectable({ providedIn: 'root' })
export class TrackingApiService {
  private readonly http = inject(HttpClient);
  private readonly base = `${API_BASE_URL}/tracking/api/tracking`;

  /** Latest GPS position for a delivery */
  getLatestLocation(deliveryId: string): Observable<TrackingEvent> {
    return this.http.get<TrackingEvent>(`${this.base}/${deliveryId}/location`);
  }

  /** Full event history (newest first) */
  getHistory(deliveryId: string): Observable<TrackingEvent[]> {
    return this.http.get<TrackingEvent[]>(`${this.base}/${deliveryId}/history`);
  }

  /** ETA to a destination */
  getEta(deliveryId: string, destLat: number, destLng: number): Observable<EtaResponse> {
    return this.http.get<EtaResponse>(
      `${this.base}/${deliveryId}/eta?destLat=${destLat}&destLng=${destLng}`
    );
  }

  /** Optimized route for a delivery */
  getRoute(deliveryId: string): Observable<RouteOptimizationResponse> {
    return this.http.get<RouteOptimizationResponse>(`${this.base}/${deliveryId}/route`);
  }
}
