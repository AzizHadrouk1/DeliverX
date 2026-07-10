# Microservices

| Service | Port | Eureka Name | Gateway | Base de données |
|---------|------|-------------|---------|-----------------|
| assignment-service | 8081 | ASSIGNMENT-SERVICE | `/assignment/**` | H2 (`assignment_db` — conteneur `h2`) |
| driver-client-service | 8082 | DRIVER-CLIENT-SERVICE | `/drivers/**` | MySQL (`driver_client_db` — conteneur `mysql`) |
| vehicle-service | 8083 | VEHICLE-SERVICE | `/vehicles/**` | H2 (`vehicle_db` — conteneur `h2`) |
| delivery-service | 8084 | DELIVERY-SERVICE | `/deliveries/**` | MySQL (`delivery_db` — conteneur `mysql`) |
| package-service | 8085 | PACKAGE-SERVICE | `/packages/**` | MySQL (`package_db` — conteneur `mysql`) |
| tracking-service | — | — | — | MongoDB (`tracking_db` — conteneur `mongodb`, futur) |

## delivery-service

Microservice de gestion des livraisons avec :

- CRUD complet sur `/api/deliveries`
- Gestion des statuts avec transitions validées
- Preuves de livraison (photo, signature)
- Persistance MySQL via Spring Data JPA
- Swagger UI : `http://localhost:8084/swagger-ui.html`

## Maturité

| Service | État |
|---------|------|
| vehicle-service | CRUD JPA complet |
| delivery-service | CRUD JPA complet |
| package-service | Données mock in-memory |
| assignment-service | Health endpoint |
| driver-client-service | Health endpoint |
