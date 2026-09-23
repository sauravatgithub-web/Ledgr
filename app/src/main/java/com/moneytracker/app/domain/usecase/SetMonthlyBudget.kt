package com.moneytracker.app.domain.usecase

import com.moneytracker.app.domain.MoneyRepository
import com.moneytracker.app.domain.model.Money

class SetMonthlyBudget(private val repository: MoneyRepository) {
    suspend operator fun invoke(yearMonth: String, amount: Money) {
        require(amount.paise > 0) { "Budget must be greater than zero" }
        repository.setBudget(yearMonth, amount)
    }
}
