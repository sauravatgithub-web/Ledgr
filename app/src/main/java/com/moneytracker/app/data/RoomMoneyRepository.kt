package com.moneytracker.app.data

import com.moneytracker.app.data.local.CategoryEntity
import com.moneytracker.app.data.local.MoneyDatabase
import com.moneytracker.app.data.local.MonthlyBudgetEntity
import com.moneytracker.app.data.mapper.toDomain
import com.moneytracker.app.data.mapper.toEntity
import com.moneytracker.app.domain.MoneyRepository
import com.moneytracker.app.domain.model.Category
import com.moneytracker.app.domain.model.Expense
import com.moneytracker.app.domain.model.Money
import com.moneytracker.app.domain.model.MonthlyBudget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomMoneyRepository(
    private val database: MoneyDatabase,
) : MoneyRepository {

    private val categoryDao = database.categoryDao()
    private val budgetDao = database.budgetDao()
    private val expenseDao = database.expenseDao()

    override fun observeCategories(): Flow<List<Category>> =
        categoryDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun addCategory(name: String): Long =
        categoryDao.insert(CategoryEntity(name = name))

    override fun observeBudget(yearMonth: String): Flow<MonthlyBudget?> =
        budgetDao.observe(yearMonth).map { it?.toDomain() }

    override suspend fun setBudget(yearMonth: String, amount: Money) {
        budgetDao.upsert(MonthlyBudgetEntity(yearMonth = yearMonth, amountPaise = amount.paise))
    }

    override fun observeExpenses(yearMonth: String): Flow<List<Expense>> =
        expenseDao.observeForMonth(yearMonth).map { list -> list.map { it.toDomain() } }

    override suspend fun getExpense(id: Long): Expense? =
        expenseDao.getById(id)?.toDomain()

    override suspend fun addExpense(expense: Expense): Long =
        expenseDao.insert(expense.toEntity())

    override suspend fun updateExpense(expense: Expense) {
        expenseDao.update(expense.toEntity())
    }

    override suspend fun deleteExpense(id: Long) {
        expenseDao.deleteById(id)
    }

    override suspend fun ensureSeedCategories() {
        if (categoryDao.count() > 0) return
        categoryDao.insertAll(
            listOf(
                CategoryEntity(name = "Food"),
                CategoryEntity(name = "Transport"),
                CategoryEntity(name = "Rent"),
                CategoryEntity(name = "Utilities"),
                CategoryEntity(name = "Shopping"),
                CategoryEntity(name = "Other"),
            ),
        )
    }
}
