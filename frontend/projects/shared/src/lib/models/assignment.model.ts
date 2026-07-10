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
