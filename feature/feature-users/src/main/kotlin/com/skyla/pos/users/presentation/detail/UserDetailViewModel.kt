package com.skyla.pos.users.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyla.pos.common.Resource
import com.skyla.pos.common.UiState
import com.skyla.pos.model.User
import com.skyla.pos.users.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface UserDetailEvent {
    data object UserDeleted : UserDetailEvent
    data object UserDeactivated : UserDetailEvent
    data class ShowError(val message: String) : UserDetailEvent
}

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val userId: String = checkNotNull(savedStateHandle["userId"])

    private val _uiState = MutableStateFlow<UiState<User>>(UiState.Loading)
    val uiState: StateFlow<UiState<User>> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<UserDetailEvent>()
    val events: SharedFlow<UserDetailEvent> = _events.asSharedFlow()

    private val _isActionLoading = MutableStateFlow(false)
    val isActionLoading: StateFlow<Boolean> = _isActionLoading.asStateFlow()

    init {
        loadUser()
    }

    fun loadUser() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = userRepository.getUser(userId)) {
                is Resource.Success -> {
                    _uiState.value = UiState.Success(result.data)
                }
                is Resource.Error -> {
                    _uiState.value = UiState.Error(result.message)
                }
                is Resource.Loading -> { /* handled by state update above */ }
            }
        }
    }

    fun deleteUser() {
        viewModelScope.launch {
            _isActionLoading.value = true
            when (val result = userRepository.deleteUser(userId)) {
                is Resource.Success -> {
                    _isActionLoading.value = false
                    _events.emit(UserDetailEvent.UserDeleted)
                }
                is Resource.Error -> {
                    _isActionLoading.value = false
                    _events.emit(UserDetailEvent.ShowError(result.message))
                }
                is Resource.Loading -> { /* handled by state update above */ }
            }
        }
    }

    fun deactivateUser() {
        viewModelScope.launch {
            _isActionLoading.value = true
            when (val result = userRepository.deactivateUser(userId)) {
                is Resource.Success -> {
                    _isActionLoading.value = false
                    _events.emit(UserDetailEvent.UserDeactivated)
                    loadUser()
                }
                is Resource.Error -> {
                    _isActionLoading.value = false
                    _events.emit(UserDetailEvent.ShowError(result.message))
                }
                is Resource.Loading -> { /* handled by state update above */ }
            }
        }
    }
}
