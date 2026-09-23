package com.moneytracker.app.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneytracker.app.domain.usecase.ReportMode
import com.moneytracker.app.ui.components.CategoryBarChart
import com.moneytracker.app.ui.components.DailySpendChart
import com.moneytracker.app.ui.components.ReportTotalsRow
import com.moneytracker.app.ui.theme.MoneyColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(viewModel: ReportsViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val report = state.report

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MoneyColors.ScreenBrush)
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(
                    text = "Reports",
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = viewModel::previousMonth) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous month")
                    }
                    Text(state.yearMonthLabel, style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = viewModel::nextMonth) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next month")
                    }
                }
            }
            item {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = state.mode == ReportMode.ALL,
                        onClick = { viewModel.setMode(ReportMode.ALL) },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    ) { Text("All spend") }
                    SegmentedButton(
                        selected = state.mode == ReportMode.FLEXIBLE,
                        onClick = { viewModel.setMode(ReportMode.FLEXIBLE) },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    ) { Text("Flexible") }
                }
            }

            if (report == null) return@LazyColumn

            item {
                ReportTotalsRow(report = report)
            }

            if (report.dailyBreakdown.isNotEmpty()) {
                item {
                    DailySpendChart(days = report.dailyBreakdown)
                }
            }

            if (report.categoryBreakdown.isEmpty()) {
                item {
                    Text(
                        "No spending recorded for this view.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                item {
                    CategoryBarChart(spends = report.categoryBreakdown.take(8))
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}
