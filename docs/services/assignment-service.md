# Assignment Service

Gestion des **affectations** (liaison livraison ↔ conducteur ↔ véhicule).

| Propriété | Valeur |
|-----------|--------|
| Port | 8081 |
| Eureka | `ASSIGNMENT-SERVICE` |
| Gateway | `/assignment/**` (stripPrefix) |
| Base | H2 in-memory `assignmentdb` |
| Package | `com.esprit.microservice.assignment` |

## Rôle

Créer et suivre les assignments. Avant création, le service vérifie via **OpenFeign** que la livraison, le conducteur et le véhicule existent.

## Endpoints

| Méthode | Chemin | Description |
|---------|--------|-------------|
| GET | `/health`, `/hello` | Santé |
| GET | `/api/assignments` | Liste |
| GET | `/api/assignments/{id}` | Détail |
| POST | `/api/assignments` | Créer |
| PUT | `/api/assignments/{id}` | Modifier |
| PATCH | `/api/assignments/{id}/status` | Changer statut |
| DELETE | `/api/assignments/{id}` | Supprimer |
| GET | `/api/assignments/driver/{driverId}` | Par conducteur |
| GET | `/api/assignments/delivery/{deliveryId}` | Par livraison |
| GET | `/api/assignments/status/{status}` | Par statut |

Statuts : `ASSIGNED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`

## OpenFeign

- `DeliveryClient` → DELIVERY-SERVICE
- `DriverClient` → DRIVER-CLIENT-SERVICE
- `VehicleClient` → VEHICLE-SERVICE

## Comment tester

```powershell
# Health direct
curl http://localhost:8081/health

# Via Gateway
curl http://localhost:8090/assignment/health
curl http://localhost:8090/assignment/api/assignments

# Créer (JWT requis via Gateway pour POST)
curl -X POST http://localhost:8081/api/assignments `
  -H "Content-Type: application/json" `
  -d "{\"deliveryId\":1,\"driverId\":1,\"vehicleId\":1}"
```

H2 console (si activée) : http://localhost:8081/h2-console
