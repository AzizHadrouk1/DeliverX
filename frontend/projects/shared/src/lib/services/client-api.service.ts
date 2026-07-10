import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL, API_ROUTES } from '../config/api.config';
import { Client, ClientStatus, ClientType } from '../models/client.model';

@Injectable({ providedIn: 'root' })
export class ClientApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}${API_ROUTES.clients}`;

  getAll(status?: ClientStatus, type?: ClientType): Observable<Client[]> {
    let params = new HttpParams();
    if (status) {
      params = params.set('status', status);
    }
    if (type) {
      params = params.set('type', type);
    }
    return this.http.get<Client[]>(this.baseUrl, { params });
  }

  getById(id: number): Observable<Client> {
    return this.http.get<Client>(`${this.baseUrl}/${id}`);
  }

  getMe(): Observable<Client> {
    return this.http.get<Client>(`${this.baseUrl}/me`);
  }

  updateMe(client: Client): Observable<Client> {
    return this.http.put<Client>(`${this.baseUrl}/me`, client);
  }

  create(client: Client): Observable<Client> {
    return this.http.post<Client>(`${this.baseUrl}/create`, client);
  }

  update(id: number, client: Client): Observable<Client> {
    return this.http.put<Client>(`${this.baseUrl}/${id}`, client);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
