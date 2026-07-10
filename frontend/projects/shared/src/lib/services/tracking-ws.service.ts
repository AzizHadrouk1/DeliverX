import { Injectable, OnDestroy } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { LiveTrackingMessage } from '../models/tracking.model';

/**
 * WebSocket service using native browser WebSocket + STOMP protocol.
 * Connects to the tracking-service WebSocket endpoint at:
 *   ws://localhost:8090/ws/websocket
 *
 * STOMP frames are simple text — no external library needed.
 */
@Injectable({ providedIn: 'root' })
export class TrackingWebSocketService implements OnDestroy {
  private ws: WebSocket | null = null;
  private messageSubject = new Subject<LiveTrackingMessage>();
  private connectedSubject = new Subject<boolean>();
  private subscriptions = new Map<string, string>(); // deliveryId → STOMP subscription id
  private stompSubscriptionCounter = 0;
  private heartbeatInterval: ReturnType<typeof setInterval> | null = null;

  readonly connected$ = this.connectedSubject.asObservable();

  /**
   * Connect and subscribe to a delivery's live tracking topic.
   * Returns an Observable that emits every incoming LiveTrackingMessage for that delivery.
   */
  subscribe(deliveryId: string): Observable<LiveTrackingMessage> {
    this.connect(deliveryId);
    return new Observable<LiveTrackingMessage>(observer => {
      const sub = this.messageSubject.subscribe(msg => {
        if (msg.deliveryId === deliveryId) {
          observer.next(msg);
        }
      });
      return () => sub.unsubscribe();
    });
  }

  disconnect(): void {
    if (this.heartbeatInterval) clearInterval(this.heartbeatInterval);
    if (this.ws) {
      this.ws.close();
      this.ws = null;
    }
    this.subscriptions.clear();
  }

  ngOnDestroy(): void {
    this.disconnect();
  }

  // ─────────────────────────────────────────────────────────
  // Private — STOMP over native WebSocket
  // ─────────────────────────────────────────────────────────

  private connect(deliveryId: string): void {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.stompSubscribe(deliveryId);
      return;
    }

    // Use the SockJS-compatible raw WebSocket path
    const wsUrl = 'ws://localhost:8086/ws/websocket';
    this.ws = new WebSocket(wsUrl);

    this.ws.onopen = () => {
      this.sendStomp('CONNECT', { 'accept-version': '1.1,1.0', 'heart-beat': '10000,10000' }, '');
    };

    this.ws.onmessage = (event: MessageEvent) => {
      this.handleFrame(event.data as string, deliveryId);
    };

    this.ws.onerror = () => {
      this.connectedSubject.next(false);
    };

    this.ws.onclose = () => {
      this.connectedSubject.next(false);
      if (this.heartbeatInterval) clearInterval(this.heartbeatInterval);
    };
  }

  private handleFrame(raw: string, deliveryId: string): void {
    const frame = this.parseFrame(raw);

    if (frame.command === 'CONNECTED') {
      this.connectedSubject.next(true);
      this.stompSubscribe(deliveryId);
      // Send STOMP heartbeats
      this.heartbeatInterval = setInterval(() => {
        if (this.ws?.readyState === WebSocket.OPEN) {
          this.ws.send('\n');
        }
      }, 10000);
    }

    if (frame.command === 'MESSAGE') {
      try {
        const payload = JSON.parse(frame.body) as LiveTrackingMessage;
        this.messageSubject.next(payload);
      } catch {
        // ignore non-JSON frames
      }
    }
  }

  private stompSubscribe(deliveryId: string): void {
    if (this.subscriptions.has(deliveryId)) return;
    const subId = `sub-${++this.stompSubscriptionCounter}`;
    this.subscriptions.set(deliveryId, subId);
    this.sendStomp('SUBSCRIBE', {
      id: subId,
      destination: `/topic/tracking/${deliveryId}`
    }, '');
  }

  private sendStomp(command: string, headers: Record<string, string>, body: string): void {
    if (!this.ws || this.ws.readyState !== WebSocket.OPEN) return;
    const headerLines = Object.entries(headers)
      .map(([k, v]) => `${k}:${v}`)
      .join('\n');
    const frame = `${command}\n${headerLines}\n\n${body}\0`;
    this.ws.send(frame);
  }

  private parseFrame(raw: string): { command: string; headers: Record<string, string>; body: string } {
    const lines = raw.split('\n');
    const command = lines[0].trim();
    const headers: Record<string, string> = {};
    let i = 1;
    while (i < lines.length && lines[i].trim() !== '') {
      const [k, ...v] = lines[i].split(':');
      headers[k.trim()] = v.join(':').trim();
      i++;
    }
    const body = lines.slice(i + 1).join('\n').replace(/\0$/, '');
    return { command, headers, body };
  }
}
