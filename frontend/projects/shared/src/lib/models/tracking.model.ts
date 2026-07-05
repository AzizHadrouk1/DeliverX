export type PackageStatus =
  | 'IN_TRANSIT'
  | 'OUT_FOR_DELIVERY'
  | 'AT_SORTING_FACILITY'
  | 'DELIVERED'
  | 'FAILED'
  | 'RETURNED';

export interface TrackingEvent {
  id: string;
  deliveryId: string;
  latitude: number;
  longitude: number;
  speed: number | null;
  heading: number | null;
  status: PackageStatus;
  notes: string | null;
  timestamp: string;
}

export interface EtaResponse {
  deliveryId: string;
  etaMinutes: number;
  distanceKm: number;
  currentLatitude: number;
  currentLongitude: number;
  destinationLatitude: number;
  destinationLongitude: number;
  speedKmh: number;
  estimatedArrival: string;
}

export interface LiveTrackingMessage {
  eventType: 'LOCATION_UPDATE' | 'STATUS_UPDATE' | 'ETA_UPDATE';
  deliveryId: string;
  latitude: number;
  longitude: number;
  speed: number | null;
  heading: number | null;
  status: PackageStatus;
  notes: string | null;
  etaMinutes: number | null;
  timestamp: string;
}

export interface RouteWaypoint {
  order: number;
  latitude: number;
  longitude: number;
  label: string;
  distanceFromPreviousKm: number;
}

export interface RouteOptimizationResponse {
  deliveryId: string;
  optimizedWaypoints: RouteWaypoint[];
  totalDistanceKm: number;
}

export const STATUS_LABELS: Record<PackageStatus, string> = {
  IN_TRANSIT: 'In Transit',
  OUT_FOR_DELIVERY: 'Out for Delivery',
  AT_SORTING_FACILITY: 'At Sorting Facility',
  DELIVERED: 'Delivered',
  FAILED: 'Delivery Failed',
  RETURNED: 'Returned'
};

export const STATUS_ICONS: Record<PackageStatus, string> = {
  IN_TRANSIT: '🚚',
  OUT_FOR_DELIVERY: '📦',
  AT_SORTING_FACILITY: '🏭',
  DELIVERED: '✅',
  FAILED: '❌',
  RETURNED: '↩️'
};
