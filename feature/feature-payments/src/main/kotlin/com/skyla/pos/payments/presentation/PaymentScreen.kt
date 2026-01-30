package com.skyla.pos.payments.presentation

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skyla.pos.common.formatAsCurrency
import com.skyla.pos.model.Payment
import com.skyla.pos.model.PaymentMethod
import com.skyla.pos.ui.components.SkylaButton
import com.skyla.pos.ui.components.SkylaCard
import com.skyla.pos.ui.components.SkylaErrorView
import com.skyla.pos.ui.components.SkylaLoadingOverlay
import com.skyla.pos.ui.components.SkylaLoadingScreen
import com.skyla.pos.ui.components.SkylaMoneyText
import com.skyla.pos.ui.components.SkylaTextField
import com.skyla.pos.ui.components.SkylaTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    onNavigateBack: () -> Unit,
    onSaleCompleted: (saleId: String) -> Unit,
    viewModel: PaymentViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is PaymentEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                is PaymentEvent.PaymentAdded -> snackbarHostState.showSnackbar("Payment added successfully")
                is PaymentEvent.SaleCompleted -> onSaleCompleted(event.saleId)
            }
        }
    }

    Scaffold(
        topBar = {
            SkylaTopBar(
                title = "Payment",
                onNavigateBack = onNavigateBack,
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
                uiState.isLoading && uiState.summary == null -> {
                    SkylaLoadingScreen()
                }
                uiState.errorMessage != null && uiState.summary == null -> {
                    SkylaErrorView(
                        message = uiState.errorMessage!!,
                        onRetry = { viewModel.loadPaymentSummary() },
                    )
                }
                uiState.summary != null -> {
                    PaymentContent(
                        uiState = uiState,
                        onPaymentMethodSelected = viewModel::onPaymentMethodSelected,
                        onAmountInputChanged = viewModel::onAmountInputChanged,
                        onReferenceNumberChanged = viewModel::onReferenceNumberChanged,
                        onAddPayment = viewModel::addPayment,
                        onCompleteSale = viewModel::completeSale,
                    )
                }
            }

            if (uiState.isProcessing) {
                SkylaLoadingOverlay()
            }
        }
    }
}

@Composable
private fun PaymentContent(
    uiState: PaymentUiState,
    onPaymentMethodSelected: (PaymentMethod) -> Unit,
    onAmountInputChanged: (String) -> Unit,
    onReferenceNumberChanged: (String) -> Unit,
    onAddPayment: () -> Unit,
    onCompleteSale: () -> Unit,
) {
    val summary = uiState.summary ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        // Balance summary card
        SkylaCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Total Amount",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    SkylaMoneyText(
                        amountInCents = summary.totalAmount,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Total Paid",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    SkylaMoneyText(
                        amountInCents = summary.totalPaid,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Remaining Balance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    SkylaMoneyText(
                        amountInCents = summary.remainingBalance,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (summary.remainingBalance > 0) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Payment method selector
        if (!uiState.isFullyPaid) {
            Text(
                text = "Payment Method",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PaymentMethod.entries.forEach { method ->
                    FilterChip(
                        selected = uiState.selectedMethod == method,
                        onClick = { onPaymentMethodSelected(method) },
                        label = {
                            Text(
                                text = when (method) {
                                    PaymentMethod.CASH -> "Cash"
                                    PaymentMethod.CARD -> "Card"
                                    PaymentMethod.E_WALLET -> "E-Wallet"
                                },
                            )
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount input
            SkylaTextField(
                value = uiState.amountInput,
                onValueChange = onAmountInputChanged,
                label = "Amount ($)",
                placeholder = "0.00",
                keyboardType = KeyboardType.Decimal,
            )

            // Change calculation for cash
            if (uiState.selectedMethod == PaymentMethod.CASH && uiState.calculatedChange > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Change",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = uiState.calculatedChange.formatAsCurrency(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            // Reference number field (for card/e-wallet)
            if (uiState.selectedMethod != PaymentMethod.CASH) {
                Spacer(modifier = Modifier.height(12.dp))
                SkylaTextField(
                    value = uiState.referenceNumber,
                    onValueChange = onReferenceNumberChanged,
                    label = "Reference Number",
                    placeholder = "Enter reference number",
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            SkylaButton(
                text = "Add Payment",
                onClick = onAddPayment,
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.amountInput.isNotBlank() && !uiState.isProcessing,
                isLoading = uiState.isProcessing,
            )
        }

        // Existing payments list
        if (summary.payments.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Payments Made",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(8.dp))

            summary.payments.forEach { payment ->
                PaymentListItem(payment = payment)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Complete sale button
        if (uiState.isFullyPaid) {
            Spacer(modifier = Modifier.height(24.dp))
            SkylaButton(
                text = "Complete Sale",
                onClick = onCompleteSale,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun PaymentListItem(payment: Payment) {
    SkylaCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                    fontWeight = FontWeight.Medium,
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
                SkylaMoneyText(
                    amountInCents = payment.amount,
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
}
