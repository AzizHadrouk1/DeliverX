export type PackageMgmtStatus = 'READY' | 'IN_TRANSIT' | 'DELIVERED' | 'CREATED' | 'IN_WAREHOUSE' | 'ASSIGNED' | 'RETURNED';

export interface Package {
  id: number;
  trackingNumber: string;
  weight: number;
  destination: string;
  status: string;
}
