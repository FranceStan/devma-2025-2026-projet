package com.example.shared

import com.example.shared.data.repository.FakeTransactionRepository
import com.example.shared.domain.EcoBudgetStore
import com.example.shared.model.Category
import com.example.shared.model.YearMonth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EcoBudgetStoreTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `store adds a transaction and closes the dialog`() = runTest {
        val baseDate = 1_700_000_000_000L
        val repository = FakeTransactionRepository()
        val store = EcoBudgetStore(
            repository = repository,
            scope = backgroundScope,
            idGenerator = { "created-id" },
            initialMonth = YearMonth.fromTimestamp(baseDate)
        )
        store.openAddDialog()
        val openedState = store.uiState.first { it.isAddDialogOpen }
        assertTrue(openedState.isAddDialogOpen)

        store.saveTransaction("  Café  ", 2500.0, Category.ALIMENTATION, baseDate)
        val savedState = store.uiState.first { state ->
            state.allTransactions.any { it.id == "created-id" } && !state.isAddDialogOpen
        }

        val created = savedState.allTransactions.first { it.id == "created-id" }
        assertEquals("Café", created.title)
        assertEquals(2500.0, created.amount)
        assertEquals(Category.ALIMENTATION, created.category)
        assertFalse(savedState.isAddDialogOpen)
    }
}
