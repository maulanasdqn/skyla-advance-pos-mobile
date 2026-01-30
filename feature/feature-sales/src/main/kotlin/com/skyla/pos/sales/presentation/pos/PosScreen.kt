package com.skyla.pos.sales.presentation.pos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skyla.pos.common.formatAsCurrency
import com.skyla.pos.model.Product
import com.skyla.pos.model.SaleItem
import com.skyla.pos.ui.components.SkylaButton
import com.skyla.pos.ui.components.SkylaCard
import com.skyla.pos.ui.components.SkylaLoadingOverlay
import com.skyla.pos.ui.components.SkylaLoadingScreen
import com.skyla.pos.ui.components.SkylaMoneyText
import com.skyla.pos.ui.components.SkylaOutlinedButton
import com.skyla.pos.ui.components.SkylaSearchBar
import com.skyla.pos.ui.components.SkylaTextField
import com.skyla.pos.ui.components.SkylaTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPayment: (saleId: String) -> Unit,
    viewModel: PosViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is PosEvent.NavigateToPayment -> onNavigateToPayment(event.saleId)
                is PosEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                is PosEvent.SaleCreated -> Unit
            }
        }
    }

    Scaffold(
        topBar = {
            SkylaTopBar(
                title = uiState.sale?.saleNumber ?: "New Sale",
                onNavigateBack = onNavigateBack,
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        if (uiState.isLoading && uiState.sale == null) {
            SkylaLoadingScreen()
            return@Scaffold
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                // Product search bar
                SkylaSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChanged,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = "Search products...",
                )

                // Search results dropdown
                if (uiState.searchResults.isNotEmpty()) {
                    SearchResultsDropdown(
                        results = uiState.searchResults,
                        onProductSelected = { product ->
                            viewModel.addItem(product)
                        },
                    )
                }

                // Cart items
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    val items = uiState.sale?.items ?: emptyList()
                    if (items.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillParentMaxHeight(0.5f)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "Cart is empty. Search and add products above.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    } else {
                        items(items, key = { it.id }) { item ->
                            CartItemRow(
                                item = item,
                                onIncrease = { viewModel.updateItemQuantity(item, item.quantity + 1) },
                                onDecrease = { viewModel.updateItemQuantity(item, item.quantity - 1) },
                                onRemove = { viewModel.removeItem(item) },
                            )
                        }
                    }
                }

                // Bottom summary section
                SaleSummarySection(
                    subtotal = uiState.sale?.subtotal ?: 0L,
                    discount = uiState.sale?.discountAmount ?: 0L,
                    tax = uiState.sale?.taxAmount ?: 0L,
                    total = uiState.sale?.totalAmount ?: 0L,
                    onApplyDiscount = viewModel::showDiscountDialog,
                    onPay = viewModel::navigateToPayment,
                    isPayEnabled = (uiState.sale?.items?.isNotEmpty() == true) && !uiState.isProcessing,
                )
            }

            // Processing overlay
            if (uiState.isProcessing) {
                SkylaLoadingOverlay()
            }
        }

        // Discount dialog
        if (uiState.showDiscountDialog) {
            DiscountDialog(
                discountInput = uiState.discountInput,
                onDiscountInputChanged = viewModel::onDiscountInputChanged,
                onApply = viewModel::applyDiscount,
                onDismiss = viewModel::dismissDiscountDialog,
            )
        }
    }
}

@Composable
private fun SearchResultsDropdown(
    results: List<Product>,
    onProductSelected: (Product) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        results.forEach { product ->
            SkylaCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onProductSelected(product) },
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "SKU: ${product.sku}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    SkylaMoneyText(
                        amountInCents = product.price,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: SaleItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.productName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = item.unitPrice.formatAsCurrency(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Quantity controls
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onDecrease,
                modifier = Modifier.size(32.dp),
                enabled = item.quantity > 1,
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease quantity",
                    modifier = Modifier.size(18.dp),
                )
            }

            Text(
                text = "${item.quantity}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp),
            )

            IconButton(
                onClick = onIncrease,
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase quantity",
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        SkylaMoneyText(
            amountInCents = item.lineTotal,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(32.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove item",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp),
            )
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun SaleSummarySection(
    subtotal: Long,
    discount: Long,
    tax: Long,
    total: Long,
    onApplyDiscount: () -> Unit,
    onPay: () -> Unit,
    isPayEnabled: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        HorizontalDivider()
        Spacer(modifier = Modifier.height(12.dp))

        SummaryRow(label = "Subtotal", amountInCents = subtotal)
        if (discount > 0) {
            SummaryRow(
                label = "Discount",
                amountInCents = -discount,
                valueColor = MaterialTheme.colorScheme.error,
            )
        }
        SummaryRow(label = "Tax", amountInCents = tax)

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Total",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            SkylaMoneyText(
                amountInCents = total,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SkylaOutlinedButton(
                text = "Discount",
                onClick = onApplyDiscount,
                modifier = Modifier.weight(1f),
            )
            SkylaButton(
                text = "Pay",
                onClick = onPay,
                modifier = Modifier.weight(1f),
                enabled = isPayEnabled,
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    amountInCents: Long,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        SkylaMoneyText(
            amountInCents = amountInCents,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
        )
    }
}

@Composable
private fun DiscountDialog(
    discountInput: String,
    onDiscountInputChanged: (String) -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Apply Discount",
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            SkylaTextField(
                value = discountInput,
                onValueChange = onDiscountInputChanged,
                label = "Discount amount ($)",
                placeholder = "0.00",
                keyboardType = KeyboardType.Decimal,
            )
        },
        confirmButton = {
            TextButton(
                onClick = onApply,
                enabled = discountInput.isNotBlank(),
            ) {
                Text(text = "Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
    )
}
