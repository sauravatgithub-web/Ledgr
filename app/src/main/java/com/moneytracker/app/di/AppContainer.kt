package com.moneytracker.app.di

import android.content.Context
import androidx.room.Room
import com.moneytracker.app.data.RoomMoneyRepository
import com.moneytracker.app.data.local.MoneyDatabase
import com.moneytracker.app.domain.MoneyRepository
import com.moneytracker.app.domain.usecase.AddCategory
import com.moneytracker.app.domain.usecase.AddExpense
import com.moneytracker.app.domain.usecase.BuildMonthReport
import com.moneytracker.app.domain.usecase.DeleteExpense
import com.moneytracker.app.domain.usecase.ExportMonthCsv
import com.moneytracker.app.domain.usecase.SetMonthlyBudget
import com.moneytracker.app.domain.usecase.UpdateExpense

class AppContainer(context: Context) {
    private val database: MoneyDatabase = Room.databaseBuilder(
        context.applicationContext,
        MoneyDatabase::class.java,
        "money_tracker.db",
    ).build()

    val repository: MoneyRepository = RoomMoneyRepository(database)

    val addExpense = AddExpense(repository)
    val updateExpense = UpdateExpense(repository)
    val deleteExpense = DeleteExpense(repository)
    val addCategory = AddCategory(repository)
    val setMonthlyBudget = SetMonthlyBudget(repository)
    val buildMonthReport = BuildMonthReport()
    val exportMonthCsv = ExportMonthCsv()
}
