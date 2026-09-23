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
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class NonNegotiableFilter { ALL, ONLY, EXCLUDE }

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
    val recentByDate: List<ExpenseDayGroup> = emptyList(),
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
    private val filters = MutableStateFlow(FilterState())

    private data class FilterState(
        val searchQuery: String = "",
        val categoryFilterId: Long? = null,
        val nonNegotiableFilter: NonNegotiableFilter = NonNegotiableFilter.ALL,
    )

    val uiState = combine(
        container.repository.observeBudget(yearMonth),
        container.repository.observeExpenses(yearMonth),
        container.repository.observeCategories(),
        filters,
    ) { budget, expenses, categories, filter ->
        val report = buildReport(yearMonth, budget, expenses, categories, ReportMode.ALL)
        val query = filter.searchQuery.trim().lowercase()
        val filtered = expenses.filter { expense ->
            val matchesQuery = query.isEmpty() ||
                expense.place.lowercase().contains(query) ||
                (categories.find { it.id == expense.categoryId }?.name?.lowercase()?.contains(query) == true)
            val matchesCategory = filter.categoryFilterId == null || expense.categoryId == filter.categoryFilterId
            val matchesFlag = when (filter.nonNegotiableFilter) {
                NonNegotiableFilter.ALL -> true
                NonNegotiableFilter.ONLY -> expense.isNonNegotiable
                NonNegotiableFilter.EXCLUDE -> !expense.isNonNegotiable
            }
            matchesQuery && matchesCategory && matchesFlag
        }
        val today = LocalDate.now(zone)
        val yesterday = today.minusDays(1)
        val grouped = filtered
            .groupBy { Instant.ofEpochMilli(it.createdAtEpochMs).atZone(zone).toLocalDate() }
            .toSortedMap(compareByDescending { it })
            .map { (date, dayExpenses) ->
                val label = when (date) {
                    today -> "Today"
                    yesterday -> "Yesterday"
                    else -> date.format(dayFormatter)
                }
                ExpenseDayGroup(
                    dateKey = date,
                    label = label,
                    expenses = dayExpenses.sortedByDescending { it.createdAtEpochMs },
                )
            }
        HomeUiState(
            yearMonthLabel = YearMonths.displayLabel(yearMonth),
            budget = report.budget,
            spent = report.totalSpent,
            remaining = report.remaining,
            recentByDate = grouped,
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

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HomeViewModel(container) as T
    }
}
