package com.skyla.pos.reports.data.repository

import com.skyla.pos.common.Resource
import com.skyla.pos.model.CashierPerformanceReport
import com.skyla.pos.model.DailySalesReport
import com.skyla.pos.model.InventoryReport
import com.skyla.pos.model.PaymentMethodReport
import com.skyla.pos.model.TopProductReport
import com.skyla.pos.network.safeApiCall
import com.skyla.pos.reports.data.api.ReportApiService
import com.skyla.pos.reports.domain.repository.ReportRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepositoryImpl @Inject constructor(
    private val apiService: ReportApiService,
) : ReportRepository {

    override suspend fun getDailySalesReport(
        startDate: String,
        endDate: String,
    ): Resource<List<DailySalesReport>> {
        return when (val result = safeApiCall { apiService.getDailySalesReport(startDate, endDate) }) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getTopProductsReport(
        startDate: String,
        endDate: String,
        limit: Int,
    ): Resource<List<TopProductReport>> {
        return when (val result = safeApiCall { apiService.getTopProductsReport(startDate, endDate, limit) }) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getInventoryReport(): Resource<List<InventoryReport>> {
        return when (val result = safeApiCall { apiService.getInventoryReport() }) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getCashierPerformanceReport(
        startDate: String,
        endDate: String,
    ): Resource<List<CashierPerformanceReport>> {
        return when (val result = safeApiCall { apiService.getCashierPerformanceReport(startDate, endDate) }) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getPaymentMethodsReport(
        startDate: String,
        endDate: String,
    ): Resource<List<PaymentMethodReport>> {
        return when (val result = safeApiCall { apiService.getPaymentMethodsReport(startDate, endDate) }) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }
}
