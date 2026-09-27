package com.example.shared.domain

import com.example.shared.model.Category
import com.example.shared.model.Transaction
import com.example.shared.model.YearMonth

object EcoBudgetDomain {
    const val DEFAULT_MONTHLY_BUDGET = 500000.0

    fun filterTransactionsForMonth(
        transactions: List<Transaction>,
        currentMonth: YearMonth,
        selectedCategories: Set<Category> = emptySet()
    ): List<Transaction> {
        val monthTransactions = transactions.filter { currentMonth.containsTimestamp(it.date) }
        return if (selectedCategories.isEmpty() || selectedCategories.size == Category.entries.size) {
            monthTransactions
        } else {
            monthTransactions.filter { it.category in selectedCategories }
        }
    }

    fun totalSpent(transactions: List<Transaction>): Double = transactions.sumOf { it.amount }

    data class CategoryExpenseStat(
        val category: Category,
        val total: Double,
        val percentage: Float
    )

    fun categoryBreakdown(
        transactions: List<Transaction>,
        month: YearMonth
    ): List<CategoryExpenseStat> {
        val monthTransactions = transactions.filter { month.containsTimestamp(it.date) }
        val totalSpent = totalSpent(monthTransactions)
        return Category.entries.map { category ->
            val categoryTotal = monthTransactions
                .filter { it.category == category }
                .sumOf { it.amount }
            CategoryExpenseStat(
                category = category,
                total = categoryTotal,
                percentage = if (totalSpent > 0.0) {
                    (categoryTotal / totalSpent).toFloat()
                } else {
                    0f
                }
            )
        }
    }

    fun remainingBudget(monthlyBudget: Double, spent: Double): Double =
        (monthlyBudget - spent).coerceAtLeast(0.0)

    data class BudgetSummary(
        val month: YearMonth,
        val totalSpent: Double,
        val filteredSpent: Double,
        val remainingBudget: Double,
        val filteredTransactions: List<Transaction>
    )

    fun summarizeMonth(
        transactions: List<Transaction>,
        month: YearMonth,
        monthlyBudget: Double = DEFAULT_MONTHLY_BUDGET,
        selectedCategories: Set<Category> = emptySet()
    ): BudgetSummary {
        val filtered = filterTransactionsForMonth(transactions, month, selectedCategories)
        val monthTransactions = transactions.filter { month.containsTimestamp(it.date) }
        val totalSpent = totalSpent(monthTransactions)
        val filteredSpent = totalSpent(filtered)
        return BudgetSummary(
            month = month,
            totalSpent = totalSpent,
            filteredSpent = filteredSpent,
            remainingBudget = remainingBudget(monthlyBudget, totalSpent),
            filteredTransactions = filtered
        )
    }
}
