import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, JwtResponse } from '../../core/services/auth.service';

interface User {
  id: number;
  email: string;
  name: string;
  role: string;
  vehicleModel?: string;
  licensePlate?: string;
  capacity?: number;
  blocked?: boolean;
  driverVerified?: boolean;
}

interface Ride { id: number; source: string; destination: string; departureTime: string; farePerSeat: number; availableSeats: number; }
interface Booking { id: number; seatsBooked: number; status: string; passenger: User; ride: Ride; }
interface Payment { id: number; amount: number; status: string; createdAt: string; booking: Booking; }

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-dashboard.page.html',
  styleUrl: './admin-dashboard.page.scss'
})
export class AdminDashboardPageComponent {
  private http = inject(HttpClient);
  private auth = inject(AuthService);
  private router = inject(Router);

  user = signal<JwtResponse | null>(this.auth.currentUser());
  users = signal<User[]>([]);
  rides = signal<Ride[]>([]);
  bookings = signal<Booking[]>([]);
  payments = signal<Payment[]>([]);
  error = signal<string | null>(null);

  constructor() {
    const u = this.auth.currentUser();
    if (!u || u.role !== 'ROLE_ADMIN') {
      this.router.navigateByUrl('/');
      return;
    }
    this.refreshAll();
  }

  refreshAll() {
    this.error.set(null);
    this.http.get<User[]>('http://localhost:8080/api/admin/users').subscribe({ next: (d) => this.users.set(d), error: () => this.error.set('Failed to load users') });
    this.http.get<Ride[]>('http://localhost:8080/api/admin/rides').subscribe({ next: (d) => this.rides.set(d) });
    this.http.get<Booking[]>('http://localhost:8080/api/admin/bookings').subscribe({ next: (d) => this.bookings.set(d) });
    this.http.get<Payment[]>('http://localhost:8080/api/admin/payments').subscribe({ next: (d) => this.payments.set(d) });
  }

  blockUser(u: User, blocked: boolean) {
    this.http.post(`http://localhost:8080/api/admin/users/${u.id}/block?blocked=${blocked}`, {}).subscribe({ next: () => this.refreshAll() });
  }

  verifyDriver(u: User) {
    this.http.post(`http://localhost:8080/api/admin/users/${u.id}/verify-driver`, {}).subscribe({ next: () => this.refreshAll() });
  }
}

