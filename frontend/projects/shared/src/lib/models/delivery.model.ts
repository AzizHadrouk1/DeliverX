import { Package } from './package.model';

export interface DeliveryPackageResponse {
  deliveryService: string;
  message: string;
  package: Package;
  communication: string;
}
