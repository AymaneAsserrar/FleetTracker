export enum VehicleStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  MAINTENANCE = 'MAINTENANCE',
  IN_TRANSIT = 'IN_TRANSIT',
}

export interface Vehicle {
  id: number;
  label: string;
  name: string;
  licensePlate: string;
  latitude?: number;
  longitude?: number;
  status: VehicleStatus;
  lastUpdated?: string;
}
