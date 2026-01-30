package com.skyla.pos.customers.presentation.detail

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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
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
import com.skyla.pos.model.Customer
import com.skyla.pos.ui.components.ActiveStatusChip
import com.skyla.pos.ui.components.SkylaCard
import com.skyla.pos.ui.components.SkylaConfirmDialog
import com.skyla.pos.ui.components.SkylaErrorView
import com.skyla.pos.ui.components.SkylaLoadingScreen
import com.skyla.pos.ui.components.SkylaTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: CustomerDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val isActionLoading by viewModel.isActionLoading.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CustomerDetailEvent.CustomerDeleted -> {
                    onNavigateBack()
                }
                is CustomerDetailEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    if (showDeleteDialog) {
        SkylaConfirmDialog(
            title = "Delete Customer",
            message = "Are you sure you want to delete this customer? This action cannot be undone.",
            confirmText = "Delete",
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteCustomer()
            },
            onDismiss = { showDeleteDialog = false },
            isDestructive = true,
        )
    }

    Scaffold(
        topBar = {
            SkylaTopBar(
                title = "Customer Details",
                onNavigateBack = onNavigateBack,
                actions = {
                    if (uiState is UiState.Success) {
                        val customer = (uiState as UiState.Success<Customer>).data
                        IconButton(onClick = { onNavigateToEdit(customer.id) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit customer",
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete customer",
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
                    onRetry = viewModel::loadCustomer,
                    modifier = Modifier.padding(paddingValues),
                )
            }
            is UiState.Idle -> { /* waiting */ }
            is UiState.Success -> {
                CustomerDetailContent(
                    customer = state.data,
                    modifier = Modifier.padding(paddingValues),
                )
            }
        }
    }
}

@Composable
private fun CustomerDetailContent(
    customer: Customer,
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
                        text = customer.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    ActiveStatusChip(isActive = customer.isActive)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Contact info
                customer.phone?.let { phone ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 12.dp),
                        )
                        Text(
                            text = phone,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                customer.email?.let { email ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 12.dp),
                        )
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Loyalty & Spending
        SkylaCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = "Loyalty & Spending",
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
                            text = "Loyalty Points",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(end = 4.dp),
                            )
                            Text(
                                text = "${customer.loyaltyPoints}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Total Spent",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = customer.totalSpent.formatAsCurrency(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notes
        if (!customer.notes.isNullOrBlank()) {
            SkylaCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "Notes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = customer.notes.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Timestamps
        SkylaCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = "Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(12.dp))

                DetailRow(label = "Customer ID", value = customer.id)
                DetailRow(label = "Created", value = customer.createdAt.toReadableDateTime())
                DetailRow(label = "Updated", value = customer.updatedAt.toReadableDateTime())
            }
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
