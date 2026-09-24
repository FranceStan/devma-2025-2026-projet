package com.example.data.repository

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shared.model.Category
import com.example.shared.model.Transaction

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val amount: Double,
    val date: Long,
    val category: String
) {
    fun toDomain(): Transaction = Transaction(
        id = id,
        title = title,
        amount = amount,
        date = date,
        category = Category.valueOf(category)
    )

    companion object {
        fun fromDomain(transaction: Transaction): TransactionEntity = TransactionEntity(
            id = transaction.id,
            title = transaction.title,
            amount = transaction.amount,
            date = transaction.date,
            category = transaction.category.name
        )
    }
}
