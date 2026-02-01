package com.skyla.pos.dashboard.data.dto

import com.skyla.pos.model.Sale
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DashboardSummaryDto(
    @SerialName("today_sales") val todaySales: Long,
    @SerialName("today_transactions") val todayTransactions: Int,
    @SerialName("today_revenue") val todayRevenue: Long,
    @SerialName("low_stock_count") val lowStockCount: Int,
    @SerialName("recent_sales") val recentSales: List<Sale> = emptyList(),
)
