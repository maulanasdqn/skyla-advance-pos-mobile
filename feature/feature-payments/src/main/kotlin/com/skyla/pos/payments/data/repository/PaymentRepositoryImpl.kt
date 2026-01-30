package com.skyla.pos.payments.data.repository

import com.skyla.pos.common.Resource
import com.skyla.pos.model.Payment
import com.skyla.pos.model.PaymentSummary
import com.skyla.pos.model.Sale
import com.skyla.pos.network.safeApiCall
import com.skyla.pos.payments.data.api.PaymentApiService
import com.skyla.pos.payments.data.dto.AddPaymentRequest
import com.skyla.pos.payments.domain.repository.PaymentRepository
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val paymentApiService: PaymentApiService,
) : PaymentRepository {

    override suspend fun addPayment(
        saleId: String,
        paymentMethod: String,
        amount: Long,
        referenceNumber: String?,
    ): Resource<Payment> {
        val request = AddPaymentRequest(
            paymentMethod = paymentMethod,
            amount = amount,
            referenceNumber = referenceNumber,
        )
        val result = safeApiCall { paymentApiService.addPayment(saleId, request) }
        return when (result) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getPayments(saleId: String): Resource<List<Payment>> {
        val result = safeApiCall { paymentApiService.getPayments(saleId) }
        return when (result) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getPaymentSummary(saleId: String): Resource<PaymentSummary> {
        val result = safeApiCall { paymentApiService.getPaymentSummary(saleId) }
        return when (result) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun completeSale(saleId: String): Resource<Sale> {
        val result = safeApiCall { paymentApiService.completeSale(saleId) }
        return when (result) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }
}
