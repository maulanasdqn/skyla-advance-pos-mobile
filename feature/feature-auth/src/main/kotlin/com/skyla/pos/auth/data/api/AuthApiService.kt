package com.skyla.pos.auth.data.api

import com.skyla.pos.auth.data.dto.ChangePasswordRequest
import com.skyla.pos.auth.data.dto.LoginRequest
import com.skyla.pos.auth.data.dto.LoginResponse
import com.skyla.pos.auth.data.dto.LogoutRequest
import com.skyla.pos.auth.data.dto.RefreshTokenRequest
import com.skyla.pos.auth.data.dto.RefreshTokenResponse
import com.skyla.pos.network.dto.MessageResponse
import com.skyla.pos.network.dto.SingleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<SingleResponse<LoginResponse>>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<SingleResponse<RefreshTokenResponse>>

    @POST("auth/logout")
    suspend fun logout(@Body request: LogoutRequest): Response<MessageResponse>

    @POST("auth/logout-all")
    suspend fun logoutAll(): Response<MessageResponse>

    @POST("auth/password/change")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<MessageResponse>
}
