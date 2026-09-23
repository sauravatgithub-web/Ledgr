package com.moneytracker.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneytracker.app.ui.components.ExpenseRow
import com.moneytracker.app.ui.components.MonthSummaryCard
import com.moneytracker.app.ui.theme.MoneyColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAddExpense: () -> Unit,
    onEditExpense: (Long) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddExpense,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add expense") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MoneyColors.ScreenBrush)
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Ledgr",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = "Keep a clear eye on this month’s spend.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                MonthSummaryCard(
                    title = state.yearMonthLabel,
                    budget = state.budget,
                    spent = state.spent,
                    remaining = state.remaining,
                )
            }
            item {
                Text(
                    text = "Recent",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                )
            }
            item {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = viewModel::onSearchChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Search place or category") },
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = state.nonNegotiableFilter == NonNegotiableFilter.ALL,
                        onClick = { viewModel.onNonNegotiableFilter(NonNegotiableFilter.ALL) },
                        label = { Text("All") },
                    )
                    FilterChip(
                        selected = state.nonNegotiableFilter == NonNegotiableFilter.ONLY,
                        onClick = { viewModel.onNonNegotiableFilter(NonNegotiableFilter.ONLY) },
                        label = { Text("Non-negotiable") },
                    )
                    FilterChip(
                        selected = state.nonNegotiableFilter == NonNegotiableFilter.EXCLUDE,
                        onClick = { viewModel.onNonNegotiableFilter(NonNegotiableFilter.EXCLUDE) },
                        label = { Text("Flexible") },
                    )
                    state.categories.forEach { category ->
                        FilterChip(
                            selected = state.categoryFilterId == category.id,
                            onClick = { viewModel.onCategoryFilter(category.id) },
                            label = { Text(category.name) },
                        )
                    }
                }
            }
            if (state.recentByDate.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.padding(vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = if (state.searchQuery.isNotBlank() ||
                                state.categoryFilterId != null ||
                                state.nonNegotiableFilter != NonNegotiableFilter.ALL
                            ) {
                                "No matches"
                            } else {
                                "Nothing logged yet"
                            },
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "Tap Add expense when you spend — or clear filters.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            } else {
                state.recentByDate.forEach { group ->
                    item(key = "header-${group.dateKey}") {
                        Text(
                            text = group.label,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp, bottom = 2.dp),
                        )
                    }
                    items(group.expenses, key = { it.id }) { expense ->
                        ExpenseRow(
                            expense = expense,
                            categoryName = state.categoryNames[expense.categoryId] ?: "Unknown",
                            onClick = { onEditExpense(expense.id) },
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(72.dp)) }
        }
    }
}
