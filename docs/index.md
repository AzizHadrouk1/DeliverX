# DeliverX

DeliverX est une plateforme de livraison basée sur une architecture microservices Spring Boot.

## Contenu de la documentation

- [Architecture](architecture.md) — vue d'ensemble du système
- [Microservices](microservices.md) — liste des services et leurs rôles
- [Bases de données](databases.md) — MySQL, H2, MongoDB par service
- [Docker](docker.md) — conteneurs et commandes
- [Démarrage](getting-started.md) — installation et lancement
- [API Delivery Service](api/delivery-service.md) — endpoints et exemples

## Démarrage rapide

```bash
docker compose up -d
cd delivery-service && mvnw spring-boot:run
```

Documentation interactive : `mkdocs serve` puis ouvrir `http://127.0.0.1:8000`.
