# Driver & Client Service

Gestion des **conducteurs** et des **clients**. Sécurisé par JWT Keycloak.

| Propriété | Valeur |
|-----------|--------|
| Port | 8082 (Docker host **8087**) |
| Eureka | `DRIVER-CLIENT-SERVICE` |
| Gateway | `/drivers/**`, `/clients/**` |
| Base | MySQL `driver_client_db` |
| Package | `com.esprit.microservice.driverclient` |

## Drivers — endpoints

| Méthode | Chemin | Description |
|---------|--------|-------------|
| GET | `/drivers` | Liste (filtres status, q, pagination) |
| GET | `/drivers/search` | Recherche |
| GET | `/drivers/{id}` | Détail |
| POST | `/drivers`, `/drivers/create` | Créer |
| PUT | `/drivers/{id}` | Modifier |
| POST | `/drivers/{id}/status` | Changer statut |
| DELETE | `/drivers/{id}` | Supprimer |

Statuts driver : `AVAILABLE`, `ON_DELIVERY`, `OFF_DUTY`, `SUSPENDED`

## Clients — endpoints

| Méthode | Chemin | Description |
|---------|--------|-------------|
| GET | `/clients` | Liste (status, type, q, page) |
| GET | `/clients/search` | Recherche |
| GET | `/clients/{id}` | Détail |
| GET | `/clients/me` | Profil connecté |
| PUT | `/clients/me` | Mettre à jour profil |
| POST | `/clients`, `/clients/create` | Créer |
| PUT | `/clients/{id}` | Modifier |
| DELETE | `/clients/{id}` | Supprimer |

Types client : `INDIVIDUAL`, `BUSINESS` · Statuts : `ACTIVE`, `INACTIVE`

## Sécurité

- Resource server JWT (même realm Keycloak)
- Écritures souvent réservées au rôle `admin`
- Audit Hibernate Envers (`*_aud`)

## Comment tester

```powershell
# Local
curl http://localhost:8082/health
curl http://localhost:8082/drivers
curl http://localhost:8082/clients

# Docker (port hôte)
curl http://localhost:8087/health

# Via Gateway
curl http://localhost:8090/drivers
curl http://localhost:8090/clients
curl http://localhost:8090/drivers/health
```

Mutations via Gateway : ajouter `Authorization: Bearer <token>` (voir [Keycloak](../auth-keycloak.md)).
