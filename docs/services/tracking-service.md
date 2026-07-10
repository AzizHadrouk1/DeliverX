# Tracking Service

Suivi GPS des livraisons : historique MongoDB, ETA, optimisation d'itinéraire, **WebSocket STOMP**.

| Propriété | Valeur |
|-----------|--------|
| Port | 8086 (config-repo / Docker) |
| Eureka | `TRACKING-SERVICE` |
| Gateway | `/tracking/**` (strip), `/ws/**` |
| Base | MongoDB `tracking_db` |
| Package | `com.esprit.microservice.tracking` |
| Swagger | http://localhost:8086/swagger-ui.html |

## Endpoints REST

| Méthode | Chemin | Description |
|---------|--------|-------------|
| POST | `/api/tracking/{deliveryId}/location` | Enregistrer une position |
| GET | `/api/tracking/{deliveryId}/location` | Dernière position |
| GET | `/api/tracking/{deliveryId}/history` | Historique |
| PATCH | `/api/tracking/{deliveryId}/status` | Statut tracking |
| GET | `/api/tracking/{deliveryId}/eta` | ETA (`destLat`, `destLng`, `speed`) |
| POST | `/api/tracking/{deliveryId}/route/optimize` | Optimiser route |
| GET | `/api/tracking/{deliveryId}/route` | Route stockée |
| GET | `/health`, `/hello` | Santé |

## WebSocket

| Élément | Valeur |
|---------|--------|
| Endpoint | `/ws` |
| Subscribe | `/topic/tracking/{deliveryId}` |
| Send | `/app/tracking.location` |
| Via Gateway | `ws://localhost:8090/ws` |

## Collections MongoDB

- `tracking_events` — positions / événements
- `delivery_routes` — waypoints et distance

## Comment tester

```powershell
curl http://localhost:8086/health
curl http://localhost:8086/swagger-ui.html

curl http://localhost:8090/tracking/health
curl http://localhost:8090/tracking/api/tracking/1/history

curl -X POST http://localhost:8086/api/tracking/1/location `
  -H "Content-Type: application/json" `
  -d "{\"latitude\":36.8,\"longitude\":10.1,\"speed\":40,\"heading\":90,\"status\":\"IN_TRANSIT\"}"
```
