package com.moneytracker.app.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moneytracker.app.di.AppContainer
import com.moneytracker.app.domain.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CategoriesUiState(
    val categories: List<Category> = emptyList(),
    val newName: String = "",
    val error: String? = null,
)

class CategoriesViewModel(
    private val container: AppContainer,
) : ViewModel() {
    private val _form = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = kotlinx.coroutines.flow.combine(
        container.repository.observeCategories(),
        _form,
    ) { categories, form ->
        form.copy(categories = categories)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CategoriesUiState())

    fun onNameChange(value: String) = _form.update { it.copy(newName = value, error = null) }

    fun add() {
        val name = _form.value.newName
        viewModelScope.launch {
            runCatching { container.addCategory(name) }
                .onSuccess { _form.update { it.copy(newName = "", error = null) } }
                .onFailure { e -> _form.update { it.copy(error = e.message ?: "Could not add") } }
        }
    }

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CategoriesViewModel(container) as T
    }
}
