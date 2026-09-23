package com.moneytracker.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.moneytracker.app.domain.model.Money
import com.moneytracker.app.ui.theme.MoneyColors

@Composable
fun MonthSummaryCard(
    title: String,
    budget: Money,
    spent: Money,
    remaining: Money,
    modifier: Modifier = Modifier,
) {
    val progress = if (budget.paise > 0) {
        (spent.paise.toFloat() / budget.paise.toFloat()).coerceIn(0f, 1.2f)
    } else {
        0f
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceAtMost(1f),
        animationSpec = tween(700),
        label = "budgetProgress",
    )
    val overBudget = remaining.paise < 0

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MoneyColors.HeroBrush)
            .padding(24.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.75f),
            )
            AmountText(
                money = remaining,
                style = MaterialTheme.typography.displayLarge,
                color = Color.White,
            )
            Text(
                text = if (overBudget) "Over budget" else "Left to spend",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.85f),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(Color.White.copy(alpha = 0.22f)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(8.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(
                            if (overBudget) MoneyColors.Coral else Color.White,
                        ),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        "Budget",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.7f),
                    )
                    Text(
                        budget.formatInr(),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                    )
                }
                Column {
                    Text(
                        "Spent",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.7f),
                    )
                    Text(
                        spent.formatInr(),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                    )
                }
            }
        }
    }
}
