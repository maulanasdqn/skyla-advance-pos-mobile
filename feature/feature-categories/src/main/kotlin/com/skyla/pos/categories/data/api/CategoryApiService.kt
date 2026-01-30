package com.skyla.pos.categories.data.api

import com.skyla.pos.categories.data.dto.CreateCategoryRequest
import com.skyla.pos.categories.data.dto.UpdateCategoryRequest
import com.skyla.pos.model.Category
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

interface CategoryApiService {

    @GET("categories")
    suspend fun getCategories(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("search") search: String? = null,
    ): Response<ListResponse<Category>>

    @GET("categories/{id}")
    suspend fun getCategory(
        @Path("id") id: String,
    ): Response<SingleResponse<Category>>

    @POST("categories")
    suspend fun createCategory(
        @Body request: CreateCategoryRequest,
    ): Response<SingleResponse<Category>>

    @PUT("categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: String,
        @Body request: UpdateCategoryRequest,
    ): Response<SingleResponse<Category>>

    @DELETE("categories/{id}")
    suspend fun deleteCategory(
        @Path("id") id: String,
    ): Response<MessageResponse>

    @POST("categories/{id}/deactivate")
    suspend fun deactivateCategory(
        @Path("id") id: String,
    ): Response<MessageResponse>
}
