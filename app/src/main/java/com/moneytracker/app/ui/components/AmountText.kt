package com.moneytracker.app.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.moneytracker.app.domain.model.Money

@Composable
fun AmountText(
    money: Money,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.displaySmall,
    emphasize: Boolean = true,
    color: Color? = null,
) {
    val resolved = color ?: when {
        money.paise < 0 -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onBackground
    }
    Text(
        text = money.formatInr(),
        modifier = modifier,
        style = style.copy(
            fontWeight = if (emphasize) FontWeight.Medium else style.fontWeight,
            color = resolved,
        ),
    )
}
