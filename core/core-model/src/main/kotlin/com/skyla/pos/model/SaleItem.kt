package com.skyla.pos.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaleItem(
    val id: String,
    @SerialName("sale_id") val saleId: String,
    @SerialName("product_id") val productId: String,
    @SerialName("product_name") val productName: String,
    @SerialName("product_sku") val productSku: String,
    val quantity: Int,
    @SerialName("unit_price") val unitPrice: Long,
    @SerialName("discount_amount") val discountAmount: Long,
    @SerialName("line_total") val lineTotal: Long,
    @SerialName("created_at") val createdAt: String
)
