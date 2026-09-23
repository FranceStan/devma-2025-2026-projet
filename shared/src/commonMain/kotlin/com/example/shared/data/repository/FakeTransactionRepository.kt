package com.example.shared.data.repository

import com.example.shared.model.Category
import com.example.shared.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs
import kotlin.random.Random

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
            val now = Clock.System.now().toEpochMilliseconds()
            val baseDate = now - (7L * 24 * 60 * 60 * 1000)

            return listOf(
                Transaction("tx-${Random.nextLong()}", "Supermarché Bio", 45000.0, baseDate + 2 * 86400000L, Category.ALIMENTATION),
                Transaction("tx-${Random.nextLong()}", "Session Tennis", 12000.0, baseDate + 3 * 86400000L, Category.LOISIRS),
                Transaction("tx-${Random.nextLong()}", "Ticket de Bus Express", 2500.0, baseDate + 5 * 86400000L, Category.TRANSPORT),
                Transaction("tx-${Random.nextLong()}", "Loyer Mensuel", 250000.0, baseDate + 9 * 86400000L, Category.LOGEMENT)
            )
        }
    }
}
