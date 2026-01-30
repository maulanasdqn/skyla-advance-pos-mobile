package com.skyla.pos.products.data.api

import com.skyla.pos.model.Product
import com.skyla.pos.network.dto.ListResponse
import com.skyla.pos.network.dto.MessageResponse
import com.skyla.pos.network.dto.SingleResponse
import com.skyla.pos.products.data.dto.CreateProductRequest
import com.skyla.pos.products.data.dto.UpdateProductRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApiService {

    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("search") search: String? = null,
    ): Response<ListResponse<Product>>

    @GET("products/{id}")
    suspend fun getProduct(
        @Path("id") id: String,
    ): Response<SingleResponse<Product>>

    @POST("products")
    suspend fun createProduct(
        @Body request: CreateProductRequest,
    ): Response<SingleResponse<Product>>

    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") id: String,
        @Body request: UpdateProductRequest,
    ): Response<SingleResponse<Product>>

    @DELETE("products/{id}")
    suspend fun deleteProduct(
        @Path("id") id: String,
    ): Response<MessageResponse>

    @GET("products/barcode/{code}")
    suspend fun getProductByBarcode(
        @Path("code") code: String,
    ): Response<SingleResponse<Product>>

    @GET("products/sku/{sku}")
    suspend fun getProductBySku(
        @Path("sku") sku: String,
    ): Response<SingleResponse<Product>>

    @POST("products/{id}/deactivate")
    suspend fun deactivateProduct(
        @Path("id") id: String,
    ): Response<MessageResponse>
}
