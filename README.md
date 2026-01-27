# Projet Architecture - Système de Gestion des Employés et Fiches de Paie

## 🏗️ Architecture du Projet

Le projet est divisé en trois parties :

```
Achitecture_projet/
├── my-api/          # API REST
├── my-batch/        # Génération et envoi de fiches de paie automatique
└── mywebapp/        # Application Web
```

---

## Comptes par Défaut

**IMPORTANT** : Au premier lancement de l'application `my-api`, un compte RH administrateur et un compte Employé sont automatiquement créé dans la base de données.

### Voici leurs identifiants par défaut : 

- **RH**
- **Email** : `rh@gmail.com`
- **Mot de passe** : `rhAdmin`

- **Employé**
- **Email** : `gaetan.pezas@gmail.com`
- **Mot de passe** : `1234`

Le compte RH permet de se connecter immédiatement à l'application web pour gérer des salariés de type `EMPLOYE` ou `RH`.

---

## Partie 1 : my-api

### Description :
`my-api` est le backend de l'application pour la gestion des employés et des fiches de paie.

### Port :
- **8080** (port par défaut)

### Fonctionnalités :

#### CRUD Complet pour les Employés
L'API fournit tous les endpoints nécessaires pour gérer les employés :

**Endpoints disponibles** :
- `GET /api/employees` - Récupérer tous les employés
- `GET /api/employees/{id}` - Récupérer un employé par son ID
- `POST /api/employees` - Créer un nouvel employé
- `PUT /api/employees/{id}` - Modifier un employé existant
- `DELETE /api/employees/{id}` - Supprimer un employé

#### Endpoint pour Récupérer les Fiches de Paie
L'API permet de consulter les fiches de paie générées :

**Endpoints disponibles** :
- `GET /api/payslips` - Récupérer toutes les fiches de paie
- `GET /api/payslips/{id}` - Récupérer une fiche de paie spécifique
- `GET /api/payslips/employee/{employeeId}` - Récupérer toutes les fiches de paie d'un employé

#### Authentification
- `POST /api/auth/login` - Authentification des utilisateurs (RH et Employés)

### Configuration
**Fichier** : `my-api/src/main/resources/application.properties`
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=password
server.port=8080
```

## Partie 2 : my-batch

### Description
`my-batch` est un module de traitement par lots utilisant Spring Batch. Il génère automatiquement les fiches de paie mensuellement pour tous les employés et les envoie réellement sur l'adresse email renseigné.

### Port
- **8082**

### Fonctionnalités principales

#### Génération Automatique des Fiches de Paie
Le batch génère les fiches de paie en fonction des données stockées dans la base de données :

**Processus de calcul** :
1. Récupère tous les employés depuis la base de données
2. Pour chaque employé :
   - Calcule le **salaire brut** : `heures travaillées × taux horaire`
   - Calcule le **salaire net** : `salaire brut × 0.8` (20% de charges)
   - Génère un **PDF** de la fiche de paie
   - Enregistre la fiche de paie dans la base de données

**Exemple de calcul** :
- Employé : Jean Dupont
- Heures travaillées : 160h
- Taux horaire : 25€/h
- **Salaire brut** : 160 × 25 = 4000€
- **Salaire net** : 4000 × 0.8 = 3200€

#### Envoi des Fiches de Paie par Email
Après génération, chaque fiche de paie est envoyée par email à l'employé concerné :

**Configuration Email** :
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=gaetan.pezas@gmail.com
spring.mail.password=ijfjwbgnkfyucbpc
```

**Contenu de l'email** :
- **Destinataire** : Email de l'employé
- **Sujet** : "Votre fiche de paie pour [Mois] [Année]"
- **Corps** : Message avec les détails du salaire
- **Pièce jointe** : PDF de la fiche de paie

#### Déclenchement du Batch
Le batch peut être déclenché manuellement via un endpoint REST :

