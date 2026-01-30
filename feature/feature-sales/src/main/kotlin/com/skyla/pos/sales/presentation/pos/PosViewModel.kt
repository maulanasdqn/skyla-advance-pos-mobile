package com.skyla.pos.sales.presentation.pos

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyla.pos.common.Constants
import com.skyla.pos.common.Resource
import com.skyla.pos.model.Product
import com.skyla.pos.model.Sale
import com.skyla.pos.model.SaleItem
import com.skyla.pos.products.domain.repository.ProductRepository
import com.skyla.pos.sales.domain.repository.SaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PosUiState(
    val sale: Sale? = null,
    val isLoading: Boolean = false,
    val isProcessing: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val searchResults: List<Product> = emptyList(),
    val isSearching: Boolean = false,
    val showDiscountDialog: Boolean = false,
    val discountInput: String = "",
)

sealed interface PosEvent {
    data class NavigateToPayment(val saleId: String) : PosEvent
    data class ShowError(val message: String) : PosEvent
    data object SaleCreated : PosEvent
}

@HiltViewModel
class PosViewModel @Inject constructor(
    private val saleRepository: SaleRepository,
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PosUiState())
    val uiState: StateFlow<PosUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PosEvent>()
    val events: SharedFlow<PosEvent> = _events.asSharedFlow()

    private var searchJob: Job? = null

    private val saleId: String? = savedStateHandle["saleId"]

    init {
        if (saleId != null) {
            loadSale(saleId)
        } else {
            createNewSale()
        }
    }

    fun createNewSale(customerId: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = saleRepository.createSale(customerId)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(sale = result.data, isLoading = false)
                    }
                    _events.emit(PosEvent.SaleCreated)
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                    _events.emit(PosEvent.ShowError(result.message))
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun loadSale(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = saleRepository.getSale(id)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(sale = result.data, isLoading = false)
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(300L)
            _uiState.update { it.copy(isSearching = true) }

            when (val result = productRepository.getProducts(
                page = Constants.FIRST_PAGE,
                perPage = Constants.DEFAULT_PAGE_SIZE,
                search = query,
            )) {
                is Resource.Success -> {
                    val (products, _) = result.data
                    _uiState.update {
                        it.copy(searchResults = products, isSearching = false)
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isSearching = false) }
                    _events.emit(PosEvent.ShowError(result.message))
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun addItem(product: Product) {
        val currentSale = _uiState.value.sale ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }

            when (val result = saleRepository.addSaleItem(
                saleId = currentSale.id,
                productId = product.id,
                quantity = 1,
                discountAmount = null,
            )) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            sale = result.data,
                            isProcessing = false,
                            searchQuery = "",
                            searchResults = emptyList(),
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isProcessing = false) }
                    _events.emit(PosEvent.ShowError(result.message))
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun updateItemQuantity(item: SaleItem, newQuantity: Int) {
        val currentSale = _uiState.value.sale ?: return
        if (newQuantity < 1) return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }

            when (val result = saleRepository.updateSaleItem(
                saleId = currentSale.id,
                itemId = item.id,
                quantity = newQuantity,
                discountAmount = null,
            )) {
                is Resource.Success -> {
                    _uiState.update { it.copy(sale = result.data, isProcessing = false) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isProcessing = false) }
                    _events.emit(PosEvent.ShowError(result.message))
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun removeItem(item: SaleItem) {
        val currentSale = _uiState.value.sale ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }

            when (val result = saleRepository.removeSaleItem(
                saleId = currentSale.id,
                itemId = item.id,
            )) {
                is Resource.Success -> {
                    _uiState.update { it.copy(sale = result.data, isProcessing = false) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isProcessing = false) }
                    _events.emit(PosEvent.ShowError(result.message))
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun showDiscountDialog() {
        _uiState.update { it.copy(showDiscountDialog = true, discountInput = "") }
    }

    fun dismissDiscountDialog() {
        _uiState.update { it.copy(showDiscountDialog = false, discountInput = "") }
    }

    fun onDiscountInputChanged(input: String) {
        _uiState.update { it.copy(discountInput = input) }
    }

    fun applyDiscount() {
        val currentSale = _uiState.value.sale ?: return
        val discountCents = try {
            val amount = _uiState.value.discountInput.toDouble()
            Math.round(amount * 100)
        } catch (e: NumberFormatException) {
            viewModelScope.launch { _events.emit(PosEvent.ShowError("Invalid discount amount")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, showDiscountDialog = false) }

            when (val result = saleRepository.applyDiscount(
                saleId = currentSale.id,
                discountAmount = discountCents,
            )) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(sale = result.data, isProcessing = false, discountInput = "")
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isProcessing = false) }
                    _events.emit(PosEvent.ShowError(result.message))
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun navigateToPayment() {
        val currentSale = _uiState.value.sale ?: return
        if (currentSale.items.isEmpty()) {
            viewModelScope.launch {
                _events.emit(PosEvent.ShowError("Add at least one item before proceeding to payment"))
            }
            return
        }
        viewModelScope.launch {
            _events.emit(PosEvent.NavigateToPayment(currentSale.id))
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
