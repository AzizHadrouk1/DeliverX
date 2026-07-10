# API Delivery Service — exemples

Base URL directe : `http://localhost:8084/api/deliveries`  
Via Gateway : `http://localhost:8090/deliveries/api/deliveries`

Swagger : http://localhost:8084/swagger-ui.html  
Swagger unifié (Gateway) : http://localhost:8090/swagger — choisir **delivery-service** dans le menu.

## Endpoints

| Méthode | Chemin | Description |
|---------|--------|-------------|
| GET | `/api/deliveries` | Liste paginée + filtres |
| GET | `/api/deliveries/{id}` | Détail |
| POST | `/api/deliveries` | Créer |
| PUT | `/api/deliveries/{id}` | Modifier (PENDING uniquement) |
| PATCH | `/api/deliveries/{id}/status` | Changer le statut |
| DELETE | `/api/deliveries/{id}` | Supprimer ou annuler |
| GET | `/api/deliveries/{id}/proof` | Lire la preuve |
| POST | `/api/deliveries/{id}/proof` | Créer une preuve |
| GET | `/api/deliveries/driver/{driverId}` | Par conducteur |
| GET | `/api/deliveries/schedule?date=YYYY-MM-DD` | Par date |

## Transitions de statut

`PENDING` → `ASSIGNED` → `PICKED_UP` → `IN_PROGRESS` → `DELIVERED` | `FAILED` | `CANCELLED`

Depuis `PENDING` / états intermédiaires, `CANCELLED` est autorisé selon le validateur. Les états terminaux n'acceptent plus de transition.

## Exemples curl

### Lister (paginé, filtre)

```powershell
curl "http://localhost:8084/api/deliveries?page=0&size=10&status=PENDING"
curl "http://localhost:8090/deliveries/api/deliveries?page=0&size=5"
```

### Créer

```powershell
curl -X POST http://localhost:8084/api/deliveries `
  -H "Content-Type: application/json" `
  -d '{
    "packageId": 1,
    "clientId": 1,
    "driverId": 1,
    "vehicleId": 1,
    "pickupAddress": "12 Rue de la Paix, Paris",
    "deliveryAddress": "45 Avenue Victor Hugo, Lyon",
    "scheduledDate": "2026-07-15T14:00:00"
  }'
```

!!! note
    Via Gateway, les POST nécessitent un Bearer JWT Keycloak.

### Modifier le statut

```powershell
curl -X PATCH http://localhost:8084/api/deliveries/1/status `
  -H "Content-Type: application/json" `
  -d '{"status": "ASSIGNED", "note": "Driver assigned"}'
```

### Preuve de livraison

```powershell
curl -X POST http://localhost:8084/api/deliveries/1/proof `
  -H "Content-Type: application/json" `
  -d '{
    "photoUrl": "https://example.com/photo.jpg",
    "signature": "base64-signature",
    "recipientName": "Jean Dupont"
  }'
```

### Planning

```powershell
curl "http://localhost:8084/api/deliveries/schedule?date=2026-07-15"
```

### OpenFeign package

```powershell
curl http://localhost:8084/package/1
curl http://localhost:8090/deliveries/package/1
```

## Codes d'erreur

| HTTP | Cas |
|------|-----|
| 400 | Validation / BadRequest |
| 404 | Delivery introuvable |
| 409 | Transition invalide / déjà terminée |
