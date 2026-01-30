package com.skyla.pos.auth.domain.repository

import com.skyla.pos.auth.data.dto.LoginResponse
import com.skyla.pos.common.Resource

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<LoginResponse>
    suspend fun logout(): Resource<String>
    suspend fun changePassword(currentPassword: String, newPassword: String): Resource<String>
    suspend fun isLoggedIn(): Boolean
}
