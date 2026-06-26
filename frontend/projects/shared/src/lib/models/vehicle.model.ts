export type VehicleType = 'VAN' | 'TRUCK' | 'MOTORCYCLE' | 'CAR' | 'BICYCLE';
export type VehicleStatus = 'AVAILABLE' | 'IN_USE' | 'MAINTENANCE' | 'OUT_OF_SERVICE';

export interface Vehicle {
  id?: number;
  licensePlate: string;
  brand: string;
  model: string;
  type: VehicleType;
  status: VehicleStatus;
  maxWeightCapacity: number;
  maxVolumeCapacity: number;
  manufacturingYear: number;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export const VEHICLE_TYPES: VehicleType[] = ['VAN', 'TRUCK', 'MOTORCYCLE', 'CAR', 'BICYCLE'];
export const VEHICLE_STATUSES: VehicleStatus[] = ['AVAILABLE', 'IN_USE', 'MAINTENANCE', 'OUT_OF_SERVICE'];
