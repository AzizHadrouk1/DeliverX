# Vehicle Service

Gestion du parc de **véhicules**.

| Propriété | Valeur |
|-----------|--------|
| Port | 8083 |
| Eureka | `VEHICLE-SERVICE` |
| Gateway | `/vehicles/**` |
| Base | H2 fichier `./data/vehicledb` |
| Package | `com.esprit.microservice.vehicle` |

## Endpoints

| Méthode | Chemin | Description |
|---------|--------|-------------|
| GET | `/vehicles` | Liste (filtre `?status=`) |
| GET | `/vehicles/{id}` | Détail |
| POST | `/vehicles/create` | Créer |
| PUT | `/vehicles/{id}` | Modifier |
| DELETE | `/vehicles/{id}` | Supprimer |
| GET | `/health`, `/vehicles/health`, `/hello` | Santé |

Types : `VAN`, `TRUCK`, `MOTORCYCLE`, `CAR`, `BICYCLE`  
Statuts : `AVAILABLE`, `IN_USE`, `MAINTENANCE`, `OUT_OF_SERVICE`

## Comment tester

```powershell
curl http://localhost:8083/health
curl http://localhost:8083/vehicles
curl "http://localhost:8083/vehicles?status=AVAILABLE"

curl http://localhost:8090/vehicles
curl http://localhost:8090/vehicles/health

# Création (JWT via Gateway)
curl -X POST http://localhost:8090/vehicles/create `
  -H "Authorization: Bearer <token>" `
  -H "Content-Type: application/json" `
  -d "{\"licensePlate\":\"TN-1234\",\"brand\":\"Renault\",\"model\":\"Master\",\"type\":\"VAN\",\"status\":\"AVAILABLE\",\"maxWeightCapacity\":1000,\"maxVolumeCapacity\":12,\"manufacturingYear\":2022}"
```

H2 console : http://localhost:8083/h2-console  
JDBC URL : `jdbc:h2:file:./data/vehicledb` · user `sa` · password vide
