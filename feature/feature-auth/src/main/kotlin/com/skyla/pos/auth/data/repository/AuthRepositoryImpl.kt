package com.skyla.pos.auth.data.repository

import com.skyla.pos.auth.data.api.AuthApiService
import com.skyla.pos.auth.data.dto.ChangePasswordRequest
import com.skyla.pos.auth.data.dto.LoginRequest
import com.skyla.pos.auth.data.dto.LoginResponse
import com.skyla.pos.auth.data.dto.LogoutRequest
import com.skyla.pos.auth.domain.repository.AuthRepository
import com.skyla.pos.common.Resource
import com.skyla.pos.network.TokenManager
import com.skyla.pos.network.safeApiCall
import timber.log.Timber
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Resource<LoginResponse> {
        val result = safeApiCall {
            authApiService.login(LoginRequest(email = email, password = password))
        }

        return when (result) {
            is Resource.Success -> {
                val loginResponse = result.data.data
                tokenManager.saveTokens(
                    accessToken = loginResponse.tokens.accessToken,
                    refreshToken = loginResponse.tokens.refreshToken,
                )
                tokenManager.saveUserInfo(
                    userId = loginResponse.user.id,
                    role = loginResponse.user.role,
                    name = "${loginResponse.user.firstName} ${loginResponse.user.lastName}",
                )
                Timber.d("Login successful for user: ${loginResponse.user.email}")
                Resource.Success(loginResponse)
            }
            is Resource.Error -> {
                Timber.w("Login failed: ${result.message}")
                Resource.Error(result.message, result.code)
            }
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun logout(): Resource<String> {
        val refreshToken = tokenManager.getRefreshToken()
        if (refreshToken == null) {
            tokenManager.clearAll()
            return Resource.Success("Logged out")
        }

        val result = safeApiCall {
            authApiService.logout(LogoutRequest(refreshToken = refreshToken))
        }

        // Always clear tokens on logout, regardless of API result
        tokenManager.clearAll()

        return when (result) {
            is Resource.Success -> {
                Timber.d("Logout successful")
                Resource.Success(result.data.message)
            }
            is Resource.Error -> {
                Timber.w("Logout API call failed: ${result.message}, but tokens cleared locally")
                Resource.Success("Logged out locally")
            }
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
    ): Resource<String> {
        val result = safeApiCall {
            authApiService.changePassword(
                ChangePasswordRequest(
                    currentPassword = currentPassword,
                    newPassword = newPassword,
                )
            )
        }

        return when (result) {
            is Resource.Success -> {
                Timber.d("Password changed successfully")
                Resource.Success(result.data.message)
            }
            is Resource.Error -> {
                Timber.w("Change password failed: ${result.message}")
                Resource.Error(result.message, result.code)
            }
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return tokenManager.getAccessToken() != null
    }
}
