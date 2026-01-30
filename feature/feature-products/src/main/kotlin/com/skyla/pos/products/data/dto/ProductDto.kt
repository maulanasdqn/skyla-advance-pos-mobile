package com.skyla.pos.products.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateProductRequest(
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
)

@Serializable
data class UpdateProductRequest(
    val sku: String? = null,
    val barcode: String? = null,
    val name: String? = null,
    val description: String? = null,
    @SerialName("category_id") val categoryId: String? = null,
    val price: Long? = null,
    @SerialName("cost_price") val costPrice: Long? = null,
    @SerialName("reorder_level") val reorderLevel: Int? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("is_active") val isActive: Boolean? = null,
)
