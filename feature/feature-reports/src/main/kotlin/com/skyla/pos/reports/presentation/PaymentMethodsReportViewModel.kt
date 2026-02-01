package com.skyla.pos.reports.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyla.pos.common.Resource
import com.skyla.pos.model.PaymentMethodReport
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

data class PaymentMethodsReportUiState(
    val isLoading: Boolean = false,
    val paymentMethods: List<PaymentMethodReport> = emptyList(),
    val errorMessage: String? = null,
    val totalAmount: Long = 0L,
    val totalTransactions: Int = 0,
)

@HiltViewModel
class PaymentMethodsReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentMethodsReportUiState())
    val uiState: StateFlow<PaymentMethodsReportUiState> = _uiState.asStateFlow()

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

            when (val result = reportRepository.getPaymentMethodsReport(startDate, endDate)) {
                is Resource.Success -> {
                    val methods = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            paymentMethods = methods,
                            totalAmount = methods.sumOf { method -> method.totalAmount },
                            totalTransactions = methods.sumOf { method -> method.transactionCount },
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
