package com.skyla.pos.customers.presentation.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyla.pos.common.Resource
import com.skyla.pos.common.isValidEmail
import com.skyla.pos.customers.data.dto.CreateCustomerRequest
import com.skyla.pos.customers.data.dto.UpdateCustomerRequest
import com.skyla.pos.customers.domain.repository.CustomerRepository
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

data class CustomerFormUiState(
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val notes: String = "",
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val isFormLoading: Boolean = false,
    val nameError: String? = null,
    val emailError: String? = null,
)

sealed interface CustomerFormEvent {
    data object CustomerSaved : CustomerFormEvent
    data class ShowError(val message: String) : CustomerFormEvent
}

@HiltViewModel
class CustomerFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val customerRepository: CustomerRepository,
) : ViewModel() {

    private val customerId: String? = savedStateHandle["customerId"]

    private val _uiState = MutableStateFlow(CustomerFormUiState(isEditMode = customerId != null))
    val uiState: StateFlow<CustomerFormUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CustomerFormEvent>()
    val events: SharedFlow<CustomerFormEvent> = _events.asSharedFlow()

    init {
        if (customerId != null) {
            loadCustomer(customerId)
        }
    }

    private fun loadCustomer(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isFormLoading = true) }
            when (val result = customerRepository.getCustomer(id)) {
                is Resource.Success -> {
                    val customer = result.data
                    _uiState.update {
                        it.copy(
                            name = customer.name,
                            phone = customer.phone ?: "",
                            email = customer.email ?: "",
                            notes = customer.notes ?: "",
                            isFormLoading = false,
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isFormLoading = false) }
                    _events.emit(CustomerFormEvent.ShowError(result.message))
                }
                is Resource.Loading -> { /* handled by state update above */ }
            }
        }
    }

    fun onNameChanged(value: String) {
        _uiState.update { it.copy(name = value, nameError = null) }
    }

    fun onPhoneChanged(value: String) {
        _uiState.update { it.copy(phone = value) }
    }

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
    }

    fun onNotesChanged(value: String) {
        _uiState.update { it.copy(notes = value) }
    }

    fun saveCustomer() {
        if (!validateForm()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val state = _uiState.value
            val result = if (customerId != null) {
                customerRepository.updateCustomer(
                    id = customerId,
                    request = UpdateCustomerRequest(
                        name = state.name,
                        phone = state.phone.ifBlank { null },
                        email = state.email.ifBlank { null },
                        notes = state.notes.ifBlank { null },
                    ),
                )
            } else {
                customerRepository.createCustomer(
                    request = CreateCustomerRequest(
                        name = state.name,
                        phone = state.phone.ifBlank { null },
                        email = state.email.ifBlank { null },
                        notes = state.notes.ifBlank { null },
                    ),
                )
            }

            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(CustomerFormEvent.CustomerSaved)
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(CustomerFormEvent.ShowError(result.message))
                }
                is Resource.Loading -> { /* handled by state update above */ }
            }
        }
    }

    private fun validateForm(): Boolean {
        val state = _uiState.value
        var isValid = true

        if (state.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Customer name is required") }
            isValid = false
        }

        if (state.email.isNotBlank() && !state.email.isValidEmail()) {
            _uiState.update { it.copy(emailError = "Invalid email address") }
            isValid = false
        }

        return isValid
    }
}
