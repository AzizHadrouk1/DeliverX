# DeliverX

DeliverX est une plateforme de livraison construite en **architecture microservices** avec Spring Boot, Spring Cloud et Angular.

## Stack technique

| Composant | Technologie |
|-----------|-------------|
| Backend | Spring Boot 3.2.5, Java 17 |
| Discovery | Netflix Eureka |
| Configuration | Spring Cloud Config Server |
| API Gateway | Spring Cloud Gateway |
| Authentification | Keycloak 25 (realm `deliverx`) |
| Communication sync | OpenFeign |
| Temps réel | WebSocket STOMP (tracking) |
| Frontend | Angular 19 (client + admin) |
| Bases de données | MySQL 8, H2, MongoDB 7 |

## Documentation

| Page | Contenu |
|------|---------|
| [Architecture](architecture.md) | Vue d'ensemble du système |
| [Démarrage](getting-started.md) | Prérequis et lancement |
| [Docker](docker.md) | Conteneurs et ports |
| [Bases de données](databases.md) | Schémas et credentials |
| [Communication](communication.md) | OpenFeign et WebSocket |
| [Keycloak](auth-keycloak.md) | Auth JWT et rôles |
| [Frontend](frontend.md) | Portails Angular |
| [Microservices](microservices.md) | Catalogue des services |

## Démarrage rapide

```bash
docker compose up -d --build
```

Puis ouvrir :

- Client portal : http://localhost:4200
- Admin portal : http://localhost:4201
- Eureka : http://localhost:8761
- Keycloak : http://localhost:8080
- Documentation : http://localhost:8000

Comptes démo : `admin1` / `admin123` (admin) · `client1` / `client123` (user)

Documentation interactive : **http://localhost:8000** (Docker) ou `mkdocs serve` en local.
