package com.skyla.pos.reports.data.api

import com.skyla.pos.model.CashierPerformanceReport
import com.skyla.pos.model.DailySalesReport
import com.skyla.pos.model.InventoryReport
import com.skyla.pos.model.PaymentMethodReport
import com.skyla.pos.model.TopProductReport
import com.skyla.pos.network.dto.ListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ReportApiService {

    @GET("reports/daily-sales")
    suspend fun getDailySalesReport(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
    ): Response<ListResponse<DailySalesReport>>

    @GET("reports/top-products")
    suspend fun getTopProductsReport(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("limit") limit: Int = 10,
    ): Response<ListResponse<TopProductReport>>

    @GET("reports/inventory")
    suspend fun getInventoryReport(): Response<ListResponse<InventoryReport>>

    @GET("reports/cashier-performance")
    suspend fun getCashierPerformanceReport(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
    ): Response<ListResponse<CashierPerformanceReport>>

    @GET("reports/payment-methods")
    suspend fun getPaymentMethodsReport(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
    ): Response<ListResponse<PaymentMethodReport>>
}
