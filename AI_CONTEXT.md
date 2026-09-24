# EcoBudget — contexte projet pour IA

## 1. Objectif du projet

EcoBudget est une application de gestion budgétaire mobile conçue pour aider à suivre les dépenses mensuelles, filtrer par catégorie, naviguer entre les mois, et garder un contrôle sur le budget alloué.

Le projet a été démarré comme une application Android native en Kotlin + Jetpack Compose, puis progressivement migré vers une architecture Kotlin Multiplatform (KMP) afin de partager la logique métier et les modèles entre Android et une cible future iOS / Compose Multiplatform.

L’objectif principal est de :
- conserver le comportement fonctionnel existant de l’application Android ;
- réduire la logique métier dans du code portable ;
- préparer une base de code réutilisable pour un avenir multiplateforme ;
- rester ouvert à une contribution collaborative et transparente.

## 2. Vision du produit

Le produit attendu est une application de suivi budgétaire simple, claire et utile :
- visualiser les dépenses du mois courant ;
- naviguer entre les mois précédents et suivants ;
- filtrer par catégories de dépenses ;
- calculer le total dépensé et le budget restant ;
- ajouter, modifier et supprimer des transactions ;
- conserver un comportement fiable côté Android tout en préparant la migration KMP.

## 3. État actuel du projet

### Architecture actuelle
- `app/` : application Android native (Compose, ViewModel, UI)
- `shared/` : module KMP partagé pour les modèles, règles métier, contrats de données et logique d’état commune

### Modules et responsabilités
- `app` : UI Android, navigation, interactions utilisateur, intégration Android
- `shared` : logique commune non dépendante de l’UI ou de l’OS

### Ce qui a déjà été migré / stabilisé
- configuration Kotlin Multiplatform du module `shared`
- modèles partagés : `Category`, `Transaction`, `YearMonth`
- logique métier partagée : `EcoBudgetDomain`
- logique d’état de présentation partagée : `EcoBudgetPresentation`
- store d’état partagé : `EcoBudgetStore`
- contrats de repository communs et implémentation in-memory
- persistance Android Room derrière le contrat partagé
- tests Kotlin communs validés pour le module partagé

### Ce qui reste à faire dans le cadre de la migration progressive
- migration complète de l’UI commune si l’équipe décide d’aller vers Compose Multiplatform
- extraction de plus de règles de validation / state / business logic dans `shared`
- éventuellement remplacement de l’implémentation in-memory par une vraie source de données et un layer repository plus réaliste
- validation Android plus poussée sur l’application complète
- éventuelle préparation de ressources textuelles partagées

## 4. Détails techniques importants

### Technologies utilisées
- Kotlin
- Jetpack Compose
- ViewModel Android
- Kotlin Multiplatform (KMP)
- kotlinx.coroutines
- kotlinx.datetime
- Gradle Kotlin DSL

### Versions prises en compte
- Kotlin : 2.1.0
- AGP : 8.9.2
- Java target : 17

### Règles de migration KMP
- aucun code de `commonMain` ne doit dépendre de `android.*`, `androidx.*`, `java.*` ou du contexte Android ;
- les accès système spécifiques à la plateforme doivent passer par des APIs `expect/actual` ;
- la logique pure et portable doit rester dans `shared/src/commonMain` ;
- la logique UI/Android reste dans `app/` ;
- les tests communs doivent valider les règles métier sans dépendance Android.

### Bonnes pratiques du projet
- garder la migration progressive et compatible avec l’application existante ;
- maintenir les tests communs pour éviter la régression de la logique métier ;
- valider chaque étape avec Gradle avant de fusionner ;
- ne pas introduire de dépendance plateforme dans le code partagé.

## 5. Conventions de code

### Structure du module partagé
- `shared/src/commonMain/kotlin/com/example/shared/model/` : modèles métier communs
- `shared/src/commonMain/kotlin/com/example/shared/domain/` : règles métier et évaluation de l’état
- `shared/src/commonMain/kotlin/com/example/shared/data/repository/` : contrats et implémentations de données
- `shared/src/androidMain/kotlin/` : adaptateurs Android spécifiques
- `shared/src/commonTest/kotlin/` : tests Kotlin communs

### Conventions de nommage
- classes et objets en PascalCase ;
- fonctions de logique métier en verbes explicites ;
- `YearMonth` pour toute logique liée au mois ;
- `EcoBudgetPresentation` pour l’état calculé de l’UI commune ;
- `EcoBudgetStore` pour l’état réactif et les commandes de budget partagées ;
- `RoomTransactionRepository` pour l’implémentation persistante Android ;
- `TransactionRepository` comme contrat de données partagé.

## 6. Modèles métier clés

### `Category`
- énumération des catégories principales : `TRANSPORT`, `ALIMENTATION`, `LOISIRS`, `LOGEMENT`
- contient un emoji central pour l’interface
- ne doit pas dépendre de ressources Android

### `Transaction`
- modèle immuable comprenant : `id`, `title`, `amount`, `date`, `category`
- utilisé par la logique métier et par les repositories

