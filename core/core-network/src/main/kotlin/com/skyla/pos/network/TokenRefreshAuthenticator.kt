package com.skyla.pos.network

import com.skyla.pos.common.Constants
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject

class TokenRefreshAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val json: Json
) : Authenticator {

    private val lock = Any()

    @Serializable
    private data class RefreshTokenRequest(
        @SerialName("refresh_token") val refreshToken: String
    )

    @Serializable
    private data class RefreshTokenResponse(
        val data: TokenData
    )

    @Serializable
    private data class TokenData(
        @SerialName("access_token") val accessToken: String,
        @SerialName("refresh_token") val refreshToken: String
    )

    override fun authenticate(route: Route?, response: Response): Request? {
        // Avoid infinite loops — if we already tried refreshing, give up
        if (responseCount(response) >= 2) {
            Timber.w("Token refresh retry limit reached, clearing tokens")
            runBlocking { tokenManager.clearAll() }
            return null
        }

        synchronized(lock) {
            val currentToken = runBlocking { tokenManager.getAccessToken() }
            val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")

            // If the token has already been refreshed by another thread, retry with the new token
            if (currentToken != null && currentToken != requestToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            // Attempt to refresh the token
            val refreshToken = runBlocking { tokenManager.getRefreshToken() } ?: run {
                Timber.w("No refresh token available, clearing tokens")
                runBlocking { tokenManager.clearAll() }
                return null
            }

            return try {
                val newTokens = refreshTokenSync(refreshToken)
                if (newTokens != null) {
                    runBlocking {
                        tokenManager.saveTokens(newTokens.accessToken, newTokens.refreshToken)
                    }
                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${newTokens.accessToken}")
                        .build()
                } else {
                    Timber.w("Token refresh failed, clearing tokens")
                    runBlocking { tokenManager.clearAll() }
                    null
                }
            } catch (e: Exception) {
                Timber.e(e, "Token refresh error")
                runBlocking { tokenManager.clearAll() }
                null
            }
        }
    }

    private fun refreshTokenSync(refreshToken: String): TokenData? {
        val client = OkHttpClient.Builder().build()

        val body = json.encodeToString(
            RefreshTokenRequest.serializer(),
            RefreshTokenRequest(refreshToken = refreshToken)
        )

        val request = Request.Builder()
            .url("${Constants.DEFAULT_BASE_URL}auth/refresh")
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()

        return if (response.isSuccessful) {
            response.body?.string()?.let { responseBody ->
                try {
                    val parsed = json.decodeFromString(
                        RefreshTokenResponse.serializer(),
                        responseBody
                    )
                    parsed.data
                } catch (e: Exception) {
                    Timber.e(e, "Failed to parse refresh token response")
                    null
                }
            }
        } else {
            Timber.w("Refresh token request failed with code: ${response.code}")
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }
        return count
    }
}
