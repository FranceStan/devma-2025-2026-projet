package com.example.shared.data.repository

import com.example.shared.model.Category
import com.example.shared.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeTransactionRepository : TransactionRepository {
    private val _transactionsFlow = MutableStateFlow(createInitialTransactions())

    override fun getTransactions(): Flow<List<Transaction>> = _transactionsFlow.asStateFlow()

    override suspend fun addTransaction(transaction: Transaction) {
        _transactionsFlow.update { listOf(transaction) + it }
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        _transactionsFlow.update { current ->
            current.map { if (it.id == transaction.id) transaction else it }
        }
    }

    override suspend fun deleteTransaction(id: String) {
        _transactionsFlow.update { current -> current.filterNot { it.id == id } }
    }

    private companion object {
        fun createInitialTransactions(): List<Transaction> {
            val baseDate = 1_700_000_000_000L
            return listOf(
                Transaction("tx-1", "Supermarché Bio", 45000.0, baseDate + 2 * 86_400_000L, Category.ALIMENTATION),
                Transaction("tx-2", "Session Tennis", 12000.0, baseDate + 3 * 86_400_000L, Category.LOISIRS),
                Transaction("tx-3", "Ticket de Bus Express", 2500.0, baseDate + 5 * 86_400_000L, Category.TRANSPORT),
                Transaction("tx-4", "Loyer Mensuel", 250000.0, baseDate + 9 * 86_400_000L, Category.LOGEMENT)
            )
        }
    }
}
