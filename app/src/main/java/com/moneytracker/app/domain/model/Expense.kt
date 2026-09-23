package com.moneytracker.app.domain.model

data class Expense(
    val id: Long = 0,
    val amount: Money,
    val place: String,
    val categoryId: Long,
    val isNonNegotiable: Boolean,
    val createdAtEpochMs: Long,
    val yearMonth: String,
)
