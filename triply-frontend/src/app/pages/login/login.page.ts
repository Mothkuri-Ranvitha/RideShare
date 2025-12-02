import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.page.html',
  styleUrl: './login.page.scss'
})
export class LoginPageComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  email = '';
  password = '';
  loading = signal(false);
  error = signal<string | null>(null);

  submit() {
    if (!this.email || !this.password) {
      this.error.set('Email and password are required.');
      return;
    }

    this.loading.set(true);
    this.error.set(null);

    this.auth
      .login({ email: this.email, password: this.password })
      .subscribe({
        next: () => {
          this.loading.set(false);
          this.router.navigateByUrl('/app');
        },
        error: () => {
          this.loading.set(false);
          this.error.set('Invalid credentials. Please try again.');
        }
      });
  }
}


