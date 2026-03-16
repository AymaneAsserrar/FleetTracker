import { Routes } from '@angular/router';
import { LayoutComponent } from './layout/layout';
import { authGuard } from './guards/auth.guard';
import { managerGuard } from './guards/manager.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./login/login').then(m => m.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () => import('./register/register').then(m => m.RegisterComponent),
  },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        canActivate: [managerGuard],
        loadComponent: () => import('./dashboard/dashboard').then(m => m.DashboardComponent),
      },
      {
        path: 'vehicles',
        canActivate: [managerGuard],
        loadComponent: () => import('./vehicles/vehicles').then(m => m.VehiclesComponent),
      },
      {
        path: 'trips',
        canActivate: [managerGuard],
        loadComponent: () => import('./trips/trips').then(m => m.TripsComponent),
      },
      {
        path: 'routes',
        canActivate: [managerGuard],
        loadComponent: () => import('./routes/routes').then(m => m.RoutesComponent),
      },
      {
        path: 'drivers',
        canActivate: [managerGuard],
        loadComponent: () => import('./drivers/drivers').then(m => m.DriversComponent),
      },
      {
        path: 'alerts',
        canActivate: [managerGuard],
        loadComponent: () => import('./alerts/alerts').then(m => m.AlertsComponent),
      },
      {
        path: 'my-trips',
        loadComponent: () => import('./my-trips/my-trips').then(m => m.MyTripsComponent),
      },
    ],
  },
  { path: '**', redirectTo: 'login' },
];
