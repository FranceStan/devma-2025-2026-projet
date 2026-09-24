package com.example.data.repository

import com.example.shared.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomTransactionRepository(
    private val dao: TransactionDao
) : TransactionRepository {
    override fun getTransactions(): Flow<List<Transaction>> =
        dao.observeTransactions().map { entities -> entities.map(TransactionEntity::toDomain) }

    override suspend fun addTransaction(transaction: Transaction) {
        dao.insert(TransactionEntity.fromDomain(transaction))
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        dao.update(TransactionEntity.fromDomain(transaction))
    }

    override suspend fun deleteTransaction(id: String) {
        dao.deleteById(id)
    }
}
