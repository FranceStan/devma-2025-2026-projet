package com.example.shared.domain

import com.example.shared.data.repository.TransactionRepository
import com.example.shared.model.Category
import com.example.shared.model.Transaction
import com.example.shared.model.YearMonth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EcoBudgetStore(
    private val repository: TransactionRepository,
    private val scope: CoroutineScope,
    idGenerator: () -> String,
    initialMonth: YearMonth = YearMonth.current(),
    monthlyBudget: Double = EcoBudgetDomain.DEFAULT_MONTHLY_BUDGET
) {
    private val createId = idGenerator
    private val currentMonth = MutableStateFlow(initialMonth)
    private val selectedCategories = MutableStateFlow<Set<Category>>(emptySet())
    private val isAddDialogOpen = MutableStateFlow(false)
    private val editingTransaction = MutableStateFlow<Transaction?>(null)
    private val budget = MutableStateFlow(monthlyBudget)

    private val controls = combine(
        currentMonth,
        selectedCategories,
        isAddDialogOpen,
        editingTransaction,
        budget
    ) { month, categories, dialogOpen, editing, currentBudget ->
        StoreControls(month, categories, dialogOpen, editing, currentBudget)
    }

    val uiState: StateFlow<EcoBudgetUiState> = combine(
        repository.getTransactions(),
        controls
    ) { transactions, state ->
        EcoBudgetPresentation.buildUiState(
            transactions = transactions,
            currentMonth = state.month,
            selectedCategories = state.categories,
            monthlyBudget = state.budget,
            isAddDialogOpen = state.dialogOpen,
            editingTransaction = state.editing
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = EcoBudgetUiState(
            currentMonth = initialMonth,
            monthlyBudget = monthlyBudget,
            remainingBudget = monthlyBudget
        )
    )

    private data class StoreControls(
        val month: YearMonth,
        val categories: Set<Category>,
        val dialogOpen: Boolean,
        val editing: Transaction?,
        val budget: Double
    )

    fun previousMonth() {
        currentMonth.value = currentMonth.value.previous()
    }

    fun nextMonth() {
        currentMonth.value = currentMonth.value.next()
    }

    fun goToCurrentMonth() {
        currentMonth.value = YearMonth.current()
    }

    fun toggleCategory(category: Category) {
        selectedCategories.value = if (category in selectedCategories.value) {
            selectedCategories.value - category
        } else {
            selectedCategories.value + category
        }
    }

    fun clearCategoryFilter() {
        selectedCategories.value = emptySet()
    }

    fun openAddDialog() {
        editingTransaction.value = null
        isAddDialogOpen.value = true
    }

    fun openEditDialog(transaction: Transaction) {
        editingTransaction.value = transaction
        isAddDialogOpen.value = true
    }

    fun dismissDialog() {
        isAddDialogOpen.value = false
        editingTransaction.value = null
    }

    fun saveTransaction(title: String, amount: Double, category: Category, date: Long) {
        if (title.isBlank() || amount <= 0.0) return

        val editing = editingTransaction.value
        val transaction = editing?.copy(
            title = title.trim(),
            amount = amount,
            category = category
        ) ?: Transaction(
            id = createId(),
            title = title.trim(),
            amount = amount,
            date = date,
            category = category
        )

        scope.launch {
            if (editing == null) {
                repository.addTransaction(transaction)
            } else {
                repository.updateTransaction(transaction)
            }
            dismissDialog()
        }
    }

    fun deleteTransaction(id: String) {
        scope.launch {
            repository.deleteTransaction(id)
        }
    }
}