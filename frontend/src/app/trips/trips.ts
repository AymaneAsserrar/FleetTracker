import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { TripService } from '../services/trip.service';
import { VehicleService } from '../services/vehicle.service';
import { RouteService } from '../services/route.service';
import { DriverService } from '../services/driver.service';
import { Trip, TripStatus } from '../models/trip.model';
import { Vehicle } from '../models/vehicle.model';
import { Route } from '../models/route.model';
import { Driver } from '../models/driver.model';

type FilterValue = 'ALL' | TripStatus;

interface TripForm {
  vehicleId: number | null;
  routeId: number | null;
  driverId: number | null;
  startTime: string;
  endTime: string;
  status: TripStatus;
}

@Component({
  selector: 'app-trips',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './trips.html',
})
export class TripsComponent implements OnInit {
  private readonly tripSvc = inject(TripService);
  private readonly vehicleSvc = inject(VehicleService);
  private readonly routeSvc = inject(RouteService);
  private readonly driverSvc = inject(DriverService);

  trips = signal<Trip[]>([]);
  vehicles = signal<Vehicle[]>([]);
  routes = signal<Route[]>([]);
  drivers = signal<Driver[]>([]);
  loading = signal(true);
  saving = signal(false);
  filter = signal<FilterValue>('ALL');
  showModal = signal(false);
  editingId = signal<number | null>(null);
  confirmDeleteId = signal<number | null>(null);

  readonly TripStatus = TripStatus;
  readonly statusList = Object.values(TripStatus);

  formData: TripForm = this.blank();

  filteredTrips = computed(() => {
    const f = this.filter();
    return f === 'ALL' ? this.trips() : this.trips().filter(t => t.status === f);
  });

  counts = computed(() => {
    const all = this.trips();
    return {
      ALL:         all.length,
      SCHEDULED:   all.filter(t => t.status === TripStatus.SCHEDULED).length,
      IN_PROGRESS: all.filter(t => t.status === TripStatus.IN_PROGRESS).length,
      COMPLETED:   all.filter(t => t.status === TripStatus.COMPLETED).length,
      CANCELLED:   all.filter(t => t.status === TripStatus.CANCELLED).length,
    };
  });

  private blank(): TripForm {
    return { vehicleId: null, routeId: null, driverId: null, startTime: '', endTime: '', status: TripStatus.SCHEDULED };
  }

  ngOnInit() { this.load(); }

  load() {
    this.loading.set(true);
    forkJoin({
      trips: this.tripSvc.getAll(),
      vehicles: this.vehicleSvc.getAll(),
      routes: this.routeSvc.getAll(),
      drivers: this.driverSvc.getAll(),
    }).subscribe({
      next: ({ trips, vehicles, routes, drivers }) => {
        this.trips.set(trips);
        this.vehicles.set(vehicles);
        this.routes.set(routes);
        this.drivers.set(drivers);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  openCreate() {
    this.editingId.set(null);
    this.formData = this.blank();
    this.showModal.set(true);
  }

  openEdit(t: Trip) {
    this.editingId.set(t.id);
    this.formData = {
      vehicleId: t.vehicleId,
      routeId: t.routeId,
      driverId: t.driverId ?? null,
      startTime: this.toDatetimeLocal(t.startTime),
      endTime: this.toDatetimeLocal(t.endTime),
      status: t.status,
    };
    this.showModal.set(true);
  }

  closeModal() {
    this.showModal.set(false);
    this.editingId.set(null);
  }

  isValid(): boolean {
    return !!(this.formData.vehicleId && this.formData.routeId && this.formData.startTime);
  }

  save() {
    if (!this.isValid() || this.saving()) return;
    this.saving.set(true);
    const payload = {
      vehicleId: Number(this.formData.vehicleId),
      routeId: Number(this.formData.routeId),
      driverId: this.formData.driverId ? Number(this.formData.driverId) : undefined,
      startTime: this.fromDatetimeLocal(this.formData.startTime),
      endTime: this.formData.endTime ? this.fromDatetimeLocal(this.formData.endTime) : undefined,
      status: this.formData.status,
    };
    const id = this.editingId();
    const req$ = id ? this.tripSvc.update(id, payload) : this.tripSvc.create(payload);
    req$.subscribe({
      next: () => { this.closeModal(); this.load(); this.saving.set(false); },
      error: () => this.saving.set(false),
    });
  }

  quickStatus(trip: Trip, status: TripStatus) {
    this.tripSvc.updateStatus(trip.id, status).subscribe({
      next: () => this.load(),
    });
  }

  confirmDelete(id: number) { this.confirmDeleteId.set(id); }
  cancelDelete() { this.confirmDeleteId.set(null); }

  deleteTrip(id: number) {
    this.tripSvc.delete(id).subscribe({
      next: () => { this.confirmDeleteId.set(null); this.load(); },
    });
  }

  private toDatetimeLocal(iso?: string): string {
    if (!iso) return '';
    try { return new Date(iso).toISOString().slice(0, 16); } catch { return ''; }
  }

  private fromDatetimeLocal(value: string): string {
    return value ? value + ':00' : '';
  }

  statusBadge(status: TripStatus): string {
    const map: Record<TripStatus, string> = {
      [TripStatus.IN_PROGRESS]: 'bg-blue-100 text-blue-700',
      [TripStatus.SCHEDULED]:   'bg-violet-100 text-violet-700',
      [TripStatus.COMPLETED]:   'bg-emerald-100 text-emerald-700',
      [TripStatus.CANCELLED]:   'bg-red-100 text-red-700',
    };
    return 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ' +
      (map[status] ?? 'bg-gray-100 text-gray-600');
  }

  statusLabel(s: string): string { return s.replace('_', ' '); }

  formatTime(d?: string): string {
    if (!d) return '—';
    try {
      return new Date(d).toLocaleString(undefined, { month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit' });
    } catch { return d; }
  }
}
