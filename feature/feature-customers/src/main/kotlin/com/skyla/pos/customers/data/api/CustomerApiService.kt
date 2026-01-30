package com.skyla.pos.customers.data.api

import com.skyla.pos.customers.data.dto.CreateCustomerRequest
import com.skyla.pos.customers.data.dto.UpdateCustomerRequest
import com.skyla.pos.model.Customer
import com.skyla.pos.network.dto.ListResponse
import com.skyla.pos.network.dto.MessageResponse
import com.skyla.pos.network.dto.SingleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CustomerApiService {

    @GET("customers")
    suspend fun getCustomers(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("search") search: String? = null,
    ): Response<ListResponse<Customer>>

    @GET("customers/{id}")
    suspend fun getCustomer(
        @Path("id") id: String,
    ): Response<SingleResponse<Customer>>

    @POST("customers")
    suspend fun createCustomer(
        @Body request: CreateCustomerRequest,
    ): Response<SingleResponse<Customer>>

    @PUT("customers/{id}")
    suspend fun updateCustomer(
        @Path("id") id: String,
        @Body request: UpdateCustomerRequest,
    ): Response<SingleResponse<Customer>>

    @DELETE("customers/{id}")
    suspend fun deleteCustomer(
        @Path("id") id: String,
    ): Response<MessageResponse>
}
