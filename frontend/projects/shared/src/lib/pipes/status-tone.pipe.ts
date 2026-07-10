import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'statusTone',
  standalone: true
})
export class StatusTonePipe implements PipeTransform {
  transform(status: string): 'success' | 'warning' | 'danger' | 'info' | 'neutral' {
    const normalized = status?.toUpperCase() ?? '';

    if (['UP', 'AVAILABLE', 'DELIVERED', 'READY', 'COMPLETED'].includes(normalized)) {
      return 'success';
    }
    if (['IN_TRANSIT', 'IN_USE', 'ASSIGNED', 'IN_PROGRESS', 'ON_DELIVERY'].includes(normalized)) {
      return 'info';
    }
    if (['MAINTENANCE', 'IN_WAREHOUSE', 'CREATED', 'PENDING', 'PICKED_UP', 'OFF_DUTY'].includes(normalized)) {
      return 'warning';
    }
    if (['OUT_OF_SERVICE', 'RETURNED', 'CANCELLED', 'FAILED', 'SUSPENDED'].includes(normalized)) {
      return 'danger';
    }
    return 'neutral';
  }
}
