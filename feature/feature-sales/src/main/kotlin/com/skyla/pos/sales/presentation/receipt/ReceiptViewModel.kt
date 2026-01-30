package com.skyla.pos.sales.presentation.receipt

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyla.pos.common.Resource
import com.skyla.pos.sales.data.dto.ReceiptResponse
import com.skyla.pos.sales.domain.repository.SaleRepository
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

data class ReceiptUiState(
    val receipt: ReceiptResponse? = null,
    val isLoading: Boolean = false,
    val isVoiding: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface ReceiptEvent {
    data class ShowError(val message: String) : ReceiptEvent
    data object SaleVoided : ReceiptEvent
}

@HiltViewModel
class ReceiptViewModel @Inject constructor(
    private val saleRepository: SaleRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReceiptUiState())
    val uiState: StateFlow<ReceiptUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ReceiptEvent>()
    val events: SharedFlow<ReceiptEvent> = _events.asSharedFlow()

    private val saleId: String = checkNotNull(savedStateHandle["saleId"])

    init {
        loadReceipt()
    }

    fun loadReceipt() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = saleRepository.getReceipt(saleId)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(receipt = result.data, isLoading = false)
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

    fun voidSale(reason: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isVoiding = true) }

            when (val result = saleRepository.voidSale(saleId, reason)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isVoiding = false) }
                    _events.emit(ReceiptEvent.SaleVoided)
                    // Reload receipt to reflect voided status
                    loadReceipt()
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isVoiding = false) }
                    _events.emit(ReceiptEvent.ShowError(result.message))
                }
                is Resource.Loading -> Unit
            }
        }
    }
}
