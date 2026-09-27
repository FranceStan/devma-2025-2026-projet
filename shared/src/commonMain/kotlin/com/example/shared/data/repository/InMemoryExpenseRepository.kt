package com.example.shared.data.repository

import com.example.shared.model.Category
import com.example.shared.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryExpenseRepository : ExpenseRepository {
    private val transactions = MutableStateFlow(createInitialTransactions())

    override fun getTransactions(): Flow<List<Transaction>> = transactions.asStateFlow()

    override suspend fun addTransaction(transaction: Transaction) {
        transactions.update { current -> listOf(transaction) + current }
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactions.update { current ->
            current.map { if (it.id == transaction.id) transaction else it }
        }
    }

    override suspend fun deleteTransaction(id: String) {
        transactions.update { current -> current.filterNot { it.id == id } }
    }

    private companion object {
        fun createInitialTransactions(): List<Transaction> {
            val base = 1_700_000_000_000L
            return listOf(
                Transaction("1", "Supermarché Bio", 45000.0, base + 2 * 86_400_000L, Category.ALIMENTATION),
                Transaction("2", "Session Tennis", 12000.0, base + 3 * 86_400_000L, Category.LOISIRS),
                Transaction("3", "Ticket de Bus", 2500.0, base + 5 * 86_400_000L, Category.TRANSPORT),
                Transaction("4", "Loyer", 250000.0, base + 8 * 86_400_000L, Category.LOGEMENT)
            )
        }
    }
}
