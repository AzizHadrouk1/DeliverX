# API Delivery Service

Base URL directe : `http://localhost:8084/api/deliveries`

Via Gateway : `http://localhost:8090/deliveries/api/deliveries`

## Endpoints

| Méthode | Chemin | Description |
|---------|--------|-------------|
| GET | `/api/deliveries` | Liste paginée avec filtres |
| GET | `/api/deliveries/{id}` | Détail d'une livraison |
| POST | `/api/deliveries` | Créer une livraison |
| PUT | `/api/deliveries/{id}` | Modifier (statut PENDING uniquement) |
| PATCH | `/api/deliveries/{id}/status` | Changer le statut |
| DELETE | `/api/deliveries/{id}` | Supprimer ou annuler |
| GET | `/api/deliveries/{id}/proof` | Preuve de livraison |
| POST | `/api/deliveries/{id}/proof` | Créer une preuve |
| GET | `/api/deliveries/driver/{driverId}` | Livraisons par conducteur |
| GET | `/api/deliveries/schedule?date=YYYY-MM-DD` | Livraisons planifiées |

## Statuts

`PENDING` → `ASSIGNED` → `PICKED_UP` → `IN_PROGRESS` → `DELIVERED` | `FAILED` | `CANCELLED`

## Exemples

### Lister les livraisons (paginé, filtre statut)

```bash
curl "http://localhost:8084/api/deliveries?page=0&size=10&status=PENDING"
```

### Créer une livraison

```bash
curl -X POST http://localhost:8084/api/deliveries \
  -H "Content-Type: application/json" \
  -d '{
    "packageId": 1,
    "clientId": 1,
    "driverId": 1,
    "vehicleId": 1,
    "pickupAddress": "12 Rue de la Paix, Paris",
    "deliveryAddress": "45 Avenue Victor Hugo, Lyon",
    "scheduledDate": "2026-06-30T14:00:00"
  }'
```

### Modifier le statut

```bash
curl -X PATCH http://localhost:8084/api/deliveries/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "ASSIGNED", "note": "Driver assigned"}'
```

### Créer une preuve de livraison

```bash
curl -X POST http://localhost:8084/api/deliveries/1/proof \
  -H "Content-Type: application/json" \
  -d '{
    "photoUrl": "https://example.com/photo.jpg",
    "signature": "base64-signature",
    "recipientName": "Jean Dupont"
  }'
```

### Livraisons planifiées pour une date

```bash
curl "http://localhost:8084/api/deliveries/schedule?date=2026-06-30"
```

### Via Gateway

```bash
curl "http://localhost:8090/deliveries/api/deliveries?page=0&size=5"
curl "http://localhost:8090/deliveries/health"
curl "http://localhost:8090/deliveries/package/1"
```

## Codes d'erreur

| Code | Exception |
|------|-----------|
| 400 | Validation, BadRequestException |
| 404 | DeliveryNotFoundException |
| 409 | InvalidStatusTransitionException, DeliveryAlreadyCompletedException |

## Swagger

- API docs : `http://localhost:8084/api-docs`
- Swagger UI : `http://localhost:8084/swagger-ui.html`
