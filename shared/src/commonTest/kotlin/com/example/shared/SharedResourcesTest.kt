package com.example.shared

import com.example.shared.model.Category
import kotlin.test.Test
import kotlin.test.assertEquals

class SharedResourcesTest {
    @Test
    fun `category labels are defined in the shared model`() {
        assertEquals("Transport", Category.TRANSPORT.label)
        assertEquals("Alimentation", Category.ALIMENTATION.label)
        assertEquals("Loisirs", Category.LOISIRS.label)
        assertEquals("Logement", Category.LOGEMENT.label)
    }
}
