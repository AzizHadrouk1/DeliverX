export type DriverStatus = 'AVAILABLE' | 'ON_DELIVERY' | 'OFF_DUTY' | 'SUSPENDED';

export interface Driver {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  licenseNumber: string;
  status?: DriverStatus;
  vehicleId?: number | null;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export const DRIVER_STATUSES: DriverStatus[] = ['AVAILABLE', 'ON_DELIVERY', 'OFF_DUTY', 'SUSPENDED'];
