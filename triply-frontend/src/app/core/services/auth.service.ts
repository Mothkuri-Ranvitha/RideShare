import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  name: string;
  phone: string;
  role: 'DRIVER' | 'PASSENGER';
  vehicleModel?: string;
  licensePlate?: string;
  capacity?: number;
}

export interface JwtResponse {
  token: string;
  type: string;
  id: number;
  email: string;
  role: string;
  name: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);

  private readonly apiBase = 'http://localhost:8080/api/auth';

  currentUser = signal<JwtResponse | null>(null);

  constructor() {
    const stored = localStorage.getItem('triply_user');
    const token = localStorage.getItem('triply_token');
    if (stored && token) {
      this.currentUser.set(JSON.parse(stored));
    }
  }

  login(payload: LoginRequest) {
    return this.http.post<JwtResponse>(`${this.apiBase}/login`, payload).pipe(
      tap((res) => {
        localStorage.setItem('triply_token', res.token);
        localStorage.setItem('triply_user', JSON.stringify(res));
        this.currentUser.set(res);
      })
    );
  }

  register(payload: RegisterRequest) {
    return this.http.post<JwtResponse>(`${this.apiBase}/register`, payload).pipe(
      tap((res) => {
        localStorage.setItem('triply_token', res.token);
        localStorage.setItem('triply_user', JSON.stringify(res));
        this.currentUser.set(res);
      })
    );
  }

  logout() {
    localStorage.removeItem('triply_token');
    localStorage.removeItem('triply_user');
    this.currentUser.set(null);
  }

  isLoggedIn() {
    return !!this.currentUser();
  }
}


