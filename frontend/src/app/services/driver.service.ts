import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Driver } from '../models/driver.model';

const BASE_URL = 'http://localhost:8080/api/drivers';

@Injectable({ providedIn: 'root' })
export class DriverService {
  private readonly http = inject(HttpClient);

  getAll(): Observable<Driver[]> {
    return this.http.get<Driver[]>(BASE_URL);
  }

  getById(id: number): Observable<Driver> {
    return this.http.get<Driver>(`${BASE_URL}/${id}`);
  }

  create(driver: Partial<Driver>): Observable<Driver> {
    return this.http.post<Driver>(BASE_URL, driver);
  }

  update(id: number, driver: Partial<Driver>): Observable<Driver> {
    return this.http.put<Driver>(`${BASE_URL}/${id}`, driver);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${BASE_URL}/${id}`);
  }
}
