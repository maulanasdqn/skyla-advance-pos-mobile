package com.skyla.pos.users.presentation.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyla.pos.common.Resource
import com.skyla.pos.model.UserRole
import com.skyla.pos.users.data.dto.CreateUserRequest
import com.skyla.pos.users.data.dto.UpdateUserRequest
import com.skyla.pos.users.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserFormUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val role: String = UserRole.CASHIER.name,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isEditMode: Boolean = false,
    val isFormLoading: Boolean = false,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
)

sealed interface UserFormEvent {
    data object UserSaved : UserFormEvent
    data class ShowError(val message: String) : UserFormEvent
}

@HiltViewModel
class UserFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val userId: String? = savedStateHandle["userId"]

    private val _uiState = MutableStateFlow(UserFormUiState(isEditMode = userId != null))
    val uiState: StateFlow<UserFormUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<UserFormEvent>()
    val events: SharedFlow<UserFormEvent> = _events.asSharedFlow()

    init {
        if (userId != null) {
            loadUser(userId)
        }
    }

    private fun loadUser(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isFormLoading = true) }
            when (val result = userRepository.getUser(id)) {
                is Resource.Success -> {
                    val user = result.data
                    _uiState.update {
                        it.copy(
                            firstName = user.firstName,
                            lastName = user.lastName,
                            email = user.email,
                            role = user.role.name,
                            isFormLoading = false,
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isFormLoading = false) }
                    _events.emit(UserFormEvent.ShowError(result.message))
                }
                is Resource.Loading -> { /* handled by state update above */ }
            }
        }
    }

    fun onFirstNameChanged(value: String) {
        _uiState.update { it.copy(firstName = value, firstNameError = null) }
    }

    fun onLastNameChanged(value: String) {
        _uiState.update { it.copy(lastName = value, lastNameError = null) }
    }

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    fun onRoleChanged(value: String) {
        _uiState.update { it.copy(role = value) }
    }

    fun saveUser() {
        if (!validateForm()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val state = _uiState.value
            val roleValue = state.role.lowercase()

            val result = if (userId != null) {
                userRepository.updateUser(
                    id = userId,
                    request = UpdateUserRequest(
                        email = state.email,
                        firstName = state.firstName,
                        lastName = state.lastName,
                        role = roleValue,
                    ),
                )
            } else {
                userRepository.createUser(
                    request = CreateUserRequest(
                        email = state.email,
                        firstName = state.firstName,
                        lastName = state.lastName,
                        password = state.password,
                        role = roleValue,
                    ),
                )
            }

            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isSaving = false) }
                    _events.emit(UserFormEvent.UserSaved)
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isSaving = false) }
                    _events.emit(UserFormEvent.ShowError(result.message))
                }
                is Resource.Loading -> { /* handled by state update above */ }
            }
        }
    }

    private fun validateForm(): Boolean {
        val state = _uiState.value
        var isValid = true

        if (state.firstName.isBlank()) {
            _uiState.update { it.copy(firstNameError = "First name is required") }
            isValid = false
        }

        if (state.lastName.isBlank()) {
            _uiState.update { it.copy(lastNameError = "Last name is required") }
            isValid = false
        }

        if (state.email.isBlank()) {
            _uiState.update { it.copy(emailError = "Email is required") }
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _uiState.update { it.copy(emailError = "Invalid email format") }
            isValid = false
        }

        if (!state.isEditMode && state.password.isBlank()) {
            _uiState.update { it.copy(passwordError = "Password is required") }
            isValid = false
        } else if (!state.isEditMode && state.password.length < 6) {
            _uiState.update { it.copy(passwordError = "Password must be at least 6 characters") }
            isValid = false
        }

        return isValid
    }
}
