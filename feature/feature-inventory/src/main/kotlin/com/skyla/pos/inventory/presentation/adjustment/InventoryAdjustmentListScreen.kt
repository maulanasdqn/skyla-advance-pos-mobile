package com.skyla.pos.inventory.presentation.adjustment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skyla.pos.common.toReadableDateTime
import com.skyla.pos.model.AdjustmentReason
import com.skyla.pos.model.InventoryAdjustment
import com.skyla.pos.ui.components.SkylaCard
import com.skyla.pos.ui.components.SkylaErrorView
import com.skyla.pos.ui.components.SkylaPaginatedList
import com.skyla.pos.ui.components.SkylaStatusChip
import com.skyla.pos.ui.components.SkylaTopBar
import com.skyla.pos.ui.theme.StatusSuccess
import com.skyla.pos.ui.theme.StatusVoided
import com.skyla.pos.ui.theme.StatusWarning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryAdjustmentListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToForm: () -> Unit,
    viewModel: InventoryAdjustmentListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            SkylaTopBar(
                title = "Adjustment History",
                onNavigateBack = onNavigateBack,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToForm,
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create new adjustment",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            if (uiState.errorMessage != null && uiState.adjustments.isEmpty()) {
                SkylaErrorView(
                    message = uiState.errorMessage!!,
                    onRetry = { viewModel.loadAdjustments() },
                )
            } else {
                SkylaPaginatedList(
                    items = uiState.adjustments,
                    isLoading = uiState.isLoading || uiState.isLoadingMore,
                    hasMore = uiState.hasMore,
                    onLoadMore = { viewModel.loadMoreAdjustments() },
                    emptyTitle = "No Adjustments",
                    emptyMessage = "No inventory adjustments found. Tap + to create one.",
                    itemContent = { _, adjustment ->
                        AdjustmentListItem(adjustment = adjustment)
                    },
                )
            }
        }
    }
}

@Composable
private fun AdjustmentListItem(adjustment: InventoryAdjustment) {
    SkylaCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Product: ${adjustment.productId}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(8.dp))
                ReasonChip(reason = adjustment.reason)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = adjustment.createdAt.toReadableDateTime(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                QuantityChangeText(quantityChange = adjustment.quantityChange)
            }

            if (!adjustment.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = adjustment.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ReasonChip(reason: AdjustmentReason) {
    val (label, color) = when (reason) {
        AdjustmentReason.PURCHASE -> "Purchase" to StatusSuccess
        AdjustmentReason.SALE -> "Sale" to MaterialTheme.colorScheme.primary
        AdjustmentReason.RETURN -> "Return" to StatusWarning
        AdjustmentReason.DAMAGE -> "Damage" to StatusVoided
        AdjustmentReason.CORRECTION -> "Correction" to MaterialTheme.colorScheme.tertiary
        AdjustmentReason.INITIAL -> "Initial" to MaterialTheme.colorScheme.secondary
    }
    SkylaStatusChip(
        label = label,
        color = color,
    )
}

@Composable
private fun QuantityChangeText(quantityChange: Int) {
    val isPositive = quantityChange > 0
    val displayText = if (isPositive) "+$quantityChange" else "$quantityChange"
    val color = if (isPositive) StatusSuccess else StatusVoided

    Text(
        text = displayText,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = color,
    )
}
