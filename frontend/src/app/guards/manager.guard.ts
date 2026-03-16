import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const managerGuard: CanActivateFn = () => {
  const auth   = inject(AuthService);
  const router = inject(Router);
  if (auth.isManager()) return true;
  // Drivers are redirected to their own dashboard
  return router.createUrlTree(['/my-trips']);
};
