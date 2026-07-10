# Bases de données

Chaque microservice possède sa **propre base** (pas de schéma partagé). L'infrastructure Docker expose trois moteurs : MySQL, H2 et MongoDB.

## Cartographie service → base

| Microservice | Moteur | Base / fichier | Conteneur |
|--------------|--------|----------------|-----------|
| delivery-service | MySQL | `delivery_db` | mysql |
| driver-client-service | MySQL | `driver_client_db` | mysql |
| package-service | — (in-memory) | `package_db` provisionnée mais **non utilisée** | mysql |
| assignment-service | H2 | `jdbc:h2:mem:assignmentdb` (local) | h2 (serveur dispo) |
| vehicle-service | H2 | `jdbc:h2:file:./data/vehicledb` | h2 / volume local |
| tracking-service | MongoDB | `tracking_db` | mongodb |

---

## MySQL (port 3306)

| Base | Utilisateur | Mot de passe | Service |
|------|-------------|--------------|---------|
| delivery_db | delivery_user | delivery_pass | delivery-service |
| driver_client_db | driver_client_user | driver_client_pass | driver-client-service |
| package_db | package_user | package_pass | (réservé) |

JDBC local :

```
jdbc:mysql://localhost:3306/delivery_db
jdbc:mysql://localhost:3306/driver_client_db
```

JDBC réseau Docker : host `mysql` au lieu de `localhost`.

### Schéma delivery_db

**Table `deliveries`**

| Colonne | Type | Notes |
|---------|------|-------|
| id | BIGINT PK | auto-increment |
| package_id, client_id, driver_id, vehicle_id | BIGINT | références logiques (pas de FK cross-service) |
| pickup_address, delivery_address | VARCHAR | |
| scheduled_date, actual_delivery_date | DATETIME | |
| status | VARCHAR (enum) | PENDING, ASSIGNED, PICKED_UP, IN_PROGRESS, DELIVERED, FAILED, CANCELLED |
| created_at | DATETIME | `@PrePersist` |

**Table `delivery_proofs`**

| Colonne | Type | Notes |
|---------|------|-------|
| id | BIGINT PK | |
| delivery_id | BIGINT UK | relation **1:1** avec Delivery |
| photo_url, signature, recipient_name | VARCHAR | |
| timestamp | DATETIME | |

### Schéma driver_client_db

**Table `drivers`**

| Colonne | Notes |
|---------|-------|
| id, first_name, last_name, email, phone | |
| license_number | |
| status | AVAILABLE, ON_DELIVERY, OFF_DUTY, SUSPENDED |
| vehicle_id | référence logique |
| created_at, updated_at | |
| tables `*_aud` | audit Hibernate Envers |

**Table `clients`**

| Colonne | Notes |
|---------|-------|
| id, first_name, last_name, email, phone | |
| company_name, address, city | |
| type | INDIVIDUAL, BUSINESS |
| status | ACTIVE, INACTIVE |

---

## H2

| Service | URL typique | Console |
|---------|-------------|---------|
| assignment-service | `jdbc:h2:mem:assignmentdb` | `/h2-console` sur :8081 |
| vehicle-service | `jdbc:h2:file:./data/vehicledb` | http://localhost:8083/h2-console |
| Conteneur h2 | TCP `localhost:9092` | http://localhost:8082 |

### Entité Assignment (`assignments`)

id, deliveryId, driverId, vehicleId, status (ASSIGNED | IN_PROGRESS | COMPLETED | CANCELLED), assignedAt, updatedAt

### Entité Vehicle (`vehicles`)

licensePlate, brand, model, type (VAN | TRUCK | MOTORCYCLE | CAR | BICYCLE), status (AVAILABLE | IN_USE | MAINTENANCE | OUT_OF_SERVICE), capacités, manufacturingYear, timestamps

---

## MongoDB (port 27017)

| Base | Utilisateur | Mot de passe |
|------|-------------|--------------|
| tracking_db | tracking_user | tracking_pass |

URI :

```
mongodb://tracking_user:tracking_pass@localhost:27017/tracking_db
```

### Collection `tracking_events`

deliveryId, latitude, longitude, speed, heading, status (IN_TRANSIT, OUT_FOR_DELIVERY, …), notes, timestamp

### Collection `delivery_routes`

deliveryId, waypoints, totalDistanceKm, optimized, timestamps

---

## Migration

Les services JPA utilisent :

```properties
spring.jpa.hibernate.ddl-auto=update
```

Les tables sont créées / mises à jour automatiquement au démarrage. Aucun outil Flyway/Liquibase n'est requis pour le développement.
