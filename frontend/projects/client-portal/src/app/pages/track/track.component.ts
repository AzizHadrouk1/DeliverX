import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import {
  DeliveryApiService,
  DeliveryPackageResponse,
  LoadingStateComponent,
  Package,
  PackageApiService,
  StatusBadgeComponent,
  StatusTonePipe
} from 'shared';

@Component({
  selector: 'app-track',
  standalone: true,
  imports: [FormsModule, LoadingStateComponent, StatusBadgeComponent, StatusTonePipe],
  templateUrl: './track.component.html',
  styleUrl: './track.component.scss'
})
export class TrackComponent implements OnInit {
  private readonly packageApi = inject(PackageApiService);
  private readonly deliveryApi = inject(DeliveryApiService);
  private readonly route = inject(ActivatedRoute);

  protected packageId = '';
  protected readonly loading = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly pkg = signal<Package | null>(null);
  protected readonly delivery = signal<DeliveryPackageResponse | null>(null);

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.packageId = id;
      this.search();
    }
  }

  search(): void {
    const id = Number(this.packageId);
    if (!id || Number.isNaN(id)) {
      this.error.set('Enter a valid numeric package ID.');
      this.pkg.set(null);
      this.delivery.set(null);
      return;
    }

    this.loading.set(true);
    this.error.set(null);

    this.packageApi.getById(id).subscribe({
      next: (pkg) => {
        this.pkg.set(pkg);
        this.deliveryApi.getDeliveryForPackage(id).subscribe({
          next: (response) => {
            this.delivery.set(response);
            this.loading.set(false);
          },
          error: () => {
            this.delivery.set(null);
            this.loading.set(false);
          }
        });
      },
      error: () => {
        this.pkg.set(null);
        this.delivery.set(null);
        this.error.set(`Package ${id} was not found.`);
        this.loading.set(false);
      }
    });
  }
}
