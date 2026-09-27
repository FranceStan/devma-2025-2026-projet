package com.example.shared.model

enum class Category(
    val emoji: String,
    val label: String
) {
    TRANSPORT("🚌", "Transport"),
    ALIMENTATION("🍱", "Alimentation"),
    LOISIRS("🎾", "Loisirs"),
    LOGEMENT("🏠", "Logement");

    val key: String
        get() = name.lowercase()
}
