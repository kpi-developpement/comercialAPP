# GestCom – Gestion commerciale pour PME marocaines

Application de gestion commerciale (ventes, achats, stock multi-dépôts, règlements, tableau de bord) :

- **backend/** : Spring Boot 3.5 (Java 21), PostgreSQL + Flyway, JWT, MapStruct, OpenPDF
- **frontend/** : Next.js 16 (App Router, TypeScript strict), CSS Modules purs, TanStack Query, react-hook-form + zod

---

## Démarrage rapide (développement)

Prérequis : Java 21, Node.js 20+, Docker (ou un PostgreSQL 14+ local).

```bash
# 1. Base de données
docker compose up -d

# 2. API (profil dev : migrations + données de démonstration)
cd backend
./mvnw spring-boot:run            # Windows : mvnw.cmd spring-boot:run
# -> http://localhost:3255  ·  Swagger : http://localhost:3255/swagger-ui.html

# 3. Frontend
cd frontend
cp .env.example .env.local        # NEXT_PUBLIC_API_URL=http://localhost:3255
npm install
npm run dev
# -> http://localhost:2534
```

Au premier démarrage en profil `dev`, Flyway charge `db/seed/V999__seed_dev.sql` (société, 3 dépôts,
11 familles, 30 articles, 10 clients, 5 fournisseurs, 4 utilisateurs) puis `DevDataSeeder` génère, **via les
services métier**, une année d'activité : commandes/réceptions fournisseurs (stock + PUMP), devis → BC → BL →
factures (dont livraisons partielles regroupées), factures directes, un avoir avec retour, un transfert, des
règlements (virements, chèques en portefeuille, impayés). Le tableau de bord, le stock et l'échéancier sont donc
cohérents dès l'ouverture.

### Comptes de démonstration (profil dev)

| Identifiant  | Mot de passe | Rôle        |
|--------------|--------------|-------------|
| `admin`      | `admin123`   | ADMIN       |
| `commercial` | `demo1234`   | COMMERCIAL  |
| `magasinier` | `demo1234`   | MAGASINIER  |
| `comptable`  | `demo1234`   | COMPTABLE   |

### Sans Docker

`backend/src/test/java/ma/gestcom/LocalDevApplication.java` démarre l'API avec un PostgreSQL embarqué
(zonky, données dans `backend/target/embedded-pg`) :

```bash
./mvnw spring-boot:test-run -Dspring-boot.run.main-class=ma.gestcom.LocalDevApplication
```

(Les postes Windows avec une politique de contrôle d'applications qui bloque les DLL non signées ne peuvent pas
l'utiliser : installez alors PostgreSQL ou Docker.)

---

## Variables d'environnement

### Backend

| Variable                  | Défaut (dev)                        | Rôle |
|---------------------------|-------------------------------------|------|
| `SPRING_PROFILES_ACTIVE`  | `dev`                               | `dev` (seed) ou `prod` |
| `DB_HOST` / `DB_PORT`     | `localhost` / `5432`                | PostgreSQL |
| `DB_NAME` / `DB_USER` / `DB_PASSWORD` | `gestcom` / `gestcom` / `gestcom` | |
| `JWT_SECRET`              | secret de dev (≥ 32 caractères)     | **obligatoire en prod** |
| `JWT_EXPIRATION_MINUTES`  | `720`                               | Durée de validité du jeton |
| `CORS_ALLOWED_ORIGINS`    | `http://localhost:2534`             | Origines du frontend (séparées par des virgules) |
| `GESTCOM_ADMIN_USERNAME`  | `admin`                             | Administrateur créé si la table des utilisateurs est vide |
| `GESTCOM_ADMIN_PASSWORD`  | `admin123` en dev, vide en prod     | Mot de passe de cet administrateur initial |
| `SWAGGER_ENABLED`         | `false` en prod                     | Exposer Swagger en prod |
| `SERVER_PORT`             | `3255`                              | |

### Frontend

| Variable              | Défaut                  |
|-----------------------|-------------------------|
| `NEXT_PUBLIC_API_URL` | `http://localhost:3255` |

### Production

```bash
cd backend && ./mvnw clean package
SPRING_PROFILES_ACTIVE=prod JWT_SECRET=... DB_HOST=... GESTCOM_ADMIN_PASSWORD=... java -jar target/gestcom-backend-1.0.0.jar
cd frontend && npm ci && npm run build && npm start
```

---

## Tests et vérifications

```bash
cd backend && ./mvnw test       # tests unitaires + test d'intégration Testcontainers (ignoré sans Docker)
cd frontend && npm run typecheck && npm run build
```

Tests unitaires : totaux et arrondis (`DocumentCalculatorTest`), PUMP (`PumpCalculatorTest`), transformation
partielle et quantités restantes (`DocumentLinkServiceTest`), effets stock / pas de double sortie
(`DocumentWorkflowTest`, `DocumentVenteWorkflowServiceImplTest`, `StockServiceImplTest`), affectation des
règlements et impayés (`ReglementServiceImplTest`), numérotation sans trou (`NumerotationServiceImplTest`),
montant en lettres (`AmountInWordsUtilTest`).

Test d'intégration `FullFlowIT` (PostgreSQL réel via Testcontainers) : réceptions (PUMP) → devis → BC → BL partiel
→ facture → règlement partiel → avoir avec retour, avec vérification du stock, des statuts et du solde client.

---

## Architecture

### Backend (`ma.gestcom`)

```
config/        Security, CORS, OpenAPI, auditing JPA, Jackson, propriétés gestcom.*
security/      JWT (JwtService, JwtAuthFilter), AuthController, matrice des rôles (Roles, DocumentAccess)
common/        BaseEntity (audit + @Version), PageResponse, ApiError, exceptions + GlobalExceptionHandler,
               enums, MoneyUtil, AmountInWordsUtil, FormatUtil, SpecificationUtil
modules/
  utilisateur/ parametre/ numerotation/ article/ tiers/ stock/
  document/    socle commun ventes + achats : AbstractDocument / AbstractDocumentLigne (@MappedSuperclass),
               DocumentCalculator, DocumentWorkflow (chaînes + effets stock), DocumentLinkService
               (quantités restantes), TransformationPlanner, DocumentPdfService
  vente/       DocumentVente (devis, BC, BL, facture, avoir) : CRUD, validation, transformation, regroupement
  achat/       DocumentAchat (BC fournisseur, réception, facture, avoir fournisseur)
  reglement/   règlements, affectations, cycle chèques/effets, soldes, échéancier
  dashboard/   requêtes natives agrégées en lecture seule
devdata/       DevDataSeeder (profil dev uniquement)
```

Chaque module : `controller/` (HTTP uniquement), `service/` + `impl/` (logique, `@Transactional`), `repository/`
(+ Specifications), `entity/`, `dto/`, `mapper/` (MapStruct). Les modules communiquent par leurs interfaces de
service ; les contrôleurs ne renvoient que des DTO.

Règles clés :

- **Numérotation** à la validation, compteur annuel par type verrouillé (`SELECT … FOR UPDATE`) dans la transaction
  de validation : pas de trou.
- **Stock** modifié uniquement par `StockService`, qui écrit toujours un `StockMouvement`. Le BL sort le stock ; une
  facture issue d'un BL ne le touche pas, une facture directe (ou issue d'un devis/BC) le sort ; l'avoir avec retour
  le réintègre. La réception (et la facture fournisseur directe) entre le stock et recalcule le **PUMP** global.
  Le flag `impacteStock` par ligne empêche toute double écriture.
- **Transformations** totales ou partielles : chaque ligne cible pointe sa ligne source ; un brouillon consomme les
  quantités restantes dès sa création et les restitue s'il est modifié ou supprimé.
- **Documents validés** non modifiables ; annulation possible pour devis / BC non transformés, sinon avoir.
- **Totaux** : arrondi HALF_UP à 2 décimales par ligne puis sommés ; TVA regroupée par taux ; recalcul serveur à
  chaque enregistrement.
- **Plafond de crédit** client : avertissement (non bloquant) renvoyé dans `warnings` à la validation.
- **Règlements** : affectation à une ou plusieurs factures (≤ reste à payer) ; un chèque/effet impayé restitue les
  montants affectés.

### Frontend (`frontend/src`)

```
app/                    routes (pages fines qui composent des composants de features)
components/ui/          kit UI pur CSS (Button, Input, Select, Combobox, Table, Modal, Drawer, Toast, …)
components/layout/      AppShell, Sidebar, Topbar, PageHeader, Breadcrumbs, FormGrid, FilterBar
features/<module>/      api/ (appels typés) · hooks/ (TanStack Query) · types/ · schemas/ (zod) · components/
features/documents/     grille de lignes, ArticlePicker, totaux, actions, transformation et regroupement partagés
                        par les 9 types de documents (configuration dans config/documentTypes.ts)
lib/                    api-client, query-client, format, auth, permissions, hooks de liste
```

Toutes les couleurs, espacements, rayons et tailles de texte viennent des variables de `app/globals.css`
(structure prête pour un thème sombre via `[data-theme="dark"]`). Les menus et actions sont masqués selon le rôle
(`lib/permissions.ts`, miroir de `security/Roles.java`) ; le backend reste l'autorité (`@PreAuthorize`).

### API principale

| Ressource | Endpoints |
|-----------|-----------|
| Auth | `POST /api/v1/auth/login`, `GET /api/v1/auth/me` |
| Articles / familles | `/api/v1/articles`, `/api/v1/articles/scan?valeur=`, `/api/v1/familles` |
| Tiers | `/api/v1/clients`, `/api/v1/fournisseurs` |
| Ventes | `/api/v1/ventes/{devis|commandes|livraisons|factures|avoirs}` + `/{id}/valider`, `/{id}/annuler`, `/{id}/transformer`, `/{id}/pdf`, `POST /api/v1/ventes/factures/grouper` |
| Achats | `/api/v1/achats/{commandes|receptions|factures|avoirs}` (mêmes actions) |
| Stock | `/api/v1/stock/{etat,alertes,valorisation,mouvements,depots,inventaires,transferts}` |
| Règlements | `/api/v1/reglements/{clients|fournisseurs}` + `/{id}/statut`, `/{id}/affectations`, `/soldes/{tiersId}`, `/echeancier` |
| Paramètres | `/api/v1/parametres/{societe,taux-tva,modes-reglement,numerotation}` |
| Dashboard | `GET /api/v1/dashboard?dateDebut=&dateFin=` |
