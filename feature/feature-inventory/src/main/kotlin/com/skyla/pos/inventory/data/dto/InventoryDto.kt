package com.skyla.pos.inventory.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateAdjustmentRequest(
    @SerialName("product_id") val productId: String,
    @SerialName("quantity_change") val quantityChange: Int,
    val reason: String,
    @SerialName("reference_id") val referenceId: String? = null,
    val notes: String? = null,
    @SerialName("adjusted_by") val adjustedBy: String,
)
