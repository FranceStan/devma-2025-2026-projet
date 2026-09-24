# EcoBudget — Synthèse technique KMP

## 1. Présentation et architecture

EcoBudget est une application mobile de suivi budgétaire. Elle permet de consulter les dépenses mensuelles, naviguer entre les mois, filtrer par catégorie, afficher les statistiques de consommation, ajouter/modifier/supprimer des transactions et calculer le budget restant.

Le projet a commencé comme une application Android native Kotlin + Jetpack Compose. Il évolue progressivement vers Kotlin Multiplatform (KMP) afin de partager les modèles, les règles métier, l’état de présentation et certains composants Compose entre Android et iOS.

### Architecture globale

```text
app/
  Application Android native
  Jetpack Compose, ViewModel, Room, ressources Android
  Adaptateurs de plateforme et intégration UI

shared/
  Module Kotlin Multiplatform
  commonMain : modèles, domaine, état, textes, UI Compose partagée
  commonTest : tests de domaine, présentation et composants partagés
  androidMain : implémentations spécifiques Android
  cibles iOS : configuration KMP préparée
```

### Responsabilités

| Module | Responsabilité |
|---|---|
| `app` | Activity Android, écran Compose, ViewModel Android, Room et ressources `R.string` restantes |
| `shared` | Modèles métier, calculs, état réactif, contrats de repository, textes communs et UI Compose portable |
| `shared/commonMain` | Code sans dépendance directe à `android.*`, `androidx.*`, `java.*` ou `Context` |
| `shared/commonTest` | Tests multiplateformes de la logique métier et de l’état partagé |

### Versions principales

- Kotlin : `2.1.0`
- Android Gradle Plugin : `8.9.2`
- Java cible : `17`
- Kotlin Multiplatform : Android et cibles iOS configurées
- Compose Multiplatform : plugin `org.jetbrains.compose` dans `shared`
- Room : persistance Android
- Coroutines et Flow : état réactif partagé
- `kotlinx.datetime` : logique de mois et dates portables

## 2. Fichiers migrés vers `commonMain`

| Domaine | Fichier | Rôle |
|---|---|---|
| Modèle | `shared/src/commonMain/kotlin/com/example/shared/model/Category.kt` | Catégories, emojis, libellés portables et clés métier |
| Modèle | `shared/src/commonMain/kotlin/com/example/shared/model/Transaction.kt` | Transaction immuable partagée |
| Modèle | `shared/src/commonMain/kotlin/com/example/shared/model/YearMonth.kt` | Navigation mensuelle, label du mois et filtrage par timestamp |
| Domaine | `shared/src/commonMain/kotlin/com/example/shared/domain/EcoBudgetDomain.kt` | Filtrage, totaux, budget restant et statistiques par catégorie |
| Présentation | `shared/src/commonMain/kotlin/com/example/shared/domain/EcoBudgetPresentation.kt` | Construction de `EcoBudgetUiState` |
| Store | `shared/src/commonMain/kotlin/com/example/shared/domain/EcoBudgetStore.kt` | Navigation, filtres, dialogues et mutations de transactions |
| Données | `shared/src/commonMain/kotlin/com/example/shared/data/repository/TransactionRepository.kt` | Contrat portable de repository |
| Données | `shared/src/commonMain/kotlin/com/example/shared/data/repository/FakeTransactionRepository.kt` | Repository in-memory de test et de démonstration |
| Ressources | `shared/src/commonMain/kotlin/com/example/shared/resources/EcoBudgetStrings.kt` | Textes métier portables : dialogue, actions, devise, dates et statistiques |
| UI Compose | `shared/src/commonMain/kotlin/com/example/shared/ui/components/CategoryExpenseBreakdown.kt` | Statistiques de dépenses par catégorie avec jauges horizontales |

### Contrats conservés côté Android

Room reste Android-specific :

- `app/src/main/java/com/example/data/repository/TransactionEntity.kt`
- `app/src/main/java/com/example/data/repository/TransactionDao.kt`
- `app/src/main/java/com/example/data/repository/EcoBudgetDatabase.kt`
- `app/src/main/java/com/example/data/repository/RoomTransactionRepository.kt`

Ces classes implémentent le contrat `shared.TransactionRepository`, ce qui permet de remplacer Room par une autre solution sur iOS.

## 3. Changements techniques majeurs

### UUID et remplacement des transactions

- **Problème rencontré**
  - La création utilisait `UUID.randomUUID()::toString`.
  - Cette référence capturait un UUID unique au moment de la création du store et le réutilisait pour les insertions suivantes.
  - Room recevait donc le même identifiant primaire et remplaçait la transaction précédente.

- **Choix technique appliqué**
  - Génération différée avec une lambda : `UUID.randomUUID().toString()`.
  - DAO Room configuré avec `OnConflictStrategy.ABORT` au lieu de `REPLACE`.

- **Justification multiplateforme**
  - Le contrat et le modèle `Transaction` restent dans `commonMain`.
  - La génération d’identifiant est injectée dans `EcoBudgetStore`, ce qui permet une autre stratégie sur iOS.
  - Room ne remplace plus silencieusement une entité distincte en cas de collision.

### AGP 8.9.2

- **Problème rencontré**
  - L’IDE Android local ne supportait pas la version initiale AGP `8.10.1`.
  - La configuration Gradle échouait avant la compilation de l’application.

