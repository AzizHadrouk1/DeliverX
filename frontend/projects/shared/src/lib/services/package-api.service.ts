import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL, API_ROUTES } from '../config/api.config';
import { Package } from '../models/package.model';

@Injectable({ providedIn: 'root' })
export class PackageApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}${API_ROUTES.packages}`;

  getAll(): Observable<Package[]> {
    return this.http.get<Package[]>(this.baseUrl);
  }

  getById(id: number): Observable<Package> {
    return this.http.get<Package>(`${this.baseUrl}/${id}`);
  }
}
