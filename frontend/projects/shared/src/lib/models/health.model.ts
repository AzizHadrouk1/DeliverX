export interface HealthStatus {
  status: string;
  service: string;
}

export interface ServiceHealth {
  name: string;
  path: string;
  status?: HealthStatus;
  online: boolean;
  error?: string;
}
