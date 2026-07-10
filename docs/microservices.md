# Microservices

| Service | Port | Eureka Name | Gateway | Base de données |
|---------|------|-------------|---------|-----------------|
| assignment-service | 8081 | ASSIGNMENT-SERVICE | `/assignment/**` | H2 (`assignment_db` — conteneur `h2`) |
| driver-client-service | 8082 | DRIVER-CLIENT-SERVICE | `/drivers/**` | MySQL (`driver_client_db` — conteneur `mysql`) |
| vehicle-service | 8083 | VEHICLE-SERVICE | `/vehicles/**` | H2 (`vehicle_db` — conteneur `h2`) |
| delivery-service | 8084 | DELIVERY-SERVICE | `/deliveries/**` | MySQL (`delivery_db` — conteneur `mysql`) |
| package-service | 8085 | PACKAGE-SERVICE | `/packages/**` | MySQL (`package_db` — conteneur `mysql`) |
| tracking-service | 8086 | TRACKING-SERVICE | `/tracking/**` | MongoDB (`tracking_db` — conteneur `mongodb`) |

## delivery-service

Microservice de gestion des livraisons avec :

- CRUD complet sur `/api/deliveries`
- Gestion des statuts avec transitions validées
- Preuves de livraison (photo, signature)
- Persistance MySQL via Spring Data JPA
- Swagger UI : `http://localhost:8084/swagger-ui.html`

## driver-client-service

Microservice de gestion des conducteurs et clients avec :

- CRUD complet sur `/drivers` et `/clients` (recherche, filtres, pagination, tri)
- Sécurité Keycloak : écritures réservées au rôle `admin`, lectures publiques
- Self-service `/clients/me` (GET/PUT) basé sur le claim `email` du JWT
- Persistance MySQL (`driver_client_db`) avec historique d'audit **Hibernate Envers** (`drivers_aud`, `clients_aud`)

## Maturité

| Service | État |
|---------|------|
| vehicle-service | CRUD JPA complet |
| delivery-service | CRUD JPA complet |
| driver-client-service | CRUD JPA complet + sécurité Keycloak + audit Envers |
| assignment-service | CRUD complet + clients Feign (driver, vehicle, delivery) |
| package-service | Données mock in-memory |
| tracking-service | Suivi temps réel MongoDB + WebSocket |
