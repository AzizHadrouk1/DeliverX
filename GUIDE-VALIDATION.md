# Guide de validation — Ma partie : driver-client-service + Keycloak

> Moenes Jaouadi — validation semaine du 13-04-2026
> Ma partie couvre : le microservice **driver-client-service** (Spring Boot + MySQL + Envers)
> et la **sécurité Keycloak** de toute la plateforme (Gateway + service).

---

## Étape 0 — Démarrer le projet (5 min avant la validation)

```bash
docker compose up -d
```

Attendre ~2 minutes, puis vérifier que tout est "Up" :

```bash
docker compose ps
```

⚠️ **Ne jamais lancer `docker compose down -v`** (le `-v` efface les volumes = les données MySQL et le realm Keycloak).

---

## Étape 1 — Montrer que le microservice tourne

| Quoi | Lien | Attendu |
|------|------|---------|
| Eureka — le service est enregistré | http://localhost:8761 | `DRIVER-CLIENT-SERVICE` dans la liste |
| Health via le Gateway | http://localhost:8090/drivers/health | `{"status":"UP"}` |
| Liste des conducteurs (lecture publique) | http://localhost:8090/drivers | JSON |
| Liste des clients (lecture publique) | http://localhost:8090/clients | JSON |
| Recherche + pagination | http://localhost:8090/drivers/search?q=&page=0&size=5 | page JSON |

**À dire** : le front et les testeurs ne passent QUE par le Gateway (:8090), jamais par le port direct du service — c'est le pattern API Gateway.

---

## Étape 2 — Montrer la persistance MySQL + audit Envers

```bash
docker exec mysql mysql -u driver_client_user -pdriver_client_pass -e "USE driver_client_db; SHOW TABLES;"
```

Attendu : `drivers`, `clients`, **`drivers_aud`, `clients_aud`, `revinfo`**

**À dire** :
- Chaque microservice a SA base (pas de schéma partagé) — ici `driver_client_db` sur le conteneur MySQL.
- Les tables `_aud` + `revinfo` = **Hibernate Envers** : chaque modification d'un driver/client est historisée automatiquement (valeur ajoutée).
- Le schéma est généré par Hibernate (`ddl-auto=update`), pas de migration manuelle.

---

## Étape 3 — Montrer Keycloak (le cœur de ma partie)

### 3a. La configuration

| Quoi | Lien |
|------|------|
| Console admin Keycloak | http://localhost:8080/admin — login `admin` / `admin` |
| Le realm | menu déroulant en haut à gauche → **deliverx** |
| Les clients | Clients → `admin-portal`, `client-portal` (publics), `driver-client-service` (confidentiel) |
| Les rôles | Clients → driver-client-service → Roles → `admin`, `user` |
| Les utilisateurs | Users → `admin1` (rôle admin), `client1` (rôle user) |

**À dire** : le realm entier est versionné dans `docker/keycloak/deliverx-realm.json` et importé automatiquement au démarrage du conteneur (`start-dev --import-realm`) → configuration reproductible, rien de fait à la main.

### 3b. Le test automatisé — LA démonstration clé

```bash
bash scripts/validate-driver-client-auth.sh
```

Résultat attendu (les 4 lignes prouvent toute la chaîne) :

```
GET  /drivers  (public)     -> 200   lecture publique
POST /drivers  sans token   -> 401   bloqué : pas authentifié
POST /drivers  rôle user    -> 403   bloqué : authentifié mais mauvais rôle
POST /drivers  rôle admin   -> 201   créé : bon rôle
```

**À dire** : la sécurité est en **deux couches** (défense en profondeur) :
1. **Gateway** (`GateWay/src/main/java/org/example/gateway/config/SecurityConfig.java`) — filtre global : GET publics, toute mutation exige un JWT Keycloak valide.
2. **driver-client-service** (`driver-client-service/src/main/java/com/esprit/microservice/driverclient/config/SecurityConfig.java`) — revalide le JWT lui-même et applique les règles fines : POST/PUT/DELETE → `hasRole("ADMIN")`, `/clients/me` → tout utilisateur authentifié.

### 3c. Le self-service `/clients/me` (valeur ajoutée)

Obtenir un token `client1` puis appeler `/clients/me` — le service retrouve le profil via le **claim `email` du JWT** :

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/realms/deliverx/protocol/openid-connect/token \
  -d "grant_type=password&client_id=client-portal&username=client1&password=client123" \
  | grep -o '"access_token":"[^"]*"' | sed 's/"access_token":"//;s/"//')

