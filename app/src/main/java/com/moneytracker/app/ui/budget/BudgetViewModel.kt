package com.moneytracker.app.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moneytracker.app.di.AppContainer
import com.moneytracker.app.domain.model.Money
import com.moneytracker.app.domain.model.YearMonths
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BudgetUiState(
    val yearMonthLabel: String = "",
    val amountText: String = "",
    val currentBudget: Money? = null,
    val error: String? = null,
    val saved: Boolean = false,
)

class BudgetViewModel(
    private val container: AppContainer,
) : ViewModel() {
    private val yearMonth = YearMonths.current()
    private val _form = MutableStateFlow(
        BudgetUiState(yearMonthLabel = YearMonths.displayLabel(yearMonth)),
    )

    val uiState: StateFlow<BudgetUiState> = combine(
        container.repository.observeBudget(yearMonth),
        _form,
    ) { budget, form ->
        form.copy(currentBudget = budget?.amount)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        BudgetUiState(yearMonthLabel = YearMonths.displayLabel(yearMonth)),
    )

    fun onAmountChange(value: String) =
        _form.update { it.copy(amountText = value, error = null, saved = false) }

    fun save() {
        val amount = Money.fromRupeeString(_form.value.amountText)
        if (amount == null || amount.paise <= 0) {
            _form.update { it.copy(error = "Enter a valid monthly budget in ₹") }
            return
        }
        viewModelScope.launch {
            runCatching { container.setMonthlyBudget(yearMonth, amount) }
                .onSuccess { _form.update { it.copy(saved = true, error = null, amountText = "") } }
                .onFailure { e -> _form.update { it.copy(error = e.message ?: "Could not save") } }
        }
    }

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            BudgetViewModel(container) as T
    }
}
