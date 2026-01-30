package com.skyla.pos.inventory.presentation.adjustment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyla.pos.common.Resource
import com.skyla.pos.inventory.domain.repository.InventoryRepository
import com.skyla.pos.model.AdjustmentReason
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

data class AdjustmentFormUiState(
    val productId: String = "",
    val productQuery: String = "",
    val quantityChange: String = "",
    val selectedReason: AdjustmentReason = AdjustmentReason.PURCHASE,
    val notes: String = "",
    val adjustedBy: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val productIdError: String? = null,
    val quantityError: String? = null,
)

sealed interface AdjustmentFormEvent {
    data object AdjustmentCreated : AdjustmentFormEvent
    data class ShowError(val message: String) : AdjustmentFormEvent
}

@HiltViewModel
class InventoryAdjustmentFormViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdjustmentFormUiState())
    val uiState: StateFlow<AdjustmentFormUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AdjustmentFormEvent>()
    val events: SharedFlow<AdjustmentFormEvent> = _events.asSharedFlow()

    fun onProductQueryChanged(query: String) {
        _uiState.update { it.copy(productQuery = query, productId = query, productIdError = null) }
    }

    fun onProductSelected(productId: String, productName: String) {
        _uiState.update {
            it.copy(
                productId = productId,
                productQuery = productName,
                productIdError = null,
            )
        }
    }

    fun onQuantityChanged(quantity: String) {
        _uiState.update { it.copy(quantityChange = quantity, quantityError = null) }
    }

    fun onReasonSelected(reason: AdjustmentReason) {
        _uiState.update { it.copy(selectedReason = reason) }
    }

    fun onNotesChanged(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun onAdjustedByChanged(adjustedBy: String) {
        _uiState.update { it.copy(adjustedBy = adjustedBy) }
    }

    fun submit() {
        val state = _uiState.value

        // Validation
        var hasError = false

        if (state.productId.isBlank()) {
            _uiState.update { it.copy(productIdError = "Product is required") }
            hasError = true
        }

        val quantityChange = state.quantityChange.toIntOrNull()
        if (quantityChange == null || quantityChange == 0) {
            _uiState.update { it.copy(quantityError = "Valid quantity change is required (non-zero integer)") }
            hasError = true
        }

        if (hasError) return

        val reasonString = when (state.selectedReason) {
            AdjustmentReason.PURCHASE -> "purchase"
            AdjustmentReason.SALE -> "sale"
            AdjustmentReason.RETURN -> "return"
            AdjustmentReason.DAMAGE -> "damage"
            AdjustmentReason.CORRECTION -> "correction"
            AdjustmentReason.INITIAL -> "initial"
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            when (val result = inventoryRepository.createAdjustment(
                productId = state.productId,
                quantityChange = quantityChange!!,
                reason = reasonString,
                referenceId = null,
                notes = state.notes.takeIf { it.isNotBlank() },
                adjustedBy = state.adjustedBy.ifBlank { "system" },
            )) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isSubmitting = false) }
                    _events.emit(AdjustmentFormEvent.AdjustmentCreated)
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(isSubmitting = false, errorMessage = result.message)
                    }
                    _events.emit(AdjustmentFormEvent.ShowError(result.message))
                }
                is Resource.Loading -> Unit
            }
        }
    }
}
