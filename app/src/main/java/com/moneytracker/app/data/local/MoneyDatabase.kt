package com.moneytracker.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CategoryEntity::class, MonthlyBudgetEntity::class, ExpenseEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class MoneyDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun expenseDao(): ExpenseDao
}
