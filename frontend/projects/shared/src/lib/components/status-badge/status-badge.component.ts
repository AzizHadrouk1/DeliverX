import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'lib-status-badge',
  standalone: true,
  imports: [CommonModule],
  template: `
    <span class="badge" [class]="'badge--' + tone">{{ label }}</span>
  `,
  styles: [`
    .badge {
      display: inline-flex;
      align-items: center;
      padding: 0.2rem 0.65rem;
      border-radius: 999px;
      font-size: 0.75rem;
      font-weight: 600;
      letter-spacing: 0.02em;
      text-transform: uppercase;
    }

    .badge--success { background: #dcfce7; color: #166534; }
    .badge--warning { background: #fef3c7; color: #92400e; }
    .badge--danger { background: #fee2e2; color: #991b1b; }
    .badge--info { background: #dbeafe; color: #1d4ed8; }
    .badge--neutral { background: #e5e7eb; color: #374151; }
  `]
})
export class StatusBadgeComponent {
  @Input({ required: true }) label!: string;
  @Input() tone: 'success' | 'warning' | 'danger' | 'info' | 'neutral' = 'neutral';
}
