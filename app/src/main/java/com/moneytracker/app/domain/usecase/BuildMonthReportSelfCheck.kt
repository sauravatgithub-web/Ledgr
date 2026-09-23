package com.moneytracker.app.domain.usecase

import com.moneytracker.app.domain.model.Category
import com.moneytracker.app.domain.model.Expense
import com.moneytracker.app.domain.model.Money
import com.moneytracker.app.domain.model.MonthlyBudget

/**
 * Lightweight assert-based check for report math (run from a unit test or REPL).
 * ponytail: no Android instrumentation; validates BuildMonthReport only.
 */
object BuildMonthReportSelfCheck {
    fun run() {
        val categories = listOf(Category(1, "Rent"), Category(2, "Food"))
        val expenses = listOf(
            Expense(1, Money(10_000_00), "Landlord", 1, true, 0L, "2026-09"),
            Expense(2, Money(500_00), "Cafe", 2, false, 0L, "2026-09"),
        )
        val budget = MonthlyBudget("2026-09", Money(30_000_00))
        val build = BuildMonthReport()

        val all = build("2026-09", budget, expenses, categories, ReportMode.ALL)
        check(all.totalSpent.paise == 10_500_00L) { "all total" }
        check(all.categoryBreakdown.size == 2) { "all categories" }
        check(all.remaining.paise == 19_500_00L) { "all remaining" }
        check(all.budgetUsedPercent == 35) { "budget %" }
        check(all.dailyBreakdown.isNotEmpty()) { "daily" }

        val flexible = build("2026-09", budget, expenses, categories, ReportMode.FLEXIBLE)
        check(flexible.totalSpent.paise == 500_00L) { "flexible total" }
        check(flexible.nonNegotiableSpent.paise == 10_000_00L) { "non-negotiable" }
        check(flexible.categoryBreakdown.single().categoryName == "Food") { "flexible cats" }
    }
}
