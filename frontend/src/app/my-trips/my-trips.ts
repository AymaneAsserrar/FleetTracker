import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { TripService } from '../services/trip.service';
import { AuthService } from '../services/auth.service';
import { Trip, TripStatus } from '../models/trip.model';

@Component({
  selector: 'app-my-trips',
  standalone: true,
  imports: [],
  templateUrl: './my-trips.html',
})
export class MyTripsComponent implements OnInit {
  private readonly tripSvc = inject(TripService);
  readonly auth            = inject(AuthService);

  trips   = signal<Trip[]>([]);
  loading = signal(true);

  activeTrip  = computed(() => this.trips().find(t => t.status === TripStatus.IN_PROGRESS) ?? null);
  upcoming    = computed(() => this.trips().filter(t => t.status === TripStatus.SCHEDULED));
  completed   = computed(() => this.trips().filter(t => t.status === TripStatus.COMPLETED));
  totalTrips  = computed(() => this.trips().length);
  doneCount   = computed(() => this.completed().length);

  ngOnInit() {
    const id = this.auth.driverId();
    if (id) {
      this.tripSvc.getByDriver(id).subscribe({
        next: t => { this.trips.set(t); this.loading.set(false); },
        error: () => this.loading.set(false),
      });
    }
  }

  statusClass(status: TripStatus): string {
    const map: Record<TripStatus, string> = {
      [TripStatus.IN_PROGRESS]: 'bg-blue-100 text-blue-700',
      [TripStatus.SCHEDULED]:   'bg-yellow-100 text-yellow-700',
      [TripStatus.COMPLETED]:   'bg-green-100 text-green-700',
      [TripStatus.CANCELLED]:   'bg-red-100 text-red-700',
    };
    return 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ' + (map[status] ?? '');
  }

  statusLabel(status: TripStatus): string {
    return status.replace('_', ' ');
  }

  formatDate(iso: string): string {
    return new Date(iso).toLocaleString(undefined, {
      month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit',
    });
  }
}
