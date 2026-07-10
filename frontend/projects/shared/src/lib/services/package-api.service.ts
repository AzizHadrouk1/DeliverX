import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL, API_ROUTES } from '../config/api.config';
import { PageResponse } from '../models/delivery.model';
import {
  Package,
  PackageMgmtStatus,
  PackageStatusHistory,
  UpdatePackageStatusRequest
} from '../models/package.model';

export interface PackageListFilters {
  status?: PackageMgmtStatus | '';
  clientId?: number | null;
  page?: number;
  size?: number;
}

@Injectable({ providedIn: 'root' })
export class PackageApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}${API_ROUTES.packages}`;

  getAll(filters: PackageListFilters = {}): Observable<PageResponse<Package>> {
    let params = new HttpParams()
      .set('page', String(filters.page ?? 0))
      .set('size', String(filters.size ?? 20));

    if (filters.status) {
      params = params.set('status', filters.status);
    }
    if (filters.clientId != null) {
      params = params.set('clientId', String(filters.clientId));
    }

    return this.http.get<PageResponse<Package>>(this.baseUrl, { params });
  }

  getById(id: number): Observable<Package> {
    return this.http.get<Package>(`${this.baseUrl}/${id}`);
  }

  getByTracking(trackingNumber: string): Observable<Package> {
    return this.http.get<Package>(`${this.baseUrl}/tracking/${encodeURIComponent(trackingNumber)}`);
  }

  getByClient(clientId: number): Observable<Package[]> {
    return this.http.get<Package[]>(`${this.baseUrl}/client/${clientId}`);
  }

  getHistory(id: number): Observable<PackageStatusHistory[]> {
    return this.http.get<PackageStatusHistory[]>(`${this.baseUrl}/${id}/history`);
  }

  create(pkg: Package): Observable<Package> {
    return this.http.post<Package>(this.baseUrl, this.toRequestBody(pkg));
  }

  update(id: number, pkg: Package): Observable<Package> {
    return this.http.put<Package>(`${this.baseUrl}/${id}`, this.toRequestBody(pkg));
  }

  updateStatus(id: number, body: UpdatePackageStatusRequest): Observable<Package> {
    return this.http.patch<Package>(`${this.baseUrl}/${id}/status`, body);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  private toRequestBody(pkg: Package): Partial<Package> {
    return {
      weight: pkg.weight,
      width: pkg.width,
      height: pkg.height,
      depth: pkg.depth,
      description: pkg.description,
      clientId: pkg.clientId
    };
  }
}
