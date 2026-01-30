package com.skyla.pos.payments.data.api

import com.skyla.pos.model.Payment
import com.skyla.pos.model.PaymentSummary
import com.skyla.pos.model.Sale
import com.skyla.pos.network.dto.ListResponse
import com.skyla.pos.network.dto.SingleResponse
import com.skyla.pos.payments.data.dto.AddPaymentRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentApiService {

    @POST("sales/{saleId}/payments")
    suspend fun addPayment(
        @Path("saleId") saleId: String,
        @Body request: AddPaymentRequest,
    ): Response<SingleResponse<Payment>>

    @GET("sales/{saleId}/payments")
    suspend fun getPayments(
        @Path("saleId") saleId: String,
    ): Response<ListResponse<Payment>>

    @GET("sales/{saleId}/payments/summary")
    suspend fun getPaymentSummary(
        @Path("saleId") saleId: String,
    ): Response<SingleResponse<PaymentSummary>>

    @POST("sales/{saleId}/complete")
    suspend fun completeSale(
        @Path("saleId") saleId: String,
    ): Response<SingleResponse<Sale>>
}
