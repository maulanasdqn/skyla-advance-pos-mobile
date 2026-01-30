package com.skyla.pos.customers.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyla.pos.common.Resource
import com.skyla.pos.common.UiState
import com.skyla.pos.customers.domain.repository.CustomerRepository
import com.skyla.pos.model.Customer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CustomerDetailEvent {
    data object CustomerDeleted : CustomerDetailEvent
    data class ShowError(val message: String) : CustomerDetailEvent
}

@HiltViewModel
class CustomerDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val customerRepository: CustomerRepository,
) : ViewModel() {

    private val customerId: String = checkNotNull(savedStateHandle["customerId"])

    private val _uiState = MutableStateFlow<UiState<Customer>>(UiState.Loading)
    val uiState: StateFlow<UiState<Customer>> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CustomerDetailEvent>()
    val events: SharedFlow<CustomerDetailEvent> = _events.asSharedFlow()

    private val _isActionLoading = MutableStateFlow(false)
    val isActionLoading: StateFlow<Boolean> = _isActionLoading.asStateFlow()

    init {
        loadCustomer()
    }

    fun loadCustomer() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = customerRepository.getCustomer(customerId)) {
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

    fun deleteCustomer() {
        viewModelScope.launch {
            _isActionLoading.value = true
            when (val result = customerRepository.deleteCustomer(customerId)) {
                is Resource.Success -> {
                    _isActionLoading.value = false
                    _events.emit(CustomerDetailEvent.CustomerDeleted)
                }
                is Resource.Error -> {
                    _isActionLoading.value = false
                    _events.emit(CustomerDetailEvent.ShowError(result.message))
                }
                is Resource.Loading -> { /* handled by state update above */ }
            }
        }
    }
}
