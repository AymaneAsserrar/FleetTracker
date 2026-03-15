import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Trip, TripStatus } from '../models/trip.model';

const BASE_URL = 'http://localhost:8080/api/trips';

@Injectable({ providedIn: 'root' })
export class TripService {
  private readonly http = inject(HttpClient);

  getAll(status?: TripStatus): Observable<Trip[]> {
    let params = new HttpParams();
    if (status) params = params.set('status', status);
    return this.http.get<Trip[]>(BASE_URL, { params });
  }

  getById(id: number): Observable<Trip> {
    return this.http.get<Trip>(`${BASE_URL}/${id}`);
  }

  getByVehicle(vehicleId: number): Observable<Trip[]> {
    return this.http.get<Trip[]>(`${BASE_URL}/vehicle/${vehicleId}`);
  }

  create(trip: Partial<Trip>): Observable<Trip> {
    return this.http.post<Trip>(BASE_URL, trip);
  }

  update(id: number, trip: Partial<Trip>): Observable<Trip> {
    return this.http.put<Trip>(`${BASE_URL}/${id}`, trip);
  }

  updateStatus(id: number, status: TripStatus): Observable<Trip> {
    return this.http.patch<Trip>(`${BASE_URL}/${id}/status`, { status });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${BASE_URL}/${id}`);
  }
}
