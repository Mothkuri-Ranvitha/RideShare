import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="home-container">
      <div class="hero">
        <h1>🚗 Triply</h1>
        <p class="tagline">Smart ride sharing made simple</p>
      </div>
      
      <div class="actions">
        <a routerLink="/login" class="btn btn-primary">Sign In</a>
        <a routerLink="/register" class="btn btn-secondary">Create Account</a>
      </div>
    </div>
  `,
  styles: [`
    .home-container {
      min-height: 100vh;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 2rem;
    }
    
    .hero {
      text-align: center;
      margin-bottom: 3rem;
    }
    
    .hero h1 {
      font-size: 4rem;
      margin: 0 0 1rem 0;
      font-weight: 700;
    }
    
    .tagline {
      font-size: 1.5rem;
      opacity: 0.9;
      margin: 0;
    }
    
    .actions {
      display: flex;
      gap: 1rem;
      flex-wrap: wrap;
      justify-content: center;
    }
    
    .btn {
      padding: 1rem 2rem;
      border-radius: 8px;
      text-decoration: none;
      font-weight: 600;
      font-size: 1.1rem;
      transition: transform 0.2s, box-shadow 0.2s;
      display: inline-block;
    }
    
    .btn:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0,0,0,0.2);
    }
    
    .btn-primary {
      background: white;
      color: #667eea;
    }
    
    .btn-secondary {
      background: transparent;
      color: white;
      border: 2px solid white;
    }
  `]
})
export class HomePageComponent {}

