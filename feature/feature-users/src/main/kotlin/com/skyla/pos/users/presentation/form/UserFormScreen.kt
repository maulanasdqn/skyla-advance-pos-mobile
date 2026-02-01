package com.skyla.pos.users.presentation.form

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skyla.pos.model.UserRole
import com.skyla.pos.ui.components.SkylaButton
import com.skyla.pos.ui.components.SkylaLoadingScreen
import com.skyla.pos.ui.components.SkylaTextField
import com.skyla.pos.ui.components.SkylaTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserFormScreen(
    onNavigateBack: () -> Unit,
    onUserSaved: () -> Unit,
    viewModel: UserFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UserFormEvent.UserSaved -> {
                    onUserSaved()
                }
                is UserFormEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            SkylaTopBar(
                title = if (uiState.isEditMode) "Edit User" else "Add User",
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
                text = "User Information",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(12.dp))

            SkylaTextField(
                value = uiState.firstName,
                onValueChange = viewModel::onFirstNameChanged,
                label = "First Name",
                placeholder = "Enter first name",
                isError = uiState.firstNameError != null,
                errorMessage = uiState.firstNameError,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            SkylaTextField(
                value = uiState.lastName,
                onValueChange = viewModel::onLastNameChanged,
                label = "Last Name",
                placeholder = "Enter last name",
                isError = uiState.lastNameError != null,
                errorMessage = uiState.lastNameError,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            SkylaTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChanged,
                label = "Email",
                placeholder = "Enter email address",
                keyboardType = KeyboardType.Email,
                isError = uiState.emailError != null,
                errorMessage = uiState.emailError,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (!uiState.isEditMode) {
                SkylaTextField(
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChanged,
                    label = "Password",
                    placeholder = "Enter password",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation(),
                    isError = uiState.passwordError != null,
                    errorMessage = uiState.passwordError,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Role",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(12.dp))

            RoleSelector(
                selectedRole = uiState.role,
                onRoleSelected = viewModel::onRoleChanged,
            )

            Spacer(modifier = Modifier.height(32.dp))

            SkylaButton(
                text = if (uiState.isEditMode) "Update User" else "Create User",
                onClick = viewModel::saveUser,
                isLoading = uiState.isSaving,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoleSelector(
    selectedRole: String,
    onRoleSelected: (String) -> Unit,
) {
    val roles = listOf(
        UserRole.ADMIN to "Admin",
        UserRole.MANAGER to "Manager",
        UserRole.CASHIER to "Cashier",
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        roles.forEach { (role, label) ->
            val isSelected = selectedRole == role.name
            FilterChip(
                selected = isSelected,
                onClick = { onRoleSelected(role.name) },
                label = { Text(text = label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                ),
                border = if (isSelected) {
                    BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                } else {
                    FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = false,
                    )
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}
