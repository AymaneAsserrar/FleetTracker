export interface Route {
  id: number;
  name: string;
  description?: string;
  active: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface Stop {
  id: number;
  name: string;
  latitude: number;
  longitude: number;
  sequenceOrder: number;
  routeId: number;
  routeName?: string;
}
