package com.skyla.pos.categories.presentation.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyla.pos.categories.data.dto.CreateCategoryRequest
import com.skyla.pos.categories.data.dto.UpdateCategoryRequest
import com.skyla.pos.categories.domain.repository.CategoryRepository
import com.skyla.pos.common.Resource
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

data class CategoryFormUiState(
    val name: String = "",
    val description: String = "",
    val parentId: String = "",
    val sortOrder: String = "0",
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val isFormLoading: Boolean = false,
    val nameError: String? = null,
    val sortOrderError: String? = null,
)

sealed interface CategoryFormEvent {
    data object CategorySaved : CategoryFormEvent
    data class ShowError(val message: String) : CategoryFormEvent
}

@HiltViewModel
class CategoryFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val categoryId: String? = savedStateHandle["categoryId"]

    private val _uiState = MutableStateFlow(CategoryFormUiState(isEditMode = categoryId != null))
    val uiState: StateFlow<CategoryFormUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CategoryFormEvent>()
    val events: SharedFlow<CategoryFormEvent> = _events.asSharedFlow()

    init {
        if (categoryId != null) {
            loadCategory(categoryId)
        }
    }

    private fun loadCategory(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isFormLoading = true) }
            when (val result = categoryRepository.getCategory(id)) {
                is Resource.Success -> {
                    val category = result.data
                    _uiState.update {
                        it.copy(
                            name = category.name,
                            description = category.description ?: "",
                            parentId = category.parentId ?: "",
                            sortOrder = category.sortOrder.toString(),
                            isFormLoading = false,
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isFormLoading = false) }
                    _events.emit(CategoryFormEvent.ShowError(result.message))
                }
                is Resource.Loading -> { /* handled by state update above */ }
            }
        }
    }

    fun onNameChanged(value: String) {
        _uiState.update { it.copy(name = value, nameError = null) }
    }

    fun onDescriptionChanged(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onParentIdChanged(value: String) {
        _uiState.update { it.copy(parentId = value) }
    }

    fun onSortOrderChanged(value: String) {
        _uiState.update { it.copy(sortOrder = value, sortOrderError = null) }
    }

    fun saveCategory() {
        if (!validateForm()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val state = _uiState.value
            val result = if (categoryId != null) {
                categoryRepository.updateCategory(
                    id = categoryId,
                    request = UpdateCategoryRequest(
                        name = state.name,
                        description = state.description.ifBlank { null },
                        parentId = state.parentId.ifBlank { null },
                        sortOrder = state.sortOrder.toIntOrNull(),
                    ),
                )
            } else {
                categoryRepository.createCategory(
                    request = CreateCategoryRequest(
                        name = state.name,
                        description = state.description.ifBlank { null },
                        parentId = state.parentId.ifBlank { null },
                        sortOrder = state.sortOrder.toIntOrNull() ?: 0,
                    ),
                )
            }

            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(CategoryFormEvent.CategorySaved)
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(CategoryFormEvent.ShowError(result.message))
                }
                is Resource.Loading -> { /* handled by state update above */ }
            }
        }
    }

    private fun validateForm(): Boolean {
        val state = _uiState.value
        var isValid = true

        if (state.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Category name is required") }
            isValid = false
        }

        if (state.sortOrder.isNotBlank() && state.sortOrder.toIntOrNull() == null) {
            _uiState.update { it.copy(sortOrderError = "Invalid sort order") }
            isValid = false
        }

        return isValid
    }
}
