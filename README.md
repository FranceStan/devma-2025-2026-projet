# EcoBudget 🌿

## Contexte

EcoBudget est une application de gestion budgétaire mobile développée initialement en Kotlin natif avec Jetpack Compose pour Android. Le projet vise à migrer progressivement la logique métier et les modèles vers une architecture Kotlin Multiplatform, tout en conservant le comportement actuel de l’application Android et en préparant l’intégration future avec Compose Multiplatform.

## Objectifs de migration KMP

- conserver le comportement fonctionnel actuel d’EcoBudget ;
- créer un module partagé `shared` ;
- cibler Android et iOS avec Kotlin Multiplatform ;
- déplacer les modèles et règles métier dans `shared/src/commonMain` ;
- éviter toute dépendance `android.*`, `androidx.*` ou `java.*` dans le code partagé ;
- établir une base pour les futurs écrans multiplateformes ;
- documenter précisément les décisions techniques et la validation effectuée.

## Architecture du projet

Le dépôt est structuré comme suit :

- `app` : application Android native, avec Jetpack Compose et ViewModel Android ;
- `shared` : module commun KMP ;
- `shared/src/commonMain` : code multiplateforme pour modèles, règles métier et contrats de données ;
- `shared/src/commonTest` : tests Kotlin communs exécutables sur la JVM ;
- `shared/src/androidMain` : adaptateurs Android éventuels et dépendances spécifiques ;
- `shared/src/iosX64`, `iosArm64`, `iosSimulatorArm64` : cibles iOS supportées par le plugin KMP.

## Modules et rôle

### Module `app`

Le module Android conserve l’interface Compose et l’intégration avec le runtime Android. Il dépend du module `shared` pour réutiliser la logique commune et les modèles partagés.

### Module `shared`

Le module commun contient les structures de données et la logique purement métier qui ne dépendent ni de l’Android ni de l’UI. La migration actuelle couvre notamment :

- `com.example.shared.model.Category`
- `com.example.shared.model.Transaction`
- `com.example.shared.model.YearMonth`
- `com.example.shared.data.repository.TransactionRepository`
- `com.example.shared.data.repository.FakeTransactionRepository`

## Workflow Git

Le workflow utilisé dans le dépôt suit les branches fonctionnelles prévues :

- `main` : branche de référence stabilisée ;
- `develop` : branche de travail principale ;
- `feature/kmp-infrastructure` : configuration KMP ;
- `feature/shared-domain` : migration domain ;
- `feature/shared-data` : contracts et repositories ;
- `feature/shared-presentation` : state and presentation ;
- `feature/shared-resources` : ressources textuelles partagées ;
- `feature/android-validation` : validation Android ;
- `feature/technical-documentation` : documentation.

Chaque branche fonctionnelle est créée depuis `develop`, validée puis fusionnée dans `develop`.

## Fonctionnalités migrées

Les éléments déjà migrés dans cette base de travail sont :

- création du module `shared` ;
- configuration Kotlin Multiplatform avec Android + iOS targets ;
- ajout de dépendances KMP minimales (`kotlinx-coroutines-core`, `kotlinx-datetime`) ;
- centralisation des modèles métier dans `shared/src/commonMain` ;
- ajout d’un test de smoke dans `commonTest` ;
- raccordement du module `app` vers `project(":shared")` ;
- adaptation de l’UI Android pour utiliser les modèles partagés sans dépendances Android dans le code partagé.
- extraction de l’état et des commandes de budget dans `EcoBudgetStore` en `commonMain` ;
- adaptation du `EcoBudgetViewModel` Android pour déléguer au store partagé.

## Décisions techniques et compatibilité

### Remplacement de `java.time`

Les structures utilisées dans l’application initiale reposaient sur les classes Java de calendrier. Pour les préserver dans `commonMain`, la logique a été portée sur des types Kotlin multiplateformes et les calculs de mois ont été reprogrammés de manière purement commune.

### Remplacement de `ViewModel` Android

Le code de présentation Android reste dans `app`, tandis que le module partagé contient des modèles et règles métier sans dépendance Android. Cela permet d’isoler la logique d’état et d’ouvrir la voie à une future migration Compose Multiplatform.

### Remplacement de `LiveData`

Le dépôt reste basé sur `Flow` et `StateFlow`, qui sont compatibles `commonMain` et reprennent le comportement réactif attendu.

### Adaptation des ressources `R.string`

L’énumération `Category` dans le module commun ne contient plus de `labelResId` Android. Les libellés sont désormais transformés côté Android dans l’UI à l’aide de mapping explicites, ce qui évite toute dépendance `R.string` dans le code partagé.

