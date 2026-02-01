package com.skyla.pos.reports.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyla.pos.common.Resource
import com.skyla.pos.model.DailySalesReport
import com.skyla.pos.reports.domain.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class DailySalesReportUiState(
    val isLoading: Boolean = false,
    val reports: List<DailySalesReport> = emptyList(),
    val errorMessage: String? = null,
    val totalSales: Long = 0L,
    val totalTransactions: Int = 0,
    val totalNetSales: Long = 0L,
)

@HiltViewModel
class DailySalesReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailySalesReportUiState())
    val uiState: StateFlow<DailySalesReportUiState> = _uiState.asStateFlow()

    init {
        loadReport()
    }

    fun loadReport() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val today = LocalDate.now()
            val sevenDaysAgo = today.minusDays(6)
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val startDate = sevenDaysAgo.format(formatter)
            val endDate = today.format(formatter)

            when (val result = reportRepository.getDailySalesReport(startDate, endDate)) {
                is Resource.Success -> {
                    val reports = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            reports = reports,
                            totalSales = reports.sumOf { report -> report.totalSales },
                            totalTransactions = reports.sumOf { report -> report.totalTransactions },
                            totalNetSales = reports.sumOf { report -> report.netSales },
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
