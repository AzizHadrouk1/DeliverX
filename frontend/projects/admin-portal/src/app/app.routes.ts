import { Routes } from '@angular/router';
import { AdminShellComponent } from './layout/admin-shell.component';
import { roleGuard } from './core/guards/admin-auth.guard';
import { adminGuestGuard } from './core/guards/admin-guest.guard';

export const routes: Routes = [
  {
    path: 'login',
    canActivate: [adminGuestGuard],
    loadComponent: () => import('./pages/login/admin-login.component').then(m => m.AdminLoginComponent)
  },
  {
    path: '',
    component: AdminShellComponent,
    canActivate: [roleGuard('ADMIN')],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      { path: 'dashboard', loadComponent: () => import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent) },
      { path: 'vehicles', loadComponent: () => import('./pages/vehicles/vehicle-list.component').then(m => m.VehicleListComponent) },
      { path: 'vehicles/new', loadComponent: () => import('./pages/vehicles/vehicle-form.component').then(m => m.VehicleFormComponent) },
      { path: 'vehicles/:id/edit', loadComponent: () => import('./pages/vehicles/vehicle-form.component').then(m => m.VehicleFormComponent) },
      { path: 'packages', loadComponent: () => import('./pages/packages/package-list.component').then(m => m.PackageListComponent) },
      { path: 'packages/:id', loadComponent: () => import('./pages/packages/package-detail.component').then(m => m.PackageDetailComponent) },
      { path: 'deliveries', loadComponent: () => import('./pages/deliveries/deliveries.component').then(m => m.DeliveriesComponent) },
      { path: 'driver-live', loadComponent: () => import('./pages/driver-live/driver-live.component').then(m => m.DriverLiveComponent) },
      { path: 'assignments', loadComponent: () => import('./pages/assignments/assignment-list.component').then(m => m.AssignmentListComponent) },
      { path: 'assignments/new', loadComponent: () => import('./pages/assignments/assignment-form.component').then(m => m.AssignmentFormComponent) },
      { path: 'assignments/:id/edit', loadComponent: () => import('./pages/assignments/assignment-form.component').then(m => m.AssignmentFormComponent) },
      { path: 'drivers', loadComponent: () => import('./pages/drivers/driver-list.component').then(m => m.DriverListComponent) },
      { path: 'drivers/new', loadComponent: () => import('./pages/drivers/driver-form.component').then(m => m.DriverFormComponent) },
      { path: 'drivers/:id/edit', loadComponent: () => import('./pages/drivers/driver-form.component').then(m => m.DriverFormComponent) },
      { path: 'clients', loadComponent: () => import('./pages/clients/client-list.component').then(m => m.ClientListComponent) },
      { path: 'clients/new', loadComponent: () => import('./pages/clients/client-form.component').then(m => m.ClientFormComponent) },
      { path: 'clients/:id/edit', loadComponent: () => import('./pages/clients/client-form.component').then(m => m.ClientFormComponent) }
    ]
  },
  { path: '**', redirectTo: 'dashboard' }
];
