# Architecture

DeliverX repose sur Spring Cloud pour la découverte de services, la configuration centralisée et le routage API.

## Composants

| Composant | Port | Rôle |
|-----------|------|------|
| Eureka Server | 8761 | Service discovery |
| Config Server | 8888 | Configuration centralisée (Git) |
| API Gateway | 8090 | Point d'entrée unique |
| Microservices métier | 8081–8085 | Domaines fonctionnels |

## Flux de requête

1. Le client appelle le Gateway (`:8090`)
2. Le Gateway résout le service via Eureka (`lb://SERVICE-NAME`)
3. Le microservice cible traite la requête
4. La configuration est chargée depuis le Config Server (`config-repo/`)

## Communication inter-services

- **OpenFeign** : `delivery-service` appelle `package-service` pour récupérer les informations colis
- **Eureka** : enregistrement dynamique de tous les services

## Frontend

Application Angular 19 avec deux portails :

- Client Portal : `http://localhost:4200`
- Admin Portal : `http://localhost:4201`
