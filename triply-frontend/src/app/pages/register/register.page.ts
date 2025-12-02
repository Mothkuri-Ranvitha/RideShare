import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

import { AuthService } from '../../core/services/auth.service';
import { OnboardingService } from '../../core/services/onboarding.service';

@Component({
  selector: 'app-register-page',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.page.html',
  styleUrl: './register.page.scss'
})
export class RegisterPageComponent {
  private auth = inject(AuthService);
  private onboarding = inject(OnboardingService);
  private router = inject(Router);

  role: 'DRIVER' | 'PASSENGER' = 'PASSENGER';
  name = '';
  email = '';
  phone = '';
  password = '';
  confirmPassword = '';

  vehicleModel = '';
  licensePlate = '';
  capacity: number | null = null;

  loading = signal(false);
  error = signal<string | null>(null);
  info = signal<string | null>(null);

  generatePassword() {
    const pwd = this.onboarding.generatePassword();
    this.password = pwd;
    this.confirmPassword = pwd;
    this.info.set('Strong password generated. You can still change it if you like.');
  }

  submit() {
    this.error.set(null);
    this.info.set(null);

    if (!this.name || !this.email || !this.phone) {
      this.error.set('Name, email and phone are required.');
      return;
    }

    if (!this.password || this.password.length < 8) {
      this.error.set('Password must be at least 8 characters.');
      return;
    }

    if (this.password !== this.confirmPassword) {
      this.error.set('Passwords do not match.');
      return;
    }

    if (this.role === 'DRIVER' && (!this.vehicleModel || !this.licensePlate || !this.capacity)) {
      this.error.set('Please provide vehicle details and capacity for driver accounts.');
      return;
    }

    this.loading.set(true);

    this.auth
      .register({
        email: this.email,
        password: this.password,
        name: this.name,
        phone: this.phone,
        role: this.role,
        vehicleModel: this.role === 'DRIVER' ? this.vehicleModel : undefined,
        licensePlate: this.role === 'DRIVER' ? this.licensePlate : undefined,
        capacity: this.role === 'DRIVER' ? this.capacity ?? undefined : undefined
      })
      .subscribe({
        next: () => {
          this.loading.set(false);
          this.onboarding.sendWelcomeEmail(this.email, this.name);
          this.router.navigateByUrl('/app');
        },
        error: (err) => {
          this.loading.set(false);
          if (err?.status === 400) {
            this.error.set('Email already exists. Please use a different email.');
          } else {
            this.error.set('Could not create account. Please try again.');
          }
        }
      });
  }
}


