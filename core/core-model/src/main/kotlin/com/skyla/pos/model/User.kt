package com.skyla.pos.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val email: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    val role: UserRole,
    @SerialName("is_active") val isActive: Boolean,
    @SerialName("last_login_at") val lastLoginAt: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
) {
    val fullName: String get() = "$firstName $lastName"
}

@Serializable
enum class UserRole {
    @SerialName("admin") ADMIN,
    @SerialName("manager") MANAGER,
    @SerialName("cashier") CASHIER
}
