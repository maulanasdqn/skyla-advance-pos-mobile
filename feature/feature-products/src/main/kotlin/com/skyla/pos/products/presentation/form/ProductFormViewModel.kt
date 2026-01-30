package com.skyla.pos.products.presentation.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyla.pos.common.Resource
import com.skyla.pos.common.parseToCents
import com.skyla.pos.common.toDollarAmount
import com.skyla.pos.products.data.dto.CreateProductRequest
import com.skyla.pos.products.data.dto.UpdateProductRequest
import com.skyla.pos.products.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductFormUiState(
    val sku: String = "",
    val barcode: String = "",
    val name: String = "",
    val description: String = "",
    val categoryId: String = "",
    val price: String = "",
    val costPrice: String = "",
    val currentStock: String = "",
    val reorderLevel: String = "",
    val imageUrl: String = "",
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val isFormLoading: Boolean = false,
    val skuError: String? = null,
    val nameError: String? = null,
    val priceError: String? = null,
    val costPriceError: String? = null,
    val currentStockError: String? = null,
    val reorderLevelError: String? = null,
)

sealed interface ProductFormEvent {
    data object ProductSaved : ProductFormEvent
    data class ShowError(val message: String) : ProductFormEvent
}

@HiltViewModel
class ProductFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
) : ViewModel() {

    private val productId: String? = savedStateHandle["productId"]

    private val _uiState = MutableStateFlow(ProductFormUiState(isEditMode = productId != null))
    val uiState: StateFlow<ProductFormUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProductFormEvent>()
    val events: SharedFlow<ProductFormEvent> = _events.asSharedFlow()

    init {
        if (productId != null) {
            loadProduct(productId)
        }
    }

    private fun loadProduct(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isFormLoading = true) }
            when (val result = productRepository.getProduct(id)) {
                is Resource.Success -> {
                    val product = result.data
                    _uiState.update {
                        it.copy(
                            sku = product.sku,
                            barcode = product.barcode ?: "",
                            name = product.name,
                            description = product.description ?: "",
                            categoryId = product.categoryId ?: "",
                            price = product.price.toDollarAmount().toString(),
                            costPrice = product.costPrice.toDollarAmount().toString(),
                            currentStock = product.currentStock.toString(),
                            reorderLevel = product.reorderLevel.toString(),
                            imageUrl = product.imageUrl ?: "",
                            isFormLoading = false,
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isFormLoading = false) }
                    _events.emit(ProductFormEvent.ShowError(result.message))
                }
                is Resource.Loading -> { /* handled by state update above */ }
            }
        }
    }

    fun onSkuChanged(value: String) {
        _uiState.update { it.copy(sku = value, skuError = null) }
    }

    fun onBarcodeChanged(value: String) {
        _uiState.update { it.copy(barcode = value) }
    }

    fun onNameChanged(value: String) {
        _uiState.update { it.copy(name = value, nameError = null) }
    }

    fun onDescriptionChanged(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onCategoryIdChanged(value: String) {
        _uiState.update { it.copy(categoryId = value) }
    }

    fun onPriceChanged(value: String) {
        _uiState.update { it.copy(price = value, priceError = null) }
    }

    fun onCostPriceChanged(value: String) {
        _uiState.update { it.copy(costPrice = value, costPriceError = null) }
    }

    fun onCurrentStockChanged(value: String) {
        _uiState.update { it.copy(currentStock = value, currentStockError = null) }
    }

    fun onReorderLevelChanged(value: String) {
        _uiState.update { it.copy(reorderLevel = value, reorderLevelError = null) }
    }

    fun onImageUrlChanged(value: String) {
        _uiState.update { it.copy(imageUrl = value) }
    }

    fun saveProduct() {
        if (!validateForm()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val state = _uiState.value
            val result = if (productId != null) {
                productRepository.updateProduct(
                    id = productId,
                    request = UpdateProductRequest(
                        sku = state.sku,
                        barcode = state.barcode.ifBlank { null },
                        name = state.name,
                        description = state.description.ifBlank { null },
                        categoryId = state.categoryId.ifBlank { null },
                        price = state.price.parseToCents(),
                        costPrice = state.costPrice.parseToCents(),
                        reorderLevel = state.reorderLevel.toIntOrNull(),
                        imageUrl = state.imageUrl.ifBlank { null },
                    ),
                )
            } else {
                productRepository.createProduct(
                    request = CreateProductRequest(
                        sku = state.sku,
                        barcode = state.barcode.ifBlank { null },
                        name = state.name,
                        description = state.description.ifBlank { null },
                        categoryId = state.categoryId.ifBlank { null },
                        price = state.price.parseToCents(),
                        costPrice = state.costPrice.parseToCents(),
                        currentStock = state.currentStock.toIntOrNull() ?: 0,
                        reorderLevel = state.reorderLevel.toIntOrNull() ?: 0,
                        imageUrl = state.imageUrl.ifBlank { null },
                    ),
                )
            }

            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(ProductFormEvent.ProductSaved)
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(ProductFormEvent.ShowError(result.message))
                }
                is Resource.Loading -> { /* handled by state update above */ }
            }
        }
    }

    private fun validateForm(): Boolean {
        val state = _uiState.value
        var isValid = true

        if (state.sku.isBlank()) {
            _uiState.update { it.copy(skuError = "SKU is required") }
            isValid = false
        }

        if (state.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Product name is required") }
            isValid = false
        }

        if (state.price.isBlank()) {
            _uiState.update { it.copy(priceError = "Price is required") }
            isValid = false
        } else {
            try {
                state.price.parseToCents()
            } catch (e: NumberFormatException) {
                _uiState.update { it.copy(priceError = "Invalid price format") }
                isValid = false
            }
        }

        if (state.costPrice.isBlank()) {
            _uiState.update { it.copy(costPriceError = "Cost price is required") }
            isValid = false
        } else {
            try {
                state.costPrice.parseToCents()
            } catch (e: NumberFormatException) {
                _uiState.update { it.copy(costPriceError = "Invalid cost price format") }
                isValid = false
            }
        }

        if (!state.isEditMode) {
            if (state.currentStock.isBlank()) {
                _uiState.update { it.copy(currentStockError = "Current stock is required") }
                isValid = false
            } else if (state.currentStock.toIntOrNull() == null) {
                _uiState.update { it.copy(currentStockError = "Invalid stock quantity") }
                isValid = false
            }
        }

        if (state.reorderLevel.isNotBlank() && state.reorderLevel.toIntOrNull() == null) {
            _uiState.update { it.copy(reorderLevelError = "Invalid reorder level") }
            isValid = false
        }

        return isValid
    }
}
