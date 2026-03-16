import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlertService } from '../services/alert.service';
import { Alert } from '../models/alert.model';

@Component({
  selector: 'app-alerts',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './alerts.html',
})
export class AlertsComponent implements OnInit {
  private readonly alertSvc = inject(AlertService);

  alerts = signal<Alert[]>([]);
  loading = signal(true);
  showResolved = signal(false);

  filtered = computed(() =>
    this.showResolved()
      ? this.alerts()
      : this.alerts().filter(a => !a.resolved)
  );

  unresolvedCount = computed(() => this.alerts().filter(a => !a.resolved).length);

  ngOnInit() {
    this.load();
  }

  load() {
    this.loading.set(true);
    this.alertSvc.getAll().subscribe({
      next: data => {
        this.alerts.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  resolve(alert: Alert) {
    this.alertSvc.resolve(alert.id).subscribe({
      next: () => this.load(),
    });
  }

  toggleShowResolved() {
    this.showResolved.update(v => !v);
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleString();
  }
}
