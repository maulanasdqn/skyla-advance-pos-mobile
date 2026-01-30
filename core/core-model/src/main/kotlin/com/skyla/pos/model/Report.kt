package com.skyla.pos.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DailySalesReport(
    val date: String,
    @SerialName("total_sales") val totalSales: Long,
    @SerialName("total_transactions") val totalTransactions: Int,
    @SerialName("total_discount") val totalDiscount: Long,
    @SerialName("total_tax") val totalTax: Long,
    @SerialName("net_sales") val netSales: Long
)

@Serializable
data class TopProductReport(
    @SerialName("product_id") val productId: String,
    @SerialName("product_name") val productName: String,
    @SerialName("product_sku") val productSku: String,
    @SerialName("total_quantity") val totalQuantity: Int,
    @SerialName("total_revenue") val totalRevenue: Long
)

@Serializable
data class InventoryReport(
    @SerialName("product_id") val productId: String,
    @SerialName("product_name") val productName: String,
    @SerialName("product_sku") val productSku: String,
    @SerialName("current_stock") val currentStock: Int,
    @SerialName("reorder_level") val reorderLevel: Int,
    @SerialName("stock_value") val stockValue: Long,
    @SerialName("is_low_stock") val isLowStock: Boolean
)

@Serializable
data class CashierPerformanceReport(
    @SerialName("cashier_id") val cashierId: String,
    @SerialName("cashier_name") val cashierName: String,
    @SerialName("total_sales") val totalSales: Long,
    @SerialName("total_transactions") val totalTransactions: Int,
    @SerialName("average_sale") val averageSale: Long
)

@Serializable
data class PaymentMethodReport(
    @SerialName("payment_method") val paymentMethod: PaymentMethod,
    @SerialName("total_amount") val totalAmount: Long,
    @SerialName("transaction_count") val transactionCount: Int,
    val percentage: Double
)
