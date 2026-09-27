package com.example.shared.domain

import com.example.shared.model.Category
import com.example.shared.model.Transaction
import com.example.shared.model.YearMonth

data class EcoBudgetUiState(
    val currentMonth: YearMonth = YearMonth.current(),
    val filteredTransactions: List<Transaction> = emptyList(),
    val monthTransactions: List<Transaction> = emptyList(),
    val allTransactions: List<Transaction> = emptyList(),
    val selectedCategories: Set<Category> = emptySet(),
    val monthlyBudget: Double = 500000.0,
    val totalSpent: Double = 0.0,
    val categoryStatistics: List<EcoBudgetDomain.CategoryExpenseStat> = emptyList(),
    val categorySpent: Double = 0.0,
    val remainingBudget: Double = 500000.0,
    val isAddDialogOpen: Boolean = false,
    val editingTransaction: Transaction? = null
) {
    val isAllCategoriesSelected: Boolean
        get() = selectedCategories.isEmpty() || selectedCategories.size == Category.entries.size

    val budgetUsageRatio: Float
        get() = if (monthlyBudget > 0) (totalSpent / monthlyBudget).toFloat().coerceIn(0f, 1f) else 0f

    val budgetUsagePercentage: Int
        get() = if (monthlyBudget > 0) ((totalSpent / monthlyBudget) * 100).toInt() else 0
}

object EcoBudgetPresentation {
    fun buildUiState(
        transactions: List<Transaction>,
        currentMonth: YearMonth,
        selectedCategories: Set<Category> = emptySet(),
        monthlyBudget: Double = 500000.0,
        isAddDialogOpen: Boolean = false,
        editingTransaction: Transaction? = null
    ): EcoBudgetUiState {
        val summary = EcoBudgetDomain.summarizeMonth(
            transactions = transactions,
            month = currentMonth,
            monthlyBudget = monthlyBudget,
            selectedCategories = selectedCategories
        )
        val monthTransactions = transactions.filter { currentMonth.containsTimestamp(it.date) }
        val categoryStatistics = EcoBudgetDomain.categoryBreakdown(transactions, currentMonth)

        return EcoBudgetUiState(
            currentMonth = currentMonth,
            filteredTransactions = summary.filteredTransactions,
            monthTransactions = monthTransactions,
            allTransactions = transactions,
            selectedCategories = selectedCategories,
            monthlyBudget = monthlyBudget,
            totalSpent = summary.totalSpent,
            categoryStatistics = categoryStatistics,
            categorySpent = summary.filteredSpent,
            remainingBudget = summary.remainingBudget,
            isAddDialogOpen = isAddDialogOpen,
            editingTransaction = editingTransaction
        )
    }
}
