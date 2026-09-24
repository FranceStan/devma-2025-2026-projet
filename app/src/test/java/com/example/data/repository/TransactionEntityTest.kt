package com.example.data.repository

import com.example.shared.model.Category
import com.example.shared.model.Transaction
import org.junit.Assert.assertEquals
import org.junit.Test

class TransactionEntityTest {
    @Test
    fun `domain transaction round trips through room entity`() {
        val transaction = Transaction(
            id = "tx-1",
            title = "Courses",
            amount = 12500.0,
            date = 1_700_000_000_000L,
            category = Category.ALIMENTATION
        )

        val restored = TransactionEntity.fromDomain(transaction).toDomain()

        assertEquals(transaction, restored)
    }
}