### `YearMonth`
- représente un mois de manière portable
- supporte navigation `previous()`, `next()`
- permet de filtrer les transactions selon le mois courant

## 7. Logique métier actuelle

La logique métier partagée couvre :
- filtrage par mois et catégories ;
- calcul du total dépensé ;
- calcul du budget restant ;
- génération de l’état d’interface pour la vue courante ;
- navigation stable entre les mois ;
- validation des données de transactions.

Les fonctions principales sont :
- `EcoBudgetDomain.filterTransactionsForMonth(...)`
- `EcoBudgetDomain.summarizeMonth(...)`
- `EcoBudgetPresentation.buildUiState(...)`

## 8. Dépendances et compatibilité

### Règles de compatibilité importantes
- la logique de date utilise `kotlinx.datetime` avec des garde-fous d’opt-in lorsque nécessaire ;
- les APIs de système comme `System.currentTimeMillis()` ne doivent pas être appelées dans `commonMain` ;
- le code partagé doit rester compatible avec Android et les cibles Kotlin/Native.

### Dépendances clés du projet
- `kotlinx.coroutines-core`
- `kotlinx.coroutines-test`
- `kotlinx.datetime`
- `kotlin.test`

## 9. Workflow de développement recommandé

Le projet suit une logique de branches fonctionnelles :
- `main` / `master` : historiques du projet
- `develop` : base de travail actuelle
- branches `feature/...` : avancées fonctionnelles spécifiques

Pour chaque étape :
1. créer une branche dédiée ;
2. implémenter la fonctionnalité / correction ;
3. exécuter les tests ciblés ;
4. valider le comportement ;
5. committer ;
6. pousser la branche ;
7. fusionner dans `develop` ;
8. publier `develop` si le travail est validé.

## 10. Commandes de validation utiles

Voici les commandes de validation déjà utilisées ou à retenir :

- `./gradlew.bat :shared:testDebugUnitTest`
- `./gradlew.bat :shared:allTests`
- `./gradlew.bat :app:testDebugUnitTest`
- `./gradlew.bat :app:assembleDebug`

## 11. Points clés pour une IA qui reprend le projet

Une IA qui reprend ce dépôt doit savoir que :
- le projet est un projet d’évolution progressive vers KMP ;
- la logique métier doit être déplacée dans `shared` sans dépendance Android ;
- l’UI Android reste la couche de présentation stable pour l’instant ;
- la migration doit être progressive et compatible avec les fonctionnalités existantes ;
- les tests du module partagé sont une garantie de sécurité avant toute migration d’UI ou de donnée ;
- tout accès système / date / ressources Android doit être encapsulé proprement.

## 12. Problèmes déjà rencontrés et solutions appliquées

### Problèmes liés au KMP
- incompatibilité de versions Kotlin / AGP
- APIs expérimentales de `kotlinx.datetime`
- utilisation de `System` dans `commonMain`
- dépendances de test manquantes pour le module partagé

### Solutions appliquées
- version Kotlin alignée sur 2.1.0
- utilisation d’`expect/actual` pour les accès plateforme
- opt-in explicite pour les APIs de temps expérimentales
- ajout des dépendances KMP de test nécessaires
- validation de la logique commune avec tests Kotlin communs

## 13. Contexte projet pour contribution open source

Ce dépôt est pensé pour être repris et enrichi par plusieurs contributeurs. La structure et la documentation doivent rester lisibles, explicites et cohérentes. Les contributions doivent viser :
- la compatibilité multiplateforme ;
- la clarté du code ;
- la stabilité fonctionnelle pour Android ;
- la simplicité du maintien du projet ;
- une documentation compréhensible pour les IA et les humains.

## 14. Roadmap de développement

### Phase 1 — stabilisation KMP de base (terminée)
- créer le module `shared`
- configurer Android + iOS targets
- centraliser les modèles et la logique métier
- valider le partage de logique commune avec tests Kotlin communs

### Phase 2 — migration de la logique de présentation (terminée)
- déplacer les états d’UI et calculs de présentation dans `shared`
- conserver le comportement Android fonctionnel
- sécuriser la logique commune avec des tests

### Phase 3 — ressources et textes partagés (terminée)
- migrer les libellés vers une abstraction commune
- centraliser les textes métier dans `EcoBudgetStrings` en `commonMain`
- conserver les ressources Android pour les textes spécifiques à l’accessibilité et à la présentation
- éviter les dépendances directes vers `R.string` dans `commonMain`

### Phase 4 — validation Android complète
- tester l’application sur émulateur/physique (terminé)
- connecter les actions Ajouter et Supprimer au `EcoBudgetStore` (terminé)
- connecter le store au `RoomTransactionRepository` (terminé)
- vérifier l’intégration de la couche partagée dans le composant Android (terminé)

### Phase 5 — persistance et préparation multiplateforme
- ajouter une implémentation de persistance iOS derrière `TransactionRepository`
- remplacer progressivement les textes Android par une abstraction de ressources commune
- préparer les premiers composants Compose Multiplatform
