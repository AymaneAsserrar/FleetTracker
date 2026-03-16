import { Injectable, signal, computed, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';

export interface LoginResponse {
  token: string;
  driverId: number;
  name: string;
  username: string;
  isManager: boolean;
}

const BASE_URL = 'http://localhost:8080/api/auth';
const TOKEN_KEY = 'fleet_token';
const USER_KEY  = 'fleet_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http   = inject(HttpClient);
  private readonly router = inject(Router);

  private _user = signal<LoginResponse | null>(this._loadUser());

  readonly currentUser = this._user.asReadonly();
  readonly isLoggedIn  = computed(() => this._user() !== null);
  readonly isManager   = computed(() => this._user()?.isManager === true);
  readonly driverId    = computed(() => this._user()?.driverId ?? null);

  register(data: {
    name: string; age: number; licenceNumber: string;
    contactPhone: string; username: string; password: string;
  }): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${BASE_URL}/register`, data).pipe(
      tap(res => {
        localStorage.setItem(TOKEN_KEY, res.token);
        localStorage.setItem(USER_KEY, JSON.stringify(res));
        this._user.set(res);
      })
    );
  }

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${BASE_URL}/login`, { username, password }).pipe(
      tap(res => {
        localStorage.setItem(TOKEN_KEY, res.token);
        localStorage.setItem(USER_KEY, JSON.stringify(res));
        this._user.set(res);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this._user.set(null);
    this.router.navigate(['/login']);
  }

  /** Call once on app startup — refreshes role/token from the live DB. */
  refreshFromServer(): void {
    if (!this.getToken()) return;
    this.http.get<LoginResponse>(`${BASE_URL}/me`).subscribe({
      next: res => {
        localStorage.setItem(TOKEN_KEY, res.token);
        localStorage.setItem(USER_KEY, JSON.stringify(res));
        this._user.set(res);
      },
      error: () => this.logout(),
    });
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  private _loadUser(): LoginResponse | null {
    try {
      const raw = localStorage.getItem(USER_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  }
}
