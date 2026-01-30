package com.skyla.pos.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sale(
    val id: String,
    @SerialName("sale_number") val saleNumber: String,
    @SerialName("cashier_id") val cashierId: String,
    @SerialName("customer_id") val customerId: String? = null,
    val status: SaleStatus,
    val subtotal: Long,
    @SerialName("discount_amount") val discountAmount: Long,
    @SerialName("tax_amount") val taxAmount: Long,
    @SerialName("total_amount") val totalAmount: Long,
    val notes: String? = null,
    @SerialName("voided_at") val voidedAt: String? = null,
    @SerialName("voided_by") val voidedBy: String? = null,
    @SerialName("void_reason") val voidReason: String? = null,
    @SerialName("completed_at") val completedAt: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    val items: List<SaleItem> = emptyList()
)

@Serializable
enum class SaleStatus {
    @SerialName("draft") DRAFT,
    @SerialName("completed") COMPLETED,
    @SerialName("voided") VOIDED
}
