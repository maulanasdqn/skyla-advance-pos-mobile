package com.skyla.pos.products.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skyla.pos.common.UiState
import com.skyla.pos.common.formatAsCurrency
import com.skyla.pos.common.toReadableDateTime
import com.skyla.pos.model.Product
import com.skyla.pos.ui.components.ActiveStatusChip
import com.skyla.pos.ui.components.SkylaButton
import com.skyla.pos.ui.components.SkylaCard
import com.skyla.pos.ui.components.SkylaConfirmDialog
import com.skyla.pos.ui.components.SkylaErrorView
import com.skyla.pos.ui.components.SkylaLoadingScreen
import com.skyla.pos.ui.components.SkylaMoneyText
import com.skyla.pos.ui.components.SkylaOutlinedButton
import com.skyla.pos.ui.components.SkylaTopBar
import com.skyla.pos.ui.theme.StatusWarning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val isActionLoading by viewModel.isActionLoading.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var showDeactivateDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ProductDetailEvent.ProductDeleted -> {
                    onNavigateBack()
                }
                is ProductDetailEvent.ProductDeactivated -> {
                    snackbarHostState.showSnackbar("Product deactivated successfully")
                }
                is ProductDetailEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    if (showDeleteDialog) {
        SkylaConfirmDialog(
            title = "Delete Product",
            message = "Are you sure you want to delete this product? This action cannot be undone.",
            confirmText = "Delete",
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteProduct()
            },
            onDismiss = { showDeleteDialog = false },
            isDestructive = true,
        )
    }

    if (showDeactivateDialog) {
        SkylaConfirmDialog(
            title = "Deactivate Product",
            message = "Are you sure you want to deactivate this product? It will no longer appear in sales.",
            confirmText = "Deactivate",
            onConfirm = {
                showDeactivateDialog = false
                viewModel.deactivateProduct()
            },
            onDismiss = { showDeactivateDialog = false },
        )
    }

    Scaffold(
        topBar = {
            SkylaTopBar(
                title = "Product Details",
                onNavigateBack = onNavigateBack,
                actions = {
                    if (uiState is UiState.Success) {
                        val product = (uiState as UiState.Success<Product>).data
                        IconButton(onClick = { onNavigateToEdit(product.id) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit product",
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete product",
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        when (val state = uiState) {
            is UiState.Loading -> {
                SkylaLoadingScreen(modifier = Modifier.padding(paddingValues))
            }
            is UiState.Error -> {
                SkylaErrorView(
                    message = state.message,
                    onRetry = viewModel::loadProduct,
                    modifier = Modifier.padding(paddingValues),
                )
            }
            is UiState.Idle -> { /* waiting */ }
            is UiState.Success -> {
                ProductDetailContent(
                    product = state.data,
                    isActionLoading = isActionLoading,
                    onDeactivate = { showDeactivateDialog = true },
                    modifier = Modifier.padding(paddingValues),
                )
            }
        }
    }
}

@Composable
private fun ProductDetailContent(
    product: Product,
    isActionLoading: Boolean,
    onDeactivate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        // Header
        SkylaCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    ActiveStatusChip(isActive = product.isActive)
                }

                Spacer(modifier = Modifier.height(8.dp))

                DetailRow(label = "SKU", value = product.sku)
                product.barcode?.let { DetailRow(label = "Barcode", value = it) }
                product.description?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pricing
        SkylaCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = "Pricing",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(
                            text = "Selling Price",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        SkylaMoneyText(
                            amountInCents = product.price,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Cost Price",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        SkylaMoneyText(
                            amountInCents = product.costPrice,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                DetailRow(
                    label = "Profit Margin",
                    value = String.format("%.1f%%", product.profitMargin),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Inventory
        SkylaCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = "Inventory",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(
                            text = "Current Stock",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "${product.currentStock}",
                            style = MaterialTheme.typography.titleLarge,
                            color = if (product.isLowStock) StatusWarning else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Reorder Level",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "${product.reorderLevel}",
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                }

                if (product.isLowStock) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Low stock warning: Current stock is at or below reorder level.",
                        style = MaterialTheme.typography.bodySmall,
                        color = StatusWarning,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Timestamps
        SkylaCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = "Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(12.dp))

                product.categoryId?.let { DetailRow(label = "Category ID", value = it) }
                DetailRow(label = "Created", value = product.createdAt.toReadableDateTime())
                DetailRow(label = "Updated", value = product.updatedAt.toReadableDateTime())
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Actions
        if (product.isActive) {
            SkylaOutlinedButton(
                text = "Deactivate Product",
                onClick = onDeactivate,
                isLoading = isActionLoading,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}
