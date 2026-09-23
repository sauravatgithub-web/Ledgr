package com.moneytracker.app.domain.usecase

import com.moneytracker.app.domain.model.Category
import com.moneytracker.app.domain.model.CategorySpend
import com.moneytracker.app.domain.model.DaySpend
import com.moneytracker.app.domain.model.Expense
import com.moneytracker.app.domain.model.Money
import com.moneytracker.app.domain.model.MonthReport
import com.moneytracker.app.domain.model.MonthlyBudget
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class ReportMode {
    ALL,
    FLEXIBLE,
}

class BuildMonthReport(
    private val zoneId: ZoneId = ZoneId.systemDefault(),
) {
    private val dayLabelFmt = DateTimeFormatter.ofPattern("d MMM")

    operator fun invoke(
        yearMonth: String,
        budget: MonthlyBudget?,
        expenses: List<Expense>,
        categories: List<Category>,
        mode: ReportMode,
    ): MonthReport {
        val budgetAmount = budget?.amount ?: Money.ZERO
        val categoryNames = categories.associateBy({ it.id }, { it.name })

        val nonNegotiableSpent = expenses
            .filter { it.isNonNegotiable }
            .fold(Money.ZERO) { acc, e -> acc + e.amount }

        val totalSpent = expenses.fold(Money.ZERO) { acc, e -> acc + e.amount }
        val flexibleSpent = totalSpent - nonNegotiableSpent

        val scoped = when (mode) {
            ReportMode.ALL -> expenses
            ReportMode.FLEXIBLE -> expenses.filterNot { it.isNonNegotiable }
        }

        val scopedTotal = scoped.fold(Money.ZERO) { acc, e -> acc + e.amount }

        val breakdown = scoped
            .groupBy { it.categoryId }
            .map { (categoryId, items) ->
                CategorySpend(
                    categoryId = categoryId,
                    categoryName = categoryNames[categoryId] ?: "Unknown",
                    total = items.fold(Money.ZERO) { acc, e -> acc + e.amount },
                )
            }
            .sortedByDescending { it.total.paise }

        val daily = scoped
            .groupBy { Instant.ofEpochMilli(it.createdAtEpochMs).atZone(zoneId).toLocalDate() }
            .toSortedMap()
            .map { (date, items) ->
                DaySpend(
                    epochDay = date.toEpochDay(),
                    label = date.format(dayLabelFmt),
                    total = items.fold(Money.ZERO) { acc, e -> acc + e.amount },
                )
            }

        val remainingForMode = when (mode) {
            ReportMode.ALL -> budgetAmount - totalSpent
            ReportMode.FLEXIBLE -> budgetAmount - flexibleSpent
        }

        return MonthReport(
            yearMonth = yearMonth,
            budget = budgetAmount,
            totalSpent = if (mode == ReportMode.ALL) totalSpent else scopedTotal,
            nonNegotiableSpent = nonNegotiableSpent,
            flexibleSpent = flexibleSpent,
            remaining = remainingForMode,
            categoryBreakdown = breakdown,
            dailyBreakdown = daily,
        )
    }
}
