# Docker

## Prérequis

- Docker Desktop
- Docker Compose v2

## Architecture (3 conteneurs)

| Conteneur | Image | Ports | Bases hébergées |
|-----------|-------|-------|-----------------|
| mysql | mysql:8.0 | 3306 | delivery_db, driver_client_db, package_db |
| h2 | oscarfonts/h2 | 9092 (TCP), 8082 (console) | assignment_db, vehicle_db |
| mongodb | mongo:7 | 27017 | tracking_db |

## Démarrer les bases de données

```bash
docker compose up -d
```

## Vérifier l'état

```bash
docker compose ps
```

Vérifier les bases MySQL :

```bash
docker exec -it mysql mysql -u delivery_user -pdelivery_pass -e "SHOW DATABASES;"
```

## Arrêter les conteneurs

```bash
docker compose down
```

## Supprimer les conteneurs et volumes

```bash
docker compose down -v
```

## Reconstruction complète

```bash
docker compose down -v
docker compose up -d --force-recreate
```

## Réseau

Tous les conteneurs sont sur le réseau `deliverx-network`.

- Depuis la machine hôte : utilisez `localhost` et le port mappé (ex. `localhost:3306`).
- Depuis un conteneur applicatif sur le même réseau : utilisez le nom du service (`mysql`, `h2`, `mongodb`).

## Initialisation MySQL

Le script [`docker/mysql/init.sql`](../docker/mysql/init.sql) crée automatiquement les 3 bases et les utilisateurs au premier démarrage du conteneur `mysql`.
