# Frontend

Workspace Angular 19 : [`frontend/`](../frontend/) avec deux applications et une librairie partagée.

| Projet | Port | Keycloak client | Rôle requis |
|--------|------|-----------------|-------------|
| client-portal | 4200 | `client-portal` | `user` |
| admin-portal | 4201 | `admin-portal` | `admin` |
| shared | — | — | modèles + services API |

Base API : `http://localhost:8090` ([`api.config.ts`](../frontend/projects/shared/src/lib/config/api.config.ts))

---

## Client portal

### Pages

| Route | Description | Auth |
|-------|-------------|------|
| `/` | Accueil | public |
| `/track`, `/track/:id` | Suivi de colis / livraison | public |
| `/profile` | Profil client (`/clients/me`) | rôle `user` |
| `/login` | Connexion Keycloak | invité uniquement |

### Fonctionnalités

- Recherche / suivi de package
- Affichage des infos livraison (via Gateway → delivery + package)
- Connexion et gestion de session Keycloak
- Profil client authentifié

### Lancer

```powershell
cd frontend
npm install
npm run start:client
```

Compte : `client1` / `client123`

---

## Admin portal

### Pages

| Route | Description |
|-------|-------------|
| `/login` | Connexion admin |
| `/dashboard` | Vue d'ensemble / santé services |
| `/vehicles` | CRUD véhicules |
| `/packages` | Liste / détail colis |
| `/deliveries` | Livraisons |
| `/tracking` | Carte / suivi temps réel |
| `/assignments` | Affectations |
| `/drivers` | CRUD conducteurs |
| `/clients` | CRUD clients |

Toutes les routes métier sont protégées par `roleGuard('ADMIN')`.

### Lancer

```powershell
cd frontend
npm run start:admin
```

Compte : `admin1` / `admin123`

---

## Services API partagés

| Service Angular | Chemins Gateway |
|-----------------|-----------------|
| VehicleApiService | `/vehicles` |
| PackageApiService | `/packages` |
| DeliveryApiService | `/deliveries/...` |
| AssignmentApiService | `/assignment/api/assignments` |
| DriverApiService | `/drivers` |
| ClientApiService | `/clients` |
| TrackingApiService | `/tracking/api/tracking/...` |
| HealthApiService | `/*/health` |
| TrackingWsService | WebSocket (souvent `ws://localhost:8086/ws`) |

Le Bearer token Keycloak est attaché automatiquement aux appels vers `:8090`.

---

## CORS

Le Gateway autorise les origines `http://localhost:4200` et `http://localhost:4201`.

## Docker

Les images `client-portal` et `admin-portal` sont construites depuis `frontend/Dockerfile.client` et `Dockerfile.admin`, exposées sur 4200 et 4201.
