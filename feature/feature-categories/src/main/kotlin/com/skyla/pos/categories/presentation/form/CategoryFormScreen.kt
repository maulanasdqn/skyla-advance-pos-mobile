package com.skyla.pos.categories.presentation.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skyla.pos.ui.components.SkylaButton
import com.skyla.pos.ui.components.SkylaLoadingScreen
import com.skyla.pos.ui.components.SkylaTextField
import com.skyla.pos.ui.components.SkylaTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFormScreen(
    onNavigateBack: () -> Unit,
    onCategorySaved: () -> Unit,
    viewModel: CategoryFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CategoryFormEvent.CategorySaved -> {
                    onCategorySaved()
                }
                is CategoryFormEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            SkylaTopBar(
                title = if (uiState.isEditMode) "Edit Category" else "New Category",
                onNavigateBack = onNavigateBack,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        if (uiState.isFormLoading) {
            SkylaLoadingScreen(modifier = Modifier.padding(paddingValues))
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Text(
                text = "Category Information",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(12.dp))

            SkylaTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChanged,
                label = "Category Name",
                placeholder = "Enter category name",
                isError = uiState.nameError != null,
                errorMessage = uiState.nameError,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            SkylaTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChanged,
                label = "Description (optional)",
                placeholder = "Enter category description",
                singleLine = false,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            SkylaTextField(
                value = uiState.parentId,
                onValueChange = viewModel::onParentIdChanged,
                label = "Parent Category ID (optional)",
                placeholder = "Enter parent category ID",
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            SkylaTextField(
                value = uiState.sortOrder,
                onValueChange = viewModel::onSortOrderChanged,
                label = "Sort Order",
                placeholder = "0",
                keyboardType = KeyboardType.Number,
                isError = uiState.sortOrderError != null,
                errorMessage = uiState.sortOrderError,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(32.dp))

            SkylaButton(
                text = if (uiState.isEditMode) "Update Category" else "Create Category",
                onClick = viewModel::saveCategory,
                isLoading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
