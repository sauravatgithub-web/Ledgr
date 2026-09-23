package com.moneytracker.app.domain

import com.moneytracker.app.domain.model.Category
import com.moneytracker.app.domain.model.Expense
import com.moneytracker.app.domain.model.Money
import com.moneytracker.app.domain.model.MonthlyBudget
import kotlinx.coroutines.flow.Flow

/**
 * Persistence contract. UI and use cases depend only on this interface
 * so new storage or ingest sources can swap implementations later.
 */
interface MoneyRepository {
    fun observeCategories(): Flow<List<Category>>
    suspend fun addCategory(name: String): Long

    fun observeBudget(yearMonth: String): Flow<MonthlyBudget?>
    suspend fun setBudget(yearMonth: String, amount: Money)

    fun observeExpenses(yearMonth: String): Flow<List<Expense>>
    suspend fun getExpense(id: Long): Expense?
    suspend fun addExpense(expense: Expense): Long
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(id: Long)

    suspend fun ensureSeedCategories()
}
