package com.skyla.pos.users.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val email: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    val password: String,
    val role: String,
)

@Serializable
data class UpdateUserRequest(
    val email: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    val role: String? = null,
    @SerialName("is_active") val isActive: Boolean? = null,
)
