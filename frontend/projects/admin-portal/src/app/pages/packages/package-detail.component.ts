import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import {
  LoadingStateComponent,
  PACKAGE_STATUSES,
  Package,
  PackageApiService,
  PackageMgmtStatus,
  PackageStatusHistory,
  StatusBadgeComponent,
  StatusTonePipe
} from 'shared';

@Component({
  selector: 'app-package-detail',
  standalone: true,
  imports: [DatePipe, FormsModule, RouterLink, LoadingStateComponent, StatusBadgeComponent, StatusTonePipe],
  templateUrl: './package-detail.component.html',
  styleUrl: './package-detail.component.scss'
})
export class PackageDetailComponent implements OnInit {
  private readonly packageApi = inject(PackageApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly statuses = PACKAGE_STATUSES;
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly statusError = signal<string | null>(null);
  protected readonly pkg = signal<Package | null>(null);
  protected readonly history = signal<PackageStatusHistory[]>([]);
  protected readonly updatingStatus = signal(false);

  protected nextStatus: PackageMgmtStatus = 'PICKED_UP';
  protected statusComment = '';

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.load(id);
  }

  load(id: number): void {
    this.loading.set(true);
    this.error.set(null);

    this.packageApi.getById(id).subscribe({
      next: (pkg) => {
        this.pkg.set(pkg);
        this.loading.set(false);
        this.packageApi.getHistory(id).subscribe({
          next: (history) => this.history.set(history),
          error: () => this.history.set([])
        });
      },
      error: () => {
        this.error.set('Package not found.');
        this.loading.set(false);
      }
    });
  }

  updateStatus(): void {
    const current = this.pkg();
    if (!current?.id) {
      return;
    }

    this.updatingStatus.set(true);
    this.statusError.set(null);

    this.packageApi.updateStatus(current.id, {
      status: this.nextStatus,
      comment: this.statusComment || undefined
    }).subscribe({
      next: (pkg) => {
        this.pkg.set(pkg);
        this.statusComment = '';
        this.updatingStatus.set(false);
        this.packageApi.getHistory(pkg.id!).subscribe({
          next: (history) => this.history.set(history)
        });
      },
      error: () => {
        this.statusError.set('Invalid status transition or request failed.');
        this.updatingStatus.set(false);
      }
    });
  }

  deletePackage(): void {
    const current = this.pkg();
    if (!current?.id || !confirm('Delete this package?')) {
      return;
    }

    this.packageApi.delete(current.id).subscribe({
      next: () => this.router.navigate(['/packages']),
      error: () => this.error.set('Failed to delete package.')
    });
  }
}
