import {
  Component, OnInit, OnDestroy, inject, signal, NgZone, ElementRef, AfterViewInit, ViewChild
} from '@angular/core';
import { RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';
import {
  TrackingApiService,
  TrackingEvent,
  STATUS_LABELS,
  STATUS_ICONS,
  PackageStatus
} from 'shared';

declare const L: any;

interface ActiveDelivery {
  deliveryId: string;
  event: TrackingEvent;
  marker?: any;
}

@Component({
  selector: 'app-tracking-map',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './tracking-map.component.html',
  styleUrl: './tracking-map.component.scss'
})
export class TrackingMapComponent implements OnInit, AfterViewInit, OnDestroy {
  private readonly trackingApi = inject(TrackingApiService);
  private readonly ngZone = inject(NgZone);

  @ViewChild('mapEl') mapEl!: ElementRef;

  protected readonly loading   = signal(true);
  protected readonly deliveries = signal<ActiveDelivery[]>([]);
  protected readonly selected  = signal<ActiveDelivery | null>(null);

  protected readonly statusLabels = STATUS_LABELS;
  protected readonly statusIcons  = STATUS_ICONS;

  private map: any = null;
  private refreshInterval?: ReturnType<typeof setInterval>;

  // Demo delivery IDs to track — in production these would come from delivery-service
  private readonly DEMO_IDS = ['1', '2', '3', '4', '5'];

  ngOnInit(): void {
    this.loadAll();
    // Refresh every 15s
    this.refreshInterval = setInterval(() => this.loadAll(), 15000);
  }

  ngAfterViewInit(): void {
    this.initMap();
  }

  ngOnDestroy(): void {
    if (this.refreshInterval) clearInterval(this.refreshInterval);
    if (this.map) this.map.remove();
  }

  private initMap(): void {
    this.ngZone.runOutsideAngular(() => {
      this.map = L.map(this.mapEl.nativeElement, {
        zoomControl: true,
        attributionControl: false
      }).setView([36.8065, 10.1815], 11);

      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19
      }).addTo(this.map);
    });
  }

  private loadAll(): void {
    let loaded = 0;
    const active: ActiveDelivery[] = [];

    this.DEMO_IDS.forEach(id => {
      this.trackingApi.getLatestLocation(id).subscribe({
        next: (event) => {
          const existing = this.deliveries().find(d => d.deliveryId === id);
          const entry: ActiveDelivery = { deliveryId: id, event, marker: existing?.marker };
          active.push(entry);

          if (this.map) {
            this.ngZone.runOutsideAngular(() => {
              this.upsertMarker(entry);
            });
          }

          loaded++;
          if (loaded === this.DEMO_IDS.length) {
            this.ngZone.run(() => {
              this.deliveries.set([...active]);
              this.loading.set(false);
            });
          }
        },
        error: () => {
          loaded++;
          if (loaded === this.DEMO_IDS.length) {
            this.ngZone.run(() => {
              this.deliveries.set([...active]);
              this.loading.set(false);
            });
          }
        }
      });
    });
  }

  private upsertMarker(d: ActiveDelivery): void {
    const color = this.statusColor(d.event.status);
    const icon = L.divIcon({
      className: '',
      html: `<div class="fleet-marker fleet-marker--${color}">
               <span>${STATUS_ICONS[d.event.status]}</span>
               <div class="fleet-marker__id">#${d.deliveryId}</div>
             </div>`,
      iconSize: [56, 56],
      iconAnchor: [28, 28]
    });

    if (d.marker) {
      d.marker.setLatLng([d.event.latitude, d.event.longitude]);
      d.marker.setIcon(icon);
    } else {
      d.marker = L.marker([d.event.latitude, d.event.longitude], { icon })
        .addTo(this.map)
        .on('click', () => {
          this.ngZone.run(() => this.selected.set(d));
        });
    }
  }

  protected selectDelivery(d: ActiveDelivery): void {
    this.selected.set(d);
    if (this.map && d.event) {
      this.map.flyTo([d.event.latitude, d.event.longitude], 14, { animate: true });
    }
  }

  protected statusColor(status: PackageStatus): string {
    switch (status) {
      case 'DELIVERED': return 'green';
      case 'FAILED':
      case 'RETURNED': return 'red';
      case 'OUT_FOR_DELIVERY': return 'blue';
      default: return 'orange';
    }
  }

  protected formatTime(iso: string): string {
    return new Date(iso).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }
}
