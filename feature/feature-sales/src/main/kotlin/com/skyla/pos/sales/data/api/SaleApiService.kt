package com.skyla.pos.sales.data.api

import com.skyla.pos.model.Sale
import com.skyla.pos.network.dto.ListResponse
import com.skyla.pos.network.dto.SingleResponse
import com.skyla.pos.sales.data.dto.AddSaleItemRequest
import com.skyla.pos.sales.data.dto.ApplyDiscountRequest
import com.skyla.pos.sales.data.dto.CreateSaleRequest
import com.skyla.pos.sales.data.dto.ReceiptResponse
import com.skyla.pos.sales.data.dto.UpdateSaleItemRequest
import com.skyla.pos.sales.data.dto.VoidSaleRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SaleApiService {

    @POST("sales")
    suspend fun createSale(
        @Body request: CreateSaleRequest,
    ): Response<SingleResponse<Sale>>

    @GET("sales")
    suspend fun getSales(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("status") status: String? = null,
    ): Response<ListResponse<Sale>>

    @GET("sales/{id}")
    suspend fun getSale(
        @Path("id") id: String,
    ): Response<SingleResponse<Sale>>

    @POST("sales/{id}/items")
    suspend fun addSaleItem(
        @Path("id") saleId: String,
        @Body request: AddSaleItemRequest,
    ): Response<SingleResponse<Sale>>

    @PUT("sales/{id}/items/{itemId}")
    suspend fun updateSaleItem(
        @Path("id") saleId: String,
        @Path("itemId") itemId: String,
        @Body request: UpdateSaleItemRequest,
    ): Response<SingleResponse<Sale>>

    @DELETE("sales/{id}/items/{itemId}")
    suspend fun removeSaleItem(
        @Path("id") saleId: String,
        @Path("itemId") itemId: String,
    ): Response<SingleResponse<Sale>>

    @PUT("sales/{id}/discount")
    suspend fun applyDiscount(
        @Path("id") saleId: String,
        @Body request: ApplyDiscountRequest,
    ): Response<SingleResponse<Sale>>

    @POST("sales/{id}/complete")
    suspend fun completeSale(
        @Path("id") saleId: String,
    ): Response<SingleResponse<Sale>>

    @POST("sales/{id}/void")
    suspend fun voidSale(
        @Path("id") saleId: String,
        @Body request: VoidSaleRequest,
    ): Response<SingleResponse<Sale>>

    @GET("sales/{id}/receipt")
    suspend fun getReceipt(
        @Path("id") saleId: String,
    ): Response<SingleResponse<ReceiptResponse>>
}
