import { Component, effect, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

import { AuthService, JwtResponse } from '../../core/services/auth.service';

interface Ride {
  id: number;
  source: string;
  destination: string;
  departureTime: string;
  availableSeats: number;
  farePerSeat: number;
}

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.page.html',
  styleUrl: './dashboard.page.scss'
})
export class DashboardPageComponent {
  private auth = inject(AuthService);
  private router = inject(Router);
  private http = inject(HttpClient);

  user = signal<JwtResponse | null>(this.auth.currentUser());

  searchSource = '';
  searchDestination = '';
  searchDate = '';
  minFare: number | null = null;
  maxFare: number | null = null;
  vehicleModel = '';
  rides = signal<Ride[]>([]);
  loadingRides = signal(false);
  seatsToBook: Record<number, number> = {};

  constructor() {
    effect(() => {
      this.user.set(this.auth.currentUser());
    });

    if (!this.auth.isLoggedIn()) {
      this.router.navigateByUrl('/login');
    }

    // load initial available rides
    this.loadingRides.set(true);
    this.http.get<Ride[]>('http://localhost:8080/api/rides')
      .subscribe({
        next: (res) => {
          this.loadingRides.set(false);
          this.rides.set(res);
        },
        error: () => {
          this.loadingRides.set(false);
        }
      });
  }

  logout() {
    this.auth.logout();
    this.router.navigateByUrl('/login');
  }

  search() {
    if (!this.searchSource || !this.searchDestination) {
      return;
    }
    this.loadingRides.set(true);
    const params = new URLSearchParams({
      source: this.searchSource,
      destination: this.searchDestination
    });
    if (this.searchDate) params.set('date', this.searchDate);
    if (this.minFare != null) params.set('minFare', String(this.minFare));
    if (this.maxFare != null) params.set('maxFare', String(this.maxFare));
    if (this.vehicleModel) params.set('vehicleModel', this.vehicleModel);

    this.http
      .get<Ride[]>(`http://localhost:8080/api/rides/search?${params.toString()}`)
      .subscribe({
        next: (res) => {
          this.loadingRides.set(false);
          this.rides.set(res);
        },
        error: () => {
          this.loadingRides.set(false);
        }
      });
  }

  book(rideId: number) {
    const seats = this.seatsToBook[rideId] ?? 1;
    if (seats <= 0) return;
    this.http
      .post('http://localhost:8080/api/bookings/book', {
        rideId,
        seatsBooked: seats
      })
      .subscribe({
        next: () => {
          this.search();
        }
      });
  }
}


