package com.moneytracker.app.domain.usecase

import com.moneytracker.app.domain.MoneyRepository

class AddCategory(private val repository: MoneyRepository) {
    suspend operator fun invoke(name: String): Long {
        val trimmed = name.trim()
        require(trimmed.isNotEmpty()) { "Category name is required" }
        return repository.addCategory(trimmed)
    }
}
