export enum AlertType {
  LATE = 'LATE',
}

export interface Alert {
  id: number;
  tripId: number;
  vehicleName?: string;
  driverName?: string;
  routeName?: string;
  type: AlertType;
  message: string;
  resolved: boolean;
  createdAt: string;
}
