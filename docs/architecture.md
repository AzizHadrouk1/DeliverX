# Architecture

DeliverX repose sur Spring Cloud pour la découverte de services, la configuration centralisée et le routage API.

## Composants

| Composant | Port | Rôle |
|-----------|------|------|
| Eureka Server | 8761 | Service discovery |
| Config Server | 8888 | Configuration centralisée (Git) |
| API Gateway | 8090 | Point d'entrée unique + validation JWT centralisée |
| Keycloak | 8080 | Authentification OAuth2 / émission des JWT (realm `deliverx`) |
| Microservices métier | 8081–8086 | Domaines fonctionnels |

## Flux de requête

1. Le client appelle le Gateway (`:8090`)
2. Le Gateway résout le service via Eureka (`lb://SERVICE-NAME`)
3. Le microservice cible traite la requête
4. La configuration est chargée depuis le Config Server (`config-repo/`)

## Sécurité

- **Keycloak** émet des JWT signés (realm `deliverx`, rôles portés par le client `driver-client-service`)
- Le **Gateway** valide chaque requête : GET publics, mutations authentifiées (défense au périmètre)
- **driver-client-service** revalide le JWT et applique les règles par rôle (défense en profondeur)

## Communication inter-services

- **OpenFeign** : `delivery-service` appelle `package-service` ; `assignment-service` appelle driver, vehicle et delivery
- **Eureka** : enregistrement dynamique de tous les services

## Frontend

Application Angular 19 avec deux portails :

- Client Portal : `http://localhost:4200`
- Admin Portal : `http://localhost:4201`
