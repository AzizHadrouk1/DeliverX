# Package Service

Catalogue de **colis** (données mock en mémoire — 3 packages de démo).

| Propriété | Valeur |
|-----------|--------|
| Port | 8085 |
| Eureka | `PACKAGE-SERVICE` |
| Gateway | `/packages/**` |
| Base | Aucune (Map in-memory) |
| Package | `com.esprit.microservice.package_mgmt` |

!!! note
    La base MySQL `package_db` est créée par Docker pour une migration future, mais **n'est pas utilisée** par ce service aujourd'hui.

## Endpoints

| Méthode | Chemin | Description |
|---------|--------|-------------|
| GET | `/packages` | Liste |
| GET | `/packages/{id}` | Détail |
| GET | `/health`, `/packages/health`, `/hello` | Santé |

## Données seed

| ID | Tracking | Destination | Statut |
|----|----------|-------------|--------|
| 1 | DX-TRK-001 | Tunis | READY |
| 2 | DX-TRK-002 | Sfax | IN_TRANSIT |
| 3 | DX-TRK-003 | Sousse | DELIVERED |

## Comment tester

```powershell
curl http://localhost:8085/health
curl http://localhost:8085/packages
curl http://localhost:8085/packages/1

curl http://localhost:8090/packages
curl http://localhost:8090/packages/1
curl http://localhost:8090/packages/health
```

Consommé par delivery-service via OpenFeign (`GET /deliveries/package/1` sur le Gateway).
