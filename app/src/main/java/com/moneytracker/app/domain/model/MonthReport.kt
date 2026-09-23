package com.moneytracker.app.domain.model

data class CategorySpend(
    val categoryId: Long,
    val categoryName: String,
    val total: Money,
)

data class DaySpend(
    val epochDay: Long,
    val label: String,
    val total: Money,
)

data class MonthReport(
    val yearMonth: String,
    val budget: Money,
    val totalSpent: Money,
    val nonNegotiableSpent: Money,
    val flexibleSpent: Money,
    val remaining: Money,
    val categoryBreakdown: List<CategorySpend>,
    val dailyBreakdown: List<DaySpend> = emptyList(),
) {
    val isOverBudget: Boolean get() = totalSpent.paise > budget.paise

    val budgetUsedPercent: Int
        get() = if (budget.paise <= 0L) {
            0
        } else {
            ((totalSpent.paise * 100L) / budget.paise).toInt().coerceAtLeast(0)
        }
}
