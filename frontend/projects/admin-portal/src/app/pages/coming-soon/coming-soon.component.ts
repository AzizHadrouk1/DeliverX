import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-coming-soon',
  standalone: true,
  template: `
    <section class="soon">
      <h1>{{ title }}</h1>
      <p>{{ description }}</p>
      <p class="hint">This module will connect to the backend once the corresponding microservice APIs are implemented.</p>
    </section>
  `,
  styles: [`
    .soon {
      max-width: 640px;
      padding: 2rem;
      background: #fff;
      border: 1px dashed #cbd5e1;
      border-radius: 0.85rem;
    }

    .hint {
      color: #64748b;
    }
  `]
})
export class ComingSoonComponent {
  private readonly route = inject(ActivatedRoute);

  protected title = this.route.snapshot.data['title'] as string;
  protected description = this.route.snapshot.data['description'] as string;
}
