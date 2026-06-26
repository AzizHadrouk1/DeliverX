import { Routes } from '@angular/router';
import { ShellComponent } from './layout/shell.component';
import { clientGuestGuard } from './core/guards/client-guest.guard';

export const routes: Routes = [
  {
    path: '',
    component: ShellComponent,
    children: [
      { path: '', loadComponent: () => import('./pages/home/home.component').then(m => m.HomeComponent) },
      { path: 'track', loadComponent: () => import('./pages/track/track.component').then(m => m.TrackComponent) },
      { path: 'track/:id', loadComponent: () => import('./pages/track/track.component').then(m => m.TrackComponent) },
      {
        path: 'login',
        canActivate: [clientGuestGuard],
        loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent)
      }
    ]
  },
  { path: '**', redirectTo: '' }
];
