package com.skyla.pos.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InventoryAdjustment(
    val id: String,
    @SerialName("product_id") val productId: String,
    @SerialName("quantity_change") val quantityChange: Int,
    val reason: AdjustmentReason,
    @SerialName("reference_id") val referenceId: String? = null,
    val notes: String? = null,
    @SerialName("adjusted_by") val adjustedBy: String,
    @SerialName("created_at") val createdAt: String
)

@Serializable
enum class AdjustmentReason {
    @SerialName("purchase") PURCHASE,
    @SerialName("sale") SALE,
    @SerialName("return") RETURN,
    @SerialName("damage") DAMAGE,
    @SerialName("correction") CORRECTION,
    @SerialName("initial") INITIAL
}

@Serializable
data class StockLevel(
    @SerialName("product_id") val productId: String,
    @SerialName("product_name") val productName: String,
    @SerialName("product_sku") val productSku: String,
    @SerialName("current_stock") val currentStock: Int,
    @SerialName("reorder_level") val reorderLevel: Int,
    @SerialName("is_low_stock") val isLowStock: Boolean
)
