package com.example.shared

import com.example.shared.model.Category
import com.example.shared.resources.EcoBudgetStrings
import kotlin.test.Test
import kotlin.test.assertEquals

class EcoBudgetStringsTest {
    @Test
    fun `shared labels remain stable for Android and future platforms`() {
        assertEquals("Nouvelle dépense", EcoBudgetStrings.dialogTitleNewExpense)
        assertEquals("Modifier la dépense", EcoBudgetStrings.dialogTitleEditExpense)
        assertEquals("FCFA", EcoBudgetStrings.currencyFcfa)
        assertEquals("Alimentation", Category.ALIMENTATION.label)
    }
}
