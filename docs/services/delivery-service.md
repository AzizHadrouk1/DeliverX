# Delivery Service

Orchestration des **livraisons** et preuves de livraison. Persistance MySQL + Swagger.

| Propriété | Valeur |
|-----------|--------|
| Port | 8084 |
| Eureka | `DELIVERY-SERVICE` |
| Gateway | `/deliveries/**` (stripPrefix) |
| Base | MySQL `delivery_db` |
| Package | `com.esprit.microservice.delivery` |
| Swagger | http://localhost:8084/swagger-ui.html |

## Endpoints principaux

| Méthode | Chemin | Description |
|---------|--------|-------------|
| GET | `/api/deliveries` | Liste paginée (status, driverId, date) |
| GET | `/api/deliveries/{id}` | Détail |
| POST | `/api/deliveries` | Créer (status PENDING) |
| PUT | `/api/deliveries/{id}` | Modifier si PENDING |
| PATCH | `/api/deliveries/{id}/status` | Transition de statut |
| DELETE | `/api/deliveries/{id}` | Supprimer ou annuler |
| GET/POST | `/api/deliveries/{id}/proof` | Preuve de livraison |
| GET | `/api/deliveries/driver/{driverId}` | Par conducteur |
| GET | `/api/deliveries/schedule?date=` | Par date planifiée |
| GET | `/package/{id}` | Démo OpenFeign → package |
| GET | `/health`, `/hello` | Santé |

Statuts : `PENDING` → `ASSIGNED` → `PICKED_UP` → `IN_PROGRESS` → `DELIVERED` | `FAILED` | `CANCELLED`

## OpenFeign

`PackageClient` → `PACKAGE-SERVICE` `GET /packages/{id}`

## Comment tester

```powershell
curl http://localhost:8084/health
curl http://localhost:8084/swagger-ui.html

curl "http://localhost:8084/api/deliveries?page=0&size=10"
curl http://localhost:8090/deliveries/api/deliveries
curl http://localhost:8090/deliveries/package/1
```

Exemples CRUD détaillés : [API Delivery](../api/delivery-service.md)
