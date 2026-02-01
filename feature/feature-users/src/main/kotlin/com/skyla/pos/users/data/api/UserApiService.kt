package com.skyla.pos.users.data.api

import com.skyla.pos.model.User
import com.skyla.pos.network.dto.ListResponse
import com.skyla.pos.network.dto.MessageResponse
import com.skyla.pos.network.dto.SingleResponse
import com.skyla.pos.users.data.dto.CreateUserRequest
import com.skyla.pos.users.data.dto.UpdateUserRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApiService {

    @GET("users")
    suspend fun getUsers(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("search") search: String? = null,
    ): Response<ListResponse<User>>

    @GET("users/{id}")
    suspend fun getUser(
        @Path("id") id: String,
    ): Response<SingleResponse<User>>

    @POST("users")
    suspend fun createUser(
        @Body request: CreateUserRequest,
    ): Response<SingleResponse<User>>

    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Body request: UpdateUserRequest,
    ): Response<SingleResponse<User>>

    @DELETE("users/{id}")
    suspend fun deleteUser(
        @Path("id") id: String,
    ): Response<MessageResponse>

    @POST("users/{id}/deactivate")
    suspend fun deactivateUser(
        @Path("id") id: String,
    ): Response<MessageResponse>
}
