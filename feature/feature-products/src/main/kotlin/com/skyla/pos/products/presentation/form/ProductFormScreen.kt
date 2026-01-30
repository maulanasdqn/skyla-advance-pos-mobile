package com.skyla.pos.products.presentation.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skyla.pos.ui.components.SkylaButton
import com.skyla.pos.ui.components.SkylaLoadingScreen
import com.skyla.pos.ui.components.SkylaTextField
import com.skyla.pos.ui.components.SkylaTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    onNavigateBack: () -> Unit,
    onProductSaved: () -> Unit,
    viewModel: ProductFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ProductFormEvent.ProductSaved -> {
                    onProductSaved()
                }
                is ProductFormEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            SkylaTopBar(
                title = if (uiState.isEditMode) "Edit Product" else "New Product",
                onNavigateBack = onNavigateBack,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        if (uiState.isFormLoading) {
            SkylaLoadingScreen(modifier = Modifier.padding(paddingValues))
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Text(
                text = "Product Information",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(12.dp))

            SkylaTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChanged,
                label = "Product Name",
                placeholder = "Enter product name",
                isError = uiState.nameError != null,
                errorMessage = uiState.nameError,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            SkylaTextField(
                value = uiState.sku,
                onValueChange = viewModel::onSkuChanged,
                label = "SKU",
                placeholder = "Enter SKU code",
                isError = uiState.skuError != null,
                errorMessage = uiState.skuError,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            SkylaTextField(
                value = uiState.barcode,
                onValueChange = viewModel::onBarcodeChanged,
                label = "Barcode (optional)",
                placeholder = "Enter barcode",
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            SkylaTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChanged,
                label = "Description (optional)",
                placeholder = "Enter product description",
                singleLine = false,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            SkylaTextField(
                value = uiState.categoryId,
                onValueChange = viewModel::onCategoryIdChanged,
                label = "Category ID (optional)",
                placeholder = "Enter category ID",
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Pricing",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(12.dp))

            SkylaTextField(
                value = uiState.price,
                onValueChange = viewModel::onPriceChanged,
                label = "Selling Price",
                placeholder = "0.00",
                keyboardType = KeyboardType.Decimal,
                isError = uiState.priceError != null,
                errorMessage = uiState.priceError,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            SkylaTextField(
                value = uiState.costPrice,
                onValueChange = viewModel::onCostPriceChanged,
                label = "Cost Price",
                placeholder = "0.00",
                keyboardType = KeyboardType.Decimal,
                isError = uiState.costPriceError != null,
                errorMessage = uiState.costPriceError,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Inventory",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (!uiState.isEditMode) {
                SkylaTextField(
                    value = uiState.currentStock,
                    onValueChange = viewModel::onCurrentStockChanged,
                    label = "Current Stock",
                    placeholder = "0",
                    keyboardType = KeyboardType.Number,
                    isError = uiState.currentStockError != null,
                    errorMessage = uiState.currentStockError,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            SkylaTextField(
                value = uiState.reorderLevel,
                onValueChange = viewModel::onReorderLevelChanged,
                label = "Reorder Level",
                placeholder = "0",
                keyboardType = KeyboardType.Number,
                isError = uiState.reorderLevelError != null,
                errorMessage = uiState.reorderLevelError,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            SkylaTextField(
                value = uiState.imageUrl,
                onValueChange = viewModel::onImageUrlChanged,
                label = "Image URL (optional)",
                placeholder = "https://example.com/image.jpg",
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(32.dp))

            SkylaButton(
                text = if (uiState.isEditMode) "Update Product" else "Create Product",
                onClick = viewModel::saveProduct,
                isLoading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
