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
  banner = signal<string | null>(null);
  editingUser = signal<User | null>(null);
  editModel = {
    name: '',
    phone: '',
    role: '',
    blocked: false,
    driverVerified: false,
    vehicleModel: '',
    licensePlate: '',
    capacity: 1
  };

  constructor() {
    const u = this.auth.currentUser();
    if (!u || u.role !== 'ROLE_ADMIN') {
      this.router.navigateByUrl('/');
      return;
    }
    this.refreshAll();
  }

  // id selected in the top edit bar
  selectedUserId: number | null = null;

  // top nav tabs
  activeTab: 'team' | 'drivers' | 'users' | 'rides' | 'requests' = 'users';

  // derived counts
  countTeam() { return this.users().filter(u => u.role === 'ROLE_ADMIN').length; }
  countDrivers() { return this.users().filter(u => u.role === 'ROLE_DRIVER').length; }
  countUsers() { return this.users().filter(u => u.role === 'ROLE_PASSENGER').length; }
  countRides() { return this.rides().length; }
  countRequests() { return this.users().filter(u => u.role === 'ROLE_DRIVER' && !u.driverVerified).length; }

  // data shown for current tab
  displayUsers(): User[] {
    switch (this.activeTab) {
      case 'team':
        return this.users().filter(u => u.role === 'ROLE_ADMIN');
      case 'drivers':
        return this.users().filter(u => u.role === 'ROLE_DRIVER');
      case 'users':
        return this.users().filter(u => u.role === 'ROLE_PASSENGER');
      case 'requests':
        return this.users().filter(u => u.role === 'ROLE_DRIVER' && !u.driverVerified);
      default:
        return this.users();
    }
  }

  setTab(tab: 'team' | 'drivers' | 'users' | 'rides' | 'requests') {
    this.activeTab = tab;
    this.editingUser.set(null);
    this.selectedUserId = null;
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

  startEditFromToolbar() {
    if (!this.selectedUserId) {
      return;
      }
    const u = this.users().find(x => x.id === this.selectedUserId);
    if (u) {
      this.startEdit(u);
    }
  }

  deleteFromToolbar() {
    if (!this.selectedUserId) {
      return;
    }
    const u = this.users().find(x => x.id === this.selectedUserId);
    if (u) {
      this.deleteUser(u);
    }
  }

  startEdit(u: User) {
    this.editingUser.set(u);
    this.editModel = {
      name: u.name ?? '',
      phone: '',
      role: u.role,
      blocked: !!u.blocked,
      driverVerified: !!u.driverVerified,
      vehicleModel: u.vehicleModel ?? '',
      licensePlate: u.licensePlate ?? '',
      capacity: u.capacity ?? 1
    };
  }

  cancelEdit() {
    this.editingUser.set(null);
  }

  saveEdit() {
    const u = this.editingUser();
    if (!u) return;
    this.http
      .put(`http://localhost:8080/api/admin/users/${u.id}`, {
        name: this.editModel.name,
        phone: this.editModel.phone,
        role: this.editModel.role,
        blocked: this.editModel.blocked,
        driverVerified: this.editModel.driverVerified,
        vehicleModel: this.editModel.vehicleModel,
        licensePlate: this.editModel.licensePlate,
        capacity: this.editModel.capacity
      })
      .subscribe({
        next: () => {
          this.editingUser.set(null);
          this.refreshAll();
        },
        error: () => this.error.set('Failed to update user')
      });
  }

  deleteUser(u: User) {
    if (!confirm(`Delete user ${u.email}? This cannot be undone.`)) {
      return;
    }
    this.http.delete(`http://localhost:8080/api/admin/users/${u.id}`).subscribe({
      next: () => {
        this.refreshAll();
        this.banner.set('Deleted.');
        setTimeout(() => this.banner.set(null), 2500);
      },
      error: () => this.error.set('Failed to delete user')
    });
  }
}
