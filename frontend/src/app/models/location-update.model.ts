export interface LocationUpdate {
  id: number;
  vehicleId: number;
  vehicleName?: string;
  latitude: number;
  longitude: number;
  speed?: number;
  heading?: number;
  recordedAt: string;
}
