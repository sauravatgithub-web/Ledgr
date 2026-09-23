package com.moneytracker.app.ui.more

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.moneytracker.app.di.AppContainer
import com.moneytracker.app.domain.model.YearMonths
import com.moneytracker.app.ui.theme.MoneyColors
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

private val MoreItemShape = RoundedCornerShape(20.dp)

@Composable
fun MoreScreen(
    container: AppContainer,
    onCategories: () -> Unit,
    onBudget: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val yearMonth = YearMonths.current()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MoneyColors.ScreenBrush)
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("More", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Budget, categories, and backup.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            MoreRow(
                title = "Monthly budget",
                subtitle = "Set this month’s spending cap",
                onClick = onBudget,
            )
            MoreRow(
                title = "Categories",
                subtitle = "Organise where money goes",
                onClick = onCategories,
            )
            MoreRow(
                title = "Export this month (CSV)",
                subtitle = "Share a spreadsheet of ${YearMonths.displayLabel(yearMonth)}",
                onClick = {
                    scope.launch {
                        val expenses = container.repository.observeExpenses(yearMonth).first()
                        val categories = container.repository.observeCategories().first()
                        val csv = container.exportMonthCsv(yearMonth, expenses, categories)
                        val dir = File(context.cacheDir, "exports").apply { mkdirs() }
                        val file = File(dir, "ledgr-$yearMonth.csv")
                        file.writeText(csv)
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file,
                        )
                        val share = Intent(Intent.ACTION_SEND).apply {
                            type = "text/csv"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            putExtra(Intent.EXTRA_SUBJECT, "Ledgr $yearMonth")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(share, "Export CSV"))
                    }
                },
            )
        }
    }
}

@Composable
private fun MoreRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        trailingContent = {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clip(MoreItemShape)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f), MoreItemShape)
            .clickable(onClick = onClick),
    )
}
