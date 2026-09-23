package com.example.shared

import com.example.shared.data.repository.InMemoryExpenseRepository
import com.example.shared.model.Category
import com.example.shared.model.Transaction
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ExpenseRepositoryTest {
    @Test
    fun `repository keeps transactions in memory`() = runTest {
        val repository = InMemoryExpenseRepository()
        val initial = repository.getTransactions().first()
        assertEquals(4, initial.size)

        val newTransaction = Transaction("new", "Courses", 80.0, 1757000000000L, Category.ALIMENTATION)
        repository.addTransaction(newTransaction)
        val updated = repository.getTransactions().first()
        assertEquals(5, updated.size)
        assertEquals(newTransaction, updated.first())
    }
}
