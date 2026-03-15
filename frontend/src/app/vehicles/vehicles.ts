import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { VehicleService } from '../services/vehicle.service';
import { Vehicle, VehicleStatus } from '../models/vehicle.model';

type FilterValue = 'ALL' | VehicleStatus;

interface VehicleForm {
  label: string;
  name: string;
  licensePlate: string;
  status: VehicleStatus;
  latitude: number | null;
  longitude: number | null;
}

@Component({
  selector: 'app-vehicles',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './vehicles.html',
})
export class VehiclesComponent implements OnInit {
  private readonly svc = inject(VehicleService);

  vehicles = signal<Vehicle[]>([]);
  loading = signal(true);
  saving = signal(false);
  filter = signal<FilterValue>('ALL');
  showModal = signal(false);
  editingId = signal<number | null>(null);
  confirmDeleteId = signal<number | null>(null);

  readonly VehicleStatus = VehicleStatus;
  readonly statusList = Object.values(VehicleStatus);

  formData: VehicleForm = this.blank();

  filteredVehicles = computed(() => {
    const f = this.filter();
    return f === 'ALL'
      ? this.vehicles()
      : this.vehicles().filter(v => v.status === f);
  });

  counts = computed(() => {
    const all = this.vehicles();
    return {
      ALL:         all.length,
      ACTIVE:      all.filter(v => v.status === VehicleStatus.ACTIVE).length,
      IN_TRANSIT:  all.filter(v => v.status === VehicleStatus.IN_TRANSIT).length,
      MAINTENANCE: all.filter(v => v.status === VehicleStatus.MAINTENANCE).length,
      INACTIVE:    all.filter(v => v.status === VehicleStatus.INACTIVE).length,
    };
  });

  private blank(): VehicleForm {
    return { label: '', name: '', licensePlate: '', status: VehicleStatus.ACTIVE, latitude: null, longitude: null };
  }

  ngOnInit() { this.load(); }

  load() {
    this.loading.set(true);
    this.svc.getAll().subscribe({
      next: v => { this.vehicles.set(v); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  openCreate() {
    this.editingId.set(null);
    this.formData = this.blank();
    this.showModal.set(true);
  }

  openEdit(v: Vehicle) {
    this.editingId.set(v.id);
    this.formData = {
      label: v.label,
      name: v.name,
      licensePlate: v.licensePlate,
      status: v.status,
      latitude: v.latitude ?? null,
      longitude: v.longitude ?? null,
    };
    this.showModal.set(true);
  }

  closeModal() {
    this.showModal.set(false);
    this.editingId.set(null);
  }

  isValid(): boolean {
    return !!(this.formData.label?.trim() && this.formData.name?.trim() && this.formData.licensePlate?.trim());
  }

  save() {
    if (!this.isValid() || this.saving()) return;
    this.saving.set(true);
    const id = this.editingId();
    const payload = {
      ...this.formData,
      latitude: this.formData.latitude ?? undefined,
      longitude: this.formData.longitude ?? undefined,
    };
    const req$ = id ? this.svc.update(id, payload) : this.svc.create(payload);
    req$.subscribe({
      next: () => { this.closeModal(); this.load(); this.saving.set(false); },
      error: () => this.saving.set(false),
    });
  }

  confirmDelete(id: number) { this.confirmDeleteId.set(id); }
  cancelDelete() { this.confirmDeleteId.set(null); }

  deleteVehicle(id: number) {
    this.svc.delete(id).subscribe({
      next: () => { this.confirmDeleteId.set(null); this.load(); },
    });
  }

  statusBadge(status: VehicleStatus): string {
    const map: Record<VehicleStatus, string> = {
      [VehicleStatus.ACTIVE]:      'bg-emerald-100 text-emerald-700',
      [VehicleStatus.IN_TRANSIT]:  'bg-blue-100 text-blue-700',
      [VehicleStatus.MAINTENANCE]: 'bg-amber-100 text-amber-700',
      [VehicleStatus.INACTIVE]:    'bg-gray-100 text-gray-600',
    };
    return 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ' +
      (map[status] ?? 'bg-gray-100 text-gray-600');
  }

  statusLabel(s: string): string { return s.replace('_', ' '); }

  filterLabel(f: FilterValue): string {
    return f === 'ALL' ? 'All' : this.statusLabel(f);
  }

  formatTime(d?: string): string {
    if (!d) return '—';
    try {
      return new Date(d).toLocaleString(undefined, { month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit' });
    } catch { return d; }
  }
}
