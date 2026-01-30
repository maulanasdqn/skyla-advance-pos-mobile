package com.skyla.pos.inventory.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyla.pos.common.Resource
import com.skyla.pos.inventory.domain.repository.InventoryRepository
import com.skyla.pos.model.StockLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InventoryListUiState(
    val lowStockProducts: List<StockLevel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedTab: InventoryTab = InventoryTab.LOW_STOCK,
)

enum class InventoryTab(val label: String) {
    LOW_STOCK("Low Stock"),
    STOCK_LEVELS("Stock Levels"),
}

@HiltViewModel
class InventoryListViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryListUiState())
    val uiState: StateFlow<InventoryListUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        when (_uiState.value.selectedTab) {
            InventoryTab.LOW_STOCK -> loadLowStockProducts()
            InventoryTab.STOCK_LEVELS -> loadLowStockProducts() // Uses same endpoint; stock levels filtered differently on server
        }
    }

    fun onTabSelected(tab: InventoryTab) {
        if (_uiState.value.selectedTab == tab) return
        _uiState.update { it.copy(selectedTab = tab) }
        loadData()
    }

    private fun loadLowStockProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = inventoryRepository.getLowStockProducts()) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            lowStockProducts = result.data,
                            isLoading = false,
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
}
