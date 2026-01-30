package com.skyla.pos.auth.domain.usecase

import com.skyla.pos.auth.data.dto.LoginResponse
import com.skyla.pos.auth.domain.repository.AuthRepository
import com.skyla.pos.common.Resource
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): Resource<LoginResponse> {
        if (email.isBlank()) {
            return Resource.Error("Email is required")
        }
        if (password.isBlank()) {
            return Resource.Error("Password is required")
        }
        return authRepository.login(email = email, password = password)
    }
}
