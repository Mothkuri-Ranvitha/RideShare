import { Routes } from '@angular/router';

import { HomePageComponent } from './pages/home/home.page';
import { LoginPageComponent } from './pages/login/login.page';
import { RegisterPageComponent } from './pages/register/register.page';
import { DashboardPageComponent } from './pages/dashboard/dashboard.page';
import { AuthLayoutComponent } from './layout/auth-layout/auth-layout.component';

export const routes: Routes = [
  {
    path: '',
    component: HomePageComponent
  },
  {
    path: 'auth',
    component: AuthLayoutComponent,
    children: [
      { path: 'login', component: LoginPageComponent },
      { path: 'register', component: RegisterPageComponent }
    ]
  },
  {
    path: 'login',
    component: AuthLayoutComponent,
    children: [
      { path: '', component: LoginPageComponent }
    ]
  },
  {
    path: 'register',
    component: AuthLayoutComponent,
    children: [
      { path: '', component: RegisterPageComponent }
    ]
  },
  {
    path: 'app',
    component: DashboardPageComponent
  },
  {
    path: 'post-ride',
    loadComponent: () => import('./pages/post-ride/post-ride.page').then(m => m.PostRidePageComponent)
  },
  {
    path: 'admin',
    loadComponent: () => import('./pages/admin-dashboard/admin-dashboard.page').then(m => m.AdminDashboardPageComponent)
  },
  { path: '**', redirectTo: '' }
];
