import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouteService } from '../services/route.service';
import { Route } from '../models/route.model';

interface RouteForm {
  name: string;
  description: string;
  active: boolean;
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

  formatTime(d?: string): string {
    if (!d) return '—';
    try {
      return new Date(d).toLocaleString(undefined, { month: 'short', day: 'numeric', year: 'numeric' });
    } catch { return d; }
  }
}