- **Choix technique appliqué**
  - Version stable supportée localement : `agp = "8.9.2"` dans `gradle/libs.versions.toml`.
  - Les plugins Android application et Android library utilisent cette version via le catalogue.

- **Justification multiplateforme**
  - Ce changement concerne uniquement l’outillage Android et ne modifie aucun code `commonMain`.
  - Kotlin Multiplatform conserve ses cibles et son contrat portable.
  - La même base `shared` peut être consommée par Android et les futures cibles iOS.

### Remplacement de `NumberFormat`

- **Problème rencontré**
  - `java.text.NumberFormat` et `java.util.Locale` étaient utilisés dans le composant de statistiques.
  - Ces imports Java empêchent le déplacement du composant vers `commonMain`.

- **Choix technique appliqué**
  - Suppression de `NumberFormat`, `Locale` et de la dépendance Java.
  - Formatage Kotlin portable avec une fonction simple : valeur entière via `toLong()`, sinon `toString()`.

- **Justification multiplateforme**
  - Le composant peut compiler sur Android et Kotlin/Native sans API JVM.
  - Une localisation avancée pourra être ajoutée plus tard par une abstraction `expect/actual` ou une couche de ressources dédiée.

### Déplacement Compose Multiplatform

- **Problème rencontré**
  - `CategoryExpenseBreakdown.kt` dépendait du package UI Android `com.example.ui.theme`.
  - Le déplacement direct vers `commonMain` aurait conservé des imports de plateforme et empêché la compilation KMP.
  - La structure précédente de l’écran plaçait également l’en-tête dans le contenu défilant, ce qui provoquait un rendu noir et une boucle `MeasureAndLayoutDelegate` sur l’émulateur.

- **Choix technique appliqué**
  - Déplacement vers `shared/src/commonMain/kotlin/com/example/shared/ui/components/CategoryExpenseBreakdown.kt`.
  - Remplacement des couleurs du thème Android par des `Color` portables dans le composant partagé.
  - Ajout du plugin Compose Compiler Kotlin et des dépendances Compose Multiplatform dans `shared/build.gradle.kts`.
  - Import Android mis à jour vers `com.example.shared.ui.components.CategoryExpenseBreakdown`.
  - `EcoBudgetCleanHeader()` déplacé dans `Scaffold.topBar`.
  - Le contenu principal utilise un `LazyColumn` unique pour les statistiques et les transactions.

- **Justification multiplateforme**
  - Le composant utilise uniquement les APIs Compose disponibles dans `commonMain`.
  - Aucun `Context`, ressource Android, thème Android ou API Java n’est requis.
  - Android consomme le composant via le module `shared`; iOS pourra réutiliser la même fonction Compose.

### Statistiques par catégorie

- `EcoBudgetDomain.categoryBreakdown(...)` calcule le total et le pourcentage de chaque catégorie pour le mois sélectionné.
- `EcoBudgetPresentation` expose les résultats dans `EcoBudgetUiState.categoryStatistics`.
- `CategoryExpenseBreakdown` affiche une jauge horizontale par catégorie.
- Une jauge devient rouge lorsque la catégorie dépasse `50 %` de la dépense mensuelle.
- Les catégories sans dépense restent présentes avec un total et un pourcentage à zéro.

## 4. Validation fonctionnelle et technique

### Validation émulateur

L’application a été installée et testée sur un émulateur Android Medium Phone API 36.

Scénarios validés :

- ouverture du dialogue « Nouvelle dépense » ;
- ajout de plusieurs transactions sans écrasement ;
- persistance Room après modification et suppression ;
- suppression d’une transaction depuis sa carte ;
- navigation mois précédent, mois suivant et retour au mois courant ;
- filtrage par catégorie simple et multiple ;
- calcul du budget total et du budget restant ;
- affichage des statistiques par catégorie ;
- affichage de l’alerte rouge au-delà de `50 %` ;
- défilement complet de l’écran principal ;
- affichage de l’en-tête, des statistiques et de la liste des transactions.

### Validation Gradle

La compilation Android a été validée avec :

```text
./gradlew.bat :shared:compileDebugKotlinAndroid --console=plain
./gradlew.bat :shared:testDebugUnitTest --console=plain
./gradlew.bat :app:testDebugUnitTest --console=plain
./gradlew.bat :app:assembleDebug --console=plain
```

Résultat attendu et obtenu sur l’environnement Android configuré :

```text
BUILD SUCCESSFUL
```

Les tests communs couvrent notamment :

- navigation `YearMonth` ;
- filtrage par catégorie ;
- calcul des totaux ;
- calcul des pourcentages par catégorie ;
- état du store partagé ;
- ajout et fermeture du dialogue après sauvegarde ;
- stabilité des ressources textuelles partagées.

### Limites connues

- Room est actuellement l’implémentation de persistance Android.
- Une implémentation iOS de `TransactionRepository` reste à créer.
- La compilation des cibles Kotlin/Native iOS nécessite un environnement macOS/Xcode.
- Les ressources Android restantes, notamment certaines descriptions d’accessibilité, pourront être migrées vers une abstraction de ressources commune.

## État du projet

La base Android est fonctionnelle et validée sur émulateur API 36. Le module `shared` contient désormais les modèles, la logique métier, l’état de présentation, les textes partagés et un premier composant Compose Multiplatform. La prochaine étape consiste à brancher une persistance iOS sur `TransactionRepository`, puis à étendre progressivement la migration des écrans Compose vers `commonMain`.
