import { Component, Input } from '@angular/core';

@Component({
  selector: 'lib-loading-state',
  standalone: true,
  template: `
    <div class="loading">
      <div class="spinner"></div>
      <p>{{ message }}</p>
    </div>
  `,
  styles: [`
    .loading {
      display: grid;
      place-items: center;
      gap: 0.75rem;
      padding: 2rem;
      color: #64748b;
    }

    .spinner {
      width: 2rem;
      height: 2rem;
      border: 3px solid #e2e8f0;
      border-top-color: #2563eb;
      border-radius: 50%;
      animation: spin 0.8s linear infinite;
    }

    @keyframes spin {
      to { transform: rotate(360deg); }
    }
  `]
})
export class LoadingStateComponent {
  @Input() message = 'Loading...';
}