**Endpoint** :
- `POST /api/batch/run` - Lancer le traitement des fiches de paie


### Stockage des PDF
Les fiches de paie PDF sont stockées localement :
- **Répertoire** : `C:/payslips/`
- **Format du nom** : `payslip_{employeeId}_{year}_{month}.pdf`

### Configuration
**Fichier** : `my-batch/src/main/resources/application.properties`
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
server.port=8082
pdf.directory=C:/payslips/
spring.batch.job.enabled=false
```

## Partie 3 : mywebapp

### Description
`mywebapp` est l'interface web de l'application. Elle fournit deux espaces distincts : un pour les RH et un pour les employés.

### Port
- **8081**

### Fonctionnalités principales

#### Interface Web pour les RH
Les responsables RH disposent d'une interface complète pour gérer les employés :

**Fonctionnalités disponibles** :
1. **Ajouter un employé** :
   - Formulaire avec tous les champs nécessaires
   - Validation des données
   - Sauvegarde via l'API REST

2. **Modifier un employé** :
   - Modification de toutes les informations
   - Mise à jour du taux horaire
   - Mise à jour des heures travaillées

3. **Supprimer un employé** :
   - Suppression avec confirmation
   - Suppression en cascade des fiches de paie associées

4. **Visualiser la liste des employés** :
   - Tableau avec tous les employés
   - Recherche et filtrage
   - Tri par colonnes


#### Espace Personnel pour les Employés
Chaque employé peut consulter ses propres fiches de paie et ses informations personnelles :

**Fonctionnalités disponibles** :
1. **Consulter ses fiches de paie** :
   - Liste de toutes les fiches de paie générées
   - Affichage des détails (mois, année, salaire brut, salaire net)
   - Téléchargement du PDF

2. **Visualiser ses informations personnelles** :
   - Nom, prénom, email
   - Poste occupé
   - Taux horaire

#### Authentification

- Connexion avec email et mot de passe
- Redirection automatique selon le rôle :
  - **RH** → Interface de gestion des employés
  - **EMPLOYEE** → Espace personnel

---

## Démarrage avec Docker

### Lancement du Projet

Pour lancer l'ensemble du projet (PostgreSQL + my-api + mywebapp + my-batch) :

```bash
docker-compose up --build
```

Cette commande va :
1. Créer et démarrer le conteneur PostgreSQL
2. Compiler et démarrer my-api (port 8080)
3. Compiler et démarrer mywebapp (port 8081)
4. Compiler et démarrer my-batch (port 8082)
5. Créer automatiquement les comptes RH et EMPLOYEE par défaut

### Accès aux Services

Une fois les conteneurs démarrés :
- **Application Web** : http://localhost:8081
- **API REST** : http://localhost:8080
- **Swagger UI** : http://localhost:8080/swagger-ui.html
- **Batch** : http://localhost:8082

### Arrêt du Projet

Pour arrêter tous les conteneurs :

```bash
docker-compose down
```

## Utilisation

### 1. Connexion en tant que RH
1. Accéder à : http://localhost:8081
2. Se connecter avec :
   - **Email** : `rh@gmail.com`
   - **Mot de passe** : `rhAdmin`
3. Gérer les employés (ajouter, modifier, supprimer)

### 2. Ajouter un Employé
1. Cliquer sur "Ajouter un employé"
2. Remplir le formulaire
3. Valider

### 3. Générer les Fiches de Paie
1. Effectuer une requête POST vers :
```bash
curl -X POST http://localhost:8082/api/batch/run?month=YYYY-MM
```
2. Le batch génère les fiches de paie et envoie les emails

### 4. Consulter ses Fiches de Paie (Employé)
1. Accéder à : http://localhost:8081
2. Se connecter avec :
   - **Email** : `gaetan.pezas@gmail.com`
   - **Mot de passe** : `1234`
3. Consultation des informations personnelles et téléchargement des payslips