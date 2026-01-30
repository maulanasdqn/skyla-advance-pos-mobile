package com.skyla.pos.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val id: String,
    val name: String,
    val phone: String? = null,
    val email: String? = null,
    @SerialName("loyalty_points") val loyaltyPoints: Int,
    @SerialName("total_spent") val totalSpent: Long,
    val notes: String? = null,
    @SerialName("is_active") val isActive: Boolean,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)
