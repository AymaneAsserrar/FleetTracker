import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Route } from '../models/route.model';

const BASE_URL = 'http://localhost:8080/api/routes';

@Injectable({ providedIn: 'root' })
export class RouteService {
  private readonly http = inject(HttpClient);

  getAll(activeOnly?: boolean): Observable<Route[]> {
    let params = new HttpParams();
    if (activeOnly) params = params.set('activeOnly', 'true');
    return this.http.get<Route[]>(BASE_URL, { params });
  }

  getById(id: number): Observable<Route> {
    return this.http.get<Route>(`${BASE_URL}/${id}`);
  }

  create(route: Partial<Route>): Observable<Route> {
    return this.http.post<Route>(BASE_URL, route);
  }

  update(id: number, route: Partial<Route>): Observable<Route> {
    return this.http.put<Route>(`${BASE_URL}/${id}`, route);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${BASE_URL}/${id}`);
  }
}
