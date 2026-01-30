package com.skyla.pos.sales.presentation.list

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
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skyla.pos.common.formatAsCurrency
import com.skyla.pos.common.toReadableDateTime
import com.skyla.pos.model.Sale
import com.skyla.pos.model.SaleStatus
import com.skyla.pos.ui.components.SaleStatusChip
import com.skyla.pos.ui.components.SkylaCard
import com.skyla.pos.ui.components.SkylaErrorView
import com.skyla.pos.ui.components.SkylaMoneyText
import com.skyla.pos.ui.components.SkylaPaginatedList
import com.skyla.pos.ui.components.SkylaTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesListScreen(
    onNavigateBack: () -> Unit,
    onSaleClick: (saleId: String, status: SaleStatus) -> Unit,
    onCreateSale: () -> Unit,
    viewModel: SalesListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            SkylaTopBar(
                title = "Sales",
                onNavigateBack = onNavigateBack,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateSale,
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create new sale",
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
            // Filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SaleFilterOption.entries.forEach { filter ->
                    FilterChip(
                        selected = uiState.selectedFilter == filter,
                        onClick = { viewModel.onFilterSelected(filter) },
                        label = { Text(text = filter.label) },
                    )
                }
            }

            // Content
            if (uiState.errorMessage != null && uiState.sales.isEmpty()) {
                SkylaErrorView(
                    message = uiState.errorMessage!!,
                    onRetry = { viewModel.loadSales() },
                )
            } else {
                SkylaPaginatedList(
                    items = uiState.sales,
                    isLoading = uiState.isLoading || uiState.isLoadingMore,
                    hasMore = uiState.hasMore,
                    onLoadMore = { viewModel.loadMoreSales() },
                    emptyTitle = "No Sales",
                    emptyMessage = "No sales found. Tap + to create a new sale.",
                    itemContent = { _, sale ->
                        SaleListItem(
                            sale = sale,
                            onClick = { onSaleClick(sale.id, sale.status) },
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun SaleListItem(
    sale: Sale,
    onClick: () -> Unit,
) {
    SkylaCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = sale.saleNumber,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = sale.createdAt.toReadableDateTime(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                SaleStatusChip(status = sale.status.name.lowercase())
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${sale.items.size} item${if (sale.items.size != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                SkylaMoneyText(
                    amountInCents = sale.totalAmount,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
