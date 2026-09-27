package com.example.shared

import com.example.shared.model.Category
import com.example.shared.model.Transaction
import com.example.shared.model.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals

class SharedSmokeTest {
    @Test
    fun `shared domain exposes month navigation and budgets`() {
        val january = YearMonth(2025, 0)
        val nextMonth = january.next()
        assertEquals(2025, nextMonth.year)
        assertEquals(1, nextMonth.month)

        val transaction = Transaction(
            id = "t1",
            title = "Courses",
            amount = 80.0,
            date = 1735689600000L,
            category = Category.ALIMENTATION
        )

        val currentMonth = YearMonth.fromTimestamp(transaction.date)
        assertEquals("janvier 2025", currentMonth.displayLabel.lowercase())
        assertEquals(true, currentMonth.containsTimestamp(transaction.date))
        assertEquals(Category.ALIMENTATION, transaction.category)
    }
}
