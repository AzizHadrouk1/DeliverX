# Démarrage

## Prérequis

- Docker et Docker Compose
- Java 17
- Maven (ou Maven Wrapper inclus dans chaque service)

## 1. Démarrer les bases de données

```bash
docker compose up -d
```

Attendre que MySQL soit prêt (~30 secondes).

## 2. Démarrer l'infrastructure Spring Cloud

Dans l'ordre :

```bash
cd eureka-server && mvnw spring-boot:run
cd config-server && mvnw spring-boot:run
```

## 3. Démarrer les microservices

```bash
cd package-service && mvnw spring-boot:run
cd delivery-service && mvnw spring-boot:run
cd vehicle-service && mvnw spring-boot:run
# ... autres services
cd GateWay && mvnw spring-boot:run
```

Sous Windows PowerShell, utilisez `.\mvnw.cmd` à la place de `mvnw`.

## 4. Construire un service

```bash
cd delivery-service
mvnw clean package -DskipTests
```

## 5. Documentation MkDocs

```bash
pip install mkdocs mkdocs-material
mkdocs serve
```

Ouvrir `http://127.0.0.1:8000`.

## 6. Frontend (optionnel)

```bash
cd frontend
npm install
npm start
```

## Vérification

- Eureka : `http://localhost:8761`
- Gateway health : `http://localhost:8090/deliveries/health`
- Delivery Swagger : `http://localhost:8084/swagger-ui.html`
