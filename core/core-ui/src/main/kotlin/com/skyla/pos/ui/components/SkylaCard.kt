package com.skyla.pos.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SkylaCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    if (onClick != null) {
        ElevatedCard(
            onClick = onClick,
            modifier = modifier,
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.padding(16.dp),
            ) {
                content()
            }
        }
    } else {
        ElevatedCard(
            modifier = modifier,
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.padding(16.dp),
            ) {
                content()
            }
        }
    }
}
