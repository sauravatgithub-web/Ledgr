package com.moneytracker.app.domain.usecase

import com.moneytracker.app.domain.MoneyRepository
import com.moneytracker.app.domain.model.Expense
import com.moneytracker.app.domain.model.Money
import com.moneytracker.app.domain.model.YearMonths

class AddExpense(private val repository: MoneyRepository) {
    suspend operator fun invoke(
        amount: Money,
        place: String,
        categoryId: Long,
        isNonNegotiable: Boolean,
        atEpochMs: Long = System.currentTimeMillis(),
    ): Long {
        require(amount.paise > 0) { "Amount must be greater than zero" }
        require(place.isNotBlank()) { "Place is required" }
        require(categoryId > 0) { "Category is required" }

        val expense = Expense(
            amount = amount,
            place = place.trim(),
            categoryId = categoryId,
            isNonNegotiable = isNonNegotiable,
            createdAtEpochMs = atEpochMs,
            yearMonth = YearMonths.fromEpochMs(atEpochMs),
        )
        return repository.addExpense(expense)
    }
}

class UpdateExpense(private val repository: MoneyRepository) {
    suspend operator fun invoke(
        id: Long,
        amount: Money,
        place: String,
        categoryId: Long,
        isNonNegotiable: Boolean,
        atEpochMs: Long,
    ) {
        require(id > 0) { "Invalid expense" }
        require(amount.paise > 0) { "Amount must be greater than zero" }
        require(place.isNotBlank()) { "Place is required" }
        require(categoryId > 0) { "Category is required" }

        repository.updateExpense(
            Expense(
                id = id,
                amount = amount,
                place = place.trim(),
                categoryId = categoryId,
                isNonNegotiable = isNonNegotiable,
                createdAtEpochMs = atEpochMs,
                yearMonth = YearMonths.fromEpochMs(atEpochMs),
            ),
        )
    }
}

class DeleteExpense(private val repository: MoneyRepository) {
    suspend operator fun invoke(id: Long) {
        require(id > 0) { "Invalid expense" }
        repository.deleteExpense(id)
    }
}
