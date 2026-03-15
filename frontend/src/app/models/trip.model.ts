export enum TripStatus {
  SCHEDULED = 'SCHEDULED',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
}

export interface Trip {
  id: number;
  vehicleId: number;
  vehicleName?: string;
  routeId: number;
  routeName?: string;
  startTime: string;
  endTime?: string;
  status: TripStatus;
  createdAt: string;
}
