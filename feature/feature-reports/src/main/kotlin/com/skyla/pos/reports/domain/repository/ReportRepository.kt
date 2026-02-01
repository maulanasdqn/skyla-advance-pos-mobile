package com.skyla.pos.reports.domain.repository

import com.skyla.pos.common.Resource
import com.skyla.pos.model.CashierPerformanceReport
import com.skyla.pos.model.DailySalesReport
import com.skyla.pos.model.InventoryReport
import com.skyla.pos.model.PaymentMethodReport
import com.skyla.pos.model.TopProductReport

interface ReportRepository {

    suspend fun getDailySalesReport(
        startDate: String,
        endDate: String,
    ): Resource<List<DailySalesReport>>

    suspend fun getTopProductsReport(
        startDate: String,
        endDate: String,
        limit: Int = 10,
    ): Resource<List<TopProductReport>>

    suspend fun getInventoryReport(): Resource<List<InventoryReport>>

    suspend fun getCashierPerformanceReport(
        startDate: String,
        endDate: String,
    ): Resource<List<CashierPerformanceReport>>

    suspend fun getPaymentMethodsReport(
        startDate: String,
        endDate: String,
    ): Resource<List<PaymentMethodReport>>
}
