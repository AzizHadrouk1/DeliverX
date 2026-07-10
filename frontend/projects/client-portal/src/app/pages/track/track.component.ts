import {
  Component, OnInit, OnDestroy, inject, signal, computed, NgZone, ElementRef, AfterViewInit, ViewChild
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription, interval } from 'rxjs';
import {
  DeliveryApiService,
  LoadingStateComponent,
  Package,
  PackageApiService,
  StatusBadgeComponent,
  StatusTonePipe,
  TrackingApiService,
  TrackingWebSocketService,
  TrackingEvent,
  LiveTrackingMessage,
  PackageStatus,
  STATUS_LABELS,
  STATUS_ICONS,
  EtaResponse
} from 'shared';

declare const L: any; // Leaflet loaded via CDN in index.html

@Component({
  selector: 'app-track',
  standalone: true,
  imports: [FormsModule, LoadingStateComponent, StatusBadgeComponent, StatusTonePipe],
  templateUrl: './track.component.html',
  styleUrl: './track.component.scss'
})
export class TrackComponent implements OnInit, AfterViewInit, OnDestroy {
  private readonly packageApi = inject(PackageApiService);
  private readonly deliveryApi = inject(DeliveryApiService);
  private readonly trackingApi = inject(TrackingApiService);
  private readonly trackingWs = inject(TrackingWebSocketService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly ngZone = inject(NgZone);

  @ViewChild('mapContainer') mapContainer!: ElementRef;

  // ── State signals ──────────────────────────────────────────────────────────
  protected packageId = '';
  protected readonly loading   = signal(false);
  protected readonly error     = signal<string | null>(null);
  protected readonly pkg       = signal<Package | null>(null);
  protected readonly delivery  = signal<any>(null);

  // Tracking-specific state
  protected readonly isLive          = signal(false);
  protected readonly wsConnected     = signal(false);
  protected readonly trackingLoaded  = signal(false);
  protected readonly currentEvent    = signal<TrackingEvent | null>(null);
  protected readonly history         = signal<TrackingEvent[]>([]);
  protected readonly eta             = signal<EtaResponse | null>(null);
  protected readonly etaCountdown    = signal<string>('–');

  // Map
  private map: any = null;
  private driverMarker: any = null;
  private destMarker: any = null;
  private routePolyline: any = null;
  private routeGlowPolyline: any = null;
  private mapReady = false;

  // Subscriptions
  private wsSub?: Subscription;
  private timerSub?: Subscription;
  private currentDeliveryId?: string;

  // ETA destination (from delivery address coords — mocked for demo)
  private readonly DEST_LAT = 36.8065;
  private readonly DEST_LNG = 10.1815; // Tunis center as demo destination

  // Expose helpers to template
  protected readonly statusLabels = STATUS_LABELS;
  protected readonly statusIcons  = STATUS_ICONS;

  protected readonly statusSteps: PackageStatus[] = [
    'IN_TRANSIT',
    'OUT_FOR_DELIVERY',
    'DELIVERED'
  ];

  protected currentStepIndex = computed(() => {
    const s = this.currentEvent()?.status;
    if (!s) return -1;
    return this.statusSteps.indexOf(s as any);
  });

  // ── Lifecycle ──────────────────────────────────────────────────────────────

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.packageId = id;
        this.executeSearch(id);
      } else {
        this.packageId = '';
        this.pkg.set(null);
        this.teardown();
        this.destroyMap();
      }
    });
  }

  ngAfterViewInit(): void {
    this.mapReady = true;
    if (this.trackingLoaded() && this.currentEvent()) {
      this.initMap(this.currentEvent()!);
    }
  }

  ngOnDestroy(): void {
    this.teardown();
  }

  // ── Search ─────────────────────────────────────────────────────────────────

  search(): void {
    const query = this.packageId.trim();
    if (!query) {
      this.error.set('Enter a package ID or tracking number.');
      return;
    }
    this.router.navigate(['/track', query]);
  }

  private executeSearch(query: string): void {
    this.loading.set(true);
    this.error.set(null);
    this.teardown();
    this.destroyMap();

    const numericId = Number(query);
    const lookup$ = !isNaN(numericId) && /^\d+$/.test(query)
      ? this.packageApi.getById(numericId)
      : this.packageApi.getByTracking(query);

    lookup$.subscribe({
      next: (pkg) => {
        this.pkg.set(pkg);
        const id = pkg.id!;
        this.deliveryApi.getDeliveryForPackage(id).subscribe({
          next: (d) => {
            this.delivery.set(d);
            this.loading.set(false);
            this.loadTracking(String(id));
          },
          error: () => {
            this.delivery.set(null);
            this.loading.set(false);
            this.loadTracking(String(id));
          }
        });
      },
      error: () => {
        this.error.set(`Package "${query}" was not found.`);
        this.pkg.set(null);
        this.delivery.set(null);
        this.loading.set(false);
      }
    });
  }

  // ── Tracking ───────────────────────────────────────────────────────────────

  private loadTracking(deliveryId: string): void {
    this.currentDeliveryId = deliveryId;

    // Load latest position
    this.trackingApi.getLatestLocation(deliveryId).subscribe({
      next: (event) => {
        this.currentEvent.set(event);
        this.trackingLoaded.set(true);
        this.loadHistory(deliveryId);
        this.startWebSocket(deliveryId);
        this.loadEta(deliveryId);
        if (this.mapReady) {
          setTimeout(() => this.initMap(event), 50);
        }
      },
      error: () => {
        // No tracking data yet — still show WebSocket in standby
        this.trackingLoaded.set(false);
        this.startWebSocket(deliveryId);
      }
    });
  }

  private loadHistory(deliveryId: string): void {
    this.trackingApi.getHistory(deliveryId).subscribe({
      next: (h) => this.history.set(h.slice(0, 10))
    });
  }

  private loadEta(deliveryId: string): void {
    this.trackingApi.getEta(deliveryId, this.DEST_LAT, this.DEST_LNG).subscribe({
      next: (e) => {
        this.eta.set(e);
        this.startEtaCountdown(e);
      }
    });
  }

  private startWebSocket(deliveryId: string): void {
    this.wsSub?.unsubscribe();

    this.trackingWs.connected$.subscribe(c => {
      this.ngZone.run(() => this.wsConnected.set(c));
    });

    this.wsSub = this.trackingWs.subscribe(deliveryId).subscribe({
      next: (msg: LiveTrackingMessage) => {
        this.ngZone.run(() => this.onLiveMessage(msg));
      }
    });
    this.isLive.set(true);
  }

  private onLiveMessage(msg: LiveTrackingMessage): void {
    // Update current position
    const event: TrackingEvent = {
      id: crypto.randomUUID(),
      deliveryId: msg.deliveryId,
      latitude: msg.latitude,
      longitude: msg.longitude,
      speed: msg.speed,
      heading: msg.heading,
      status: msg.status,
      notes: msg.notes,
      timestamp: msg.timestamp
    };
    this.currentEvent.set(event);
    this.history.update(h => [event, ...h].slice(0, 10));

    const wasLoaded = this.trackingLoaded();
    if (!wasLoaded) {
      this.trackingLoaded.set(true);
      if (this.mapReady) {
        setTimeout(() => this.initMap(event), 50);
      }
    } else {
      // Move marker on map
      if (this.driverMarker) {
        this.driverMarker.setLatLng([msg.latitude, msg.longitude]);
        
        // Update rotation dynamically on the custom driver pin element
        const rotation = msg.heading ?? 0;
        const el = this.driverMarker.getElement();
        if (el) {
          const innerPin = el.querySelector('.custom-driver-pin');
          if (innerPin) {
            innerPin.style.transform = `rotate(${rotation}deg)`;
          }
        }
        
        this.map?.panTo([msg.latitude, msg.longitude], { animate: true });
      } else if (this.map) {
        this.addDriverMarker(msg.latitude, msg.longitude, msg.heading);
      }

      // Update route paths dynamically
      if (this.routePolyline) {
        this.routePolyline.setLatLngs([[msg.latitude, msg.longitude], [this.DEST_LAT, this.DEST_LNG]]);
      }
      if (this.routeGlowPolyline) {
        this.routeGlowPolyline.setLatLngs([[msg.latitude, msg.longitude], [this.DEST_LAT, this.DEST_LNG]]);
      }
    }

    // Reload ETA
    if (this.currentDeliveryId) this.loadEta(this.currentDeliveryId);
  }

  // ── ETA countdown ──────────────────────────────────────────────────────────

  private startEtaCountdown(eta: EtaResponse): void {
    this.timerSub?.unsubscribe();
    let remaining = Math.round(eta.etaMinutes * 60); // seconds

    const format = (s: number) => {
      if (s <= 0) return 'Arriving now';
      const m = Math.floor(s / 60);
      const sec = s % 60;
      return m > 0 ? `${m} min ${sec}s` : `${sec}s`;
    };

    this.etaCountdown.set(format(remaining));

    this.timerSub = interval(1000).subscribe(() => {
      remaining--;
      this.ngZone.run(() => this.etaCountdown.set(format(remaining)));
      if (remaining <= 0) this.timerSub?.unsubscribe();
    });
  }

  // ── Leaflet map ─────────────────────────────────────────────────────────────

  private initMap(event: TrackingEvent): void {
    if (!this.mapContainer?.nativeElement || this.map) return;

    this.ngZone.runOutsideAngular(() => {
      this.map = L.map(this.mapContainer.nativeElement, {
        zoomControl: true,
        attributionControl: false
      }).setView([event.latitude, event.longitude], 14);

      // Clean premium map style (CartoDB Voyager map tiles)
      L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
        maxZoom: 19,
        attribution: '&copy; OpenStreetMap contributors &copy; CARTO'
      }).addTo(this.map);

      this.addDriverMarker(event.latitude, event.longitude, event.heading);

      // Destination marker with animated pin drop SVG
      const destIcon = L.divIcon({
        className: 'custom-leaflet-marker',
        html: `
          <div class="custom-dest-pin">
            <div class="dest-pin-body">
              <svg viewBox="0 0 24 24" width="22" height="22" fill="#ef4444">
                <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
              </svg>
            </div>
            <div class="dest-pin-shadow"></div>
          </div>`,
        iconSize: [40, 40],
        iconAnchor: [20, 40]
      });
      this.destMarker = L.marker([this.DEST_LAT, this.DEST_LNG], { icon: destIcon })
        .addTo(this.map)
        .bindPopup('<strong>Destination</strong>');

      // Route line - Glowing path underlay
      this.routeGlowPolyline = L.polyline(
        [[event.latitude, event.longitude], [this.DEST_LAT, this.DEST_LNG]],
        { color: '#93c5fd', weight: 8, opacity: 0.35 }
      ).addTo(this.map);

      // Route line - Glowing navigation path
      this.routePolyline = L.polyline(
        [[event.latitude, event.longitude], [this.DEST_LAT, this.DEST_LNG]],
        { color: '#2563eb', weight: 4, dashArray: '6 8', opacity: 0.9 }
      ).addTo(this.map);

      // Fit bounds
      this.map.fitBounds(
        [[event.latitude, event.longitude], [this.DEST_LAT, this.DEST_LNG]],
        { padding: [50, 50] }
      );

      setTimeout(() => {
        this.map?.invalidateSize();
      }, 100);
    });
  }

  private addDriverMarker(lat: number, lng: number, heading: number | null): void {
    const rotation = heading ?? 0;
    const driverIcon = L.divIcon({
      className: 'custom-leaflet-marker',
      html: `
        <div class="custom-driver-pin" style="transform: rotate(${rotation}deg)">
          <div class="pulse-ring"></div>
          <div class="pin-body">
            <svg viewBox="0 0 24 24" width="22" height="22" fill="#2563eb">
              <path d="M20 8h-3V4H3c-1.1 0-2 .9-2 2v11h2c0 1.66 1.34 3 3 3s3-1.34 3-3h6c0 1.66 1.34 3 3 3s3-1.34 3-3h2v-5l-3-4zM6 18.5c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5zm12 0c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5zm2-5.5h-3V9h3v4z"/>
            </svg>
          </div>
        </div>`,
      iconSize: [52, 52],
      iconAnchor: [26, 26]
    });
    this.driverMarker = L.marker([lat, lng], { icon: driverIcon })
      .addTo(this.map)
      .bindPopup('<strong>Driver location</strong>');
  }

  private destroyMap(): void {
    if (this.map) {
      this.map.remove();
      this.map = null;
      this.driverMarker = null;
      this.destMarker = null;
      this.routePolyline = null;
      this.routeGlowPolyline = null;
    }
  }

  private teardown(): void {
    this.wsSub?.unsubscribe();
    this.timerSub?.unsubscribe();
    this.trackingWs.disconnect();
    this.currentEvent.set(null);
    this.history.set([]);
    this.eta.set(null);
    this.isLive.set(false);
    this.wsConnected.set(false);
    this.trackingLoaded.set(false);
  }

  // ── Helpers for template ────────────────────────────────────────────────────

  protected formatTime(iso: string): string {
    return new Date(iso).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }

  protected formatDate(iso: string): string {
    return new Date(iso).toLocaleDateString([], { day: 'numeric', month: 'short' });
  }

  protected speed(event: TrackingEvent | null): string {
    if (!event?.speed) return '–';
    return `${Math.round(event.speed)} km/h`;
  }
}
