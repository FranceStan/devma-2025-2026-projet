package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.EcoBudgetDatabase
import com.example.data.repository.RoomTransactionRepository
import com.example.data.repository.TransactionRepository
import com.example.shared.domain.EcoBudgetStore
import com.example.shared.domain.EcoBudgetUiState
import com.example.shared.model.Category
import com.example.shared.model.Transaction
import com.example.shared.model.YearMonth
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar
import java.util.UUID

/**
 * ViewModel responsable de la couche logique, de la navigation mensuelle et de l'état réactif d'EcoBudget.
 *
 * @param repository Dépôt de données pour les transactions.
 */
class EcoBudgetViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TransactionRepository = RoomTransactionRepository(
        EcoBudgetDatabase.getInstance(application).transactionDao()
    )
    private val store = EcoBudgetStore(
        repository = repository,
        scope = viewModelScope,
        idGenerator = { UUID.randomUUID().toString() }
    )

    val uiState: StateFlow<EcoBudgetUiState> = store.uiState

    /**
     * Navigue vers le mois précédent.
     */
    fun previousMonth() {
        store.previousMonth()
    }

    /**
     * Navigue vers le mois suivant.
     */
    fun nextMonth() {
        store.nextMonth()
    }

    /**
     * Réinitialise la navigation sur le mois courant.
     */
    fun goToCurrentMonth() {
        store.goToCurrentMonth()
    }

    /**
     * Bascule la sélection d'une catégorie (support multi-sélection).
     * Si la catégorie était sélectionnée, on la retire.
     * Si elle n'était pas sélectionnée, on l'ajoute.
     */
    fun toggleCategory(category: Category) {
        store.toggleCategory(category)
    }

    /**
     * Réinitialise le filtre pour afficher toutes les catégories ("Tous").
     */
    fun clearCategoryFilter() {
        store.clearCategoryFilter()
    }

    /**
     * Ouvre la boîte de dialogue pour créer une nouvelle transaction.
     */
    fun openAddDialog() {
        store.openAddDialog()
    }

    /**
     * Ouvre la boîte de dialogue pré-remplie pour modifier une transaction existante.
     */
    fun openEditDialog(transaction: Transaction) {
        store.openEditDialog(transaction)
    }

    /**
     * Ferme la boîte de dialogue d'ajout / édition.
     */
    fun dismissDialog() {
        store.dismissDialog()
    }

    /**
     * Enregistre ou met à jour une dépense selon le contexte d'édition.
     */
    fun saveTransaction(title: String, amount: Double, category: Category) {
        val currentMonth = uiState.value.currentMonth
        val date = if (currentMonth == YearMonth.current()) {
            System.currentTimeMillis()
        } else {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, currentMonth.year)
            calendar.set(Calendar.MONTH, currentMonth.month)
            calendar.set(Calendar.DAY_OF_MONTH, 15)
            calendar.set(Calendar.HOUR_OF_DAY, 12)
            calendar.timeInMillis
        }
        store.saveTransaction(title, amount, category, date)
    }

    /**
     * Supprime une dépense par son identifiant unique.
     */
    fun deleteTransaction(id: String) {
        store.deleteTransaction(id)
    }
}
