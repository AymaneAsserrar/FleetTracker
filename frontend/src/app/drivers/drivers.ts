import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DriverService } from '../services/driver.service';
import { Driver } from '../models/driver.model';

type FilterValue = 'ALL' | 'MANAGERS' | 'DRIVERS';

interface DriverForm {
  name: string;
  age: number | null;
  licenceNumber: string;
  isManager: boolean;
}

@Component({
  selector: 'app-drivers',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './drivers.html',
})
export class DriversComponent implements OnInit {
  private readonly svc = inject(DriverService);

  drivers = signal<Driver[]>([]);
  loading = signal(true);
  saving = signal(false);
  filter = signal<FilterValue>('ALL');
  showModal = signal(false);
  editingId = signal<number | null>(null);
  confirmDeleteId = signal<number | null>(null);

  formData: DriverForm = this.blank();

  filteredDrivers = computed(() => {
    const f = this.filter();
    const all = this.drivers();
    if (f === 'MANAGERS') return all.filter(d => d.isManager);
    if (f === 'DRIVERS') return all.filter(d => !d.isManager);
    return all;
  });

  counts = computed(() => {
    const all = this.drivers();
    return {
      ALL:      all.length,
      MANAGERS: all.filter(d => d.isManager).length,
      DRIVERS:  all.filter(d => !d.isManager).length,
    };
  });

  private blank(): DriverForm {
    return { name: '', age: null, licenceNumber: '', isManager: false };
  }

  ngOnInit() { this.load(); }

  load() {
    this.loading.set(true);
    this.svc.getAll().subscribe({
      next: d => { this.drivers.set(d); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  openCreate() {
    this.editingId.set(null);
    this.formData = this.blank();
    this.showModal.set(true);
  }

  openEdit(d: Driver) {
    this.editingId.set(d.id);
    this.formData = {
      name: d.name,
      age: d.age,
      licenceNumber: d.licenceNumber,
      isManager: d.isManager,
    };
    this.showModal.set(true);
  }

  closeModal() {
    this.showModal.set(false);
    this.editingId.set(null);
  }

  isValid(): boolean {
    return !!(this.formData.name?.trim() && this.formData.licenceNumber?.trim() && this.formData.age !== null && this.formData.age > 0);
  }

  save() {
    if (!this.isValid() || this.saving()) return;
    this.saving.set(true);
    const id = this.editingId();
    const payload = { ...this.formData, age: this.formData.age ?? undefined };
    const req$ = id ? this.svc.update(id, payload) : this.svc.create(payload);
    req$.subscribe({
      next: () => { this.closeModal(); this.load(); this.saving.set(false); },
      error: () => this.saving.set(false),
    });
  }

  confirmDelete(id: number) { this.confirmDeleteId.set(id); }
  cancelDelete() { this.confirmDeleteId.set(null); }

  deleteDriver(id: number) {
    this.svc.delete(id).subscribe({
      next: () => { this.confirmDeleteId.set(null); this.load(); },
    });
  }

  roleBadge(isManager: boolean): string {
    return 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ' +
      (isManager ? 'bg-indigo-100 text-indigo-700' : 'bg-gray-100 text-gray-600');
  }
}
