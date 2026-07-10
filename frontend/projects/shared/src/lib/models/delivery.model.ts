import { Package } from './package.model';

export interface DeliveryPackageResponse {
  deliveryService: string;
  message: string;
  package: Package;
  communication: string;
}

export type DeliveryStatus = 'PENDING' | 'ASSIGNED' | 'PICKED_UP' | 'IN_PROGRESS' | 'DELIVERED' | 'FAILED' | 'CANCELLED';

export interface Delivery {
  id: number;
  packageId: number;
  clientId: number;
  driverId?: number | null;
  vehicleId?: number | null;
  pickupAddress: string;
  deliveryAddress: string;
  scheduledDate: string;
  actualDeliveryDate?: string | null;
  status: DeliveryStatus;
  createdAt?: string | null;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  last: boolean;
}
