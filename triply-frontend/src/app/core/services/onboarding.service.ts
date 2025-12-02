import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class OnboardingService {
  /**
   * Generate a strong random password.
   */
  generatePassword(length = 12): string {
    const upper = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
    const lower = 'abcdefghijklmnopqrstuvwxyz';
    const digits = '0123456789';
    const symbols = '!@#$%^&*()_+-=[]{}|;:,.<>?';
    const all = upper + lower + digits + symbols;

    let pwd = '';
    pwd += upper[Math.floor(Math.random() * upper.length)];
    pwd += lower[Math.floor(Math.random() * lower.length)];
    pwd += digits[Math.floor(Math.random() * digits.length)];
    pwd += symbols[Math.floor(Math.random() * symbols.length)];

    for (let i = pwd.length; i < length; i++) {
      pwd += all[Math.floor(Math.random() * all.length)];
    }

    return pwd
      .split('')
      .sort(() => Math.random() - 0.5)
      .join('');
  }

  /**
   * Simulated email sending – in real life you would call a backend email API.
   */
  sendWelcomeEmail(email: string, name: string) {
    // For now we just log; UI can also show a toast/snackbar.
    console.info(`Simulated email: Welcome ${name}! Account created for ${email}.`);
  }
}


