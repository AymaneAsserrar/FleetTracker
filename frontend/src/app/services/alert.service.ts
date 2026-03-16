import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Alert } from '../models/alert.model';

const BASE_URL = 'http://localhost:8080/api/alerts';

@Injectable({ providedIn: 'root' })
export class AlertService {
  private readonly http = inject(HttpClient);

  getAll(): Observable<Alert[]> {
    return this.http.get<Alert[]>(BASE_URL);
  }

  getUnresolved(): Observable<Alert[]> {
    return this.http.get<Alert[]>(`${BASE_URL}/unresolved`);
  }

  resolve(id: number): Observable<Alert> {
    return this.http.patch<Alert>(`${BASE_URL}/${id}/resolve`, null);
  }
}
