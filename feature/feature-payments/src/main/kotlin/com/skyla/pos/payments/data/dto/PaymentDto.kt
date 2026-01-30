package com.skyla.pos.payments.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddPaymentRequest(
    @SerialName("payment_method") val paymentMethod: String,
    val amount: Long,
    @SerialName("reference_number") val referenceNumber: String? = null,
)
