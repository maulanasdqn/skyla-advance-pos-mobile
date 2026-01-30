package com.skyla.pos.inventory.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skyla.pos.model.StockLevel
import com.skyla.pos.ui.components.SkylaCard
import com.skyla.pos.ui.components.SkylaEmptyView
import com.skyla.pos.ui.components.SkylaErrorView
import com.skyla.pos.ui.components.SkylaLoadingScreen
import com.skyla.pos.ui.components.SkylaStatusChip
import com.skyla.pos.ui.components.SkylaTopBar
import com.skyla.pos.ui.theme.StatusSuccess
import com.skyla.pos.ui.theme.StatusWarning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAdjustmentForm: () -> Unit,
    onNavigateToAdjustmentHistory: () -> Unit,
    viewModel: InventoryListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            SkylaTopBar(
                title = "Inventory",
                onNavigateBack = onNavigateBack,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            // Tab row
            TabRow(
                selectedTabIndex = InventoryTab.entries.indexOf(uiState.selectedTab),
            ) {
                InventoryTab.entries.forEach { tab ->
                    Tab(
                        selected = uiState.selectedTab == tab,
                        onClick = { viewModel.onTabSelected(tab) },
                        text = { Text(text = tab.label) },
                    )
                }
            }

            // Content
            when {
                uiState.isLoading -> {
                    SkylaLoadingScreen()
                }
                uiState.errorMessage != null -> {
                    SkylaErrorView(
                        message = uiState.errorMessage!!,
                        onRetry = { viewModel.loadData() },
                    )
                }
                uiState.lowStockProducts.isEmpty() -> {
                    SkylaEmptyView(
                        title = if (uiState.selectedTab == InventoryTab.LOW_STOCK) {
                            "No Low Stock Items"
                        } else {
                            "No Stock Data"
                        },
                        message = if (uiState.selectedTab == InventoryTab.LOW_STOCK) {
                            "All products have sufficient stock levels."
                        } else {
                            "No stock level data available."
                        },
                        icon = Icons.Default.Inventory2,
                    )
                }
                else -> {
                    val displayItems = when (uiState.selectedTab) {
                        InventoryTab.LOW_STOCK -> uiState.lowStockProducts.filter { it.isLowStock }
                        InventoryTab.STOCK_LEVELS -> uiState.lowStockProducts
                    }

                    if (displayItems.isEmpty() && uiState.selectedTab == InventoryTab.LOW_STOCK) {
                        SkylaEmptyView(
                            title = "No Low Stock Items",
                            message = "All products have sufficient stock levels.",
                            icon = Icons.Default.Inventory2,
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(displayItems, key = { it.productId }) { stockLevel ->
                                StockLevelItem(stockLevel = stockLevel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StockLevelItem(stockLevel: StockLevel) {
    SkylaCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stockLevel.productName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "SKU: ${stockLevel.productSku}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                if (stockLevel.isLowStock) {
                    SkylaStatusChip(
                        label = "Low Stock",
                        color = StatusWarning,
                    )
                } else {
                    SkylaStatusChip(
                        label = "In Stock",
                        color = StatusSuccess,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                StockInfoColumn(
                    label = "Current Stock",
                    value = "${stockLevel.currentStock}",
                    isWarning = stockLevel.isLowStock,
                )
                StockInfoColumn(
                    label = "Reorder Level",
                    value = "${stockLevel.reorderLevel}",
                    isWarning = false,
                )
            }
        }
    }
}

@Composable
private fun StockInfoColumn(
    label: String,
    value: String,
    isWarning: Boolean,
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isWarning) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Low stock warning",
                    tint = StatusWarning,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .height(16.dp)
                        .width(16.dp),
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (isWarning) StatusWarning else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
