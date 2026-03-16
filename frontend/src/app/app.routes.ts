import { Routes } from '@angular/router';
import { LayoutComponent } from './layout/layout';

export const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () => import('./dashboard/dashboard').then(m => m.DashboardComponent),
      },
      {
        path: 'vehicles',
        loadComponent: () => import('./vehicles/vehicles').then(m => m.VehiclesComponent),
      },
      {
        path: 'trips',
        loadComponent: () => import('./trips/trips').then(m => m.TripsComponent),
      },
      {
        path: 'routes',
        loadComponent: () => import('./routes/routes').then(m => m.RoutesComponent),
      },
      {
        path: 'drivers',
        loadComponent: () => import('./drivers/drivers').then(m => m.DriversComponent),
      },
      {
        path: 'alerts',
        loadComponent: () => import('./alerts/alerts').then(m => m.AlertsComponent),
      },
    ],
  },
  { path: '**', redirectTo: '' },
];
