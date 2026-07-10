import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Package, PackageApiService } from 'shared';

@Component({
  selector: 'app-package-form',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './package-form.component.html',
  styleUrl: './package-form.component.scss'
})
export class PackageFormComponent implements OnInit {
  private readonly packageApi = inject(PackageApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly error = signal<string | null>(null);
  protected readonly saving = signal(false);
  protected isEdit = false;
  protected packageId?: number;

  protected pkg: Package = {
    weight: 1,
    width: null,
    height: null,
    depth: null,
    description: '',
    clientId: 1,
    status: 'CREATED'
  };

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.packageId = Number(id);
      this.packageApi.getById(this.packageId).subscribe({
        next: (pkg) => (this.pkg = pkg),
        error: () => this.error.set('Package not found.')
      });
    }
  }

  submit(): void {
    this.saving.set(true);
    this.error.set(null);

    const request = this.isEdit && this.packageId
      ? this.packageApi.update(this.packageId, this.pkg)
      : this.packageApi.create(this.pkg);

    request.subscribe({
      next: (saved) => this.router.navigate(['/packages', saved.id]),
      error: () => {
        this.error.set('Unable to save package. Check required fields.');
        this.saving.set(false);
      }
    });
  }
}
