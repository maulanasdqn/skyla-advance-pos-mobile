package com.skyla.pos.payments.domain.repository

import com.skyla.pos.common.Resource
import com.skyla.pos.model.Payment
import com.skyla.pos.model.PaymentSummary
import com.skyla.pos.model.Sale

interface PaymentRepository {

    suspend fun addPayment(
        saleId: String,
        paymentMethod: String,
        amount: Long,
        referenceNumber: String?,
    ): Resource<Payment>

    suspend fun getPayments(saleId: String): Resource<List<Payment>>

    suspend fun getPaymentSummary(saleId: String): Resource<PaymentSummary>

    suspend fun completeSale(saleId: String): Resource<Sale>
}
