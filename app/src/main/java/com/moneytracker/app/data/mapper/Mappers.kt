package com.moneytracker.app.data.mapper

import com.moneytracker.app.data.local.CategoryEntity
import com.moneytracker.app.data.local.ExpenseEntity
import com.moneytracker.app.data.local.MonthlyBudgetEntity
import com.moneytracker.app.domain.model.Category
import com.moneytracker.app.domain.model.Expense
import com.moneytracker.app.domain.model.Money
import com.moneytracker.app.domain.model.MonthlyBudget

fun CategoryEntity.toDomain() = Category(id = id, name = name)

fun Category.toEntity() = CategoryEntity(id = id, name = name)

fun MonthlyBudgetEntity.toDomain() = MonthlyBudget(
    yearMonth = yearMonth,
    amount = Money(amountPaise),
)

fun MonthlyBudget.toEntity() = MonthlyBudgetEntity(
    yearMonth = yearMonth,
    amountPaise = amount.paise,
)

fun ExpenseEntity.toDomain() = Expense(
    id = id,
    amount = Money(amountPaise),
    place = place,
    categoryId = categoryId,
    isNonNegotiable = isNonNegotiable,
    createdAtEpochMs = createdAtEpochMs,
    yearMonth = yearMonth,
)

fun Expense.toEntity() = ExpenseEntity(
    id = id,
    amountPaise = amount.paise,
    place = place,
    categoryId = categoryId,
    isNonNegotiable = isNonNegotiable,
    createdAtEpochMs = createdAtEpochMs,
    yearMonth = yearMonth,
)
