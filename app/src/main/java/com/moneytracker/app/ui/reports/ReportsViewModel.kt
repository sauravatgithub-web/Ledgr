package com.moneytracker.app.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moneytracker.app.di.AppContainer
import com.moneytracker.app.domain.model.MonthReport
import com.moneytracker.app.domain.model.YearMonths
import com.moneytracker.app.domain.usecase.ReportMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.YearMonth

data class ReportsUiState(
    val yearMonth: String = YearMonths.current(),
    val yearMonthLabel: String = YearMonths.displayLabel(YearMonths.current()),
    val mode: ReportMode = ReportMode.ALL,
    val report: MonthReport? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
class ReportsViewModel(
    private val container: AppContainer,
) : ViewModel() {
    private val yearMonth = MutableStateFlow(YearMonths.current())
    private val mode = MutableStateFlow(ReportMode.ALL)

    val uiState: StateFlow<ReportsUiState> = combine(
        yearMonth,
        mode,
        yearMonth.flatMapLatest { ym ->
            combine(
                container.repository.observeBudget(ym),
                container.repository.observeExpenses(ym),
                container.repository.observeCategories(),
            ) { budget, expenses, categories ->
                Triple(budget, expenses, categories)
            }
        },
    ) { ym, reportMode, data ->
        val (budget, expenses, categories) = data
        ReportsUiState(
            yearMonth = ym,
            yearMonthLabel = YearMonths.displayLabel(ym),
            mode = reportMode,
            report = container.buildMonthReport(ym, budget, expenses, categories, reportMode),
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ReportsUiState(),
    )

    fun setMode(mode: ReportMode) = this.mode.update { mode }

    fun previousMonth() = yearMonth.update {
        YearMonth.parse(it).minusMonths(1).toString()
    }

    fun nextMonth() = yearMonth.update {
        YearMonth.parse(it).plusMonths(1).toString()
    }

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ReportsViewModel(container) as T
    }
}
