package com.skyla.pos.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String,
    val sku: String,
    val barcode: String? = null,
    val name: String,
    val description: String? = null,
    @SerialName("category_id") val categoryId: String? = null,
    val price: Long,
    @SerialName("cost_price") val costPrice: Long,
    @SerialName("current_stock") val currentStock: Int,
    @SerialName("reorder_level") val reorderLevel: Int,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("is_active") val isActive: Boolean,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
) {
    val isLowStock: Boolean get() = currentStock <= reorderLevel
    val profitMargin: Double get() = if (price > 0) ((price - costPrice).toDouble() / price) * 100 else 0.0
}
