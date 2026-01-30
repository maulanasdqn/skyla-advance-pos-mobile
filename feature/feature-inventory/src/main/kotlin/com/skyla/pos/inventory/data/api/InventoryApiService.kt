package com.skyla.pos.inventory.data.api

import com.skyla.pos.inventory.data.dto.CreateAdjustmentRequest
import com.skyla.pos.model.InventoryAdjustment
import com.skyla.pos.model.StockLevel
import com.skyla.pos.network.dto.ListResponse
import com.skyla.pos.network.dto.SingleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface InventoryApiService {

    @POST("inventory/adjustments")
    suspend fun createAdjustment(
        @Body request: CreateAdjustmentRequest,
    ): Response<SingleResponse<InventoryAdjustment>>

    @GET("inventory/adjustments")
    suspend fun getAdjustments(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): Response<ListResponse<InventoryAdjustment>>

    @GET("inventory/adjustments/{id}")
    suspend fun getAdjustment(
        @Path("id") id: String,
    ): Response<SingleResponse<InventoryAdjustment>>

    @GET("inventory/stock/{productId}")
    suspend fun getStockLevel(
        @Path("productId") productId: String,
    ): Response<SingleResponse<StockLevel>>

    @GET("inventory/low-stock")
    suspend fun getLowStockProducts(): Response<ListResponse<StockLevel>>
}
