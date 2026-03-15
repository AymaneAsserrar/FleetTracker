import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LocationUpdate } from '../models/location-update.model';

const BASE_URL = 'http://localhost:8080/api/location-updates';

@Injectable({ providedIn: 'root' })
export class LocationUpdateService {
  private readonly http = inject(HttpClient);

  getByVehicle(vehicleId: number): Observable<LocationUpdate[]> {
    return this.http.get<LocationUpdate[]>(`${BASE_URL}/vehicle/${vehicleId}`);
  }

  getLatestByVehicle(vehicleId: number): Observable<LocationUpdate> {
    return this.http.get<LocationUpdate>(`${BASE_URL}/vehicle/${vehicleId}/latest`);
  }

  create(update: Partial<LocationUpdate>): Observable<LocationUpdate> {
    return this.http.post<LocationUpdate>(BASE_URL, update);
  }
}