curl http://localhost:8090/clients/me -H "Authorization: Bearer $TOKEN"
```

Attendu : **200** avec le profil de `client1@deliverx.com`.

---

## Étape 4 — Démo front (la chaîne complète en une action)

1. Ouvrir **http://localhost:4201** (admin-portal)
2. Se connecter : **`admin1` / `admin123`** (vrai login Keycloak, pas un mock)
3. Aller dans **Drivers** → créer un conducteur
4. (Optionnel) Prouver la persistance :
   ```bash
   docker exec mysql mysql -u driver_client_user -pdriver_client_pass -e "SELECT id, first_name, last_name, email FROM driver_client_db.drivers;"
   ```

**À dire** : cette seule action traverse TOUTE l'architecture :
login Keycloak → JWT avec rôles → interceptor Angular attache le token → Gateway valide → service revalide + vérifie le rôle → MySQL + audit Envers.

---

## Étape 5 — Questions probables et réponses

**Q : Pourquoi les rôles sont dans `resource_access` et pas `realm_access` ?**
R : Ce sont des **rôles de client** (portés par le client Keycloak `driver-client-service`), pas des rôles de realm. Dans le JWT ils vivent sous `resource_access.driver-client-service.roles`. Les deux `SecurityConfig` extraient exactement ce chemin et mappent chaque rôle vers `ROLE_<NOM>` (convention Spring Security).

**Q : Pourquoi le service revalide le token si le Gateway le fait déjà ?**
R : Défense en profondeur. Si quelqu'un contourne le Gateway et appelle le port du service directement, le service se protège quand même. Le Gateway = filtre grossier au périmètre ; le service = règles métier fines (qui a le droit de faire quoi).

**Q : Comment le service vérifie un JWT sans appeler Keycloak à chaque requête ?**
R : Au démarrage, il télécharge les **clés publiques** de Keycloak (JWKS). Ensuite chaque JWT est vérifié **localement** : signature (clé publique), expiration, issuer. Zéro appel réseau par requête → stateless et rapide.

**Q : Pourquoi `localhost:8080` marchait sur la machine mais cassait dans Docker ?** *(c'est dans mon historique git)*
R : Dans un conteneur, `localhost` = le conteneur lui-même, pas la machine hôte. Le Gateway n'arrivait plus à télécharger les clés JWKS → 500 sur toute requête authentifiée. Fix : surcharger **uniquement `jwk-set-uri`** vers `http://keycloak:8080` (le nom du service sur le réseau Docker), en gardant `issuer-uri` sur `localhost:8080` car il doit correspondre **exactement** au claim `iss` du token (émis pour un client qui demande le token depuis l'hôte).

**Q : Pourquoi MySQL et pas H2 ?**
R : Au départ le service tournait sur un fichier H2 local — mais l'infra Docker (`docker/mysql/init.sql`) et la doc prévoyaient déjà `driver_client_db` en MySQL. J'ai aligné le code sur l'architecture cible : connecteur `mysql-connector-j`, datasource centralisée dans `config-repo/DRIVER-CLIENT-SERVICE.properties`, et en Docker une variable d'environnement surcharge l'hôte (`mysql:3306`).

**Q : C'est quoi Envers ?**
R : Extension Hibernate d'**audit automatique** : chaque insert/update/delete sur `Driver` ou `Client` crée une ligne dans `drivers_aud`/`clients_aud` avec un numéro de révision (`revinfo`). On peut reconstituer l'historique complet d'une entité.

---

## Récap des liens

| | Lien |
|---|---|
| Eureka | http://localhost:8761 |
| Keycloak admin | http://localhost:8080/admin (`admin`/`admin`) |
| Realm deliverx | http://localhost:8080/realms/deliverx |
| Gateway | http://localhost:8090 |
| Drivers (public) | http://localhost:8090/drivers |
| Clients (public) | http://localhost:8090/clients |
| Admin portal | http://localhost:4201 (`admin1`/`admin123`) |
| Client portal | http://localhost:4200 (`client1`/`client123`) |
| Script de validation | `bash scripts/validate-driver-client-auth.sh` |

## Récap des fichiers clés de ma partie

| Fichier | Rôle |
|---|---|
| `driver-client-service/src/main/java/.../config/SecurityConfig.java` | Règles de sécurité fines + extraction des rôles JWT |
| `GateWay/src/main/java/.../config/SecurityConfig.java` | Sécurité centralisée au Gateway |
| `docker/keycloak/deliverx-realm.json` | Realm versionné (clients, rôles, users de test) |
| `driver-client-service/src/main/java/.../controller/` | REST : DriverController, ClientController (dont `/clients/me`) |
| `config-repo/DRIVER-CLIENT-SERVICE.properties` | Config centralisée (datasource MySQL, Eureka) |
| `scripts/validate-driver-client-auth.sh` | Test automatisé 401/403/201 |

## Si quelque chose casse le jour J

```bash
# voir pourquoi un conteneur est down
docker logs driver-client-service --tail 50
docker logs gateway --tail 50

# redémarrer un service précis
docker compose up -d driver-client-service

# tout relancer proprement (SANS -v !)
docker compose down && docker compose up -d
```
