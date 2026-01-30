package com.skyla.pos.sales.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyla.pos.common.Constants
import com.skyla.pos.common.Resource
import com.skyla.pos.model.PaginationMeta
import com.skyla.pos.model.Sale
import com.skyla.pos.model.SaleStatus
import com.skyla.pos.sales.domain.repository.SaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SalesListUiState(
    val sales: List<Sale> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val selectedFilter: SaleFilterOption = SaleFilterOption.ALL,
    val paginationMeta: PaginationMeta? = null,
    val currentPage: Int = Constants.FIRST_PAGE,
    val hasMore: Boolean = false,
)

enum class SaleFilterOption(val label: String, val apiValue: String?) {
    ALL("All", null),
    DRAFT("Draft", "draft"),
    COMPLETED("Completed", "completed"),
    VOIDED("Voided", "voided"),
}

@HiltViewModel
class SalesListViewModel @Inject constructor(
    private val saleRepository: SaleRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SalesListUiState())
    val uiState: StateFlow<SalesListUiState> = _uiState.asStateFlow()

    init {
        loadSales()
    }

    fun loadSales() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, currentPage = Constants.FIRST_PAGE) }

            val result = saleRepository.getSales(
                page = Constants.FIRST_PAGE,
                perPage = Constants.DEFAULT_PAGE_SIZE,
                status = _uiState.value.selectedFilter.apiValue,
            )

            when (result) {
                is Resource.Success -> {
                    val (sales, meta) = result.data
                    _uiState.update {
                        it.copy(
                            sales = sales,
                            isLoading = false,
                            paginationMeta = meta,
                            currentPage = meta.currentPage,
                            hasMore = meta.currentPage < meta.totalPages,
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

    fun loadMoreSales() {
        val state = _uiState.value
        if (state.isLoadingMore || !state.hasMore) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }

            val nextPage = state.currentPage + 1
            val result = saleRepository.getSales(
                page = nextPage,
                perPage = Constants.DEFAULT_PAGE_SIZE,
                status = state.selectedFilter.apiValue,
            )

            when (result) {
                is Resource.Success -> {
                    val (newSales, meta) = result.data
                    _uiState.update {
                        it.copy(
                            sales = it.sales + newSales,
                            isLoadingMore = false,
                            paginationMeta = meta,
                            currentPage = meta.currentPage,
                            hasMore = meta.currentPage < meta.totalPages,
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoadingMore = false) }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun onFilterSelected(filter: SaleFilterOption) {
        if (_uiState.value.selectedFilter == filter) return
        _uiState.update { it.copy(selectedFilter = filter) }
        loadSales()
    }
}
