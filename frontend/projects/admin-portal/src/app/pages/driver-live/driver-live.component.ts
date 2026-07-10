import { Component, OnDestroy, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TrackingApiService } from 'shared';

/**
 * Turns this device's real GPS (navigator.geolocation) into the source of a
 * delivery's live position, replacing the fake data from simulate-delivery.js.
 * Any page viewing /tracking or /track/:id for the same deliveryId sees the
 * position update live over WebSocket - this page only pushes, it doesn't render a map.
 */
@Component({
  selector: 'app-driver-live',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './driver-live.component.html',
  styleUrl: './driver-live.component.scss'
})
export class DriverLiveComponent implements OnDestroy {
  private readonly trackingApi = inject(TrackingApiService);

  protected deliveryId = '1';
  protected readonly sharing = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly lastFix = signal<GeolocationCoordinates | null>(null);
  protected readonly pingCount = signal(0);
  protected readonly lastPingAt = signal<Date | null>(null);

  private watchId: number | null = null;

  protected start(): void {
    if (!this.deliveryId.trim()) {
      this.error.set('Enter a delivery ID first.');
      return;
    }
    if (!('geolocation' in navigator)) {
      this.error.set('This browser does not support Geolocation.');
      return;
    }

    this.error.set(null);

    this.watchId = navigator.geolocation.watchPosition(
      (position) => this.onFix(position),
      (err) => this.onGeoError(err),
      { enableHighAccuracy: true, maximumAge: 5000, timeout: 15000 }
    );
    this.sharing.set(true);
  }

  protected stop(): void {
    if (this.watchId !== null) {
      navigator.geolocation.clearWatch(this.watchId);
      this.watchId = null;
    }
    this.sharing.set(false);
  }

  private onFix(position: GeolocationPosition): void {
    const { latitude, longitude, speed, heading } = position.coords;
    this.lastFix.set(position.coords);

    this.trackingApi
      .pushLocation(this.deliveryId.trim(), {
        deliveryId: this.deliveryId.trim(),
        latitude,
        longitude,
        // Geolocation gives speed in m/s; the backend expects km/h.
        speed: speed != null ? speed * 3.6 : null,
        heading: heading ?? null
      })
      .subscribe({
        next: () => {
          this.pingCount.update((n) => n + 1);
          this.lastPingAt.set(new Date());
        },
        error: () => this.error.set('Position captured but the server rejected the update.')
      });
  }

  private onGeoError(err: GeolocationPositionError): void {
    const messages: Record<number, string> = {
      1: 'Location permission denied - allow it in the browser to share your position.',
      2: 'Position unavailable - check GPS/network.',
      3: 'Timed out waiting for a position fix.'
    };
    this.error.set(messages[err.code] ?? 'Unknown geolocation error.');
  }

  ngOnDestroy(): void {
    this.stop();
  }
}
