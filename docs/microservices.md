# Microservices — vue d'ensemble

| Service | Port | Eureka | Gateway | Base | Doc détaillée |
|---------|------|--------|---------|------|---------------|
| assignment-service | 8081 | ASSIGNMENT-SERVICE | `/assignment/**` | H2 | [Assignment](services/assignment-service.md) |
| driver-client-service | 8082 (Docker **8087**) | DRIVER-CLIENT-SERVICE | `/drivers/**`, `/clients/**` | MySQL | [Driver & Client](services/driver-client-service.md) |
| vehicle-service | 8083 | VEHICLE-SERVICE | `/vehicles/**` | H2 | [Vehicle](services/vehicle-service.md) |
| delivery-service | 8084 | DELIVERY-SERVICE | `/deliveries/**` | MySQL | [Delivery](services/delivery-service.md) |
| package-service | 8085 | PACKAGE-SERVICE | `/packages/**` | In-memory | [Package](services/package-service.md) |
| tracking-service | 8086 | TRACKING-SERVICE | `/tracking/**`, `/ws/**` | MongoDB | [Tracking](services/tracking-service.md) |

## driver-client-service

Microservice de gestion des conducteurs et clients avec :

- CRUD complet sur `/drivers` et `/clients` (recherche, filtres, pagination, tri)
- Sécurité Keycloak : écritures réservées au rôle `admin`, lectures publiques
- Self-service `/clients/me` (GET/PUT) basé sur le claim `email` du JWT
- Persistance MySQL (`driver_client_db`) avec historique d'audit **Hibernate Envers** (`drivers_aud`, `clients_aud`)

## Maturité

| Service | État |
|---------|------|
| assignment-service | CRUD + OpenFeign (delivery, driver, vehicle) |
| driver-client-service | CRUD drivers/clients + JWT Keycloak + Envers |
| vehicle-service | CRUD JPA H2 |
| delivery-service | CRUD JPA MySQL + preuves + Swagger |
| package-service | API lecture mock (3 colis) |
| tracking-service | REST + MongoDB + WebSocket STOMP + Swagger |

## Infrastructure associée

| Composant | Port | Rôle |
|-----------|------|------|
| eureka-server | 8761 | Service discovery |
| config-server | 8888 | Config centralisée (`config-repo/`) |
| GateWay | 8090 | Routage + JWT |
| Keycloak | 8080 | Identity provider |

Chaque page service décrit les endpoints, le modèle de données et **comment tester** le microservice.
