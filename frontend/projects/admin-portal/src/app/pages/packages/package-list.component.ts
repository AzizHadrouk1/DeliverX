import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { LoadingStateComponent, Package, PackageApiService, StatusBadgeComponent, StatusTonePipe } from 'shared';

@Component({
  selector: 'app-package-list',
  standalone: true,
  imports: [RouterLink, LoadingStateComponent, StatusBadgeComponent, StatusTonePipe],
  templateUrl: './package-list.component.html',
  styleUrl: './package-list.component.scss'
})
export class PackageListComponent implements OnInit {
  private readonly packageApi = inject(PackageApiService);

  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly packages = signal<Package[]>([]);

  ngOnInit(): void {
    this.packageApi.getAll().subscribe({
      next: (packages) => {
        this.packages.set(packages);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Unable to load packages.');
        this.loading.set(false);
      }
    });
  }
}
