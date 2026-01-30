package com.skyla.pos.inventory.presentation.adjustment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skyla.pos.model.AdjustmentReason
import com.skyla.pos.ui.components.SkylaButton
import com.skyla.pos.ui.components.SkylaLoadingOverlay
import com.skyla.pos.ui.components.SkylaTextField
import com.skyla.pos.ui.components.SkylaTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryAdjustmentFormScreen(
    onNavigateBack: () -> Unit,
    onAdjustmentCreated: () -> Unit,
    viewModel: InventoryAdjustmentFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AdjustmentFormEvent.AdjustmentCreated -> {
                    snackbarHostState.showSnackbar("Adjustment created successfully")
                    onAdjustmentCreated()
                }
                is AdjustmentFormEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            SkylaTopBar(
                title = "New Adjustment",
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Product selector (search field)
                SkylaTextField(
                    value = uiState.productQuery,
                    onValueChange = viewModel::onProductQueryChanged,
                    label = "Product ID or Name",
                    placeholder = "Enter product ID or search by name",
                    isError = uiState.productIdError != null,
                    errorMessage = uiState.productIdError,
                )

                // Quantity change
                SkylaTextField(
                    value = uiState.quantityChange,
                    onValueChange = viewModel::onQuantityChanged,
                    label = "Quantity Change",
                    placeholder = "e.g., 10 or -5",
                    keyboardType = KeyboardType.Number,
                    isError = uiState.quantityError != null,
                    errorMessage = uiState.quantityError,
                )

                // Reason dropdown
                ReasonDropdown(
                    selectedReason = uiState.selectedReason,
                    onReasonSelected = viewModel::onReasonSelected,
                )

                // Notes
                SkylaTextField(
                    value = uiState.notes,
                    onValueChange = viewModel::onNotesChanged,
                    label = "Notes",
                    placeholder = "Optional notes about the adjustment",
                    singleLine = false,
                )

                // Adjusted by
                SkylaTextField(
                    value = uiState.adjustedBy,
                    onValueChange = viewModel::onAdjustedByChanged,
                    label = "Adjusted By",
                    placeholder = "User ID or name",
                )

                // Error message
                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Submit button
                SkylaButton(
                    text = "Create Adjustment",
                    onClick = viewModel::submit,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isSubmitting,
                    isLoading = uiState.isSubmitting,
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (uiState.isSubmitting) {
                SkylaLoadingOverlay()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReasonDropdown(
    selectedReason: AdjustmentReason,
    onReasonSelected: (AdjustmentReason) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    val reasonLabels = mapOf(
        AdjustmentReason.PURCHASE to "Purchase",
        AdjustmentReason.SALE to "Sale",
        AdjustmentReason.RETURN to "Return",
        AdjustmentReason.DAMAGE to "Damage",
        AdjustmentReason.CORRECTION to "Correction",
        AdjustmentReason.INITIAL to "Initial Stock",
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            value = reasonLabels[selectedReason] ?: selectedReason.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Reason") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = MaterialTheme.shapes.small,
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            AdjustmentReason.entries.forEach { reason ->
                DropdownMenuItem(
                    text = { Text(text = reasonLabels[reason] ?: reason.name) },
                    onClick = {
                        onReasonSelected(reason)
                        expanded = false
                    },
                )
            }
        }
    }
}
