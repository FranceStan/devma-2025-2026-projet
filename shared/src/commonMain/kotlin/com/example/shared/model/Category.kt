package com.example.shared.model

enum class Category(val emoji: String) {
    TRANSPORT("🚌"),
    ALIMENTATION("🍱"),
    LOISIRS("🎾"),
    LOGEMENT("🏠");

    val key: String
        get() = name.lowercase()
}
