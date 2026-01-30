package com.skyla.pos.auth.domain.usecase

import com.skyla.pos.auth.domain.repository.AuthRepository
import com.skyla.pos.common.Resource
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String,
    ): Resource<String> {
        if (currentPassword.isBlank()) {
            return Resource.Error("Current password is required")
        }
        if (newPassword.isBlank()) {
            return Resource.Error("New password is required")
        }
        if (newPassword.length < 8) {
            return Resource.Error("New password must be at least 8 characters")
        }
        if (newPassword != confirmPassword) {
            return Resource.Error("Passwords do not match")
        }
        if (currentPassword == newPassword) {
            return Resource.Error("New password must be different from current password")
        }
        return authRepository.changePassword(
            currentPassword = currentPassword,
            newPassword = newPassword,
        )
    }
}
