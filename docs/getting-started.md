# Démarrage

## Prérequis

| Outil | Version minimale | Usage |
|-------|------------------|-------|
| Docker Desktop | Compose v2 | Conteneurs (DB, Keycloak, stack complète) |
| Java JDK | **17** | Microservices Spring Boot |
| Maven | Wrapper inclus (`mvnw`) | Build / run local |
| Node.js | **18+** | Frontend Angular (mode local) |
| npm | fourni avec Node | Dépendances frontend |
| Git | — | Cloner le dépôt |
| Python + pip | 3.x | Documentation MkDocs (optionnel) |

Vérifier :

```powershell
java -version
docker --version
docker compose version
node -v
npm -v
```

---

## Mode 1 — Docker full-stack (recommandé)

Depuis la racine du dépôt :

```powershell
docker compose up -d --build
```

Attendre que Eureka, Config Server, Keycloak et les microservices soient healthy (~2–5 min au premier build).

### URLs après démarrage

| Service | URL |
|---------|-----|
| Eureka | http://localhost:8761 |
| Keycloak Admin | http://localhost:8080 (admin / admin) |
| Gateway | http://localhost:8090 |
| Client portal | http://localhost:4200 |
| Admin portal | http://localhost:4201 |
| Documentation MkDocs | http://localhost:8000 |
| **Swagger Gateway (tous les APIs)** | **http://localhost:8090/swagger** |
| Swagger Gateway (ancien lien) | http://localhost:8090/swagger-ui.html (redirect) |
| Delivery Swagger (direct) | http://localhost:8084/swagger-ui.html |
| Tracking Swagger (direct) | http://localhost:8086/swagger-ui.html |

### Comptes démo Keycloak

| Utilisateur | Mot de passe | Rôle | Portail |
|-------------|--------------|------|---------|
| `admin1` | `admin123` | admin | Admin (:4201) |
| `client1` | `client123` | user | Client (:4200) |

### Smoke tests

```powershell
curl http://localhost:8090/assignment/health
curl http://localhost:8090/drivers/health
curl http://localhost:8090/vehicles/health
curl http://localhost:8090/deliveries/health
curl http://localhost:8090/packages/health
curl http://localhost:8090/tracking/health
```

### Swagger — erreur « Service Unavailable /aggregated-docs/... »

Cela signifie que le **Gateway ne trouve pas le microservice dans Eureka** (service arrêté ou Eureka redémarré pendant que le service tournait).

```powershell
docker compose ps
# Vérifier DELIVERY-SERVICE sur http://localhost:8761
docker compose restart delivery-service
curl http://localhost:8090/aggregated-docs/delivery
```

Puis recharger http://localhost:8090/swagger

### Arrêt

```powershell
docker compose down
# Avec suppression des volumes :
docker compose down -v
```

---

## Mode 2 — Développement local (hybride)

### 1. Bases + Keycloak uniquement

```powershell
docker compose up -d mysql h2 mongodb keycloak
```

### 2. Infrastructure Spring Cloud

```powershell
cd eureka-server
.\mvnw.cmd spring-boot:run

cd ..\config-server
.\mvnw.cmd spring-boot:run
```

### 3. Microservices (terminaux séparés)

Ordre conseillé : package → delivery → vehicle → driver-client → assignment → tracking → Gateway.

```powershell
cd package-service
.\mvnw.cmd spring-boot:run
```

Répéter pour chaque service. Le Gateway en dernier :

```powershell
cd GateWay
.\mvnw.cmd spring-boot:run
```

!!! note "Port driver-client"
    En local le service écoute sur **8082**. En Docker le port hôte est **8087** (`8087:8082`).

### 4. Frontend

```powershell
cd frontend
npm install
npm run start:client   # http://localhost:4200
npm run start:admin    # http://localhost:4201 (autre terminal)
```

---

## Documentation MkDocs

Avec Docker, la documentation est disponible sur **http://localhost:8000** (service `docs`).

En local (sans le conteneur) :

```powershell
pip install mkdocs mkdocs-material pymdown-extensions
mkdocs serve
```

Ouvrir http://127.0.0.1:8000
