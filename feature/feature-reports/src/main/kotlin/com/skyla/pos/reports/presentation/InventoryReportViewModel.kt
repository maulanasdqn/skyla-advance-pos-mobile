package com.skyla.pos.reports.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyla.pos.common.Resource
import com.skyla.pos.model.InventoryReport
import com.skyla.pos.reports.domain.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InventoryReportUiState(
    val isLoading: Boolean = false,
    val items: List<InventoryReport> = emptyList(),
    val errorMessage: String? = null,
    val lowStockCount: Int = 0,
)

@HiltViewModel
class InventoryReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryReportUiState())
    val uiState: StateFlow<InventoryReportUiState> = _uiState.asStateFlow()

    init {
        loadReport()
    }

    fun loadReport() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = reportRepository.getInventoryReport()) {
                is Resource.Success -> {
                    val items = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            items = items,
                            lowStockCount = items.count { item -> item.isLowStock },
                            errorMessage = null,
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message,
                        )
                    }
                }
                is Resource.Loading -> { /* handled by state update above */ }
            }
        }
    }
}
