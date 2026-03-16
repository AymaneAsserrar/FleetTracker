import { Component, signal, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html',
})
export class LoginComponent {
  private readonly auth   = inject(AuthService);
  private readonly router = inject(Router);

  username = '';
  password = '';
  loading  = signal(false);
  error    = signal('');

  submit() {
    if (!this.username.trim() || !this.password.trim()) return;
    this.loading.set(true);
    this.error.set('');

    this.auth.login(this.username, this.password).subscribe({
      next: res => {
        this.loading.set(false);
        // Managers go to the full dashboard; drivers go to their trips
        this.router.navigate([res.isManager ? '/dashboard' : '/my-trips']);
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Invalid username or password.');
      },
    });
  }
}
