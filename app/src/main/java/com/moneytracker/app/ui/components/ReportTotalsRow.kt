package com.moneytracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.moneytracker.app.domain.model.MonthReport

@Composable
fun ReportTotalsRow(
    report: MonthReport,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.28f), shape)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        TotalCell(label = "Spent", value = report.totalSpent.formatInr())
        TotalCell(
            label = "Remaining",
            value = report.remaining.formatInr(),
            emphasize = report.remaining.paise < 0,
        )
        TotalCell(
            label = "Of budget",
            value = if (report.budget.paise <= 0L) "—" else "${report.budgetUsedPercent}%",
        )
    }
}

@Composable
private fun TotalCell(
    label: String,
    value: String,
    emphasize: Boolean = false,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = if (emphasize) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        )
    }
}
