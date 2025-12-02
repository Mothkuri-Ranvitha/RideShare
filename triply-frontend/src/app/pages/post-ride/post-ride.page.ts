import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-post-ride-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './post-ride.page.html',
  styleUrl: './post-ride.page.scss'
})
export class PostRidePageComponent {
  private auth = inject(AuthService);
  private http = inject(HttpClient);
  private router = inject(Router);

  source = '';
  destination = '';
  date = ''; // yyyy-MM-dd
  time = ''; // HH:mm
  availableSeats: number | null = null;
  farePerSeat: number | null = null; // optional; computed if empty and lat/lng provided
  sourceLat: number | null = null;
  sourceLng: number | null = null;
  destLat: number | null = null;
  destLng: number | null = null;

  loading = signal(false);
  error = signal<string | null>(null);
  info = signal<string | null>(null);

  ngOnInit() {
    const u = this.auth.currentUser();
    if (!u || u.role !== 'ROLE_DRIVER') {
      this.router.navigateByUrl('/app');
    }
  }

  submit() {
    this.error.set(null);
    this.info.set(null);

    if (!this.source || !this.destination || !this.date || !this.time) {
      this.error.set('Please provide source, destination, date and time.');
      return;
    }
    if (!this.availableSeats || this.availableSeats <= 0) {
      this.error.set('Please provide a valid available seats count.');
      return;
    }

    const payload: any = {
      source: this.source,
      destination: this.destination,
      departureTime: `${this.date}T${this.time}:00`,
      availableSeats: this.availableSeats,
      farePerSeat: this.farePerSeat ?? 0,
      sourceLat: this.sourceLat ?? undefined,
      sourceLng: this.sourceLng ?? undefined,
      destLat: this.destLat ?? undefined,
      destLng: this.destLng ?? undefined
    };

    this.loading.set(true);
    this.http.post('http://localhost:8080/api/rides/post', payload).subscribe({
      next: () => {
        this.loading.set(false);
        this.info.set('Ride posted successfully.');
        this.router.navigateByUrl('/app');
      },
      error: (err) => {
        this.loading.set(false);
        if (err?.status === 401) {
          this.error.set('Unauthorized. Please login as a driver.');
        } else {
          this.error.set('Could not post ride. Please try again.');
        }
      }
    });
  }
}
