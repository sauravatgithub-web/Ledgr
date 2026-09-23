package com.moneytracker.app.domain.model

data class MonthlyBudget(
    val yearMonth: String,
    val amount: Money,
)
