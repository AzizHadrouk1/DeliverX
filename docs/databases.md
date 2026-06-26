# Bases de données

Chaque microservice utilise sa propre base de données. Aucun partage de schéma entre services.

L'infrastructure Docker repose sur **3 conteneurs** : un MySQL, un H2 et un MongoDB.

## Conteneur MySQL (`mysql` — port 3306)

| Base | Utilisateur | Mot de passe | Microservice |
|------|-------------|--------------|--------------|
| delivery_db | delivery_user | delivery_pass | delivery-service |
| driver_client_db | driver_client_user | driver_client_pass | driver-client-service |
| package_db | package_user | package_pass | package-service |

URLs JDBC (depuis la machine hôte) :

```
jdbc:mysql://localhost:3306/delivery_db
jdbc:mysql://localhost:3306/driver_client_db
jdbc:mysql://localhost:3306/package_db
```

URL JDBC delivery-service (réseau Docker) :

```
jdbc:mysql://mysql:3306/delivery_db
```

## Conteneur H2 (`h2` — port TCP 9092, console 8082)

| Base | Microservice |
|------|--------------|
| assignment_db | assignment-service |
| vehicle_db | vehicle-service |

URLs JDBC (depuis la machine hôte) :

```
jdbc:h2:tcp://localhost:9092/./assignment_db
jdbc:h2:tcp://localhost:9092/./vehicle_db
```

URLs JDBC (réseau Docker) :

```
jdbc:h2:tcp://h2:1521/./assignment_db
jdbc:h2:tcp://h2:1521/./vehicle_db
```

Console H2 : http://localhost:8082

> Les services assignment et vehicle utilisent encore H2 fichier local par défaut. Le conteneur H2 est provisionné pour une migration future.

## Conteneur MongoDB (`mongodb` — port 27017)

| Base | Utilisateur | Mot de passe | Microservice |
|------|-------------|--------------|--------------|
| tracking_db | tracking_user | tracking_pass | tracking-service (futur) |

URI MongoDB :

```
mongodb://tracking_user:tracking_pass@localhost:27017/tracking_db
```

## Migration automatique

Le delivery-service utilise Hibernate avec :

```properties
spring.jpa.hibernate.ddl-auto=update
```

Les tables sont créées et mises à jour automatiquement à partir des entités JPA.
