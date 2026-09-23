package com.moneytracker.app.ui.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moneytracker.app.di.AppContainer
import com.moneytracker.app.domain.model.Category
import com.moneytracker.app.domain.model.Money
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class ExpenseEditorUiState(
    val isEdit: Boolean = false,
    val amountText: String = "",
    val place: String = "",
    val categoryId: Long? = null,
    val isNonNegotiable: Boolean = false,
    val dateEpochDay: Long = LocalDate.now().toEpochDay(),
    val categories: List<Category> = emptyList(),
    val error: String? = null,
    val saved: Boolean = false,
    val deleted: Boolean = false,
)

class ExpenseEditorViewModel(
    private val container: AppContainer,
    private val expenseId: Long?,
) : ViewModel() {
    private val zone = ZoneId.systemDefault()
    private val _uiState = MutableStateFlow(ExpenseEditorUiState(isEdit = expenseId != null))
    val uiState: StateFlow<ExpenseEditorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            container.repository.observeCategories().collect { categories ->
                _uiState.update { state ->
                    state.copy(
                        categories = categories,
                        categoryId = state.categoryId ?: categories.firstOrNull()?.id,
                    )
                }
            }
        }
        if (expenseId != null) {
            viewModelScope.launch {
                val expense = container.repository.getExpense(expenseId) ?: return@launch
                val date = Instant.ofEpochMilli(expense.createdAtEpochMs).atZone(zone).toLocalDate()
                _uiState.update {
                    it.copy(
                        isEdit = true,
                        amountText = formatAmount(expense.amount.paise),
                        place = expense.place,
                        categoryId = expense.categoryId,
                        isNonNegotiable = expense.isNonNegotiable,
                        dateEpochDay = date.toEpochDay(),
                    )
                }
            }
        }
    }

    fun onAmountChange(value: String) = _uiState.update { it.copy(amountText = value, error = null) }
    fun onPlaceChange(value: String) = _uiState.update { it.copy(place = value, error = null) }
    fun onCategorySelected(id: Long) = _uiState.update { it.copy(categoryId = id, error = null) }
    fun onNonNegotiableChange(value: Boolean) = _uiState.update { it.copy(isNonNegotiable = value) }
    fun onDateSelected(epochDay: Long) = _uiState.update { it.copy(dateEpochDay = epochDay, error = null) }

    fun save() {
        val state = _uiState.value
        val amount = Money.fromRupeeString(state.amountText)
        val categoryId = state.categoryId
        if (amount == null || amount.paise <= 0) {
            _uiState.update { it.copy(error = "Enter a valid amount in ₹") }
            return
        }
        if (state.place.isBlank()) {
            _uiState.update { it.copy(error = "Enter where you spent") }
            return
        }
        if (categoryId == null) {
            _uiState.update { it.copy(error = "Pick a category") }
            return
        }
        val atEpochMs = LocalDate.ofEpochDay(state.dateEpochDay)
            .atTime(12, 0)
            .atZone(zone)
            .toInstant()
            .toEpochMilli()

        viewModelScope.launch {
            runCatching {
                if (expenseId == null) {
                    container.addExpense(
                        amount = amount,
                        place = state.place,
                        categoryId = categoryId,
                        isNonNegotiable = state.isNonNegotiable,
                        atEpochMs = atEpochMs,
                    )
                } else {
                    container.updateExpense(
                        id = expenseId,
                        amount = amount,
                        place = state.place,
                        categoryId = categoryId,
                        isNonNegotiable = state.isNonNegotiable,
                        atEpochMs = atEpochMs,
                    )
                }
            }.onSuccess {
                _uiState.update { it.copy(saved = true, error = null) }
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message ?: "Could not save") }
            }
        }
    }

    fun delete() {
        val id = expenseId ?: return
        viewModelScope.launch {
            runCatching { container.deleteExpense(id) }
                .onSuccess { _uiState.update { it.copy(deleted = true, error = null) } }
                .onFailure { e -> _uiState.update { it.copy(error = e.message ?: "Could not delete") } }
        }
    }

    private fun formatAmount(paise: Long): String {
        val rupees = paise / 100.0
        return if (paise % 100L == 0L) (paise / 100).toString() else "%.2f".format(rupees)
    }

    class Factory(
        private val container: AppContainer,
        private val expenseId: Long?,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ExpenseEditorViewModel(container, expenseId) as T
    }
}
