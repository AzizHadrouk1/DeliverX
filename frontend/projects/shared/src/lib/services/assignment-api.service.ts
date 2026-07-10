import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL, API_ROUTES } from '../config/api.config';
import { Assignment, AssignmentDetails, AssignmentStatus } from '../models/assignment.model';

@Injectable({ providedIn: 'root' })
export class AssignmentApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}${API_ROUTES.assignment}/api/assignments`;

  getAll(): Observable<Assignment[]> {
    return this.http.get<Assignment[]>(this.baseUrl);
  }

  getById(id: number): Observable<Assignment> {
    return this.http.get<Assignment>(`${this.baseUrl}/${id}`);
  }

  getDetails(id: number): Observable<AssignmentDetails> {
    return this.http.get<AssignmentDetails>(`${this.baseUrl}/${id}/details`);
  }

  create(assignment: Assignment): Observable<Assignment> {
    return this.http.post<Assignment>(this.baseUrl, assignment);
  }

  update(id: number, assignment: Assignment): Observable<Assignment> {
    return this.http.put<Assignment>(`${this.baseUrl}/${id}`, assignment);
  }

  updateStatus(id: number, status: AssignmentStatus): Observable<Assignment> {
    return this.http.patch<Assignment>(`${this.baseUrl}/${id}/status`, { status });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
