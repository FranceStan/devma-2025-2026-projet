package com.example.shared

import com.example.shared.domain.EcoBudgetDomain
import com.example.shared.model.Category
import com.example.shared.model.Transaction
import com.example.shared.model.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EcoBudgetDomainTest {
    @Test
    fun `filters transactions by month and category`() {
        val month = YearMonth(2025, 8)
        val transactions = listOf(
            Transaction("1", "Courses", 120.0, 1757000000000L, Category.ALIMENTATION),
            Transaction("2", "Bus", 30.0, 1757160000000L, Category.TRANSPORT),
            Transaction("3", "Cinema", 40.0, 1757000000000L, Category.LOISIRS)
        )

        val filtered = EcoBudgetDomain.filterTransactionsForMonth(transactions, month, setOf(Category.ALIMENTATION))
        assertEquals(1, filtered.size)
        assertEquals("Courses", filtered.first().title)
    }

    @Test
    fun `sums totals for the selected month`() {
        val month = YearMonth(2025, 8)
        val transactions = listOf(
            Transaction("1", "Courses", 100.0, 1757000000000L, Category.ALIMENTATION),
            Transaction("2", "Loyer", 500.0, 1757000000000L, Category.LOGEMENT),
            Transaction("3", "Autre mois", 999.0, 1723000000000L, Category.LOISIRS)
        )

        val summary = EcoBudgetDomain.summarizeMonth(transactions, month, monthlyBudget = 1000.0)
        assertEquals(600.0, summary.totalSpent)
        assertEquals(400.0, summary.remainingBudget)
    }

    @Test
    fun `empty data keeps zero totals`() {
        val summary = EcoBudgetDomain.summarizeMonth(emptyList(), YearMonth.current())
        assertEquals(0.0, summary.totalSpent)
        assertEquals(0.0, summary.filteredSpent)
        assertTrue(summary.filteredTransactions.isEmpty())
    }

    @Test
    fun `category breakdown computes totals and percentages for every category`() {
        val month = YearMonth(2025, 8)
        val statistics = EcoBudgetDomain.categoryBreakdown(
            transactions = listOf(
                Transaction("1", "Courses", 120.0, 1757000000000L, Category.ALIMENTATION),
                Transaction("2", "Bus", 30.0, 1757160000000L, Category.TRANSPORT),
                Transaction("3", "Cinema", 50.0, 1757000000000L, Category.LOISIRS)
            ),
            month = month
        )

        assertEquals(Category.entries.size, statistics.size)
        assertEquals(120.0, statistics.first { it.category == Category.ALIMENTATION }.total)
        assertEquals(0.6f, statistics.first { it.category == Category.ALIMENTATION }.percentage)
        assertEquals(0.0, statistics.first { it.category == Category.LOGEMENT }.total)
        assertEquals(0f, statistics.first { it.category == Category.LOGEMENT }.percentage)
    }

    @Test
    fun `month navigation stays stable`() {
        val current = YearMonth(2025, 11)
        assertEquals(2026, current.next().year)
        assertEquals(0, current.next().month)
        assertEquals(2025, current.previous().year)
        assertEquals(10, current.previous().month)
    }
}
