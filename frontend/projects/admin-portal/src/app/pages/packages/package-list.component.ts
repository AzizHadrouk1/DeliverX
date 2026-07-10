import { FormsModule } from '@angular/forms';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import {
  LoadingStateComponent,
  PACKAGE_STATUSES,
  Package,
  PackageApiService,
  PackageMgmtStatus,
  StatusBadgeComponent,
  StatusTonePipe
} from 'shared';

@Component({
  selector: 'app-package-list',
  standalone: true,
  imports: [FormsModule, RouterLink, LoadingStateComponent, StatusBadgeComponent, StatusTonePipe],
  templateUrl: './package-list.component.html',
  styleUrl: './package-list.component.scss'
})
export class PackageListComponent implements OnInit {
  private readonly packageApi = inject(PackageApiService);

  protected readonly statuses = PACKAGE_STATUSES;
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly packages = signal<Package[]>([]);
  protected readonly totalPages = signal(0);
  protected readonly page = signal(0);

  protected selectedStatus: PackageMgmtStatus | '' = '';
  protected clientIdFilter: number | null = null;
  protected readonly pageSize = 12;

  ngOnInit(): void {
    this.load();
  }

  load(page = 0): void {
    this.loading.set(true);
    this.error.set(null);
    this.page.set(page);

    this.packageApi.getAll({
      status: this.selectedStatus || undefined,
      clientId: this.clientIdFilter || undefined,
      page,
      size: this.pageSize
    }).subscribe({
      next: (response) => {
        this.packages.set(response.content);
        this.totalPages.set(response.totalPages);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Unable to load packages. Ensure the gateway and package-service are running.');
        this.loading.set(false);
      }
    });
  }

  deletePackage(id: number): void {
    if (!confirm('Delete this package? Only packages in CREATED status can be deleted.')) {
      return;
    }

    this.packageApi.delete(id).subscribe({
      next: () => this.load(this.page()),
      error: () => this.error.set('Failed to delete package. It may no longer be in CREATED status.')
    });
  }
}
