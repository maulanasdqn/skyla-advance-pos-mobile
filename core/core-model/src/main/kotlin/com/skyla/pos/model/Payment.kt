package com.skyla.pos.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Payment(
    val id: String,
    @SerialName("sale_id") val saleId: String,
    @SerialName("payment_method") val paymentMethod: PaymentMethod,
    val amount: Long,
    @SerialName("reference_number") val referenceNumber: String? = null,
    @SerialName("change_amount") val changeAmount: Long,
    @SerialName("created_at") val createdAt: String
)

@Serializable
enum class PaymentMethod {
    @SerialName("cash") CASH,
    @SerialName("card") CARD,
    @SerialName("e_wallet") E_WALLET
}

@Serializable
data class PaymentSummary(
    @SerialName("sale_id") val saleId: String,
    @SerialName("total_amount") val totalAmount: Long,
    @SerialName("total_paid") val totalPaid: Long,
    @SerialName("remaining_balance") val remainingBalance: Long,
    val payments: List<Payment>
)
