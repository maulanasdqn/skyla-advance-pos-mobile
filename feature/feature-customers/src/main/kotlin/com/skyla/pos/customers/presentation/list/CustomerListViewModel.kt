package com.skyla.pos.customers.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyla.pos.common.Constants
import com.skyla.pos.common.Resource
import com.skyla.pos.customers.domain.repository.CustomerRepository
import com.skyla.pos.model.Customer
import com.skyla.pos.model.PaginationMeta
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomerListUiState(
    val customers: List<Customer> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val currentPage: Int = Constants.FIRST_PAGE,
    val hasMorePages: Boolean = false,
    val paginationMeta: PaginationMeta? = null,
)

@OptIn(FlowPreview::class)
@HiltViewModel
class CustomerListViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomerListUiState())
    val uiState: StateFlow<CustomerListUiState> = _uiState.asStateFlow()

    private val searchQueryFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)

    init {
        searchQueryFlow
            .debounce(300L)
            .distinctUntilChanged()
            .onEach { query ->
                _uiState.update { it.copy(searchQuery = query, currentPage = Constants.FIRST_PAGE) }
                loadCustomers(isRefresh = true)
            }
            .launchIn(viewModelScope)

        loadCustomers(isRefresh = true)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchQueryFlow.tryEmit(query)
    }

    fun loadCustomers(isRefresh: Boolean = false) {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.isLoadingMore) return

        val page = if (isRefresh) Constants.FIRST_PAGE else currentState.currentPage + 1

        viewModelScope.launch {
            _uiState.update {
                if (isRefresh) {
                    it.copy(isLoading = true, error = null)
                } else {
                    it.copy(isLoadingMore = true, error = null)
                }
            }

            val search = _uiState.value.searchQuery.ifBlank { null }
            when (val result = customerRepository.getCustomers(page, Constants.DEFAULT_PAGE_SIZE, search)) {
                is Resource.Success -> {
                    val (customers, meta) = result.data
                    _uiState.update { state ->
                        val updatedCustomers = if (isRefresh) customers else state.customers + customers
                        state.copy(
                            customers = updatedCustomers,
                            isLoading = false,
                            isLoadingMore = false,
                            currentPage = meta.currentPage,
                            hasMorePages = meta.currentPage < meta.totalPages,
                            paginationMeta = meta,
                            error = null,
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            error = result.message,
                        )
                    }
                }
                is Resource.Loading -> { /* handled by state update above */ }
            }
        }
    }

    fun loadMoreCustomers() {
        if (_uiState.value.hasMorePages) {
            loadCustomers(isRefresh = false)
        }
    }

    fun refresh() {
        loadCustomers(isRefresh = true)
    }
}
