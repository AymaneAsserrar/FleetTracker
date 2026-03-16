import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouteService } from '../services/route.service';
import { Route, Stop } from '../models/route.model';

interface RouteForm {
  name: string;
  description: string;
  active: boolean;
}

interface StopForm {
  name: string;
  latitude: string;
  longitude: string;
  sequenceOrder: string;
}

@Component({
  selector: 'app-routes',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './routes.html',
})
export class RoutesComponent implements OnInit {
  private readonly svc = inject(RouteService);

  routes = signal<Route[]>([]);
  loading = signal(true);
  saving = signal(false);
  showActiveOnly = signal(false);
  showModal = signal(false);
  editingId = signal<number | null>(null);
  confirmDeleteId = signal<number | null>(null);

  // Stops panel state
  expandedRouteId = signal<number | null>(null);
  routeStops = signal<Stop[]>([]);
  stopsLoading = signal(false);
  confirmDeleteStopId = signal<number | null>(null);
  showStopForm = signal(false);
  savingStop = signal(false);

  stopForm: StopForm = this.blankStop();

  formData: RouteForm = this.blank();

  filteredRoutes = computed(() =>
    this.showActiveOnly()
      ? this.routes().filter(r => r.active)
      : this.routes()
  );

  counts = computed(() => ({
    total: this.routes().length,
    active: this.routes().filter(r => r.active).length,
    inactive: this.routes().filter(r => !r.active).length,
  }));

  private blank(): RouteForm {
    return { name: '', description: '', active: true };
  }

  private blankStop(): StopForm {
    return { name: '', latitude: '', longitude: '', sequenceOrder: '' };
  }

  ngOnInit() { this.load(); }

  load() {
    this.loading.set(true);
    this.svc.getAll().subscribe({
      next: r => { this.routes.set(r); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  openCreate() {
    this.editingId.set(null);
    this.formData = this.blank();
    this.showModal.set(true);
  }

  openEdit(r: Route) {
    this.editingId.set(r.id);
    this.formData = { name: r.name, description: r.description ?? '', active: r.active };
    this.showModal.set(true);
  }

  closeModal() {
    this.showModal.set(false);
    this.editingId.set(null);
  }

  isValid(): boolean {
    return !!this.formData.name?.trim();
  }

  save() {
    if (!this.isValid() || this.saving()) return;
    this.saving.set(true);
    const id = this.editingId();
    const req$ = id ? this.svc.update(id, this.formData) : this.svc.create(this.formData);
    req$.subscribe({
      next: () => { this.closeModal(); this.load(); this.saving.set(false); },
      error: () => this.saving.set(false),
    });
  }

  confirmDelete(id: number) { this.confirmDeleteId.set(id); }
  cancelDelete() { this.confirmDeleteId.set(null); }

  deleteRoute(id: number) {
    this.svc.delete(id).subscribe({
      next: () => { this.confirmDeleteId.set(null); this.load(); },
    });
  }

  // ── Stops ─────────────────────────────────────────────────────────────

  toggleStops(routeId: number) {
    if (this.expandedRouteId() === routeId) {
      this.expandedRouteId.set(null);
      this.routeStops.set([]);
      this.showStopForm.set(false);
    } else {
      this.expandedRouteId.set(routeId);
      this.showStopForm.set(false);
      this.stopForm = this.blankStop();
      this.loadStops(routeId);
    }
  }

  private loadStops(routeId: number) {
    this.stopsLoading.set(true);
    this.svc.getStopsByRoute(routeId).subscribe({
      next: stops => { this.routeStops.set(stops); this.stopsLoading.set(false); },
      error: () => this.stopsLoading.set(false),
    });
  }

  openStopForm() {
    const stops = this.routeStops();
    const next = stops.length > 0 ? Math.max(...stops.map(s => s.sequenceOrder)) + 1 : 1;
    this.stopForm = { name: '', latitude: '', longitude: '', sequenceOrder: String(next) };
    this.showStopForm.set(true);
  }

  cancelStopForm() {
    this.showStopForm.set(false);
    this.stopForm = this.blankStop();
  }

  isStopValid(): boolean {
    const lat = parseFloat(this.stopForm.latitude);
    const lng = parseFloat(this.stopForm.longitude);
    const seq = parseInt(this.stopForm.sequenceOrder, 10);
    return (
      !!this.stopForm.name.trim() &&
      !isNaN(lat) && lat >= -90 && lat <= 90 &&
      !isNaN(lng) && lng >= -180 && lng <= 180 &&
      !isNaN(seq) && seq >= 1
    );
  }

  saveStop() {
    const routeId = this.expandedRouteId();
    if (!routeId || !this.isStopValid() || this.savingStop()) return;
    this.savingStop.set(true);
    this.svc.createStop({
      name: this.stopForm.name.trim(),
      latitude: parseFloat(this.stopForm.latitude),
      longitude: parseFloat(this.stopForm.longitude),
      sequenceOrder: parseInt(this.stopForm.sequenceOrder, 10),
      routeId,
    }).subscribe({
      next: () => {
        this.savingStop.set(false);
        this.showStopForm.set(false);
        this.stopForm = this.blankStop();
        this.loadStops(routeId);
      },
      error: () => this.savingStop.set(false),
    });
  }

  confirmDeleteStop(id: number) { this.confirmDeleteStopId.set(id); }
  cancelDeleteStop() { this.confirmDeleteStopId.set(null); }

  deleteStop(id: number) {
    this.svc.deleteStop(id).subscribe({
      next: () => {
        this.confirmDeleteStopId.set(null);
        const routeId = this.expandedRouteId();
        if (routeId) this.loadStops(routeId);
      },
    });
  }

  formatTime(d?: string): string {
    if (!d) return '—';
    try {
      return new Date(d).toLocaleString(undefined, { month: 'short', day: 'numeric', year: 'numeric' });
    } catch { return d; }
  }
}
