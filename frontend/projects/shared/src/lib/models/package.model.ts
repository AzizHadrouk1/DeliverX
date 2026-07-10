export type PackageMgmtStatus =
  | 'CREATED'
  | 'PICKED_UP'
  | 'IN_TRANSIT'
  | 'OUT_FOR_DELIVERY'
  | 'DELIVERED'
  | 'FAILED'
  | 'RETURNED';

export const PACKAGE_STATUSES: PackageMgmtStatus[] = [
  'CREATED',
  'PICKED_UP',
  'IN_TRANSIT',
  'OUT_FOR_DELIVERY',
  'DELIVERED',
  'FAILED',
  'RETURNED'
];

export interface Package {
  id?: number;
  trackingNumber?: string;
  weight: number;
  width?: number | null;
  height?: number | null;
  depth?: number | null;
  description?: string | null;
  status: PackageMgmtStatus | string;
  clientId: number;
  createdAt?: string;
  /** Feign/legacy compatibility; may be null */
  destination?: string | null;
}

export interface PackageStatusHistory {
  id: number;
  packageId: number;
  status: PackageMgmtStatus | string;
  timestamp: string;
  comment?: string | null;
}

export interface UpdatePackageStatusRequest {
  status: PackageMgmtStatus;
  comment?: string;
}
