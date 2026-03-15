import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Vehicle, VehicleStatus } from '../models/vehicle.model';

const BASE_URL = 'http://localhost:8080/api/vehicles';

@Injectable({ providedIn: 'root' })
export class VehicleService {
  private readonly http = inject(HttpClient);

  getAll(status?: VehicleStatus): Observable<Vehicle[]> {
    let params = new HttpParams();
    if (status) params = params.set('status', status);
    return this.http.get<Vehicle[]>(BASE_URL, { params });
  }

  getById(id: number): Observable<Vehicle> {
    return this.http.get<Vehicle>(`${BASE_URL}/${id}`);
  }

  create(vehicle: Partial<Vehicle>): Observable<Vehicle> {
    return this.http.post<Vehicle>(BASE_URL, vehicle);
  }

  update(id: number, vehicle: Partial<Vehicle>): Observable<Vehicle> {
    return this.http.put<Vehicle>(`${BASE_URL}/${id}`, vehicle);
  }

  updateLocation(id: number, latitude: number, longitude: number): Observable<Vehicle> {
    return this.http.put<Vehicle>(`${BASE_URL}/${id}/location`, { latitude, longitude });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${BASE_URL}/${id}`);
  }
}
