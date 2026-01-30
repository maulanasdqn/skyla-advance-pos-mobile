package com.skyla.pos.products.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyla.pos.common.Resource
import com.skyla.pos.common.UiState
import com.skyla.pos.model.Product
import com.skyla.pos.products.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProductDetailEvent {
    data object ProductDeleted : ProductDetailEvent
    data object ProductDeactivated : ProductDetailEvent
    data class ShowError(val message: String) : ProductDetailEvent
}

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
) : ViewModel() {

    private val productId: String = checkNotNull(savedStateHandle["productId"])

    private val _uiState = MutableStateFlow<UiState<Product>>(UiState.Loading)
    val uiState: StateFlow<UiState<Product>> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProductDetailEvent>()
    val events: SharedFlow<ProductDetailEvent> = _events.asSharedFlow()

    private val _isActionLoading = MutableStateFlow(false)
    val isActionLoading: StateFlow<Boolean> = _isActionLoading.asStateFlow()

    init {
        loadProduct()
    }

    fun loadProduct() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = productRepository.getProduct(productId)) {
                is Resource.Success -> {
                    _uiState.value = UiState.Success(result.data)
                }
                is Resource.Error -> {
                    _uiState.value = UiState.Error(result.message)
                }
                is Resource.Loading -> { /* handled by state update above */ }
            }
        }
    }

    fun deleteProduct() {
        viewModelScope.launch {
            _isActionLoading.value = true
            when (val result = productRepository.deleteProduct(productId)) {
                is Resource.Success -> {
                    _isActionLoading.value = false
                    _events.emit(ProductDetailEvent.ProductDeleted)
                }
                is Resource.Error -> {
                    _isActionLoading.value = false
                    _events.emit(ProductDetailEvent.ShowError(result.message))
                }
                is Resource.Loading -> { /* handled by state update above */ }
            }
        }
    }

    fun deactivateProduct() {
        viewModelScope.launch {
            _isActionLoading.value = true
            when (val result = productRepository.deactivateProduct(productId)) {
                is Resource.Success -> {
                    _isActionLoading.value = false
                    _events.emit(ProductDetailEvent.ProductDeactivated)
                    loadProduct()
                }
                is Resource.Error -> {
                    _isActionLoading.value = false
                    _events.emit(ProductDetailEvent.ShowError(result.message))
                }
                is Resource.Loading -> { /* handled by state update above */ }
            }
        }
    }
}
