package com.skyla.pos.sales.presentation.receipt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skyla.pos.common.formatAsCurrency
import com.skyla.pos.common.toReadableDateTime
import com.skyla.pos.model.Payment
import com.skyla.pos.model.PaymentMethod
import com.skyla.pos.model.SaleItem
import com.skyla.pos.model.SaleStatus
import com.skyla.pos.sales.data.dto.ReceiptResponse
import com.skyla.pos.ui.components.SaleStatusChip
import com.skyla.pos.ui.components.SkylaButton
import com.skyla.pos.ui.components.SkylaConfirmDialog
import com.skyla.pos.ui.components.SkylaErrorView
import com.skyla.pos.ui.components.SkylaLoadingOverlay
import com.skyla.pos.ui.components.SkylaLoadingScreen
import com.skyla.pos.ui.components.SkylaMoneyText
import com.skyla.pos.ui.components.SkylaOutlinedButton
import com.skyla.pos.ui.components.SkylaTextField
import com.skyla.pos.ui.components.SkylaTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReceiptViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ReceiptEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                is ReceiptEvent.SaleVoided -> snackbarHostState.showSnackbar("Sale has been voided")
            }
        }
    }

    Scaffold(
        topBar = {
            SkylaTopBar(
                title = "Receipt",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = { /* Print placeholder */ }) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = "Print receipt",
                        )
                    }
                    IconButton(onClick = { /* Share placeholder */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share receipt",
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when {
                uiState.isLoading && uiState.receipt == null -> {
                    SkylaLoadingScreen()
                }
                uiState.errorMessage != null && uiState.receipt == null -> {
                    SkylaErrorView(
                        message = uiState.errorMessage!!,
                        onRetry = { viewModel.loadReceipt() },
                    )
                }
                uiState.receipt != null -> {
                    ReceiptContent(
                        receipt = uiState.receipt!!,
                        isVoiding = uiState.isVoiding,
                        onVoidSale = viewModel::voidSale,
                    )
                }
            }

            if (uiState.isVoiding) {
                SkylaLoadingOverlay()
            }
        }
    }
}

@Composable
private fun ReceiptContent(
    receipt: ReceiptResponse,
    isVoiding: Boolean,
    onVoidSale: (String) -> Unit,
) {
    val sale = receipt.sale
    var showVoidDialog by remember { mutableStateOf(false) }
    var voidReason by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        // Header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = sale.saleNumber,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = sale.createdAt.toReadableDateTime(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            SaleStatusChip(status = sale.status.name.lowercase())
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Cashier and customer info
        Text(
            text = "Cashier: ${receipt.cashierName}",
            style = MaterialTheme.typography.bodyMedium,
        )
        if (receipt.customerName != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Customer: ${receipt.customerName}",
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Items header
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Item",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "Qty",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(40.dp),
            )
            Text(
                text = "Price",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End,
                modifier = Modifier.width(80.dp),
            )
            Text(
                text = "Total",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End,
                modifier = Modifier.width(80.dp),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Items list
        sale.items.forEach { item ->
            ReceiptItemRow(item = item)
            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(12.dp))

        // Totals
        SummaryRow(label = "Subtotal", amount = sale.subtotal)
        if (sale.discountAmount > 0) {
            SummaryRow(label = "Discount", amount = -sale.discountAmount)
        }
        SummaryRow(label = "Tax", amount = sale.taxAmount)

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Total",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            SkylaMoneyText(
                amountInCents = sale.totalAmount,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        // Payments section
        if (receipt.payments.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Payments",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(8.dp))

            receipt.payments.forEach { payment ->
                PaymentRow(payment = payment)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // Void reason if voided
        if (sale.status == SaleStatus.VOIDED && sale.voidReason != null) {
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Void Reason",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.error,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = sale.voidReason!!,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // Void sale button (only for completed sales)
        if (sale.status == SaleStatus.COMPLETED) {
            Spacer(modifier = Modifier.height(24.dp))
            SkylaOutlinedButton(
                text = "Void Sale",
                onClick = { showVoidDialog = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isVoiding,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Void confirmation dialog
    if (showVoidDialog) {
        VoidSaleDialog(
            reason = voidReason,
            onReasonChanged = { voidReason = it },
            onConfirm = {
                onVoidSale(voidReason)
                showVoidDialog = false
                voidReason = ""
            },
            onDismiss = {
                showVoidDialog = false
                voidReason = ""
            },
        )
    }
}

@Composable
private fun ReceiptItemRow(item: SaleItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = item.productName,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "${item.quantity}",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(40.dp),
        )
        Text(
            text = item.unitPrice.formatAsCurrency(),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.End,
            modifier = Modifier.width(80.dp),
        )
        Text(
            text = item.lineTotal.formatAsCurrency(),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End,
            modifier = Modifier.width(80.dp),
        )
    }
}

@Composable
private fun SummaryRow(label: String, amount: Long) {
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
        Text(
            text = amount.formatAsCurrency(),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun PaymentRow(payment: Payment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = when (payment.paymentMethod) {
                    PaymentMethod.CASH -> "Cash"
                    PaymentMethod.CARD -> "Card"
                    PaymentMethod.E_WALLET -> "E-Wallet"
                },
                style = MaterialTheme.typography.bodyMedium,
            )
            if (payment.referenceNumber != null) {
                Text(
                    text = "Ref: ${payment.referenceNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = payment.amount.formatAsCurrency(),
                style = MaterialTheme.typography.bodyMedium,
            )
            if (payment.changeAmount > 0) {
                Text(
                    text = "Change: ${payment.changeAmount.formatAsCurrency()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun VoidSaleDialog(
    reason: String,
    onReasonChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Void Sale",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error,
            )
        },
        text = {
            Column {
                Text(
                    text = "Are you sure you want to void this sale? This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(16.dp))
                SkylaTextField(
                    value = reason,
                    onValueChange = onReasonChanged,
                    label = "Reason for voiding",
                    singleLine = false,
                )
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(
                onClick = onConfirm,
                enabled = reason.isNotBlank(),
                colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Text(text = "Void Sale")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
    )
}
