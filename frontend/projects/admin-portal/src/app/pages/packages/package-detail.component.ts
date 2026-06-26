import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { LoadingStateComponent, Package, PackageApiService, StatusBadgeComponent, StatusTonePipe } from 'shared';

@Component({
  selector: 'app-package-detail',
  standalone: true,
  imports: [RouterLink, LoadingStateComponent, StatusBadgeComponent, StatusTonePipe],
  templateUrl: './package-detail.component.html',
  styleUrl: './package-detail.component.scss'
})
export class PackageDetailComponent implements OnInit {
  private readonly packageApi = inject(PackageApiService);
  private readonly route = inject(ActivatedRoute);

  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly pkg = signal<Package | null>(null);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.packageApi.getById(id).subscribe({
      next: (pkg) => {
        this.pkg.set(pkg);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Package not found.');
        this.loading.set(false);
      }
    });
  }
}
