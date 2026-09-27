package com.example.shared.model

data class Transaction(
    val id: String,
    val title: String,
    val amount: Double,
    val date: Long,
    val category: Category
)
