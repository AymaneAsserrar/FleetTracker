import { Component, signal, inject, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../services/auth.service';

interface RegisterForm {
  name: string;
  age: number | null;
  licenceNumber: string;
  contactPhone: string;
  username: string;
  password: string;
  confirmPassword: string;
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './register.html',
})
export class RegisterComponent {
  private readonly auth   = inject(AuthService);
  private readonly router = inject(Router);

  form: RegisterForm = {
    name: '', age: null, licenceNumber: '',
    contactPhone: '', username: '', password: '', confirmPassword: '',
  };

  loading  = signal(false);
  error    = signal('');
  showPass = signal(false);
  showConfirm = signal(false);

  passwordsMatch = computed(() =>
    !this.form.confirmPassword || this.form.password === this.form.confirmPassword
  );

  isValid(): boolean {
    return !!(
      this.form.name.trim() &&
      this.form.age !== null && this.form.age >= 18 &&
      this.form.licenceNumber.trim() &&
      this.form.contactPhone.trim() &&
      this.form.username.trim() &&
      this.form.password.length >= 6 &&
      this.form.password === this.form.confirmPassword
    );
  }

  submit() {
    if (!this.isValid() || this.loading()) return;
    this.loading.set(true);
    this.error.set('');

    this.auth.register({
      name:          this.form.name.trim(),
      age:           this.form.age!,
      licenceNumber: this.form.licenceNumber.trim(),
      contactPhone:  this.form.contactPhone.trim(),
      username:      this.form.username.trim(),
      password:      this.form.password,
    }).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/my-trips']);
      },
      error: (err) => {
        this.loading.set(false);
        const msg = err?.error?.message ?? err?.error ?? '';
        this.error.set(typeof msg === 'string' && msg ? msg : 'Registration failed. Please try again.');
      },
    });
  }
}
