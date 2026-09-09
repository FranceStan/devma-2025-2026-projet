# EcoBudget 🌿

Application Android moderne de gestion et de suivi budgétaire personnel en Francs CFA (FCFA), développée avec **Kotlin**, **Jetpack Compose** et respectant les principes stricts de l'**architecture MVVM** et du **flux de données unidirectionnel (UDF)**.

---

## 🏛️ Architecture de l'Application

L'application est conçue selon les recommandations officielles d'Android (Clean Architecture & MVVM), garantissant la séparation des responsabilités, la testabilité et l'évolutivité du code.

```
┌─────────────────────────────────────────────────────────────┐
│                       UI Layer (Compose)                    │
│   - EcoBudgetScreen (Scaffold, Hero Card, Filters, Lists)   │
│   - Components (MonthNavigatorBar, TransactionCard, Dialog) │
│   - Theme & Design Tokens (Material Design 3)               │
└──────────────────────────────▲──────────────────────────────┘
                               │ StateFlow<EcoBudgetUiState>
                               │ Events (Lambdas)
┌──────────────────────────────┴──────────────────────────────┐
│                      ViewModel Layer                        │
│   - EcoBudgetViewModel                                      │
│     * Expose StateFlow<EcoBudgetUiState> immuable           │
│     * Traite les flux réactifs (combine, stateIn)           │
│     * Gère la navigation temporelle (YearMonth)             │
│     * Gère les filtres multi-catégories et le CRUD          │
└──────────────────────────────▲──────────────────────────────┘
                               │ Flow<List<Transaction>>
                               │ Suspend Functions
┌──────────────────────────────┴──────────────────────────────┐
│                    Data & Domain Layer                      │
│   - TransactionRepository (Interface de contrat)            │
│   - FakeTransactionRepository / Room Data Source            │
│   - Models (Transaction, Category, YearMonth)               │
└─────────────────────────────────────────────────────────────┘
```

---

## 📦 Organisation des Paquetages (`com.example`)

```
com.example/
├── MainActivity.kt                 # Point d'entrée unique de l'application Android
│
├── model/                          # Modèles de domaine immuables (Data Classes & Enums)
│   ├── Category.kt                 # Énumération des catégories avec ressources strings & emojis
│   ├── Transaction.kt              # Modèle immuable représentant une dépense
│   └── YearMonth.kt                # Objet valeur gérant la navigation temporelle (Mois/Année)
│
├── data/
│   └── repository/                 # Abstraction des sources de données
│       ├── TransactionRepository.kt      # Interface du contrat de données
│       └── FakeTransactionRepository.kt  # Implémentation réactive en mémoire / persistance
│
├── viewmodel/                      # Couche de présentation logique
│   └── EcoBudgetViewModel.kt       # ViewModel & UiState (StateFlow, UDF)
│
└── ui/
    ├── screens/
    │   └── EcoBudgetScreen.kt      # Écran d'accueil principal (Hero Card, filtres, historique)
    ├── components/
    │   ├── AddTransactionDialog.kt # Dialogue d'ajout et d'édition de dépense
    │   ├── MonthNavigatorBar.kt    # Barre de navigation mensuelle (◀ Mois ▶)
    │   └── TransactionCard.kt      # Composant atomique représentant une carte de dépense
    └── theme/
        ├── Color.kt                # Palette de couleurs sombre & violette
        ├── Theme.kt                # Thème Material 3 global
        └── Type.kt                 # Typographie de l'application
```

---

## 🔄 Flux Unidirectionnel des Données (UDF)

1. **State Down (État descendant)** :
   - Le `EcoBudgetViewModel` orchestre l'état global via une data class immuable `EcoBudgetUiState`.
   - Cet état est exposé sous forme de `StateFlow<EcoBudgetUiState>` et collecté de façon sécurisée dans Compose via `collectAsStateWithLifecycle()`.
   - Aucune composante d'interface utilisateur ne possède ou ne mute directement l'état métier.

2. **Events Up (Événements ascendants)** :
   - Les interactions utilisateur (changement de mois, sélection d'une catégorie, ajout/modification/suppression d'une dépense) déclenchent des callbacks transmis au ViewModel.
   - Le ViewModel traite ces actions au sein de coroutines (`viewModelScope.launch`) et met à jour les flux réactifs sous-jacents.

---

## 💎 Modèle d'État Immuable (`EcoBudgetUiState`)

```kotlin
data class EcoBudgetUiState(
    val currentMonth: YearMonth = YearMonth.current(),
    val filteredTransactions: List<Transaction> = emptyList(),
    val monthTransactions: List<Transaction> = emptyList(),
    val allTransactions: List<Transaction> = emptyList(),
    val selectedCategories: Set<Category> = emptySet(),
    val monthlyBudget: Double = 500000.0,
    val totalSpent: Double = 0.0,
    val categorySpent: Double = 0.0,
    val remainingBudget: Double = 500000.0,
    val isAddDialogOpen: Boolean = false,
    val editingTransaction: Transaction? = null
)
```

---

## 🌟 Fonctionnalités Clés

- 📅 **Navigation mensuelle temporelle** : consultation et ventilation des dépenses mois par mois avec calcul automatique du budget disponible.
- 🎯 **Filtrage multi-catégories** : possibilité de combiner simultanément plusieurs catégories (*Alimentation*, *Transport*, *Loisirs*, *Logement*) avec indicateur visuel de coche et somme dynamique calculée.
- ✏️ **Édition et suppression intuitive** : modification directe au clic sur une carte de dépense avec formulaire pré-rempli.
- 🎨 **Design System Material 3 Dark Theme** : contraste élevé, typographie hiérarchisée, carte Hero violette moderne et respect des zones tactiles d'accessibilité (>= 48dp).
- 🌍 **Internationalisation & centralisation** : 100% des textes et libellés gérés via `res/values/strings.xml`.

---

## 🛠️ Stack Technique

- **Langage** : Kotlin (100%)
- **Interface UI** : Jetpack Compose & Material Design 3
- **Concurrence & Asynchronisme** : Coroutines Kotlin & Flows (`StateFlow`, `combine`, `stateIn`)
- **Cycle de Vie** : AndroidX Lifecycle (`collectAsStateWithLifecycle`, `ViewModel`)
- **Injection de Dépendances** : Injection par constructeur (Constructor Injection)
- **Système de Build** : Gradle Kotlin DSL (`build.gradle.kts`)
