export type AssignmentStatus = 'ASSIGNED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';

export interface Assignment {
  id?: number;
  deliveryId: number;
  driverId: number;
  vehicleId: number;
  status?: AssignmentStatus;
  assignedAt?: string | null;
  updatedAt?: string | null;
}

export const ASSIGNMENT_STATUSES: AssignmentStatus[] = ['ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'];

// Shapes returned by GET /api/assignments/{id}/details — the assignment enriched
// with live data fetched synchronously (OpenFeign) from the other services.
export interface AssignmentDriverInfo {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  status: string;
}

export interface AssignmentVehicleInfo {
  id: number;
  licensePlate: string;
  brand: string;
  model: string;
  status: string;
}

export interface AssignmentDeliveryInfo {
  id: number;
  packageId: number;
  pickupAddress: string;
  deliveryAddress: string;
  status: string;
}

export interface AssignmentDetails {
  assignment: Assignment;
  driver: AssignmentDriverInfo;
  vehicle: AssignmentVehicleInfo;
  delivery: AssignmentDeliveryInfo;
}
