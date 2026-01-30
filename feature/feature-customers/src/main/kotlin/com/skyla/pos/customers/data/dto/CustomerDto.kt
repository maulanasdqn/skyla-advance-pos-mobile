package com.skyla.pos.customers.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateCustomerRequest(
    val name: String,
    val phone: String? = null,
    val email: String? = null,
    val notes: String? = null,
)

@Serializable
data class UpdateCustomerRequest(
    val name: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val notes: String? = null,
)
