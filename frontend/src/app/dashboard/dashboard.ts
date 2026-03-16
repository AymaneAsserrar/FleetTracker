import {
  Component,
  OnInit,
  OnDestroy,
  AfterViewInit,
  ViewChild,
  ElementRef,
  inject,
  signal,
  computed,
} from '@angular/core';
import { Subject, forkJoin } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import * as L from 'leaflet';

import { VehicleService } from '../services/vehicle.service';
import { TripService } from '../services/trip.service';
import { DriverService } from '../services/driver.service';
import { Vehicle, VehicleStatus } from '../models/vehicle.model';
import { Trip, TripStatus } from '../models/trip.model';
import { Driver } from '../models/driver.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [],
  templateUrl: './dashboard.html',
})
export class DashboardComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('mapContainer') mapContainer!: ElementRef<HTMLDivElement>;

  private readonly vehicleService = inject(VehicleService);
  private readonly tripService = inject(TripService);
  private readonly driverService = inject(DriverService);
  private readonly destroy$ = new Subject<void>();

  vehicles = signal<Vehicle[]>([]);
  trips = signal<Trip[]>([]);
  drivers = signal<Driver[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  private map?: L.Map;
  private markers = new Map<number, L.Marker>();
  private routePolyline?: L.Polyline;
  private routeStartMarker?: L.Marker;
  private routeEndMarker?: L.Marker;
  private mapReady = false;
  private dataReady = false;

  readonly VehicleStatus = VehicleStatus;
  readonly TripStatus = TripStatus;

  totalVehicles = computed(() => this.vehicles().length);
  activeVehicles = computed(() =>
    this.vehicles().filter(v => v.status === VehicleStatus.ACTIVE).length
  );
  inTransitVehicles = computed(() =>
    this.vehicles().filter(v => v.status === VehicleStatus.IN_TRANSIT).length
  );
  maintenanceVehicles = computed(() =>
    this.vehicles().filter(v => v.status === VehicleStatus.MAINTENANCE).length
  );
  totalDrivers = computed(() => this.drivers().length);
  totalManagers = computed(() => this.drivers().filter(d => d.isManager).length);
  activeTrips = computed(() =>
    this.trips().filter(t => t.status === TripStatus.IN_PROGRESS)
  );
  scheduledTrips = computed(() =>
    this.trips().filter(t => t.status === TripStatus.SCHEDULED).length
  );
  recentTrips = computed(() => [...this.trips()].slice(0, 10));

  selectedTripId = signal<number | null>(null);

  ngOnInit(): void {
    this.loadData();
  }

  ngAfterViewInit(): void {
    this.initMap();
    this.mapReady = true;
    if (this.dataReady) {
      this.updateMapMarkers();
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.clearRoute();
    this.map?.remove();
  }

  private initMap(): void {
    this.map = L.map(this.mapContainer.nativeElement, {
      center: [20, 10],
      zoom: 2,
      zoomControl: true,
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
      maxZoom: 19,
    }).addTo(this.map);
  }

  private loadData(): void {
    this.loading.set(true);
    this.error.set(null);

    forkJoin({
      vehicles: this.vehicleService.getAll(),
      trips: this.tripService.getAll(),
      drivers: this.driverService.getAll(),
    })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: ({ vehicles, trips, drivers }) => {
          this.vehicles.set(vehicles);
          this.trips.set(trips);
          this.drivers.set(drivers);
          this.loading.set(false);
          this.dataReady = true;
          if (this.mapReady) {
            this.updateMapMarkers();
          }
        },
        error: err => {
          this.error.set(
            'Could not connect to the server. Make sure the backend is running on port 8080.'
          );
          this.loading.set(false);
          console.error('Dashboard load error:', err);
        },
      });
  }

  private updateMapMarkers(): void {
    if (!this.map) return;

    this.markers.forEach(m => m.remove());
    this.markers.clear();

    const statusColors: Record<VehicleStatus, string> = {
      [VehicleStatus.ACTIVE]: '#10b981',
      [VehicleStatus.IN_TRANSIT]: '#3b82f6',
      [VehicleStatus.MAINTENANCE]: '#f59e0b',
      [VehicleStatus.INACTIVE]: '#9ca3af',
    };

    const located = this.vehicles().filter(
      v => v.latitude != null && v.longitude != null
    );

    located.forEach(v => {
      const color = statusColors[v.status] ?? '#9ca3af';
      const icon = L.divIcon({
        html: `<div style="width:13px;height:13px;border-radius:50%;background:${color};border:2.5px solid #fff;box-shadow:0 2px 6px rgba(0,0,0,.35)"></div>`,
        className: '',
        iconSize: [13, 13],
        iconAnchor: [6, 6],
        popupAnchor: [0, -10],
      });

      const marker = L.marker([v.latitude!, v.longitude!], { icon })
        .bindPopup(
          `<div style="font:13px/1.6 system-ui,sans-serif;min-width:140px">` +
          `<strong style="font-size:14px">${v.name}</strong><br>` +
          `<span style="color:#6b7280;font-size:11px">${v.licensePlate}</span><br>` +
          `<span style="display:inline-block;margin-top:5px;padding:2px 8px;border-radius:4px;` +
          `font-size:11px;font-weight:600;background:${color}22;color:${color}">` +
          `${v.status.replace('_', ' ')}</span></div>`
        )
        .addTo(this.map!);

      this.markers.set(v.id, marker);
    });

    if (located.length > 0) {
      const group = L.featureGroup(Array.from(this.markers.values()));
      this.map.fitBounds(group.getBounds().pad(0.3));
    }
  }

  refresh(): void {
    this.clearRoute();
    this.selectedTripId.set(null);
    this.loadData();
  }

  focusTrip(trip: Trip): void {
    this.selectedTripId.set(trip.id);
    const vehicle = this.vehicles().find(v => v.id === trip.vehicleId);

    this.clearRoute();

    if (!this.map) return;

    const hasStart = trip.startLatitude != null && trip.startLongitude != null;
    const hasEnd   = trip.endLatitude   != null && trip.endLongitude   != null;

    if (hasStart && hasEnd) {
      const startLatLng: L.LatLngTuple = [trip.startLatitude!, trip.startLongitude!];
      const endLatLng:   L.LatLngTuple = [trip.endLatitude!,   trip.endLongitude!];

      const latlngs: L.LatLngTuple[] = [startLatLng];
      if (vehicle?.latitude && vehicle?.longitude) {
        latlngs.push([vehicle.latitude, vehicle.longitude]);
      }
      latlngs.push(endLatLng);

      this.routePolyline = L.polyline(latlngs, {
        color: '#6366f1',
        weight: 4,
        opacity: 0.8,
        dashArray: '8 4',
      }).addTo(this.map);

      this.routeStartMarker = L.marker(startLatLng, {
        icon: L.divIcon({
          html: `<div style="width:14px;height:14px;border-radius:50%;background:#22c55e;border:3px solid #fff;box-shadow:0 2px 6px rgba(0,0,0,.4)"></div>`,
          className: '',
          iconSize: [14, 14],
          iconAnchor: [7, 7],
          popupAnchor: [0, -10],
        }),
      })
        .bindPopup(`<div style="font:13px/1.6 system-ui,sans-serif"><strong>Start</strong></div>`)
        .addTo(this.map);

      this.routeEndMarker = L.marker(endLatLng, {
        icon: L.divIcon({
          html: `<div style="width:14px;height:14px;border-radius:50%;background:#ef4444;border:3px solid #fff;box-shadow:0 2px 6px rgba(0,0,0,.4)"></div>`,
          className: '',
          iconSize: [14, 14],
          iconAnchor: [7, 7],
          popupAnchor: [0, -10],
        }),
      })
        .bindPopup(`<div style="font:13px/1.6 system-ui,sans-serif"><strong>End</strong></div>`)
        .addTo(this.map);

      const bounds = this.routePolyline.getBounds();
      this.map.fitBounds(bounds.pad(0.3));

      const marker = vehicle ? this.markers.get(vehicle.id) : undefined;
      if (marker) setTimeout(() => marker.openPopup(), 800);

    } else if (vehicle?.latitude && vehicle?.longitude) {
      // No coordinates on trip — just fly to the vehicle
      this.map.flyTo([vehicle.latitude, vehicle.longitude], 14, { duration: 1.2 });
      const marker = this.markers.get(vehicle.id);
      if (marker) setTimeout(() => marker.openPopup(), 1300);
    }
  }

  private clearRoute(): void {
    this.routePolyline?.remove();
    this.routeStartMarker?.remove();
    this.routeEndMarker?.remove();
    this.routePolyline = undefined;
    this.routeStartMarker = undefined;
    this.routeEndMarker = undefined;
  }

  tripBadgeClass(status: TripStatus): string {
    const map: Record<TripStatus, string> = {
      [TripStatus.IN_PROGRESS]: 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-700',
      [TripStatus.SCHEDULED]:   'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-violet-100 text-violet-700',
      [TripStatus.COMPLETED]:   'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-emerald-100 text-emerald-700',
      [TripStatus.CANCELLED]:   'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-700',
    };
    return map[status] ?? 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-600';
  }

  formatTime(dateStr?: string): string {
    if (!dateStr) return '—';
    try {
      return new Date(dateStr).toLocaleString(undefined, {
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      });
    } catch {
      return dateStr;
    }
  }

  tripStatusLabel(status: TripStatus): string {
    return status.replace('_', ' ');
  }
}