### Gestion des coroutines

Les dépendances de coroutines Kotlin sont conservées en version compatible avec la version de Kotlin du projet. Les flux de données restent basés sur `Flow` pour une intégration simple avec l’UI Android actuelle.

## Fichiers migrés

### `shared/src/commonMain/kotlin/com/example/shared/model/Category.kt`

Rôle : énumération des catégories métier, sans dépendance Android.
Problème : `R.string` et les ressources Android empêchaient le code partagé de compiler en KMP.
Solution : garder uniquement les données purement métier et les emojis dans `commonMain`, avec les libellés Android mappés côté UI.
Test : `SharedSmokeTest` vérifie l’accessibilité des catégories et la structure du modèle.

### `shared/src/commonMain/kotlin/com/example/shared/model/Transaction.kt`

Rôle : modèle de transaction partagé.
Problème : les classes de l’application Android étaient directement dépendantes de l’UI et du contexte système.
Solution : utilisation d’un modèle immuable et indépendant des dépendances Android.
Compatibilité : compatible avec Android et iOS car il ne dépend que des types Kotlin standard.
Test : ajout d’un test de base pour vérifier le comportement de construction et la conservation des données.

### `shared/src/commonMain/kotlin/com/example/shared/model/YearMonth.kt`

Rôle : navigation de mois et calcul d’appartenance d’un timestamp à un mois donné.
Problème : `java.util.Calendar` et `SimpleDateFormat` sont JVM-only.
Solution : calculs élaborés via `kotlinx.datetime` et règles de navigation en Kotlin pur.
Compatibilité : le module partage la logique de date sans dépendance JVM.
Test : vérification du calcul du mois courant, de la navigation et des labels.

### `shared/src/commonMain/kotlin/com/example/shared/data/repository/TransactionRepository.kt`

Rôle : contrat de repository partagé pour les transactions.
Problème : la logique de données était initialement enfermée dans l’application Android.
Solution : création d’une interface de dépôt commune à partir de `Flow` et `suspend`.
Compatibilité : compatible car elle ne dépend pas de `AndroidViewModel`, `Context` ni de Room.
Test : le contrat est validé par le test de smoke ainsi que la repository factice dédiée.

### `shared/src/commonMain/kotlin/com/example/shared/data/repository/FakeTransactionRepository.kt`

Rôle : implémentation in-memory factice pour simuler les données.
Problème : l’ancien dépôt d’application restait spécifique au module Android et n’était pas partageable.
Solution : création d’un repository exemple compatible KMP sans dépendances Android.
Compatibilité : `Flow` + `MutableStateFlow` sont partagés par Kotlin Multiplatform.
Test : la réactivité et le chargement des données sont vérifiés par le test commun.

## Commandes de compilation et de test

Les commandes suivantes ont été utilisées ou sont recommandées pour la validation locale :

- `./gradlew.bat :shared:compileKotlinMetadata`
- `./gradlew.bat :shared:allTests`
- `./gradlew.bat :app:testDebugUnitTest`
- `./gradlew.bat :app:assembleDebug`

## Résultats de validation réels

La validation locale est opérationnelle avec Java 17 et le SDK Android configurés. Les contrôles suivants passent actuellement :

- `./gradlew.bat :shared:testDebugUnitTest --console=plain`
- `./gradlew.bat :app:assembleDebug --console=plain`

Le build Android a également confirmé l’intégration de la présentation partagée dans le module `app`, notamment après la centralisation des calculs dans `EcoBudgetDomain.summarizeMonth(...)`.

## Limites connues

- l’UI Android reste native et le passage à Compose Multiplatform n’est pas commencé ;
- la documentation et l’architecture KMP sont préparées mais les phases suivantes de migration plus avancées doivent être poursuivies dans des branches dédiées ;
- la validation Android complète dépend d’un SDK Android et d’un Gradle pleinement initialisés sur la machine locale.

## Améliorations possibles

- centraliser les textes fonctionnels dans une abstraction de ressources commune ;
- migrer les repositories vers des implémentations plus réalistes côté iOS/Android ;
- ajouter des tests unitaires plus détaillés sur les totaux, filtres et navigation mensuelle ;
- finaliser la préparation à Compose Multiplatform.

## État final

Le dépôt est laissé sur `develop` avec une base KMP opérationnelle et une documentation technique détaillée. La migration est progressive et prête pour les prochaines phases de partage de la présentation et des ressources.
