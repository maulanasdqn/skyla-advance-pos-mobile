package com.skyla.pos.payments.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyla.pos.common.Resource
import com.skyla.pos.model.Payment
import com.skyla.pos.model.PaymentMethod
import com.skyla.pos.model.PaymentSummary
import com.skyla.pos.payments.domain.repository.PaymentRepository
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

data class PaymentUiState(
    val summary: PaymentSummary? = null,
    val isLoading: Boolean = false,
    val isProcessing: Boolean = false,
    val errorMessage: String? = null,
    val selectedMethod: PaymentMethod = PaymentMethod.CASH,
    val amountInput: String = "",
    val referenceNumber: String = "",
    val calculatedChange: Long = 0L,
    val isFullyPaid: Boolean = false,
)

sealed interface PaymentEvent {
    data class ShowError(val message: String) : PaymentEvent
    data object PaymentAdded : PaymentEvent
    data class SaleCompleted(val saleId: String) : PaymentEvent
}

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PaymentEvent>()
    val events: SharedFlow<PaymentEvent> = _events.asSharedFlow()

    val saleId: String = checkNotNull(savedStateHandle["saleId"])

    init {
        loadPaymentSummary()
    }

    fun loadPaymentSummary() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = paymentRepository.getPaymentSummary(saleId)) {
                is Resource.Success -> {
                    val summary = result.data
                    _uiState.update {
                        it.copy(
                            summary = summary,
                            isLoading = false,
                            isFullyPaid = summary.remainingBalance <= 0,
                            amountInput = if (summary.remainingBalance > 0) {
                                String.format("%.2f", summary.remainingBalance / 100.0)
                            } else {
                                ""
                            },
                        )
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

    fun onPaymentMethodSelected(method: PaymentMethod) {
        _uiState.update {
            it.copy(
                selectedMethod = method,
                referenceNumber = "",
                calculatedChange = 0L,
            )
        }
        recalculateChange()
    }

    fun onAmountInputChanged(input: String) {
        _uiState.update { it.copy(amountInput = input) }
        recalculateChange()
    }

    fun onReferenceNumberChanged(input: String) {
        _uiState.update { it.copy(referenceNumber = input) }
    }

    private fun recalculateChange() {
        val state = _uiState.value
        if (state.selectedMethod != PaymentMethod.CASH || state.summary == null) {
            _uiState.update { it.copy(calculatedChange = 0L) }
            return
        }

        val amountCents = try {
            Math.round(state.amountInput.toDouble() * 100)
        } catch (e: NumberFormatException) {
            0L
        }

        val remaining = state.summary.remainingBalance
        val change = if (amountCents > remaining) amountCents - remaining else 0L
        _uiState.update { it.copy(calculatedChange = change) }
    }

    fun addPayment() {
        val state = _uiState.value
        val amountCents = try {
            Math.round(state.amountInput.toDouble() * 100)
        } catch (e: NumberFormatException) {
            viewModelScope.launch { _events.emit(PaymentEvent.ShowError("Invalid amount")) }
            return
        }

        if (amountCents <= 0) {
            viewModelScope.launch { _events.emit(PaymentEvent.ShowError("Amount must be greater than zero")) }
            return
        }

        val methodString = when (state.selectedMethod) {
            PaymentMethod.CASH -> "cash"
            PaymentMethod.CARD -> "card"
            PaymentMethod.E_WALLET -> "e_wallet"
        }

        val reference = state.referenceNumber.takeIf {
            it.isNotBlank() && state.selectedMethod != PaymentMethod.CASH
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }

            when (val result = paymentRepository.addPayment(
                saleId = saleId,
                paymentMethod = methodString,
                amount = amountCents,
                referenceNumber = reference,
            )) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            amountInput = "",
                            referenceNumber = "",
                            calculatedChange = 0L,
                        )
                    }
                    _events.emit(PaymentEvent.PaymentAdded)
                    // Reload summary to reflect updated balance
                    loadPaymentSummary()
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isProcessing = false) }
                    _events.emit(PaymentEvent.ShowError(result.message))
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun completeSale() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }

            when (val result = paymentRepository.completeSale(saleId)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isProcessing = false) }
                    _events.emit(PaymentEvent.SaleCompleted(saleId))
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isProcessing = false) }
                    _events.emit(PaymentEvent.ShowError(result.message))
                }
                is Resource.Loading -> Unit
            }
        }
    }
}
