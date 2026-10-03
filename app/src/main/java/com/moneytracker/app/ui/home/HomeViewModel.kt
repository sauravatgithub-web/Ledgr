package com.moneytracker.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moneytracker.app.di.AppContainer
import com.moneytracker.app.domain.model.Category
import com.moneytracker.app.domain.model.Expense
import com.moneytracker.app.domain.model.Money
import com.moneytracker.app.domain.model.YearMonths
import com.moneytracker.app.domain.usecase.ReportMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class NonNegotiableFilter { ALL, ONLY, EXCLUDE }

data class CalendarDay(
    val date: LocalDate?,
    val hasExpenses: Boolean,
    val isToday: Boolean,
    val isSelected: Boolean,
)

data class ExpenseDayGroup(
    val dateKey: LocalDate,
    val label: String,
    val expenses: List<Expense>,
)

data class HomeUiState(
    val yearMonthLabel: String = "",
    val budget: Money = Money.ZERO,
    val spent: Money = Money.ZERO,
    val remaining: Money = Money.ZERO,
    val calendarDays: List<CalendarDay> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedDayLabel: String = "Today",
    val expenseGroups: List<ExpenseDayGroup> = emptyList(),
    val isSearching: Boolean = false,
    val isTodaySelected: Boolean = true,
    val categories: List<Category> = emptyList(),
    val categoryNames: Map<Long, String> = emptyMap(),
    val searchQuery: String = "",
    val categoryFilterId: Long? = null,
    val nonNegotiableFilter: NonNegotiableFilter = NonNegotiableFilter.ALL,
)

class HomeViewModel(
    container: AppContainer,
) : ViewModel() {
    private val yearMonth = YearMonths.current()
    private val buildReport = container.buildMonthReport
    private val zone = ZoneId.systemDefault()
    private val dayFormatter = DateTimeFormatter.ofPattern("EEEE, d MMM")
    private val month = YearMonth.parse(yearMonth)
    private val filters = MutableStateFlow(FilterState())

    private data class FilterState(
        val searchQuery: String = "",
        val categoryFilterId: Long? = null,
        val nonNegotiableFilter: NonNegotiableFilter = NonNegotiableFilter.ALL,
        val selectedDate: LocalDate = LocalDate.now(),
    )

    val uiState = combine(
        container.repository.observeBudget(yearMonth),
        container.repository.observeExpenses(yearMonth),
        container.repository.observeCategories(),
        filters,
    ) { budget, expenses, categories, filter ->
        val report = buildReport(yearMonth, budget, expenses, categories, ReportMode.ALL)
        val query = filter.searchQuery.trim().lowercase()
        val today = LocalDate.now(zone)
        val selected = filter.selectedDate
        val daysWithSpend = expenses
            .map { Instant.ofEpochMilli(it.createdAtEpochMs).atZone(zone).toLocalDate() }
            .toSet()

        fun matches(expense: Expense): Boolean {
            val matchesQuery = query.isEmpty() ||
                expense.place.lowercase().contains(query) ||
                (categories.find { it.id == expense.categoryId }?.name?.lowercase()?.contains(query) == true)
            val matchesCategory = filter.categoryFilterId == null || expense.categoryId == filter.categoryFilterId
            val matchesFlag = when (filter.nonNegotiableFilter) {
                NonNegotiableFilter.ALL -> true
                NonNegotiableFilter.ONLY -> expense.isNonNegotiable
                NonNegotiableFilter.EXCLUDE -> !expense.isNonNegotiable
            }
            return matchesQuery && matchesCategory && matchesFlag
        }

        fun dateOf(expense: Expense): LocalDate =
            Instant.ofEpochMilli(expense.createdAtEpochMs).atZone(zone).toLocalDate()

        fun dayLabel(date: LocalDate): String = when (date) {
            today -> "Today"
            today.minusDays(1) -> "Yesterday"
            else -> date.format(dayFormatter)
        }

        val matched = expenses.filter(::matches)
        val visible = if (query.isEmpty()) {
            matched.filter { dateOf(it) == selected }
        } else {
            matched
        }.sortedByDescending { it.createdAtEpochMs }

        val groups = visible
            .groupBy { dateOf(it) }
            .toSortedMap(compareByDescending { it })
            .map { (date, dayExpenses) ->
                ExpenseDayGroup(
                    dateKey = date,
                    label = dayLabel(date),
                    expenses = dayExpenses,
                )
            }

        HomeUiState(
            yearMonthLabel = YearMonths.displayLabel(yearMonth),
            budget = report.budget,
            spent = report.totalSpent,
            remaining = report.remaining,
            calendarDays = buildCalendarDays(month, today, selected, daysWithSpend),
            selectedDate = selected,
            selectedDayLabel = dayLabel(selected),
            expenseGroups = groups,
            isSearching = query.isNotEmpty(),
            isTodaySelected = selected == today,
            categories = categories,
            categoryNames = categories.associate { it.id to it.name },
            searchQuery = filter.searchQuery,
            categoryFilterId = filter.categoryFilterId,
            nonNegotiableFilter = filter.nonNegotiableFilter,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun onSearchChange(value: String) = filters.update { it.copy(searchQuery = value) }

    fun onCategoryFilter(id: Long?) = filters.update {
        it.copy(categoryFilterId = if (it.categoryFilterId == id) null else id)
    }

    fun onNonNegotiableFilter(filter: NonNegotiableFilter) = filters.update {
        it.copy(nonNegotiableFilter = filter)
    }

    fun onDateSelected(date: LocalDate) = filters.update {
        it.copy(selectedDate = date, searchQuery = "")
    }

    fun selectToday() = filters.update {
        it.copy(selectedDate = LocalDate.now(zone), searchQuery = "")
    }

    private fun buildCalendarDays(
        month: YearMonth,
        today: LocalDate,
        selected: LocalDate,
        daysWithSpend: Set<LocalDate>,
    ): List<CalendarDay> {
        val first = month.atDay(1)
        val leading = first.dayOfWeek.value - 1
        val cells = ArrayList<CalendarDay>(42)
        repeat(leading) {
            cells += CalendarDay(date = null, hasExpenses = false, isToday = false, isSelected = false)
        }
        for (day in 1..month.lengthOfMonth()) {
            val date = month.atDay(day)
            cells += CalendarDay(
                date = date,
                hasExpenses = date in daysWithSpend,
                isToday = date == today,
                isSelected = date == selected,
            )
        }
        while (cells.size % 7 != 0) {
            cells += CalendarDay(date = null, hasExpenses = false, isToday = false, isSelected = false)
        }
        return cells
    }

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HomeViewModel(container) as T
    }
}
