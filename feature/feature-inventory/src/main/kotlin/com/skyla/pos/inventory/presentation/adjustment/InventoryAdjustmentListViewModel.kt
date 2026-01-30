package com.skyla.pos.inventory.presentation.adjustment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyla.pos.common.Constants
import com.skyla.pos.common.Resource
import com.skyla.pos.inventory.domain.repository.InventoryRepository
import com.skyla.pos.model.InventoryAdjustment
import com.skyla.pos.model.PaginationMeta
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdjustmentListUiState(
    val adjustments: List<InventoryAdjustment> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val paginationMeta: PaginationMeta? = null,
    val currentPage: Int = Constants.FIRST_PAGE,
    val hasMore: Boolean = false,
)

@HiltViewModel
class InventoryAdjustmentListViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdjustmentListUiState())
    val uiState: StateFlow<AdjustmentListUiState> = _uiState.asStateFlow()

    init {
        loadAdjustments()
    }

    fun loadAdjustments() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null, currentPage = Constants.FIRST_PAGE)
            }

            val result = inventoryRepository.getAdjustments(
                page = Constants.FIRST_PAGE,
                perPage = Constants.DEFAULT_PAGE_SIZE,
            )

            when (result) {
                is Resource.Success -> {
                    val (adjustments, meta) = result.data
                    _uiState.update {
                        it.copy(
                            adjustments = adjustments,
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

    fun loadMoreAdjustments() {
        val state = _uiState.value
        if (state.isLoadingMore || !state.hasMore) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }

            val nextPage = state.currentPage + 1
            val result = inventoryRepository.getAdjustments(
                page = nextPage,
                perPage = Constants.DEFAULT_PAGE_SIZE,
            )

            when (result) {
                is Resource.Success -> {
                    val (newAdjustments, meta) = result.data
                    _uiState.update {
                        it.copy(
                            adjustments = it.adjustments + newAdjustments,
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
}
