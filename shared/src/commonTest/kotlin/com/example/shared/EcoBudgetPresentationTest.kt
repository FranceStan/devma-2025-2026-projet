package com.example.shared

import com.example.shared.domain.EcoBudgetPresentation
import com.example.shared.model.Category
import com.example.shared.model.Transaction
import com.example.shared.model.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EcoBudgetPresentationTest {
    @Test
    fun `shared presentation builds filtered state for month and categories`() {
        val month = YearMonth(2025, 8)
        val transactions = listOf(
            Transaction("1", "Courses", 120.0, 1757000000000L, Category.ALIMENTATION),
            Transaction("2", "Bus", 30.0, 1757160000000L, Category.TRANSPORT),
            Transaction("3", "Cinema", 40.0, 1757000000000L, Category.LOISIRS)
        )

        val state = EcoBudgetPresentation.buildUiState(
            transactions = transactions,
            currentMonth = month,
            selectedCategories = setOf(Category.ALIMENTATION),
            monthlyBudget = 1000.0
        )

        assertEquals(1, state.filteredTransactions.size)
        assertEquals(190.0, state.totalSpent)
        assertEquals(810.0, state.remainingBudget)
        assertEquals(120.0, state.categorySpent)
        assertFalse(state.isAllCategoriesSelected)
    }

    @Test
    fun `shared presentation keeps all categories selected when filter is empty`() {
        val state = EcoBudgetPresentation.buildUiState(
            transactions = emptyList(),
            currentMonth = YearMonth.current(),
            selectedCategories = emptySet(),
            monthlyBudget = 500.0
        )

        assertTrue(state.isAllCategoriesSelected)
        assertEquals(0.0, state.totalSpent)
        assertEquals(500.0, state.remainingBudget)
    }
}
