export type ClientType = 'INDIVIDUAL' | 'BUSINESS';
export type ClientStatus = 'ACTIVE' | 'INACTIVE';

export interface Client {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  companyName?: string | null;
  address: string;
  city: string;
  type?: ClientType;
  status?: ClientStatus;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export const CLIENT_TYPES: ClientType[] = ['INDIVIDUAL', 'BUSINESS'];
export const CLIENT_STATUSES: ClientStatus[] = ['ACTIVE', 'INACTIVE'];
