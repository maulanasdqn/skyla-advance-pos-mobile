package com.skyla.pos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.skyla.pos.ui.theme.StatusDraft
import com.skyla.pos.ui.theme.StatusSuccess
import com.skyla.pos.ui.theme.StatusVoided

@Composable
fun SkylaStatusChip(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
        )
    }
}

/**
 * Predefined status chip for sale statuses.
 * Accepts a status string and maps it to the appropriate color.
 *
 * Supported statuses: "draft", "completed", "voided".
 */
@Composable
fun SaleStatusChip(
    status: String,
    modifier: Modifier = Modifier,
) {
    val (label, color) = when (status.lowercase()) {
        "draft" -> "Draft" to StatusDraft
        "completed" -> "Completed" to StatusSuccess
        "voided" -> "Voided" to StatusVoided
        else -> status.replaceFirstChar { it.uppercase() } to StatusDraft
    }
    SkylaStatusChip(
        label = label,
        color = color,
        modifier = modifier,
    )
}

/**
 * Predefined status chip for active/inactive states.
 */
@Composable
fun ActiveStatusChip(
    isActive: Boolean,
    modifier: Modifier = Modifier,
) {
    val label = if (isActive) "Active" else "Inactive"
    val color = if (isActive) StatusSuccess else StatusDraft
    SkylaStatusChip(
        label = label,
        color = color,
        modifier = modifier,
    )
}
