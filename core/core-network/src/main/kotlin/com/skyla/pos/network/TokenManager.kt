package com.skyla.pos.network

interface TokenManager {
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun saveUserInfo(userId: String, role: String, name: String)
    suspend fun getUserId(): String?
    suspend fun getUserRole(): String?
    suspend fun getUserName(): String?
    suspend fun clearAll()
}
