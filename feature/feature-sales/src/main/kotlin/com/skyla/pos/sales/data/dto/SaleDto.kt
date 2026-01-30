package com.skyla.pos.sales.data.dto

import com.skyla.pos.model.Payment
import com.skyla.pos.model.Sale
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateSaleRequest(
    @SerialName("customer_id") val customerId: String? = null,
)

@Serializable
data class AddSaleItemRequest(
    @SerialName("product_id") val productId: String,
    val quantity: Int,
    @SerialName("discount_amount") val discountAmount: Long? = null,
)

@Serializable
data class UpdateSaleItemRequest(
    val quantity: Int? = null,
    @SerialName("discount_amount") val discountAmount: Long? = null,
)

@Serializable
data class ApplyDiscountRequest(
    @SerialName("discount_amount") val discountAmount: Long,
)

@Serializable
data class VoidSaleRequest(
    val reason: String,
)

@Serializable
data class ReceiptResponse(
    val sale: Sale,
    val payments: List<Payment>,
    @SerialName("cashier_name") val cashierName: String,
    @SerialName("customer_name") val customerName: String? = null,
)
