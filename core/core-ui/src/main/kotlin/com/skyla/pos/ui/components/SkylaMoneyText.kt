package com.skyla.pos.ui.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SkylaMoneyText(
    amountInCents: Long,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    val formattedAmount = formatCentsToCurrency(amountInCents)
    Text(
        text = formattedAmount,
        modifier = modifier,
        style = style,
        color = color,
    )
}

private fun formatCentsToCurrency(cents: Long): String {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    return currencyFormat.format(cents / 100.0)
}
